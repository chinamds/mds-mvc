package com.mds.cm.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;

import com.mds.cm.content.AlbumBo;
import com.mds.cm.content.ContentConversionQueue;
import com.mds.cm.content.ContentObjectBo;
import com.mds.cm.content.ContentTemplateBo;
import com.mds.cm.content.DisplayObject;
import com.mds.cm.content.MimeTypeBo;
import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidContentObjectException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.core.ContentObjectType;
import com.mds.core.DisplayObjectType;
import com.mds.core.ResourceId;
import com.mds.core.exception.ArgumentException;
import com.mds.core.exception.ArgumentNullException;
import com.mds.core.exception.BusinessException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.core.exception.WebException;
import com.mds.i18n.util.I18nUtils;
import com.mds.sys.util.AppSettings;
import com.mds.sys.util.UserUtils;
import com.mds.util.FileMisc;
import com.mds.util.HelperFunctions;
import com.mds.util.StringUtils;
import com.mds.util.Utils;

/// <summary>
/// Provides functionality for generating the HTML that can be sent to a client browser to render a
/// particular content object. Objects implementing this interface use the HTML templates in the configuration
/// file. Replaceable parameters in the template are indicated by the open and close brackets, such as 
/// {Width}. These parameters are replaced with the relevant values.
/// TODO: Add caching functionality to speed up the ability to generate HTML.
/// </summary>
public class ContentObjectHtmlBuilder{
	//#region Private Fields

	private String uniquePrefixId;
	private ContentTemplateBo mediaTemplate;

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="ContentObjectHtmlBuilder"/> class.
	/// </summary>
	/// <param name="options">The options that will dictate the HTML and URL generation.</param>
	/// <exception cref="System.ArgumentNullException">Thrown when <paramref name="options" /> is null.</exception>
	/// <exception cref="System.ArgumentException">Thrown when <paramref name="options" /> contains one or more
	/// invalid values.</exception>
	public ContentObjectHtmlBuilder(ContentObjectHtmlBuilderOptions options){
		if (options == null)
			throw new ArgumentNullException("options");

		if ((options.Browsers == null) || (options.Browsers.size() < 1))
			throw new ArgumentException(I18nUtils.getMessage("contentObjectHtmlBuilder.Ctor_InvalidBrowsers_Msg"));

		if (options.ContentObject == null)
			throw new ArgumentException("The ContentObject property of the options parameter cannot be null.", "options");

		if (options.DisplayType == DisplayObjectType.Unknown)
			throw new ArgumentException("The DisplayType property of the options parameter cannot be DisplayObjectType.Unknown.", "options");

		Options = options;
	}

	//#endregion

	//#region Properties

	/// <summary>
	/// Gets or sets the options that dictate the HTML and URL generation.
	/// </summary>
	private ContentObjectHtmlBuilderOptions Options;

	/// <summary>
	/// Gets the content object.
	/// </summary>
	public ContentObjectBo getContentObject(){
		return Options.ContentObject;
	}

	/// <summary>
	/// Gets the ID of the content object associated with <see cref="ContentObject" />. When <see cref="ContentObject" />
	/// is an <see cref="AlbumBo" />, this property returns the ID of the thumbnail image or zero if no thumbnail
	/// image is assigned.
	/// </summary>
	private long getContentObjectId() throws WebException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException{
		if (getContentObject() instanceof AlbumBo){
			return getAlbumThumbnailId();
		}

		return getContentObject().getId();
	}

	/// <summary>
	/// Gets the MIME type of the <see cref="DisplayObject" /> of the <see cref="ContentObject" />.
	/// </summary>
	public MimeTypeBo getMimeType() throws InvalidGalleryException{
		return getDisplayObject().getMimeType();
	}

	/// <summary>
	/// Gets the physical path to this content object, including the object's name. Example:
	/// C:\Inetpub\wwwroot\MDS\contentobjects\Summer 2005\sunsets\desert sunsets\sonorandesert.jpg
	/// </summary>
	public String getContentObjectPhysicalPath() throws InvalidGalleryException{
		return getDisplayObject().getFileNamePhysicalPath();
	}

	/// <summary>
	/// Gets the width of this object, in pixels.
	/// </summary>
	public int getWidth() throws InvalidGalleryException{
		return getDisplayObject().getWidth();
	}

