package com.mds.aiotplayer.cm.util;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;

import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.sys.util.MDSRoleCollection;
import com.mds.aiotplayer.cm.content.GallerySettings;
import com.mds.aiotplayer.cm.content.nullobjects.NullContentObject;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.core.ResourceId;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.core.VirtualAlbumType;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.core.exception.WebException;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.util.Utils;

/// <summary>
/// A user control that renders the Actions menu that appears when a logged-on user has permission to carry out at least one action.
/// </summary>
public class AlbumMenuBuilder{
	public AlbumMenuBuilder(GalleryView galleryView) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException {
		this.userCanCreateAlbum = galleryView.isUserCanCreateAlbum();
		this.userCanEditAlbum = galleryView.isUserCanEditAlbum();
		this.userCanAddContentObject = galleryView.isUserCanAddContentObject();
		this.userCanEditContentObject = galleryView.isUserCanEditContentObject();
		this.userCanDeleteCurrentAlbum = galleryView.isUserCanDeleteCurrentAlbum();
		this.userCanDeleteChildAlbum = galleryView.isUserCanDeleteChildAlbum();
		this.userCanDeleteContentObject = galleryView.isUserCanDeleteContentObject();
	    //this.userCanApprovalContentObject = galleryView.isUserCanCreateAlbum();
		this.userCanSynchronize = galleryView.isUserCanSynchronize();
		//this.userDoesNotGetWatermark;
		this.userCanAddContentObjectToAtLeastOneAlbum = galleryView.isUserCanAddContentObjectToAtLeastOneAlbum();
		this.userCanAddAlbumToAtLeastOneAlbum = galleryView.isUserCanAddAlbumToAtLeastOneAlbum();
		this.userIsAdminForAtLeastOneOtherGallery = galleryView.isUserIsAdminForAtLeastOneOtherGallery();
		this.albumId = galleryView.getAlbumId();
		this.contentObjectId = galleryView.getContentObjectId();
		this.gallerySettings = galleryView.getGallerySettings();
		this.album = galleryView.getAlbum();
		this.galleryView = galleryView;
		this.cid=galleryView.getMdsClientId();
	}
	//#region Private Fields

	private long albumId = Long.MIN_VALUE;
	private long contentObjectId = Long.MIN_VALUE;
	private GallerySettings gallerySettings;
	private AlbumBo album;
	private GalleryView galleryView;
	private String cid;
	/*private boolean userCanViewAlbumOrContentObject;
	private boolean userCanViewOriginal;
	private boolean userCanAddAdministerSite;
	private boolean userCanAdministerGallery;*/
	private boolean userCanCreateAlbum;
	private boolean userCanEditAlbum;
	private boolean userCanAddContentObject;
	private boolean userCanEditContentObject;
	private boolean userCanDeleteCurrentAlbum;
	private boolean userCanDeleteChildAlbum;
	private boolean userCanDeleteContentObject;
    //private boolean userCanApprovalContentObject;
	private boolean userCanSynchronize;
	//private boolean userDoesNotGetWatermark;
	private boolean userCanAddContentObjectToAtLeastOneAlbum;
	private boolean userCanAddAlbumToAtLeastOneAlbum;
	private boolean userIsAdminForAtLeastOneOtherGallery;
	private Boolean showActionMenu;

	//#endregion

	//#region Protected Methods

