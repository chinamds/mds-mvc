/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

/// <summary>
/// A client-optimized object that stores properties that affect the user experience.
/// </summary>
public class SettingsRest{
	public SettingsRest() {}
	/// <summary>
	/// Gets the gallery id.
	/// </summary>
	private long galleryId;
	@JsonProperty(value = "GalleryId")
	public long getGalleryId() {
		return this.galleryId;
	}
	
	public void setGalleryId(long galleryId) {
		this.galleryId = galleryId;
	}

	/// <summary>
	/// Gets the client ID for the current Gallery control. An HTML element having this ID will
	/// be present in the web page and can be used by javascript to scope all actions to the 
	/// intended control instance. Example: "mds_g"
	/// </summary>
	private String clientId;
	@JsonProperty(value = "ClientId")
	public String getClientId() {
		return this.clientId;
	}
	
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/// <summary>
	/// Gets the client ID for the DOM element that is to receive the contents of the media
	/// object. Ex: "mds_g_mediaHtml"
	/// </summary>
	private String contentClientId;
	@JsonProperty(value = "ContentClientId")
	public String getContentClientId() {
		return this.contentClientId;
	}
	
	public void setContentClientId(String contentClientId) {
		this.contentClientId = contentClientId;
	}

	/// <summary>
	/// Gets the name of the compiled jsRender template for the content object.
	/// Ex: "mds_g_media_tmpl"
	/// </summary>
	private String contentTmplName;
	@JsonProperty(value = "ContentTmplName")
	public String getContentTmplName() {
		return this.contentTmplName;
	}
	
	public void setContentTmplName(String contentTmplName) {
		this.contentTmplName = contentTmplName;
	}

	/// <summary>
	/// Gets the client ID for the DOM element that is to receive the contents of the gallery
	/// header. Ex: "mds_g_gHdrHtml"
	/// </summary>
	private String headerClientId;
	@JsonProperty(value = "HeaderClientId")
	public String getHeaderClientId() {
		return this.headerClientId;
	}
	
	public void setHeaderClientId(String headerClientId) {
		this.headerClientId = headerClientId;
	}

	/// <summary>
	/// Gets the name of the compiled jsRender template for the header. Ex: "mds_g_gallery_header_tmpl"
	/// </summary>
	private String headerTmplName;
	@JsonProperty(value = "HeaderTmplName")
	public String getHeaderTmplName() {
		return this.headerTmplName;
	}
	
	public void setHeaderTmplName(String headerTmplName) {
		this.headerTmplName = headerTmplName;
	}

	/// <summary>
	/// Gets the client ID for the DOM element that is to receive the contents of album thumbnail 
	/// images. Ex: "mds_g_thmbHtml"
	/// </summary>
	private String thumbnailClientId;
	@JsonProperty(value = "ThumbnailClientId")
	public String getThumbnailClientId() {
		return this.thumbnailClientId;
	}
	
	public void setThumbnailClientId(String thumbnailClientId) {
		this.thumbnailClientId = thumbnailClientId;
	}

	/// <summary>
	/// Gets the name of the compiled jsRender template for the album thumbnail images.
	/// Ex: "mds_g_thumbnail_tmpl"
	/// </summary>
	private String thumbnailTmplName;
	@JsonProperty(value = "ThumbnailTmplName")
	public String getThumbnailTmplName() {
		return this.thumbnailTmplName;
	}
	
	public void setThumbnailTmplName(String thumbnailTmplName) {
		this.thumbnailTmplName = thumbnailTmplName;
	}

	/// <summary>
	/// Gets the client ID for the DOM element that is to receive the contents of the left pane
	/// of the media view page. Ex: "mds_g_lpHtml"
	/// </summary>
	private String leftPaneClientId;
	@JsonProperty(value = "LeftPaneClientId")
	public String getLeftPaneClientId() {
		return this.leftPaneClientId;
	}
	
