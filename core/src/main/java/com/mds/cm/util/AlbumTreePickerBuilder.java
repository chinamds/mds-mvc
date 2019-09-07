package com.mds.cm.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.mds.cm.content.AlbumBo;
import com.mds.cm.content.ContentObjectBo;
import com.mds.cm.content.ContentObjectBoCollection;
import com.mds.cm.content.GallerySettings;
import com.mds.cm.exception.GallerySecurityException;
import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidContentObjectException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.core.ContentObjectType;
import com.mds.core.DisplayObjectType;
import com.mds.core.exception.ArgumentNullException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.core.exception.WebException;
import com.mds.i18n.util.I18nUtils;
import com.mds.sys.util.AppSettings;
import com.mds.util.StringUtils;
import com.mds.util.Utils;

/// <summary>
/// A page-like user control that handles the Delete objects task.
/// </summary>
public class AlbumTreePickerBuilder{
	//#region Private Fields

	private List<ContentObjectBo> contentObjects;
	private AlbumBo album;
	private long albumId = Long.MIN_VALUE;
	private GallerySettings gallerySettings;

	//#endregion

	//#region Properties
	
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
	
	private GallerySettings getGallerySettings() throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		if (gallerySettings == null)
			gallerySettings = CMUtils.loadGallerySetting(this.getAlbum().getGalleryId());
		