	/// <summary>
	/// Gets the height of this object, in pixels.
	/// </summary>
	public int getHeight() throws InvalidGalleryException{
		return getDisplayObject().getHeight();
	}

	/// <summary>
	/// Gets an <see cref="System.Array"/> of browser ids for the current browser. This is a list of Strings,
	/// ordered from most general to most specific, that represent the various categories of browsers the current
	/// browser belongs to.
	/// </summary>
	public List getBrowsers(){
		return Options.Browsers;
	}

	private ContentTemplateBo getContentTemplate() throws InvalidGalleryException{
		if (mediaTemplate == null)
			mediaTemplate = getMimeType().getContentTemplate(getBrowsers());
		
		return mediaTemplate;
	}

	/// <summary>
	/// Gets the HTML from the media template corresponding to the <see cref="MimeType" /> and <see cref="Browsers" />.
	/// If no media template exists, an empty String is returned.
	/// </summary>
	private String getHtmlTemplate() throws InvalidGalleryException{
		return (getContentTemplate() == null ? StringUtils.EMPTY : getContentTemplate().HtmlTemplate);
	}

	/// <summary>
	/// Gets the JavaScript from the media template corresponding to the <see cref="MimeType" /> and <see cref="Browsers" />.
	/// If no media template exists, an empty String is returned.
	/// </summary>
	private String getScriptTemplate() throws InvalidGalleryException{
		return (getContentTemplate() == null ? StringUtils.EMPTY : getContentTemplate().ScriptTemplate);
	}

	/// <summary>
	/// Gets the type of the display object.
	/// </summary>
	public DisplayObjectType getDisplayType(){
		return Options.DisplayType;
	}

	/// <summary>
	/// Gets the display object of the <see cref="ContentObject" />.
	/// </summary>
	public DisplayObject getDisplayObject() throws InvalidGalleryException{
		switch (getDisplayType()){
			case Thumbnail:
				return getContentObject().getThumbnail();
			case Optimized:
				return getContentObject().getOptimized();
			default:
				return getContentObject().getOriginal();
		}
	}

	/// <summary>
	/// Gets a generated String about twelve characters long that can be used as a unique identifier, such as the ID of
	/// an HTML element. The value is generated the first time the property is accessed, and subsequent reads return
	/// the same value. There is currently no support for generating more than one ID during the lifetime of an instance.
	/// Ex: "mds_1c135176ed"
	/// </summary>
	private String getUniquePrefixId(){
		if (StringUtils.isBlank(uniquePrefixId)){
			uniquePrefixId = StringUtils.join("mds_", UUID.randomUUID().toString().replace("-", StringUtils.EMPTY).substring(0, 10));
		}

		return uniquePrefixId;
	}

	/// <summary>
	/// Gets the URL, relative to the website root and optionally including any query String parameters,
	/// to the page any generated URLs should point to. Examples: "/dev/ds/gallery.aspx",
	/// "/dev/ds/gallery.aspx?g=admin_email&amp;aid=2389"
	/// </summary>
	public String getDestinationPageUrl(){
		return Options.DestinationPageUrl; 
	}

	/// <summary>
	/// Gets the URI scheme, DNS host name or IP address, and port number for the current application. 
	/// Examples: "http://www.site.com", "http://localhost", "http://127.0.0.1", "http://godzilla"
	/// </summary>
	public String getHostUrl(){
		return Options.HostUrl; 
	}

	/// <summary>
	/// Gets the path, relative to the web site root, to the current web application.
	/// Example: "/dev/gallery".
	/// </summary>
	public String getAppRoot(){
		return Options.AppRoot; 
	}

	/// <summary>
	/// Gets the path, relative to the web site root, to the directory containing the MDS System user 
	/// controls and other resources. Example: "/dev/gallery/ds".
	/// </summary>
	public String getGalleryRoot(){
		return Options.GalleryRoot;
	}

	/// <summary>
	/// Gets a value indicating whether the current user is authenticated.
	/// </summary>
	/// <value><c>true</c> if the current user is authenticated; otherwise, <c>false</c>.</value>
	public boolean isAuthenticated(){
		return Options.IsAuthenticated;
	}