	public String getMenuHtml(HttpServletRequest request) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException{
		if (!getShowActionMenu())
			return "";
		
		final String uiStateDisabled = " ui-state-disabled";
		String currURL = (String)request.getAttribute("javax.servlet.forward.request_uri");
		if (currURL == null)
			currURL = Utils.getCurrentPageUrl(request);

		return StringUtils.format( 
"<ul id='{0}_mnu' class='mds_a_m'>\n" + 
"	<li class='mds_a_m_root'><a href='#'>{1}</a>\n" +
"		<ul>\n" +
"			<li class='mds_a_m_c_a{2}'><a href='{18}' title='{33}'><span>{49}</span></a></li>\n" +
"			<li></li>\n" +
"			<li class='mds_a_m_a_o{3}'><a href='{19}' title='{34}'><span>{50}</span></a></li>\n" +
"			<li class='mds_a_m_m_o{4}'><a href='{20}' title='{35}'><span>{51}</span></a></li>\n" +
"			<li class='mds_a_m_c_o{5}'><a href='{21}' title='{36}'><span>{52}</span></a></li>\n" +
"			<li class='mds_a_m_m_a{6}'><a href='{22}' title='{37}'><span>{53}</span></a></li>\n" +
"			<li class='mds_a_m_c_alb{7}'><a href='{23}' title='{38}'><span>{54}</span></a></li>\n" +
"			<li></li>\n" +
"			<li class='mds_a_m_dl_o{8}'><a href='{24}' title='{39}'><span>{55}</span></a></li>\n" +
"			<li></li>\n" +
"			<li class='mds_a_m_e_c{9}'><a href='{25}' title='{40}'><span>{56}</span></a></li>\n" +
"			<li class='mds_a_m_a_t{10}'><a href='{26}' title='{41}'><span>{57}</span></a></li>\n" +
"			<li class='mds_a_m_r_i{11}'><a href='{27}' title='{42}'><span>{58}</span></a></li>\n" +
"			<li></li>\n" +
"			<li class='mds_a_m_d_o{12}'><a href='{28}' title='{43}'><span>{59}</span></a></li>\n" +
"			<li class='mds_a_m_d_o_f{13}'><a href='{29}' title='{44}'><span>{60}</span></a></li>\n" +
"			<li class='mds_a_m_d_a{14}'><a href='{30}' title='{45}'><span>{61}</span></a></li>\n" +
"			<li></li>\n" +
"			<li class='mds_a_m_s{15}'><a href='{31}' title='{46}'><span>{62}</span></a></li>\n" +
"		</ul>\n" +
"	</li>\n" +
"</ul>\n" +
"\n",
	cid, // 0
	I18nUtils.getString("uc.actionMenu.Root_Text", request.getLocale()), // 1
	isCreateAlbumEnabled() ? StringUtils.EMPTY : uiStateDisabled, // 2
	isAddObjectsEnabled() ? StringUtils.EMPTY : uiStateDisabled, // 3
	isMoveObjectsEnabled() ? StringUtils.EMPTY : uiStateDisabled, // 4
	isCopyObjectsEnabled() ? StringUtils.EMPTY : uiStateDisabled, // 5
	isMoveAlbumEnabled() ? StringUtils.EMPTY : uiStateDisabled, // 6
	isCopyAlbumEnabled() ? StringUtils.EMPTY : uiStateDisabled, // 7
	isDownloadObjectsEnabled() ? StringUtils.EMPTY : uiStateDisabled, // 8
	isEditCaptionsEnabled() ? StringUtils.EMPTY : uiStateDisabled, // 9
	isAssignThumbnailEnabled() ? StringUtils.EMPTY : uiStateDisabled, // 10
	isRotateImagesEnabled() ? StringUtils.EMPTY : uiStateDisabled, // 11
	isDeleteObjectsEnabled() ? StringUtils.EMPTY : uiStateDisabled, // 12
	isDeleteOriginalFilesEnabled() ? StringUtils.EMPTY : uiStateDisabled, // 13
	isDeleteAlbumEnabled() ? StringUtils.EMPTY : uiStateDisabled, // 14
	isSynchronizeEnabled() ? StringUtils.EMPTY : uiStateDisabled, // 15
	null, //16
    null, //17
	Utils.getUrl(currURL, ResourceId.cm_createalbum, "aid={0}", getAlbumId()), // 18
	Utils.getUrl(currURL, ResourceId.cm_addobjects, "aid={0}", getAlbumId()), // 19
	Utils.getUrl(currURL, ResourceId.cm_transferobject, "aid={0}&tt=move&skipstep1=false", getAlbumId()), // 20
	Utils.getUrl(currURL, ResourceId.cm_transferobject, "aid={0}&tt=copy&skipstep1=false", getAlbumId()), // 21
	Utils.getUrl(currURL, ResourceId.cm_transferobject, "aid={0}&tt=move&skipstep1=true", getAlbumId()), // 22
	Utils.getUrl(request, ResourceId.cm_transferobject, "aid={0}&tt=copy&skipstep1=true", getAlbumId()), // 23
	getDownloadObjectsUrl(request), // 24
	Utils.getUrl(currURL, ResourceId.cm_editcaptions, "aid={0}", getAlbumId()), // 25
	Utils.getUrl(currURL, ResourceId.cm_assignthumbnail, "aid={0}", getAlbumId()), // 26
	Utils.getUrl(currURL, ResourceId.cm_rotateimages, "aid={0}", getAlbumId()), // 27
	Utils.getUrl(currURL, ResourceId.cm_deleteobjects, "aid={0}", getAlbumId()), // 28
	Utils.getUrl(currURL, ResourceId.cm_deleteoriginals, "aid={0}", getAlbumId()), // 29
	Utils.getUrl(currURL, ResourceId.cm_deletealbum, "aid={0}", getAlbumId()), // 30
	Utils.getUrl(currURL, ResourceId.cm_synchronize, "aid={0}", getAlbumId()), // 31
	null, //32
	getCreateAlbumTooltip(request),         // 33
	getAddObjectsTooltip(request),          // 34
	getMoveObjectsTooltip(request),         // 35
	getCopyObjectsTooltip(request),         // 36
	getMoveAlbumTooltip(request),           // 37
	getCopyAlbumTooltip(request),           // 38
	getDownloadObjectsTooltip(request),     // 39
	getEditCaptionsTooltip(request),        // 40
	getAssignThumbnailTooltip(request),     // 41
	getRotateImagesTooltip(request),        // 42
	getDeleteObjectsTooltip(request),       // 43
	getDeleteOriginalFilesTooltip(request), // 44
	getDeleteAlbumTooltip(request),         // 45
	getSynchronizeTooltip(request),         // 46
	null, //47
	null, //48
	I18nUtils.getString("uc.actionMenu.Create_Album_Text", request.getLocale()),     // 49
	I18nUtils.getString("uc.actionMenu.Add_Objects_Text", request.getLocale()),      // 50
	I18nUtils.getString("uc.actionMenu.Transfer_Objects_Text", request.getLocale()), // 51
	I18nUtils.getString("uc.actionMenu.Copy_Objects_Text", request.getLocale()),     // 52
	I18nUtils.getString("uc.actionMenu.Move_Album_Text", request.getLocale()),       // 53
	I18nUtils.getString("uc.actionMenu.Copy_Album_Text", request.getLocale()),       // 54
	I18nUtils.getString("uc.actionMenu.Download_Objects_Text", request.getLocale()), // 55
	I18nUtils.getString("uc.actionMenu.Edit_Captions_Text", request.getLocale()),    // 56
	I18nUtils.getString("uc.actionMenu.Assign_Thumbnail_Text", request.getLocale()), // 57
	I18nUtils.getString("uc.actionMenu.Rotate_Text", request.getLocale()),           // 58
	I18nUtils.getString("uc.actionMenu.Delete_Objects_Text", request.getLocale()),   // 59
	I18nUtils.getString("uc.actionMenu.Delete_HiRes_Text", request.getLocale()),     // 60
	I18nUtils.getString("uc.actionMenu.Delete_Album_Text", request.getLocale()),     // 61
	I18nUtils.getString("uc.actionMenu.Synchronize_Text", request.getLocale())       // 62
);

	}
	
