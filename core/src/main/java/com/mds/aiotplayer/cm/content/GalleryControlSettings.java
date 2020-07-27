package com.mds.aiotplayer.cm.content;

import java.lang.reflect.Field;
import java.text.MessageFormat;

import org.apache.commons.lang3.ArrayUtils;

import com.mds.aiotplayer.common.utils.Reflections;
import com.mds.aiotplayer.core.SlideShowType;
import com.mds.aiotplayer.core.ViewMode;
import com.mds.aiotplayer.core.exception.ArgumentException;
import com.mds.aiotplayer.core.exception.ArgumentOutOfRangeException;
import com.mds.aiotplayer.cm.util.CMUtils;

/// <summary>
/// Represents a set of settings for a specific instance of a Gallery control.
/// </summary>
public class GalleryControlSettings{
	////#region Private Fields
	
	//#endregion
	
	//#region Constructors
	
	/// <summary>
	/// Initializes a new instance of the <see cref="GalleryControlSettings"/> class.
	/// </summary>
	/// <param name="id">The value that uniquely identifies the gallery control setting.</param>
	/// <param name="controlId">The value that uniquely identifies the Gallery control. This is a concatenation of the relative
	/// path to the control and its client ID. For example: "\default.aspx|mds"</param>
	public GalleryControlSettings(long id, String controlId){
	  this.galleryControlSettingId = id;
	  this.controlId = controlId;
	}
	
	//#endregion
	
	//#region Properties
	
	/// <summary>
	/// Gets or sets the ID for the gallery control setting.
	/// </summary>
	/// <value>The gallery control setting ID.</value>
	private long galleryControlSettingId;
	public long getGalleryControlSettingId() {
		return this.galleryControlSettingId;
	}
	
	public void setGalleryControlSettingId(long galleryControlSettingId) {
		this.galleryControlSettingId = galleryControlSettingId;
	}
	
	/// <summary>
	/// Gets or sets the value that uniquely identifies the Gallery control. This is a concatenation of the full physical
	/// path to the control and its client ID. For example: "~/Default.aspx|mds"
	/// </summary>
	/// <value>The value that uniquely identifies the Gallery control.</value>
	private String controlId;
	
	public String getControlId() {
		return this.controlId;
	}
	
	public void setControlId(String controlId) {
		this.controlId = controlId;
	}
	
	/// <summary>
	/// Gets or sets the ID of the gallery associated with the control.
	/// </summary>
	/// <value>The gallery ID.</value>
	private Long galleryId;
	
	public Long getGalleryId() {return this.galleryId;}
	
	public void setGalleryId(Long galleryId) {
		this.galleryId = galleryId;
	}	
	/// <summary>
	/// Gets or sets a value indicating whether to render the header at the top of the gallery. If not specified, the application
	/// uses <see cref="GallerySettings.ShowHeader"/>; when specified, this property overrides it. The header includes the
	/// gallery title, login/logout controls, and search function. The login/logout controls and search function can be individually
	/// controlled via the <see cref="ShowLogin"/> and <see cref="ShowSearch"/> properties.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the header is to be displayed; otherwise, <c>false</c>.
	/// </value>
	private Boolean showHeader;
	public Boolean getShowHeader() {return this.showHeader;}
	public void setShowHeader(Boolean showHeader) {this.showHeader = showHeader;}
	
	/// <summary>
	/// Gets or sets the header text that appears at the top of each web page. Requires that <see cref="GalleryControlSettings.ShowHeader" /> be set to
	/// <c>true</c> in order to be visible. If not specified, the application uses <see cref="GallerySettings.GalleryTitle" />;
	/// when specified, this property overrides it.
	/// </summary>
	/// <value>The gallery title.</value>
	private String galleryTitle;
	public String getGalleryTitle() {
		return this.galleryTitle;
	}
	
	public void setGalleryTitle(String galleryTitle) {
		this.galleryTitle = galleryTitle;
	}
	
	/// <summary>
	/// Gets or sets the URL the user will be directed to when she clicks the gallery title. Optional. If not 
	/// present, no link will be rendered. Examples: "http://www.mysite.com", "/" (the root of the web site),
	/// "~/" (the top level album). If not specified, the application uses <see cref="GallerySettings.GalleryTitleUrl" />;
	/// when specified, this property overrides it.
	/// </summary>
	/// <value>The gallery title URL.</value>
	private String galleryTitleUrl;
	public String getGalleryTitleUrl() {
		return this.galleryTitleUrl;
	}
	