	//#endregion

	//#region Public Methods

	/// <summary>
	/// Generate the HTML that can be sent to a browser to render the content object. The HTML is generated from the
	/// media template associated with the content objects MIME type. If <see cref="ContentObject" /> is an <see cref="AlbumBo" />,
	/// <see cref="StringUtils.EMPTY" /> is returned. Guaranteed to not return null.
	/// </summary>
	/// <returns>Returns a String of valid HTML that can be sent to a browser.</returns>
	public String generateHtml() throws Exception{
		if (getContentObject() instanceof AlbumBo)
			return StringUtils.EMPTY;

		String htmlOutput = genHtmlTemplate();

		htmlOutput = htmlOutput.replace("{HostUrl}", getHostUrl());
		htmlOutput = htmlOutput.replace("{ContentObjectUrl}", getContentObjectUrl());
		htmlOutput = htmlOutput.replace("{MimeType}", getMimeType().getBrowserMimeType());
		htmlOutput = htmlOutput.replace("{Width}", Integer.toString(getWidth()));
		htmlOutput = htmlOutput.replace("{Height}", Integer.toString(getHeight()));
		htmlOutput = htmlOutput.replace("{Title}", getContentObject().getTitle());
		htmlOutput = htmlOutput.replace("{TitleNoHtml}", HelperFunctions.removeHtmlTags(getContentObject().getTitle(), true));
		htmlOutput = htmlOutput.replace("{UniqueId}", getUniquePrefixId());

		boolean autoStartContentObject = CMUtils.loadGallerySetting(getContentObject().getGalleryId()).getAutoStartContentObject();

		// Replace {AutoStartContentObjectText} with "true" or "false".
		htmlOutput = htmlOutput.replace("{AutoStartContentObjectText}", Boolean.toString(autoStartContentObject).toLowerCase(Locale.ROOT));

		// Replace {AutoStartContentObjectInt} with "1" or "0".
		htmlOutput = htmlOutput.replace("{AutoStartContentObjectInt}", autoStartContentObject ? "1" : "0");

		// Replace {AutoPlay} with "autoplay" or "".
		htmlOutput = htmlOutput.replace("{AutoPlay}", autoStartContentObject ? "autoplay" : StringUtils.EMPTY);

		if (htmlOutput.contains("{ContentObjectAbsoluteUrlNoHandler}"))
			htmlOutput = replaceContentObjectAbsoluteUrlNoHandlerParameter(htmlOutput);

		if (htmlOutput.contains("{ContentObjectRelativeUrlNoHandler}"))
			htmlOutput = replaceContentObjectRelativeUrlNoHandlerParameter(htmlOutput);

		if (htmlOutput.contains("{GalleryPath}"))
			htmlOutput = htmlOutput.replace("{GalleryPath}", getGalleryRoot());

		return htmlOutput;
	}

