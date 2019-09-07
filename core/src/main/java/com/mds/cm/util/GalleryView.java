package com.mds.cm.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.utils.URIBuilder;

import com.mds.common.Constants;
import com.mds.cm.content.AlbumBo;
import com.mds.cm.content.ClientMessageOptions;
import com.mds.cm.content.ContentObjectBo;
import com.mds.cm.content.ContentObjectSearchOptions;
import com.mds.cm.content.ContentObjectSearcher;
import com.mds.sys.util.MDSRoleCollection;
import com.mds.cm.content.GalleryBo;
import com.mds.cm.content.GalleryBoCollection;
import com.mds.cm.content.GalleryControlSettings;
import com.mds.cm.content.GallerySettings;
import com.mds.sys.util.SecurityGuard;
import com.mds.cm.content.SynchronizationStatus;
import com.mds.cm.content.UiTemplateBoCollection;
import com.mds.sys.util.UserAccountCollection;
import com.mds.cm.exception.GallerySecurityException;
import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidContentObjectException;
import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.cm.rest.CMData;
import com.mds.cm.rest.CMDataLoadOptions;
import com.mds.cm.rest.SettingsRest;
import com.mds.cm.rest.SyncInitiator;
import com.mds.cm.rest.SyncOptions;
import com.mds.cm.rest.TreeView;
import com.mds.cm.rest.TreeViewOptions;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.mapper.JsonMapper;
import com.mds.common.utils.Reflections;
import com.mds.core.ActionResult;
import com.mds.core.ActionResultStatus;
import com.mds.core.ApprovalStatus;
import com.mds.core.ContentObjectSearchType;
import com.mds.core.ContentObjectType;
import com.mds.core.DisplayObjectType;
import com.mds.core.LongCollection;
import com.mds.core.MessageStyle;
import com.mds.core.MessageType;
import com.mds.core.ResourceId;
import com.mds.core.SecurityActions;
import com.mds.core.SecurityActionsOption;
import com.mds.core.SlideShowType;
import com.mds.core.SynchronizationState;
import com.mds.core.ViewMode;
import com.mds.core.VirtualAlbumType;
import com.mds.core.exception.ArgumentException;
import com.mds.core.exception.ArgumentNullException;
import com.mds.core.exception.ArgumentOutOfRangeException;
import com.mds.core.exception.NotSupportedException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.core.exception.WebException;
import com.mds.i18n.util.I18nUtils;
import com.mds.sys.model.Organization;
import com.mds.sys.util.AppSettings;
import com.mds.sys.util.RoleUtils;
import com.mds.sys.util.UserUtils;
import com.mds.util.DateUtils;
import com.mds.util.StringUtils;
import com.mds.util.Utils;

/// <summary>
/// The base class user control used in MDS System to represent page-like functionality.
/// </summary>
public class GalleryView{
	//#region Private Fields

	private long galleryId = Long.MIN_VALUE;
	private AlbumBo album;
	private Long contentObjectId;
	private ContentObjectBo contentObject;
	private ClientMessageOptions clientMessage;
	private MDSRoleCollection roles;
	private String pageTitle = StringUtils.EMPTY;
	private Boolean userCanViewAlbumOrContentObject;
	private Boolean userCanViewOriginal;
	private Boolean userCanAddAdministerSite;
	private Boolean userCanAdministerGallery;
	private Boolean userCanCreateAlbum;
	private Boolean userCanEditAlbum;
	private Boolean userCanAddContentObject;
	private Boolean userCanEditContentObject;
	private Boolean userCanDeleteCurrentAlbum;
	private Boolean userCanDeleteChildAlbum;
	private Boolean userCanDeleteContentObject;
	private Boolean userCanApprovalContentObject;
	private Boolean userCanSynchronize;
	private Boolean userDoesNotGetWatermark;
	private Boolean userCanAddContentObjectToAtLeastOneAlbum;
	private Boolean userCanAddAlbumToAtLeastOneAlbum;
	private Boolean userIsAdminForAtLeastOneOtherGallery;
	//private Gallery galleryControlSettings;
	private GalleryControlSettings galleryControlSettings;
	private GallerySettings gallerySetting;
	private int currentPage;
	private ResourceId resourceId;
	private Boolean showLogin;
	private Boolean showSearch;
	private Boolean allowAnonymousBrowsing;
	private Boolean showLeftPaneForAlbum;
	private Boolean showLeftPaneForContentObject;
	private Boolean showCenterPane;
	private Boolean showRightPane;
	private Boolean showActionMenu;
	private Boolean showAlbumBreadCrumb;
	private Boolean showHeader;
	private String galleryTitle;
	private String galleryTitleUrl;
	private Boolean showContentObjectToolbar;
	private Boolean showContentObjectTitle;
	private Boolean showContentObjectNavigation;
	private Boolean showContentObjectIndexPosition;
	private Boolean showUrlsButton;
	private Boolean showSlideShowButton;
	private Boolean showTransferContentObjectButton;
	private Boolean showCopyContentObjectButton;
	private Boolean showRotateContentObjectButton;
	private Boolean showDeleteContentObjectButton;
	private Boolean autoPlaySlideShow;
	private SlideShowType slideShowType = SlideShowType.NotSet;
	private UiTemplateBoCollection uiTemplates;
	private String controlId;
	
	private CMData mdsData;
	private TreeView tv;

	HttpServletRequest request;
	HttpServletResponse response;

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="GalleryView"/> class.
	/// </summary>
	public GalleryView(){
	}
	
	public GalleryView(long galleryId, Long aid, Long moid, ResourceId resourceId, HttpServletRequest request, HttpServletResponse response){
		this.galleryId = galleryId;
		this.contentObjectId = moid;
		this.resourceId = resourceId;
		this.request = request;
		this.response = response;
	}

	//#endregion

	//#region Event Handlers

	//#endregion

	//#region Properties
	
	public HttpServletRequest getRequest() {
		return request;
	}
	
	public HttpServletResponse getResponse() {
		return response;
	}

	/// <summary>
	/// Gets the client ID for the current Gallery control. This value can be used in client
	/// script to differentiate variables and other script when multiple instances of the control
	/// are placed on the web page. Returns <see cref="Control.ClientID" />, prepended with "mds_".
	/// </summary>
	/// <value>A String.</value>
	public String getMdsClientId(){
		 return StringUtils.join("mds_", "g");
	}
	
	 /// <summary>
    /// Gets a value that uniquely identifies this control. This value is used to identify its settings in the gallery control settings
    /// table. Example: "~/Default.aspx|mds"
    /// </summary>
    /// <value>A value that uniquely identifies this control.</value>
    /// <remarks>We use an application-relative file path rather than a server-relative path. This allows an admin to move the 
    /// application around and not lose control settings, but this means that if multiple applications are using the same database, then
    /// we must take care to use unique web page names or unique IDs.</remarks>
    public String getControlId() {
        if (StringUtils.isBlank(controlId)) {
          controlId =  StringUtils.join(request.getRequestURI(), "|", UserUtils.getLoginName());
          //String.Concat(System.Web.HttpContext.Current.Request.AppRelativeCurrentExecutionFilePath, "|", this.ClientID);
        }

        return controlId;
    }

	/// <summary>
	/// Gets the client ID for the current Gallery control. This value can be used in client
	/// script to differentiate variables and other script when multiple instances of the control
	/// are placed on the web page. Returns <see cref="Control.ClientID" />, prepended with "mds_".
	/// </summary>
	/// <value>A String.</value>
	/// <remarks>This property is simply a pass-through for getMdsClientId(). It's purpose is to be
	/// a very short variable that doesn't clutter up javascript.</remarks>
	public String getCId(){
		 return getMdsClientId();
	}

	/// <summary>
	/// Gets the client ID for the DOM element that is to receive the contents of the media
	/// object. Ex: "mds_g_mediaHtml"
	/// </summary>
	public String getContentClientId(){
		 return StringUtils.join(this.getMdsClientId(), "_mediaHtml");
	}

	/// <summary>
	/// Gets the name of the compiled jsRender template for the content object.
	/// Ex: "mds_g_media_tmpl"
	/// </summary>
	public String getContentTmplName(){
		 return StringUtils.join(this.getMdsClientId(), "_media_tmpl");
	}

	/// <summary>
	/// Gets the client ID for the DOM element that is to receive the contents of the gallery
	/// header. Ex: "mds_g_gHdrHtml"
	/// </summary>
	public String getHeaderClientId(){
		 return StringUtils.join(this.getMdsClientId(), "_gHdrHtml");
	}

	/// <summary>
	/// Gets the name of the compiled jsRender template for the header. Ex: "mds_g_gallery_header_tmpl"
	/// </summary>
	public String getHeaderTmplName(){
		 return StringUtils.join(this.getMdsClientId(), "_gallery_header_tmpl");
	}

	/// <summary>
	/// Gets the client ID for the DOM element that is to receive the contents of album thumbnail 
	/// images. Ex: "mds_g_thmbHtml"
	/// </summary>
	public String getThumbnailClientId(){
		 return StringUtils.join(this.getMdsClientId(), "_thmbHtml");
	}

	/// <summary>
	/// Gets the name of the compiled jsRender template for the album thumbnail images.
	/// Ex: "mds_g_thumbnail_tmpl"
	/// </summary>
	public String getThumbnailTmplName(){
		 return StringUtils.join(this.getMdsClientId(), "_thumbnail_tmpl");
	}

	/// <summary>
	/// Gets the client ID for the DOM element that is to receive the contents of the left pane
	/// of the media view page. Ex: "mds_g_lpHtml"
	/// </summary>
	public String getLeftPaneClientId(){
		 return StringUtils.join(this.getMdsClientId(), "_lpHtml");
	}

	/// <summary>
	/// Gets the name of the compiled jsRender template for the left pane of the media view page.
	/// Ex: "mds_g_lp_tmpl"
	/// </summary>
	public String getLeftPaneTmplName(){
		 return StringUtils.join(this.getMdsClientId(), "_lp_tmpl");
	}

	/// <summary>
	/// Gets the client ID for the DOM element that is to receive the contents of the left pane
	/// of the media view page. Ex: "mds_g_lpHtml"
	/// </summary>
	public String getRightPaneClientId(){
		 return StringUtils.join(this.getMdsClientId(), "_rpHtml");
	}

	/// <summary>
	/// Gets the name of the compiled jsRender template for the right pane of the media view page.
	/// Ex: "mds_g_rp_tmpl"
	/// </summary>
	public String getRightPaneTmplName(){
		 return StringUtils.join(this.getMdsClientId(), "_rp_tmpl");
	}

	/// <summary>
	/// Gets the value that uniquely identifies the gallery the current instance belongs to. This value is retrieved from the 
	/// requested content object or album, or from the <see cref="Gallery.getGalleryId()" /> property of the <see cref="Gallery" /> control 
	/// that created this instance. If no gallery ID is found by the previous search, then return the first gallery found in the database.
	/// Retrieving this value causes the <see cref="Gallery.getGalleryId()" /> on the containing control to be set to the same value.
	/// </summary>
	/// <value>The gallery ID for the current gallery.</value>
	/// <exception cref="InvalidAlbumException">Thrown when the requested album does not exist.</exception>
	/// <exception cref="GallerySecurityException">Thrown when the user is requesting an album or content object they don't have 
	/// permission to view.</exception>
	public long getGalleryId() throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException, InvalidAlbumException, RecordExistsException{
		if (galleryId == Long.MIN_VALUE){
			if (getContentObjectId() > Long.MIN_VALUE){
				galleryId = getContentObject().getGalleryId();
			}else if (parseAlbumId() > Long.MIN_VALUE){
				galleryId = getAlbum().getGalleryId();
			}
			else if (this.getGalleryControlSettings() != null && this.getGalleryControlSettings().getGalleryId() != null && this.getGalleryControlSettings().getGalleryId() > Long.MIN_VALUE && CMUtils.loadGalleries().stream().anyMatch(g -> g.getGalleryId() == this.getGalleryControlSettings().getGalleryId())){
				galleryId = this.getGalleryControlSettings().getGalleryId();
			}else{
				// There is no album or content object to get the gallery ID from, and no gallery ID has been specified on the control.
				// Just grab the first gallery in the database, creating it if necessary.
				GalleryBo gallery = CMUtils.loadLoginUserGalleries().stream().findFirst().orElse(null);
				if (gallery != null)	{
					galleryId = gallery.getGalleryId();
					this.getGalleryControlSettings().setGalleryId(galleryId);
					this.getGalleryControlSettings().save();
				}else{
					// No gallery found anywhere, including the data store. Create one and assign it to this control instance.
					GalleryBo g = CMUtils.createGalleryInstance();
					g.setName("m_"+ UUID.randomUUID().toString().replace("-", ""));
					g.addOrganization(UserUtils.getUserOrganizationId());
					g.setDescription(I18nUtils.getString("webapp.name", request.getLocale()));
					g.setCreationDate(DateUtils.Now());
					g.save();
					
					this.getGalleryControlSettings().setGalleryId(g.getGalleryId());
					this.getGalleryControlSettings().save();
					galleryId = g.getGalleryId();
				}
			}
		}

		if (this.getGalleryControlSettings().getGalleryId() == Long.MIN_VALUE){
			this.getGalleryControlSettings().setGalleryId(galleryId);
		}

		return galleryId;
	}

	/// <summary>
	/// Gets the gallery settings for the current gallery.
	/// </summary>
	/// <value>The gallery settings for the current gallery.</value>
	public GallerySettings getGallerySettings()	{
		return gallerySetting;
	}

	/// <summary>
	/// Gets or sets the page index when paging is enabled and active. This is one-based, so the first page is one, the second
	/// is two, and so one.
	/// </summary>
	/// <value>The current page index.</value>
	public int getCurrentPage(){
		if (this.currentPage == 0){
			int page = Utils.getQueryStringParameterInt32(request, "page");

			this.currentPage = (page > 0 ? page : 1);
		}

		return this.currentPage;
	}
	
	public void setCurrentPage(int currentPage) throws URISyntaxException{
		this.currentPage = currentPage;

		if (request.getSession(false) != null){
			URI backURL = this.getPreviousUri();
			if (backURL != null){
				// Update the page query String parameter so that the referring url points to the current page index.
				backURL = updateUriQueryString(request, backURL, "page", Integer.toString(this.currentPage));
			}else{
				backURL = updateUriQueryString(request, Utils.getCurrentPageURI(request), "page", Integer.toString(this.currentPage));
			}
			this.setPreviousUri(backURL);
		}
	}

	/// <summary>
	/// Gets a value indicating whether the current user is anonymous. If the user has authenticated with a user name/password, 
	/// this property is false.
	/// </summary>
	public boolean isAnonymousUser(){
		// Note: Do not store in a private field that lasts the lifetime of the page request, as this may give the wrong
		// value after logon and logoff events.
		return !UserUtils.isAuthenticated();
	}

	/// <summary>
	/// Gets a value indicating whether the current user has permission to administer the site. If true, the user
	/// has all possible permissions and there is nothing he or she can't do.
	/// </summary>
	public boolean isUserCanAdministerSite() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException{
		if (this.userCanAddAdministerSite == null)
			evaluateUserPermissions();

		return this.userCanAddAdministerSite;
	}

	/// <summary>
	/// Gets a value indicating whether the logged on user is a gallery administrator for the current gallery.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the logged on user is a gallery administrator for the current gallery; otherwise, <c>false</c>.
	/// </value>
	public boolean isUserCanAdministerGallery() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException{
		if (this.userCanAdministerGallery == null)
			evaluateUserPermissions();

		return this.userCanAdministerGallery;
	}

	/// <summary>
	/// Gets a value indicating whether the current user has permission to create a new album within the current album.
	/// </summary>
	public boolean isUserCanCreateAlbum() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException{
		if (this.userCanCreateAlbum == null)
			evaluateUserPermissions();

		return this.userCanCreateAlbum;
	}

	/// <summary>
	/// Gets a value indicating whether the current user has permission to edit information about the current album.
	/// This includes changing the album's title, description, start and end dates, assigning the album's thumbnail image,
	/// and rearranging the order of objects within the album.
	/// </summary>
	public boolean isUserCanEditAlbum() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException	{
		if (this.userCanEditAlbum == null)
			evaluateUserPermissions();

		return this.userCanEditAlbum;
	}

	/// <summary>
	/// Gets a value indicating whether the current user has permission to add content objects to the current album.
	/// </summary>
	public boolean isUserCanAddContentObject() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException{
		if (this.userCanAddContentObject == null)
			evaluateUserPermissions();

		return this.userCanAddContentObject;
	}