	public void setGalleryTitleUrl(String galleryTitleUrl) {
		this.galleryTitleUrl = galleryTitleUrl;
	}
	
	/// <summary>
	/// Indicates whether to show the login controls at the top right of each page. When false, no login controls
	/// are shown, but the user can navigate directly to the login page to log on. If not specified, the application
	/// uses <see cref="GallerySettings.ShowLogin"/>; when specified, this property overrides it.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if login controls are visible; otherwise, <c>false</c>.
	/// </value>
	private Boolean showLogin;
	public Boolean getShowLogin() {return this.showLogin;}
	public void setShowLogin(Boolean showLogin) {this.showLogin = showLogin;}
	
	/// <summary>
	/// Indicates whether to show the search box at the top right of each page. If not specified, the application
	/// uses <see cref="GallerySettings.ShowSearch"/>; when specified, this property overrides it.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the search box is visible; otherwise, <c>false</c>.
	/// </value>
	private Boolean showSearch;
	public Boolean getShowSearch() {return this.showSearch;}
	public void setShowSearch(Boolean showSearch) {this.showSearch = showSearch;}
	
	/// <summary>
	/// Gets or sets the ID of the album to be displayed. This setting can be used to specify that a particular album be displayed. When
	/// specified, the <see cref="GalleryControlSettings.GalleryId" /> is ignored. Only one of these properties should be set: <see cref="GalleryControlSettings.GalleryId" />, 
	/// <see cref="GalleryControlSettings.AlbumId" />, <see cref="GalleryControlSettings.ContentObjectId" />.
	/// </summary>
	/// <value>The album ID.</value>
	private Long albumId;
	public Long getAlbumId() {return this.albumId;}
	
	public void setAlbumId(Long albumId) {
		this.albumId = albumId;
	}	
	
	/// <summary>
	/// Gets or sets the ID of the content object to be displayed. This setting can be used to specify that a particular content object be displayed. When
	/// specified, the <see cref="GalleryControlSettings.GalleryId" /> is ignored and the <see cref="GalleryControlSettings.ViewMode" /> is 
	/// automatically set to <see cref="MDS.Business.ViewMode.Single" />. Only one of these properties should be set: 
	/// <see cref="GalleryControlSettings.GalleryId" />, <see cref="GalleryControlSettings.AlbumId" />, <see cref="GalleryControlSettings.ContentObjectId" />.
	/// </summary>
	/// <value>The content object ID.</value>
	private Long contentObjectId;
	public Long getContentObjectId() {return this.contentObjectId;}
	
	public void setContentObjectId(Long contentObjectId) {
		this.contentObjectId = contentObjectId;
	}	
	
	/// <summary>
	/// Gets or sets a value indicating how the content objects are to be rendered in the browser. The default value is
	/// <see cref="MDS.Business.ViewMode.Multiple" />. When the value is <see cref="MDS.Business.ViewMode.Multiple" />,
	/// the contents of an album are shown as a set of thumbnail images. When set to <see cref="MDS.Business.ViewMode.Single" />, 
	/// a single content object is displayed. When set to <see cref="MDS.Business.ViewMode.SingleRandom" />, a single content object 
	/// is displayed that is randomly selected. When a <see cref="GalleryControlSettings.ContentObjectId" /> is specified, the 
	/// <see cref="GalleryControlSettings.ViewMode" /> is automatically set to <see cref="MDS.Business.ViewMode.Single" />.
	/// </summary>
	/// <value>A value indicating how the content objects are to be rendered in the browser.</value>
	private ViewMode viewMode;
	
	public ViewMode getViewMode() {return this.viewMode;}
	public void setViewMode(ViewMode viewMode) {this.viewMode = viewMode;}
	
	/// <summary>
	/// Gets or sets the base URL to invoke when a tree node is clicked.
	/// The album ID of the selected album is passed to the URL as the query String parameter "aid".
	/// Example: "Gallery.aspx, http://site.com/gallery.aspx"
	/// </summary>
	private String treeViewNavigateUrl;
	public String getTreeViewNavigateUrl() {
		return this.treeViewNavigateUrl;
	}
	