	public void setLeftPaneClientId(String leftPaneClientId) {
		this.leftPaneClientId = leftPaneClientId;
	}

	/// <summary>
	/// Gets the name of the compiled jsRender template for the left pane of the media view page.
	/// Ex: "mds_g_lp_tmpl"
	/// </summary>
	private String leftPaneTmplName;
	@JsonProperty(value = "LeftPaneTmplName")
	public String getLeftPaneTmplName() {
		return this.leftPaneTmplName;
	}
	
	public void setLeftPaneTmplName(String leftPaneTmplName) {
		this.leftPaneTmplName = leftPaneTmplName;
	}

	/// <summary>
	/// Gets the client ID for the DOM element that is to receive the contents of the right pane
	/// of the media view page. Ex: "mds_g_rpHtml"
	/// </summary>
	private String rightPaneClientId;
	@JsonProperty(value = "RightPaneClientId")
	public String getRightPaneClientId() {
		return this.rightPaneClientId;
	}
	
	public void setRightPaneClientId(String rightPaneClientId) {
		this.rightPaneClientId = rightPaneClientId;
	}

	/// <summary>
	/// Gets the name of the compiled jsRender template for the right pane of the media view page.
	/// Ex: "mds_g_rp_tmpl"
	/// </summary>
	private String rightPaneTmplName;
	@JsonProperty(value = "RightPaneTmplName")
	public String getRightPaneTmplName() {
		return this.rightPaneTmplName;
	}
	
	public void setRightPaneTmplName(String rightPaneTmplName) {
		this.rightPaneTmplName = rightPaneTmplName;
	}

	/// <summary>
	/// Gets a value indicating whether to show the header
	/// </summary>
	private boolean showHeader;

	/// <summary>
	/// Gets a value indicating whether show the login functionality.
	/// </summary>
	private boolean showLogin;

	/// <summary>
	/// Gets a value indicating whether show the search functionality.
	/// </summary>
	private boolean showSearch;

	/// <summary>
	/// Indicates whether anonymous users are allowed to create accounts.
	/// </summary>
	private boolean enableSelfRegistration;

	/// <summary>
	/// Indicates whether the user album feature is enabled.</summary>
	private boolean enableUserAlbum;

	/// <summary>
	/// Indicates whether to allow a logged-on user to manage their account.
	/// </summary>
	private boolean allowManageOwnAccount;

	/// <summary>
	/// Gets the header text that appears at the top of each web page.
	/// </summary>
	private String title;

	/// <summary>
	/// Gets the URL the header text links to.
	/// </summary>
	private String titleUrl;

	/// <summary>
	/// Gets the tooltip for the <see cref="TitleUrl" />.
	/// </summary>
	private String titleUrlTooltip;

	/// <summary>
	/// Gets a value indicating whether the title is displayed beneath individual content objects.
	/// </summary>
	private boolean showContentObjectTitle;

	/// <summary>
	/// Gets a value indicating whether the next and previous buttons are rendered for individual content objects.
	/// </summary>
	private boolean showContentObjectNavigation;

	/// <summary>
	/// Gets a value indicating whether to display the relative position of a content object within an album (example: (3 of 24)). 
	/// </summary>
	private boolean showContentObjectIndexPosition;

	/// <summary>
	/// Indicates the number of thumbnails to display at a time. A value of zero indicates paging
	/// is disabled (all items will be shown).
	/// </summary>
	private int pageSize;

	/// <summary>
	/// Gets or sets the location for the pager used to navigate thumbnails. Will be one of the 
	/// following values: Top, Bottom, TopAndBottom.
	/// </summary>
	private String pagerLocation;

	/// <summary>
	/// Specifies the visual transition effect to use when moving from one content object to another.
	/// </summary>
	private String transitionType;

	/// <summary>
	/// The duration of the transition effect, in milliseconds, when navigating between media 
	/// objects. This setting has no effect when <see cref="TransitionType" /> is null or empty.
	/// </summary>
	private int transitionDurationMs;