	public String buildMenuString(HttpServletRequest request) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException, RecordExistsException{
		if (!this.galleryView.isShowAlbumBreadCrumb()){
			return StringUtils.EMPTY;
		}

		String menuString = StringUtils.EMPTY;
		boolean renderLinks = galleryView.getGalleryControlSettings().getAllowUrlOverride() == null ? true : galleryView.getGalleryControlSettings().getAllowUrlOverride();

		AlbumBo album = galleryView.getAlbum();
		MDSRoleCollection roles = this.galleryView.getMDSRolesForUser();
		String dividerText = I18nUtils.getString("uc.album.Menu_Album_Divider_Text", request.getLocale());
		//.replace("{album.root_Album_Default_Title}", I18nUtils.getMessage("album.root_Album_Default_Title"))
		String root_Album_Default_Title = I18nUtils.getString("album.root_Album_Default_Title", request.getLocale());
		boolean foundTopAlbum = false;
		boolean foundBottomAlbum = false;
		while (!foundTopAlbum){
			// Iterate through each album and it's parents, working the way toward the top. For each album, build up a breadcrumb menu item.
			// Eventually we will reach one of three situations: (1) a virtual album that contains the child albums, (2) an album the current
			// user does not have permission to view, or (3) the actual top-level album.
			if (album.getIsVirtualAlbum()){
				menuString = StringUtils.insert(menuString, 0, StringUtils.format(" {0} <a href=\"{1}\">{2}</a>", dividerText, AlbumUtils.getUrl(album, request), album.getTitle()));

				VirtualAlbumType[] searchVirtualAlbumTypes = new VirtualAlbumType[] { VirtualAlbumType.Tag, VirtualAlbumType.People, VirtualAlbumType.Search, VirtualAlbumType.TitleOrCaption, VirtualAlbumType.MostRecentlyAdded, VirtualAlbumType.Rated, VirtualAlbumType.Approval};
				boolean isAlbumSearchResult = ArrayUtils.indexOf(searchVirtualAlbumTypes, album.getVirtualAlbumType()) > -1;

				if (isAlbumSearchResult){
					// Add one more link to represent the root album.  
					menuString = StringUtils.insert(menuString, 0, StringUtils.format(" {0} <a href=\"{1}\">{2}</a>", dividerText, Utils.getCurrentPageUrl(request), I18nUtils.getString("album.site_Virtual_Album_Title", request.getLocale())));
				}

				foundTopAlbum = true;
			}else if (!UserUtils.isUserAuthorized(SecurityActions.ViewAlbumOrContentObject, roles, album.getId(), album.getGalleryId(), album.getIsPrivate(), album.getIsVirtualAlbum())){
				// User is not authorized to view this album. If the user has permission to view more than one top-level album, then we want
				// to display an "All albums" link. To determine this, load the root album. If a virtual album is returned, then we know the
				// user has access to more than one top-level album. If it is an actual album (with a real ID and persisted in the data store),
				// that means that album is the only top-level album the user can view, and thus we do not need to create a link that is one
				// "higher" than that album.
				AlbumBo rootAlbum = CMUtils.loadRootAlbum(this.galleryView.getGalleryId(), galleryView.getMDSRolesForUser(), UserUtils.isAuthenticated());
				if (rootAlbum.getIsVirtualAlbum()){
					menuString = StringUtils.insert(menuString, 0, StringUtils.format(" {0} <a href=\"{1}\">{2}</a>", dividerText, Utils.getCurrentPageUrl(request), I18nUtils.getString("album.site_Virtual_Album_Title", request.getLocale())));
				}
				foundTopAlbum = true;
			}else{
				// Regular album somewhere in the hierarchy. Create a breadcrumb link.
				String hyperlinkIdString = StringUtils.EMPTY;
				if (!foundBottomAlbum){
					hyperlinkIdString = " id=\"currentAlbumLink\""; // ID is referenced when inline editing an album's title
					foundBottomAlbum = true;
				}

				if (renderLinks){
					menuString = StringUtils.insert(menuString, 0, StringUtils.format(" {0} <a{1} href=\"{2}\">{3}</a>", dividerText, hyperlinkIdString, AlbumUtils.getUrl(album, request), Utils.removeHtmlTags(album.getTitle().replace("{album.root_Album_Default_Title}", root_Album_Default_Title))));
				}else{
					menuString = StringUtils.insert(menuString, 0, StringUtils.format(" {0} {1}", dividerText, Utils.removeHtmlTags(album.getTitle().replace("{album.root_Album_Default_Title}", root_Album_Default_Title))));
				}
			}

			if (album.getParent() instanceof NullContentObject)
				foundTopAlbum = true;
			else
				album = (AlbumBo)album.getParent();
		}

		if (menuString.length() > (dividerText.length() + 2)){
			menuString = menuString.substring(dividerText.length() + 2); // Remove the first divider character
		}

		return menuString;
	}
	