	public void setTreeViewNavigateUrl(String treeViewNavigateUrl) {
		this.treeViewNavigateUrl = treeViewNavigateUrl;
	}
	
	/// <summary>
	/// Gets or sets a value indicating whether users can view galleries without logging in. When false, users are redirected to a login
	/// page when any album is requested. Private albums are never shown to anonymous users, even when this property is true. If not 
	/// specified, the application uses <see cref="GallerySettings.AllowAnonymousBrowsing" />; when specified, this property overrides it.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if anonymous users can view the gallery; otherwise, <c>false</c>.
	/// </value>
	private Boolean allowAnonymousBrowsing;
	public Boolean getAllowAnonymousBrowsing() {return this.allowAnonymousBrowsing;}
	public void setAllowAnonymousBrowsing(Boolean allowAnonymousBrowsing) {this.allowAnonymousBrowsing = allowAnonymousBrowsing;}
	
	/// <summary>
	/// Gets or sets a value indicating whether to render the left pane when an album is being displayed.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the left pane is to be rendered; otherwise, <c>false</c>.
	/// </value>
	private Boolean showLeftPaneForAlbum;
	public Boolean getShowLeftPaneForAlbum() {return this.showLeftPaneForAlbum;}
	public void setShowLeftPaneForAlbum(Boolean showLeftPaneForAlbum) {this.showLeftPaneForAlbum = showLeftPaneForAlbum;}
	
	/// <summary>
	/// Gets or sets a value indicating whether to render the left pane when a single content object is being displayed.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the left pane is to be rendered when a single content object is being displayed; otherwise, <c>false</c>.
	/// </value>
	private Boolean showLeftPaneForContentObject;
	public Boolean getShowLeftPaneForContentObject() {return this.showLeftPaneForContentObject;}
	public void setShowLeftPaneForContentObject(Boolean showLeftPaneForContentObject) {this.showLeftPaneForContentObject = showLeftPaneForContentObject;}
	
	/// <summary>
	/// Gets or sets a value indicating whether to render the center pane of the user interface.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the center pane of the user interface is to be displayed; otherwise, <c>false</c>.
	/// </value>
	private Boolean showCenterPane;
	public Boolean getShowCenterPane() {return this.showCenterPane;}
	public void setShowCenterPane(Boolean showCenterPane) {this.showCenterPane = showCenterPane;}
	
	/// <summary>
	/// Gets or sets a value indicating whether to render the right pane of the user interface.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the right pane of the user interface is to be displayed; otherwise, <c>false</c>.
	/// </value>
	private Boolean showRightPane;
	public Boolean getShowRightPane() {return this.showRightPane;}
	public void setShowRightPane(Boolean showRightPane) {this.showRightPane = showRightPane;}
	
	/// <summary>
	/// Gets or sets a value indicating whether to render the Actions menu. If the currently logged on user does not have permission 
	/// to perform any of the items on the Actions menu, it is not displayed, even if this value is <c>true</c>.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the Actions menu is to be rendered; otherwise, <c>false</c>.
	/// </value>
	private Boolean showActionMenu;
	public Boolean getShowActionMenu() {return this.showActionMenu;}
	public void setShowActionMenu(Boolean showActionMenu) {this.showActionMenu = showActionMenu;}
	
	/// <summary>
	/// Gets or sets a value indicating whether to render the album bread crumb links, including the Actions menu.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the album bread crumb links are to be visible; otherwise, <c>false</c>.
	/// </value>
	private Boolean showAlbumBreadCrumb;
	public Boolean getShowAlbumBreadCrumb() {return this.showAlbumBreadCrumb;}
	public void setShowAlbumBreadCrumb(Boolean showAlbumBreadCrumb) {this.showAlbumBreadCrumb = showAlbumBreadCrumb;}
	
	/// <summary>
	/// Gets or sets a value indicating whether the toolbar is rendered above individual content objects.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the toolbar is rendered above individual content objects; otherwise, <c>false</c>.
	/// </value>
	private Boolean showContentObjectToolbar;
	public Boolean getShowContentObjectToolbar() {return this.showContentObjectToolbar;}
	public void setShowContentObjectToolbar(Boolean showContentObjectToolbar) {this.showContentObjectToolbar = showContentObjectToolbar;}
	