	/// <summary>
	/// Gets a value indicating whether the current user is a site or gallery administrator for at least one other
	/// gallery besides the current one.
	/// </summary>
	public boolean isUserIsAdminForAtLeastOneOtherGallery() throws InvalidMDSRoleException{
		if (this.userIsAdminForAtLeastOneOtherGallery == null)	{
			this.userIsAdminForAtLeastOneOtherGallery = UserUtils.getGalleriesCurrentUserCanAdminister().stream().anyMatch(g -> {
				try {
					return g.getGalleryId() != this.getGalleryId();
				} catch (UnsupportedContentObjectTypeException | UnsupportedImageTypeException
						| InvalidContentObjectException | InvalidMDSRoleException | GallerySecurityException
						| IOException | InvalidGalleryException | WebException | InvalidAlbumException | RecordExistsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			});
		}

		return this.userIsAdminForAtLeastOneOtherGallery;
	}

	/// <summary>
	/// Gets a value indicating whether the current user has permission to edit the current content object. This includes 
	/// changing the content object's caption, rotating the object (if it is an image), and deleting the high resolution
	/// version of the object (applies only if it is an image).
	/// </summary>
	public boolean isUserCanEditContentObject() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException	{
		if (this.userCanEditContentObject == null)
			evaluateUserPermissions();

		return this.userCanEditContentObject;
	}

	/// <summary>
	/// Gets a value indicating whether the current user has permission to delete the current album.
	/// </summary>
	public boolean isUserCanDeleteCurrentAlbum() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException{
		if (this.userCanDeleteCurrentAlbum == null)
			evaluateUserPermissions();

		return this.userCanDeleteCurrentAlbum;
	}

	/// <summary>
	/// Gets a value indicating whether the current user has permission to delete albums within the current album.
	/// </summary>
	public boolean isUserCanDeleteChildAlbum() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException{
		if (this.userCanDeleteChildAlbum == null)
			evaluateUserPermissions();

		return this.userCanDeleteChildAlbum;
	}

	/// <summary>
	/// Gets a value indicating whether the current user has permission to delete a content object in the current album.
	/// </summary>
	public boolean isUserCanDeleteContentObject() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException{
		if (this.userCanDeleteContentObject == null)
			evaluateUserPermissions();

		return this.userCanDeleteContentObject;
	}

	/// <summary>
	/// Gets a value indicating whether the current user has permission to approval a content object in the current album.
	/// </summary>
	public boolean isUserCanApprovalContentObject() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException{
		if (this.userCanApprovalContentObject == null)
			evaluateUserPermissions();

		return this.userCanApprovalContentObject;
	}

	/// <summary>
	/// Gets a value indicating whether the current user has permission to synchronize the current album.
	/// </summary>
	public boolean isUserCanSynchronize() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException{
		if (this.userCanSynchronize == null)
			evaluateUserPermissions();

		return this.userCanSynchronize;
	}

	/// <summary>
	/// Gets a value indicating whether the current user has permission to view an image without a watermark applied to it.
	/// </summary>
	public boolean isUserDoesNotGetWatermark() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException{
		if (this.userDoesNotGetWatermark == null)
			evaluateUserPermissions();

		return this.userDoesNotGetWatermark;
	}

	/// <summary>
	/// Gets a value indicating whether the current user has permission to view the current content object and album.
	/// </summary>
	public boolean isUserCanViewAlbumOrContentObject() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException	{
		if (this.userCanViewAlbumOrContentObject == null)
			evaluateUserPermissions();

		return this.userCanViewAlbumOrContentObject;
	}

	/// <summary>
	/// Gets a value indicating whether the current user has permission to view the original version of a content object.
	/// </summary>
	public boolean isUserCanViewOriginal() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException{
		if (this.userCanViewOriginal == null)
			evaluateUserPermissions();

		return this.userCanViewOriginal;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the current user has permission to add content objects to at least one album.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if current user has permission to add content objects to at least one album; otherwise, <c>false</c>.
	/// </value>
	public boolean isUserCanAddContentObjectToAtLeastOneAlbum() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException	{
		if (this.userCanAddContentObjectToAtLeastOneAlbum == null)
			evaluateUserPermissions();

		return this.userCanAddContentObjectToAtLeastOneAlbum;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the current user has permission to add albums to at least one album.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the current user has permission to add albums to at least one album; otherwise, <c>false</c>.
	/// </value>
	public boolean isUserCanAddAlbumToAtLeastOneAlbum() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException	{
		if (this.userCanAddAlbumToAtLeastOneAlbum == null)
			evaluateUserPermissions();

		return this.userCanAddAlbumToAtLeastOneAlbum;
	}

	/// <summary>
	/// Gets or sets the message to display to the user, such as "Invalid login". The value is retrieved from the
	/// "msgId" query String parameter or from a private field if it was explicitly assigned earlier in the current page's
	/// life cycle. Returns null if the parameter is not found, it is not a valid integer, or it is &lt;= 0.
	/// Setting this property sets a private field that lives as long as the current page lifecycle. It is not persisted across
	/// postbacks or added to the queryString. Set the value only when you will use it later in the current page's lifecycle.
	/// Defaults to null.
	/// </summary>
	public ClientMessageOptions getClientMessage(){
		if (clientMessage == null){
			int msgId = Utils.getQueryStringParameterInt32(request, "msg");
			if (msgId > Integer.MIN_VALUE)	{
				MessageType message = MessageType.getMessageType(msgId);
				clientMessage = getMessageOptions(message);
			}
		}
		return clientMessage;
	}
	
	public void setClientMessage(ClientMessageOptions clientMessage){
		this.clientMessage = clientMessage;
	}

	/// <summary>
	/// Gets or sets the value that identifies the type of gallery page that is currently being displayed.
	/// </summary>
	/// <value>The value that identifies the type of gallery page that is currently being displayed.</value>
	/// <exception cref="InvalidOperationException">Thrown when the property is accessed before it has been set.</exception>
	public ResourceId getResourceId(){
		if (this.resourceId == null)
			throw new UnsupportedOperationException("The ResourceId property has not been set to a valid value.");

		return this.resourceId;
	}
	
	public void setResourceId(ResourceId resourceId){
		this.resourceId = resourceId;
	}

	/// <summary>
	/// Gets or sets the instance of the user control that created this user control.
	/// </summary>
	/// <value>The user control that created this user control.</value>
	/// <exception cref="WebException">Thrown when an instance of the <see cref="Gallery" /> control is not found in the parent 
	/// heirarchy of the current control.</exception>
	public GalleryControlSettings getGalleryControlSettings(){
		if (galleryControlSettings != null)
			return galleryControlSettings;

		galleryControlSettings = CMUtils.loadGalleryControlSetting(getControlId());
		return galleryControlSettings;
	}

	public void setGalleryControlSettings(GalleryControlSettings galleryControlSettings){
		this.galleryControlSettings = galleryControlSettings;
	}
	
	/// <summary>
    /// Gets or sets a value indicating how the content objects are to be rendered in the browser. The default value is ViewMode.Multiple. When the value is 
    /// ViewMode.Multiple, the contents of an album are shown as a set of thumbnail images. When set to ViewMode.Single, a single content object is 
    /// displayed. When set to ViewMode.SingleRandom, a single content object is displayed that is randomly selected. When a <see cref="ContentObjectId" /> is 
    /// specified, the <see cref="ViewMode" /> is automatically set to ViewMode.Single.
    /// </summary>
    /// <value>A value indicating how the content objects are to be rendered in the browser.</value>
    public ViewMode getViewMode() {
        return ( ViewMode.NotSet != galleryControlSettings.getViewMode() ? galleryControlSettings.getViewMode() : ViewMode.Multiple);
     }
    
    public void setViewMode(ViewMode viewMode) {
        galleryControlSettings.setViewMode(viewMode);
    }

	/// <summary>
	/// Gets or sets a value that can be used in the title tag in the HTML page header. If this property is not set by the user
	/// control, the current album's title is used.
	/// </summary>
	/// <value>A value that can be used in the title tag in the HTML page header.</value>
	public String getPageTitle() throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException{
		if (StringUtils.isEmpty(pageTitle))	{
			// Get an HTML-cleaned version of the current album's title, limited to the first 50 characters.
			String title = Utils.removeHtmlTags(getAlbum().getTitle());
			title = title.substring(0, title.length() < 50 ? title.length() : 50);

			return StringUtils.join(I18nUtils.getString("uc.thumbnailView.Album_Title_Prefix_Text", request.getLocale()), " ", title);
		}else
			return pageTitle;
	}
	
	public void setPageTitle(String pageTitle){
			this.pageTitle = pageTitle;
	}

	/// <summary>
	/// Gets or sets the URI of the previous page the user was viewing. The value is stored in the user's session, and 
	/// can be used after a user has completed a task to return to the original page. If the Session object is not available,
	/// no value is saved in the setter and a null is returned in the getter.
	/// </summary>
	/// <value>The URI of the previous page the user was viewing.</value>
	public URI getPreviousUri(){
		return Utils.getPreviousUri(request);
	}

	public void setPreviousUri(URI uri){
		Utils.setPreviousUri(request, uri);
	}
	
	/// <summary>
	/// Gets the URL of the previous page the user was viewing. The value is based on the <see cref="getPreviousUri()" /> property
	/// and is relative to the website root. If <see cref="getPreviousUri()" /> is null, such as when the Session object is not
	/// available or it has never been assigned, return StringUtils.EMPTY. Remove the query String parameter "msg" if present. 
	/// Ex: "/gallery/ds/default.aspx?moid=770"
	/// </summary>
	/// <value>The URL of the previous page the user was viewing.</value>
	public String getPreviousUrl(){
		if (getPreviousUri() != null)
			return Utils.removeQueryStringParameter(getPreviousUri().getPath(), "msg");
		else
			return StringUtils.EMPTY;
	}
	
	public String getAlbumViewPageUrl() throws UnsupportedContentObjectTypeException, InvalidGalleryException, InvalidMDSRoleException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException{
		return Utils.getUrl(request, ResourceId.album, "aid={0}", getAlbumId());
	}

	/// <summary>
	/// Gets a value indicating whether to show the login controls at the top right of each page. When false, no login controls
	/// are shown, but the user can still navigate directly to the login page to log on. This value is retrieved from the 
	/// <see cref="Gallery.isShowLogin()" /> property if specified; if not, it inherits the value from <see cref="getGallerySettings().isShowLogin()" />. 
	/// </summary>
	/// <value><c>true</c> if login controls are visible; otherwise, <c>false</c>.</value>
	public boolean isShowLogin(){
		if (showLogin == null){
			this.showLogin = (this.getGalleryControlSettings().getShowLogin() != null ? this.getGalleryControlSettings().getShowLogin() : this.getGallerySettings().getShowLogin());
		}

		return this.showLogin;
	}

	/// <summary>
	/// Gets a value indicating whether to show the search box at the top right of each page. This value is retrieved from the 
	/// <see cref="Gallery.isShowSearch()" /> property if specified; if not, it inherits the value from <see cref="getGallerySettings().isShowSearch()" />. 
	/// </summary>
	/// <value><c>true</c> if the search box is visible; otherwise, <c>false</c>.</value>
	public boolean isShowSearch(){
		if (showSearch == null)	{
			this.showSearch = (this.getGalleryControlSettings().getShowSearch() != null ? this.getGalleryControlSettings().getShowSearch() : this.getGallerySettings().getShowSearch());
		}

		return this.showSearch;
	}

	/// <summary>
	/// Gets a value indicating whether users can view galleries without logging in. When false, users are redirected to a login
	/// page when any album is requested. Private albums are never shown to anonymous users, even when this property is true. 
	/// This value is retrieved from the <see cref="Gallery.isAllowAnonymousBrowsing()" /> property if specified; if not, it inherits 
	/// the value from <see cref="getGallerySettings().isAllowAnonymousBrowsing()" />.
	/// </summary>
	/// <value><c>true</c> if anonymous users can view the gallery; otherwise, <c>false</c>.</value>
	public boolean isAllowAnonymousBrowsing(){
		if (allowAnonymousBrowsing == null)	{
			this.allowAnonymousBrowsing = (this.getGalleryControlSettings().getAllowAnonymousBrowsing() != null ? this.getGalleryControlSettings().getAllowAnonymousBrowsing() : this.getGallerySettings().getAllowAnonymousBrowsing());
		}

		return this.allowAnonymousBrowsing;
	}
	
	/// <summary>
    /// Gets or sets a value indicating whether an album or content object specified in the URL can override the <see cref="GalleryId" />,
    /// <see cref="AlbumId" />, and <see cref="ContentObjectId" /> properties of this control. Use the query string parameter "aid" to 
    /// specify an album; use "moid" for a content object (example: default.aspx?aid=12 for album ID=12, default.aspx?moid=37 for media
    /// object ID=37). If the property has not been explicitly assigned a value, it returns the value of 
    /// <see cref="IGalleryControlSettings.AllowUrlOverride" />. Returns a default value of <c>true</c> if no gallery control setting exists.
    /// </summary>
    /// <value><c>true</c> if an album or content object specified in the query string can override one specified as a control property; otherwise,
    ///  <c>false</c>.</value>
    public boolean isAllowUrlOverride() {
        return getGalleryControlSettings().getAllowUrlOverride() != null ? getGalleryControlSettings().getAllowUrlOverride(): true;
    }
    
    public void setAllowUrlOverride(boolean allowUrlOverride) {
        getGalleryControlSettings().setAllowUrlOverride(allowUrlOverride);
    }

	/// <summary>
	/// Gets or sets a value indicating whether to render the left pane when an album is being displayed.
	/// This value is retrieved from the <see cref="Gallery.isShowLeftPaneForAlbum()" /> property if specified; if not, it uses a 
	/// default value of <c>true</c>.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the left pane is to be rendered; otherwise, <c>false</c>.
	/// </value>
	public boolean isShowLeftPaneForAlbum()	{
		if (this.showLeftPaneForAlbum == null)	{
			this.showLeftPaneForAlbum = this.getGalleryControlSettings().getShowLeftPaneForAlbum() == null  ?  true : this.getGalleryControlSettings().getShowLeftPaneForAlbum();
		}

		return this.showLeftPaneForAlbum;
	}

	/// <summary>
	/// Gets or sets a value indicating whether to render the left pane when a single content object is
	/// being displayed. This value is retrieved from the <see cref="Gallery.isShowLeftPaneForContentObject()" /> 
	/// property if specified; if not, it uses a default value of <c>true</c>.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the left pane is to be rendered when a single content object is being displayed; otherwise, <c>false</c>.
	/// </value>
	public boolean isShowLeftPaneForContentObject(){
		if (this.showLeftPaneForContentObject == null)	{
			this.showLeftPaneForContentObject = this.getGalleryControlSettings().getShowLeftPaneForContentObject() == null  ?  true : this.getGalleryControlSettings().getShowLeftPaneForContentObject();
		}

		return this.showLeftPaneForContentObject;
	}

	/// <summary>
	/// Gets or sets a value indicating whether to render the center pane. This value is retrieved from the <see cref="Gallery.isShowCenterPane()" /> 
	/// property if specified; if not, it uses a default value of <c>true</c>.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the center pane is to be rendered; otherwise, <c>false</c>.
	/// </value>
	public boolean isShowCenterPane(){
		if (this.showCenterPane == null)
		{
			this.showCenterPane = this.getGalleryControlSettings().getShowCenterPane() == null  ?  true : this.getGalleryControlSettings().getShowCenterPane();
		}

		return this.showCenterPane;
	}

	/// <summary>
	/// Gets or sets a value indicating whether to render the right pane. This value is retrieved from the <see cref="Gallery.isShowRightPane()" /> 
	/// property if specified; if not, it uses a default value of <c>true</c>.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the right pane is to be rendered; otherwise, <c>false</c>.
	/// </value>
	public boolean isShowRightPane(){
		if (this.showRightPane == null)	{
			this.showRightPane = this.getGalleryControlSettings().getShowRightPane() == null  ?  true : this.getGalleryControlSettings().getShowRightPane();
		}

		return this.showRightPane;
	}

	/// <summary>
	/// Gets a value indicating whether to render the Actions menu. This value is retrieved from the 
	/// <see cref="Gallery.isShowActionMenu()" /> property if specified; if not, it uses a default value of <c>true</c>. Note that calling 
	/// code may determine the Actions menu should be hidden even if this property returns <c>true</c>. For example, this will happen
	/// when the currently logged on user does not have permission to execute any of the actions in the menu.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the Actions menu is to be rendered; otherwise, <c>false</c>.
	/// </value>
	public boolean isShowActionMenu(){
		if (this.showActionMenu == null)	{
			this.showActionMenu = (this.getGalleryControlSettings().getShowActionMenu() != null ? this.getGalleryControlSettings().getShowActionMenu() : true);
		}

		return this.showActionMenu;
	}
	
	public boolean isShowAlbumMenu() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException{
		return (isUserCanAdministerSite() || isUserCanAdministerGallery() || this.isShowActionMenu() || this.isShowAlbumBreadCrumb());
	}
	
	/// <summary>
	/// Gets a value indicating whether to render the album bread crumb links. This value is retrieved from the 
	/// <see cref="Gallery.isShowAlbumBreadCrumb()" /> property if specified; if not, it uses a default value of <c>true</c>.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the album bread crumb links are to be visible; otherwise, <c>false</c>.
	/// </value>
	public boolean isShowAlbumBreadCrumb(){
		if (this.showAlbumBreadCrumb == null)	{
			this.showAlbumBreadCrumb = (this.getGalleryControlSettings().getShowAlbumBreadCrumb() != null ? this.getGalleryControlSettings().getShowAlbumBreadCrumb() : true);
		}

		return this.showAlbumBreadCrumb;
	}

	/// <summary>
	/// Gets a value indicating whether to render the header at the top of the gallery. This value is retrieved from the 
	/// <see cref="Gallery.isShowHeader()" /> property if specified; if not, it inherits the value from <see cref="getGallerySettings().isShowHeader()" />.
	/// The header includes the gallery title, login/logout controls, user account management link, and search 
	/// function. The title, login/logout controls and search function can be individually controlled via the <see cref="getGalleryTitle()" />,
	/// <see cref="isShowLogin()" /> and <see cref="isShowSearch()" /> properties.
	/// </summary>
	/// <value><c>true</c> if the header is to be dislayed; otherwise, <c>false</c>.</value>
	public boolean isShowHeader(){
		if (this.showHeader == null){
			this.showHeader = (this.getGalleryControlSettings().getShowHeader() != null ? this.getGalleryControlSettings().getShowHeader() : this.getGallerySettings().getShowHeader());
		}

		return this.showHeader;
	}

	/// <summary>
	/// Gets the header text that appears at the top of each web page. This value is retrieved from the 
	/// <see cref="Gallery.getGalleryTitle()" /> property if specified; if not, it inherits the value from <see cref="getGallerySettings().getGalleryTitle()" />.
	/// </summary>
	/// <value>The gallery title.</value>
	public String getGalleryTitle()	{
		if (galleryTitle == null){
			this.galleryTitle = (getGalleryControlSettings().getGalleryTitle() != null ? this.getGalleryControlSettings().getGalleryTitle() : this.getGallerySettings().getGalleryTitle());
		}

		return this.galleryTitle;
	}

	/// <summary>
	/// Gets the URL the user will be directed to when she clicks the gallery title. This value is retrieved from the 
	/// <see cref="Gallery.getGalleryTitleUrl()" /> property if specified; if not, it inherits the value from <see cref="getGallerySettings().getGalleryTitleUrl()" />.
	/// </summary>
	/// <value>The gallery title.</value>
	public String getGalleryTitleUrl(){
		if (galleryTitleUrl == null){
			this.galleryTitleUrl = (getGalleryControlSettings().getGalleryTitleUrl() != null ? this.getGalleryControlSettings().getGalleryTitleUrl() : this.getGallerySettings().getGalleryTitleUrl());
		}

		return this.galleryTitleUrl;
	}

	/// <summary>
	/// Gets a value indicating whether the toolbar is rendered above individual content objects. This value is retrieved from the 
	/// <see cref="Gallery.isShowContentObjectToolbar()" /> property if specified; if not, it uses a default value of <c>true</c>.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the toolbar is rendered above individual content objects; otherwise, <c>false</c>.
	/// </value>
	public boolean isShowContentObjectToolbar(){
		if (this.showContentObjectToolbar == null){
			this.showContentObjectToolbar = (this.getGalleryControlSettings().getShowContentObjectToolbar() != null ? this.getGalleryControlSettings().getShowContentObjectToolbar() : true);
		}

		return this.showContentObjectToolbar;
	}

	/// <summary>
	/// Gets a value indicating whether the title is displayed beneath individual content objects. This value is retrieved from the 
	/// <see cref="Gallery.isShowContentObjectTitle()" /> property if specified; if not, it uses a default value of <c>true</c>.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the title is displayed beneath individual content objects; otherwise, <c>false</c>.
	/// </value>
	public boolean isShowContentObjectTitle(){
		if (this.showContentObjectTitle == null){
			this.showContentObjectTitle = (this.getGalleryControlSettings().getShowContentObjectTitle() != null ? this.getGalleryControlSettings().getShowContentObjectTitle() : true);
		}

		return this.showContentObjectTitle;
	}

	/// <summary>
	/// Gets a value indicating whether the next and previous buttons are rendered for individual content objects. This value is retrieved 
	/// from the <see cref="Gallery.isShowContentObjectNavigation()" /> property if specified; if not, it uses a default value of <c>true</c>.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the next and previous buttons are rendered for individual content objects; otherwise, <c>false</c>.
	/// </value>
	public boolean isShowContentObjectNavigation(){
		if (this.showContentObjectNavigation == null){
			this.showContentObjectNavigation = (this.getGalleryControlSettings().getShowContentObjectNavigation() != null ? this.getGalleryControlSettings().getShowContentObjectNavigation() : true);
		}

		return this.showContentObjectNavigation;
	}

	/// <summary>
	/// Gets a value indicating whether to display the relative position of a content object within an album (example: (3 of 24)). 
	/// This value is retrieved from the <see cref="Gallery.isShowContentObjectNavigation()" /> property if specified; if not, it uses a 
	/// default value of <c>true</c>. Applicable only when a single content object is displayed.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the relative position of a content object within an album is to be rendered; otherwise, <c>false</c>.
	/// </value>
	public boolean isShowContentObjectIndexPosition(){
		if (this.showContentObjectIndexPosition == null){
			this.showContentObjectIndexPosition = (this.getGalleryControlSettings().getShowContentObjectIndexPosition() != null ? this.getGalleryControlSettings().getShowContentObjectIndexPosition() : true);
		}

		return this.showContentObjectIndexPosition;
	}

	/// <summary>
	/// Gets a value indicating whether the download links/embed code button is visible above a content object. This value is retrieved from the 
	/// <see cref="Gallery.isShowUrlsButton()" /> property if specified; if not, it defaults to <c>true</c>.
	/// When <see cref="isShowContentObjectToolbar()" />=<c>false</c>, this property is ignored.
	/// </summary>
	/// <value><c>true</c> if the show permalink button is visible above a content object; otherwise, <c>false</c>.</value>
	public boolean isShowUrlsButton(){
		if (this.showUrlsButton == null){
			this.showUrlsButton = this.getGalleryControlSettings().getShowUrlsButton() == null  ?  true : this.getGalleryControlSettings().getShowUrlsButton();
		}

		return this.showUrlsButton;
	}

	/// <summary>
	/// Gets a value indicating whether the play/pause slide show button is visible above a content object. This value is retrieved from the 
	/// <see cref="Gallery.isShowSlideShowButton()" /> property if specified; if not, it inherits the value from 
	/// <see cref="getGallerySettings().EnableSlideShow" />.  When <see cref="isShowContentObjectToolbar()" />=<c>false</c>, this property is ignored.
	/// </summary>
	/// <value><c>true</c> if the play/pause slide show button is visible above a content object; otherwise, <c>false</c>.</value>
	public boolean isShowSlideShowButton(){
		if (showSlideShowButton == null){
			this.showSlideShowButton = this.getGalleryControlSettings().getShowSlideShowButton() == null ? this.getGallerySettings().getEnableSlideShow() : this.getGalleryControlSettings().getShowSlideShowButton();
		}

		return this.showSlideShowButton;
	}

	/// <summary>
	/// Gets or sets a value indicating the type of slide show to use for images. This value is retrieved 
	/// from the <see cref="Gallery.SlideShowType" /> property if specified; if not, it inherits the value from 
	/// <see cref="getGallerySettings().SlideShowType" />.
	/// </summary>
	/// <value>An instance of <see cref="SlideShowType" />.</value>
	public SlideShowType getSlideShowType(){
		if (this.slideShowType == SlideShowType.NotSet)	{
			SlideShowType ssType = this.getGalleryControlSettings().getSlideShowType();
			this.slideShowType = ((ssType != null && ssType != SlideShowType.NotSet) ? ssType : this.getGallerySettings().getSlideShowType());
		}

		return this.slideShowType;
	}

	/// <summary>
	/// Gets a value indicating whether the transfer content object button is visible above a content object. The button is not
	/// shown if the current user does not have permission to move content objects, even if this property is <c>true</c>. This 
	/// value is retrieved from the <see cref="Gallery.isShowTransferContentObjectButton()" /> property if specified; if not, it uses
	/// a default value of <c>true</c>. When <see cref="isShowContentObjectToolbar()" />=<c>false</c>, this property is ignored.
	/// </summary>
	/// <value><c>true</c> if the transfer content object button is visible above a content object; otherwise, <c>false</c>.</value>
	public boolean isShowTransferContentObjectButton(){
		if (this.showTransferContentObjectButton == null) {
			this.showTransferContentObjectButton = this.getGalleryControlSettings().getShowTransferContentObjectButton() == null  ?  true : this.getGalleryControlSettings().getShowTransferContentObjectButton();
		}

		return this.showTransferContentObjectButton;
	}

	/// <summary>
	/// Gets a value indicating whether the copy content object button is visible above a content object. The button is not
	/// shown if the current user does not have permission to copy content objects, even if this property is <c>true</c>. This 
	/// value is retrieved from the <see cref="Gallery.isShowCopyContentObjectButton()" /> property if specified; if not, it uses
	/// a default value of <c>true</c>. When <see cref="isShowContentObjectToolbar()" />=<c>false</c>, this property is ignored.
	/// </summary>
	/// <value><c>true</c> if the copy content object button is visible above a content object; otherwise, <c>false</c>.</value>
	public boolean isShowCopyContentObjectButton(){
		if (this.showCopyContentObjectButton == null) {
			this.showCopyContentObjectButton = this.getGalleryControlSettings().getShowCopyContentObjectButton() == null  ?  true : this.getGalleryControlSettings().getShowCopyContentObjectButton();
		}

		return this.showCopyContentObjectButton;
	}

	/// <summary>
	/// Gets a value indicating whether the rotate content object button is visible above a content object. The button is not
	/// shown if the current user does not have permission to rotate content objects, even if this property is <c>true</c>. This 
	/// value is retrieved from the <see cref="Gallery.isShowRotateContentObjectButton()" /> property if specified; if not, it uses
	/// a default value of <c>true</c>. When <see cref="isShowContentObjectToolbar()" />=<c>false</c>, this property is ignored.
	/// </summary>
	/// <value><c>true</c> if the rotate content object button is visible above a content object; otherwise, <c>false</c>.</value>
	public boolean isShowRotateContentObjectButton(){
		if (this.showRotateContentObjectButton == null)	{
			this.showRotateContentObjectButton = this.getGalleryControlSettings().getShowRotateContentObjectButton() == null  ?  true : this.getGalleryControlSettings().getShowRotateContentObjectButton();
		}

		return this.showRotateContentObjectButton;
	}

	/// <summary>
	/// Gets a value indicating whether the delete content object button is visible above a content object. The button is not
	/// shown if the current user does not have permission to delete content objects, even if this property is <c>true</c>. This 
	/// value is retrieved from the <see cref="Gallery.isShowDeleteContentObjectButton()" /> property if specified; if not, it uses
	/// a default value of <c>true</c>. When <see cref="isShowContentObjectToolbar()" />=<c>false</c>, this property is ignored.
	/// </summary>
	/// <value><c>true</c> if the delete content object button is visible above a content object; otherwise, <c>false</c>.</value>
	public boolean isShowDeleteContentObjectButton(){
		if (this.showDeleteContentObjectButton == null)	{
			this.showDeleteContentObjectButton = this.getGalleryControlSettings().getShowDeleteContentObjectButton() == null  ?  true : this.getGalleryControlSettings().getShowDeleteContentObjectButton();
		}

		return this.showDeleteContentObjectButton;
	}

	/// <summary>
	/// Gets a value indicating whether a slide show of image content objects automatically starts playing when the page loads. This value is retrieved 
	/// from the <see cref="Gallery.isAutoPlaySlideShow()" /> property if specified; if not, it uses a default value of <c>false</c>. This setting 
	/// applies only when the application is showing a single content object.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if a slide show of image content objects will automatically start playing; otherwise, <c>false</c>.
	/// </value>
	public boolean isAutoPlaySlideShow(){
		if (autoPlaySlideShow == null)	{
			autoPlaySlideShow = Utils.getQueryStringParameterBoolean(request, "ss");

			if (autoPlaySlideShow == null)
				autoPlaySlideShow = getGalleryControlSettings().getAutoPlaySlideShow() == null  ?  false : getGalleryControlSettings().getAutoPlaySlideShow();
		}

		return autoPlaySlideShow;
	}

	/// <summary>
	/// Gets the ID for the hidden field that contains the content object ID. This hidden field is updated via javascript
	/// as a user navigates within an album and can be used by the server to determine the current content object the user
	/// is viewing.
	/// </summary>
	/// <value>The ID for the hidden field that contains the content object ID.</value>
	private String getHiddenFieldContentObjectId(){
		 return StringUtils.join("this.ClientID", "_moid");
	}

	/// <summary>
	/// Gets the UI templates used to render various aspects of the page. Returns templates belonging to <see cref="getGalleryId()" />.
	/// Guaranteed to not return null.
	/// </summary>
	/// <value>An instance of <see cref="UiTemplateBoCollection" />.</value>
	public UiTemplateBoCollection getUiTemplates(){
		 if (uiTemplates == null)
			 uiTemplates = new UiTemplateBoCollection(GalleryUtils.getUiTemplates().stream().filter(t -> {
				try {
					return t.GalleryId == getGalleryId();
				} catch (UnsupportedContentObjectTypeException | UnsupportedImageTypeException
						| InvalidContentObjectException | InvalidMDSRoleException | GallerySecurityException
						| IOException | InvalidGalleryException | WebException | InvalidAlbumException | RecordExistsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}).collect(Collectors.toList()));
		 
		 return uiTemplates;
	}

	/// <summary>
	/// Gets the MDS System logo.
	/// </summary>
	/// <value>An instance of <see cref="LiteralControl" />.</value>
	protected String getMdsLogo(){
			/*var tooltip = StringUtils.format(I18nUtils.getString("Footer_Logo_Tooltip, Utils.getMDSSystemVersion());

			return new LiteralControl(StringUtils.format(@"<footer class='mds_addtopmargin5 mds_footer'>
		  <a href='http://www.mdsplus.com' title='{0}'>
		   <img src='{1}' alt='{0}' />
		  </a>
		 </footer>",
			tooltip,
			Page.ClientScript.GetWebResourceUrl(this.GetType().BaseType, "MDS.Web.App_GlobalResources.mds-ftr-logo-170x46.png")));*/
			//return new LiteralControl(StringUtils.format(@"<footer class='mds_addtopmargin5 mds_footer'> </footer>"));
			return StringUtils.EMPTY;
	}

	//#endregion

	//#region Public Events

	/// <summary>
	/// Occurs just before the gallery header and album breadcrumb menu controls are added to the control collection. This event is an
	/// opportunity for inheritors to insert controls of their own at the zero position using the Controls.AddAt(0, myControl) method.
	/// Viewstate is lost if inheritors add controls at any index other than 0, so the way to deal with this is to use this 
	/// event handler to add controls. For example, the Site Settings admin menu is added in the event handler in the <see cref="AdminPage"/> class.
	/// </summary>
	//protected event System.EventHandler BeforeHeaderControlsAdded;

	//#endregion

	//#region Public Methods

	/// <overloads>
	/// Gets the album ID corresponding to the current album.
	/// </overloads>
	/// <summary>
	/// Gets the album ID corresponding to the current album. The value is determined in the following sequence: (1) If 
	/// <see cref="getContentObject" /> returns an object (which will happen when a particular content object has been requested), then 
	/// use the album ID of the content object's parent. (2) When no content object is available, then look for the "aid" query String 
	/// parameter. (3) If not there, or if <see cref="Gallery.AllowUrlOverride" /> has been set to <c>false</c>, look for an album 
	/// ID on the containing <see cref="Gallery" /> control. (4) If we haven't found an album yet, load the top-level album 
	/// for which the current user has view permission. This function verifies the album exists and the current user has permission 
	/// to view it. If the album does not exist, a <see cref="InvalidAlbumException" /> is thrown. If the user does not have permission to
	/// view the album, a <see cref="GallerySecurityException" /> is thrown. Guaranteed to return a valid album ID, except
	/// when the user does not have view permissions to any album and when the top-level album is a virtual album, in which case
	/// it returns <see cref="Int32.MinValue" />.
	/// </summary>
	/// <returns>Returns the album ID corresponding to the current album.</returns>
	/// <exception cref="InvalidAlbumException">Thrown when the requested album does not exist.</exception>
	/// <exception cref="GallerySecurityException">Thrown when the user is requesting an album or content object they don't have 
	/// permission to view.</exception>
	public long getAlbumId() throws UnsupportedContentObjectTypeException, InvalidGalleryException, InvalidMDSRoleException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException{
		if (album != null){
			return album.getId();
		}else{
			return getAlbumId(album);
		}
	}

	/// <summary>
	/// Gets the album ID corresponding to the current album and assigns the album to the <paramref name="album" /> parameter. 
	/// The value is determined in the following sequence: (1) If <see cref="GalleryView.getContentObject"/> returns an 
	/// object (which will happen when a particular content object has been requested), then use the album ID of the 
	/// content object's parent. (2) When no content object is available, then look for the "aid" query String parameter.
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
	public long getAlbumId(AlbumBo album) throws UnsupportedContentObjectTypeException, InvalidGalleryException, InvalidMDSRoleException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException{
		if (this.album != null){
			album = this.album;
			return album.getId();
		}

		long aid;

		// First look for title/caption search text in the query String.
		if (Utils.isQueryStringParameterPresent(request, "title")){
			this.album = ContentObjectUtils.getContentObjectsHavingTitleOrCaption(Utils.getQueryStringParameterStrings(request, "title"), getContentObjectFilter(request), getContentObjectApprovalFilter(request), getGalleryControlSettings().getGalleryId());
			aid = this.album.getId();
		}
		// Then look for search text in the query String.
		else if (Utils.isQueryStringParameterPresent(request, "search")){
			this.album = ContentObjectUtils.getContentObjectsHavingSearchString(Utils.getQueryStringParameterStrings(request, "search"), getContentObjectFilter(request), getContentObjectApprovalFilter(request), getGalleryControlSettings().getGalleryId());
			aid = this.album.getId();
		}
		// Then look for tags in the query String.
		else if (Utils.isQueryStringParameterPresent(request, "tag") || Utils.isQueryStringParameterPresent(request, "people"))
		{
			this.album = ContentObjectUtils.getContentObjectsHavingTags(Utils.getQueryStringParameterStrings(request, "tag"), Utils.getQueryStringParameterStrings(request, "people"), getContentObjectFilter(request), getContentObjectApprovalFilter(request), getGalleryControlSettings().getGalleryId());
			aid = this.album.getId();
		}
		// Then look for a request for the rated objects in the query String.
		else if (Utils.isQueryStringParameterPresent(request, "rating")) // && AppSettings.getInstance().License.LicenseType == LicenseLevel.Enterprise
		{
			this.album = ContentObjectUtils.getRatedContentObjects(Utils.getQueryStringParameterString(request, "rating"), Utils.getQueryStringParameterInt32(request, "top"), getGalleryControlSettings().getGalleryId(), getContentObjectFilter(ContentObjectType.ContentObject, request), getContentObjectApprovalFilter(request));
			aid = this.album.getId();
		}
		// Then look for a request for the latest objects in the query String.
		else if (Utils.isQueryStringParameterPresent(request, "latest")) // && AppSettings.getInstance().License.LicenseType == LicenseLevel.Enterprise
		{
			this.album = ContentObjectUtils.getMostRecentlyAddedContentObjects(Utils.getQueryStringParameterInt32(request, "latest"), getGalleryControlSettings().getGalleryId(), getContentObjectFilter(ContentObjectType.ContentObject, request), getContentObjectApprovalFilter(request));
			aid = this.album.getId();
		}
		// Then look for approval status in the query String.
		else if (Utils.isQueryStringParameterPresent(request, "approval")){
			this.album = ContentObjectUtils.getApprovalContentObjects(getGalleryControlSettings().getGalleryId(), getContentObjectFilter(request), getContentObjectApprovalFilter(request));
			aid = this.album.getId();
		}else{
			// If we have a content object, get it's album ID.
			ContentObjectBo contentObject = getContentObject();

			aid = contentObject != null ? contentObject.getParent().getId() : parseAlbumId();

			if (aid > Long.MIN_VALUE){
				this.album = validateAlbum(aid, this.album);
			}else{
				// Nothing in viewstate, the query String, and no content object is specified. Get the highest album the user can view.
				this.album = getHighestAlbumUserCanView();
				aid = this.album.getId();
			}
		}

		album = this.album;

		return aid;
	}

	/// <overloads>
	/// Get a fully inflated album instance for the requested album.
	/// </overloads>
	/// <summary>
	/// Get a fully inflated, read-only album instance for the requested album. The album can be specified in the following places:  (1) Through 
	/// the <see cref="Gallery.AlbumId" /> property of the Gallery user control (2) From the requested content object by accessing its 
	/// parent object (3) Through the "aid" query String parameter. If this album contains child objects, they are added but not inflated. 
	/// If the album does not exist, a <see cref="InvalidAlbumException" /> is thrown. If the user does not have permission to
	/// view the album, a <see cref="GallerySecurityException" /> is thrown. Guaranteed to never return null.
	/// </summary>
	/// <returns>Returns an AlbumBo object.</returns>
	/// <exception cref="InvalidAlbumException">Thrown when the requested album does not exist.</exception>
	/// <exception cref="GallerySecurityException">Thrown when the user is requesting an album or content object they don't have 
	/// permission to view.</exception>
	public AlbumBo getAlbum() throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException{
		return getAlbum(false);
	}

	/// <summary>
	/// Get a fully inflated album instance for the requested album. Specify <c>true</c> for the <paramref name="isWritable"/>
	/// parameter to get an instance that can be modified. The album can be specified in the following places:  (1) Through
	/// the <see cref="Gallery.AlbumId"/> property of the Gallery user control (2) From the requested content object by accessing its
	/// parent object (3) Through the "aid" query String parameter. If this album contains child objects, they are added but not 
	/// inflated. If the album does not exist, a <see cref="InvalidAlbumException" /> is thrown. If the user does not have permission to
	/// view the album, a <see cref="GallerySecurityException" /> is thrown. Guaranteed to never return null.
	/// </summary>
	/// <param name="isWritable">if set to <c>true</c> return an updateable instance.</param>
	/// <returns>Returns an AlbumBo object.</returns>
	/// <exception cref="InvalidAlbumException">Thrown when the requested album does not exist.</exception>
	/// <exception cref="GallerySecurityException">Thrown when the user is requesting an album or content object they don't have 
	/// permission to view.</exception>
	public AlbumBo getAlbum(boolean isWritable) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException{
		if (isWritable)	{
			return AlbumUtils.loadAlbumInstance(getAlbumId(), true, isWritable);
		}

		if (this.album == null)	{
			long albumId = getAlbumId(); // Getting the album ID will set the album variable.

			if (this.album == null)
				throw new UnsupportedOperationException("Retrieving the album ID should have also assigned an album to the album member variable, but it did not.");
		}

		return this.album;
	}

	//public void SetAlbumId(long albumId)
	//{
	//  validateAlbum(albumId);

	//  ViewState["aid"] = albumId;
	//  this.contentObject = null;
	//  this.album = null;
	//  this.galleryId = Long.MIN_VALUE;
	//}

	/// <summary>
	/// Gets the content object ID corresponding to the current content object, or <see cref="Int32.MinValue" /> if no valid media 
	/// object is available. The value is determined in the following sequence: (1) See if code earlier in the page's life cycle
	/// assigned an ID to the class member variable (this happens during Ajax postbacks). (2) Look for the "moid" query String parameter.
	/// (3) If not there, or if <see cref="Gallery.AllowUrlOverride" /> has been set to <c>false</c>, look at the <see cref="Gallery" />
	/// control to see if we need to get a content object. This function verifies the content object exists and the 
	/// current user has permission to view it. If either is not true, the function returns <see cref="Int32.MinValue"/>.
	/// </summary>
	/// <returns>Returns the content object ID corresponding to the current content object, or <see cref="Int32.MinValue" /> if 
	/// no valid content object is available.</returns>
	public long getContentObjectId() throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException, InvalidAlbumException{
		if (contentObject != null){
			return contentObject.getId(); // We already figured out the content object for this page instance, so just get the ID.
		}

		long moid;

		// See if it has been assigned to the member variable. This happens during Ajax postbacks.
		if (this.contentObjectId != null){
			moid = this.contentObjectId;
		}else{
			// Try to figure it out based on the query String and various <see cref="Gallery" /> control properties.
			this.contentObjectId = parseContentObjectId();
			moid = this.contentObjectId;
		}

		
		if (moid > Long.MIN_VALUE)	{
			Pair<Boolean, ContentObjectBo> result = validateContentObject(moid, contentObject);
			if (!result.getLeft()) {
				// Content object is not valid or user does not have permission to view it. Default to Long.MIN_VALUE.
				moid = Long.MIN_VALUE;
			}else {
				contentObject = result.getRight();
			}
		}
	
		return moid;
	}

	/// <summary>
	/// Get a fully inflated, properly typed content object instance for the requested content object. The content object can be specified 
	/// in the following places:  (1) Through the <see cref="Gallery.ContentObjectId" /> property of the Gallery user control (2) Through 
	/// the "moid" query String parameter. If the requested content object doesn't exist or the user does not have permission to view it, 
	/// a null value is returned. An automatic security check is performed to make sure the user has view permission for the specified 
	/// content object.
	/// </summary>
	/// <returns>Returns an <see cref="ContentObjectBo" /> object that represents the relevant derived content object type 
	/// (e.g. <see cref="Image" />, <see cref="Video" />, etc), or null if no content object is specified.</returns>
	public ContentObjectBo getContentObject() throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException, InvalidAlbumException{
		if (this.contentObject == null)	{
			long contentObjectId = getContentObjectId(); // If a content object has been requested, getting its ID will set the contentObject variable.

			if ((contentObjectId > Long.MIN_VALUE) && this.contentObject == null)
				throw new UnsupportedOperationException("Retrieving the content object ID should have also assigned a content object to the contentObject member variable, but it did not.");
		}

		return this.contentObject;
	}

	/// <summary>
	/// Get an absolute URL to the thumbnail image of the specified gallery object. Either a content object or album may be specified. 
	/// Ex: "http://site.com/gallery/ds/handler/getmedia.ashx?moid=34&amp;dt=1&amp;g=1"
	/// The URL can be used to assign to the src attribute of an image tag (&lt;img src='...' /&gt;).
	/// </summary>
	/// <param name="contentObject">The gallery object for which an URL to its thumbnail image is to be generated.
	/// Either a content object or album may be specified.</param>
	/// <returns>Returns the URL to the thumbnail image of the specified gallery object.</returns>
	public static String getThumbnailUrl(ContentObjectBo contentObject, HttpServletRequest request) throws UnsupportedContentObjectTypeException, UnsupportedEncodingException, WebException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException{
		return getContentObjectUrl(contentObject, DisplayObjectType.Thumbnail, request);
	}

	/// <summary>
	/// Get an absolute URL to the optimized image of the specified gallery object.
	/// Ex: "http://site.com/gallery/ds/handler/getmedia.ashx?moid=34&amp;dt=1&amp;g=1"
	/// The URL can be used to assign to the src attribute of an image tag (&lt;img src='...' /&gt;).
	/// </summary>
	/// <param name="contentObject">The gallery object for which an URL to its optimized image is to be generated.</param>
	/// <returns>Returns the URL to the optimized image of the specified gallery object.</returns>
	public static String getOptimizedUrl(ContentObjectBo contentObject, HttpServletRequest request) throws UnsupportedContentObjectTypeException, UnsupportedEncodingException, WebException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException {
		return getContentObjectUrl(contentObject, DisplayObjectType.Optimized, request);
	}

	/// <summary>
	/// Get an absolute URL to the original image of the specified gallery object.
	/// Ex: "http://site.com/gallery/ds/handler/getmedia.ashx?moid=34&amp;dt=1&amp;g=1"
	/// The URL can be used to assign to the src attribute of an image tag (&lt;img src='...' /&gt;).
	/// </summary>
	/// <param name="contentObject">The gallery object for which an URL to its original image is to be generated.</param>
	/// <returns>Returns the URL to the original image of the specified gallery object.</returns>
	public static String getOriginalUrl(ContentObjectBo contentObject, HttpServletRequest request) throws UnsupportedContentObjectTypeException, UnsupportedEncodingException, WebException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException {
		return getContentObjectUrl(contentObject, DisplayObjectType.Original, request);
	}

	/// <summary>
	/// Get an absolute URL to the thumbnail, optimized, or original content object.
	/// Ex: "http://site.com/gallery/ds/handler/getmedia.ashx?moid=34&amp;dt=1&amp;g=1"
	/// The URL can be used to assign to the src attribute of an image tag (&lt;img src='...' /&gt;).
	/// Not tested: It should be possible to pass an album and request the url to its thumbnail image.
	/// </summary>
	/// <param name="contentObject">The gallery object for which an URL to the specified image is to be generated.</param>
	/// <param name="displayType">A DisplayObjectType enumeration value indicating the version of the
	/// object for which the URL should be generated. Possible values: Thumbnail, Optimized, Original.
	/// An exception is thrown if any other enumeration is passed.</param>
	/// <returns>Returns the URL to the thumbnail, optimized, or original version of the requested content object.</returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="contentObject" /> is null.</exception>
	public static String getContentObjectUrl(ContentObjectBo contentObject, DisplayObjectType displayType, HttpServletRequest request) throws UnsupportedContentObjectTypeException, UnsupportedEncodingException, WebException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException{
		if (contentObject == null){
			throw new ArgumentNullException("contentObject");
		}

		if (contentObject instanceof AlbumBo && (displayType != DisplayObjectType.Thumbnail)){
			throw new ArgumentException(StringUtils.format("It is invalid to request an URL for an album display type '{0}'.", displayType));
		}

		ContentObjectHtmlBuilder moBuilder = new ContentObjectHtmlBuilder(ContentObjectHtmlBuilder.getContentObjectHtmlBuilderOptions(contentObject, displayType, request));

		return moBuilder.getContentObjectUrl();
	}

	/// <summary>
	/// Remove all HTML tags from the specified String and HTML-encodes the result.
	/// </summary>
	/// <param name="textWithHtml">The String containing HTML tags to remove.</param>
	/// <returns>Returns a String with all HTML tags removed, including the brackets.</returns>
	/// <returns>Returns an HTML-encoded String with all HTML tags removed.</returns>
	public String removeHtmlTags(String textWithHtml){
		// Return the text with all HTML removed.
		return Utils.htmlEncode(Utils.removeHtmlTags(textWithHtml));
	}

	/// <overloads>
	/// Throw a <see cref="GallerySecurityException" /> if the current user does not have the permission to perform the requested action.
	/// </overloads>
	/// <summary>
	/// Check to ensure user has permission to perform at least one of the specified security actions against the current album 
	/// (identified in <see cref="getAlbumId()" />). Throw a <see cref="GallerySecurityException" />
	/// if the permission isn't granted to the logged on user. Un-authenticated users (anonymous users) are always considered 
	/// NOT authorized (that is, this method returns false) except when the requested security action is <see cref="SecurityActions.ViewAlbumOrContentObject" /> 
	/// or <see cref="SecurityActions.ViewOriginalContentObject" />, since MDS System is configured by default to allow anonymous viewing access but it does 
	/// not allow anonymous editing of any kind. This method behaves similarly to <see cref="isUserAuthorized(SecurityActions)" /> except that it throws an
	/// exception instead of returning false when the user is not authorized.
	/// </summary>
	/// <param name="securityActions">Represents the permission or permissions being requested. Multiple actions can be specified by using 
	/// a bitwise OR between them (example: <see cref="SecurityActions.AdministerSite" /> | <see cref="SecurityActions.AdministerGallery" />).
	/// If multiple actions are specified, the method is successful if the user has permission for at least one of the actions. If you require 
	/// that all actions be satisfied to be successful, call one of the overloads that accept a <see cref="SecurityActionsOption" /> and 
	/// specify <see cref="SecurityActionsOption.RequireAll" />.</param>
	/// <exception cref="MDS.EventLogs.CustomExceptions.GallerySecurityException">Thrown when the logged on user 
	/// does not belong to a role that authorizes the specified security action, or if an anonymous user is requesting any permission 
	/// other than a viewing-related permission (i.e., <see cref="SecurityActions.ViewAlbumOrContentObject" /> or 
	/// <see cref="SecurityActions.ViewOriginalContentObject" />).</exception>
	public void checkUserSecurity(SecurityActions securityActions) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException	{
		checkUserSecurity(securityActions, SecurityActionsOption.RequireOne);
	}

	/// <summary>
	/// Check to ensure user has permission to perform the specified security actions against the current album (identified in 
	/// <see cref="getAlbumId()" />). Throw a <see cref="GallerySecurityException"/>
	/// if the permission isn't granted to the logged on user. When multiple security actions are passed, use 
	/// <paramref name="secActionsOption" /> to specify whether all of the actions must be satisfied to be successful or only one item
	/// must be satisfied. Un-authenticated users (anonymous users) are always considered NOT authorized (that is, this method 
	/// returns false) except when the requested security action is <see cref="SecurityActions.ViewAlbumOrContentObject"/> or 
	/// <see cref="SecurityActions.ViewOriginalContentObject"/>, since MDS System is configured by default to allow anonymous viewing access 
	/// but it does not allow anonymous editing of any kind. This method behaves similarly to 
	/// <see cref="isUserAuthorized(SecurityActions, SecurityActionsOption)"/> except that 
	/// it throws an exception instead of returning false when the user is not authorized.
	/// </summary>
	/// <param name="securityActions">Represents the permission or permissions being requested. Multiple actions can be specified by using
	/// a bitwise OR between them (example: <see cref="SecurityActions.AdministerSite" /> | <see cref="SecurityActions.AdministerGallery" />). 
	/// If multiple actions are specified, use <paramref name="secActionsOption" /> to specify whether all of the actions must be satisfied 
	/// to be successful or only one item must be satisfied.</param>
	/// <param name="secActionsOption">Specifies whether the user must have permission for all items in <paramref name="securityActions" />
	/// to be successful or just one. This parameter is applicable only when <paramref name="securityActions" /> contains more than one item.</param>
	/// <exception cref="MDS.EventLogs.CustomExceptions.GallerySecurityException">Thrown when the logged on user
	/// does not belong to a role that authorizes the specified security action, or if an anonymous user is requesting any permission
	/// other than a viewing-related permission (i.e., <see cref="SecurityActions.ViewAlbumOrContentObject"/> or
	/// <see cref="SecurityActions.ViewOriginalContentObject"/>).</exception>
	public void checkUserSecurity(SecurityActions securityActions, SecurityActionsOption secActionsOption) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException{
		if (!Utils.isUserAuthorized(securityActions, getMDSRolesForUser(), this.getAlbumId(), this.getGalleryId(), this.getAlbum().getIsPrivate(), secActionsOption, this.getAlbum().getIsVirtualAlbum())){
			if (this.isAnonymousUser())	{
				throw new GallerySecurityException(StringUtils.format("Anonymous user does not have permission '{0}' for album ID {1}.", securityActions.toString(), this.getAlbumId()));
			}else{
				throw new GallerySecurityException(StringUtils.format("User '{0}' does not have permission '{1}' for album ID {2}.", UserUtils.getLoginName(), securityActions.toString(), this.getAlbumId()));
			}
		}
	}

	/// <summary>
	/// Check to ensure user has permission to perform at least one of the specified security actions for the specified <paramref name="album" />. 
	/// Throw a <see cref="GallerySecurityException" /> if the permission isn't granted to the logged on user. Un-authenticated users 
	/// (anonymous users) are always considered NOT authorized (that is, this method returns false) except when the requested security 
	/// action is <see cref="SecurityActions.ViewAlbumOrContentObject" /> or <see cref="SecurityActions.ViewOriginalContentObject" />, since 
	/// MDS System is configured by default to allow anonymous viewing access but it does not allow anonymous editing of any kind. 
	/// This method behaves similarly to <see cref="isUserAuthorized(SecurityActions, AlbumBo)" /> except that it throws an exception 
	/// instead of returning false when the user is not authorized.
	/// </summary>
	/// <param name="securityActions">Represents the permission or permissions being requested. Multiple actions can be specified by using 
	/// a bitwise OR between them (example: <see cref="SecurityActions.AdministerSite" /> | <see cref="SecurityActions.AdministerGallery" />). 
	/// If multiple actions are specified, the method is successful if the user has permission for at least one of the actions. If you require 
	/// that all actions be satisfied to be successful, call one of the overloads that accept a <see cref="SecurityActionsOption" /> and 
	/// specify <see cref="SecurityActionsOption.RequireAll" />.</param>
	/// <param name="album">The album for which the security check is to be applied.</param>
	/// <exception cref="MDS.EventLogs.CustomExceptions.GallerySecurityException">Thrown when the logged on user
	/// does not belong to a role that authorizes the specified security action, or if an anonymous user is requesting any permission
	/// other than a viewing-related permission (i.e., <see cref="SecurityActions.ViewAlbumOrContentObject"/> or
	/// <see cref="SecurityActions.ViewOriginalContentObject"/>).</exception>
	public void checkUserSecurity(SecurityActions securityActions, AlbumBo album) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, GallerySecurityException, InvalidMDSRoleException{
		checkUserSecurity(securityActions, album, SecurityActionsOption.RequireOne);
	}

	/// <summary>
	/// Check to ensure user has permission to perform the specified security actions for the specified <paramref name="album" />. 
	/// Throw a <see cref="GallerySecurityException" /> if the permission isn't granted to the logged on user. When multiple 
	/// security actions are passed, use <paramref name="secActionsOption" /> to specify whether all of the actions must be 
	/// satisfied to be successful or only one item must be satisfied. Un-authenticated users (anonymous users) are always 
	/// considered NOT authorized (that is, this method returns false) except when the requested security action is 
	/// <see cref="SecurityActions.ViewAlbumOrContentObject"/> or <see cref="SecurityActions.ViewOriginalContentObject"/>, since Gallery 
	/// Server is configured by default to allow anonymous viewing access but it does not allow anonymous editing of any kind. 
	/// This method behaves similarly to <see cref="isUserAuthorized(SecurityActions, AlbumBo, SecurityActionsOption)"/> except 
	/// that it throws an exception instead of returning false when the user is not authorized.
	/// </summary>
	/// <param name="securityActions">Represents the permission or permissions being requested. Multiple actions can be specified by using
	/// a bitwise OR between them (example: <see cref="SecurityActions.AdministerSite" /> | <see cref="SecurityActions.AdministerGallery" />). 
	/// If multiple actions are specified, use <paramref name="secActionsOption" /> to specify whether all of the actions must be satisfied 
	/// to be successful or only one item must be satisfied.</param>
	/// <param name="album">The album for which the security check is to be applied.</param>
	/// <param name="secActionsOption">Specifies whether the user must have permission for all items in <paramref name="securityActions" />
	/// to be successful or just one. This parameter is applicable only when <paramref name="securityActions" /> contains more than one item.</param>
	/// <exception cref="MDS.EventLogs.CustomExceptions.GallerySecurityException">Thrown when the logged on user
	/// does not belong to a role that authorizes the specified security action, or if an anonymous user is requesting any permission
	/// other than a viewing-related permission (i.e., <see cref="SecurityActions.ViewAlbumOrContentObject"/> or
	/// <see cref="SecurityActions.ViewOriginalContentObject"/>).</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="album" /> is null.</exception>
	public void checkUserSecurity(SecurityActions securityActions, AlbumBo album, SecurityActionsOption secActionsOption) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, GallerySecurityException, InvalidMDSRoleException{
		if (album == null)
			throw new ArgumentNullException("album");

		if (!Utils.isUserAuthorized(securityActions, getMDSRolesForUser(), album.getId(), album.getGalleryId(), album.getIsPrivate(), secActionsOption, album.getIsVirtualAlbum())){
			if (this.isAnonymousUser()){
				throw new GallerySecurityException(StringUtils.format("Anonymous user does not have permission '{0}' for album ID {1}.", securityActions.toString(), album.getId()));
			}else{
				throw new GallerySecurityException(StringUtils.format("User '{0}' does not have permission '{1}' for album ID {2}.", UserUtils.getLoginName(), securityActions.toString(), album.getId()));
			}
		}
	}

	/// <overloads>
	/// Determine if the current user has permission to perform the requested action.
	/// </overloads>
	/// <summary>
	/// Determine whether user has permission to perform at least one of the specified security actions against the current album 
	/// (identified in <see cref="getAlbumId()" />). Un-authenticated users (anonymous users) are always considered NOT authorized (that 
	/// is, this method returns false) except when the requested security action is <see cref="SecurityActions.ViewAlbumOrContentObject" /> 
	/// or <see cref="SecurityActions.ViewOriginalContentObject" />, since MDS System is configured by default to allow anonymous viewing 
	/// access but it does not allow anonymous editing of any kind.
	/// </summary>
	/// <param name="securityActions">Represents the permission or permissions being requested. Multiple actions can be specified by using 
	/// a bitwise OR between them (example: <see cref="SecurityActions.AdministerSite" /> | <see cref="SecurityActions.AdministerGallery" />). 
	/// If multiple actions are specified, the method is successful if the user has permission for at least one of the actions. If you require 
	/// that all actions be satisfied to be successful, call one of the overloads that accept a <see cref="SecurityActionsOption" /> and 
	/// specify <see cref="SecurityActionsOption.RequireAll" />.</param>
	/// <returns>Returns true when the user is authorized to perform the specified security action; otherwise returns false.</returns>
	public boolean isUserAuthorized(SecurityActions securityActions) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException{
		return isUserAuthorized(securityActions, SecurityActionsOption.RequireOne);
	}

	/// <summary>
	/// Determine whether user has permission to perform the specified security actions against the current album (identified in 
	/// <see cref="getAlbumId()" />). When multiple security actions are passed, use 
	/// <paramref name="secActionsOption" /> to specify whether all of the actions must be satisfied to be successful or only one item
	/// must be satisfied. Un-authenticated users (anonymous users) are always considered NOT authorized (that 
	/// is, this method returns false) except when the requested security action is <see cref="SecurityActions.ViewAlbumOrContentObject" /> 
	/// or <see cref="SecurityActions.ViewOriginalContentObject" />, since MDS System is configured by default to allow anonymous viewing 
	/// access but it does not allow anonymous editing of any kind.
	/// </summary>
	/// <param name="securityActions">Represents the permission or permissions being requested. Multiple actions can be specified by using
	/// a bitwise OR between them (example: <see cref="SecurityActions.AdministerSite" /> | <see cref="SecurityActions.AdministerGallery" />). 
	/// If multiple actions are specified, use <paramref name="secActionsOption" /> to specify whether all of the actions must be satisfied 
	/// to be successful or only one item must be satisfied. This parameter is applicable only when <paramref name="securityActions" /> 
	/// contains more than one item.</param>
	/// <param name="secActionsOption">Specifies whether the user must have permission for all items in <paramref name="securityActions" />
	/// to be successful or just one.</param>
	/// <returns>Returns true when the user is authorized to perform the specified security action; otherwise returns false.</returns>
	public boolean isUserAuthorized(SecurityActions securityActions, SecurityActionsOption secActionsOption) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException{
		return Utils.isUserAuthorized(securityActions, getMDSRolesForUser(), this.getAlbumId(), this.getGalleryId(), this.getAlbum().getIsPrivate(), secActionsOption, this.getAlbum().getIsVirtualAlbum());
	}

	/// <summary>
	/// Determine whether user has permission to perform at least one of the specified security actions. Un-authenticated users (anonymous users) are
	/// always considered NOT authorized (that is, this method returns false) except when the requested security action is
	/// <see cref="SecurityActions.ViewAlbumOrContentObject" /> or <see cref="SecurityActions.ViewOriginalContentObject" />, 
	/// since MDS System is configured by default to allow anonymous viewing access but it does not allow anonymous editing of 
	/// any kind. This method will continue to work correctly if the webmaster configures MDS System to require users to log 
	/// in in order to view objects, since at that point there will be no such thing as un-authenticated users, and the standard 
	/// MDS System role functionality applies.
	/// </summary>
	/// <param name="securityActions">Represents the permission or permissions being requested. Multiple actions can be specified by using 
	/// 	a bitwise OR between them (example: <see cref="SecurityActions.AdministerSite" /> | <see cref="SecurityActions.AdministerGallery" />). 
	/// 	If multiple actions are specified, the method is successful if the user has permission for at least one of the actions.</param>
	/// <param name="albumId">The album ID to which the security action applies.</param>
	/// <param name="galleryId">The ID for the gallery the user is requesting permission in. The <paramref name="albumId" /> must exist 
	/// 	in this gallery.</param>
	/// <returns>
	/// Returns true when the user is authorized to perform the specified security action against the specified album;
	/// otherwise returns false.
	/// </returns>
	/// <exception cref="NotSupportedException">Thrown when <paramref name="securityActions" /> is <see cref="SecurityActions.ViewAlbumOrContentObject" /> 
	/// or <see cref="SecurityActions.ViewOriginalContentObject" /> and the user is anonymous (not logged on).</exception>
	public boolean isUserAuthorized(SecurityActions securityActions, long albumId, long galleryId, boolean isVirtualAlbum) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException{
		if (((securityActions == SecurityActions.ViewAlbumOrContentObject) || (securityActions == SecurityActions.ViewOriginalContentObject))
				&& (!Utils.isAuthenticated()))
			throw new NotSupportedException("Wrong method call: You must call the overload of GalleryView.isUserAuthorized that has the isPrivate parameter when the security action is ViewAlbumOrContentObject or ViewOriginalImage and the user is anonymous (not logged on).");

		return isUserAuthorized(securityActions, albumId, galleryId, false, isVirtualAlbum);
	}

	/// <summary>
	/// Determine whether user has permission to perform at least one of the specified security actions. Un-authenticated users (anonymous users) are
	/// always considered NOT authorized (that is, this method returns false) except when the requested security action is
	/// <see cref="SecurityActions.ViewAlbumOrContentObject" /> or <see cref="SecurityActions.ViewOriginalContentObject" />,
	/// since MDS System is configured by default to allow anonymous viewing access but it does not allow anonymous editing of
	/// any kind. This method will continue to work correctly if the webmaster configures MDS System to require users to log
	/// in in order to view objects, since at that point there will be no such thing as un-authenticated users, and the standard
	/// MDS System role functionality applies.
	/// </summary>
	/// <param name="securityActions">Represents the permission or permissions being requested. Multiple actions can be specified by using
	/// a bitwise OR between them (example: <see cref="SecurityActions.AdministerSite" /> | <see cref="SecurityActions.AdministerGallery" />).
	/// If multiple actions are specified, the method is successful if the user has permission for at least one of the actions.</param>
	/// <param name="albumId">The album ID to which the security action applies.</param>
	/// <param name="galleryId">The ID for the gallery the user is requesting permission in. The <paramref name="albumId" /> must exist
	/// in this gallery.</param>
	/// <param name="isPrivate">Indicates whether the specified album is private (hidden from anonymous users). The parameter
	/// is ignored for logged on users.</param>
	/// <param name="isVirtualAlbum">if set to <c>true</c> the album is virtual album.</param>
	/// <returns>
	/// Returns true when the user is authorized to perform the specified security action against the specified album;
	/// otherwise returns false.
	/// </returns>
	public boolean isUserAuthorized(SecurityActions securityActions, long albumId, long galleryId, boolean isPrivate, boolean isVirtualAlbum) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException{
		return Utils.isUserAuthorized(securityActions, getMDSRolesForUser(), albumId, galleryId, isPrivate, isVirtualAlbum);
	}

	/// <summary>
	/// Determine whether user has permission to perform at least one of the specified security actions against the specified <paramref name="album" />. 
	/// Un-authenticated users (anonymous users) are always considered NOT authorized (that is, this method returns false) except 
	/// when the requested security action is <see cref="SecurityActions.ViewAlbumOrContentObject" /> or 
	/// <see cref="SecurityActions.ViewOriginalContentObject" />, since MDS System is configured by default to allow anonymous viewing access 
	/// but it does not allow anonymous editing of any kind.
	/// </summary>
	/// <param name="securityActions">Represents the permission or permissions being requested. Multiple actions can be specified by using 
	/// a bitwise OR between them (example: <see cref="SecurityActions.AdministerSite" /> | <see cref="SecurityActions.AdministerGallery" />). 
	/// If multiple actions are specified, the method is successful if the user has permission for at least one of the actions. If you require 
	/// that all actions be satisfied to be successful, call one of the overloads that accept a <see cref="SecurityActionsOption" /> and 
	/// specify <see cref="SecurityActionsOption.RequireAll" />.</param>
	/// <param name="album">The album for which the security check is to be applied.</param>
	/// <returns>Returns true when the user is authorized to perform the specified security action; otherwise returns false.</returns>
	public boolean isUserAuthorized(SecurityActions securityActions, AlbumBo album) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException	{
		return isUserAuthorized(securityActions, album, SecurityActionsOption.RequireOne);
	}

	/// <summary>
	/// Determine whether user has permission to perform the specified security action against the specified album. If no album 
	/// is specified, then the current album (as returned by getAlbum()) is used. Un-authenticated users (anonymous users) are 
	/// always considered NOT authorized (that is, this method returns false) except when the requested security action is 
	/// ViewAlbumOrContentObject or ViewOriginalImage, since MDS System is configured by default to allow anonymous viewing access
	/// but it does not allow anonymous editing of any kind.
	/// </summary>
	/// <param name="securityActions">Represents the permission or permissions being requested. Multiple actions can be specified by using
	/// a bitwise OR between them (example: <see cref="SecurityActions.AdministerSite" /> | <see cref="SecurityActions.AdministerGallery" />). 
	/// If multiple actions are specified, use <paramref name="secActionsOption" /> to specify whether all of the actions must be satisfied 
	/// to be successful or only one item must be satisfied.</param>
	/// <param name="album">The album for which the security check is to be applied.</param>
	/// <param name="secActionsOption">Specifies whether the user must have permission for all items in <paramref name="securityActions" />
	/// to be successful or just one. This parameter is applicable only when <paramref name="securityActions" /> contains more than one item.</param>
	/// <returns>Returns true when the user is authorized to perform the specified security action; otherwise returns false.</returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="album" /> is null.</exception>
	public boolean isUserAuthorized(SecurityActions securityActions, AlbumBo album, SecurityActionsOption secActionsOption) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException{
		if (album == null)
			throw new ArgumentNullException("album");

		return Utils.isUserAuthorized(securityActions, getMDSRolesForUser(), album.getId(), album.getGalleryId(), album.getIsPrivate(), secActionsOption, album.getIsVirtualAlbum());
	}

	/// <summary>
	/// Gets MDS System roles representing the roles for the currently logged-on user and belonging to the current gallery. 
	/// Returns an empty collection if no user is logged in or the user is logged in but not assigned to any roles relevant 
	/// to the current gallery (Count = 0).
	/// </summary>
	/// <returns>Returns a collection of MDS System roles representing the roles for the currently logged-on user. 
	/// Returns an empty collection if no user is logged in or the user is logged in but not assigned to any roles relevant 
	/// to the current gallery (Count = 0).</returns>
	public MDSRoleCollection getMDSRolesForUser() throws InvalidMDSRoleException{
		if (this.roles == null)	{
			this.roles = RoleUtils.getMDSRolesForUser();
		}

		return this.roles;
	}

	/// <overloads>
	/// Redirect the user to the previous page he or she was on, optionally appending a query String name/value.
	/// </overloads>
	/// <summary>
	/// Redirect the user to the previous page he or she was on. The previous page is retrieved from a session variable that was stored during 
	/// the Page_Init event. If the original query String contains a "msg" parameter, it is removed so that the message 
	/// is not shown again to the user. If no previous page URL is available - perhaps because the user navigated directly to
	/// the page or has just logged in - the user is redirected to the application root.
	/// </summary>
	public void redirectToPreviousPage() throws ServletException, IOException{
		redirectToPreviousPage(StringUtils.EMPTY, StringUtils.EMPTY);
	}

	/// <summary>
	/// Redirect the user to the previous page he or she was on. If a query String name/pair value is specified, append that 
	/// to the URL.
	/// </summary>
	/// <param name="queryStringName">The query String name.</param>
	/// <param name="queryStringValue">The query String value.</param>
	public void redirectToPreviousPage(String queryStringName, String queryStringValue) throws ServletException, IOException	{
		//#region Validation

		if (!StringUtils.isEmpty(queryStringName) && StringUtils.isEmpty(queryStringValue))
			throw new ArgumentException(StringUtils.format("The queryStringValue parameter is required when the queryStringName parameter is specified. (queryStringName='{0}', queryStringValue='{1}')", queryStringName, queryStringValue));

		if (!StringUtils.isEmpty(queryStringValue) && StringUtils.isEmpty(queryStringName))
			throw new ArgumentException(StringUtils.format("The queryStringName parameter is required when the queryStringValue parameter is specified. (queryStringName='{0}', queryStringValue='{1}')", queryStringName, queryStringValue));

		//#endregion

		String url = this.getPreviousUrl();

		if (StringUtils.isEmpty(url))
			url = Utils.getCurrentPageUrl(request); // No previous url is available. Default to the current page.

		if (!StringUtils.isEmpty(queryStringName))
			url = Utils.addQueryStringParameter(url, StringUtils.join(queryStringName, "=", queryStringValue));

		this.setPreviousUri(null);

		//Page.Response.Redirect(url, true);
		request.getRequestDispatcher(url).forward(request, response);
	}

	/// <overloads>Redirects to album view page of the current album.</overloads>
	/// <summary>
	/// Redirects to album view page of the current album.
	/// </summary>
	public void redirectToAlbumViewPage() throws UnsupportedContentObjectTypeException, ServletException, IOException, InvalidGalleryException, InvalidMDSRoleException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, WebException, GallerySecurityException{
		Utils.redirect(request, response, ResourceId.album, "aid={0}", getAlbumId());
	}

	/// <summary>
	/// Redirects to album view page of the current album and with the specified <paramref name="args"/> appended as query String 
	/// parameters. Example: If the current page is /dev/ds/gallery.aspx, the user is viewing album 218, <paramref name="format"/> 
	/// is "msg={0}", and <paramref name="args"/> is "23", this function redirects to /dev/ds/gallery.aspx?g=album&amp;aid=218&amp;msg=23.
	/// </summary>
	/// <param name="format">A format String whose placeholders are replaced by values in <paramref name="args"/>. Do not use a '?'
	/// or '&amp;' at the beginning of the format String. Example: "msg={0}".</param>
	/// <param name="args">The values to be inserted into the <paramref name="format"/> String.</param>
	public void redirectToAlbumViewPage(String format, Object... args) throws UnsupportedContentObjectTypeException, ServletException, IOException, InvalidGalleryException, InvalidMDSRoleException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, WebException, GallerySecurityException{
		if (format == null)
			format = StringUtils.EMPTY;

		if (format.startsWith("?"))
			format = StringUtils.removeStart(format, 1); // Remove leading '?' if present

		String queryString = StringUtils.format(format, args);
		if (!queryString.startsWith("&"))
			queryString = StringUtils.join("&", queryString); // Append leading '&' if not present

		Utils.redirect(request, response, ResourceId.album, StringUtils.join("aid={0}", queryString), getAlbumId());
	}

	/// <summary>
	/// Recursively iterate through the children of the specified containing control, searching for a child control with
	/// the specified server ID. If the control is found, return it; otherwise return null. This method is useful for finding
	/// child controls of composite controls like GridView.
	/// </summary>
	/// <param name="containingControl">The containing control whose child controls should be searched.</param>
	/// <param name="id">The server ID of the child control to search for.</param>
	/// <returns>Returns a Control matching the specified server id, or null if no matching control is found.</returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="containingControl" /> is null.</exception>
	/// <exception cref="ArgumentException">Thrown when <paramref name="id" /> is null or an empty String.</exception>
	/*public Control FindControlRecursive(Control containingControl, String id)
	{
		if (containingControl == null)
			throw new ArgumentNullException("containingControl");

		if (StringUtils.isEmpty(id))
			throw new ArgumentException("The parameter 'id' is null or empty.");

		foreach (Control ctrl in containingControl.Controls)
		{
			if (ctrl.ID == id)
				return ctrl;

			if (ctrl.HasControls())
			{
				Control foundCtrl = FindControlRecursive(ctrl, id);
				if (foundCtrl != null)
					return foundCtrl;
			}
		}
		return null;
	}*/

	/// <summary>
	/// Record the error and optionally notify an administrator via e-mail.
	/// </summary>
	/// <param name="ex">The exception to record.</param>
	/// <returns>Returns an integer that uniquely identifies this application event (<see cref="IEventLog.EventId"/>).</returns>
	/*public int LogError(Exception ex)
	{
		return AppEventLogController.LogError(ex, this.getGalleryId()).EventId;
	}*/

	/// <summary>
	/// Gets a collection of users the current user has permission to view. Users who have administer site permission can view all users.
	/// Users with administer gallery permission can only view users in galleries they have gallery admin permission in. Note that
	/// a user may be able to view a user but not update it. This can happen when the user belongs to roles that are associated with
	/// galleries the current user is not an admin for. The users may be returned from a cache. Guaranteed to not return null.
	/// </summary>
	/// <returns>Returns an <see cref="UserAccountCollection" /> containing a list of roles the user has permission to view.</returns>
	public UserAccountCollection getUsersCurrentUserCanView() throws InvalidMDSRoleException, UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException	{
		return UserUtils.getUsersCurrentUserCanView(isUserCanAdministerSite(), isUserCanAdministerGallery());
	}

	/// <summary>
	/// Gets the list of roles the user has permission to view. Users who have administer site permission can view all roles.
	/// Users with administer gallery permission can only view roles they have been associated with or roles that aren't 
	/// associated with *any* gallery.
	/// </summary>
	/// <returns>Returns an <see cref="MDSRoleCollection" /> containing a list of roles the user has permission to view.</returns>
	public MDSRoleCollection getRolesCurrentUserCanView() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidMDSRoleException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException	{
		return RoleUtils.getRolesCurrentUserCanView(isUserCanAdministerSite(), isUserCanAdministerGallery());
	}

	/// <summary>
	/// Gets the HTML to display a nicely formatted thumbnail image of the specified <paramref name="contentObject" />, including a 
	/// border, shadows and (possibly) rounded corners. This function is the same as calling the overloaded version with 
	/// includeHyperlinkToObject and allowAlbumTextWrapping parameters both set to <c>false</c>.
	/// </summary>
	/// <param name="contentObject">The gallery object to be used as the source for the thumbnail image.</param>
	/// <returns>Returns HTML that displays a nicely formatted thumbnail image of the specified <paramref name="contentObject" /></returns>
	public static String getThumbnailHtml(ContentObjectBo contentObject, HttpServletRequest request) throws Exception{
		ContentObjectHtmlBuilder moBuilder = new ContentObjectHtmlBuilder(ContentObjectHtmlBuilder.getContentObjectHtmlBuilderOptions(contentObject, DisplayObjectType.Thumbnail, request));

		return moBuilder.getThumbnailHtml();
	}

	/// <summary>
	/// Gets the gallery data for the current content object, if one exists, or the current album.
	/// <see cref="Entity.MDSData.Settings" /> is assigned, unlike when this object is retrieved
	/// through the web service (since the control-specific settings can't be determined in that case).
	/// </summary>
	/// <returns>Returns an instance of <see cref="Entity.MDSData" />.</returns>
	public CMData getClientMdsData() throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException, InvalidAlbumException, Exception{
		if (mdsData == null) {
			mdsData = getContentObjectId() > Long.MIN_VALUE ?
				GalleryUtils.getCMDataForContentObject(getContentObject(), getAlbum(), new CMDataLoadOptions(false, true, getContentObjectFilter(request), getContentObjectApprovalFilter(request)), request) :
				GalleryUtils.getCMDataForAlbum(getAlbum(), new CMDataLoadOptions(true, false, getContentObjectFilter(request), getContentObjectApprovalFilter(request)), request);
	
				mdsData.setSettings(getSettingsEntity());
		}

		return mdsData;
	}

	//#endregion

	//#region Protected Methods

	//#endregion

	//#region Private Static Methods


	/// <summary>
	/// Updates the query String parameter in the <paramref name="uri"/> with the specified value. If the 
	/// <paramref name="queryStringName"/> is not present, it is added. The modified URI is returned. The <paramref name="uri"/>
	/// is not modified.
	/// </summary>
	/// <param name="uri">The URI that is to receive the updated or added query String <paramref name="queryStringName">name</paramref>
	/// and <paramref name="queryStringValue">value</paramref>. This object is not modified; rather, a new URI is created
	/// and returned.</param>
	/// <param name="queryStringName">Name of the query String to include in the URI.</param>
	/// <param name="queryStringValue">The query String value to include in the URI.</param>
	/// <returns>Returns the uri with the specified query String name and value updated or added.</returns>
	private static URI updateUriQueryString(HttpServletRequest request, URI uri, String queryStringName, String queryStringValue) throws URISyntaxException{
		URI updatedUri = null;
		String newQueryString = uri.getQuery();

		if (Utils.isQueryStringParameterPresent(uri, queryStringName)){
			if (Utils.getQueryStringParameterString(request, uri, queryStringName) != queryStringValue){
				// The URI has the query String parm and it is different than the value. Update the URI.
				newQueryString = Utils.removeQueryStringParameter(newQueryString, queryStringName);
				newQueryString = Utils.addQueryStringParameter(newQueryString, StringUtils.format("{0}={1}", queryStringName, queryStringValue));

				URIBuilder uriBuilder = new URIBuilder(uri);
				uriBuilder.setQuery(StringUtils.stripStart(newQueryString, "?"));
				updatedUri = uriBuilder.build();
			}
			//else {} // Query String is present and already has the requested value. Do nothing.
		}else{
			// Query String parm not present. Add it.
			newQueryString = Utils.addQueryStringParameter(newQueryString, StringUtils.format("{0}={1}", queryStringName, queryStringValue));

			URIBuilder uriBuilder = new URIBuilder(uri);
			uriBuilder.setQuery(StringUtils.stripStart(newQueryString,  "?"));
			updatedUri = uriBuilder.build();
		}
		
		return updatedUri == null ? uri : updatedUri;
	}

	private List<ActionResult> getUploadErrors(List<ActionResult> uploadResults)	{
		if (uploadResults == null)
			return null;

		return (uploadResults.stream().filter(m -> m.Status == ActionResultStatus.Error.toString())).collect(Collectors.toList());
	}

	private static String convertListToHtmlBullets(List<ActionResult> skippedFiles){
		String html = "<ul class='mds_addleftmargin5'>";
		for (ActionResult kvp : skippedFiles){
			html += StringUtils.format("<li>{0}: {1}</li>", kvp.Title, kvp.Message);
		}
		html += "</ul>";

		return html;
	}

	/// <summary>
	/// Verifies the content object exists and the user has permission to view it. If valid, the content object is assigned to the
	/// contentObject member variable and the function returns <c>true</c>; otherwise returns <c>false</c>.
	/// </summary>
	/// <param name="contentObjectId">The content object ID to validate. Throws a <see cref="ArgumentOutOfRangeException"/>
	/// if the value is <see cref="Int32.MinValue"/>.</param>
	/// <param name="contentObject">The content object.</param>
	/// <returns></returns>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when <paramref name="contentObjectId"/> is <see cref="Int32.MinValue"/>.</exception>
	private static Pair<Boolean, ContentObjectBo> validateContentObject(long contentObjectId, ContentObjectBo contentObject) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException{
		if (contentObjectId == Long.MIN_VALUE)
			throw new ArgumentOutOfRangeException("contentObjectId", StringUtils.format("A valid content object ID must be passed to this function. Instead, the value was {0}.", contentObjectId));

		contentObject = null;
		boolean isValid = false;
		ContentObjectBo tempContentObject = null;
		try{
			tempContentObject = CMUtils.loadContentObjectInstance(contentObjectId);
		}catch (ArgumentException ae) { }
		catch (InvalidContentObjectException ce) { }

		if (tempContentObject != null){
			// Perform a basic security check to make sure user can view content object. Another, more detailed security check is performed by child
			// user controls if necessary. (e.g. Perhaps the user is requesting the high-res version but he does not have the ViewOriginalImage 
			// permission. The view content object user control will verify this.)
			if (UserUtils.isUserAuthorized(SecurityActions.ViewAlbumOrContentObject.value() | SecurityActions.ViewOriginalContentObject.value(), RoleUtils.getMDSRolesForUser(), tempContentObject.getParent().getId(), tempContentObject.getGalleryId(), tempContentObject.getIsPrivate(), SecurityActionsOption.RequireOne, ((AlbumBo)tempContentObject.getParent()).getIsVirtualAlbum())){
				// User is authorized. Assign to page-level variable.
				contentObject = tempContentObject;

				isValid = true;
			}
		}

		return new ImmutablePair<Boolean, ContentObjectBo>(isValid, contentObject);
	}

	//#endregion

	//#region Private Methods

	public void initializeView() throws Exception{
		initializeGallerySettings();

		// Redirect to the logon page if the user has to log in. (Note that the InitializegetGallerySettings()() function
		// may also check requiresLogin() and do a redirect when a GallerySecurityException is thrown.)
		/*if (requiresLogin()){
			Utils.redirect(request, response, ResourceId.login, true, "ReturnUrl={0}", Utils.urlEncode(Utils.getCurrentPageUrl(request, true)));
		}*/
		getClientMdsData();
		getAlbumTreeData();

		this.storeCurrentPageUri();

		/*if (Utils.isAuthenticated() && getGallerySettings().getEnableUserAlbum()){
			UserUtils.validateUserAlbum(UserUtils.getLoginName(), getGalleryId());
		}*/

		/*if (IsPostBack)
		{
			// Postback (such as logon/logoff events): Since the user may have been navigating several content objects in this 
			// album through AJAX calls, we need to check a hidden field to discover the current content object. Assign this 
			// object's ID to our base user control. The base control is smart enough to retrieve the new content object if it 
			// is different than what was previously set.
			object formFieldMoid = Request.Form[getHiddenFieldContentObjectId()];
			int moid;
			if ((formFieldMoid != null) && (Int32.TryParse(formFieldMoid.ToString(), out moid)))
			{
				this.setContentObjectId(moid);
			}
		}

		if (!IsPostBack)
		{
			RegisterHiddenFields();
		}

		AddJavaScriptAndCss();

		// Add user controls to the page, such as the header and album breadcrumb menu.
		this.AddUserControls();

		RunAutoSynchIfNeeded();*/
	}

	/// <summary>
	/// Assign reference to gallery settings for the current gallery. If the user does not have permission to the requested
	/// album or content object, the user is automatically redirected as needed (e.g. the login page or the highest level album
	/// the user has permission to view). One exception to this is if a particular album is assigned to the control and the
	/// user does not have permission to view it, an empty album is used and a relevant message is assigned to the 
	/// <see cref="ClientMessage" /> property.
	/// </summary>
	public void initializeGallerySettings() throws UnsupportedContentObjectTypeException, InvalidGalleryException, IOException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, WebException, ServletException, RecordExistsException{
		try	{
			loadGallerySettings();
		}catch (InvalidContentObjectException ce) { }
		catch (InvalidAlbumException ex)
		{
			checkForInvalidAlbumIdInGalleryControlSetting(ex.getAlbumId());

			Utils.redirect(response, Utils.addQueryStringParameter(Utils.getCurrentPageUrl(request), "msg=" + MessageType.AlbumDoesNotExist));
		}catch (GallerySecurityException ge){
			// Redirect to the logon page if the user has to log in.
			if ((this.resourceId == ResourceId.login) || (this.resourceId == ResourceId.createaccount) || (this.resourceId == ResourceId.recoverpassword)){
				// User is on one of the authentication pages, so just create an empty album. We'll get here when anon.
				// browsing is disabled and a specific album is specified on the GCS page.
				this.album = createEmptyAlbum(AlbumUtils.loadAlbumInstance(this.getGalleryControlSettings().getAlbumId(), false).getGalleryId(), request);
			}else if (requiresLogin()){
				Utils.redirect(request, response, ResourceId.login, true, "ReturnUrl={0}", Utils.urlEncode(Utils.getCurrentPageUrl(request, true)));
			}else{
				if (this.getGalleryControlSettings().getAlbumId() > Long.MIN_VALUE)	{
					// User does not have access to the album specified as the default gallery object.
					this.album = createEmptyAlbum(AlbumUtils.loadAlbumInstance(this.getGalleryControlSettings().getAlbumId(), false).getGalleryId(), request);

					clientMessage = getMessageOptions(MessageType.AlbumNotAuthorizedForUser);
				}else{
					Utils.redirect(request, response, ResourceId.album);
				}
			}
		}
	}

	/// <summary>
	/// Assign reference to gallery settings for the current gallery.
	/// </summary>
	/// <exception cref="InvalidAlbumException">Thrown when an album is requested but does not exist.</exception>
	/// <exception cref="GallerySecurityException">Thrown when the user is requesting an album or content object they don't have 
	/// permission to view.</exception>
	/// <remarks>This must be called from <see cref="GalleryView_Init" />! It can't go in the <see cref="GalleryView" /> constructor 
	/// because that is too early to access the getGalleryId() property, and it can't go in the getGallerySettings() property getter because 
	/// that is too late if a gallery has to be dynamically created.)</remarks>
	private void loadGallerySettings() throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, IOException, WebException, InvalidAlbumException, GallerySecurityException, RecordExistsException{
		try{
			this.gallerySetting = CMUtils.loadGallerySetting(getGalleryId());
		}catch (GallerySecurityException ge){
			// The user is requesting an album or content object they don't have permission to view. Manually load the gallery settings
			// from the query String parameter and assign the gallery ID property so that they are available in the requiresLogin() 
			// function later in GalleryView_Init(). That code will take care of redirecting the user to the login page.
			long albumId = Utils.getQueryStringParameterInt64(request, "aid");
			long contentObjectId = Utils.getQueryStringParameterInt64(request, "moid");

			if ((albumId == Long.MIN_VALUE)){
				albumId = this.getGalleryControlSettings().getAlbumId();
			}

			if (contentObjectId == Long.MIN_VALUE){
				contentObjectId = this.getGalleryControlSettings().getContentObjectId();
			}

			if (albumId > Long.MIN_VALUE){
				try	{
					galleryId = AlbumUtils.loadAlbumInstance(albumId, false).getGalleryId();
					this.gallerySetting = CMUtils.loadGallerySetting(galleryId);
				}catch (InvalidAlbumException ae) { }
			}else if (contentObjectId > Long.MIN_VALUE){
				try{
					galleryId = CMUtils.loadContentObjectInstance(contentObjectId).getParent().getGalleryId();
					this.gallerySetting = CMUtils.loadGallerySetting(galleryId);
				}catch (InvalidContentObjectException ce ) { }
				catch (InvalidAlbumException ae) { }
			}

			throw ge; // Re-throw GallerySecurityException
		}
	}

	/// <summary>
	/// Determines whether the current user must be logged in to access the requested page.
	/// </summary>
	/// <returns>Returns <c>true</c> if the user must be logged in to access the requested page; otherwise
	/// returns <c>false</c>.</returns>
	private boolean requiresLogin()	{
		if ((this.resourceId == ResourceId.login) || (this.resourceId == ResourceId.createaccount) || (this.resourceId == ResourceId.recoverpassword))
			return false; // The login, create account, & recover password pages never require one to be logged in

		if (!this.isAnonymousUser())
			return false; // Already logged in

		if (!isAllowAnonymousBrowsing())
			return true; // Not logged in, anonymous browsing disabled

		// Some pages allow anonymous browsing. If it is one of those, return false; otherwise return true;
		switch (this.resourceId){
			//case ResourceId.createaccount:
			//case ResourceId.login:
			//case ResourceId.recoverpassword: // These 3 are redundent because we already handle them above
			case album:
			case albumtreeview:
			case contentobject:
			case cm_downloadobjects:
				return false;
			default:
				return true;
		}
	}

	/// <summary>
	/// Stores or updates the URI of the current album or content object page so that we can return to it later, if desired. This
	/// method store the current URI only for fresh page loads (no postbacks or callbacks) and when the current page
	/// is displaying an album view or content object. It also updates the URI with the current content object ID when the 
	/// current page is a task page. No action is taken for other pages, such as admin pages, since we do not want to return to 
	/// them. This method assigns or updates the URI in the <see cref="getPreviousUri()"/> property. After assigning this property, 
	/// one can use <see cref="redirectToPreviousPage()"/> to navigate to the page. If session state is disabled, this method 
	/// does nothing.
	/// </summary>		
	private void storeCurrentPageUri() throws URISyntaxException{
		if (!Utils.isPostBack(request)) {
			if ((this.resourceId == ResourceId.album) || (this.resourceId == ResourceId.contentobject))
				this.setPreviousUri(Utils.getCurrentPageURI(request));
			else if (StringUtils.startsWithIgnoreCase(this.resourceId.toString(), "task")){
				// If we are on a task page and the QS contains a content object ID different than the one stored in 
				// <see cref="getPreviousUri()" />, the update the previous URI to contain the new ID. This code assumes that
				// the MO we want to go back to upon completion of the task is the same one in the QS.
				long prevMoid = Utils.getQueryStringParameterInt64(request, this.getPreviousUri(), "moid");
				long currentMoid = Utils.getQueryStringParameterInt64(request, "moid");
				if ((prevMoid > Long.MIN_VALUE) && (currentMoid > Long.MIN_VALUE) && (prevMoid != currentMoid))	{
					this.setPreviousUri(Utils.addQueryStringParameter(this.getPreviousUri(), StringUtils.join("moid=", Long.toString(currentMoid))));
				}
			}
		}
	}

	/// <summary>
	/// Set the public properties on this class related to user permissions. This method is called as needed from
	/// within the property getters.
	/// </summary>
	private void evaluateUserPermissions() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException{
		boolean isPhysicalAlbum = !this.getAlbum().getIsVirtualAlbum();

		// We need include isPhysicalAlbum in the expressions below because the isUserAuthorized function uses
		// the album ID of the current album to evaluate the user's ability to perform the action. In the case
		// of a virtual album, the album ID is Long.MIN_VALUE and the method is therefore not able to evaluate the permission.
		this.userCanViewAlbumOrContentObject = isPhysicalAlbum && this.isUserAuthorized(SecurityActions.ViewAlbumOrContentObject);
		this.userCanViewOriginal = isPhysicalAlbum && this.isUserAuthorized(SecurityActions.ViewOriginalContentObject);
		this.userCanCreateAlbum = isPhysicalAlbum && this.isUserAuthorized(SecurityActions.AddChildAlbum);
		this.userCanEditAlbum = isPhysicalAlbum && this.isUserAuthorized(SecurityActions.EditAlbum);
		this.userCanAddContentObject = isPhysicalAlbum && this.isUserAuthorized(SecurityActions.AddContentObject);
		this.userCanEditContentObject = isPhysicalAlbum && this.isUserAuthorized(SecurityActions.EditContentObject);
		this.userCanDeleteCurrentAlbum = isPhysicalAlbum && this.isUserAuthorized(SecurityActions.DeleteAlbum);
		this.userCanDeleteChildAlbum = isPhysicalAlbum && this.isUserAuthorized(SecurityActions.DeleteChildAlbum);
		this.userCanDeleteContentObject = isPhysicalAlbum && this.isUserAuthorized(SecurityActions.DeleteContentObject);
		this.userCanApprovalContentObject = isPhysicalAlbum && this.isUserAuthorized(SecurityActions.ApproveContentObject);
		this.userCanSynchronize = isPhysicalAlbum && this.isUserAuthorized(SecurityActions.Synchronize);
		this.userDoesNotGetWatermark = isPhysicalAlbum && this.isUserAuthorized(SecurityActions.HideWatermark);

		this.userCanAddAdministerSite = this.isUserAuthorized(SecurityActions.AdministerSite);
		this.userCanAdministerGallery = this.isUserAuthorized(SecurityActions.AdministerGallery);

		if ((this.userCanAddAdministerSite == null || this.userCanAddAdministerSite)){ // || (this.userCanAdministerGallery == null || this.userCanAdministerGallery)
			this.userCanAddContentObjectToAtLeastOneAlbum = true;
			this.userCanAddAlbumToAtLeastOneAlbum = true;
		}else{
			Pair<Boolean, Boolean> userAddPerms = SecurityGuard.getUserAddObjectPermissions(getMDSRolesForUser(), getGalleryId());

			this.userCanAddAlbumToAtLeastOneAlbum = userAddPerms.getLeft();
			this.userCanAddContentObjectToAtLeastOneAlbum = userAddPerms.getRight();
		}
	}

	/*private void RegisterHiddenFields()
	{
		if (getContentObjectId() > Long.MIN_VALUE)
			this.Page.ClientScript.RegisterHiddenField(getHiddenFieldContentObjectId(), getContentObjectId().ToString(CultureInfo.InvariantCulture));

		//if (getAlbumId() > Long.MIN_VALUE)
		//  ScriptManager.RegisterHiddenField(this, "aid", getAlbumId().ToString(CultureInfo.InvariantCulture));
	}

	private void AddUserControls()
	{
		// If any inheritors subscribed to the event, fire it.
		if (BeforeHeaderControlsAdded != null)
		{
			BeforeHeaderControlsAdded(this, new EventArgs());
		}

		//AddAlbumTreeView();

		if (ResourceId != ResourceId.login)
		{
			if (isUserCanAdministerSite() || isUserCanAdministerGallery() || this.isShowActionMenu() || this.isShowAlbumBreadCrumb())
			{
				this.AddAlbumMenu();
			}
		}

		if (this.isShowHeader())
		{
			this.AddGalleryHeader();
		}
	}*/

	///// <summary>
	///// Write out the MDS System logo to the <paramref name="writer"/>.
	///// </summary>
	///// <param name="writer">The writer.</param>
	//private void AddMdsLogo(HtmlTextWriter writer)
	//{
	//	if (this.getGalleryControlSettings().ViewMode == ViewMode.TreeView)
	//	{
	//		return;
	//	}

	//	// This function writes out HTML like this:
	//	// <div class="mds_addtopmargin5 mds_footer">
	//	//  <a href="http://www.mdsplus.com" title="Powered by MDS System v2.1.3222">
	//	//   <img src="/images/mds_ftr_logo_170x46.png" alt="Powered by MDS System v2.1.3222" style="width:170px;height:46px;" />
	//	//  </a>
	//	// </div>
	//	String tooltip = StringUtils.format(I18nUtils.getString("Footer_Logo_Tooltip, Utils.getMDSSystemVersion());
	//	//String url = Page.ClientScript.GetWebResourceUrl(typeof(footer), "MDS.Web.ds.images.mds_ftr_logo_170x46.png");

	//	// Create <div> tag that wraps the <a> and <img> tags.<div id="gs_footer">
	//	writer.AddAttribute(HtmlTextWriterAttribute.Class, "mds_addtopmargin5 mds_footer");
	//	writer.RenderBeginTag(HtmlTextWriterTag.Div);

	//	// Create <a> tag that wraps <img> tag.
	//	writer.AddAttribute(HtmlTextWriterAttribute.Title, tooltip);
	//	writer.AddAttribute(HtmlTextWriterAttribute.Href, "http://www.mdsplus.com");
	//	writer.RenderBeginTag(HtmlTextWriterTag.A);

	//	// Create <img> tag.
	//	writer.AddStyleAttribute(HtmlTextWriterStyle.Width, "170px");
	//	writer.AddStyleAttribute(HtmlTextWriterStyle.Height, "46px");
	//	writer.AddStyleAttribute(HtmlTextWriterStyle.VerticalAlign, "middle");
	//	writer.AddAttribute(HtmlTextWriterAttribute.Src, Page.ClientScript.GetWebResourceUrl(this.GetType().BaseType, "MDS.Web.App_GlobalResources.mds-ftr-logo-170x46.png"));
	//	writer.AddAttribute(HtmlTextWriterAttribute.Alt, tooltip);
	//	writer.RenderBeginTag(HtmlTextWriterTag.Img);
	//	writer.RenderEndTag();

	//	// Close out the <a> tag.
	//	writer.RenderEndTag();

	//	// Close out the <div> tag.
	//	writer.RenderEndTag();
	//}

	/// <summary>
	/// Add a title to the page's title tag if it has not yet been assigned by any other process.
	/// </summary>
	/*private void AddPageTitleIfMissing()
	{
		HtmlHead head = this.Page.Header;
		if (head == null)
			throw new WebException(I18nUtils.getString("error.Head_Tag_Missing_Server_Attribute_Ex_Msg);

		if (StringUtils.isEmpty(head.Title))
			head.Title = PageTitle;
	}*/

	/// <summary>
	/// Add a link to the RSS feed for the current album to the <paramref name="head" />. This function
	/// has no effect unless gallery is running an Enterprise license.
	/// </summary>
	/// <param name="head">The head.</param>
	/// <exception cref="System.ArgumentNullException">Thrown when <paramref name="head" /> is null.</exception>
/*	private void AddRssLink(HtmlHead head)
	{
		if (head == null)
			throw new ArgumentNullException();

		var rssUrl = AlbumUtils.GetRssUrl(getAlbum());

		if (rssUrl != null)
		{
			var links = StringUtils.format(
"<link rel='alternate' type='application/rss+xml' title='{0}' href='{1}' />\n" + 
"\n",
 getAlbum().getTitle(),
 rssUrl);

			head.Controls.Add(new LiteralControl(links));
		}
	}*/

	/// <summary>
	/// Renders javascript that should run when the page loads in the browser.
	/// </summary>
	private void addStartupScript() throws Exception	{
		// Set up a global javascript variable that is scoped to this gallery control instance.
		// Other controls can use this object to store variables and perform other functions that
		// must be isolated from any other gallery instances on the page.

		// The p() function returns a reference to the parent div tag of this control. It can be
		// used in jQuery to limit the scope of a search. For example, to get a reference to an
		// element having the class 'infoMsg', use "$('.infoMsg', {{:Gallery.ClientId}}.p())" when
		// inside a jsRender template or "$('.infoMsg', <%=getCId()%>.p())" in an ASCX page.
		String script = StringUtils.format(
"window.{0} = {{ }};\n" + 
"window.{0}.p = function () {{ return $('#{0}');};\n" +
"window.{0}.mdsData = $.parseJSON('{1}');\n" +
"window.{0}.mdsData.ActiveMetaItems = (window.{0}.mdsData.ContentItem ? window.{0}.mdsData.ContentItem.MetaItems : window.{0}.mdsData.Album.MetaItems) || [];\n" +
"window.{0}.mdsData.ActiveApprovalItems = (window.{0}.mdsData.ContentItem ? window.{0}.mdsData.ContentItem.ApprovalItems : window.{0}.mdsData.Album.ApprovalItems) || [];\n" +
"window.{0}.mdsData.ActiveGalleryItems = (window.{0}.mdsData.ContentItem ? [window.Mds.convertContentItemToGalleryItem(window.{0}.mdsData.ContentItem)] : [window.Mds.convertAlbumToGalleryItem(window.{0}.mdsData.Album)]) || [];\n" +
"{2}\n" +
"\n",
				getMdsClientId(), // 0
				getClientMdsDataAsJson(), // 1
				getAlbumTreeDataClientScript() // 2
				);

		//this.Page.ClientScript.RegisterStartupScript(this.GetType(), StringUtils.join(this.getCId(), "_initScript"), script, true);
	}

	/// <summary>
	/// Assign a few global javascript variables that can be used throughout the app. This should
	/// only be added once to a page, even if there are multiple instances of the <see cref="Gallery" />
	/// control.
	/// </summary>
	private void addGlobalStartupScript()
	{
		String script = StringUtils.format(
"window.Mds.AppRoot = '{0}';\n" +
"window.Mds.GalleryResourcesRoot = '{1}';\n" +
"window.Mds.IsPostBack = {2};\n" +
"\n",
			Utils.getAppRoot(request), // 0
			Utils.getGalleryResourcesPath(), // 1
			new Boolean(true).toString().toLowerCase() // 2
			);

		//this.Page.ClientScript.RegisterStartupScript(this.GetType(), StringUtils.join(this.getCId(), "_initGlblScript"), script, true);
	}

	public String getClientMdsDataAsJson() throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException, InvalidAlbumException, Exception{
		return Utils.jsEncode(JsonMapper.getInstance().toJson(getClientMdsData()));
	}

	/// <summary>
	/// Gets JavaScript that assigns a client variable containing data that can be consumed by the jQuery album tree plug-in.
	/// To increase performance, returns an empty String when the left pane is not visible.
	/// </summary>
	/// <returns>System.String.</returns>
	public String getAlbumTreeDataClientScript() throws UnsupportedContentObjectTypeException, InvalidGalleryException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException{
		if (isShowLeftPaneForAlbum() || isShowLeftPaneForContentObject()){
			return StringUtils.format("window.{0}.mdsAlbumTreeData = $.parseJSON('{1}');", getMdsClientId(), getAlbumTreeDataAsJson());
		}

		return StringUtils.EMPTY;
	}
	
	public TreeView getAlbumTreeData() throws InvalidGalleryException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException	{
		if (tv == null) {
			TreeViewOptions tvOptions = new TreeViewOptions();
			tvOptions.SelectedAlbumIds = (getAlbumId() > Long.MIN_VALUE ? new LongCollection(new long[] { getAlbumId() }) : new LongCollection());
			tvOptions.NavigateUrl = getGalleryControlSettings().getTreeViewNavigateUrl() == null ?  Utils.getCurrentPageUrl(request) : getGalleryControlSettings().getTreeViewNavigateUrl();
			tvOptions.EnableCheckboxPlugin = false;
			tvOptions.RequiredSecurityPermissions = new SecurityActions[] {SecurityActions.ViewAlbumOrContentObject};
			tvOptions.Galleries = CMUtils.loadLoginUserGalleries();//new GalleryBoCollection();
			if (tvOptions.Galleries.size() > 1) {
				tvOptions.RootAlbumPrefix = "{GalleryRootAlbumPrefix}";
			}else {
				tvOptions.RootAlbumPrefix = StringUtils.EMPTY;
			}
			
			//tvOptions.Galleries.add(g);
	
			tv = AlbumTreeViewBuilder.getAlbumsAsTreeView(tvOptions);
		}

		return tv;
	}
	

	public String getAlbumTreeDataAsJson() throws InvalidGalleryException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException	{
		if (tv == null) {
			tv = getAlbumTreeData();
		}

		return Utils.jsEncode(tv.toJson());
	}

	/// <summary>
	/// Gets a value indicating whether the javascript and CSS files have already been added to the page
	/// output. This is useful in preventing multiple registrations when more than one
	/// <see cref="Gallery" /> control is on the page.
	/// </summary>
	/// <returns><c>true</c> if javascript and CSS files have already been added to the page; otherwise <c>false</c>.</returns>
	/*private static boolean JavaScriptAndCssLinksAddedToHead()
	{
		object scriptFilesAddedObject = HttpContext.Current.Items["MDS_HtmlHeadConfigured"];
		boolean scriptFilesAdded = false;
		boolean foundScriptFilesAddedVar = ((scriptFilesAddedObject != null) && Boolean.TryParse(scriptFilesAddedObject.ToString(), out scriptFilesAdded));
		return (foundScriptFilesAddedVar && scriptFilesAdded);
	}

	private String GetJQueryPath()
	{
		AppSettings appSetting = AppSettings.getInstance();
		if (Utils.isAbsoluteUrl(appSetting.JQueryScriptPath))
		{
			return appSetting.JQueryScriptPath;
		}
		else
		{
			return this.Page.ResolveUrl(appSetting.JQueryScriptPath);
		}
	}

	private String GetJQueryMigratePath()
	{
		AppSettings appSetting = AppSettings.getInstance();
		if (Utils.isAbsoluteUrl(appSetting.JQueryMigrateScriptPath))
		{
			return appSetting.JQueryMigrateScriptPath;
		}
		else
		{
			return this.Page.ResolveUrl(appSetting.JQueryMigrateScriptPath);
		}
	}

	private String GetJQueryUiPath()
	{
		AppSettings appSetting = AppSettings.getInstance();
		if (Utils.isAbsoluteUrl(appSetting.JQueryUiScriptPath))
		{
			return appSetting.JQueryUiScriptPath;
		}
		else
		{
			return this.Page.ResolveUrl(appSetting.JQueryUiScriptPath);
		}
	}*/

	/// <summary>
	/// If auto-sync is enabled and another synchronization is needed, start a synchronization of the root album in this gallery
	/// on a new thread.
	/// </summary>
	private void runAutoSynchIfNeeded() throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException, RecordExistsException	{
		if (needToRunAutoSync()){
			// Start sync on new thread
			SyncOptions syncOptions = new SyncOptions();
			syncOptions.SyncId = UUID.randomUUID().toString();
			syncOptions.SyncInitiator = SyncInitiator.AutoSync;
			syncOptions.AlbumIdToSynchronize = CMUtils.loadRootAlbumInstance(getGalleryId()).getId();
			syncOptions.IsRecursive = true;
			syncOptions.RebuildThumbnails = false;
			syncOptions.RebuildOptimized = false;
			syncOptions.UserName = Constants.SystemUserName;

			//System.Threading.Tasks.Task.CMUtils.StartNew(() => GalleryUtils.BeginSync(syncOptions), TaskCreationOptions.LongRunning);
		}
	}

	/// <summary>
	/// Gets a value indicating whether an auto-sync must be performed. It is needed when auto-sync is enabled and the specified
	/// interval has passed since the last sync.
	/// </summary>
	/// <returns><c>true</c> if a sync must be run; otherwise <c>false</c>.</returns>
	private boolean needToRunAutoSync() throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, WebException, InvalidAlbumException, RecordExistsException	{
		GallerySettings gallerySettings = CMUtils.loadGallerySetting(getGalleryId());

		if (gallerySettings.getEnableAutoSync()){
			// Auto sync is enabled.
			LocalDate start = gallerySettings.getLastAutoSync().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	    	//LocalDate end = DateUtils.Now.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	    	Duration duration = Duration.between(start, LocalDate.now());
	
			double numMinutesSinceLastSync = duration.toMinutes();
			if (numMinutesSinceLastSync > gallerySettings.getAutoSyncIntervalMinutes())	{
				// It is time to do another sync.
				SynchronizationStatus synchStatus = SynchronizationStatus.getInstance(getGalleryId());

				if ((synchStatus.getStatus() != SynchronizationState.SynchronizingFiles) && (synchStatus.getStatus() != SynchronizationState.PersistingToDataStore))
				{
					// No other sync is in progress - we need to do one!
					return true;
				}
			}
		}

		return false;
	}

	/// <summary>
	/// Evaluate the query String and properties of the Gallery control to discover which, if any, content object to display.
	/// Returns <see cref="Int32.MinValue" /> if no ID is discovered. This function does not evaluate the ID to see if it is
	/// valid or whether the current user has permission to view it.
	/// </summary>
	/// <returns>Returns the ID for the content object to display, or <see cref="Int32.MinValue" /> if no ID is discovered.</returns>
	private long parseContentObjectId() throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException, InvalidAlbumException	{
		// Determine the ID for the content object to display, if any. Follow these rules:
		// 1. If an album has been requested and no content object specified, return Int32.MinValue.
		// 2. If AllowUrlOverride=true and a content object ID has been specified in the query String, use that.
		// 3. If AllowUrlOverride=true and an album ID has been specified in the query String, get one of it's content objects.
		// 4. If a content object ID has been specified on Gallery.ContentObjectId, use that.
		// 5. If ViewMode is Single or SingleRandom and an album ID has been specified on Gallery.AlbumId, get one of it's content objects.
		// 6. If ViewMode is Single or SingleRandom, get one of the content objects in the root album.
		// 7. If none of the above, return Int32.MinValue.

		long aidGc = this.getGalleryControlSettings().getAlbumId() == null ? Long.MIN_VALUE : this.getGalleryControlSettings().getAlbumId();
		long moidGc = this.getGalleryControlSettings().getContentObjectId() == null ? Long.MIN_VALUE : this.getGalleryControlSettings().getContentObjectId();
		long moidQs = Utils.getQueryStringParameterInt64(request, "moid");
		long aidQs = Utils.getQueryStringParameterInt64(request, "aid");
		boolean isAlbumView = (this.getGalleryControlSettings().getViewMode() != null && this.getGalleryControlSettings().getViewMode() == ViewMode.Multiple);
		boolean allowUrlOverride = this.getGalleryControlSettings().getAllowUrlOverride() == null ? true : this.getGalleryControlSettings().getAllowUrlOverride();

		if (isAlbumView && ((aidQs > Long.MIN_VALUE) || (aidGc > Long.MIN_VALUE)) && (moidQs == Long.MIN_VALUE) && (moidGc == Long.MIN_VALUE))
			return Long.MIN_VALUE; // Matched rule 1

		if (allowUrlOverride){
			if (moidQs > Long.MIN_VALUE)
				return moidQs; // Matched rule 2

			if (aidQs > Long.MIN_VALUE)
				return getContentObjectIdInAlbum(aidQs); // Matched rule 3
		}

		if (moidGc > Long.MIN_VALUE)
			return moidGc; // Matched rule 4

		if (!isAlbumView && (aidGc > Long.MIN_VALUE))
			return getContentObjectIdInAlbum(aidGc); // Matched rule 5

		if (!isAlbumView)
			return getContentObjectInRootAlbum(); // Matched rule 6

		return Long.MIN_VALUE; // Matched rule 7
	}

	/// <summary>
	/// Get the ID for one of the content objects in the root album of the current gallery. The ID selected depends on the
	/// <see cref="Gallery.ViewMode" /> and whether <see cref="isAutoPlaySlideShow()" /> has been enabled. Returns <see cref="Int32.MinValue" />
	/// if the album does not contain a suitable content object.
	/// </summary>
	/// <returns>Returns the ID for one of the content objects in the root album, or <see cref="Int32.MinValue" /> if no suitable ID is found.</returns>
	private long getContentObjectInRootAlbum() throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException, InvalidAlbumException{
		if (this.getGalleryControlSettings().getGalleryId() != null && this.getGalleryControlSettings().getGalleryId() > Long.MIN_VALUE){
			return getContentObjectIdInAlbum(CMUtils.loadRootAlbumInstance(this.getGalleryControlSettings().getGalleryId()).getId());
		}

		// No gallery ID has been assigned, so just use the first one we find. I am not sure this code will ever be hit, since it is possible
		// the gallery ID will always be assigned by this point.
		GalleryBoCollection galleries = CMUtils.loadGalleries();
		if (!galleries.isEmpty()) {
			return getContentObjectIdInAlbum(CMUtils.loadRootAlbumInstance(galleries.get(0).getGalleryId()).getId());
		}

		return Long.MIN_VALUE;
	}

	/// <summary>
	/// Get the ID for one of the content objects in the specified <paramref name="albumId" />. The ID selected depends on the
	/// <see cref="Gallery.ViewMode" /> and whether <see cref="isAutoPlaySlideShow()" /> has been enabled. Returns <see cref="Int32.MinValue" />
	/// if the album does not contain a suitable content object.
	/// </summary>
	/// <param name="albumId">The album ID.</param>
	/// <returns>Returns the ID for one of the content objects in the album, or <see cref="Int32.MinValue" /> if no suitable ID is found.</returns>
	private long getContentObjectIdInAlbum(long albumId) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException{
		long moid = Long.MIN_VALUE;

		if (this.getViewMode() == ViewMode.Single){
			// Choose the first content object in the album, unless <see cref="isAutoPlaySlideShow()" /> is enabled, in which case we want
			// to choose the first *image* in the album.
			AlbumBo album = null;
			List<ContentObjectBo> contentObjects = null;

			try	{
				album = AlbumUtils.loadAlbumInstance(albumId, true);
			}catch (InvalidAlbumException ae) { }

			if (album != null){
				if (this.isAutoPlaySlideShow()){
					contentObjects = album.getChildContentObjects(ContentObjectType.Image).toSortedList(); // Get all images in album
				}else{
					contentObjects = album.getChildContentObjects(ContentObjectType.ContentObject).toSortedList(); // Get all content objects in album
				}
			}

			if ((contentObjects != null) && (!contentObjects.isEmpty())){
				moid = contentObjects.get(0).getId();
			}
		}else if (this.getViewMode() == ViewMode.SingleRandom){
			//TODO: Implement ViewMode.SingleRandom functionality
			throw new NotImplementedException("The functionality to support ViewMode.SingleRandom has not been implemented.");
		}

		return moid;
	}

	/// <summary>
	/// Gets the highest-level album the current user can view. Guaranteed to not return null. If a user does not have permission to 
	/// view any objects, this function returns a virtual album with no objects and automatically assigns the <see cref="ClientMessage" /> 
	/// property to <see cref="MessageType.NoAuthorizedAlbumForUser" />, which will cause a message to be displayed to the user.
	/// </summary>
	/// <returns>Returns an AlbumBo representing the highest-level album the current user can view.</returns>
	private AlbumBo getHighestAlbumUserCanView() throws InvalidGalleryException, InvalidMDSRoleException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException{
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
			// Create virtual album so that page has something to bind to.
			tempAlbum = CMUtils.createEmptyAlbumInstance(galleryId);
			tempAlbum.setIsVirtualAlbum(true);
			tempAlbum.setVirtualAlbumType(VirtualAlbumType.Root);
			tempAlbum.setTitle(I18nUtils.getString("album.site_Virtual_Album_Title", request.getLocale()));
			tempAlbum.setCaption(StringUtils.EMPTY);

			/*if (Array.IndexOf(new[] { ResourceId.login, ResourceId.recoverpassword, ResourceId.createaccount }, ResourceId) < 0)
			{
				ClientMessage = getMessageOptions(MessageType.NoAuthorizedAlbumForUser);
			}*/
		}

		return tempAlbum;
	}

	/// <summary>
	/// Gets the album ID corresponding to the current album, or <see cref="Int32.MinValue" /> if no valid album is available. The value 
	/// is determined in the following sequence: (1) If no content object is available, then look for the "aid" query String parameter. 
	/// (2) If not there, or if <see cref="Gallery.AllowUrlOverride" /> has been set to <c>false</c>, look for an album ID on the 
	/// containing <see cref="Gallery" /> control. This function does NOT perform any validation that the album exists and the current 
	/// user has permission to view it.
	/// </summary>
	/// <returns>Returns the album ID corresponding to the current album, or <see cref="Int32.MinValue" /> if no valid album is available.</returns>
	private long parseAlbumId(){
		long aid;
		Object viewstateAid = request.getAttribute("aid");

		if ((viewstateAid == null) || (aid = StringUtils.toLong(viewstateAid.toString())) == Long.MIN_VALUE){
			// Not in viewstate. See if it is on the "aid" query String.
			if ((isAllowUrlOverride()) && (Utils.getQueryStringParameterInt64(request, "aid") > Long.MIN_VALUE)){
				aid = Utils.getQueryStringParameterInt64(request, "aid");
			}else{
				// Use the album ID property on this user control. May return Long.MIN_VALUE.
				aid = this.getGalleryControlSettings().getAlbumId() == null ? Long.MIN_VALUE : this.getGalleryControlSettings().getAlbumId();
			}

			request.setAttribute("aid", aid);
		}

		return aid;
	}

	/// <summary>
	/// Verifies the album exists and the user has permission to view it Throws a <see cref="InvalidAlbumException" /> when an 
	/// album associated with the <paramref name="albumId" /> does not exist. Throws a <see cref="GallerySecurityException" /> 
	/// when the user requests an album he or she does not have permission to view. An instance of the album is assigned to the 
	/// album output parameter, and is guaranteed to not be null.
	/// </summary>
	/// <param name="albumId">The album ID to validate. Throws a <see cref="ArgumentOutOfRangeException"/>
	/// if the value is <see cref="Int32.MinValue"/>.</param>
	/// <param name="album">The album associated with the ID = <paramref name="albumId" />.</param>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when <paramref name="albumId"/> is <see cref="Int32.MinValue"/>.</exception>
	/// <exception cref="InvalidAlbumException">Thrown when an album associated with the <paramref name="albumId" /> does not exist.</exception>
	/// <exception cref="GallerySecurityException">Thrown when the user is requesting an album they don't have permission to view.</exception>
	private AlbumBo validateAlbum(long albumId, AlbumBo album) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException	{
		if (albumId == Long.MIN_VALUE)
			throw new ArgumentOutOfRangeException("albumId", StringUtils.format("A valid album ID must be passed to this function. Instead, the value was {0}.", albumId));

		album = null;
		AlbumBo tempAlbum = null;

		// TEST 1: If the current content object's album matches the ID we are validating, get a reference to that album.
		ContentObjectBo contentObject = getContentObject();
		if (contentObject != null){
			if (contentObject.getParent().getId() != albumId)
				throw new UnsupportedOperationException(StringUtils.format("The requested content object (ID={0}) does not exist in the requested album (ID={1}).", contentObject.getId(), albumId));

			// Instead of loading it from disk, just grab the reference to the content object's parent.
			tempAlbum = (AlbumBo)contentObject.getParent();
		}else{
			// No content object is part of this HTTP request, so load it from disk.
			tempAlbum = AlbumUtils.loadAlbumInstance(albumId, false);
		}

		// TEST 2: Does user have permission to view it?
		if (tempAlbum != null)	{
			if (Utils.isUserAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), tempAlbum.getId(), tempAlbum.getGalleryId(), tempAlbum.getIsPrivate(), tempAlbum.getIsVirtualAlbum())){
				// User is authorized. Assign to output parameter.
				album = tempAlbum;
			}else{
				throw new GallerySecurityException(); // User does not have permission to view the album.
			}
		}
		
		return album;
	}

	private static AlbumBo createEmptyAlbum(long galleryId, HttpServletRequest request) throws UnsupportedContentObjectTypeException, InvalidGalleryException	{
		AlbumBo album = CMUtils.createEmptyAlbumInstance(galleryId);
		album.setIsVirtualAlbum(true);
		album.setVirtualAlbumType(VirtualAlbumType.Root);
		album.setTitle(I18nUtils.getString("album.site_Virtual_Album_Title", request.getLocale()));

		return album;
	}

	/// <summary>
	/// Check the albumId to see if it matches the <see cref="IgetGalleryControlSettings()Settings.AlbumId" /> value for the 
	/// <see cref="Gallery.getGalleryControlSettings()Settings" /> property on the <see cref="Gallery" /> user control. If it does, that means the setting
	/// contains an ID for an album that no longer exists. Delete the setting.
	/// </summary>
	/// <param name="albumId">The album ID.</param>
	private void checkForInvalidAlbumIdInGalleryControlSetting(long albumId) {
		if (this.getGalleryControlSettings().getAlbumId() != null && this.getGalleryControlSettings().getAlbumId() == albumId){
			GalleryControlSettings galleryControlSettingsSettings = CMUtils.loadGalleryControlSetting(this.getControlId(), true);
			galleryControlSettingsSettings.setAlbumId(null);
			galleryControlSettingsSettings.save();
		}
	}

	private void setContentObjectId(long contentObjectId){
		this.contentObjectId = contentObjectId;
		this.contentObject = null;
		this.album = null;
		this.galleryId = Long.MIN_VALUE;
	}

	private String getTitleUrl()
	{
		String url = getGalleryTitleUrl().trim();

		if (!StringUtils.isBlank(getGalleryTitle()) && (url.length() > 0))
			return (url == "~/" ? Utils.getCurrentPageUrl(request) : url);
		else
			return null;
	}

	private String getTitleUrlTooltip()
	{
		String url = getGalleryTitleUrl().trim();

		if (!StringUtils.isBlank(getGalleryTitle()) && (url.length() > 0))
		{
			switch (url)
			{
				case "/":
					{
						return I18nUtils.getString("header.PageHeaderTextUrlToolTipWebRoot", request.getLocale());
					}
				case "~/":
					{
						return I18nUtils.getString("header.PageHeaderTextUrlToolTipAppRoot", request.getLocale());
					}
				default:
					{
						return StringUtils.format(I18nUtils.getString("header.PageHeaderTextUrlToolTip", request.getLocale()), url);
					}
			}
		}
		else
			return null;
	}

	/// <summary>
	/// Gets an object that fully describes how the specified <paramref name="messageId" /> is to be
	/// displayed in the browser.
	/// </summary>
	/// <returns>Returns an instance of <see cref="ClientMessageOptions" />.</returns>
	private ClientMessageOptions getMessageOptions(MessageType messageId){
		if (messageId == MessageType.None){
			return new ClientMessageOptions (messageId);
		}

		final String resourcePrefix = "msg.";
		final String headerSuffix = "_Hdr";
		final String detailSuffix = "_Dtl";

		String title = I18nUtils.getString(StringUtils.join(resourcePrefix, messageId.toString(), headerSuffix), request.getLocale());
		title = title == null ? StringUtils.EMPTY : title;
		String msg = I18nUtils.getString(StringUtils.join(resourcePrefix, messageId.toString(), detailSuffix), request.getLocale());
		msg = msg == null ? StringUtils.EMPTY : msg;

		switch (messageId){
			case ObjectsSkippedDuringUpload:
				{
					String sessionObjectString = (String)request.getSession().getAttribute(Constants.SkippedFilesDuringUploadSessionKey);

					List<ActionResult> uploadResults = null;
					if (!StringUtils.isBlank(sessionObjectString))	{
						uploadResults = getUploadErrors(JsonMapper.getInstance().fromJson(sessionObjectString, JsonMapper.getInstance().createCollectionType(List.class, ActionResult.class)));
					}

					msg = StringUtils.EMPTY;
					if (uploadResults != null){
						// This message is unique in that we need to choose one of two detail messages from the resource file. One is for when a single
						// file has been skipped; the other is when multiple files have been skipped.
						if (uploadResults.size() == 1){
							String detailMsgTemplate = I18nUtils.getString(StringUtils.join(resourcePrefix, messageId.toString(), "Single", detailSuffix), request.getLocale());
							detailMsgTemplate = detailMsgTemplate == null ? StringUtils.EMPTY : detailMsgTemplate;
							msg = StringUtils.format(detailMsgTemplate, uploadResults.get(0).Title, uploadResults.get(0).Message);
						}else if (uploadResults.size() > 1){
							String detailMsgTemplate = I18nUtils.getString(StringUtils.join(resourcePrefix, messageId.toString(), "Multiple", detailSuffix), request.getLocale());
							detailMsgTemplate = detailMsgTemplate == null ? StringUtils.EMPTY : detailMsgTemplate;
							msg = StringUtils.format(detailMsgTemplate, convertListToHtmlBullets(uploadResults));
						}
					}
					break;
				}
		}
		
		ClientMessageOptions clientMessageOptions = new ClientMessageOptions();
		clientMessageOptions.setMessageId(messageId);
		clientMessageOptions.setTitle(title);
		clientMessageOptions.setMessage(msg);
		clientMessageOptions.setStyle(getMessageStyle(messageId));

		return clientMessageOptions;
	}

	private MessageStyle getMessageStyle(MessageType messageId)	{
		switch (messageId) {
			case None:
			case ThumbnailSuccessfullyAssigned:
				return MessageStyle.Success;

			case CannotAssignThumbnailNoObjectsExistInAlbum:
			case CannotEditCaptionsNoEditableObjectsExistInAlbum:
			case CannotRotateNoRotatableObjectsExistInAlbum:
			case CannotMoveNoObjectsExistInAlbum:
			case CannotCopyNoObjectsExistInAlbum:
			case CannotDeleteOriginalFilesNoObjectsExistInAlbum:
			case CannotDeleteObjectsNoObjectsExistInAlbum:
			case ContentObjectDoesNotExist:
			case AlbumDoesNotExist:
				return MessageStyle.Info;

			case ObjectsSuccessfullyDeleted:
			case OriginalFilesSuccessfullyDeleted:
				return MessageStyle.Success;

			case UserNameOrPasswordIncorrect:
			case AlbumNotAuthorizedForUser:
			case NoAuthorizedAlbumForUser:
			case CannotOverlayWatermarkOnImage:
			case CannotRotateObjectNotRotatable:
				return MessageStyle.Info;

			case ObjectsSuccessfullyMoved:
			case ObjectsSuccessfullyCopied:
			case ObjectsSuccessfullyRearranged:
			case ObjectsSuccessfullyRotated:
				return MessageStyle.Success;

			case ObjectsSkippedDuringUpload:
			case CannotRotateInvalidImage:
			case CannotEditGalleryIsReadOnly:
			case CannotDownloadObjectsNoObjectsExistInAlbum:
				return MessageStyle.Info;

			case GallerySuccessfullyChanged:
			case SettingsSuccessfullyChanged:
				return MessageStyle.Success;

			default:
				return MessageStyle.Success;
		}
	}

	/// <summary>
	/// Displays the message stored in <see cref="ClientMessage" /> to the user when the page is loaded in the browser.
	/// </summary>
	private void showClientMessage(){
		if (clientMessage != null){
			String script = StringUtils.format(
"(function ($) {{\n"+
"$(document).ready(function () {{\n"+
"	$.mdsShowMsg('{0}', '{1}', {{msgType: '{2}', autoCloseDelay: {3}}});\n"+
"}});\n"+
"}})(jQuery);\n"+
"\n",
				clientMessage.getTitle() != null ? Utils.jsEncode(clientMessage.getTitle()) : null,
				clientMessage.getMessage() != null ? Utils.jsEncode(clientMessage.getMessage()) : null,
				clientMessage.getStyle().toString().toLowerCase(),
				clientMessage.getAutoCloseDelay());

			//Page.ClientScript.RegisterStartupScript(GetType(), StringUtils.join(ClientID, "_msgScript"), script, true);
		}
	}

	/// <summary>
	/// Gets a data entity containing information about the current gallery. The instance can be JSON-parsed and sent to the 
	/// browser.
	/// </summary>
	/// <returns>Returns <see cref="Entity.Settings" /> object containing information about the current gallery.</returns>
	private SettingsRest getSettingsEntity() throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException, InvalidAlbumException, RecordExistsException{
		SettingsRest settings = new SettingsRest();
		settings.setGalleryId(getGalleryId());
		settings.setClientId(getMdsClientId());
		settings.setContentClientId(getContentClientId());
		settings.setContentTmplName(getContentTmplName());
		settings.setHeaderClientId(getHeaderClientId());
		settings.setHeaderTmplName(getHeaderTmplName());
		settings.setThumbnailClientId(getThumbnailClientId());
		settings.setThumbnailTmplName(getThumbnailTmplName());
		settings.setLeftPaneClientId(getLeftPaneClientId());
		settings.setLeftPaneTmplName(getLeftPaneTmplName());
		settings.setRightPaneClientId(getRightPaneClientId());
		settings.setRightPaneTmplName(getRightPaneTmplName());
		settings.setShowHeader(false); //isShowHeader()
		settings.setShowLogin(isShowLogin());
		settings.setShowSearch(isShowSearch());
		settings.setShowContentObjectNavigation(isShowContentObjectNavigation());
		settings.setShowContentObjectIndexPosition(isShowContentObjectIndexPosition());
		settings.setEnableSelfRegistration(getGallerySettings().getEnableSelfRegistration());
		settings.setEnableUserAlbum(getGallerySettings().getEnableUserAlbum());
		settings.setAllowManageOwnAccount(getGallerySettings().getAllowManageOwnAccount());
		settings.setTitle(getGalleryTitle());
		settings.setTitleUrl(getTitleUrl());
		settings.setTitleUrlTooltip(getTitleUrlTooltip());
		settings.setShowContentObjectTitle(isShowContentObjectTitle());
		settings.setPageSize(getGallerySettings().getPageSize());
		settings.setPagerLocation(getGallerySettings().getPagerLocation().toString());
		settings.setTransitionType(getGallerySettings().getContentObjectTransitionType().toString().toLowerCase());
		settings.setTransitionDurationMs((int)(getGallerySettings().getContentObjectTransitionDuration() * 1000));
		settings.setShowContentObjectToolbar(isShowContentObjectToolbar());
		settings.setAllowDownload(getGallerySettings().getEnableContentObjectDownload());
		settings.setAllowZipDownload(getGallerySettings().getEnableContentObjectZipDownload());
		settings.setShowUrlsButton(isShowUrlsButton());
		settings.setShowSlideShowButton(isShowSlideShowButton());
		settings.setSlideShowIsRunning(isAutoPlaySlideShow() && !getAlbum().getChildContentObjects(ContentObjectType.Image).values().isEmpty());
		settings.setSlideShowType(getSlideShowType().toString());
		settings.setSlideShowIntervalMs(getGallerySettings().getSlideshowInterval());
		settings.setShowTransferContentObjectButton(isShowTransferContentObjectButton());
		settings.setShowCopyContentObjectButton(isShowCopyContentObjectButton());
		settings.setShowRotateContentObjectButton(isShowRotateContentObjectButton());
		settings.setShowDeleteContentObjectButton(isShowDeleteContentObjectButton());
		settings.setMaxThmbTitleDisplayLength(getGallerySettings().getMaxThumbnailTitleDisplayLength());
		settings.setAllowAnonymousRating(getGallerySettings().getAllowAnonymousRating());
		settings.setAllowAnonBrowsing(getGallerySettings().getAllowAnonymousBrowsing());
		settings.setReadOnlyGallery(getGallerySettings().getContentObjectPathIsReadOnly());
		
		return settings;
	}

	/// <summary>
	/// Gets the gallery object filter specified in the filter query String parameter. If not present or is not a valid
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
	/// Gets the gallery object filter specified in the filter query String parameter. If not present or is not a valid
	/// value, returns <paramref name="defaultFilter" />. If <paramref name="defaultFilter" /> is not specified, 
	/// it defaults to <see cref="ContentObjectType.All" />.
	/// </summary>
	/// <param name="defaultFilter">The default filter. Defaults to <see cref="ContentObjectType.All" /> when not specified.</param>
	/// <returns>An instance of <see cref="ContentObjectType" />.</returns>
	private static ApprovalStatus getContentObjectApprovalFilter(HttpServletRequest request) {
		return getContentObjectApprovalFilter(ApprovalStatus.All, request);
	}
	
    private static ApprovalStatus getContentObjectApprovalFilter(ApprovalStatus defaultFilter, HttpServletRequest request) {
    	if (Utils.isQueryStringParameterPresent(request, "approval")){
    	  	return ApprovalStatus.parse(Utils.getQueryStringParameterString(request, "approval"), defaultFilter);
      	}

      	return defaultFilter;
    }

	//#endregion
    
 // <summary>
 	/// Gets an string representing a javascript array containing the list of allowed file extensions. Returns an empty array
 	/// when there aren't any restrictions.
 	/// </summary>
 	/// <returns>System.String.</returns>
 	public static String getFileFilters(GallerySettings gallerySettings) throws InvalidGalleryException	{
 		if (gallerySettings.getAllowUnspecifiedMimeTypes())	{
 			return "[]";
 		}

 		String extensions = StringUtils.join(ArrayUtils.add(CMUtils.loadMimeTypes(gallerySettings.getGalleryId()).stream().filter(mt -> mt.getAllowAddToGallery()).map(mt -> mt.getExtension()).toArray(String[]::new), ".zip" ), ",");
 		return StringUtils.format("[{{ title: 'Supported files', extensions: '{0}' }}]", extensions.replace(".", ""));
 	}
}