	/// <summary>
	/// Generate the JavaScript that can be sent to a browser to assist with rendering the content object. 
	/// If <see cref="ContentObject" /> is an <see cref="AlbumBo" />, <see cref="StringUtils.EMPTY" /> is returned.
	/// If the configuration file does not specify a scriptOutput template for this MIME type, an empty String is returned.
	/// </summary>
	/// <returns>Returns the JavaScript that can be sent to a browser to assist with rendering the content object.</returns>
	public String generateScript() throws Exception{
		if (getContentObject() instanceof AlbumBo)
			return StringUtils.EMPTY;

		if ((getMimeType().getMajorType().equalsIgnoreCase("IMAGE")) && (isImageBrowserIncompatible()))
			return StringUtils.EMPTY; // Browsers can't display this image.

		String scriptOutput = getScriptTemplate();

		if (StringUtils.isBlank(scriptOutput))
			return StringUtils.EMPTY; // No ECMA script rendering info in config file.

		scriptOutput = scriptOutput.replace("{HostUrl}", getHostUrl());
		scriptOutput = scriptOutput.replace("{ContentObjectUrl}", getContentObjectUrl());
		scriptOutput = scriptOutput.replace("{MimeType}", getMimeType().getBrowserMimeType());
		scriptOutput = scriptOutput.replace("{Width}", Integer.toString(getWidth()));
		scriptOutput = scriptOutput.replace("{Height}", Integer.toString(getHeight()));
		scriptOutput = scriptOutput.replace("{Title}", getContentObject().getTitle());
		scriptOutput = scriptOutput.replace("{TitleNoHtml}", HelperFunctions.removeHtmlTags(getContentObject().getTitle(), true));
		scriptOutput = scriptOutput.replace("{UniqueId}", getUniquePrefixId());

		boolean autoStartContentObject = CMUtils.loadGallerySetting(getContentObject().getGalleryId()).getAutoStartContentObject();

		// Replace {AutoStartContentObjectText} with "true" or "false".
		scriptOutput = scriptOutput.replace("{AutoStartContentObjectText}", Boolean.toString(autoStartContentObject).toLowerCase(Locale.ROOT));

		// Replace {AutoStartContentObjectInt} with "1" or "0".
		scriptOutput = scriptOutput.replace("{AutoStartContentObjectInt}", autoStartContentObject ? "1" : "0");

		if (scriptOutput.contains("{ContentObjectAbsoluteUrlNoHandler}"))
			scriptOutput = replaceContentObjectAbsoluteUrlNoHandlerParameter(scriptOutput);

		if (scriptOutput.contains("{ContentObjectRelativeUrlNoHandler}"))
			scriptOutput = replaceContentObjectRelativeUrlNoHandlerParameter(scriptOutput);

		if (scriptOutput.contains("{GalleryPath}"))
			scriptOutput = scriptOutput.replace("{GalleryPath}", getGalleryRoot());

		return scriptOutput;
	}

	/// <summary>
	/// Generate an absolute URL to the content object. The url can be assigned to the src attribute of an img tag.
	/// Ex: "http://site.com/gallery/ds/handler/getmedia.ashx?moid=34&amp;dt=1&amp;g=1"
	/// The query String parameter will be encrypted if that option is enabled. If the <see cref="ContentObject" />
	/// is an album, the URL points to the album's thumbnail content object.
	/// </summary>
	/// <returns>Gets the absolute URL to the content object.</returns>
	public String getContentObjectUrl() throws UnsupportedContentObjectTypeException, WebException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException, UnsupportedEncodingException	{
		String queryString = StringUtils.format("moid={0}&dt={1}&g={2}", getContentObjectId(), getDisplayType(), getContentObject().getGalleryId());

		// If necessary, encrypt, then URL encode the query String.
		if (AppSettings.getInstance().getEncryptContentObjectUrlOnClient())	{
			queryString = HelperFunctions.urlEncode(HelperFunctions.encrypt(queryString));
		}

		//return StringUtils.join(getHostUrl(), getGalleryRoot(), "/services/api/getmedia?", queryString);
		return StringUtils.join(getAppRoot(), "/services/api/contentitems/getmedia?", queryString);
	}

	/// <summary>
	/// Get an absolute URL for the page containing the current content object. The URL refers to the page
	/// specified in <see cref="DestinationPageUrl" />.
	/// Examples: "http://site.com/gallery/default.aspx?moid=283", "http://site.com/gallery/default.aspx?aid=97"
	/// </summary>
	/// <returns>Returns an absolute URL for the page containing the current content object..</returns>
	public String getPageUrl(){
		if (getContentObject() instanceof AlbumBo){
			return getPageUrl(ResourceId.album, "aid={0}", getContentObject().getId());
		}

		return getPageUrl(ResourceId.contentobject, "moid={0}", getContentObject().getId());
	}

	/// <summary>
	/// Generates the HTML to display a nicely formatted thumbnail image, including a
	/// border, shadows and (possibly) rounded corners.
	/// </summary>
	/// <returns>Returns HTML that displays a nicely formatted thumbnail image.</returns>
	public String getThumbnailHtml() throws UnsupportedContentObjectTypeException, UnsupportedEncodingException, InvalidGalleryException, WebException, InvalidAlbumException, UnsupportedImageTypeException{
		return StringUtils.format(
"<div class='mds_i_c'>" +  
	"<img src='{1}' title='{2}' alt='{2}' style='width:{0}px;height:{3}px;' class='mds_thmb_img' />" + 
"</div>",
																getContentObject().getThumbnail().getWidth(), // 0
																getContentObjectUrl(), // 1
																HelperFunctions.htmlEncode(HelperFunctions.removeHtmlTags(getContentObject().getTitle())), // 2
																getContentObject().getThumbnail().getHeight() // 3
			);
	}
	