	/// <summary>
	/// Gets or sets a value indicating whether the title is displayed beneath individual content objects.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the title is displayed beneath individual content objects; otherwise, <c>false</c>.
	/// </value>
	private Boolean showContentObjectTitle;
	public Boolean getShowContentObjectTitle() {return this.showContentObjectTitle;}
	public void setShowContentObjectTitle(Boolean showContentObjectTitle) {this.showContentObjectTitle = showContentObjectTitle;}
	
	/// <summary>
	/// Gets or sets a value indicating whether the next and previous buttons are rendered for individual content objects.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the next and previous buttons are rendered for individual content objects; otherwise, <c>false</c>.
	/// </value>
	private Boolean showContentObjectNavigation;
	public Boolean getShowContentObjectNavigation() {return this.showContentObjectNavigation;}
	public void setShowContentObjectNavigation(Boolean showContentObjectNavigation) {this.showContentObjectNavigation = showContentObjectNavigation;}
	
	/// <summary>
	/// Gets or sets a value indicating whether to display the relative position of a content object within an album (example: (3 of 24)). 
	/// Applicable only when a single content object is displayed.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the relative position of a content object within an album is to be rendered; otherwise, <c>false</c>.
	/// </value>
	private Boolean showContentObjectIndexPosition;
	public Boolean getShowContentObjectIndexPosition() {return this.showContentObjectIndexPosition;}
	public void setShowContentObjectIndexPosition(Boolean showContentObjectIndexPosition) {this.showContentObjectIndexPosition = showContentObjectIndexPosition;}
	
	/// <summary>
	/// Gets or sets a value indicating whether the download links/embed button is visible above a content object. If not specified, the 
	/// application defaults to <c>true</c>; when specified, this property overrides it. When 
	/// <see cref="ShowContentObjectToolbar" />=<c>false</c>, this property is ignored.
	/// </summary>
	/// <value><c>true</c> if the download links/embed button is visible above a content object; otherwise, <c>false</c>.</value>
	private Boolean showUrlsButton;
	public Boolean getShowUrlsButton() {return this.showUrlsButton;}
	public void setShowUrlsButton(Boolean showUrlsButton) {this.showUrlsButton = showUrlsButton;}
	
	/// <summary>
	/// Gets or sets a value indicating whether the play/pause slide show button is visible above a content object. If not specified, the 
	/// application uses <see cref="GallerySettings.EnableSlideShow" />; when specified, this property overrides it. When 
	/// <see cref="GalleryControlSettings.ShowContentObjectToolbar" />=<c>false</c>, this property is ignored.
	/// </summary>
	/// <value><c>true</c> if the play/pause slide show button is visible above a content object; otherwise, <c>false</c>.</value>
	private Boolean showSlideShowButton;
	public Boolean getShowSlideShowButton() {return this.showSlideShowButton;}
	public void setShowSlideShowButton(Boolean showSlideShowButton) {this.showSlideShowButton = showSlideShowButton;}
	
	/// <summary>
	/// Gets or sets a value indicating whether the transfer content object button is visible above a content object. The button is not
	/// shown if the current user does not have permission to move content objects, even if this property is <c>true</c>. When 
	/// <see cref="GalleryControlSettings.ShowContentObjectToolbar" />=<c>false</c>, this property is ignored.
	/// </summary>
	/// <value><c>true</c> if the transfer content object button is visible above a content object; otherwise, <c>false</c>.</value>
	private Boolean showTransferContentObjectButton;
	public Boolean getShowTransferContentObjectButton() {return this.showTransferContentObjectButton;}
	public void setShowTransferContentObjectButton(Boolean showTransferContentObjectButton) {this.showTransferContentObjectButton = showTransferContentObjectButton;}
	