	/// <summary>
	/// Gets a value indicating whether the action menu should be displayed. Always returns false for anonymous users.
	/// Returns true for logged on users if they have permission to execute at least one of the commands in the menu
	/// against the current album and the preference setting is enabled.
	/// </summary>
	public boolean getShowActionMenu() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException{
		if (this.showActionMenu == null) {
			if (galleryView.isAnonymousUser()){
				this.showActionMenu = false; // Always returns false for anonymous users.
			}else{
				// Return true if logged on user if one of the following is true:
				// (1) User is a site or gallery admin
				// (2) User has permission for at least one menu item and the preference setting is enabled.
				boolean userHasPermissionForAtLeastOneItemInActionMenu = (this.galleryView.isUserCanAdministerSite() ||
					this.galleryView.isUserCanAdministerGallery() ||
					this.galleryView.isUserCanEditContentObject() ||
					this.galleryView.isUserCanEditAlbum() ||
					this.galleryView.isUserCanDeleteCurrentAlbum() ||
					this.galleryView.isUserCanDeleteContentObject() ||
					this.galleryView.isUserCanSynchronize() ||
					this.galleryView.isUserCanAddAlbumToAtLeastOneAlbum() ||
					this.galleryView.isUserCanAddContentObjectToAtLeastOneAlbum());
	
				boolean userIsAdmin = (this.galleryView.isUserCanAdministerSite() || this.galleryView.isUserCanAdministerGallery());
	
				this.showActionMenu = (userIsAdmin || (userHasPermissionForAtLeastOneItemInActionMenu & this.galleryView.isShowActionMenu()));
			}
		}

		return this.showActionMenu.booleanValue();
	}
	