	public String getThumbnailHtmlFitSize() throws UnsupportedContentObjectTypeException, UnsupportedEncodingException, InvalidGalleryException, WebException, InvalidAlbumException, UnsupportedImageTypeException{
		int size = getContentObject().getThumbnail().getWidth() > getContentObject().getThumbnail().getHeight() ? getContentObject().getThumbnail().getWidth() : getContentObject().getThumbnail().getHeight();
		return StringUtils.format(
"<div class='mds_i_c'>" +  
	"<img src='{1}' title='{2}' alt='{2}' style='width:{0}px;height:{3}px;' class='mds_thmb_img' />" + 
"</div>",
																size, // 0
																getContentObjectUrl(), // 1
																HelperFunctions.htmlEncode(HelperFunctions.removeHtmlTags(getContentObject().getTitle())), // 2
																size // 3
			);
	}	
	
	public String getThumbnailHtml(final String title) throws UnsupportedContentObjectTypeException, UnsupportedEncodingException, InvalidGalleryException, WebException, InvalidAlbumException, UnsupportedImageTypeException{
		return StringUtils.format(
"	<div class='mds_i_c'>" +  
"		<img src='{1}' title='{2}' alt='{2}' style='width:{0}px;height:{3}px;' class='mds_thmb_img' />" + 
"	</div>",
																getContentObject().getThumbnail().getWidth(), // 0
																getContentObjectUrl(), // 1
																HelperFunctions.htmlEncode(HelperFunctions.removeHtmlTags(title)), // 2
																getContentObject().getThumbnail().getHeight() // 3
			);
	}
	
	public String getThumbnailHtml(final String title, final String url) throws InvalidGalleryException, UnsupportedContentObjectTypeException, UnsupportedEncodingException, WebException, InvalidAlbumException, UnsupportedImageTypeException{
		return StringUtils.format(
"	<a class='mds_i_c mds_thmbLink' href='" + url + "'>" +  
"		<img src='{1}' title='{2}' alt='{2}' style='width:{0}px;height:{3}px;' class='mds_thmb_img' />" + 
"	</a>",
																getContentObject().getThumbnail().getWidth(), // 0
																getContentObjectUrl(), // 1
																HelperFunctions.htmlEncode(HelperFunctions.removeHtmlTags(title)), // 2
																getContentObject().getThumbnail().getHeight() // 3
			);
	}
	
	/// <summary>
	/// Generates the HTML to display a nicely formatted thumbnail image, including a
	/// border, with boostrap style.
	/// </summary>
	/// <returns>Returns HTML that displays a nicely formatted thumbnail image.</returns>
	public String getBSThumbnailHtml() throws UnsupportedContentObjectTypeException, UnsupportedEncodingException, InvalidGalleryException, WebException, InvalidAlbumException, UnsupportedImageTypeException {
		return StringUtils.format(
"	<div class='mds_i_c'>" +				
"		<img src='{1}' title='{2}' alt='{2}' style='max-width: 100% !important;width: {0}px !important;height: {3}px !important;' class='mds_thmb_img' />" + //width:{0}px;height:{3}px;
"	</div>",
																getContentObject().getThumbnail().getWidth(), // 0
																getContentObjectUrl(), // 1
																HelperFunctions.htmlEncode(HelperFunctions.removeHtmlTags(getContentObject().getTitle())), // 2
																getContentObject().getThumbnail().getHeight() // 3
			);
	}

	//#endregion

	//#region Public Static Methods