	/// <summary>
	/// Gets or sets a value indicating whether the copy content object button is visible above a content object. The button is not
	/// shown if the current user does not have permission to copy content objects, even if this property is <c>true</c>. When 
	/// <see cref="GalleryControlSettings.ShowContentObjectToolbar" />=<c>false</c>, this property is ignored.
	/// </summary>
	/// <value><c>true</c> if the copy content object button is visible above a content object; otherwise, <c>false</c>.</value>
	private Boolean showCopyContentObjectButton;
	public Boolean getShowCopyContentObjectButton() {return this.showCopyContentObjectButton;}
	public void setShowCopyContentObjectButton(Boolean showCopyContentObjectButton) {this.showCopyContentObjectButton = showCopyContentObjectButton;}
	
	/// <summary>
	/// Gets or sets a value indicating whether the rotate content object button is visible above a content object. The button is not
	/// shown if the current user does not have permission to rotate content objects, even if this property is <c>true</c>. When 
	/// <see cref="GalleryControlSettings.ShowContentObjectToolbar" />=<c>false</c>, this property is ignored.
	/// </summary>
	/// <value><c>true</c> if the rotate content object button is visible above a content object; otherwise, <c>false</c>.</value>
	private Boolean showRotateContentObjectButton;
	public Boolean getShowRotateContentObjectButton() {return this.showRotateContentObjectButton;}
	public void setShowRotateContentObjectButton(Boolean showRotateContentObjectButton) {this.showRotateContentObjectButton = showRotateContentObjectButton;}
	
	/// <summary>
	/// Gets or sets a value indicating whether the delete content object button is visible above a content object. The button is not
	/// shown if the current user does not have permission to delete content objects, even if this property is <c>true</c>. When 
	/// <see cref="GalleryControlSettings.ShowContentObjectToolbar" />=<c>false</c>, this property is ignored.
	/// </summary>
	/// <value><c>true</c> if the delete content object button is visible above a content object; otherwise, <c>false</c>.</value>
	private Boolean showDeleteContentObjectButton;
	public Boolean getShowDeleteContentObjectButton() {return this.showDeleteContentObjectButton;}
	public void setShowDeleteContentObjectButton(Boolean showDeleteContentObjectButton) {this.showDeleteContentObjectButton = showDeleteContentObjectButton;}
	
	/// <summary>
	/// Gets or sets a value indicating whether a slide show of content objects automatically starts playing when the page loads. The 
	/// default value is <c>false</c>. This setting applies only when the <see cref="GalleryControlSettings.ViewMode" /> is set to ViewMode.Single or ViewMode.SingleRandom
	/// and either an album or content object is specified (that is, the <see cref="GalleryControlSettings.AlbumId" /> or <see cref="GalleryControlSettings.ContentObjectId" /> is assigned a value). 
	/// If a content object is specified, all images in the object's album will be shown in the slide show.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if a slide show of content objects will automatically start playing; otherwise, <c>false</c>.
	/// </value>
	private Boolean autoPlaySlideShow;
	public Boolean getAutoPlaySlideShow() {return this.autoPlaySlideShow;}
	public void setAutoPlaySlideShow(Boolean autoPlaySlideShow) {this.autoPlaySlideShow = autoPlaySlideShow;}
	
	/// <summary>
	/// Gets or sets the type of the slide show. The default value is <see cref="MDS.Business.SlideShowType.FullScreen" />.
	/// </summary>
	/// <value>The type of the slide show.</value>
	private SlideShowType slideShowType;
	
	public SlideShowType getSlideShowType(){return this.slideShowType;}
	
	public void setSlideShowType(SlideShowType slideShowType) {this.slideShowType = slideShowType;}
	
	  /// <summary>
	/// Gets or sets a value indicating whether an album or content object specified in the URL can override the <see cref="GalleryControlSettings.GalleryId" />,
	/// <see cref="GalleryControlSettings.AlbumId" />, and <see cref="GalleryControlSettings.ContentObjectId" /> properties of this control. Use the query String parameter "aid" to 
	/// specify an album; use "moid" for a content object (example: default.aspx?aid=12 for album ID=12, default.aspx?moid=37 for media
	/// object ID=37)
	/// </summary>
	/// <value><c>true</c> if an album or content object specified in the query String can override one specified as a control property; otherwise,
	///  <c>false</c>.</value>
	private Boolean allowUrlOverride;
	public Boolean getAllowUrlOverride() {return this.allowUrlOverride;}
	public void setAllowUrlOverride(Boolean allowUrlOverride) {this.allowUrlOverride = allowUrlOverride;}
	
	//#endregion
	
