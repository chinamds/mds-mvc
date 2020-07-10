package com.mds.cm.content;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.mds.cm.exception.CannotReadFromDirectoryException;
import com.mds.cm.exception.CannotWriteToDirectoryException;
import com.mds.cm.exception.DirectoryNotFoundException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.metadata.MetadataDefinitionCollection;
import com.mds.core.exception.ArgumentException;
import com.mds.core.exception.ArgumentOutOfRangeException;
import com.mds.core.exception.BusinessException;
import com.mds.core.ApprovalSwitch;
import com.mds.core.ContentAlignment;
import com.mds.core.ContentObjectTransitionType;
import com.mds.core.EmailServerType;
import com.mds.core.exception.InvalidEnumArgumentException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.core.MetadataItemName;
import com.mds.core.PagerPosition;
import com.mds.core.SlideShowType;
import com.mds.core.StringCollection;
import com.mds.sys.util.AppSettings;
import com.mds.sys.util.UserAccountCollection;
import com.mds.cm.util.CMUtils;
import com.mds.cm.util.GalleryUtils;
import com.mds.common.utils.Reflections;
import com.mds.util.HelperFunctions;

/// <summary>
	/// Represents a set of gallery-specific settings.
	/// </summary>
public class GallerySettings implements Comparable<GallerySettings>{
	//#region Private Fields

	private long galleryId;
	private String contentObjectPath;
	private String contentAndSettingCenter;
	private String thumbnailPath;
	private String optimizedPath;
	private boolean contentObjectPathIsReadOnly;
	private boolean showHeader;
	private String galleryTitle;
	private String galleryTitleUrl;
	private boolean showLogin;
	private boolean showSearch;
	private boolean showErrorDetails;
	private boolean enableExceptionHandler;
	private int defaultAlbumDirectoryNameLength;
	private boolean synchAlbumTitleAndDirectoryName;
	private String emptyAlbumThumbnailBackgroundColor;
	private String emptyAlbumThumbnailText;
	private String emptyAlbumThumbnailFontName;
	private int emptyAlbumThumbnailFontSize;
	private String emptyAlbumThumbnailFontColor;
	private float emptyAlbumThumbnailWidthToHeightRatio;
	private int maxThumbnailTitleDisplayLength;
	private MetadataDefinitionCollection metadataDisplaySettings = new MetadataDefinitionCollection();
	private boolean allowUserEnteredHtml;
	private boolean allowUserEnteredJavascript;
	private String[] allowedHtmlTags;
	private String[] allowedHtmlAttributes;
	private boolean allowCopyingReadOnlyObjects;
	private boolean allowManageOwnAccount;
	private boolean allowDeleteOwnAccount;
	private ContentObjectTransitionType contentObjectTransitionType;
	private float contentObjectTransitionDuration;
	private int slideshowInterval;
	private boolean allowUnspecifiedMimeTypes;
	private String[] imageTypesStandardBrowsersCanDisplay;
	private String[] imageMagickFileTypes;
	private boolean enableAnonymousOriginalContentObjectDownload;
	private boolean extractMetadata;
	private boolean extractMetadataUsingWpf;
	private boolean enableContentObjectDownload;
	private boolean enableContentObjectZipDownload;
	private boolean enableAlbumZipDownload;
	private boolean enableSlideShow;
	private int maxThumbnailLength;
	private int thumbnailImageJpegQuality;
	private String thumbnailFileNamePrefix;
	private int maxOptimizedLength;
	private int optimizedImageJpegQuality;
	private int optimizedImageTriggerSizeKb;
	private String optimizedFileNamePrefix;
	private int originalImageJpegQuality;
	private boolean discardOriginalImageDuringImport;
	private boolean applyWatermark;
	private boolean applyWatermarkToThumbnails;
	private String watermarkText;
	private String watermarkTextFontName;
	private int watermarkTextFontSize;
	private int watermarkTextWidthPercent;
	private String watermarkTextColor;
	private int watermarkTextOpacityPercent;
	private ContentAlignment watermarkTextLocation;
	private String watermarkImagePath;
	private int watermarkImageWidthPercent;
	private int watermarkImageOpacityPercent;
	private ContentAlignment watermarkImageLocation;
	private boolean sendEmailOnError;
	private boolean autoStartContentObject;
	private int defaultVideoPlayerWidth;
	private int defaultVideoPlayerHeight;
	private int defaultAudioPlayerWidth;
	private int defaultAudioPlayerHeight;
	private int defaultGenericObjectWidth;
	private int defaultGenericObjectHeight;
	private int maxUploadSize;
	private boolean allowAddLocalContent;
	private boolean allowAddExternalContent;
	private boolean allowAnonymousBrowsing;
	private int pageSize;
	private PagerPosition pagerLocation;
	private boolean enableSelfRegistration;
	private boolean requireEmailValidationForSelfRegisteredUser;
	private boolean requireApprovalForSelfRegisteredUser;
	private boolean useEmailForAccountName;
	private String[] defaultRolesForSelfRegisteredUser;
/*	private IUserAccountCollection usersToNotifyWhenAccountIsCreated = new UserAccountCollection();*/
	private UserAccountCollection usersToNotifyWhenErrorOccurs = new UserAccountCollection();
	private boolean enableUserAlbum;
	private boolean enableUserAlbumDefaultForUser;
	private int userAlbumParentAlbumId;
	private String userAlbumNameTemplate;
	private String userAlbumSummaryTemplate;
	private boolean redirectToUserAlbumAfterLogin;
	private int videoThumbnailPosition;
	private boolean enableAutoSync;
	private int autoSyncIntervalMinutes;
	private Date lastAutoSync;
	private boolean enableRemoteSync;
	private String remoteSyncPassword;
	private ContentEncoderSettingsCollection contentEncoderSettings = new ContentEncoderSettingsCollection();
	private int contentEncoderTimeoutMs;

	private String fullContentObjectPath;
	private String fullThumbnailPath;
	private String fullOptimizedPath;
	
	private Integer approvalSwitch;
	private Boolean enableVerificationCode;
	private Boolean usePdfRenderer;
	
	private String emailFromName;
	private String emailFromAddress;
	private String emailPassword;
	private EmailServerType emailServerType;
	private String smtpServer;
	private String smtpDomail;
	private String smtpServerPort;
	private boolean sendEmailUsingSsl;
	
	private boolean isInitialized;
	private boolean isTemplate;
	private boolean isWritable;
	private StringCollection verifiedFilePaths = new StringCollection();

	//#endregion

	//#region Constructors

	public GallerySettings(long galleryId, boolean isTemplate)	{
		this.galleryId = galleryId;
		this.isTemplate = isTemplate;
		
		addGalleryCreatedListener(new GalleryUtils());
	}

	//#endregion
	
	//#region Events

	/// <summary>
	/// Occurs immediately after the gallery settings are persisted to the data store.
	/// </summary>
	//public static event EventHandler<GallerySettingsEventArgs> GallerySettingsSaved;
	private List<GalleryCreatedListener> listeners = new ArrayList<>();

    public void addGalleryCreatedListener(GalleryCreatedListener listener) {
        listeners.add(listener);
    }

    public void removeGalleryCreatedListener(GalleryCreatedListener listener) {
        listeners.remove(listener);
    }
	//#endregion

	//#region Public Properties

	/// <summary>
	/// Gets or sets the ID for the gallery.
	/// </summary>
	/// <value>The gallery ID.</value>
	public long getGalleryId()	{
		return this.galleryId;
	}
	
	public void setGalleryId(long galleryId)	{
		this.galleryId = galleryId;
	}

	/// <summary>
	/// Gets a value indicating whether the gallery settings have been populated with data for the current gallery.
	/// This library is initialized by calling <see cref="Initialize"/>.
	/// </summary>
	/// <value></value>
	public boolean getIsInitialized(){
		return this.isInitialized;
	}