	/// <summary>
	/// Gets a value indicating whether the toolbar is rendered above individual content objects. 
	/// </summary>
	private boolean showContentObjectToolbar;

	/// <summary>
	/// Gets a value indicating whether the user is allowed to the content object.
	/// </summary>
	private boolean allowDownload;

	/// <summary>
	/// Gets a value indicating whether the user is allowed to download content objects and albums in a ZIP file. 
	/// </summary>
	private boolean allowZipDownload;

	/// <summary>
	/// Gets a value indicating whether the show urls button is visible above a content object. 
	/// </summary>
	private boolean showUrlsButton;

	/// <summary>
	/// Gets a value indicating whether the play/pause slide show button is visible above a media 
	/// object. When <see cref="ShowContentObjectToolbar" />=<c>false</c>, this property is ignored.
	/// </summary>
	private boolean showSlideShowButton;

	/// <summary>
	/// Gets a value indicating whether a slide show of image content objects automatically starts 
	/// playing when the page loads.
	/// </summary>
	private boolean slideShowIsRunning;

	/// <summary>
	/// Gets the type of the slide show. Example: "Inline", "FullScreen"
	/// </summary>
	private String slideShowType;

	/// <summary>
	/// The delay, in milliseconds, between images during a slide show.
	/// </summary>
	private int slideShowIntervalMs;

	/// <summary>
	/// Gets a value indicating whether the transfer content object button is visible above a media 
	/// object. When <see cref="ShowContentObjectToolbar" />=<c>false</c>, this property is ignored.
	/// </summary>
	private boolean showTransferContentObjectButton;

	/// <summary>
	/// Gets a value indicating whether the copy content object button is visible above a media 
	/// object. When <see cref="ShowContentObjectToolbar" />=<c>false</c>, this property is ignored.
	/// </summary>
	private boolean showCopyContentObjectButton;

	/// <summary>
	/// Gets a value indicating whether the rotate content object button is visible above a media 
	/// object. When <see cref="ShowContentObjectToolbar" />=<c>false</c>, this property is ignored.
	/// </summary>
	private boolean showRotateContentObjectButton;

	/// <summary>
	/// Gets a value indicating whether the delete content object button is visible above a media 
	/// object. When <see cref="ShowContentObjectToolbar" />=<c>false</c>, this property is ignored.
	/// </summary>
	private boolean showDeleteContentObjectButton;

	/// <summary>
	/// Maximum # of characters to display when showing the title of a thumbnail item.
	/// </summary>
	private int maxThmbTitleDisplayLength;

	/// <summary>
	/// Specifies whether anonymous users are allowed to rate gallery objects.
	/// </summary>
	private boolean allowAnonymousRating;

	/// <summary>
	/// Specifies whether anonymous users are allowed to browse the gallery.
	/// </summary>
	private boolean allowAnonBrowsing;

	/// <summary>
	/// Specifies whether the current gallery is read only. Will be true when 
	/// <see cref="GallerySettings.ContentObjectPathIsReadOnly" /> is <c>true</c>
	/// </summary>
	private boolean readOnlyGallery;
	
	@JsonProperty(value = "ShowHeader")
	public boolean isShowHeader() {
		return showHeader;
	}

	public void setShowHeader(boolean showHeader) {
		this.showHeader = showHeader;
	}

	@JsonProperty(value = "ShowLogin")
	public boolean isShowLogin() {
		return showLogin;
	}

	public void setShowLogin(boolean showLogin) {
		this.showLogin = showLogin;
	}

	@JsonProperty(value = "ShowSearch")
	public boolean isShowSearch() {
		return showSearch;
	}

	public void setShowSearch(boolean showSearch) {
		this.showSearch = showSearch;
	}