		return gallerySettings;
	}

	/// <summary>
	/// Gets the content objects that are candidates for deleting.
	/// </summary>
	/// <value>The content objects that are candidates for deleting.</value>
	public List<ContentObjectBo> getContentObjects(){
		if (this.contentObjects == null)
			this.contentObjects = this.getAlbum().getChildContentObjects(ContentObjectType.All).toSortedList();
		
		return this.contentObjects;
	}
	
	//showContentType - show 'album' or 'contentobject' or 'contentobjectid' or 'all'.
	public  List<HashMap<String,Object>> generate(long id, String showContentType, String thumbSize, boolean sc, HttpServletRequest request) throws Exception{
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		if ("contentobjectid".equalsIgnoreCase(showContentType)) {
			ContentObjectBo contentObject = CMUtils.loadContentObjectInstance(id);
			album = (AlbumBo)contentObject.getParent();
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", getContentObjectText(contentObject.getTitle(), contentObject.getClass(), request));//title
			mapData.put("titleWidth", getTitleWidth(contentObject));//title Width
			mapData.put("thumbnailCssClass", getThumbnailCssClass(contentObject));//thumbnail Css Class
			mapData.put("id", getId(contentObject));//content Object id
			mapData.put("thumbnailHtml", getThumbnailHtml(contentObject, thumbSize, request));//Thumbnail Html
			mapData.put("isAlbumThumbnail", album.getThumbnailContentObjectId() == contentObject.getId());//is Album Thumbnail
			if (sc) {
				mapData.put("sc", true);//show checkbox
			}else {
				mapData.put("sc", shouldShowCheckbox(contentObject));//show checkbox
			}
			mapData.put("savings", getSavings(contentObject));//file size kb
			mapData.put("warningMsg", shouldShowNoOriginalFileMsg(contentObject) ? I18nUtils.getString("task.deleteOriginal.Not_Available_Text", request.getLocale()) : "");//warning message
				
			list.add(mapData);
		}else {
			album = AlbumUtils.loadAlbumInstance(id, true);
			for(ContentObjectBo contentObject : getContentObjects()) {
				if (("contentobject".equalsIgnoreCase(showContentType) && contentObject instanceof AlbumBo) 
						|| ("album".equalsIgnoreCase(showContentType) && contentObject instanceof ContentObjectBo ))
					continue;
				
				HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
				mapData.put("text", getContentObjectText(contentObject.getTitle(), contentObject.getClass(), request));//title
				mapData.put("titleWidth", getTitleWidth(contentObject));//title Width
				mapData.put("thumbnailCssClass", getThumbnailCssClass(contentObject));//thumbnail Css Class
				mapData.put("id", getId(contentObject));//content Object id
				mapData.put("thumbnailHtml", getThumbnailHtml(contentObject, thumbSize, request));//Thumbnail Html
				mapData.put("isAlbumThumbnail", album.getThumbnailContentObjectId() == contentObject.getId());//is Album Thumbnail
				if (sc) {
					mapData.put("sc", true);//show checkbox
				}else {
					mapData.put("sc", shouldShowCheckbox(contentObject));//show checkbox
				}
				mapData.put("savings", getSavings(contentObject));//file size kb
				mapData.put("warningMsg", shouldShowNoOriginalFileMsg(contentObject) ? I18nUtils.getString("task.deleteOriginal.Not_Available_Text", request.getLocale()) : "");//warning message
					
				list.add(mapData);
			}
		}
		
		return list;
	}
	
	public String getRotateHtml(GalleryView galleryView, ContentObjectBo mo, boolean multiRotate, HttpServletRequest request) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException, InvalidAlbumException{
		//ContentObjectBo mo = galleryView.getContentObject();
		
		return StringUtils.format( 
"		<div data-id=\"{0}\" class=\"thmbRotate\"{9}>\r\n" + 
"        <table>\r\n" + 
"          <tr>\r\n" + 
"            <td colspan=\"3\">\r\n" + 
"              <a class=\"mds_hor\" href=\"#\" data-side='top' style=\"background: #ccc url({1}/images/rotate/top1.gif) no-repeat 0 0\" title=\"{2}\"></a>\r\n" + 
"            </td>\r\n" + 
"          </tr>\r\n" + 
"          <tr>\r\n" + 
"            <td>\r\n" + 
"              <a class=\"mds_vert\" href=\"#\" data-side='left' style=\"background-image: url({1}/images/rotate/left.gif)\" title=\"{3}\"></a>\r\n" + 
"            </td>\r\n" + 
"            <td>\r\n" + 
"              <div>\r\n" + 
"                <img src=\"{4}\" style=\"{5}\" alt=\"{6}\" title=\"{6}\" class=\"mds_rotate\" />\r\n" + 
"              </div>\r\n" + 
"            </td>\r\n" + 
"            <td>\r\n" + 
"              <a class=\"mds_vert\" href=\"#\" data-side='right' style=\"background-image: url({1}/images/rotate/right.gif)\" title=\"{7}\"></a>\r\n" + 
"            </td>\r\n" + 
"          </tr>\r\n" + 
"          <tr>\r\n" + 
"            <td colspan=\"3\">\r\n" + 
"              <a class=\"mds_hor\" href=\"#\" data-side='bottom' style=\"background-image: url({1}/images/rotate/bottom.gif)\" title=\"{8}\"></a>\r\n" + 
"            </td>\r\n" + 
"          </tr>\r\n" + 
"        </table>\r\n" + 
"        <input id=\"hdnSelectedSide\" name=\"hdnSelectedSide\" class=\"hdnSelectedSide\"  type=\"hidden\" value=\"top\" />\r\n" + 
"      </div>\r\n" + 
"      <input id=\"moid\" name=\"moid\" type=\"hidden\" value='{0}' />\n" +
"\n", 
		mo.getId(), //0
		Utils.getSkinPath(request), //1 
		I18nUtils.getString("task.rotateImage.0_Rotate_Text", request.getLocale()), //2
		I18nUtils.getString("task.rotateImage.90_Rotate_Text", request.getLocale()), //3
		getContentObjectUrl(mo, galleryView, request), //4
		getWidthAndHeightStyle(mo, multiRotate), //5
		Utils.removeHtmlTags(mo.getTitle()), //6
		I18nUtils.getString("task.rotateImage.270_Rotate_Text", request.getLocale()), //7
		I18nUtils.getString("task.rotateImage.180_Rotate_Text", request.getLocale()), //8
		multiRotate ? StringUtils.EMPTY : " style=\"float: none;\""
		);
	}
	
	public String getRotatesHtml(GalleryView galleryView, HttpServletRequest request) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException, InvalidAlbumException{
		ContentObjectBoCollection rotatableContentObjects = galleryView.getAlbum().getChildContentObjects(ContentObjectType.Image);

		ContentObjectBoCollection videos = galleryView.getAlbum().getChildContentObjects(ContentObjectType.Video);
		if (StringUtils.isNotBlank(AppSettings.getInstance().getFFmpegPath())){
			// Only include videos when FFmpeg is installed.
			rotatableContentObjects.addRange(videos.values());
		}

		List<ContentObjectBo> albumChildren = rotatableContentObjects.toSortedList();

		String html = StringUtils.EMPTY;
		if (!albumChildren.isEmpty()) {
			for(ContentObjectBo mo : albumChildren) {
				html += getRotateHtml(galleryView, mo, true, request);
			}
		}
		
		return html;
	}
	
	protected static String getWidthAndHeightStyle(ContentObjectBo contentObject, boolean multiRotate) throws InvalidGalleryException {
		int width = multiRotate ? contentObject.getThumbnail().getWidth() : contentObject.getOptimized().getWidth();
		int height = multiRotate ? contentObject.getThumbnail().getHeight() : contentObject.getOptimized().getHeight();

		if (contentObject.getContentObjectType() == ContentObjectType.Video){
			width = contentObject.getThumbnail().getWidth();
			height = contentObject.getThumbnail().getHeight();
		}

		return StringUtils.format("width:{0}px;height:{1}px;", width, height);
	}

	protected String getContentObjectUrl(ContentObjectBo contentObject, GalleryView galleryView, HttpServletRequest request) throws UnsupportedContentObjectTypeException, UnsupportedEncodingException, WebException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException {
		switch (contentObject.getContentObjectType()){
			case Image: return galleryView.getOptimizedUrl(contentObject, request);
			case Video: return galleryView.getThumbnailUrl(contentObject, request);
		}

		return null;
	}
	
	/// <summary>
	/// Gets the HTML to display a nicely formatted thumbnail image of the specified <paramref name="galleryObject" />, including a 
	/// border, shadows and (possibly) rounded corners. This function is the same as calling the overloaded version with 
	/// includeHyperlinkToObject and allowAlbumTextWrapping parameters both set to <c>false</c>.
	/// </summary>
	/// <param name="galleryObject">The gallery object to be used as the source for the thumbnail image.</param>
	/// <returns>Returns HTML that displays a nicely formatted thumbnail image of the specified <paramref name="galleryObject" /></returns>
	public static String getThumbnailHtml(ContentObjectBo contentObject, String thumbSize, HttpServletRequest request) throws Exception{
		ContentObjectHtmlBuilder moBuilder = new ContentObjectHtmlBuilder(ContentObjectHtmlBuilder.getContentObjectHtmlBuilderOptions(contentObject, DisplayObjectType.Thumbnail, request));

		return thumbSize.equalsIgnoreCase("rotate") ? moBuilder.getThumbnailHtmlFitSize() : moBuilder.getThumbnailHtml();
	}
	
	
	public static String getFullThumbnailHtml(ContentObjectBo contentObject, HttpServletRequest request) throws Exception{
		ContentObjectHtmlBuilder moBuilder = new ContentObjectHtmlBuilder(ContentObjectHtmlBuilder.getContentObjectHtmlBuilderOptions(contentObject, DisplayObjectType.Thumbnail, request));

		return "<ul class='mds_floatcontainer'>" + // style='padding-left:0px;' 
		    "<li class='" + getThumbnailCssClass(contentObject) + "'>" +
		    moBuilder.getThumbnailHtml() +
		    "<p>" + contentObject.getTitle() + "</p>" +
			"</li>" + 
			"</ul>";
	}
	
	public static String getFullThumbnailHtml(ContentObjectBo contentObject, ContentObjectHtmlBuilderOptions moBuilderOptions, HttpServletRequest request) throws Exception{
		ContentObjectHtmlBuilder moBuilder = new ContentObjectHtmlBuilder(moBuilderOptions);

		return "<ul class='dcm_floatcontainer'>" + // style='padding-left:0px;' 
		    "<li class='" + getThumbnailCssClass(contentObject) + "'>" +
		    moBuilder.getThumbnailHtml() +
		    "<p>" + contentObject.getTitle() + "</p>" +
			"</li>" + 
			"</ul>";
	}
	
	public static String getFullThumbnailHtml(ContentObjectBo contentObject, final String title, final String thumbnailCssClass, final String url, HttpServletRequest request) throws UnsupportedContentObjectTypeException, UnsupportedEncodingException, InvalidGalleryException, WebException, InvalidAlbumException, UnsupportedImageTypeException  {
		ContentObjectHtmlBuilder moBuilder = new ContentObjectHtmlBuilder(ContentObjectHtmlBuilder.getContentObjectHtmlBuilderOptions(contentObject, DisplayObjectType.Thumbnail, request));
		
		return "<ul class='mds_floatcontainer'>" + // style='padding-left:0px;'
		"<li class='" + (StringUtils.isBlank(thumbnailCssClass) ? getThumbnailCssClass(contentObject) : thumbnailCssClass) + "'>" +
		moBuilder.getThumbnailHtml(title, url)  +
		"<p>" + (StringUtils.isBlank(title) ? contentObject.getTitle() : title) + "</p>" +
		"</li>" +  
		"</ul>";
	}

	//#endregion

	//#region Protected Methods

	/// <summary>
	/// Return an HTML formatted String representing the title of the gallery object. It is truncated and purged of HTML tags
	/// if necessary.
	/// </summary>
	/// <param name="title">The title of the gallery object as stored in the data store.</param>
	/// <param name="contentObjectType">The type of the object to which the title belongs.</param>
	/// <returns>Returns a String representing the title of the content object. It is truncated and purged of HTML tags
	/// if necessary.</returns>
	public String getContentObjectText(String title, Class<?> contentObjectType, HttpServletRequest request) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		if (StringUtils.isEmpty(title))
			return StringUtils.EMPTY;

		// If this is an album, return an empty String. Otherwise, return the title, truncated and purged of HTML
		// tags if necessary. If the title is truncated, add an ellipses to the text.
		//<asp:Label ID="lblAlbumPrefix" runat="server" CssClass="mds_bold" Text="<%$ Resources:MDS, uc.thumbnailView.Album_Title_Prefix_Text %>" />&nbsp;<%# GetContentObjectText(Eval("Title").ToString(), Container.DataItem.GetType())%>
		int maxLength = getGallerySettings().getMaxThumbnailTitleDisplayLength();
		String titlePrefix = StringUtils.EMPTY;

		if (contentObjectType == AlbumBo.class)	{
			// Album titles need a prefix, so assign that now.
			titlePrefix = StringUtils.format("<span class='mds_bold'>{0} </span>", I18nUtils.getString("uc.thumbnailView.Album_Title_Prefix_Text", request.getLocale()));

			// Override the previous max length with the value that is appropriate for albums.
			maxLength = getGallerySettings().getMaxThumbnailTitleDisplayLength();
		}

		String truncatedText = Utils.truncateTextForWeb(title, maxLength);

		if (truncatedText.length() != title.length())
			return StringUtils.join(titlePrefix, truncatedText, "...");
		else
			return StringUtils.join(titlePrefix, truncatedText);
	}
	
	/// <summary>
	/// Calculate the potential hard drive savings, in KB, if all original files were deleted from <paramref name="contentObject"/>.
	/// If <paramref name="contentObject"/> is an Album, then the value includes the sum of the size of all original files
	/// within the album.
	/// </summary>
	/// <param name="contentObject">The gallery object.</param>
	/// <returns>Returns the potential hard drive savings, in KB, if all original files were deleted from <paramref name="contentObject"/>.</returns>
	protected static String getSavings(ContentObjectBo contentObject){
		if (contentObject == null)
			throw new ArgumentNullException("contentObject");

		if (contentObject instanceof AlbumBo)
			return StringUtils.format("({0} KB)", getFileSizeKbAllOriginalFilesInAlbum((AlbumBo)contentObject));
		else
			return StringUtils.format("({0} KB)", contentObject.getOriginal().getFileSizeKB());
	}
	
	/// <summary>
	/// Gets a value indicating whether the page should display a "None" message for the <paramref name="contentObject"/>.
	/// Only content objects not having separate optimized and original files should show the message.
	/// </summary>
	/// <param name="contentObject">The gallery object.</param>
	/// <returns>Returns true if a "None" message should be shown; otherwise returns false.</returns>
	protected static boolean shouldShowNoOriginalFileMsg(ContentObjectBo contentObject)	{
		if (contentObject == null)
			throw new ArgumentNullException("contentObject");

		if (contentObject instanceof AlbumBo)
			return false;

		return !(doesOriginalExist(contentObject.getOptimized().getFileName(), contentObject.getOriginal().getFileName()));
	}

	/// <summary>
	/// Gets a value indicating whether the page should display a delete checkbox for the <paramref name="contentObject"/>.
	/// Albums should always show the checkbox; content objects should only if an original file exists.
	/// </summary>
	/// <param name="contentObject">The gallery object.</param>
	/// <returns>Returns true if a delete checkbox should be shown; otherwise returns false.</returns>
	protected static boolean shouldShowCheckbox(ContentObjectBo contentObject)	{
		if (contentObject == null)
			throw new ArgumentNullException("contentObject");

		if (contentObject instanceof AlbumBo)
			return true;

		return (doesOriginalExist(contentObject.getOptimized().getFileName(), contentObject.getOriginal().getFileName()));
	}
	
	/// <summary>
	/// Gets the total file size, in KB, of all the original files in the <paramref name="album"/>, including all 
	/// child albums. The total includes only those items where a web-optimized version also exists.
	/// </summary>
	/// <param name="album">The album for which to retrieve the file size of all original files.</param>
	/// <returns>Returns the total file size, in KB, of all the original files in the <paramref name="album"/>.</returns>
	public static long getFileSizeKbAllOriginalFilesInAlbum(AlbumBo album)	{
		// Get the total file size, in KB, of all the high resolution images in the specified album
		long sumTotal = 0;
		for (ContentObjectBo go : album.getChildContentObjects(ContentObjectType.ContentObject).values()){
			if (doesOriginalExist(go.getOptimized().getFileName(), go.getOriginal().getFileName()))
				sumTotal += go.getOriginal().getFileSizeKB();
		}

		for (AlbumBo childAlbum : album.getChildContentObjects(ContentObjectType.Album).toAlbums()){
			sumTotal += getFileSizeKbAllOriginalFilesInAlbum(childAlbum);
		}

		return sumTotal;
	}
	
	protected static boolean doesOriginalExist(String optimizedFileName, String originalFileName){
		// An original file exists if an optimized file exists and the optimized and original filenames are different.
		return (!StringUtils.isBlank(optimizedFileName) && !optimizedFileName.equalsIgnoreCase(originalFileName));
	}

	/// <summary>
	/// Gets a value indicating whether the user has permission to delete the specified <paramref name="contentObject" />.
	/// </summary>
	/// <param name="contentObject">The gallery object.</param>
	/// <returns><c>true</c> if the user has delete permission; otherwise <c>false</c></returns>
	/*public boolean DoesUserHavePermissionToDeleteContentObject(ContentObjectBo contentObject)	{
		return (contentObject instanceof AlbumBo ? UserCanDeleteChildAlbum : UserCanDeleteContentObject);
	}*/

	/// <summary>
	/// Gets a value to be initially used to set the width of the title below the thumbnail. This is later removed via jQuery
	/// after the equalSize() function standardizes all the thumbnail boxes. Without this setting the titles will push the
	/// width of the thumbnails to be as wide as the title.
	/// </summary>
	/// <param name="contentObject">The gallery object.</param>
	/// <returns>An instance of <see cref="Unit" />.</returns>
	public int getTitleWidth(ContentObjectBo contentObject) throws InvalidGalleryException	{
		return contentObject.getThumbnail().getWidth() + 40;
	}

	/// <summary>
	/// Gets the CSS class to apply to the thumbnail object.
	/// </summary>
	/// <param name="contentObject">The gallery object.</param>
	/// <returns>Returns a CSS class.</returns>
	public static String getThumbnailCssClass(ContentObjectBo contentObject){
		// If it's an album then specify the appropriate CSS class so that the "Album"
		// header appears over the thumbnail. This is to indicate to the user that the
		// thumbnail represents an album.
		if (contentObject instanceof AlbumBo)
			return "thmb album";
		else
			return "thmb";
	}

	/// <summary>
	/// Gets a value that uniquely identifies the specified <paramref name="contentObject" /> (ex: "a25", "m223").
	/// </summary>
	/// <param name="contentObject">The gallery object.</param>
	/// <returns>Returns an ID.</returns>
	public static String getId(ContentObjectBo contentObject){
		if (contentObject == null)
			throw new ArgumentNullException("contentObject");
		
		// Prepend an 'a' (for album) or 'm' (for content object) to the ID to indicate whether it is
		// an album ID or content object ID.
		if (contentObject instanceof AlbumBo)
			return "a" + Long.toString(contentObject.getId());
		else
			return "m" + Long.toString(contentObject.getId());
	}

	//#endregion
}