	/// <summary>
	/// Gets the CSS class to be used for the album breadcrumb menu.
	/// </summary>
	/// <returns></returns>
	public String getAlbumMenuClass() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, WebException, GallerySecurityException, RecordExistsException{
		if (this.getShowActionMenu()){
			return "albumMenu indented"; // Add the indented CSS class to make room for the action menu.
		}else{
			return "albumMenu";
		}
	}

	//#endregion

	//#region Event Handlers
	//#endregion

	//#region Public Properties
	
	private boolean isContentObjectPathIsWriteable(){
		 return !gallerySettings.getContentObjectPathIsReadOnly();
	}

	/// <summary>
	/// Gets a String similar to "(Disabled because you do not have permission for this action)".
	/// </summary>
	/// <value>A String.</value>
	private static String getDisabledDueToInsufficientPermissionText(HttpServletRequest request){
		return I18nUtils.getString("uc.actionMenu.Disabled_Insufficient_Permission_Tooltip", request.getLocale());
	}

	private boolean isCreateAlbumEnabled(){
		return isContentObjectPathIsWriteable() && userCanAddAlbumToAtLeastOneAlbum;
	}

	private String getCreateAlbumTooltip(HttpServletRequest request){
		if (!isContentObjectPathIsWriteable()){
			return I18nUtils.getString("uc.actionMenu.Disabled_ReadOnly_Tooltip", request.getLocale());
		}

		return isCreateAlbumEnabled() ? I18nUtils.getString("uc.actionMenu.Create_Album_Tooltip", request.getLocale()) : getDisabledDueToInsufficientPermissionText(request);
	}

