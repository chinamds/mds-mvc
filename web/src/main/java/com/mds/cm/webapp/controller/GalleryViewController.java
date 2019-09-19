package com.mds.cm.webapp.controller;

import com.mds.common.Constants;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.exception.SearchException;
import com.mds.common.mapper.JsonMapper;
import com.mds.common.utils.Reflections;
import com.mds.cm.service.GalleryManager;
import com.mds.cm.util.AlbumTreePickerBuilder;
import com.mds.cm.util.AlbumTreeViewBuilder;
import com.mds.cm.util.AlbumUtils;
import com.mds.cm.util.CMUtils;
import com.mds.cm.util.ContentObjectUtils;
import com.mds.cm.util.GalleryUtils;
import com.mds.cm.util.GalleryView;
import com.mds.cm.util.TransferObject;
import com.mds.cm.util.TransferType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.cm.content.AlbumBo;
import com.mds.cm.content.ContentObjectBo;
import com.mds.cm.content.ContentObjectBoCollection;
import com.mds.cm.content.ContentObjectSearchOptions;
import com.mds.cm.content.ContentObjectSearcher;
import com.mds.cm.content.GalleryBo;
import com.mds.cm.content.GalleryBoCollection;
import com.mds.cm.content.GalleryControlSettings;
import com.mds.cm.content.GallerySettings;
import com.mds.cm.content.UiTemplateBo;
import com.mds.cm.exception.GallerySecurityException;
import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidContentObjectException;
import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.cm.model.Album;
import com.mds.cm.model.Gallery;
import com.mds.cm.rest.CMData;
import com.mds.cm.rest.CMDataLoadOptions;
import com.mds.cm.rest.SettingsRest;
import com.mds.cm.rest.TreeView;
import com.mds.cm.rest.TreeViewOptions;
import com.mds.common.webapp.controller.AbstractBaseController;
import com.mds.common.webapp.controller.BaseController;
import com.mds.core.ApprovalStatus;
import com.mds.core.ContentObjectSearchType;
import com.mds.core.ContentObjectType;
import com.mds.core.DisplayObjectType;
import com.mds.core.LongCollection;
import com.mds.core.MessageType;
import com.mds.core.ResourceId;
import com.mds.core.SecurityActions;
import com.mds.core.SecurityActionsOption;
import com.mds.core.SlideShowType;
import com.mds.core.UiTemplateType;
import com.mds.core.VirtualAlbumType;
import com.mds.core.exception.ArgumentException;
import com.mds.core.exception.ArgumentOutOfRangeException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.core.exception.WebException;
import com.mds.i18n.util.I18nUtils;
import com.mds.sys.model.Organization;
import com.mds.sys.util.AppSettings;
import com.mds.sys.util.RoleUtils;
import com.mds.sys.util.UserUtils;
import com.mds.util.DateUtils;
import com.mds.util.HelperFunctions;
import com.mds.util.StringUtils;
import com.mds.util.Utils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cm/galleryview*")
public class GalleryViewController extends AbstractBaseController<Gallery, Long> {
    private GalleryManager galleryManager;
    
    private String controlId;
    private GalleryControlSettings galleryControlSettings;
    private GallerySettings gallerySettings;

    @Autowired
    public void setGalleryManager(GalleryManager galleryManager) {
        this.galleryManager = galleryManager;
    }