	/// <summary>
	/// Gets an instance of <see cref="ContentObjectHtmlBuilderOptions" /> that can be supplied to the 
	/// <see cref="ContentObjectHtmlBuilder" /> constructor. This method requires access to <see cref="HttpContext.Current" />.
	/// </summary>
	/// <param name="contentObject">The content object. May be null. If null, <see cref="ContentObjectHtmlBuilderOptions.ContentObject" />
	/// must be assigned before passing this instance to the <see cref="ContentObjectHtmlBuilder" /> constructor.</param>
	/// <param name="displayType">The display type. Optional. If not assigned or set to <see cref="DisplayObjectType.Unknown" />,
	/// <see cref="ContentObjectHtmlBuilderOptions.DisplayType" /> must be assigned before passing this instance to the 
	/// <see cref="ContentObjectHtmlBuilder" /> constructor.</param>
	/// <returns>An instance of <see cref="ContentObjectHtmlBuilderOptions" />.</returns>
	public static ContentObjectHtmlBuilderOptions getContentObjectHtmlBuilderOptions(ContentObjectBo contentObject, HttpServletRequest request) {
		return getContentObjectHtmlBuilderOptions(contentObject, DisplayObjectType.Unknown, request);
	}
	
	public static ContentObjectHtmlBuilderOptions getContentObjectHtmlBuilderOptions(ContentObjectBo contentObject, DisplayObjectType displayType, HttpServletRequest request){
		return new ContentObjectHtmlBuilderOptions(contentObject, displayType, Utils.getBrowserIdsForCurrentRequest(request), Utils.getCurrentPageUrl(request), UserUtils.isAuthenticated(), Utils.getHostUrl(request), Utils.getAppRoot(request), Utils.getGalleryRoot(request));
	}

	//#endregion

	//#region Private Methods

	/// <summary>
	/// Replace the replacement parameter {ContentObjectAbsoluteUrlNoHandler} with an URL that points directly to the content object
	/// (ex: /gallery/videos/birthdayvideo.wmv). A BusinessException is thrown if the content objects directory is not
	/// within the web application directory. Note that using this parameter completely bypasses the HTTP handler that 
	/// normally streams the content object. The consequence is that there is no security check when the content object request
	/// is made and no watermarks are applied, even if watermark functionality is enabled. This option should only be
	/// used when it is not important to restrict access to the content objects.
	/// </summary>
	/// <param name="htmlOutput">A String representing the HTML that will be sent to the browser for the current content object.
	/// It is based on the template stored in the media template table.</param>
	/// <returns>Returns the htmlOutput parameter with the {ContentObjectAbsoluteUrlNoHandler} String replaced by the URL to the media
	/// object.</returns>
	/// <exception cref="MDS.EventLogs.CustomExceptions.BusinessException">Thrown when the content objects 
	/// directory is not within the web application directory.</exception>
	private String replaceContentObjectAbsoluteUrlNoHandlerParameter(String htmlOutput) throws InvalidGalleryException{
		String appPath = AppSettings.getInstance().getPhysicalApplicationPath();

		if (!StringUtils.startsWithIgnoreCase(getContentObjectPhysicalPath(), appPath))
			throw new BusinessException(MessageFormat.format("Expected this.ContentObjectPhysicalPath (\"{0}\") to start with AppSetting.Instance.PhysicalApplicationPath (\"{1}\"), but it did not. If the content objects are not stored within the MDS System web application, you cannot use the ContentObjectAbsoluteUrlNoHandler replacement parameter. Instead, use ContentObjectRelativeUrlNoHandler and specify the virtual path to your content object directory in the HTML template. For example: HtmlTemplate=\"<a href=\"{{HostUrl}}/media{{ContentObjectRelativeUrlNoHandler}}\">Click to open</a>\"", getContentObjectPhysicalPath(), appPath));

		String relativePath = StringUtils.strip(StringUtils.remove(getContentObjectPhysicalPath(), appPath.length()), File.separator);

		relativePath = HelperFunctions.urlEncode(relativePath, '\\');

		String directUrl = StringUtils.join(HelperFunctions.urlEncode(getAppRoot(), '/'), "/", relativePath.replace("\\", "/"));

		return htmlOutput.replace("{ContentObjectAbsoluteUrlNoHandler}", directUrl);
	}