	private boolean isAddObjectsEnabled(){
		return isContentObjectPathIsWriteable() && userCanAddContentObject;
	}

	private String getAddObjectsTooltip(HttpServletRequest request){
		if (!isContentObjectPathIsWriteable())	{
			return I18nUtils.getString("uc.actionMenu.Disabled_ReadOnly_Tooltip", request.getLocale());
		}

		return isAddObjectsEnabled() ? I18nUtils.getString("uc.actionMenu.Add_Objects_Tooltip", request.getLocale()) : getDisabledDueToInsufficientPermissionText(request);
	}

	private boolean isMoveObjectsEnabled(){
		return isContentObjectPathIsWriteable() &&
			(userCanDeleteContentObject && userCanAddContentObjectToAtLeastOneAlbum) ||
			(userCanDeleteChildAlbum && userCanAddAlbumToAtLeastOneAlbum);
	}

	private String getMoveObjectsTooltip(HttpServletRequest request){
		if (!isContentObjectPathIsWriteable())
		{
			return I18nUtils.getString("uc.actionMenu.Disabled_ReadOnly_Tooltip", request.getLocale());
		}

		return isMoveObjectsEnabled() ? I18nUtils.getString("uc.actionMenu.Transfer_Objects_Tooltip", request.getLocale()) : getDisabledDueToInsufficientPermissionText(request);
	}

	private boolean isCopyObjectsEnabled(){
		boolean userCanCopyInCurrentGallery;
		if (gallerySettings.getAllowCopyingReadOnlyObjects()){
			userCanCopyInCurrentGallery =(isContentObjectPathIsWriteable() && (userCanAddAlbumToAtLeastOneAlbum || userCanAddContentObjectToAtLeastOneAlbum));
		}else{
			userCanCopyInCurrentGallery = (isContentObjectPathIsWriteable() && userCanAddContentObject);
		}

		return (userCanCopyInCurrentGallery || userIsAdminForAtLeastOneOtherGallery);
	}
	
	private String getCopyObjectsTooltip(HttpServletRequest request){
		if (!isCopyObjectsEnabled() && !isContentObjectPathIsWriteable()){
			return I18nUtils.getString("uc.actionMenu.Disabled_ReadOnly_Tooltip", request.getLocale());
		}

		return isCopyObjectsEnabled() ? I18nUtils.getString("uc.actionMenu.Copy_Objects_Tooltip", request.getLocale()) : getDisabledDueToInsufficientPermissionText(request);
	}

	private boolean isMoveAlbumEnabled(){
		return isContentObjectPathIsWriteable() && (!getAlbum().isRootAlbum() && userCanDeleteCurrentAlbum && userCanAddAlbumToAtLeastOneAlbum);
	}

	private String getMoveAlbumTooltip(HttpServletRequest request){
		if (!isContentObjectPathIsWriteable())
		{
			return I18nUtils.getString("uc.actionMenu.Disabled_ReadOnly_Tooltip", request.getLocale());
		}

		return isMoveAlbumEnabled() ? I18nUtils.getString("uc.actionMenu.Move_Album_Tooltip", request.getLocale()) : getDisabledDueToInsufficientPermissionText(request);
	}

	private boolean isCopyAlbumEnabled(){
		boolean userCanCopyInCurrentGallery;
		if (gallerySettings.getAllowCopyingReadOnlyObjects()){
			userCanCopyInCurrentGallery = (isContentObjectPathIsWriteable() && (!getAlbum().isRootAlbum() && userCanAddAlbumToAtLeastOneAlbum));
		}else{
			userCanCopyInCurrentGallery = (isContentObjectPathIsWriteable() && (!getAlbum().isRootAlbum() && userCanCreateAlbum));
		}

		return userCanCopyInCurrentGallery || userIsAdminForAtLeastOneOtherGallery;
	}