    @RequestMapping(value = "SearchDefault", method = RequestMethod.GET)
    public Model handleRequestDefault(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(galleryManager.search(query, Gallery.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(galleryManager.getAll());
        }
        return model;
    }
        
    @RequestMapping(method = RequestMethod.GET) //value = {"", "main"}
    public String handleRequest(@RequestParam(value = "g", required = false)  final String resourceId
    		, @RequestParam(value = "gid", required = false)  final Long galleryId
    		, @RequestParam(value = "aid", required = false) final Long aid
    		, @RequestParam(value = "moid", required = false) final Long moid
    		, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ResourceId rId = ResourceId.album;
    	if (StringUtils.isBlank(resourceId)) {
	    	if (moid != null){
	    		rId = ResourceId.contentobject;
	    	}
    	}else {
    		rId = ResourceId.getResourceId(resourceId, ResourceId.album);
    	}
    	GalleryView galleryView = new GalleryView(galleryId == null ? Long.MIN_VALUE : galleryId, aid, moid== null ? Long.MIN_VALUE : moid, rId, request, response);
    	galleryView.initializeView();
    	model.addAttribute("galleryView", galleryView);
    	    	
    	long gId = galleryId == null ? Long.MIN_VALUE : galleryId;
    	ContentObjectBo contentObject = null;
    	AlbumBo album = null;
    	if (moid != null && moid > Long.MIN_VALUE){
			contentObject = getContentObject(moid);
		}else if (aid != null && aid > Long.MIN_VALUE){
			album = AlbumUtils.loadAlbumInstance(aid, true);
		}
    	
    	if (galleryId == null || galleryId == Long.MIN_VALUE){
			if (contentObject != null){
				gId = contentObject.getGalleryId();
			}else if (album != null){
				gId = album.getGalleryId();
			}
			else
			{
				// There is no album or content object to get the gallery ID from, and no gallery ID has been specified on the control.
				// Just grab the first gallery in the database, creating it if necessary.
				GalleryBo gallery = CMUtils.loadLoginUserGalleries().stream().findFirst().orElse(null);
				if (gallery != null)
				{
					gId = gallery.getGalleryId();
				}
				else
				{
					// No gallery found anywhere, including the data store. Create one and assign it to this control instance.
					GalleryBo g = CMUtils.createGalleryInstance();
					g.addOrganization(UserUtils.getOrganizationId() == Long.MIN_VALUE ? Organization.getRootId() : UserUtils.getOrganizationId());
					g.setName("MDS_"+ UUID.randomUUID().toString().replace("-", ""));
					g.setDescription(I18nUtils.getString("webapp.name", request.getLocale()));
					g.setCreationDate(DateUtils.Now());
					g.save();
					gId = g.getGalleryId();
				}
			}
		}
    	gallerySettings = CMUtils.loadGallerySetting(gId);
		controlId = StringUtils.join(request.getRequestURI(), "|", UserUtils.getLoginName());
    	galleryControlSettings = CMUtils.loadGalleryControlSetting(controlId);
    	model.addAttribute("resourceId", rId);
		model.addAttribute("gallerySettings", gallerySettings);
		model.addAttribute("galleryControlSettings", galleryControlSettings);
		
    	if (album == null && contentObject != null) {
    		album = (AlbumBo)contentObject.getParent();
    	}
    	model.addAttribute("galleryId", gId);
    	
		if (album == null) {
			album = getAlbum(gId, request);//CMUtils.loadRootAlbumInstance(gId);
		}
		
    	//model.addAttribute("albumId", album != null ? album.getId() : Long.MIN_VALUE);
    	model.addAttribute("albumId", galleryView.getAlbumId());
    	model.addAttribute("contentObjectId", contentObject != null ? contentObject.getId() : Long.MIN_VALUE);
		
    	if (rId != ResourceId.cm_createalbum) {
    		model.addAttribute("album", album);
    	}

		/*CMData mdsData = getClientCMData(contentObject, album, gallerySettings, request);
		model.addAttribute("mdsData", mdsData);
		model.addAttribute("albumTreeData", getAlbumTreeData(CMUtils.loadGallery(gId), album, gallerySettings, request));*/

		if (rId == ResourceId.cm_addobjects) { //add content objects
			model.addAttribute("fileFilters", GalleryView.getFileFilters(gallerySettings));
			model.addAttribute("redirectToAlbum", galleryView.getAlbumViewPageUrl());
			
			return "cm/contentobjects/addobjects";
		}else if (rId == ResourceId.cm_createalbum) { //create album
			model.addAttribute("album", new Album());
			
			model.addAttribute("requiredSecurityPermissions", SecurityActions.AddChildAlbum.value());
			AlbumBo albumToSelect = galleryView.getAlbum();
			if (albumToSelect.getIsVirtualAlbum() || !galleryView.isUserAuthorized(SecurityActions.AddChildAlbum, albumToSelect)){
				albumToSelect = AlbumUtils.getHighestLevelAlbumWithCreatePermission(galleryView.getGalleryId());
			}
			model.addAttribute("selectedAlbumIds", new LongCollection(new long[] { albumToSelect.getId() }));
			
			return "cm/createalbum";
		}else if (rId == ResourceId.cm_editcaptions) { // edit caption 
			if (galleryView.getGallerySettings().getContentObjectPathIsReadOnly()) {
				return "cm/galleryview?aid=" + galleryView.getAlbumId() + StringUtils.format("&msg={0}", MessageType.CannotEditGalleryIsReadOnly.value());
			}
			
			return "cm/contentobjects/editcaptions";
		}else if (rId == ResourceId.cm_assignthumbnail) { //assign thumbnail for album
			if (galleryView.getGallerySettings().getContentObjectPathIsReadOnly()) {
				return "cm/galleryview?aid=" + galleryView.getAlbumId() + StringUtils.format("&msg={0}", MessageType.CannotEditGalleryIsReadOnly.value());
			}
			if (galleryView.getAlbum() == null || galleryView.getAlbum().getChildContentObjects().count() == 0) {
				return "cm/galleryview?aid=" + galleryView.getAlbumId() + StringUtils.format("&msg={0}", MessageType.CannotAssignThumbnailNoObjectsExistInAlbum.value());
			}
			
			return "cm/contentobjects/assignthumbnail";
		}else if (rId == ResourceId.cm_downloadobjects) { //download content objects
			model.addAttribute("enableContentObjectZipDownload", gallerySettings.getEnableContentObjectZipDownload());
			model.addAttribute("enableAlbumZipDownload", gallerySettings.getEnableAlbumZipDownload());
			model.addAttribute("imageSizes", getImageSizes(galleryView, request));
			
			return "cm/contentobjects/downloadobjects";
		}else if (rId == ResourceId.cm_deletealbum) {//delete albuum
			if (galleryView.getGallerySettings().getContentObjectPathIsReadOnly()) {
				return "cm/galleryview?aid=" + galleryView.getAlbumId() + StringUtils.format("&msg={0}", MessageType.CannotEditGalleryIsReadOnly.value());
			}
			
			return "cm/deletealbum";
		}else if (rId == ResourceId.cm_deleteobjects) {//delete content objects
			if (galleryView.getGallerySettings().getContentObjectPathIsReadOnly()) {
				return "cm/galleryview?aid=" + galleryView.getAlbumId() + StringUtils.format("&msg={0}", MessageType.CannotEditGalleryIsReadOnly.value());
			}
			
			model.addAttribute("userCanDeleteContentObject", galleryView.isUserCanDeleteContentObject());
			model.addAttribute("userCanDeleteChildAlbum", galleryView.isUserCanDeleteChildAlbum());
			
			return "cm/contentobjects/deleteobjects";
		}else if (rId == ResourceId.cm_deleteoriginals) {
			if (galleryView.getGallerySettings().getContentObjectPathIsReadOnly()) {
				return "cm/galleryview?aid=" + galleryView.getAlbumId() + StringUtils.format("&msg={0}", MessageType.CannotEditGalleryIsReadOnly.value());
			}
			
			model.addAttribute("userCanDeleteContentObject", galleryView.isUserCanDeleteContentObject());
			model.addAttribute("userCanDeleteChildAlbum", galleryView.isUserCanDeleteChildAlbum());
			model.addAttribute("totalFileSizeKbAllOriginalFiles", AlbumTreePickerBuilder.getFileSizeKbAllOriginalFilesInAlbum(galleryView.getAlbum()));
			
			return "cm/contentobjects/deleteoriginals";
		}else if (rId == ResourceId.cm_rotateimage) {
			ContentObjectBo mo = galleryView.getContentObject();
			if (mo == null) {
				return "cm/galleryview?aid=" + galleryView.getAlbumId() + StringUtils.format("&msg={0}", MessageType.ContentObjectDoesNotExist.value());
			}

			boolean isRotatable = false;
			switch (mo.getContentObjectType()){
				case Image:
					isRotatable = true;
					break;
				case Video:
					isRotatable = (StringUtils.isNotBlank(AppSettings.getInstance().getFFmpegPath()));
					break;
			}

			if (!isRotatable){
				return "cm/galleryview?g=contentobject&moid=" + mo.getId() + StringUtils.format("&msg={0}", MessageType.CannotRotateObjectNotRotatable.value());
			}
			
			return "cm/contentobjects/rotateimage";
		}else if (rId == ResourceId.cm_rotateimages) {
			ContentObjectBoCollection rotatableContentObjects = galleryView.getAlbum().getChildContentObjects(ContentObjectType.Image);

			ContentObjectBoCollection videos = galleryView.getAlbum().getChildContentObjects(ContentObjectType.Video);
			if (StringUtils.isNotBlank(AppSettings.getInstance().getFFmpegPath())){
				// Only include videos when FFmpeg is installed.
				rotatableContentObjects.addRange(videos.values());
			}

			List<ContentObjectBo> albumChildren = rotatableContentObjects.toSortedList();
			if (albumChildren.isEmpty()) {
				return Utils.getUrl(request, ResourceId.album, "aid={0}&msg={1}", aid, Integer.toString(MessageType.CannotRotateNoRotatableObjectsExistInAlbum.value()));
			}
			
			return "cm/contentobjects/rotateimages";
		}else if (rId == ResourceId.cm_transferobject) {
			String qsValue = request.getParameter("tt");
			String step = request.getParameter("step");
			String skipstep1 = request.getParameter("skipstep1");
			String showNextPage = request.getParameter("showNextPage");
			if ((skipstep1 != null && skipstep1.equals("true")) || (step != null && step.equals("step1"))){
				String ids = request.getParameter("ids");
				String selectId = (aid == null ? ("m" + moid) : ("a" + aid));
				String[] selectIds = new String[] {selectId};
				if (ids != null) {
					selectIds = StringUtils.split(ids, ",");
				}
				showTreeview(galleryView, request, model, selectIds );
				showNextPage = (step != null && step.equals("step1")) ? "1" : "0";
				model.addAttribute("showNextPage", showNextPage);
				model.addAttribute("hdnCheckedContentObjectIds", StringUtils.join(selectIds, ","));
			}
			if (qsValue.equals("move"))
				model.addAttribute("transferType", TransferType.Move);
			else if (qsValue.equals("copy"))
				model.addAttribute("transferType", TransferType.Copy);
			
			model.addAttribute("step", "step1");
			model.addAttribute("transferObjectState", TransferObject.getTransferObjectState(showNextPage, request));
			//model.addAttribute("requiredSecurityPermissions", SecurityActions.ViewAlbumOrContentObject.value());
		
			return "cm/contentobjects/transferobject"; 
		}else if (rId == ResourceId.cm_synchronize) {
			GalleryBo gallery = CMUtils.loadGallery(galleryView.getGalleryId());
			String rootAlbumPrefix = gallery.getRootAlbumPrefix().replace("{GalleryDescription}", gallery.getDescription());
			  
			model.addAttribute("albumTitle", HelperFunctions.removeHtmlTags(StringUtils.join(rootAlbumPrefix, album.getTitle().replace("{album.root_Album_Default_Title}", I18nUtils.getString("album.root_Album_Default_Title", request.getLocale())))));
			//model.addAttribute("syncCompleteJsRenderTemplate", Utils.jsEncode(getSyncCompleteJsRenderTemplate(request))); 
					
			return "cm/contentobjects/synchronize";
		}else {
			return "cm/galleryview";
		}
    }
    
    private List<Map<String, Object>> getImageSizes(GalleryView galleryView, HttpServletRequest request) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException {
    	List<Map<String, Object>> imageSizes = Lists.newArrayList();
    	Map<String, Object> map = Maps.newHashMap();
		map.put("size", DisplayObjectType.Thumbnail);
		map.put("info", I18nUtils.getString("task.downloadObjects.Select_Image_Size_Thumbnail_Option", request.getLocale()));
		imageSizes.add(map);
		
		map = Maps.newHashMap();
		map.put("size", DisplayObjectType.Optimized);
		map.put("info", I18nUtils.getString("task.downloadObjects.Select_Image_Size_Compressed_Option", request.getLocale()));
		imageSizes.add(map);
		
		if (galleryView.isUserAuthorized(SecurityActions.ViewOriginalContentObject)){
			map = Maps.newHashMap();
			map.put("size", DisplayObjectType.Original);
			map.put("info", I18nUtils.getString("task.downloadObjects.Select_Image_Size_Original_Option", request.getLocale()));
			imageSizes.add(map);
		}
		
    	return imageSizes;
    }
        
    private void showTreeview(GalleryView galleryView, HttpServletRequest request, Model model, String[] ids) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, InvalidMDSRoleException, RecordExistsException{
		// Find out if the objects we are transferring consist of only content objects, only albums, or both.
		// We use this knowledge to set the RequiredSecurityPermission property on the treeview user control
		// so that only albums where the user has permission are available for selection.
		boolean hasAlbums = false;
		boolean hasContentObjects = false;
		int securityActions = SecurityActions.NotSpecified.value();
		for (String id : ids){
			if (id.startsWith("a")){
				securityActions = ((securityActions == 0) ? SecurityActions.AddChildAlbum.value() : securityActions | SecurityActions.AddChildAlbum.value());
				hasAlbums = true;
			}
			if (id.startsWith("m")){
				securityActions = (((int)securityActions == 0) ? SecurityActions.AddContentObject.value() : securityActions | SecurityActions.AddContentObject.value());
				hasContentObjects = true;
			}
			
			if (hasAlbums && hasContentObjects)
				break;
		}

		model.addAttribute("requiredSecurityPermissions", securityActions);

		if (galleryView.isUserCanAdministerSite() || galleryView.isUserCanAdministerGallery())	{
			// Show all galleries the current user can administer. This allows them to move/copy objects between galleries.
			// We could have tried to show galleries where user has add album permission but that would have complicated things.
			// Simpler for the rule to be "Users can transfer to other galleries only where they are admins for both galleries."
			model.addAttribute("rootAlbumPrefix",  StringUtils.join(I18nUtils.getString("site.gallery_Text", request.getLocale()), " '{GalleryDescription}': "));
			model.addAttribute("galleries",  UserUtils.getGalleriesCurrentUserCanAdminister());
		}

		AlbumBo albumToSelect = galleryView.getAlbum();
		if (albumToSelect.getIsVirtualAlbum() || !galleryView.isUserAuthorized(SecurityActions.AddChildAlbum, albumToSelect)){
			albumToSelect = AlbumUtils.getHighestLevelAlbumWithAddPermission(hasAlbums, hasContentObjects, galleryView.getGalleryId());
		}

		if (albumToSelect == null){
			//RedirectToAlbumViewPage("msg={0}", ((int)MessageType.CannotTransferObjectInsufficientPermission).ToString(CultureInfo.InvariantCulture));
		}
		model.addAttribute("selectedAlbumIds", new LongCollection(new long[] { albumToSelect.getId() }));
	}
    
    /// <summary>
	/// Gets the album ID corresponding to the current album and assigns the album to the <paramref name="album" /> parameter. 
	/// The value is determined in the following sequence: (1) If <see cref="GalleryPage.GetContentObject"/> returns an 
	/// object (which will happen when a particular content object has been requested), then use the album ID of the 
	/// content object's parent. (2) When no content object is available, then look for the "aid" query string parameter.
	/// (3) If not there, or if <see cref="Gallery.AllowUrlOverride"/> has been set to <c>false</c>, look for an album
	/// ID on the containing <see cref="Gallery"/> control. (4) If we haven't found an album yet, load the top-level album
	/// for which the current user has view permission. This function verifies the album exists and the current user has permission
	/// to view it. If the album does not exist, a <see cref="InvalidAlbumException" /> is thrown. If the user does not have permission to
	/// view the album, a <see cref="GallerySecurityException" /> is thrown. Guaranteed to return a valid album ID, except
	/// when the user does not have view permissions to any album and when the top-level album is a virtual album, in which case
	/// it returns <see cref="Int32.MinValue"/>.
	/// </summary>
	/// <param name="album">The album associated with the current page.</param>
	/// <returns>
	/// Returns the album ID corresponding to the current album. 
	/// </returns>
	/// <exception cref="InvalidAlbumException">Thrown when the requested album does not exist.</exception>
	/// <exception cref="GallerySecurityException">Thrown when the user is requesting an album or content object they don't have 
	/// permission to view.</exception>
    public AlbumBo getAlbum(long galleryId, HttpServletRequest request) throws UnsupportedContentObjectTypeException, InvalidGalleryException, InvalidMDSRoleException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException{

		AlbumBo album;
		// First look for title/caption search text in the query string.
		if (Utils.isQueryStringParameterPresent(request, "title")){
            album = ContentObjectUtils.getContentObjectsHavingTitleOrCaption(Utils.getQueryStringParameterStrings(request, "title"), getContentObjectFilter(request), getContentObjectApprovalFilter(request), galleryId);
		}
		// Then look for search text in the query string.
		else if (Utils.isQueryStringParameterPresent(request, "search"))	{
            album = ContentObjectUtils.getContentObjectsHavingSearchString(Utils.getQueryStringParameterStrings(request, "search"), getContentObjectFilter(request), getContentObjectApprovalFilter(request), galleryId);
		}
		// Then look for tags in the query string.
		else if (Utils.isQueryStringParameterPresent(request, "tag") || Utils.isQueryStringParameterPresent(request, "people"))
		{
            album = ContentObjectUtils.getContentObjectsHavingTags(Utils.getQueryStringParameterStrings(request, "tag"), Utils.getQueryStringParameterStrings(request, "people"), getContentObjectFilter(request), getContentObjectApprovalFilter(request), galleryId);
		}
		// Then look for a request for the rated objects in the query string.
		else if (Utils.isQueryStringParameterPresent(request, "rating") ) //&& AppSetting.Instance.License.LicenseType == LicenseLevel.Enterprise
		{
            album = ContentObjectUtils.getRatedContentObjects(Utils.getQueryStringParameterString(request, "rating"), Utils.getQueryStringParameterInt32(request, "top"), galleryId, getContentObjectFilter(ContentObjectType.ContentObject, request), getContentObjectApprovalFilter(request));
		}
		// Then look for a request for the latest objects in the query string.
		else if (Utils.isQueryStringParameterPresent(request, "latest")) // && AppSetting.Instance.License.LicenseType == LicenseLevel.Enterprise
		{
            album = ContentObjectUtils.getMostRecentlyAddedContentObjects(Utils.getQueryStringParameterInt32(request, "latest"), galleryId, getContentObjectFilter(ContentObjectType.ContentObject, request), getContentObjectApprovalFilter(request));
		}
        // Then look for approval status in the query string.
		else if (Utils.isQueryStringParameterPresent(request, "approval"))
        {
            album = ContentObjectUtils.getApprovalContentObjects(galleryId, getContentObjectFilter(request), getContentObjectApprovalFilter(request));
        }
		else{
			// Nothing in viewstate, the query string, and no content object is specified. get the highest album the user can view.
			album = getHighestAlbumUserCanView(galleryId, request);
		}

		return album;
	}
    
    /// <summary>
	/// Gets the highest-level album the current user can view. Guaranteed to not return null. If a user does not have permission to 
	/// view any objects, this function returns a virtual album with no objects and automatically assigns the <see cref="ClientMessage" /> 
	/// property to <see cref="MessageType.NoAuthorizedAlbumForUser" />, which will cause a message to be displayed to the user.
	/// </summary>
	/// <returns>Returns an AlbumBo representing the highest-level album the current user can view.</returns>
	private AlbumBo getHighestAlbumUserCanView(long galleryId, HttpServletRequest request) throws WebException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException{
		ContentObjectSearcher contentObjectSearcher = new ContentObjectSearcher(new ContentObjectSearchOptions(galleryId,
																ContentObjectSearchType.HighestAlbumUserCanView,
																null,
																UserUtils.isAuthenticated(),
																RoleUtils.getMDSRolesForUser(),
																ContentObjectType.Album,
                                                                ApprovalStatus.All));

		ContentObjectBo album = contentObjectSearcher.findOne();

		AlbumBo tempAlbum = Reflections.as(album,  AlbumBo.class);
		if (album != null && tempAlbum == null)	{
			throw new WebException(StringUtils.format("A gallery object search for {0} returned an object that couldn't be cast to AlbumBo. It was a {1}.", ContentObjectSearchType.HighestAlbumUserCanView, album.getClass().getName()));
		}

		if (album == null){
			// Create virtual album so that page has something to bind to.
			tempAlbum = CMUtils.createEmptyAlbumInstance(galleryId);
			tempAlbum.setIsVirtualAlbum(true);
			tempAlbum.setVirtualAlbumType(VirtualAlbumType.Root);
			tempAlbum.setTitle(I18nUtils.getString("album.site_Virtual_Album_Title", request.getLocale()));
			tempAlbum.setCaption(StringUtils.EMPTY);

			/*if (Array.IndexOf(new[] { PageId.login, PageId.recoverpassword, PageId.createaccount }, PageId) < 0)
			{
				ClientMessage = GetMessageOptions(MessageType.NoAuthorizedAlbumForUser);
			}*/
		}

		return tempAlbum;
	}
    
  /// <summary>
	/// Gets the gallery data for the current content object, if one exists, or the current album.
	/// <see cref="Entity.MDSData.Settings" /> is assigned, unlike when this object is retrieved
	/// through the web service (since the control-specific settings can't be determined in that case).
	/// </summary>
	/// <returns>Returns an instance of <see cref="Entity.MDSData" />.</returns>
	private CMData getClientCMData(ContentObjectBo contentObject, AlbumBo album, GallerySettings gallerySettings, HttpServletRequest request) throws Exception{
		CMData data = contentObject != null ?
              GalleryUtils.getCMDataForContentObject(contentObject, album, new CMDataLoadOptions(false, true, getContentObjectFilter(request), getContentObjectApprovalFilter(request)), request) : // { LoadContentItems = true, Filter = GetContentObjectFilter(), ApprovalFilter = GetContentObjectApprovalFilter() }
            	  GalleryUtils.getCMDataForAlbum(album, new CMDataLoadOptions(true, false, getContentObjectFilter(request), getContentObjectApprovalFilter(request)), request); //, Filter = GetContentObjectFilter(), ApprovalFilter = GetContentObjectApprovalFilter() }

		data.setSettings(getSettingsEntity(album, gallerySettings));

		return data;
	}
	
	/// <summary>
	/// Gets a data entity containing information about the current gallery. The instance can be JSON-parsed and sent to the 
	/// browser.
	/// </summary>
	/// <returns>Returns <see cref="Entity.Settings" /> object containing information about the current gallery.</returns>
	private SettingsRest getSettingsEntity(AlbumBo album, GallerySettings gallerySettings){
		SettingsRest settings = new SettingsRest();
		settings.setGalleryId(gallerySettings.getGalleryId());
		settings.setClientId("MdsClientId");
		settings.setContentClientId(StringUtils.join(settings.getClientId(), "_mediaHtml"));
		settings.setContentTmplName(StringUtils.join(settings.getClientId(), "_media_tmpl"));
		settings.setHeaderClientId(StringUtils.join(settings.getClientId(), "_gHdrHtml"));
		settings.setHeaderTmplName(StringUtils.join(settings.getClientId(), "_gallery_header_tmpl"));
		settings.setThumbnailClientId(StringUtils.join(settings.getClientId(), "_thmbHtml"));
		settings.setThumbnailTmplName(StringUtils.join(settings.getClientId(), "_thumbnail_tmpl"));
		settings.setLeftPaneClientId(StringUtils.join(settings.getClientId(), "_lpHtml"));
		settings.setLeftPaneTmplName(StringUtils.join(settings.getClientId(), "_lp_tmpl"));
		settings.setRightPaneClientId(StringUtils.join(settings.getClientId(), "_rpHtml"));
		settings.setRightPaneTmplName(StringUtils.join(settings.getClientId(), "_rp_tmpl"));
		settings.setShowHeader(false); //gallerySettings.getShowHeader()
		settings.setShowLogin(gallerySettings.getShowLogin());
		settings.setShowSearch(gallerySettings.getShowSearch());
		settings.setShowContentObjectNavigation(true);//gallerySettings.getShowContentObjectNavigation());
		settings.setShowContentObjectIndexPosition(true); //gallerySettings.getShowContentObjectIndexPosition());
		settings.setEnableSelfRegistration(gallerySettings.getEnableSelfRegistration());
		settings.setEnableUserAlbum(gallerySettings.getEnableUserAlbum());
		settings.setAllowManageOwnAccount(gallerySettings.getAllowManageOwnAccount());
		settings.setTitle(gallerySettings.getGalleryTitle());
		settings.setTitleUrl(""); //gallerySettings.getGetTitleUrl());
		settings.setTitleUrlTooltip(""); //gallerySettings.getGetTitleUrlTooltip());
		settings.setShowContentObjectTitle(true); //gallerySettings.getShowContentObjectTitle());
		settings.setPageSize(gallerySettings.getPageSize());
		settings.setPagerLocation(gallerySettings.getPagerLocation().toString());
		settings.setTransitionType(gallerySettings.getContentObjectTransitionType().toString().toLowerCase());
		settings.setTransitionDurationMs((int)(gallerySettings.getContentObjectTransitionDuration() * 1000));
		settings.setShowContentObjectToolbar(true); //gallerySettings.getShowContentObjectToolbar());
		settings.setAllowDownload(gallerySettings.getEnableContentObjectDownload());
		settings.setAllowZipDownload(gallerySettings.getEnableContentObjectZipDownload());
		settings.setShowUrlsButton(true); //gallerySettings.getShowUrlsButton());
		settings.setShowSlideShowButton(true);//gallerySettings.getShowSlideShowButton());
		settings.setSlideShowIsRunning(false); //AutoPlaySlideShow && GetAlbum().GetChildContentObjects(ContentObjectType.Image).Any());
		settings.setSlideShowType(SlideShowType.FullScreen.toString());
		settings.setSlideShowIntervalMs(gallerySettings.getSlideshowInterval());
		settings.setShowTransferContentObjectButton(true); //ShowTransferContentObjectButton);
		settings.setShowCopyContentObjectButton(true); //ShowCopyContentObjectButton);
		settings.setShowRotateContentObjectButton(true); //gallerySettings.getShowRotateContentObjectButton());
		settings.setShowDeleteContentObjectButton(true); //ShowDeleteContentObjectButton);
		settings.setMaxThmbTitleDisplayLength(gallerySettings.getMaxThumbnailTitleDisplayLength());
		settings.setAllowAnonymousRating(gallerySettings.getAllowAnonymousRating());
		settings.setAllowAnonBrowsing(gallerySettings.getAllowAnonymousBrowsing());
		settings.setReadOnlyGallery(gallerySettings.getContentObjectPathIsReadOnly());
		
		return settings;
	}
	
	/// <summary>
	/// Gets the header text that appears at the top of each web page. This value is retrieved from the 
	/// <see cref="Gallery.GalleryTitle" /> property if specified; if not, it inherits the value from <see cref="IGallerySettings.GalleryTitle" />.
	/// </summary>
	/// <value>The gallery title.</value>
	public String getGalleryTitle()	{
		return (galleryControlSettings.getGalleryTitle() != null ? galleryControlSettings.getGalleryTitle() : gallerySettings.getGalleryTitle());
	}

	/// <summary>
	/// Gets the URL the user will be directed to when she clicks the gallery title. This value is retrieved from the 
	/// <see cref="Gallery.GalleryTitleUrl" /> property if specified; if not, it inherits the value from <see cref="IGallerySettings.GalleryTitleUrl" />.
	/// </summary>
	/// <value>The gallery title.</value>
	public String getGalleryTitleUrl(){
		return (galleryControlSettings.getGalleryTitleUrl() != null ? this.galleryControlSettings.getGalleryTitleUrl() : this.gallerySettings.getGalleryTitleUrl());
	}
	
	private String getTitleUrl(HttpServletRequest request){
		String url = getGalleryTitleUrl().trim();

		if (!StringUtils.isBlank(getGalleryTitle()) && (url.length() > 0))
			return (url == "~/" ? Utils.getCurrentPageUrl(request) : url);
		else
			return null;
	}

	private String getTitleUrlTooltip(HttpServletRequest request){
		String url = getGalleryTitleUrl().trim();

		if (!StringUtils.isBlank(getGalleryTitle()) && (url.length() > 0)){
			switch (url){
				case "/":
					{
						return I18nUtils.getString("Header_PageHeaderTextUrlToolTipWebRoot", request.getLocale());
					}
				case "~/":
					{
						return I18nUtils.getString("Header_PageHeaderTextUrlToolTipAppRoot", request.getLocale());
					}
				default:
					{
						return I18nUtils.getString("Header_PageHeaderTextUrlToolTip", request.getLocale(), url);
					}
			}
		}
		else
			return null;
	}
	
	private TreeView getAlbumTreeData(GalleryBo g, AlbumBo album, GallerySettings gallerySettings, HttpServletRequest request) throws UnsupportedContentObjectTypeException, InvalidGalleryException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException	{
		TreeViewOptions tvOptions = new TreeViewOptions();
		tvOptions.SelectedAlbumIds = ((album != null && album.getId() > Long.MIN_VALUE) ? new LongCollection(new long[] { album.getId() }) : new LongCollection());
		tvOptions.NavigateUrl = Utils.getCurrentPageUrl(request);
		tvOptions.EnableCheckboxPlugin = false;
		tvOptions.RequiredSecurityPermissions = new SecurityActions[] {SecurityActions.ViewAlbumOrContentObject};
		tvOptions.RootAlbumPrefix = StringUtils.EMPTY;
		tvOptions.Galleries = CMUtils.loadLoginUserGalleries();//new GalleryBoCollection();
		//tvOptions.Galleries.add(g);

		TreeView tv = AlbumTreeViewBuilder.getAlbumsAsTreeView(tvOptions);

		return tv;
	}
    
    private ContentObjectBo getContentObject(Long contentObjectId) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidGalleryException{
    	if (contentObjectId == null || contentObjectId == Long.MIN_VALUE)
			throw new ArgumentOutOfRangeException("contentObjectId", MessageFormat.format("A valid content object ID must be passed to this function. Instead, the value was {0}.", contentObjectId));

    	ContentObjectBo contentObject = null;
		ContentObjectBo tempContentObject = null;
		try
		{
			tempContentObject = CMUtils.loadContentObjectInstance(contentObjectId);
		}
		catch (ArgumentException ex) { }
		catch (InvalidContentObjectException ex) { }

		if (tempContentObject != null){
			// Perform a basic security check to make sure user can view content object. Another, more detailed security check is performed by child
			// user controls if necessary. (e.g. Perhaps the user is requesting the high-res version but he does not have the ViewOriginalImage 
			// permission. The view content object user control will verify this.)
			if (UserUtils.isUserAuthorized(SecurityActions.ViewAlbumOrContentObject.value() | SecurityActions.ViewOriginalContentObject.value(), RoleUtils.getMDSRolesForUser(), tempContentObject.getParent().getId(), tempContentObject.getGalleryId(), tempContentObject.getIsPrivate(), SecurityActionsOption.RequireOne, ((AlbumBo)tempContentObject.getParent()).getIsVirtualAlbum())){
				// User is authorized. Assign to page-level variable.
				contentObject = tempContentObject;
			}
		}

		return contentObject;
	}
    
    private AlbumBo getAlbum(Long albumId, boolean isWritable) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException{
		if (isWritable){
			return AlbumUtils.loadAlbumInstance(albumId, true, isWritable);
		}
		
		return AlbumUtils.loadAlbumInstance(albumId, true);

		/*if (this._album == null)
		{
			int albumId = GetAlbumId(); // Getting the album ID will set the _album variable.

			if (this._album == null)
				throw new InvalidOperationException("Retrieving the album ID should have also assigned an album to the _album member variable, but it did not.");
		}

		return this._album;*/
	}
    
    /// <summary>
	/// Gets the gallery object filter specified in the filter query string parameter. If not present or is not a valid
	/// value, returns <paramref name="defaultFilter" />. If <paramref name="defaultFilter" /> is not specified, 
	/// it defaults to <see cref="ContentObjectType.All" />.
	/// </summary>
	/// <param name="defaultFilter">The default filter. Defaults to <see cref="ContentObjectType.All" /> when not specified.</param>
	/// <returns>An instance of <see cref="ContentObjectType" />.</returns>
    private static ContentObjectType getContentObjectFilter(HttpServletRequest request) {
    	return getContentObjectFilter(ContentObjectType.All, request);
    }
    
	private static ContentObjectType getContentObjectFilter(ContentObjectType defaultFilter, HttpServletRequest request){
		if (Utils.isQueryStringParameterPresent(request, "filter")){
			return ContentObjectType.parse(Utils.getQueryStringParameterString(request, "filter"), defaultFilter);
		}

		return defaultFilter;
	}

  /// <summary>
  /// Gets the gallery object filter specified in the filter query string parameter. If not present or is not a valid
  /// value, returns <paramref name="defaultFilter" />. If <paramref name="defaultFilter" /> is not specified, 
  /// it defaults to <see cref="ContentObjectType.All" />.
  /// </summary>
  /// <param name="defaultFilter">The default filter. Defaults to <see cref="ContentObjectType.All" /> when not specified.</param>
  /// <returns>An instance of <see cref="ContentObjectType" />.</returns>
	private static ApprovalStatus getContentObjectApprovalFilter(HttpServletRequest request) {
		return getContentObjectApprovalFilter(ApprovalStatus.All, request);
	}
	
      private static ApprovalStatus getContentObjectApprovalFilter(ApprovalStatus defaultFilter, HttpServletRequest request) {
          if (Utils.isQueryStringParameterPresent(request, "approval"))
          {
              return ApprovalStatus.parse(Utils.getQueryStringParameterString(request, "approval"), defaultFilter);
          }

          return defaultFilter;
      }
}