	/// <summary>
	/// Replace the replacement parameter {ContentObjectRelativeUrlNoHandler} with an URL that is relative to the content objects
	/// directory and which points directly to the content object (ex: /videos/birthdayvideo.wmv). Note 
	/// that using this parameter completely bypasses the HTTP handler that normally streams the content object. The consequence 
	/// is that there is no security check when the content object request is made and no watermarks are applied, even if 
	/// watermark functionality is enabled. This option should only be used when it is not important to restrict access to 
	/// the content objects.
	/// </summary>
	/// <param name="htmlOutput">A String representing the HTML that will be sent to the browser for the current content object.
	/// It is based on the template stored in the media template table.</param>
	/// <returns>Returns the htmlOutput parameter with the {ContentObjectRelativeUrlNoHandler} String replaced by the URL to the media
	/// object.</returns>
	/// <exception cref="MDS.EventLogs.CustomExceptions.BusinessException">Thrown when the current content object's
	/// physical path does not start with the same text as AppSetting.Instance.ContentObjectPhysicalPath.</exception>
	/// <remarks>Typically this parameter is used instead of {ContentObjectAbsoluteUrlNoHandler} when the content objects directory 
	/// is outside of the web application. If the user wants to allow direct access to the content objects using this parameter, 
	/// she must first configure the content objects directory as a virtual directory in IIS. Then the path to this virtual directory 
	/// must be manually entered into one or more HTML templates, so that it prepends the relative url returned from this method.</remarks>
	/// <example>If the content objects directory has been set to D:\media and a virtual directory named gallery has been configured 
	/// in IIS that is accessible via http://yoursite.com/gallery, then you can configure the HTML template like this:
	/// HtmlTemplate="&lt;a href=&quot;http://yoursite.com/gallery{ContentObjectRelativeUrlNoHandler}&quot;&gt;Click to open&lt;/a&gt;"
	/// </example>
	private String replaceContentObjectRelativeUrlNoHandlerParameter(String htmlOutput) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		String moPath = CMUtils.loadGallerySetting(getContentObject().getGalleryId()).getFullContentObjectPath();

		if (!StringUtils.startsWithIgnoreCase(getContentObjectPhysicalPath(), moPath))
			throw new BusinessException(MessageFormat.format("Expected this.ContentObjectPhysicalPath (\"{0}\") to start with AppSetting.Instance.ContentObjectPhysicalPath (\"{1}\"), but it did not.", getContentObjectPhysicalPath(), moPath));

		String relativePath = StringUtils.strip(StringUtils.removeStart(getContentObjectPhysicalPath(), moPath.length()), File.separator);

		relativePath = HelperFunctions.urlEncode(relativePath, '\\');

		String relativeUrl = StringUtils.join("/", relativePath.replace("\\", "/"));