	private String getCopyAlbumTooltip(HttpServletRequest request){
		if (!isCopyAlbumEnabled() && !isContentObjectPathIsWriteable())	{
			return I18nUtils.getString("uc.actionMenu.Disabled_ReadOnly_Tooltip", request.getLocale());
		}

		return isCopyAlbumEnabled() ? I18nUtils.getString("uc.actionMenu.Copy_Album_Tooltip", request.getLocale()) : getDisabledDueToInsufficientPermissionText(request);
	}

	private boolean isDownloadObjectsEnabled(){
		return isContentObjectPathIsWriteable() && gallerySettings.getEnableContentObjectZipDownload();
	}

	private String getDownloadObjectsTooltip(HttpServletRequest request){
		return isDownloadObjectsEnabled() ? I18nUtils.getString("uc.actionMenu.Download_Objects_Tooltip", request.getLocale()) : getDisabledDueToInsufficientPermissionText(request);
	}

	private boolean isEditCaptionsEnabled(){
		return isContentObjectPathIsWriteable() && userCanEditContentObject;
	}

	private String getEditCaptionsTooltip(HttpServletRequest request){
		return isEditCaptionsEnabled() ? I18nUtils.getString("uc.actionMenu.Edit_Captions_Tooltip", request.getLocale()) : getDisabledDueToInsufficientPermissionText(request);
	}
	
	private boolean isAssignThumbnailEnabled(){
		return userCanEditAlbum;
	}

	private String getAssignThumbnailTooltip(HttpServletRequest request){
		return isAssignThumbnailEnabled() ? I18nUtils.getString("uc.actionMenu.Assign_Thumbnail_Tooltip", request.getLocale()) : getDisabledDueToInsufficientPermissionText(request);
	}

	private boolean isRotateImagesEnabled(){
		return isContentObjectPathIsWriteable() && userCanEditContentObject;
	}

	private String getRotateImagesTooltip(HttpServletRequest request){
		if (!isContentObjectPathIsWriteable())
		{
			return I18nUtils.getString("uc.actionMenu.Disabled_ReadOnly_Tooltip", request.getLocale());
		}

		return isRotateImagesEnabled() ? I18nUtils.getString("uc.actionMenu.Rotate_Tooltip", request.getLocale()) : getDisabledDueToInsufficientPermissionText(request);
	}

	private boolean isDeleteObjectsEnabled(){
		return isContentObjectPathIsWriteable() && (userCanDeleteContentObject || userCanDeleteChildAlbum);
	}

	private String getDeleteObjectsTooltip(HttpServletRequest request){
		if (!isContentObjectPathIsWriteable())
		{
			return I18nUtils.getString("uc.actionMenu.Disabled_ReadOnly_Tooltip", request.getLocale());
		}

		return isDeleteObjectsEnabled() ? I18nUtils.getString("uc.actionMenu.Delete_Objects_Tooltip", request.getLocale()) : getDisabledDueToInsufficientPermissionText(request);
	}

	private boolean isDeleteOriginalFilesEnabled(){
		return isContentObjectPathIsWriteable() && userCanEditContentObject;
	}

	private String getDeleteOriginalFilesTooltip(HttpServletRequest request){
		if (!isContentObjectPathIsWriteable())
		{
			return I18nUtils.getString("uc.actionMenu.Disabled_ReadOnly_Tooltip", request.getLocale());
		}

		return isDeleteOriginalFilesEnabled() ? I18nUtils.getString("uc.actionMenu.Delete_HiRes_Tooltip", request.getLocale()) : getDisabledDueToInsufficientPermissionText(request);
	}

	private boolean isDeleteAlbumEnabled(){
		return isContentObjectPathIsWriteable() && userCanDeleteCurrentAlbum;
	}