	@JsonProperty(value = "EnableSelfRegistration")
	public boolean isEnableSelfRegistration() {
		return enableSelfRegistration;
	}

	public void setEnableSelfRegistration(boolean enableSelfRegistration) {
		this.enableSelfRegistration = enableSelfRegistration;
	}

	@JsonProperty(value = "EnableUserAlbum")
	public boolean isEnableUserAlbum() {
		return enableUserAlbum;
	}

	public void setEnableUserAlbum(boolean enableUserAlbum) {
		this.enableUserAlbum = enableUserAlbum;
	}

	@JsonProperty(value = "AllowManageOwnAccount")
	public boolean isAllowManageOwnAccount() {
		return allowManageOwnAccount;
	}

	public void setAllowManageOwnAccount(boolean allowManageOwnAccount) {
		this.allowManageOwnAccount = allowManageOwnAccount;
	}

	@JsonProperty(value = "Title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@JsonProperty(value = "TitleUrl")
	public String getTitleUrl() {
		return titleUrl;
	}

	public void setTitleUrl(String titleUrl) {
		this.titleUrl = titleUrl;
	}

	@JsonProperty(value = "TitleUrlTooltip")
	public String getTitleUrlTooltip() {
		return titleUrlTooltip;
	}

	public void setTitleUrlTooltip(String titleUrlTooltip) {
		this.titleUrlTooltip = titleUrlTooltip;
	}

	@JsonProperty(value = "ShowContentObjectTitle")
	public boolean isShowContentObjectTitle() {
		return showContentObjectTitle;
	}

	public void setShowContentObjectTitle(boolean showContentObjectTitle) {
		this.showContentObjectTitle = showContentObjectTitle;
	}

	@JsonProperty(value = "ShowContentObjectNavigation")
	public boolean isShowContentObjectNavigation() {
		return showContentObjectNavigation;
	}

	public void setShowContentObjectNavigation(boolean showContentObjectNavigation) {
		this.showContentObjectNavigation = showContentObjectNavigation;
	}

	@JsonProperty(value = "ShowContentObjectIndexPosition")
	public boolean isShowContentObjectIndexPosition() {
		return showContentObjectIndexPosition;
	}

	public void setShowContentObjectIndexPosition(boolean showContentObjectIndexPosition) {
		this.showContentObjectIndexPosition = showContentObjectIndexPosition;
	}

	@JsonProperty(value = "PageSize")
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@JsonProperty(value = "PagerLocation")
	public String getPagerLocation() {
		return pagerLocation;
	}

	public void setPagerLocation(String pagerLocation) {
		this.pagerLocation = pagerLocation;
	}

	@JsonProperty(value = "TransitionType")
	public String getTransitionType() {
		return transitionType;
	}

	public void setTransitionType(String transitionType) {
		this.transitionType = transitionType;
	}

	@JsonProperty(value = "TransitionDurationMs")
	public int getTransitionDurationMs() {
		return transitionDurationMs;
	}

	public void setTransitionDurationMs(int transitionDurationMs) {
		this.transitionDurationMs = transitionDurationMs;
	}

	@JsonProperty(value = "ShowContentObjectToolbar")
	public boolean isShowContentObjectToolbar() {
		return showContentObjectToolbar;
	}

	public void setShowContentObjectToolbar(boolean showContentObjectToolbar) {
		this.showContentObjectToolbar = showContentObjectToolbar;
	}

	@JsonProperty(value = "AllowDownload")
	public boolean isAllowDownload() {
		return allowDownload;
	}

	public void setAllowDownload(boolean allowDownload) {
		this.allowDownload = allowDownload;
	}

	@JsonProperty(value = "AllowZipDownload")
	public boolean isAllowZipDownload() {
		return allowZipDownload;
	}

	public void setAllowZipDownload(boolean allowZipDownload) {
		this.allowZipDownload = allowZipDownload;
	}

	@JsonProperty(value = "ShowUrlsButton")
	public boolean isShowUrlsButton() {
		return showUrlsButton;
	}

	public void setShowUrlsButton(boolean showUrlsButton) {
		this.showUrlsButton = showUrlsButton;
	}

	@JsonProperty(value = "ShowSlideShowButton")
	public boolean isShowSlideShowButton() {
		return showSlideShowButton;
	}

	public void setShowSlideShowButton(boolean showSlideShowButton) {
		this.showSlideShowButton = showSlideShowButton;
	}

	@JsonProperty(value = "SlideShowIsRunning")
	public boolean isSlideShowIsRunning() {
		return slideShowIsRunning;
	}

	public void setSlideShowIsRunning(boolean slideShowIsRunning) {
		this.slideShowIsRunning = slideShowIsRunning;
	}

	@JsonProperty(value = "SlideShowType")
	public String getSlideShowType() {
		return slideShowType;
	}

	public void setSlideShowType(String slideShowType) {
		this.slideShowType = slideShowType;
	}

	@JsonProperty(value = "SlideShowIntervalMs")
	public int getSlideShowIntervalMs() {
		return slideShowIntervalMs;
	}

	public void setSlideShowIntervalMs(int slideShowIntervalMs) {
		this.slideShowIntervalMs = slideShowIntervalMs;
	}

	@JsonProperty(value = "ShowTransferContentObjectButton")
	public boolean isShowTransferContentObjectButton() {
		return showTransferContentObjectButton;
	}

	public void setShowTransferContentObjectButton(boolean showTransferContentObjectButton) {
		this.showTransferContentObjectButton = showTransferContentObjectButton;
	}

	@JsonProperty(value = "ShowCopyContentObjectButton")
	public boolean isShowCopyContentObjectButton() {
		return showCopyContentObjectButton;
	}

	public void setShowCopyContentObjectButton(boolean showCopyContentObjectButton) {
		this.showCopyContentObjectButton = showCopyContentObjectButton;
	}

	@JsonProperty(value = "ShowRotateContentObjectButton")
	public boolean isShowRotateContentObjectButton() {
		return showRotateContentObjectButton;
	}

	public void setShowRotateContentObjectButton(boolean showRotateContentObjectButton) {
		this.showRotateContentObjectButton = showRotateContentObjectButton;
	}

	@JsonProperty(value = "ShowDeleteContentObjectButton")
	public boolean isShowDeleteContentObjectButton() {
		return showDeleteContentObjectButton;
	}

	public void setShowDeleteContentObjectButton(boolean showDeleteContentObjectButton) {
		this.showDeleteContentObjectButton = showDeleteContentObjectButton;
	}

	@JsonProperty(value = "MaxThmbTitleDisplayLength")
	public int getMaxThmbTitleDisplayLength() {
		return maxThmbTitleDisplayLength;
	}

	public void setMaxThmbTitleDisplayLength(int maxThmbTitleDisplayLength) {
		this.maxThmbTitleDisplayLength = maxThmbTitleDisplayLength;
	}

	@JsonProperty(value = "AllowAnonymousRating")
	public boolean isAllowAnonymousRating() {
		return allowAnonymousRating;
	}

	public void setAllowAnonymousRating(boolean allowAnonymousRating) {
		this.allowAnonymousRating = allowAnonymousRating;
	}

	@JsonProperty(value = "AllowAnonBrowsing")
	public boolean isAllowAnonBrowsing() {
		return allowAnonBrowsing;
	}

	public void setAllowAnonBrowsing(boolean allowAnonBrowsing) {
		this.allowAnonBrowsing = allowAnonBrowsing;
	}

	@JsonProperty(value = "IsReadOnlyGallery")
	public boolean isReadOnlyGallery() {
		return readOnlyGallery;
	}

	public void setReadOnlyGallery(boolean readOnlyGallery) {
		this.readOnlyGallery = readOnlyGallery;
	}
}