	/// <summary>
	/// Gets a value indicating whether the gallery settings are the template settings used to populate the settings
	/// of new galleries.
	/// </summary>
	public boolean getIsTemplate()	{
		return this.isTemplate;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the current instance can be modified. Objects that are stored in a cache must
	/// be treated as read-only. Only objects that are instantiated right from the database and not shared across threads
	/// should be updated.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if this instance can be modified; otherwise, <c>false</c>.
	/// </value>
	public boolean getIsWritable(){
		return this.isWritable;
	}
	
	public void setIsWritable(boolean isWritable){
		this.isWritable = isWritable;
	}

	/// <summary>
	/// Gets or sets the content object path. The path may be relative to the root of the web application
	/// (e.g. \ds\contentobjects), a full path to a local resource (e.g. C:\mymedia), or a UNC path to a local or network
	/// resource (e.g. \\mynas\media). Mapped drives present a security risk and are not supported. The initial and
	/// trailing slashes are	optional. For relative paths, the directory separator character can be either a forward
	/// or backward slash. Use the property <see cref="FullContentObjectPath"/> to retrieve the full physical path
	/// (such as "C:\inetpub\wwwroot\MDS\contentobjects").
	/// </summary>
	/// <value>The content object path.</value>
	/// <remarks>The path is returned exactly how it appears in the configuration setting.</remarks>
	public String getContentObjectPath(){
		return this.contentObjectPath;
	}
	
	public void setContentObjectPath(String contentObjectPath){
		this.contentObjectPath = contentObjectPath;
	}

	/// <summary>
	/// Gets or sets the contents and settings center path. The path may be relative to the root of the web application
	/// (e.g. \MDSServer7\Contents and Settings Center), a full path to a local resource (e.g. D:\MDSServer7\Contents and Settings Center), or a UNC path to a local or network
	/// resource (e.g. \\192.168.0.180\Contents and Settings Center). Mapped drives present a security risk and are not supported. The initial and
	/// trailing slashes are	optional. For relative paths, the directory separator character can be either a forward
	/// or backward slash. Use the property <see cref="MDSDataCenterPath"/> to retrieve the full physical path
	/// (such as "D:\MDSServer7\Contents and Settings Center").
	/// </summary>
	/// <value>The data center path.</value>
	/// <remarks>The path is returned exactly how it appears in the configuration setting.</remarks>
	public String getContentAndSettingCenter()	{
		return this.contentAndSettingCenter;
	}
	
	public void setContentAndSettingCenter(String contentAndSettingCenter)	{
		this.contentAndSettingCenter = contentAndSettingCenter;
	}

	/// <summary>
	/// Gets or sets the path to a directory where MDS System stores the thumbnail images of content objects. If
	/// this path is empty, the directory containing the original content object is used to store the thumbnail image.
	/// The path may be relative to the root of the web application (e.g. \ds\contentobjects), a full path to a local
	/// resource (e.g. C:\mymedia), or a UNC path to a local or network resource (e.g. \\mynas\media). Mapped
	/// drives present a security risk and are not supported. The initial and trailing slashes are	optional.
	/// For relative paths, the directory separator character can be either a forward or backward slash. Use the
	/// property <see cref="FullThumbnailPath"/> to retrieve the full physical path
	/// (such as "C:\inetpub\wwwroot\MDS\contentobjects").
	/// </summary>
	/// <value>
	/// The path to a directory where MDS System stores the thumbnail images of content objects.
	/// </value>
	public String getThumbnailPath(){
		return this.thumbnailPath;
	}
	
	public void setThumbnailPath(String thumbnailPath){
		this.thumbnailPath = thumbnailPath;
	}

	/// <summary>
	/// Gets or sets the path to a directory where MDS System stores the optimized images of content objects. If
	/// this path is empty, the directory containing the original content object is used to store the optimized image.
	/// The path may be relative to the root of the web application (e.g. \ds\contentobjects), a full path to a local
	/// resource (e.g. C:\mymedia), or a UNC path to a local or network resource (e.g. \\mynas\media). Mapped
	/// drives present a security risk and are not supported. The initial and trailing slashes are	optional.
	/// For relative paths, the directory separator character can be either a forward or backward slash.
	/// Not applicable for non-image content objects. Use the property <see cref="FullOptimizedPath"/> to retrieve
	/// the full physical path (such as "C:\inetpub\wwwroot\MDS\contentobjects").
	/// </summary>
	/// <value>
	/// The path to a directory where MDS System stores the optimized images of content objects.
	/// </value>
	public String getOptimizedPath(){
		return this.optimizedPath;
	}
	
	public void setOptimizedPath(String optimizedPath){
		this.optimizedPath = optimizedPath;
	}

	/// <summary>
	/// Specifies that the directory containing the content objects should never be written to by MDS System.
	/// This is useful when configuring the gallery to expose an existing media library and the administrator will not
	/// add, move, or copy objects using the MDS System UI. Objects can be added or removed to the gallery
	/// only by the synchronize function. Functions that do not require modifying the original files are still
	/// available, such as editing titles and captions, rearranging items, and the security system. Configuring
	/// a read-only gallery requires setting the thumbnail and optimized paths to a different directory, disabling
	/// user albums (<see cref="EnableUserAlbum"/>), and disabling the album title / directory name synchronization
	/// setting (<see cref="SynchAlbumTitleAndDirectoryName"/>). This class does not enforce these business rules;
	/// validation must be performed by the caller.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the content objects directory is read-only; <c>false</c> if it can be written to.
	/// </value>
	public boolean getContentObjectPathIsReadOnly()	{
		return this.contentObjectPathIsReadOnly;
	}
	
	public void setContentObjectPathIsReadOnly(boolean contentObjectPathIsReadOnly)	{
		this.contentObjectPathIsReadOnly = contentObjectPathIsReadOnly;
	}

	/// <summary>
	/// Gets or sets a value indicating whether to render the header at the top of the gallery. The default value is <c>true</c>. 
	/// The header includes the gallery title, login/logout controls, and search function. The login/logout controls 
	/// and search function can be individually controlled via the <see cref="IGallerySettings.ShowLogin" /> and <see cref="IGallerySettings.ShowSearch" /> properties.
	/// When <c>false</c>, the controls within the header are not shown, even if individually they are set to be visible
	/// (e.g. ShowSearch=<c>true</c>, ShowLogin=<c>true</c>).
	/// </summary>
	/// <value><c>true</c> if the header is to be dislayed; otherwise, <c>false</c>.</value>
	public boolean getShowHeader()	{
		return this.showHeader;
	}
	
	public void setShowHeader(boolean showHeader)	{
		this.showHeader = showHeader;
	}

	/// <summary>
	/// Gets or sets the header text that appears at the top of each web page. Requires that <see cref="ShowHeader"/> be set to
	/// <c>true</c> in order to be visible.
	/// </summary>
	/// <value>The gallery title.</value>
	public String getGalleryTitle()	{
		return this.galleryTitle;
	}
	
	public void setGalleryTitle(String galleryTitle)	{
		this.galleryTitle = galleryTitle;
	}

	/// <summary>
	/// Gets or sets the URL the user will be directed to when she clicks the gallery title. Optional. If not 
	/// present, no link will be rendered. Examples: "http://www.mysite.com", "/" (the root of the web site),
	/// "~/" (the top level album).
	/// </summary>
	/// <value>The gallery title URL.</value>
	public String getGalleryTitleUrl()	{
		return this.galleryTitleUrl;
	}
	
	public void setGalleryTitleUrl(String galleryTitleUrl)	{
		this.galleryTitleUrl = galleryTitleUrl;
	}

	/// <summary>
	/// Indicates whether to show the login controls at the top right of each page. When false, no login controls
	/// are shown, but the user can navigate directly to the login page to log on. Requires that <see cref="ShowHeader"/>
	/// be set to <c>true</c> in order to be visible.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if login controls are visible; otherwise, <c>false</c>.
	/// </value>
	public boolean getShowLogin(){
		return this.showLogin;
	}
	
	public void setShowLogin(boolean showLogin){
		this.showLogin = showLogin;
	}

	/// <summary>
	/// Indicates whether to show the search box at the top right of each page. Requires that <see cref="ShowHeader"/>
	/// be set to <c>true</c> in order to be visible.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the search box is visible; otherwise, <c>false</c>.
	/// </value>
	public boolean getShowSearch()	{
		return this.showSearch;
	}
	
	public void setShowSearch(boolean showSearch)	{
		this.showSearch = showSearch;
	}

	/// <summary>
	/// Indicates whether to show the full details of any unhandled exception that occurs within the gallery. This can reveal
	/// sensitive information to the user, so it should only be used for debugging purposes. When false, a generic error 
	/// message is given to the user. This setting has no effect when enableExceptionHandler="false".
	/// </summary>
	/// <value><c>true</c> if error details are displayed in the browser; <c>false</c> if a generic error message is displayed.</value>
	public boolean getShowErrorDetails(){
		return this.showErrorDetails;
	}
	
	public void setShowErrorDetails(boolean showErrorDetails){
		this.showErrorDetails = showErrorDetails;
	}

	/// <summary>
	/// Indicates whether to use MDS System's internal exception handling mechanism. When true, unhandled exceptions
	/// are transferred to a custom error page and, if showErrorDetails="true", details about the error are displayed to the
	/// user. When false, the error is recorded and the exception is rethrown, allowing application-level error handling to
	/// handle it. This may include code in global.asax. The customErrors element in web.config may be used to manage error
	/// handling when this setting is false (the customErrors setting is ignored when this value is true).
	/// </summary>
	/// <value><c>true</c> if MDS System's internal exception handling mechanism manages unhandled exceptions; 
	/// <c>false</c> if unhandled exceptions are allowed to propagate to the parent application, allowing for application
	/// level error handling code to manage the error.</value>
	public boolean getEnableExceptionHandler()	{
		return this.enableExceptionHandler;
	}
	
	public void setEnableExceptionHandler(boolean enableExceptionHandler)	{
		this.enableExceptionHandler = enableExceptionHandler;
	}

	/// <summary>
	/// The maximum length of directory name when a user creates an album. By default, directory names are the same as the
	/// album's title, but are truncated when the title is longer than the value specified here.
	/// </summary>
	public int getDefaultAlbumDirectoryNameLength()	{
		return this.defaultAlbumDirectoryNameLength;
	}
	
	public void setDefaultAlbumDirectoryNameLength(int defaultAlbumDirectoryNameLength)	{
		this.defaultAlbumDirectoryNameLength = defaultAlbumDirectoryNameLength;
	}

	/// <summary>
	/// Indicates whether to update the directory name corresponding to an album when the album's title is changed. When 
	/// true, modifying the title of an album causes the directory name to change to the same value. If the 
	/// title is longer than the value specified in DefaultAlbumDirectoryNameLength, the directory name is truncated. You 
	/// may want to set this to false if you have a directory structure that you do not want MDS System to alter. 
	/// Note that even if this setting is false, directories will still be moved or copied when the user moves or copies
	/// an album. Also, MDS System always modifies the directory name when it is necessary to 
	/// make it unique within a parent directory. For example, this may happen if you give two sibling albums the same title 
	/// or you move/copy an album into a directory containing another album with the same name.
	/// </summary>
	public boolean getSynchAlbumTitleAndDirectoryName()	{
		return this.synchAlbumTitleAndDirectoryName;
	}
	
	public void setSynchAlbumTitleAndDirectoryName(boolean synchAlbumTitleAndDirectoryName)	{
		this.synchAlbumTitleAndDirectoryName = synchAlbumTitleAndDirectoryName;
	}

	/// <summary>
	/// Gets or sets the metadata property to sort albums by. This value is assigned to the <see cref="IAlbum.SortByMetaName" />
	/// property when an album is created.
	/// </summary>
	/// <value>The metadata property to sort albums by.</value>
	public MetadataItemName getDefaultAlbumSortMetaName() { 
		return this.defaultAlbumSortMetaName;
	}
	
	public void setDefaultAlbumSortMetaName(MetadataItemName defaultAlbumSortMetaName) { 
		this.defaultAlbumSortMetaName = defaultAlbumSortMetaName;
	}
	
	private MetadataItemName defaultAlbumSortMetaName;
	private boolean defaultAlbumSortAscending;

	/// <summary>
	/// Gets or sets a value indicating whether an album's default sort order is ascending. A <c>false</c> value indicates
	/// a descending sort.
	/// </summary>
	/// <value><c>true</c> if an album is sorted in ascending order by default; <c>false</c> if descending order.</value>
	public boolean getDefaultAlbumSortAscending() {
		return this.defaultAlbumSortAscending;
	}
	
	public void setDefaultAlbumSortAscending(boolean defaultAlbumSortAscending) {
		this.defaultAlbumSortAscending = defaultAlbumSortAscending;
	}

	/// <summary>
	/// The color used for the background of the GIF image generated by MDS System when creating a default
	/// thumbnail image for a newly created album or an album without any objects. The color can be specified as
	/// hex (e.g. #336699), RGB (e.g. 127,55,95), or one of the System.Color.KnownColor enum values (e.g. Maroon).
	/// </summary>
	public String getEmptyAlbumThumbnailBackgroundColor(){
		return this.emptyAlbumThumbnailBackgroundColor;
	}
	
	public void setEmptyAlbumThumbnailBackgroundColor(String emptyAlbumThumbnailBackgroundColor){
		this.emptyAlbumThumbnailBackgroundColor = emptyAlbumThumbnailBackgroundColor;
	}

	/// <summary>
	/// The default text written on the GIF image generated by MDS System when creating a default thumbnail image 
	/// for a newly created album or an album without any objects. The GIF is 
	/// dynamically generated by the application when it is needed and is never actually stored on the hard drive.
	/// </summary>
	public String getEmptyAlbumThumbnailText()	{
		return this.emptyAlbumThumbnailText;
	}
	
	public void setEmptyAlbumThumbnailText(String emptyAlbumThumbnailText)	{
		this.emptyAlbumThumbnailText = emptyAlbumThumbnailText;
	}

	/// <summary>
	/// The font used for text written on the GIF image generated by MDS System when creating a default
	/// thumbnail image for a newly created album or an album without any objects. The font must be installed on 
	/// the web server. If the font is not installed, a generic sans serif font will be substituted.
	/// </summary>
	public String getEmptyAlbumThumbnailFontName()	{
		return this.emptyAlbumThumbnailFontName;
	}
	
	public void setEmptyAlbumThumbnailFontName(String emptyAlbumThumbnailFontName)	{
		this.emptyAlbumThumbnailFontName = emptyAlbumThumbnailFontName;
	}

	/// <summary>
	/// The size, in pixels, of the font used for text written on the GIF image generated by MDS System when 
	/// creating a default thumbnail image for a newly created album or an album without any objects. 
	/// </summary>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when setting the value to a number outside the acceptable
	/// range of 6 to 100.</exception>
	public int getEmptyAlbumThumbnailFontSize()	{
		return this.emptyAlbumThumbnailFontSize;
	}
	
	public void setEmptyAlbumThumbnailFontSize(int emptyAlbumThumbnailFontSize)	{
		if (emptyAlbumThumbnailFontSize < 6 || emptyAlbumThumbnailFontSize > 100)
		{
			throw new ArgumentOutOfRangeException("value", MessageFormat.format("Invalid EmptyAlbumThumbnailFontSize setting: The value must be between 6 and 100. Instead, the value was {0}.", emptyAlbumThumbnailFontSize));
		}

		this.emptyAlbumThumbnailFontSize = emptyAlbumThumbnailFontSize;
	}

	/// <summary>
	/// The color of the text specified in property EmptyAlbumThumbnailText. The color can be specified as
	/// hex (e.g. #336699), RGB (e.g. 127,55,95), or one of the System.Color.KnownColor enum values (e.g. Maroon).
	/// </summary>
	public String getEmptyAlbumThumbnailFontColor()	{
		return this.emptyAlbumThumbnailFontColor;
	}
	
	public void setEmptyAlbumThumbnailFontColor(String emptyAlbumThumbnailFontColor)	{
		this.emptyAlbumThumbnailFontColor = emptyAlbumThumbnailFontColor;
	}

	/// <summary>
	/// The ratio of the width to height of the default thumbnail image for an album that does not have a thumbnail
	/// image specified. The length of the longest side of the image is set by the MaxThumbnailLength property, and the
	/// length of the remaining side is calculated using this ratio. A ratio or more than 1.00 results in the width
	/// being greater than the height (landscape), while a ratio less than 1.00 results in the width being less
	/// than the height (portrait). Example: If MaxThumbnailLength = 115 and EmptyAlbumThumbnailWidthToHeightRatio = 1.50,
	/// the width of the default thumbnail image is 115 and the height is 77 (115 / 1.50). Value must be greater
	/// than zero. 
	/// </summary>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when setting the value to a number less than or equal
	/// to zero.</exception>
	public float getEmptyAlbumThumbnailWidthToHeightRatio()	{
		return this.emptyAlbumThumbnailWidthToHeightRatio;
	}
	
	
	public void setEmptyAlbumThumbnailWidthToHeightRatio(float emptyAlbumThumbnailWidthToHeightRatio)	{
		if (emptyAlbumThumbnailWidthToHeightRatio <= 0)
		{
			throw new ArgumentOutOfRangeException("value", MessageFormat.format("Invalid EmptyAlbumThumbnailWidthToHeightRatio setting: The value must be greater than zero. Instead, the value was {0}.", emptyAlbumThumbnailWidthToHeightRatio));
		}

		this.emptyAlbumThumbnailWidthToHeightRatio = emptyAlbumThumbnailWidthToHeightRatio;
	}

	/// <summary>
	/// Maximum # of characters to display when showing the title of an album or content object in a thumbnail view.
	/// </summary>
	/// <value>The display length of the max thumbnail title.</value>
	public int getMaxThumbnailTitleDisplayLength()	{
		return this.maxThumbnailTitleDisplayLength;
	}
	
	public void setMaxThumbnailTitleDisplayLength(int maxThumbnailTitleDisplayLength)	{
		this.maxThumbnailTitleDisplayLength = maxThumbnailTitleDisplayLength;
	}

	/// <summary>
	/// Indicates whether HTML is allowed in user-entered text such as titles, captions, and external content objects.
	/// When true, the HTML tags specified in <see cref="IGallerySettings.AllowedHtmlTags"/> and the attributes in
	/// <see cref="IGallerySettings.AllowedHtmlAttributes"/> are allowed. Invalid tags are automatically removed from user
	/// input. This setting does not affect how javascript is treated; refer to <see cref="IGallerySettings.AllowUserEnteredJavascript"/>.
	/// If this value is changed from true to false, existing objects will not be immediately purged of all HTML
	/// tags. Instead, individual titles and captions are stripped of HTML as each object is edited and saved by the user.
	/// </summary>
	public boolean getAllowUserEnteredHtml(){
		return this.allowUserEnteredHtml;
	}
	
	public void setAllowUserEnteredHtml(boolean allowUserEnteredHtml){
		this.allowUserEnteredHtml = allowUserEnteredHtml;
	}

	/// <summary>
	/// Indicates whether javascript is allowed in user-entered text such as titles, captions, and external media 
	/// objects. When false, script tags and the String "javascript:" is automatically removed from all user input.
	/// WARNING: Enabling this option makes the gallery vulnerable to a cross site scripting attack by any user with 
	/// permission to edit captions or upload external content objects.
	/// </summary>
	public boolean getAllowUserEnteredJavascript()	{
		return this.allowUserEnteredJavascript;
	}
	
	public void setAllowUserEnteredJavascript(boolean allowUserEnteredJavascript)	{
		this.allowUserEnteredJavascript = allowUserEnteredJavascript;
	}

	/// <summary>
	/// A list of HTML tags that may be present in titles and captions of albums and content objects.
	/// The attributes that are allowed are specified in <see cref="IGallerySettings.AllowedHtmlAttributes"/>.
	/// Applies only when <see cref="IGallerySettings.AllowUserEnteredHtml"/> is <c>true</c>. Ex: p,a,div,span,...
	/// </summary>
	public String[] getAllowedHtmlTags(){
		return this.allowedHtmlTags;
	}
	
	public void setAllowedHtmlTags(String[] allowedHtmlTags){
		this.allowedHtmlTags = ToLowerInvariant(allowedHtmlTags);
	}

	/// <summary>
	/// A list of attributes that HTML tags are allowed to have. These attributes, when combined with the
	/// HTML tags in <see cref="IGallerySettings.AllowedHtmlTags"/>, define the HTML that is allowed in titles and captions of 
	/// albums and content objects. Applies only when <see cref="IGallerySettings.AllowUserEnteredHtml"/> is <c>true</c>. Ex: href,class,style,...
	/// </summary>
	public String[] getAllowedHtmlAttributes()	{
		return this.allowedHtmlAttributes;
	}
	
	public void setAllowedHtmlAttributes(String[] allowedHtmlAttributes)	{
		this.allowedHtmlAttributes = ToLowerInvariant(allowedHtmlAttributes);
	}

	/// <summary>
	/// Indicates whether to allow the copying of objects a user has only view permissions for.
	/// </summary>
	public boolean getAllowCopyingReadOnlyObjects()	{
		return this.allowCopyingReadOnlyObjects;
	}
	
	public void setAllowCopyingReadOnlyObjects(boolean allowCopyingReadOnlyObjects)	{
		this.allowCopyingReadOnlyObjects = allowCopyingReadOnlyObjects;
	}

	/// <summary>
	/// Indicates whether to allow a logged-on user to manage their account. When false, the link to the account page 
	/// at the top right of each page is not shown and if the user navigates directly to the account page, they are redirected away.
	/// </summary>
	/// <value><c>true</c> if a logged-on user can manage their account; otherwise, <c>false</c>.</value>
	public boolean getAllowManageOwnAccount(){
		return this.allowManageOwnAccount;
	}
	
	public void setAllowManageOwnAccount(boolean allowManageOwnAccount){
		this.allowManageOwnAccount = allowManageOwnAccount;
	}

	/// <summary>
	/// Indicates whether a user is allowed to delete his or her own account.
	/// </summary>
	public boolean getAllowDeleteOwnAccount()	{
		return this.allowDeleteOwnAccount;
	}
	
	public void setAllowDeleteOwnAccount(boolean allowDeleteOwnAccount){
		this.allowDeleteOwnAccount = allowDeleteOwnAccount;
	}

	/// <summary>
	/// Specifies the visual transition effect to use when moving from one content object to another.
	/// </summary>
	/// <exception cref="System.ComponentModel.InvalidEnumArgumentException">Thrown when setting the value to an invalid
	/// enumeration.</exception>
	public ContentObjectTransitionType getContentObjectTransitionType()	{
		return this.contentObjectTransitionType;
	}
	
	public void setContentObjectTransitionType(ContentObjectTransitionType contentObjectTransitionType)	{
		if (!ContentObjectTransitionType.isValidContentObjectTransitionType(contentObjectTransitionType)){
			throw new InvalidEnumArgumentException(MessageFormat.format("The configuration setting ContentObjectTransitionType is not one of the enum values of the ContentObjectTransitionType enumeration. Valid values are 'None' and 'Fade'. Instead, the value {0} was passed.", contentObjectTransitionType));
		}

		this.contentObjectTransitionType = contentObjectTransitionType;
	}

	/// <summary>
	/// The duration of the transition effect, in seconds, when navigating between content objects. Value must be greater
	/// than zero. This setting has no effect when contentObjectTransitionType = "None".
	/// </summary>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when setting the value to a number less than or equal
	/// to zero.</exception>
	public float getContentObjectTransitionDuration()	{
		return this.contentObjectTransitionDuration;
	}
	
	public void setContentObjectTransitionDuration(float contentObjectTransitionDuration)	{
		if (contentObjectTransitionDuration <= 0)
		{
			throw new ArgumentOutOfRangeException("value", MessageFormat.format("Invalid ContentObjectTransitionDuration setting: The value must be greater than zero. Instead, the value was {0}.", contentObjectTransitionDuration));
		}

		this.contentObjectTransitionDuration = contentObjectTransitionDuration;
	}

	/// <summary>
	/// The delay, in milliseconds, between images during a slide show.
	/// </summary>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when setting the value to a number less than one.</exception>
	public int getSlideshowInterval()	{
		return this.slideshowInterval;
	}
	
	public void setSlideshowInterval(int slideshowInterval)	{
		if (slideshowInterval < 1)
		{
			throw new ArgumentOutOfRangeException("slideshowInterval", MessageFormat.format("Invalid SlideshowInterval setting: The value must be greater than one. Instead, the value was {0}.", slideshowInterval));
		}

		this.slideshowInterval = slideshowInterval;
	}

	/// <summary>
	/// Indicates whether to allow users to upload file types not explicitly specified in the mimeTypes configuration
	/// section. When false, any file with an extension not listed in the mimeTypes section is rejected. When true,
	/// MDS System accepts all file types regardless of their file extension.
	/// </summary>
	public boolean getAllowUnspecifiedMimeTypes()	{
		return this.allowUnspecifiedMimeTypes;
	}
	
	public void setAllowUnspecifiedMimeTypes(boolean allowUnspecifiedMimeTypes)	{
		this.allowUnspecifiedMimeTypes = allowUnspecifiedMimeTypes;
	}

	/// <summary>
	/// A comma-delimited list of file extensions, including the period, indicating types of images that a standard browser can display. When
	/// the user requests an original image (high resolution), the original is sent to the browser in an &lt;img&gt; HTML tag
	/// if its extension is one of those listed here.  If not, the user is presented with a message containing instructions
	/// for downloading the image file. Typically this setting should not be changed. Ex: .jpg,.jpeg,.gif,.png
	/// </summary>
	public String[] getImageTypesStandardBrowsersCanDisplay(){
		return this.imageTypesStandardBrowsersCanDisplay;
	}
	
	public void setImageTypesStandardBrowsersCanDisplay(String[] imageTypesStandardBrowsersCanDisplay){
		this.imageTypesStandardBrowsersCanDisplay = ToLowerInvariant(imageTypesStandardBrowsersCanDisplay);
	}

	/// <summary>
	/// A comma-delimited list of file extensions, including the period, indicating types of files that can be processed
	/// by ImageMagick. MDS System uses ImageMagick to extract images from files that cannot be processed by .NET.
	/// Ex: .pdf,.txt,.eps,.psd
	/// </summary>
	public String[] getImageMagickFileTypes(){
		return this.imageMagickFileTypes;
	}
	
	public void setImageMagickFileTypes(String[] imageMagickFileTypes){
		this.imageMagickFileTypes = ToLowerInvariant(imageMagickFileTypes);
	}
	
	private boolean allowAnonymousRating;

	/// <summary>
	/// Specifies whether anonymous users are allowed to rate gallery objects.
	/// </summary>
	/// <value><c>true</c> if anonymous rating is allowed; otherwise, <c>false</c>.</value>
	public boolean getAllowAnonymousRating() {
		return allowAnonymousRating;
	}
	
	public void setAllowAnonymousRating(boolean allowAnonymousRating) {
		this.allowAnonymousRating = allowAnonymousRating;
	}

	/// <summary>
	/// Specifies whether MDS System extracts metadata from image files. If the attribute
	/// <see cref="IGallerySettings.ExtractMetadataUsingWpf" /> is true, then additional metadata such as title, keywords,
	///  and rating is extracted.
	/// </summary>
	public boolean getExtractMetadata()	{
		return this.extractMetadata;
	}
	
	public void setExtractMetadata(boolean extractMetadata)	{
		this.extractMetadata = extractMetadata;
	} 

	/// <summary>
	/// Specifies whether metadata is extracted from image files using Windows Presentation Foundation (WPF) classes
	/// in .NET Framework 3.0 and higher. The WPF classes allow additional metadata to be extracted beyond those allowed by the
	/// .NET Framework 2.0, such as title, keywords, and rating. This attribute has no effect unless the following
	/// requirements are met: <see cref="ExtractMetadataUsingWpf"/> = true; .NET Framework 3.0 or higher is installed on the web
	/// server; and the web application is running in Full Trust. The WPF classes have exhibited some reliability issues
	/// during development, most notably causing the IIS worker process (w3wp.exe) to increase in memory usage and
	/// eventually crash during uploads and synchronizations. For this reason one may want to disable this feature
	/// until a .NET Framework service pack or future version provides better performance.
	/// </summary>
	/// <value></value>
	public boolean getExtractMetadataUsingWpf()	{
		return this.extractMetadataUsingWpf;
	}
	
	public void setExtractMetadataUsingWpf(boolean extractMetadataUsingWpf)	{
		this.extractMetadataUsingWpf = extractMetadataUsingWpf;
	}

	/// <summary>
	/// Gets or sets the metadata settings that define how metadata items are displayed to the user.
	/// </summary>
	/// <value>The metadata display options.</value>
	public MetadataDefinitionCollection getMetadataDisplaySettings(){
		return this.metadataDisplaySettings;
	}
	
	public void setMetadataDisplaySettings(MetadataDefinitionCollection metadataDisplaySettings){
		this.metadataDisplaySettings = metadataDisplaySettings;
	}

	private String metadataDateTimeFormatString;
	/// <summary>
	/// Gets or sets the format String to use for <see cref="DateTime" /> metadata values. The date type of each meta item
	/// is specified by the <see cref="IMetadataDefinition.DataType" /> property.
	/// </summary>
	/// <value>The metadata date time format String.</value>
	public String getMetadataDateTimeFormatString() {
		return metadataDateTimeFormatString;
	}
	
	public void setMetadataDateTimeFormatString(String metadataDateTimeFormatString) {
		this.metadataDateTimeFormatString = metadataDateTimeFormatString;
	}

	/// <summary>
	/// Specifies whether MDS System renders user interface objects to allow a user to download the file for a media 
	/// object. Note that setting this value to false does not prevent a user from downloading a
	/// content object, since a user already has access to the content object if he or she can view it in the browser. To
	/// prevent certain users from viewing content objects (and thus downloading them), use private albums, disable
	/// anonymous viewing, or configure security to prevent users from viewing the objects.
	/// </summary>
	public boolean getEnableContentObjectDownload()	{
		return this.enableContentObjectDownload;
	}
	
	public void setEnableContentObjectDownload(boolean enableContentObjectDownload)	{
		this.enableContentObjectDownload = enableContentObjectDownload;
	}

	/// <summary>
	/// Specifies whether anonymous users are allowed to view the original versions of content objects. When no
	/// compressed (optimized) version exists, the user is allowed to view the original, regardless of this
	/// setting. This setting has no effect on logged on users.
	/// </summary>
	public boolean getEnableAnonymousOriginalContentObjectDownload(){
		return this.enableAnonymousOriginalContentObjectDownload;
	}
	
	public void setEnableAnonymousOriginalContentObjectDownload(boolean enableAnonymousOriginalContentObjectDownload){
		this.enableAnonymousOriginalContentObjectDownload = enableAnonymousOriginalContentObjectDownload;
	}

	/// <summary>
	/// Specifies whether users are allowed to download content objects and albums in a ZIP file. Downloading of albums can be
	/// restricted by setting <see cref="EnableAlbumZipDownload"/> to <c>false</c>.
	/// </summary>
	/// <value></value>
	public boolean getEnableContentObjectZipDownload()	{
		return this.enableContentObjectZipDownload;
	}
	
	public void setEnableContentObjectZipDownload(boolean enableContentObjectZipDownload)	{
		this.enableContentObjectZipDownload = enableContentObjectZipDownload;
	}

	/// <summary>
	/// Specifies whether users are allowed to download albums in a ZIP file. This setting <see cref="EnableContentObjectZipDownload"/>
	/// must be enabled for this setting to take effect. In other words, albums can be downloaded only when
	/// <see cref="EnableContentObjectZipDownload"/> and <see cref="EnableAlbumZipDownload"/> are both enabled.
	/// </summary>
	/// <value></value>
	public boolean getEnableAlbumZipDownload()	{
		return this.enableAlbumZipDownload;
	}
	
	public void setEnableAlbumZipDownload(boolean enableAlbumZipDownload)	{
		this.enableAlbumZipDownload = enableAlbumZipDownload;
	}

	/// <summary>
	/// Specifies whether slide show functionality is enabled. When true, a start/pause slideshow button is displayed in the 
	/// toolbar that appears above a content object. The length of time each image is shown before automatically moving
	/// to the next one is controlled by the SlideshowInterval setting. Note that only images are shown during a slide
	/// show; other objects such as videos, audio files, and documents are skipped.
	/// </summary>
	public boolean getEnableSlideShow()	{
		return this.enableSlideShow;
	}
	
	public void setEnableSlideShow(boolean enableSlideShow)	{
		this.enableSlideShow = enableSlideShow;
	}

	private SlideShowType slideShowType;
	/// <summary>
	/// Gets or sets the type of the slide show. The default value is <see cref="MDS.Business.SlideShowType.FullScreen" />.
	/// </summary>
	/// <value>The type of the slide show.</value>
	public SlideShowType getSlideShowType() { 
		return slideShowType;
	}
	
	public void setSlideShowType(SlideShowType slideShowType) { 
		this.slideShowType = slideShowType;
	}

	/// <summary>
	///	The length (in pixels) of the longest edge of a thumbnail image.  This value is used when a thumbnail 
	///	image is created. The length of the shorter side is calculated automatically based on the aspect ratio of the image.
	/// The value must be between 10 and 100,000.
	/// </summary>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when setting the value to a number outside of the valid range
	/// of 10 and 100,000.</exception>
	public int getMaxThumbnailLength()	{
		return this.maxThumbnailLength;
	}
	
	public void setMaxThumbnailLength(int maxThumbnailLength)	{
		if ((maxThumbnailLength < 10) || (maxThumbnailLength > 100000))
		{
			throw new ArgumentOutOfRangeException("maxThumbnailLength", MessageFormat.format("Invalid MaxThumbnailLength setting: The value must be between 10 and 100,000. Instead, the value was {0}.", maxThumbnailLength));
		}

		this.maxThumbnailLength = maxThumbnailLength;
	}

	/// <summary>
	/// The quality level that thumbnail images are stored at (0 - 100).
	/// </summary>
	public int getThumbnailImageJpegQuality()	{
		return this.thumbnailImageJpegQuality;
		
	}
	
	public void setThumbnailImageJpegQuality(int thumbnailImageJpegQuality)	{
		if ((thumbnailImageJpegQuality < 1) || (thumbnailImageJpegQuality > 100))
		{
			throw new ArgumentOutOfRangeException("thumbnailImageJpegQuality", MessageFormat.format("Invalid ThumbnailImageJpegQuality setting: The value must be between 1 and 100. Instead, the value was {0}.", thumbnailImageJpegQuality));
		}

		this.thumbnailImageJpegQuality = thumbnailImageJpegQuality;
	}

	/// <summary>
	/// The String that is prepended to the thumbnail filename for each content object. For example, if an image
	/// named puppy.jpg is added, and this setting is "zThumb_", the thumbnail image will be named 
	/// "zThumb_puppy.jpg".	NOTE: Any file named "zThumb_puppy.jpg" that already exists will be overwritten, 
	/// so it is important to choose a value that, when prepended to content object filenames, will not 
	/// conflict with existing content objects.
	/// </summary>
	public String getThumbnailFileNamePrefix()	{
		return this.thumbnailFileNamePrefix;
	}
	
	public void setThumbnailFileNamePrefix(String thumbnailFileNamePrefix)	{
		this.thumbnailFileNamePrefix = thumbnailFileNamePrefix;
	}

	/// <summary>
	///	The length (in pixels) of the longest edge of an optimized image.  This value is used when an optimized
	///	image is created. The length of the shorter side is calculated automatically based on the aspect ratio of the image.
	/// </summary>
	public int getMaxOptimizedLength()	{
		return this.maxOptimizedLength;
	}
	
	public void setMaxOptimizedLength(int maxOptimizedLength)	{
		if ((maxOptimizedLength < 10) || (maxOptimizedLength > 100000))
		{
			throw new ArgumentOutOfRangeException("maxOptimizedLength", MessageFormat.format("Invalid MaxOptimizedLength setting: The value must be between 10 and 100,000. Instead, the value was {0}.", maxOptimizedLength));
		}

		this.maxOptimizedLength = maxOptimizedLength;
	}

	/// <summary>
	/// The quality level that optimized JPG pictures are created with. This is a number from 1 - 100, with 1 
	/// being the worst quality and 100 being the best quality. Not applicable for non-image content objects.
	/// </summary>
	public int getOptimizedImageJpegQuality()	{
		return this.optimizedImageJpegQuality;
	}
	
	public void setOptimizedImageJpegQuality(int optimizedImageJpegQuality)	{
		if ((optimizedImageJpegQuality < 1) || (optimizedImageJpegQuality > 100))
		{
			throw new ArgumentOutOfRangeException("optimizedImageJpegQuality", MessageFormat.format("Invalid OptimizedImageJpegQuality setting: The value must be between 1 and 100. Instead, the value was {0}.", optimizedImageJpegQuality));
		}

		this.optimizedImageJpegQuality = optimizedImageJpegQuality;
	}

	/// <summary>
	/// The size (in KB) above which an image is compressed to create an optimized version.
	/// Not applicable for non-image content objects.
	/// </summary>
	public int getOptimizedImageTriggerSizeKb()	{
		return this.optimizedImageTriggerSizeKb;
	}
	
	public void setOptimizedImageTriggerSizeKb(int optimizedImageTriggerSizeKb)	{
		if (optimizedImageTriggerSizeKb < 0)
		{
			throw new ArgumentOutOfRangeException("optimizedImageTriggerSizeKb", MessageFormat.format("Invalid OptimizedImageTriggerSizeKb setting: The value must be greater than or equal to zero. Instead, the value was {0}.", optimizedImageTriggerSizeKb));
		}

		this.optimizedImageTriggerSizeKb = optimizedImageTriggerSizeKb;
	}

	/// <summary>
	/// The String that is prepended to the optimized filename for images. This setting is only used for image
	/// content objects where an optimized image file is created. For example, if an image named
	/// puppy.jpg is added, and this setting is "zOpt_", the optimized image will be named "zOpt_puppy.jpg".
	/// NOTE: Any file named "zOpt_puppy.jpg" that already exists will be overwritten, 
	/// so it is important to choose a value that, when prepended to content object filenames, will not 
	/// conflict with existing content objects.
	/// </summary>
	public String getOptimizedFileNamePrefix()	{
		return this.optimizedFileNamePrefix;
	}
	
	public void setOptimizedFileNamePrefix(String optimizedFileNamePrefix)	{
		this.optimizedFileNamePrefix = optimizedFileNamePrefix;
	}

	/// <summary>
	/// The quality level that original JPG pictures are saved at. This is only used when the original is 
	/// modified by the user, such as rotation. Not applicable for non-image content objects.
	/// </summary>
	public int getOriginalImageJpegQuality(){
		return this.originalImageJpegQuality;
	}
	
	public void setOriginalImageJpegQuality(int originalImageJpegQuality){
		if ((originalImageJpegQuality < 1) || (originalImageJpegQuality > 100))
		{
			throw new ArgumentOutOfRangeException("originalImageJpegQuality", MessageFormat.format("Invalid OriginalImageJpegQuality setting: The value must be between 1 and 100. Instead, the value was {0}.", originalImageJpegQuality));
		}

		this.originalImageJpegQuality = originalImageJpegQuality;
	}

	/// <summary>
	/// Specifies whether to discard the original image when it is added to the gallery. This option, when enabled, 
	/// helps reduce disk space usage. This option applies only to images, and only when they are added through an 
	/// upload or by synchronizing. Changing this setting does not affect existing content objects. When false, 
	/// users still have the option to discard the original image on the Add Objects page by unchecking the 
	/// corresponding checkbox.
	/// </summary>
	public boolean getDiscardOriginalImageDuringImport(){
		return this.discardOriginalImageDuringImport;
		
	}
	
	public void setDiscardOriginalImageDuringImport(boolean discardOriginalImageDuringImport){
		this.discardOriginalImageDuringImport = discardOriginalImageDuringImport;
	}

	/// <summary>
	/// Specifies whether to apply a watermark to optimized and original images. If true, the text in the watermarkText
	/// property is applied to images, and the image specified in watermarkImagePath is overlayed on the image. If
	/// watermarkText is empty, or if watermarkImagePath is empty or does not refer to a valid image, that watermark
	/// is not applied. If applyWatermarkToThumbnails = true, then the watermark is also applied to thumbnails.
	/// </summary>
	public boolean getApplyWatermark()	{
		return this.applyWatermark;
	}
	
	public void setApplyWatermark(boolean applyWatermark)	{
		this.applyWatermark = applyWatermark;
	}

	/// <summary>
	/// Specifies whether to apply the text and/or image watermark to thumbnail images. This property is ignored if 
	/// applyWatermark = false.
	/// </summary>
	public boolean getApplyWatermarkToThumbnails()	{
		return this.applyWatermarkToThumbnails;
	}
	
	public void setApplyWatermarkToThumbnails(boolean applyWatermarkToThumbnails)	{
		this.applyWatermarkToThumbnails = applyWatermarkToThumbnails;
	}

	/// <summary>
	/// Specifies the text to apply to images in the gallery. The text is applied in a single line.
	/// </summary>
	public String getWatermarkText()	{
		return this.watermarkText;
	}
	
	public void setWatermarkText(String watermarkText)	{
		this.watermarkText = watermarkText;
	}

	/// <summary>
	/// The font used for the watermark text. If the font is not installed on the web server, a generic font will 
	/// be substituted.
	/// </summary>
	public String getWatermarkTextFontName()	{
		return this.watermarkTextFontName;
	}
	
	public void setWatermarkTextFontName(String watermarkTextFontName)	{
		this.watermarkTextFontName = watermarkTextFontName;
	}

	/// <summary>
	/// Gets or sets the height, in pixels, of the watermark text. This value is ignored if the property
	/// WatermarkTextWidthPercent is non-zero. Valid values are 0 - 10000.
	/// </summary>
	public int getWatermarkTextFontSize()	{
		return this.watermarkTextFontSize;
	}
	
	public void setWatermarkTextFontSize(int watermarkTextFontSize)	{
		if ((watermarkTextFontSize < 0) || (watermarkTextFontSize > 10000))
		{
			throw new ArgumentOutOfRangeException("watermarkTextFontSize", MessageFormat.format("Invalid WatermarkTextFontSize setting: The value must be between 0 and 10000. Instead, the value was {0}.", watermarkTextFontSize));
		}

		this.watermarkTextFontSize = watermarkTextFontSize;
	}

	/// <summary>
	/// Gets or sets the percent of the overall width of the recipient image that should be covered with the
	/// watermark text. The size of the text is automatically scaled up or down to achieve the desired width. For example,
	/// a value of 50 means the text is 50% as wide as the recipient image. Valid values are 0 - 100. The text is never
	/// rendered in a font smaller than 6 pixels, so in cases of long text it may stretch wider than the percentage
	/// specified in this setting.
	/// A value of 0 turns off this feature and causes the text size to be determined by the 
	/// WatermarkTextFontSize property.
	/// </summary>
	public int getWatermarkTextWidthPercent()	{
		return this.watermarkTextWidthPercent;
	}
	
	public void setWatermarkTextWidthPercent(int watermarkTextWidthPercent)	{
		if ((watermarkTextWidthPercent < 0) || (watermarkTextWidthPercent > 100))
		{
			throw new ArgumentOutOfRangeException("watermarkTextWidthPercent", MessageFormat.format("Invalid WatermarkTextWidthPercent setting: The value must be between 0 and 100. Instead, the value was {0}.", watermarkTextWidthPercent));
		}

		this.watermarkTextWidthPercent = watermarkTextWidthPercent;
	}

	/// <summary>
	/// Specifies the color of the watermark text. The color can be specified as hex (e.g. #336699), RGB (e.g. 127,55,95),
	/// or one of the System.Color.KnownColor enum values (e.g. Maroon).
	/// </summary>
	public String getWatermarkTextColor()	{
		return this.watermarkTextColor;
	}
	
	public void setWatermarkTextColor(String watermarkTextColor)	{
		this.watermarkTextColor = watermarkTextColor;
	}

	/// <summary>
	/// The opacity of the watermark text. This is a value from 0 to 100, with 0 being invisible and 100 being solid, 
	/// with no transparency.
	/// </summary>
	public int getWatermarkTextOpacityPercent()	{
		return this.watermarkTextOpacityPercent;
	}
	
	public void setWatermarkTextOpacityPercent(int watermarkTextOpacityPercent)	{
		if ((watermarkTextOpacityPercent < 0) || (watermarkTextOpacityPercent > 100))
		{
			throw new ArgumentOutOfRangeException("watermarkTextOpacityPercent", MessageFormat.format("Invalid WatermarkTextOpacityPercent setting: The value must be between 0 and 100. Instead, the value was {0}.", watermarkTextOpacityPercent));
		}

		this.watermarkTextOpacityPercent = watermarkTextOpacityPercent;
	}

	/// <summary>
	/// Gets or sets the location for the watermark text on the recipient image. This value maps to the 
	/// enumeration System.Drawing.ContentAlignment, and must be one of the following nine values:
	/// TopLeft, TopCenter, TopRight, MiddleLeft, MiddleCenter, MiddleRight, BottomLeft, BottomCenter, BottomRight.
	/// </summary>
	public ContentAlignment getWatermarkTextLocation()	{
		return this.watermarkTextLocation;
	}
	
	public void setWatermarkTextLocation(ContentAlignment watermarkTextLocation)	{
		if (!ContentAlignment.isValidContentAlignment(watermarkTextLocation)){
			throw new InvalidEnumArgumentException(MessageFormat.format("The configuration setting WatermarkTextLocation is not one of the enum values of the System.Drawing.ContentAlignment enumeration. Valid values are 'BottomCenter', 'BottomLeft', 'BottomRight', 'MiddleCenter', 'MiddleLeft', 'MiddleRight', 'TopCenter', 'TopLeft', 'TopRight'. Instead, the value {0} was passed.", watermarkTextLocation));
		}

		this.watermarkTextLocation = watermarkTextLocation;
	}

	/// <summary>																																																																																																													
	/// Gets or sets the full or relative path to a watermark image to be applied to the recipient image. The image																																																												
	/// must be in a format that allows it to be instantiated in a System.Drawing.Bitmap object. Relative paths
	/// are relative to the root of the web application. The directory separator character can be either a 
	/// forward or backward slash, and, for relative paths, the initial slash is optional. The following are
	/// all valid: "/images/mywatermark.jpg", "images/mywatermark.jpg", "\images\mywatermark.jpg", 
	/// "images\mywatermark.jpg", "C:\images\mywatermark.jpg"
	/// </summary>
	public String getWatermarkImagePath()	{
		return this.watermarkImagePath;
	}
	
	public void setWatermarkImagePath(String watermarkImagePath)	{
		this.watermarkImagePath = watermarkImagePath;
	}

	/// <summary>
	/// Gets or sets the percent of the overall width of the recipient image that should be covered with the
	/// watermark image. The size of the image is automatically scaled to achieve the desired width. For example,
	/// a value of 50 means the watermark image is 50% as wide as the recipient image. Valid values are 0 - 100.
	/// A value of 0 turns off this feature and causes the image to be rendered its actual size.
	/// </summary>
	public int getWatermarkImageWidthPercent()	{
		return this.watermarkImageWidthPercent;
	}
	
	public void setWatermarkImageWidthPercent(int watermarkImageWidthPercent)	{
		if ((watermarkImageWidthPercent < 0) || (watermarkImageWidthPercent > 100))
		{
			throw new ArgumentOutOfRangeException("watermarkImageWidthPercent", MessageFormat.format("Invalid WatermarkImageWidthPercent setting: The value must be between 0 and 100. Instead, the value was {0}.", watermarkImageWidthPercent));
		}

		this.watermarkImageWidthPercent = watermarkImageWidthPercent;
	}

	/// <summary>
	/// Gets or sets the opacity of the watermark image. Valid values are 0 - 100, with 0 being completely
	/// transparent and 100 completely opaque.
	/// </summary>
	public int getWatermarkImageOpacityPercent()	{
		return this.watermarkImageOpacityPercent;
	}
	
	public void setWatermarkImageOpacityPercent(int watermarkImageOpacityPercent)	{
		if ((watermarkImageOpacityPercent < 0) || (watermarkImageOpacityPercent > 100))
		{
			throw new ArgumentOutOfRangeException("watermarkImageOpacityPercent", MessageFormat.format("Invalid WatermarkImageOpacityPercent setting: The value must be between 0 and 100. Instead, the value was {0}.", watermarkImageOpacityPercent));
		}

		this.watermarkImageOpacityPercent = watermarkImageOpacityPercent;
	}

	/// <summary>
	/// Gets or sets the location for the watermark image on the recipient image. This value maps to the 
	/// enumeration System.Drawing.ContentAlignment, and must be one of the following nine values:
	/// TopLeft, TopCenter, TopRight, MiddleLeft, MiddleCenter, MiddleRight, BottomLeft, BottomCenter, BottomRight.
	/// </summary>
	public ContentAlignment getWatermarkImageLocation()	{
		return this.watermarkImageLocation;
	}
	
	public void setWatermarkImageLocation(ContentAlignment watermarkImageLocation)	{
		if (!ContentAlignment.isValidContentAlignment(watermarkImageLocation)){
			throw new InvalidEnumArgumentException(MessageFormat.format("The configuration setting WatermarkImageLocation is not one of the enum values of the System.Drawing.ContentAlignment enumeration. Valid values are 'BottomCenter', 'BottomLeft', 'BottomRight', 'MiddleCenter', 'MiddleLeft', 'MiddleRight', 'TopCenter', 'TopLeft', 'TopRight'. Instead, the value {0} was passed.", watermarkImageLocation));
		}

		this.watermarkImageLocation = watermarkImageLocation;
	}

	/// <summary>
	/// Specifies whether the MDS System administrator (specified in EmailToName/EmailToAddress)
	/// is sent a report when a web site error occurs.  A valid SMTP server must be specified if this
	/// is set to true (attribute SmtpServer).
	/// </summary>
	public boolean getSendEmailOnError()	{
		return this.sendEmailOnError;
	}
	
	public void setSendEmailOnError(boolean sendEmailOnError)	{
		this.sendEmailOnError = sendEmailOnError;
	}

	/// <summary>
	/// Indicates whether a video, audio or other dynamic object will automatically start playing in the user's browser.
	/// </summary>
	public boolean getAutoStartContentObject()	{
		return this.autoStartContentObject;
	}
	
	public void setAutoStartContentObject(boolean autoStartContentObject)	{
		this.autoStartContentObject = autoStartContentObject;
	}

	/// <summary>
	/// Indicates the default width, in pixels, of the browser object that plays a video file. Typically 
	/// this refers to the &lt;object&gt; tag that contains the video, resulting in a tag similar to this:
	/// &lt;object style="width:640px;height:480px;" ... &gt;
	/// </summary>
	public int getDefaultVideoPlayerWidth()	{
		return this.defaultVideoPlayerWidth;
	}
	
	public void setDefaultVideoPlayerWidth(int defaultVideoPlayerWidth)	{
		if ((defaultVideoPlayerWidth < 0) || (defaultVideoPlayerWidth > 10000))
		{
			throw new ArgumentOutOfRangeException("defaultVideoPlayerWidth", MessageFormat.format("Invalid DefaultVideoPlayerWidth setting: The value must be between 0 and 10,000. Instead, the value was {0}.", defaultVideoPlayerWidth));
		}

		this.defaultVideoPlayerWidth = defaultVideoPlayerWidth;
	}

	/// <summary>
	/// Indicates the default height, in pixels, of the browser object that plays a video file. Typically 
	/// this refers to the &lt;object&gt; tag that contains the video, resulting in a tag similar to this:
	/// &lt;object style="width:640px;height:480px;" ... &gt;
	/// </summary>
	public int getDefaultVideoPlayerHeight()	{
		return this.defaultVideoPlayerHeight;
	}
	
	public void setDefaultVideoPlayerHeight(int defaultVideoPlayerHeight)	{
		if ((defaultVideoPlayerHeight < 0) || (defaultVideoPlayerHeight > 10000))
		{
			throw new ArgumentOutOfRangeException("defaultVideoPlayerHeight", MessageFormat.format("Invalid DefaultVideoPlayerHeight setting: The value must be between 0 and 10,000. Instead, the value was {0}.", defaultVideoPlayerHeight));
		}

		this.defaultVideoPlayerHeight = defaultVideoPlayerHeight;
	}

	/// <summary>
	/// Indicates the default width, in pixels, of the browser object that plays an audio file. Typically 
	/// this refers to the &lt;object&gt; tag that contains the audio file, resulting in a tag similar to this:
	/// &lt;object style="width:300px;height:200px;" ... &gt;
	/// </summary>
	public int getDefaultAudioPlayerWidth()	{
		return this.defaultAudioPlayerWidth;
	}
	
	public void setDefaultAudioPlayerWidth(int defaultAudioPlayerWidth)	{
		if ((defaultAudioPlayerWidth < 0) || (defaultAudioPlayerWidth > 10000))
		{
			throw new ArgumentOutOfRangeException("defaultAudioPlayerWidth", MessageFormat.format("Invalid DefaultAudioPlayerWidth setting: The value must be between 0 and 10,000. Instead, the value was {0}.", defaultAudioPlayerWidth));
		}

		this.defaultAudioPlayerWidth = defaultAudioPlayerWidth;
	}

	/// <summary>
	/// Indicates the default height, in pixels, of the browser object that plays an audio file. Typically 
	/// this refers to the &lt;object&gt; tag that contains the audio file, resulting in a tag similar to this:
	/// &lt;object style="width:300px;height:200px;" ... &gt;
	/// </summary>
	public int getDefaultAudioPlayerHeight()	{
		return this.defaultAudioPlayerHeight;
	}
	
	public void setDefaultAudioPlayerHeight(int defaultAudioPlayerHeight)	{
		if ((defaultAudioPlayerHeight < 0) || (defaultAudioPlayerHeight > 10000))
		{
			throw new ArgumentOutOfRangeException("defaultAudioPlayerHeight", MessageFormat.format("Invalid DefaultAudioPlayerHeight setting: The value must be between 0 and 10,000. Instead, the value was {0}.", defaultAudioPlayerHeight));
		}

		this.defaultAudioPlayerHeight = defaultAudioPlayerHeight;
	}

	/// <summary>
	/// Indicates the default width, in pixels, of the browser object that displays a generic content object.
	/// A generic content object is defined as any content object that is not an image,	audio, or video file. This
	/// includes Shockwave Flash, Adobe Reader, text files, Word documents and others. The value specified here
	/// is sent to the browser as the width for the object element containing this content object, resulting in syntax 
	/// similar to this: &lt;object style="width:640px;height:480px;" ... &gt; This setting applies only to objects 
	/// rendered within the browser, such as Shockwave Flash. Objects sent to the browser via a download
	/// link, such as text files, PDF files, and Word documents, ignore this setting.
	/// </summary>
	public int getDefaultGenericObjectWidth(){
		return this.defaultGenericObjectWidth;
	}
	
	public void setDefaultGenericObjectWidth(int defaultGenericObjectWidth){
		if ((defaultGenericObjectWidth < 0) || (defaultGenericObjectWidth > 10000))
		{
			throw new ArgumentOutOfRangeException("defaultGenericObjectWidth", MessageFormat.format("Invalid DefaultGenericObjectWidth setting: The value must be between 0 and 10,000. Instead, the value was {0}.", defaultGenericObjectWidth));
		}

		this.defaultGenericObjectWidth = defaultGenericObjectWidth;
	}

	/// <summary>
	/// Indicates the default height, in pixels, of the browser object that displays a generic content object.
	/// A generic content object is defined as any content object that is not an image,	audio, or video file. This
	/// includes Shockwave Flash, Adobe Reader, text files, Word documents and others. The value specified here
	/// is sent to the browser as the width for the object element containing this content object, resulting in syntax 
	/// similar to this: &lt;object style="width:640px;height:480px;" ... &gt; This setting applies only to objects 
	/// rendered within the browser, such as Shockwave Flash. Objects sent to the browser via a download
	/// link, such as text files, PDF files, and Word documents, ignore this setting.
	/// </summary>
	public int getDefaultGenericObjectHeight()	{
		return this.defaultGenericObjectHeight;
	}
	
	public void setDefaultGenericObjectHeight(int defaultGenericObjectHeight)	{
		if ((defaultGenericObjectHeight < 0) || (defaultGenericObjectHeight > 10000))
		{
			throw new ArgumentOutOfRangeException("defaultGenericObjectHeight", MessageFormat.format("Invalid DefaultGenericObjectHeight setting: The value must be between 0 and 10,000. Instead, the value was {0}.", defaultGenericObjectHeight));
		}

		this.defaultGenericObjectHeight = defaultGenericObjectHeight;
	}

	/// <summary>
	/// Indicates the maximum size, in kilobytes, of the files that can be uploaded.
	/// Use this setting to keep users from uploading very large files and to help guard against Denial of 
	/// Service (DOS) attacks. A value of zero (0) indicates there is no restriction on upload size (unlimited).
	/// This value applies to the content length of the entire upload request, not just the file. For example, if
	/// this value is 1024 KB and the user attempts to upload two 800 KB images, the request will fail because
	/// the total content length is larger than 1024 KB. This setting is not used during synchronization.
	/// </summary>
	public int getMaxUploadSize()	{
		return this.maxUploadSize;
	}
	
	public void setMaxUploadSize(int maxUploadSize)	{
		if (maxUploadSize < 0)	{
			throw new ArgumentOutOfRangeException("maxUploadSize", MessageFormat.format("Invalid MaxUploadSize setting: The value must be between 0 and {0}. Instead, the value was {1}.", Integer.MAX_VALUE, maxUploadSize));
		}

		this.maxUploadSize = maxUploadSize;
	}

	/// <summary>
	/// Indicates whether a user can upload a physical file to the gallery, such as an image or video file stored
	/// on a local hard drive. The user must also be authenticated and a member of a role with AllowAddContentObject 
	/// or AllowAdministerSite permission. This setting is not used during synchronization.
	/// </summary>
	public boolean getAllowAddLocalContent()	{
		return this.allowAddLocalContent;
	}
	
	public void setAllowAddLocalContent(boolean allowAddLocalContent)	{
		this.allowAddLocalContent = allowAddLocalContent;
	}

	/// <summary>
	/// Indicates whether a user can add a link to external content, such as a YouTube video, to the gallery. 
	/// The user must also be authenticated and a member of a role with AllowAddContentObject 
	/// or AllowAdministerSite permission. This setting is not used during synchronization.
	/// </summary>
	public boolean getAllowAddExternalContent()	{
		return this.allowAddExternalContent;
	}
	
	public void setAllowAddExternalContent(boolean allowAddExternalContent)	{
		this.allowAddExternalContent = allowAddExternalContent;
	}

	/// <summary>
	/// Indicates whether users can view galleries without logging in. When false, users are redirected to a login
	/// page when any album is requested. Private albums are never shown to anonymous users, even when this 
	/// property is true.
	/// </summary>
	public boolean getAllowAnonymousBrowsing()	{
		return this.allowAnonymousBrowsing;
	}
	
	public void setAllowAnonymousBrowsing(boolean allowAnonymousBrowsing)	{
		this.allowAnonymousBrowsing = allowAnonymousBrowsing;
	}

	/// <summary>
	/// Indicates the number of objects to display at a time. For example, if an album has more than this number of
	/// gallery objects, paging controls appear to assist the user in navigating to them. A value of zero disables 
	/// the paging feature.
	/// </summary>
	public int getPageSize()	{
		return this.pageSize;
	}
	
	public void setPageSize(int pageSize)	{
		if (pageSize < 0){
			throw new ArgumentOutOfRangeException("pageSize", MessageFormat.format("Invalid PageSize setting: The value must be between 0 and {0}. Instead, the value was {1}.", Integer.MAX_VALUE, pageSize));
		}

		this.pageSize = pageSize;
	}

	/// <summary>
	/// Gets or sets the location for the pager used to navigate large collections of objects. This value maps to the 
	/// enumeration <see cref="PagerPosition" />, and must be one of the following values:
	/// Top, Bottom, TopAndBottom. This value is ignored when paging is disabled (<see cref="IGallerySettings.PageSize"/> = 0).
	/// </summary>
	public PagerPosition getPagerLocation()	{
		return this.pagerLocation;
	}
	
	public void setPagerLocation(PagerPosition pagerLocation)	{
		if (!PagerPosition.isValidPagerPosition(pagerLocation))	{
			throw new InvalidEnumArgumentException(MessageFormat.format("The configuration setting PagerLocation is not one of the enum values of the PagerPosition enumeration. Valid values are 'Top', 'Bottom', and 'TopAndBottom'. Instead, the value {0} was passed.", pagerLocation));
		}

		this.pagerLocation = pagerLocation;
	}

	/// <summary>
	/// Indicates whether anonymous users are allowed to create accounts.
	/// </summary>
	public boolean getEnableSelfRegistration()	{
		return this.enableSelfRegistration;
	}
	
	public void setEnableSelfRegistration(boolean enableSelfRegistration)	{
		this.enableSelfRegistration = enableSelfRegistration;
	}

	public Integer getApprovalSwitch() {
		return approvalSwitch != null ? approvalSwitch : (AppSettings.getInstance().getApprovalSwitch() == null ? ApprovalSwitch.notspecified.value() : AppSettings.getInstance().getApprovalSwitch());
	}
	
	public void setApprovalSwitch(Integer approvalSwitch) {
		this.approvalSwitch = approvalSwitch;
	}

	public Boolean isEnableVerificationCode() {
		return enableVerificationCode != null ? enableVerificationCode : (AppSettings.getInstance().isEnableVerificationCode() == null ? false : AppSettings.getInstance().isEnableVerificationCode());
	}

	public void setEnableVerificationCode(Boolean enableVerificationCode) {
		this.enableVerificationCode = enableVerificationCode;
	}

	public Boolean getUsePdfRenderer() {
		return usePdfRenderer != null ? usePdfRenderer : (AppSettings.getInstance().getUsePdfRenderer() == null ? false : AppSettings.getInstance().getUsePdfRenderer());
	}

	public void setUsePdfRenderer(Boolean usePdfRenderer) {
		this.usePdfRenderer = usePdfRenderer;
	}

	/// <summary>
	/// Indicates whether e-mail verification is required when a user registers an account. When true, the account is 
	/// initially disabled and an email is sent to the user with a verification link. When clicked, user is approved 
	/// and logged on, unless <see cref="IGallerySettings.RequireApprovalForSelfRegisteredUser"/> is enabled, in which case an administrator
	/// must approve the account before the user can log on. Setting this to true reduces spam activity and guarantees that 
	/// a valid e-mail address is associated with the user. When the setting is false, an e-mail address is not required 
	/// and the user account is immediately created. This setting is ignored when 
	/// <see cref="IGallerySettings.EnableSelfRegistration">self registration</see> is disabled.
	/// </summary>
	public boolean getRequireEmailValidationForSelfRegisteredUser()	{
		return this.requireEmailValidationForSelfRegisteredUser;
	}
	
	public void setRequireEmailValidationForSelfRegisteredUser(boolean requireEmailValidationForSelfRegisteredUser)	{
		this.requireEmailValidationForSelfRegisteredUser = requireEmailValidationForSelfRegisteredUser;
	}

	/// <summary>
	/// Indicates whether an administrator must approve newly created accounts before the user can log on. When true, 
	/// the account is disabled until it is approved by an administrator. When a user registers an account, an e-mail
	/// is sent to each user specified in <see cref="IGallerySettings.UsersToNotifyWhenAccountIsCreated"/>. Only users belonging to a
	/// role with AllowAdministerSite permission can approve a user. If <see cref="IGallerySettings.RequireEmailValidationForSelfRegisteredUser"/>
	/// is enabled, the e-mail requesting administrator approval is not sent until the user verifies the e-mail address.
	/// This setting is ignored when <see cref="IGallerySettings.EnableSelfRegistration">self registration</see> is disabled.
	/// </summary>
	public boolean getRequireApprovalForSelfRegisteredUser()	{
		return this.requireApprovalForSelfRegisteredUser;
	}
	
	public void setRequireApprovalForSelfRegisteredUser(boolean requireApprovalForSelfRegisteredUser)	{
		this.requireApprovalForSelfRegisteredUser = requireApprovalForSelfRegisteredUser;
	}

	/// <summary>
	/// Indicates whether account names are primarily e-mail addresses. When true, certain forms, such as the self registration
	/// wizard, assume e-mail addresses are used as account names. For example, when this value is false, the self registration
	/// wizard includes fields for both an account name and an e-mail address, but when true it only requests an e-mail address.
	/// This setting is ignored when <see cref="IGallerySettings.EnableSelfRegistration">self registration</see> is disabled.
	/// </summary>
	public boolean getUseEmailForAccountName()	{
		return this.useEmailForAccountName;
	}
	
	public void setUseEmailForAccountName(boolean useEmailForAccountName)	{
		this.useEmailForAccountName = useEmailForAccountName;
	}

	/// <summary>
	/// A list of roles to assign when a user registers a new account. This setting is ignored when
	/// <see cref="IGallerySettings.EnableSelfRegistration">self registration</see> is disabled and when an account is created by an
	/// administrator. Ex: "ReadOnly,NoWatermark"
	/// </summary>
	public String[] getDefaultRolesForSelfRegisteredUser()	{
		return this.defaultRolesForSelfRegisteredUser;
	}
	
	public void setDefaultRolesForSelfRegisteredUser(String[] defaultRolesForSelfRegisteredUser)	{
		this.defaultRolesForSelfRegisteredUser = defaultRolesForSelfRegisteredUser;
	}

	/// <summary>
	/// A list of account names of users to receive an e-mail notification when an account is created.
	/// When <see cref="RequireEmailValidationForSelfRegisteredUser"/> is enabled, the e-mail is not sent until the
	/// user verifies the e-mail address. Applies whether an account is self-created or created by an administrator.
	/// </summary>
	/*public IUserAccountCollection getUsersToNotifyWhenAccountIsCreated()	{
		return this.usersToNotifyWhenAccountIsCreated;
	}
	
	public void setUsersToNotifyWhenAccountIsCreated(IUserAccountCollection usersToNotifyWhenAccountIsCreated)	{
		this.usersToNotifyWhenAccountIsCreated = usersToNotifyWhenAccountIsCreated;
	}*/

	/// <summary>
	/// A list of account names of users to receive an e-mail notification when an application error occurs.
	/// </summary>
	public UserAccountCollection getUsersToNotifyWhenErrorOccurs()	{
		return this.usersToNotifyWhenErrorOccurs;
	}
	
	public void setUsersToNotifyWhenErrorOccurs(UserAccountCollection usersToNotifyWhenErrorOccurs)	{
		this.usersToNotifyWhenErrorOccurs = usersToNotifyWhenErrorOccurs;
	}

	/// <summary>
	/// Indicates whether each user is associated owner to a unique album. The title of the album is based on the 
	/// template in the <see cref="IGallerySettings.UserAlbumNameTemplate"/> property. The album is created when the account is created or
	/// if the album does not exist when the user logs on. It is created in the album specified in the 
	/// <see cref="IGallerySettings.UserAlbumParentAlbumId"/> property.</summary>
	public boolean getEnableUserAlbum()	{
		return this.enableUserAlbum;
		
	}
	
	public void setEnableUserAlbum(boolean enableUserAlbum)	{
		this.enableUserAlbum = enableUserAlbum;
	}

	/// <summary>
	/// Indicates whether a user album is automatically created for a user the first time he or she logs on. This setting
	/// is used to seed the user's <see cref="IUserGalleryProfile.EnableUserAlbum" /> profile setting when it is created.
	/// This property applies only when <see cref="IGallerySettings.EnableUserAlbum" /> is <c>true</c>.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if a user album is created for each user the first time he or she logs on; otherwise, <c>false</c>.
	/// </value>
	public boolean getEnableUserAlbumDefaultForUser()	{
		return this.enableUserAlbumDefaultForUser;
	}
	
	public void setEnableUserAlbumDefaultForUser(boolean enableUserAlbumDefaultForUser)	{
		this.enableUserAlbumDefaultForUser = enableUserAlbumDefaultForUser;
	}

	/// <summary>
	/// Specifies the ID of the album containing user albums. This setting is ignored when <see cref="IGallerySettings.EnableUserAlbum"/>
	/// is false. This property may have a value of zero (0) when user albums are disabled.
	/// </summary>
	public int getUserAlbumParentAlbumId()	{
		return this.userAlbumParentAlbumId;
	}
	
	public void setUserAlbumParentAlbumId(int userAlbumParentAlbumId)	{
		this.userAlbumParentAlbumId = userAlbumParentAlbumId;
	}

	/// <summary>
	/// Specifies the template to use for naming the album that is created for new users. Applies only when 
	/// <see cref="IGallerySettings.EnableUserAlbum"/> is true. The placeholder String {UserName}, if present, is replaced 
	/// by the account name.
	/// </summary>
	public String getUserAlbumNameTemplate()	{
		return this.userAlbumNameTemplate;
	}
	
	public void setUserAlbumNameTemplate(String userAlbumNameTemplate)	{
		this.userAlbumNameTemplate = userAlbumNameTemplate;
	}

	/// <summary>
	/// Specifies the template to use for the album summary of a newly created user album. Applies only when 
	/// <see cref="IGallerySettings.EnableUserAlbum"/> is true. No placeholder Strings are supported.
	/// </summary>
	public String getUserAlbumSummaryTemplate()	{
		return this.userAlbumSummaryTemplate;
	}
	
	public void setUserAlbumSummaryTemplate(String userAlbumSummaryTemplate)	{
		this.userAlbumSummaryTemplate = userAlbumSummaryTemplate;
	}

	/// <summary>
	/// Indicates whether to redirect the user to his or her album after logging in. If set to false, the current page is
	/// re-loaded or, if there isn't a page, the user is shown the top level album for which the user has view access. This setting 
	/// is ignored when <see cref="IGallerySettings.EnableUserAlbum"/> is false.</summary>
	public boolean getRedirectToUserAlbumAfterLogin()	{
		return this.redirectToUserAlbumAfterLogin;
	}
	
	public void setRedirectToUserAlbumAfterLogin(boolean redirectToUserAlbumAfterLogin)	{
		this.redirectToUserAlbumAfterLogin = redirectToUserAlbumAfterLogin;
	}

	/// <summary>
	/// Gets or sets the position in the video where the thumbnail is generated from. The value is in seconds, so a value
	/// of three indicates the thumbnail for the video is generated from a frame three seconds into the video. The value must be 
	/// between 0 and 86,400 seconds.
	/// </summary>
	/// <value>The position, in seconds, in the video where the thumbnail image is generated from.</value>
	public int getVideoThumbnailPosition()	{
		return this.videoThumbnailPosition;
	}
	
	public void setVideoThumbnailPosition(int videoThumbnailPosition)	{
		if ((videoThumbnailPosition < 0) || (videoThumbnailPosition > 86400))
		{
			throw new ArgumentOutOfRangeException("videoThumbnailPosition", MessageFormat.format("Invalid VideoThumbnailPosition setting: The value must be between 0 and 86400 (24 hours). Instead, the value was {0}.", videoThumbnailPosition));
		}

		this.videoThumbnailPosition = videoThumbnailPosition;
	}

	/// <summary>
	/// Gets or sets a value indicating whether to automatically synchronize the current gallery on a periodic basis. The interval
	/// is defined in the <see cref="IGallerySettings.AutoSyncIntervalMinutes" /> property. The auto sync depends on periodic browser requests by 
	/// users to trigger the logic to check whether a sync is needed.
	/// </summary>
	/// <value><c>true</c> if auto sync is enabled; otherwise, <c>false</c>.</value>
	public boolean getEnableAutoSync()	{
		return this.enableAutoSync;
	}
	
	public void setEnableAutoSync(boolean enableAutoSync)	{
		this.enableAutoSync = enableAutoSync;
	}

	/// <summary>
	/// Gets or sets the minimum interval, in minutes, that an auto-synchronization is to occur. Since the auto sync feature 
	/// requires periodic browser requests, the actual interval may be longer for infrequently accessed galleries.
	/// </summary>
	/// <value>The auto sync interval, in minutes.</value>
	public int getAutoSyncIntervalMinutes()	{
		return this.autoSyncIntervalMinutes;
	}
	
	public void setAutoSyncIntervalMinutes(int autoSyncIntervalMinutes)	{
		this.autoSyncIntervalMinutes = autoSyncIntervalMinutes;
	}

	/// <summary>
	/// Gets or sets the date/time of the last auto-sync. Value is <see cref="DateTime.MinValue" /> when <see cref="IGallerySettings.EnableAutoSync" />
	/// is disabled or when no auto-sync has yet been performed.
	/// </summary>
	/// <value>The date/time of the last auto-sync.</value>
	public Date getLastAutoSync()	{
		return this.lastAutoSync;
	}
	
	public void setLastAutoSync(Date lastAutoSync)	{
		this.lastAutoSync = lastAutoSync;
	}

	/// <summary>
	/// Gets or sets a value indicating whether to allow external calls to the synchronize web service. When true, two web service
	/// methods can be invoked by an anonymous user to begin a synchronization: <see cref="Mds.Gallery.SyncAllGalleries(String)" /> and 
	/// <see cref="Mds.Gallery.SyncAlbum(int, String)" />. Each method must include the password specified in 
	/// <see cref="IGallerySettings.RemoteAccessPassword" />. This setting does not affect the third web service method to start a synch
	/// (<see cref="Mds.Gallery.Synchronize" />), as that method requires that it be invoked by a logged-on user with 
	/// permission to execute synchronizations, and as such it is always available (it is also the method used by the 
	/// Synchronize page to start a sync).
	/// </summary>
	/// <value><c>true</c> if a synchronization operation can be initiated through a web service; otherwise, <c>false</c>.</value>
	public boolean getEnableRemoteSync()	{
		return this.enableRemoteSync;
	}
	
	public void setEnableRemoteSync(boolean enableRemoteSync)	{
		this.enableRemoteSync = enableRemoteSync;
	}

	/// <summary>
	/// Gets or sets the password that is passed to the remote synchronization web service methods. This password prevents
	/// malicious users from starting unauthorized synchronizations. The following web service methods require this password:
	/// <see cref="Mds.Gallery.SyncAllGalleries(String)" /> and <see cref="Mds.Gallery.SyncAlbum(int, String)" />.
	/// </summary>
	/// <value>The remote sync password.</value>
	public String getRemoteAccessPassword()	{
		return this.remoteSyncPassword;
	}
	
	public void setRemoteAccessPassword(String remoteSyncPassword)	{
		this.remoteSyncPassword = remoteSyncPassword;
	}

	/// <summary>
	/// Gets or sets the media encoder settings that define how media files may be encoded.
	/// </summary>
	/// <value>An instance that implements <see cref="IContentEncoderSettingsCollection" />.</value>
	public ContentEncoderSettingsCollection getContentEncoderSettings()	{
		return this.contentEncoderSettings;
	}
	
	public void setContentEncoderSettings(ContentEncoderSettingsCollection contentEncoderSettings)	{
		this.contentEncoderSettings = contentEncoderSettings;
	}

	/// <summary>
	/// Gets or sets the timeout setting, in milliseconds, for the media encoder function.
	/// </summary>
	/// <value>An integer</value>
	public int getContentEncoderTimeoutMs()	{
		return this.contentEncoderTimeoutMs;
	}
	
	public void setContentEncoderTimeoutMs(int contentEncoderTimeoutMs)	{
		this.contentEncoderTimeoutMs = contentEncoderTimeoutMs;
	}
	
	/// <summary>
		/// The name associated with the <see cref="EmailFromAddress" /> email address. Emails sent from MDS System
		/// will appear to be sent from this person.
		/// </summary>
		/// <value>The name of the email from.</value>
		public String getEmailFromName(){
			return this.emailFromName;
		}
		
		public void setEmailFromName(String emailFromName){
			this.emailFromName = emailFromName;
		}

		/// <summary>
		/// The email address associated with <see cref="EmailFromName" />. Emails sent from MDS System
		/// will appear to be sent from this email address.
		/// </summary>
		/// <value>The email from address.</value>
		/// <exception cref="System.ArgumentOutOfRangeException">value</exception>
		public String getEmailFromAddress()	{
			return this.emailFromAddress;
		}
		
		public void setEmailFromAddress(String emailFromAddress)	{
			if (!HelperFunctions.isValidEmail(emailFromAddress)){
				throw new ArgumentOutOfRangeException("emailFromAddress", MessageFormat.format("Invalid EmailFromAddress setting: The value must be a valid e-mail address. Instead, the value was {0}.", emailFromAddress));
			}

			this.emailFromAddress = emailFromAddress;
		}
		
		/// <summary>
		/// The email address associated with <see cref="EmailPassword" />. Emails sent from HRIS System
		/// will appear to be sent from this email address.
		/// </summary>
		/// <value>The email password.</value>
		/// <exception cref="System.ArgumentOutOfRangeException">value</exception>
		public String getEmailPassword() {
	        return emailPassword; 
	    }
		
		public void setEmailPassword(String emailPassword) {
	        this.emailPassword = emailPassword;
	    }

		public EmailServerType getEmailServerType() {
			return emailServerType;
		}

		public void setEmailServerType(EmailServerType emailServerType) {
			this.emailServerType = emailServerType;
		}

		/// <summary>
		/// Specifies the IP address or name of the SMTP server used to send emails. (Examples: 127.0.0.1, 
		/// Godzilla, mail.yourisp.com) This value will override the SMTP server setting that may be in the 
		/// system.net mailSettings section of the web.config file (either explicitly or inherited from a 
		/// parent web.config file). Leave this setting blank to use the value in web.config or if you are 
		/// not using the email functionality.
		/// </summary>
		public String getSmtpServer(){
			return this.smtpServer;
		}
		
		public void setSmtpServer(String smtpServer){
			this.smtpServer = smtpServer;
		}
		
		/// <summary>
		/// Specifies the IP address or name of the SMTP domail(exchange server only) used to send emails. (Examples: mmdsplus.com, 
		/// Godzilla, mail.yourisp.com) This value will override the SMTP domail setting that may be in the 
		/// system.net mailSettings section of the web.config file (either explicitly or inherited from a 
		/// parent web.config file). Leave this setting blank to use the value in web.config or if you are 
		/// not using the email functionality.
		/// </summary>
		public String getSmtpDomail(){
			return this.smtpDomail;
		}
		
		public void setSmtpDomail(String smtpDomail){
			this.smtpDomail = smtpDomail;
		}

		/// <summary>
		/// Specifies the SMTP server port number used to send emails. This value will override the SMTP 
		/// server port setting that may be in the system.net mailSettings section of the web.config file 
		/// (either explicitly or inherited from a parent web.config file). Leave this setting blank to 
		/// use the value in web.config or if you are not using the email functionality. Defaults to 25 
		/// if not specified here or in web.config.
		/// </summary>
		public String getSmtpServerPort(){
			return this.smtpServerPort;
		}
		
		public void setSmtpServerPort(String smtpServerPort){
			this.smtpServerPort = smtpServerPort;
		}

		/// <summary>
		/// Specifies whether e-mail functionality uses Secure Sockets Layer (SSL) to encrypt the connection.
		/// </summary>
		public boolean getSendEmailUsingSsl(){
			return this.sendEmailUsingSsl;
		}
		
		public void setSendEmailUsingSsl(boolean sendEmailUsingSsl){
			this.sendEmailUsingSsl = sendEmailUsingSsl;
		}

	/// <summary>
	/// Gets the full physical path to the directory containing the content objects. Example:
	/// "C:\inetpub\wwwroot\MDS\contentobjects"
	/// </summary>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when setting this property to a null or empty String.</exception>
	public String getFullContentObjectPath()	{
		return this.fullContentObjectPath;
	}
	
	public void setFullContentObjectPath(String fullContentObjectPath)	{
		// Validate the path. Will throw an exception if a problem is found.
		if (StringUtils.isBlank(fullContentObjectPath))	{
			throw new ArgumentOutOfRangeException("fullContentObjectPath");
		}

		if (!this.verifiedFilePaths.contains(fullContentObjectPath)){
			try {
					if (contentObjectPathIsReadOnly)
						HelperFunctions.validatePhysicalPathExistsAndIsReadable(fullContentObjectPath);			
					else{
						HelperFunctions.validatePhysicalPathExistsAndIsReadWritable(fullContentObjectPath);
						this.verifiedFilePaths.add(fullContentObjectPath);
					}
				} catch (DirectoryNotFoundException | CannotReadFromDirectoryException | CannotWriteToDirectoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		this.fullContentObjectPath = fullContentObjectPath;
	}

	/// <summary>
	/// Gets the full physical path to the directory where MDS System stores the thumbnail images of content objects.
	/// If no directory is specified in the configuration setting, this returns the main content object path (that is, returns
	/// the same value as the <see cref="FullContentObjectPath"/> property).
	/// Example: "C:\inetpub\wwwroot\MDS\contentobjects"
	/// </summary>
	/// <value>The full physical path to the directory where MDS System stores the thumbnail images of content objects.</value>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when setting this property to a null or empty String.</exception>
	public String getFullThumbnailPath()	{
		return this.fullThumbnailPath;
	}
	
	public void setFullThumbnailPath(String fullThumbnailPath)	{
		// Validate the path. Will throw an exception if a problem is found.
		if (StringUtils.isBlank(fullThumbnailPath))
		{
			throw new ArgumentOutOfRangeException("fullThumbnailPath");
		}

		if (!this.verifiedFilePaths.contains(fullThumbnailPath))
		{
			try {
				HelperFunctions.validatePhysicalPathExistsAndIsReadWritable(fullThumbnailPath);
			} catch (CannotWriteToDirectoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.verifiedFilePaths.add(fullThumbnailPath);
		}

		this.fullThumbnailPath = fullThumbnailPath;
	}

	/// <summary>
	/// Gets the full physical path to the directory where MDS System stores the optimized images of content objects.
	/// If no directory is specified in the configuration setting, this returns the main content object path (that is, returns
	/// the same value as the <see cref="FullContentObjectPath"/> property).
	/// Example: "C:\inetpub\wwwroot\MDS\contentobjects"
	/// </summary>
	/// <value>The full physical path to the directory where MDS System stores the optimized images of content objects.</value>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when setting this property to a null or empty String.</exception>
	public String getFullOptimizedPath()	{
		return this.fullOptimizedPath;
	}
	
	public void setFullOptimizedPath(String fullOptimizedPath)	{
		if (StringUtils.isBlank(fullOptimizedPath))
			throw new ArgumentOutOfRangeException("value");

		// Validate the path. Will throw an exception if a problem is found.
		if (!this.verifiedFilePaths.contains(fullOptimizedPath))
		{
			try {
				HelperFunctions.validatePhysicalPathExistsAndIsReadWritable(fullOptimizedPath);
			} catch (CannotWriteToDirectoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.verifiedFilePaths.add(fullOptimizedPath);
		}

		this.fullOptimizedPath = fullOptimizedPath;
	}

	//#endregion

	/// <summary>
	/// Converts each String in <paramref name="array" /> to a lower case invariant version of the original.
	/// </summary>
	/// <param name="array">An array of Strings.</param>
	/// <returns>Returns the <paramref name="array" /> with each element converted to a lower case invariant.</returns>
	private static String[] ToLowerInvariant(String[] array){
		List<String> list =  Arrays.asList(array);
		list.forEach(item->item = item.toLowerCase());
		
		return list.toArray(new String[0]);
	}
	

	//#region Public Methods

	/// <summary>
	/// Perform any initialization tasks that must be performed before the object can be used by the application.
	/// This should be called after the core properties from the data store have been assigned.
	/// </summary>
	/// <exception cref="System.InvalidOperationException">Thrown when this method is called more than once during 
	/// the application's lifetime.</exception>
	public void initialize(){
		//#region Validation

		if (isInitialized)	{
			throw new UnsupportedOperationException("The GallerySetting instance has already been initialized. It cannot be initialized more than once.");
		}

		//#endregion

		//String contentObjectPath = ContentObjectPath;
		String thumbnailPath = (StringUtils.isBlank(this.thumbnailPath) ? contentObjectPath : this.thumbnailPath);
		String optimizedPath = (StringUtils.isBlank(this.optimizedPath) ? contentObjectPath : this.optimizedPath);

		if (contentObjectPathIsReadOnly)
			validateReadOnlyGallery(this.contentObjectPath, thumbnailPath, optimizedPath);

		// Calculate and verify a few file paths, but only for "real" gallery settings, not the template one.
		if (!isTemplate){
			// Setting the FullContentObjectPath property will throw an exception if the directory does not exist or is not writeable.
			String physicalAppPath = AppSettings.getInstance().getCscDirectory();

			fullContentObjectPath = HelperFunctions.calculateFullPath(physicalAppPath, this.contentObjectPath);

			// The property setter for the FullThumbnailPath and FullOptimizedPath properties will throw an exception if the directory 
			// does not exist or is not writeable.
			fullThumbnailPath = HelperFunctions.calculateFullPath(physicalAppPath, thumbnailPath);
			fullOptimizedPath = HelperFunctions.calculateFullPath(physicalAppPath, optimizedPath);
		}

		isInitialized = true;
	}

	/// <summary>
	/// Persist the current gallery settings to the data store. Automatically clears and then reloads the gallery settings
	/// from the data store.
	/// </summary>
	/// <overload>
	/// Persist the current gallery settings to the data store.
	/// </overload>
	public void save() throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		save(true);
	}

	/// <summary>
	/// Persist the current gallery settings to the data store, optionally modifying the default behavior of clearing
	/// and then reloading the gallery settings from the data store.
	/// </summary>
	/// <param name="forceReloadFromDataStore">If set to <c>true</c>, clear the gallery settings stored in memory, which will
	/// force loading them from the data store. Setting this to <c>false</c> can be useful when updating a simple property that
	/// does not require a complex recalculation (like, say the <see cref="UsersToNotifyWhenErrorOccurs"/> does). It may also
	/// be needed when a separate thread is persisting the data and no instance of HttpContext exists, which can cause an
	/// exception in the DotNetNuke module during the reload process in the web layer.</param>
	public void save(boolean forceReloadFromDataStore) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		validateSave();

		CMUtils.saveGallerySettings(this);

		// Clear the settings stored in static variables so they are retrieved from the data store during the next access.
		GallerySettingsCollection gallerySettings = CMUtils.loadGallerySettings();
		if (forceReloadFromDataStore){
			synchronized (gallerySettings)
			{
				gallerySettings.clear();
			}
		}

		// Invoke the GallerySettingsSaved event. This will be implemented in the web layer, which will finish populating any 
		// properties that can't be done here, such as those of type <see cref="IUserAccountCollection" /> (since they need
		// access to the Membership provider, which the business layer has no knowledge of).
		GallerySettingsEventArgs event = new GallerySettingsEventArgs(this, this.galleryId);
		listeners.forEach(l -> {
			try {
				l.gallerySettingsSaved(event);
			} catch (UnsupportedContentObjectTypeException | InvalidGalleryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		/*EventHandler<GallerySettingsEventArgs> gallerySaved = GallerySettingsSaved;
		if (gallerySaved != null)
		{
			gallerySaved(null, new GallerySettingsEventArgs(this.GalleryId));
		}*/
	}

	//#endregion
	
	//#region Private Functions
	
	/// <summary>
	/// Verifies the current instance can be saved. Throws a <see cref="BusinessException" /> if it cannot.
	/// </summary>
	/// <exception cref="BusinessException">Thrown when the current instance cannot be saved.</exception>
	private void validateSave()	{
		if (!isWritable){
			throw new BusinessException(MessageFormat.format("This gallery setting container (Gallery ID {0}, {1}) is not updateable.", this.galleryId, getClass()));
		}
	}

	private void validateReadOnlyGallery(String contentObjectPath, String thumbnailPath, String optimizedPath){
		// When a gallery is read only, the following must be true:
		// 1. The thumbnail and optimized path must be different than the content object path.
		// 2. The SynchAlbumTitleAndDirectoryName setting must be false.
		// 3. The EnableUserAlbum setting must be false.
		if ((contentObjectPath.equalsIgnoreCase(thumbnailPath)) || (contentObjectPath.equalsIgnoreCase(optimizedPath)))	{
			throw new BusinessException(MessageFormat.format("Invalid configuration. A read-only gallery requires that the thumbnail and optimized file paths be different than the original content objects path. contentObjectPath={0}; thumbnailPath={1}; optimizedPath={2}", contentObjectPath, thumbnailPath, optimizedPath));
		}

		if (synchAlbumTitleAndDirectoryName){
			throw new BusinessException("Invalid configuration. A read-only gallery requires that the automatic renaming of directory names be disabled. Set this property on the Content Objects - General page in the Site admin area, or update it directly in the database (it is the SynchAlbumTitleAndDirectoryName property in the gallery settings table).");
		}

		if (enableUserAlbum){
			throw new BusinessException("Invalid configuration. A read-only gallery requires that user albums be disabled. Set this property on the Content Objects - General page in the Site admin area, or update it directly in the database (it is the EnableUserAlbum property in the gallery settings table).");
		}
	}
	
	/*private static void AssignUserAccountsProperty(GallerySettings gallerySetting, String property, String[] userNames)
	{
		UserAccountCollection userAccounts = (UserAccountCollection)property.GetValue(gallerySetting, null);

		foreach (String userName in userNames)
		{
			if (!String.IsNullOrEmpty(userName.Trim()))
			{
				userAccounts.Add(new UserAccount(userName.Trim()));
			}
		}
	}

	private static void AssignMetadataDisplaySettingsProperty(GallerySettings gallerySetting, String property, String metadataString)
	{
		var metaItemsList = Newtonsoft.Json.JsonConvert.DeserializeObject<List<MetadataDefinition>>(metadataString);

		IMetadataDefinitionCollection metadataItems = (IMetadataDefinitionCollection)property.GetValue(gallerySetting, null);

		foreach (var mi in metaItemsList)
		{
			metadataItems.Add(mi);
		}

		metadataItems.Validate();
	}

	private static void AssignContentEncoderSettingsProperty(GallerySettings gallerySetting, String property, String[] mediaEncodings)
	{
		IContentEncoderSettingsCollection mediaEncoderSettings = (IContentEncoderSettingsCollection)property.GetValue(gallerySetting, null);
		int seq = 0;

		foreach (String mediaEncStr in mediaEncodings)
		{
			// Each String item is double-pipe-delimited. Ex: ".avi||.mp4||-i {SourceFilePath} {DestinationFilePath}"
			String[] mediaEncoderItems = mediaEncStr.Split(new[] { "||" }, 3, StringSplitOptions.None);

			if (mediaEncoderItems.Length != 3)
			{
				throw new ArgumentOutOfRangeException(MessageFormat.format("GallerySetting.RetrieveGallerySettingsFromDataStore cannot parse the media encoder definitions for property {0}. Encountered invalid String: '{1}'", property.Name, mediaEncStr));
			}

			mediaEncoderSettings.Add(new ContentEncoderSettings(mediaEncoderItems[0], mediaEncoderItems[1], mediaEncoderItems[2], seq));
			seq++;
		}

		mediaEncoderSettings.Validate();
	}*/

	/// <summary>
	/// Assigns the <paramref name="value" /> to the specified <paramref name="property" /> of the <paramref name="gallerySetting" />
	/// instance. The <paramref name="value" /> is converted to the appropriate enumeration before assignment.
	/// </summary>
	/// <param name="gallerySetting">The gallery setting instance containing the <paramref name="property" /> to assign.</param>
	/// <param name="property">The property to assign the <paramref name="value" /> to.</param>
	/// <param name="value">The value to assign to the <paramref name="property" />.</param>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when <paramref name="value" /> cannot be parsed into a
	/// <see cref="MetadataItemName" /> value.</exception>
	public static void assignMetadataItemNameProperty(GallerySettings gallerySetting, String property, String value){
		MetadataItemName metaName;

		try{
			metaName = MetadataItemName.parse(value);
		}catch (ArgumentException ex){
			throw new ArgumentOutOfRangeException(MessageFormat.format("GallerySetting.RetrieveGallerySettingsFromDataStore cannot convert the String {0} to a MetadataItemName enumeration value.", value), ex);
		}

		Reflections.invokeSetter(gallerySetting, property, metaName);
	}

	/// <summary>
	/// Assigns the <paramref name="value" /> to the specified <paramref name="property" /> of the <paramref name="gallerySetting" />
	/// instance. The <paramref name="value" /> is converted to the appropriate enumeration before assignment.
	/// </summary>
	/// <param name="gallerySetting">The gallery setting instance containing the <paramref name="property" /> to assign.</param>
	/// <param name="property">The property to assign the <paramref name="value" /> to.</param>
	/// <param name="value">The value to assign to the <paramref name="property" />.</param>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when <paramref name="value" /> cannot be parsed into a
	/// <see cref="ContentObjectTransitionType" /> value.</exception>
	public static void assignContentObjectTransitionTypeProperty(GallerySettings gallerySetting, String property, String value){
		ContentObjectTransitionType transitionType;

		try{
			transitionType = ContentObjectTransitionType.parse(value);
		}catch (ArgumentException ex){
			throw new ArgumentOutOfRangeException(MessageFormat.format("GallerySetting.RetrieveGallerySettingsFromDataStore cannot convert the String {0} to a ContentObjectTransitionType enumeration value. The following values are valid: None, Fade", value), ex);
		}

		Reflections.invokeSetter(gallerySetting, property, transitionType);
	}

	/// <summary>
	/// Assigns the <paramref name="value" /> to the specified <paramref name="property" /> of the <paramref name="gallerySetting" />
	/// instance. The <paramref name="value" /> is converted to the appropriate enumeration before assignment.
	/// </summary>
	/// <param name="gallerySetting">The gallery control setting instance containing the <paramref name="property" /> to assign.</param>
	/// <param name="property">The property to assign the <paramref name="value" /> to.</param>
	/// <param name="value">The value to assign to the <paramref name="property" />.</param>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when <paramref name="value" /> cannot be parsed into a
	/// <see cref="SlideShowType" /> value.</exception>
	public static void assignSlideShowTypeProperty(GallerySettings gallerySetting, String property, String value){
		SlideShowType ssType;

		try	{
			ssType = (SlideShowType.parse(value));
		}catch (ArgumentException ex){
			throw new ArgumentOutOfRangeException(MessageFormat.format("GallerySettings.AssignSlideShowTypeProperty cannot convert the String {0} to a SlideShowType enumeration value. The following values are valid: NotSet, Inline, FullScreen", value), ex);
		}

		Reflections.invokeSetter(gallerySetting, property, ssType);
	}

	/// <summary>
	/// Assigns the <paramref name="value" /> to the specified <paramref name="property" /> of the <paramref name="gallerySetting" />
	/// instance. The <paramref name="value" /> is converted to the appropriate enumeration before assignment.
	/// </summary>
	/// <param name="gallerySetting">The gallery setting instance containing the <paramref name="property" /> to assign.</param>
	/// <param name="property">The property to assign the <paramref name="value" /> to.</param>
	/// <param name="value">The value to assign to the <paramref name="property" />.</param>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when <paramref name="value" /> cannot be parsed into a
	/// <see cref="ContentAlignment" /> value.</exception>
	public static void assignContentAlignmentProperty(GallerySettings gallerySetting, String property, String value){
		ContentAlignment contentAlignment;

		try	{
			contentAlignment = (ContentAlignment.parse(value));
		}catch (ArgumentException ex){
			throw new ArgumentOutOfRangeException(MessageFormat.format("GallerySetting.RetrieveGallerySettingsFromDataStore cannot convert the String {0} to a ContentAlignment enumeration value. The following values are valid: TopLeft, TopCenter, TopRight, MiddleLeft, MiddleCenter, MiddleRight, BottomLeft, BottomCenter, BottomRight", value), ex);
		}

		Reflections.invokeSetter(gallerySetting, property, contentAlignment);
	}

	/// <summary>
	/// Assigns the <paramref name="value" /> to the specified <paramref name="property" /> of the <paramref name="gallerySetting" />
	/// instance. The <paramref name="value" /> is converted to the appropriate enumeration before assignment.
	/// </summary>
	/// <param name="gallerySetting">The gallery setting instance containing the <paramref name="property" /> to assign.</param>
	/// <param name="property">The property to assign the <paramref name="value" /> to.</param>
	/// <param name="value">The value to assign to the <paramref name="property" />.</param>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when <paramref name="value" /> cannot be parsed into a
	/// <see cref="PagerPosition" /> value.</exception>
	public static void assignPagerPositionProperty(GallerySettings gallerySetting, String property, String value)	{
		PagerPosition pagerPosition;

		try	{
			pagerPosition = (PagerPosition.parse(value));
		}catch (ArgumentException ex){
			throw new ArgumentOutOfRangeException(MessageFormat.format("GallerySetting.RetrieveGallerySettingsFromDataStore cannot convert the String {0} to a PagerPosition enumeration value. The following values are valid: Top, Bottom, TopAndBottom", value), ex);
		}

		Reflections.invokeSetter(gallerySetting, property, pagerPosition);
	}
	
	//#endregion
	
	@Override
	public int compareTo(GallerySettings o) {
		if (o == null)
			return 1;
		else{
			return Long.compare(this.galleryId, o.galleryId);
		}
	}
}