		return htmlOutput.replace("{ContentObjectRelativeUrlNoHandler}", relativeUrl);
	}

	/// <summary>
	/// Determines if the image can be displayed in a standard web browser. For example, JPG, JPEG, PNG and GIF images can
	/// be displayed; WMF and TIF cannot.
	/// </summary>
	/// <returns>Returns true if the image cannot be displayed in a standard browser (e.g. WMF, TIF); returns false if it can
	/// (e.g. JPG, JPEG, PNG and GIF).</returns>
	private boolean isImageBrowserIncompatible() throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		String extension = FileMisc.getExt(getContentObjectPhysicalPath());
		if (extension == null)	{
			return false;
		}

		String originalFileExtension = extension.toLowerCase();

		return ArrayUtils.indexOf(CMUtils.loadGallerySetting(getContentObject().getGalleryId()).getImageTypesStandardBrowsersCanDisplay(), originalFileExtension) < 0;
	}

	/// <summary>
	/// Gets the HTML template to use for rendering this content object. Guaranteed to not
	/// return null.
	/// </summary>
	/// <returns>Returns a String.</returns>
	private String genHtmlTemplate() throws Exception{
		if (getDisplayType() == DisplayObjectType.External)	{
			return getDisplayObject().getExternalHtmlSource();
		}

		boolean isInQueue = (getDisplayType() == DisplayObjectType.Optimized &&
			(getContentObject().getContentObjectType() == ContentObjectType.Audio || getContentObject().getContentObjectType() == ContentObjectType.Video) &&
			ContentConversionQueue.getInstance().isWaitingInQueueOrProcessing(getContentObjectId()));

		if (isInQueue){
			return StringUtils.format("<p class='mds_item_process_msg'>{0}</p>", I18nUtils.getMessage("uc.contentObjectView.Content_Object_Being_Processed_Text"));
		}

		boolean isBrowserIncompatibleImage = (getMimeType().getMajorType().equalsIgnoreCase("IMAGE")) && (isImageBrowserIncompatible());

		String htmlOutput = getHtmlTemplate();

		if (isBrowserIncompatibleImage || StringUtils.isBlank(htmlOutput)){
			// Either (1) no applicable template exists or (2) this is an image that can't be natively displayed in a 
			// browser (e.g. PSD, ICO, etc). Determine the appropriate message and return that as the HTML template.
			String url = HelperFunctions.addQueryStringParameter(getContentObjectUrl(), "sa=1"); // Get URL with the "send as attachment" query String parm
			String msg = I18nUtils.getMessage("uc.contentObjectView.Browser_Cannot_Display_Content_Object_Text", url);
			
			return StringUtils.format("<p class='mds_msgfriendly'>{0}</p>", msg);
		}

		return htmlOutput;
	}


	/// <summary>
	/// Get an absolute URL for the requested page (eg. "http://site.com/gallery/default.aspx?moid=283").
	/// This works similar to <see cref="Utils.GetUrl(ResourceId, String, object[])" /> except this has no 
	/// dependence on <see cref="HttpContext.Current" /> and it returns an absolute URL instead of a relative one.
	/// </summary>
	/// <param name="page">A <see cref="ResourceId"/> enumeration that represents the desired <see cref="Pages.GalleryPage"/>.</param>
	/// <param name="format">A format String whose placeholders are replaced by values in <paramref name="args"/>. Do not use a '?'
	/// or '&amp;' at the beginning of the format String. Example: "msg={0}".</param>
	/// <param name="args">The values to be inserted into the <paramref name="format"/> String.</param>
	/// <returns>Returns an absolute URL for the requested <paramref name="page"/>.</returns>
	private String getPageUrl(ResourceId page, String format, Object... args)	{
		String queryString = StringUtils.format(format, args);

		if ((page != ResourceId.album) && (page != ResourceId.contentobject)){
			// Don't use the "g" parameter for album or contentobject pages, since we can deduce it by looking for the 
			// aid or moid query String parms. This results in a shorter, cleaner URL.
			queryString = StringUtils.join("m=", page.toString(), "&", queryString);
		}

		return HelperFunctions.addQueryStringParameter(StringUtils.join(getHostUrl(), getDestinationPageUrl()), queryString);
	}

	/// <summary>
	/// Gets the content object ID for the album thumbnail. Relevant only when <see cref="ContentObject" /> is 
	/// an <see cref="AlbumBo" /> and <see cref="DisplayType" /> is <see cref="DisplayObjectType.Thumbnail" />;
	/// otherwise a <see cref="WebException" /> is thrown.
	/// </summary>
	/// <returns>The content object ID for the album thumbnail.</returns>
	/// <exception cref="WebException">Thrown when <see cref="ContentObject" /> is not an <see cref="AlbumBo" /> 
	/// or <see cref="DisplayType" /> is not <see cref="DisplayObjectType.Thumbnail" /></exception>
	private long getAlbumThumbnailId() throws WebException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException{
		if (!(getContentObject() instanceof AlbumBo)){
			throw new WebException(MessageFormat.format("The function GetAlbumThumbnailId should be called only when the content object is an album. Instead, it was a {0}.", getContentObject().getContentObjectType()));
		}

		if (getDisplayType() != DisplayObjectType.Thumbnail){
			throw new WebException(MessageFormat.format("The function GetAlbumThumbnailId should be called only when the display type is DisplayObjectType.Thumbnail. Instead, it was a {0}.", getDisplayType()));
		}

		if (getContentObject().getThumbnail().getContentObjectId() > 0){
			try	{
				ContentObjectBo contentObject = CMUtils.loadContentObjectInstance(getContentObject().getThumbnail().getContentObjectId());

				if (isAuthenticated() || (!contentObject.getParent().getIsPrivate() && !contentObject.getIsPrivate())){
					return contentObject.getId();
				}
			}catch (InvalidContentObjectException ce){
				// We'll get here if the ID for the thumbnail doesn't represent an existing content object.
			}
		}

		return 0; // 0 is a signal to getmedia.ashx to generate an empty album thumbnail image
	}

	//#endregion
}