	//#region Methods
	/// <summary>
    /// Persist the current gallery control settings to the data store.
    /// </summary>
    public void save() {
      CMUtils.saveGalleryControlSettings(this);

      // Clear the settings stored in static variables so they are retrieved from the data store during the next access.
      CMUtils.clearGalleryControlSettingsCache();
    }
    
	/// <summary>
	/// Delete the current gallery control settings from the data store.
	/// </summary>
	public void delete(){
	  // Set the view mode to ViewMode.NotSet, slide show type to SlideShowType.NotSet, and all nullable 
			// properties to null (except for the ControlId, which we need to identify the record to delete.
			// Then call save. This causes the matching records to get deleted from the data store.
	  this.viewMode = ViewMode.NotSet;
	  this.slideShowType = SlideShowType.NotSet;
	
	  String[] propertiesToExclude = new String[] { "ControlId" };
	
	  Field[] fs = this.getClass().getDeclaredFields();
	  for (Field prop : fs){
		if (ArrayUtils.indexOf(propertiesToExclude, prop.getName()) != ArrayUtils.INDEX_NOT_FOUND){
			continue; // Skip this one.
		}
		
		Reflections.setFieldValue(this, prop.getName(), null);
	
		//boolean isString = (prop.PropertyType == typeof(String));
		//boolean isNullableGeneric = (prop.PropertyType.IsGenericType && (prop.PropertyType.GetGenericTypeDefinition() == typeof(Nullable<>)));
	
		//if (isString || isNullableGeneric){
		//  prop.SetValue(this, null, null); // Set to null
		//}
	  }
	
	  CMUtils.saveGalleryControlSettings(this);
	}	
	/// <summary>
	/// Assigns the <paramref name="value" /> to the specified <paramref name="property" /> of the <paramref name="galleryControlSetting" />
	/// instance. The <paramref name="value" /> is converted to the appropriate enumeration before assignment.
	/// </summary>
	/// <param name="galleryControlSetting">The gallery control setting instance containing the <paramref name="property" /> to assign.</param>
	/// <param name="property">The property to assign the <paramref name="value" /> to.</param>
	/// <param name="value">The value to assign to the <paramref name="property" />.</param>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when <paramref name="value" /> cannot be parsed into a
	/// <see cref="ViewMode" /> value.</exception>
	private static void assignViewModeProperty(GalleryControlSettings galleryControlSetting, Field property, String value){
	  ViewMode viewMode;
	  try {
		  viewMode = com.mds.aiotplayer.core.ViewMode.valueOf(value);
	  } catch (ArgumentException ex) {
		  throw new ArgumentOutOfRangeException(MessageFormat.format("GalleryControlSettings.AssignViewModeProperty cannot convert the String {0} to a ViewMode enumeration value. The following values are valid: NotSet, Multiple, Single, SingleRandom", value), ex);
	  }
	
	  try {
			property.set(galleryControlSetting, viewMode);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/// <summary>
	/// Assigns the <paramref name="value" /> to the specified <paramref name="property" /> of the <paramref name="galleryControlSetting" />
	/// instance. The <paramref name="value" /> is converted to the appropriate enumeration before assignment.
	/// </summary>
	/// <param name="galleryControlSetting">The gallery control setting instance containing the <paramref name="property" /> to assign.</param>
	/// <param name="property">The property to assign the <paramref name="value" /> to.</param>
	/// <param name="value">The value to assign to the <paramref name="property" />.</param>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when <paramref name="value" /> cannot be parsed into a
		/// <see cref="SlideShowType" /> value.</exception>
	private static void assignSlideShowTypeProperty(GalleryControlSettings galleryControlSetting, Field property, String value){
		SlideShowType slideShowType;
	
		  try {
			  slideShowType = com.mds.aiotplayer.core.SlideShowType.valueOf(value);
			  
		  } catch (ArgumentException ex) {
			  throw new ArgumentOutOfRangeException(MessageFormat.format("GalleryControlSettings.AssignSlideShowTypeProperty cannot convert the String {0} to a SlideShowType enumeration value. The following values are valid: NotSet, Inline, FullScreen", value), ex);
		  }
		
		  try {
			property.set(galleryControlSetting, slideShowType);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//////#endregion
}