	private String getDeleteAlbumTooltip(HttpServletRequest request){
		if (!isContentObjectPathIsWriteable())
		{
			return I18nUtils.getString("uc.actionMenu.Disabled_ReadOnly_Tooltip", request.getLocale());
		}

		return isDeleteAlbumEnabled() ? I18nUtils.getString("uc.actionMenu.Delete_Album_Tooltip", request.getLocale()) : getDisabledDueToInsufficientPermissionText(request);
	}

	private boolean isSynchronizeEnabled(){
		return userCanSynchronize;
	}

	private String getSynchronizeTooltip(HttpServletRequest request){
		return isSynchronizeEnabled() ? I18nUtils.getString("uc.actionMenu.Synchronize_Tooltip", request.getLocale()) : getDisabledDueToInsufficientPermissionText(request);
	}

	/// <summary>
	/// Gets the album ID.
	/// </summary>
	/// <value>The album ID.</value>
	private long getAlbumId(){
		if (albumId == Long.MIN_VALUE){
			albumId = album.getId();
		}

		return albumId;
	}
	
	private AlbumBo getAlbum(){
		return album;
	}

	/// <summary>
	/// Gets the content object ID.
	/// </summary>
	/// <value>The content object ID.</value>
	private long getContentObjectId(){
		/*if (contentObjectId == Long.MIN_VALUE)
		{
			contentObjectId = GalleryPage.GetContentObjectId();
		}*/

		return contentObjectId;
	}

	//#endregion

	//#region Private Methods

	/// <summary>
	/// Gets the URL that links to the download objects page. The URL includes any tags or people that exist
	/// in the current URL.
	/// </summary>
	/// <returns>System.String.</returns>
	private String getDownloadObjectsUrl(HttpServletRequest request){
		String tag = Utils.getQueryStringParameterString(request, "tag");
		String people = Utils.getQueryStringParameterString(request, "people");
		String approval = Utils.getQueryStringParameterString(request, "approval");

		if (getAlbumId() > Long.MIN_VALUE){
			if (StringUtils.isBlank(tag) && StringUtils.isBlank(people) && StringUtils.isBlank(approval))
				return Utils.getUrl(request, ResourceId.cm_downloadobjects, "aid={0}", getAlbumId());

			if (StringUtils.isBlank(tag) && StringUtils.isBlank(people) && !StringUtils.isBlank(approval))
				return Utils.getUrl(request, ResourceId.cm_downloadobjects, "aid={0}&approval={1}", getAlbumId(), approval);

			if (StringUtils.isBlank(tag) && !StringUtils.isBlank(people) && !StringUtils.isBlank(approval))
				return Utils.getUrl(request, ResourceId.cm_downloadobjects, "aid={0}&approval={1}&people={2}", getAlbumId(), approval, people);

			if (!StringUtils.isBlank(tag) && !StringUtils.isBlank(people))
				return Utils.getUrl(request, ResourceId.cm_downloadobjects, "aid={0}&tag={1}&people={2}", getAlbumId(), tag, people);

			if (!StringUtils.isBlank(tag))
				return Utils.getUrl(request, ResourceId.cm_downloadobjects, "aid={0}&tag={1}", getAlbumId(), tag);

			if (!StringUtils.isBlank(approval))
				return Utils.getUrl(request, ResourceId.cm_downloadobjects, "aid={0}&approval={1}", getAlbumId(), approval);

			return Utils.getUrl(request, ResourceId.cm_downloadobjects, "aid={0}&people={1}", getAlbumId(), people);
		}

		if (!StringUtils.isBlank(tag) && !StringUtils.isBlank(people))
			return Utils.getUrl(request, ResourceId.cm_downloadobjects, "tag={0}&people={1}", tag, people);

		if (!StringUtils.isBlank(tag))
			return Utils.getUrl(request, ResourceId.cm_downloadobjects, "tag={0}", tag);

		if (!StringUtils.isBlank(approval))
			return Utils.getUrl(request, ResourceId.cm_downloadobjects, "approval={0}", approval);

		return Utils.getUrl(request, ResourceId.cm_downloadobjects, "people={0}", people);
	}

	//#endregion
}