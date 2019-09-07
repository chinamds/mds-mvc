package com.mds.cm.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;

import com.mds.cm.content.AlbumBo;
import com.mds.cm.content.ContentObjectBo;
import com.mds.cm.content.GallerySettings;
import com.mds.cm.content.ImageHelper;
import com.mds.cm.content.MimeTypeBo;
import com.mds.cm.content.nullobjects.NullContentObject;
import com.mds.cm.content.nullobjects.NullMimeType;
import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidContentObjectException;
import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.core.DisplayObjectType;
import com.mds.core.MimeTypeCategory;
import com.mds.core.SecurityActions;
import com.mds.core.exception.ArgumentOutOfRangeException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.sys.util.AppSettings;
import com.mds.sys.util.RoleUtils;
import com.mds.util.DateUtils;
import com.mds.common.utils.security.Encodes;
import com.mds.util.FileMisc;
import com.mds.util.HelperFunctions;
import com.mds.util.StringUtils;
import com.mds.util.Utils;

/// <summary>
/// Defines a handler that sends the specified content object to the output stream.
/// </summary>
public class MediaSourceBuilder{
	//#region Private Fields

	private static int bufferSize;

	private long galleryIdInQueryString = Long.MIN_VALUE ;
	private long galleryId = Long.MIN_VALUE ;
	private long contentObjectId;
	private long contentLength;
	private DisplayObjectType displayType;
	private String displayTypeStr;
	private String galleryIdStr;
	private String contentObjectIdStr;
	private String sendAsAttachmentStr;

	private ContentObjectBo contentObject;
	private String contentObjectFilePath;
	private GallerySettings gallerySetting;
	private InputStream stream;
	private File contentObjectFileInfo;
	private boolean sendAsAttachment;

	//#endregion
	public MediaSourceBuilder() {}
	public MediaSourceBuilder(String id, String displayType, String galleryId, String sa) {
		this.galleryIdStr = galleryId;
		this.contentObjectIdStr = id;
		this.displayTypeStr = displayType;
		sendAsAttachmentStr = sa;
	}

	//#region Enumerations

	/// <summary>
	/// Specifies a type of resource served by this HTTP handler.
	/// </summary>
	private enum ContentType{
		/// <summary>
		/// Specifies that no type has been specified.
		/// </summary>
		NotSet(0),

		/// <summary>
		/// Specifies that a content object has been requested.
		/// </summary>
		ContentObject(1),

		/// <summary>
		/// Specifies that a watermarked content object has been requested.
		/// </summary>
		ContentObjectWithWatermark(2),

		/// <summary>
		/// Specifies that an empty album thumbnail has been requested.
		/// </summary>
		EmptyAlbumThumbnail(3);
		
		private final int contentType;
	    
	    private ContentType(int contentType) {
	        this.contentType = contentType;
	    }
		
		int value() {
			return contentType;
		}
	}

	//#endregion

	//#region Properties

	/// <summary>
	/// Gets the content object being requested. Guaranteed to not return null; returns 
	/// <see cref="Business.NullObjects.NullContentObject" /> when no content object is being requested or 
	/// it is invalid. This property does not verify the user has permission to view the content object.
	/// </summary>
	/// <value>An instance of <see cref="ContentObjectBo" />.</value>
	private ContentObjectBo getContentObject() throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException{
		if (contentObject == null){
			if (contentObjectId > 0){
				try{
					contentObject = CMUtils.loadContentObjectInstance(contentObjectId);
				}catch (InvalidContentObjectException ex){
					contentObject = new NullContentObject();
				}
			}else{
				contentObject = new NullContentObject();
			}
		}

		return contentObject;
	}

	/// <summary>
	/// Gets the type of resource that has been requested by the user.
	/// </summary>
	/// <value>An instance of <see cref="ContentType" />.</value>
	private ContentType ResourceType;

	/// <summary>
	/// Gets the file path to the requested content object. It will be the thumbnail, optimized, or original file depending
	/// on which version is being requested. May return null or an empty String when an invalid content object is
	/// requested or the default album thumbnail is requested.
	/// </summary>
	/// <value>The file path to the requested content object.</value>
	public String getContentObjectFilePath() throws InvalidGalleryException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException{
		if (contentObjectFilePath == null){
			switch (this.displayType){
				case Thumbnail:
					contentObjectFilePath = getContentObject().getThumbnail().getFileNamePhysicalPath();
					break;
				case Optimized:
					contentObjectFilePath = getContentObject().getOptimized().getFileNamePhysicalPath();
					break;
				case Original:
					contentObjectFilePath = getContentObject().getOriginal().getFileNamePhysicalPath();
					break;
			}
		}

		return contentObjectFilePath;
	}

	/// <summary>
	/// Gets a reference to the file associated with the requested content object. Returns null when
	/// <see cref="ResourceType" /> = <see cref="ContentType.EmptyAlbumThumbnail" /> or <see cref="ContentType.NotSet" />.
	/// </summary>
	/// <value>A <see cref="FileInfo" /> instance, or null.</value>
	private File getContentObjectFileInfo() throws UnsupportedContentObjectTypeException, InvalidGalleryException, InvalidAlbumException, UnsupportedImageTypeException{
		if ((contentObjectFileInfo == null) && (FileMisc.fileExists(getContentObjectFilePath()))){
			contentObjectFileInfo = new File(getContentObjectFilePath());
		}

		return contentObjectFileInfo;
	}

	/// <summary>
	/// Gets the MIME type for the requested content object. It will be for the thumbnail, optimized, or original file depending
	/// on which version is being requested.
	/// </summary>
	/// <value>The MIME type for the requested content object.</value>
	private MimeTypeBo getMimeType() throws InvalidGalleryException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException{
		switch (ResourceType){
			case NotSet:
				return new NullMimeType();
			case ContentObject:
				return getMimeTypeForContentObject();
			case ContentObjectWithWatermark:
			case EmptyAlbumThumbnail:
				return CMUtils.loadMimeType("dummy.jpg");
			default:
				throw new ArgumentOutOfRangeException("");
		}
	}

	/// <summary>
	/// Gets the gallery ID associated with the content object being requested. If no content object is available (perhaps an empty
	/// album thumbnail is being requested), then use the gallery ID specified in the query String.
	/// </summary>
	/// <value>The gallery ID.</value>
	private long getGalleryId() throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException	{
		if (galleryId == Long.MIN_VALUE ){
			if (!(getContentObject() instanceof NullContentObject))	{
				galleryId = getContentObject().getGalleryId();
			}else{
				galleryId = galleryIdInQueryString;
			}
		}

		return galleryId;
	}

	/// <summary>
	/// Gets the gallery settings for the gallery the requested content object is in.
	/// </summary>
	/// <value>The gallery settings.</value>
	private GallerySettings getGallerySettings() throws UnsupportedContentObjectTypeException, InvalidGalleryException, InvalidAlbumException, UnsupportedImageTypeException{
		if (gallerySetting == null){
			gallerySetting = CMUtils.loadGallerySetting(getGalleryId());
		}

		return gallerySetting;
	}

	/// <summary>
	/// Gets the size of each chunk of data streamed back to the client.
	/// </summary>
	/// <value>An integer</value>
	/// <remarks>
	/// When a client makes a range request the requested stream's contents are
	/// read in BufferSize chunks, with each chunk flushed to the output stream
	/// until the requested byte range has been read.
	/// </remarks>
	public int getBufferSize(){
		if (bufferSize == 0){
			bufferSize = AppSettings.getInstance().getContentObjectDownloadBufferSize();
		}

		return bufferSize;
	}
	
	/// <summary>
	/// Gets the value to use for the response's content disposition. Default value is
	/// ResponseHeaderContentDisposition.Inline.
	/// </summary>
	/// <value>The value to use for the response's content disposition.</value>
	public boolean getSendAsAttachment() {
		return sendAsAttachment;
	}


	/// <summary>
	/// Gets the name of the requested file. Used to set the Content-Disposition response header. Specify
	/// <see cref="StringUtils.EMPTY"/> or null if no file name is applicable.
	/// </summary>
	/// <value>
	/// A <see cref="System.String"/> instance, or null if no file name is applicable.
	/// </value>
	public String getFileName() throws UnsupportedContentObjectTypeException, InvalidGalleryException, InvalidAlbumException, UnsupportedImageTypeException{
		return FilenameUtils.getName(getContentObjectFilePath()).replace(getGallerySettings().getOptimizedFileNamePrefix(), StringUtils.EMPTY);
	}

	//#endregion

	//#region Public Methods

	/// <summary>
	/// Initializes the request.
	/// </summary>
	/// <param name="context">The HTTP context.</param>
	/// <returns>
	/// Returns <c>true</c> when the method succeeds; otherwise <c>false</c>.
	/// </returns>
	public boolean initializeRequest(HttpServletRequest request){
		boolean isSuccessfullyInitialized = false;

		try{
			if (!GalleryUtils.isInitialized())	{
				GalleryUtils.initializeMDSApplication(request.getServletContext());
			}

			if (initializeVariables()){
				isSuccessfullyInitialized = true;
			}else{
				//context.Response.StatusCode = 404;
			}

			//return (base.InitializeRequest(context) & isSuccessfullyInitialized);
			return isSuccessfullyInitialized;
		}catch (Exception ex){
			//AppEventLogController.LogError(ex);
		}

		return isSuccessfullyInitialized;
	}

	/// <summary>
	/// Gets a <see cref="Stream"/> object representing the requested content.
	/// </summary>
	/// <returns>
	/// Returns a <see cref="Stream"/> instance.
	/// </returns>
	public InputStream getResourceStream(HttpServletRequest request) throws IOException, UnsupportedContentObjectTypeException, InvalidGalleryException, InvalidAlbumException, UnsupportedImageTypeException{
		switch (ResourceType){
			case ContentObject:
				return Files.newInputStream(new File(getContentObjectFilePath()).toPath());
			case ContentObjectWithWatermark:
				if (stream == null)
					stream = getWatermarkedImageStream(request);
				
				return stream;
			case EmptyAlbumThumbnail:
				if (stream == null)
					stream = getDefaultThumbnailStream();
				
				return stream;
			default:
				throw new ArgumentOutOfRangeException("");
		}
	}
	
	private BasicFileAttributes getBasicFileAttributes(File fi) {
		BasicFileAttributes attr = null;
		try {
			attr = Files.readAttributes(fi.toPath(), BasicFileAttributes.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return attr;
	}

	/// <summary>
	/// Gets the length of the requested resource.
	/// </summary>
	/// <returns>Returns a long.</returns>
	public long getResourceLength(HttpServletRequest request) throws UnsupportedContentObjectTypeException, InvalidGalleryException, InvalidAlbumException, UnsupportedImageTypeException, IOException{
		if (ResourceType == ContentType.ContentObject){
			return getBasicFileAttributes(getContentObjectFileInfo()).size();
		}else if (ResourceType == ContentType.ContentObjectWithWatermark){
			if (stream == null)
				stream = getWatermarkedImageStream(request);
			
			return contentLength;
		}else{
			if (stream == null)
				stream = getDefaultThumbnailStream();
			
			return contentLength;
		}
	}

	/// <summary>
	/// Gets the timestamp of the last write time of the requested resource. Returns <see cref="DateTime.MinValue"/>
	/// for a dynamically created resource.
	/// </summary>
	/// <returns>A <see cref="DateTime"/> instance.</returns>
	public FileTime getResourceLastWriteTimeUtc() throws UnsupportedContentObjectTypeException, InvalidGalleryException, InvalidAlbumException, UnsupportedImageTypeException{
		if ((ResourceType == ContentType.ContentObject) && (getContentObjectFileInfo() != null)){
			return getBasicFileAttributes(getContentObjectFileInfo()).lastModifiedTime();
		}else{
			return FileTime.fromMillis(0L);
		}
	}

	/// <summary>
	/// Cleans up resources. This is called in a finally block at the end of the ProcessRequest method of the
	/// base class.
	/// method.
	/// </summary>
	public void CleanUpResources() throws IOException{
		if (stream != null)	{
			stream.close();
		}
	}

	/// <summary>
	/// Returns the Entity Tag (ETag) for the requested content. Returns an empty String if an ETag value
	/// is not applicable or if the derived class does not provide an implementation.
	/// </summary>
	/// <returns>A <see cref="System.String"/> instance.</returns>
	public String getResourceFileEntityTag() throws NoSuchAlgorithmException, UnsupportedContentObjectTypeException, InvalidGalleryException, InvalidAlbumException, UnsupportedImageTypeException{
		if (ResourceType != ContentType.ContentObject)
			return StringUtils.EMPTY;

		byte[] sourceBytes = StringUtils.join(getContentObjectFileInfo().getPath(), "|", getBasicFileAttributes(getContentObjectFileInfo()).lastModifiedTime().toString()).getBytes(StandardCharsets.US_ASCII);

		return Encodes.encodeBase64(MessageDigest.getInstance("md5").digest(sourceBytes));
	}

	public String getResourceMimeType() throws InvalidGalleryException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException	{
		return getMimeType().getFullType();
	}

	/// <summary>
	/// Verifies that the current user can access the requested resource.
	/// </summary>
	/// <returns>
	/// 	<c>true</c> if validation succeeds; otherwise <c>false</c>.
	/// </returns>
	/*protected boolean CheckAuthorizationRules()	{
		if (!IsUserAuthorized())
		{
			context.Response.StatusCode = 403;
			AddHeader(context.Response, "Content-Type", "text/html");
			context.Response.Write("<h1>Unauthorized user</h1>");
			return false;
		}

		return true;
	}*/

	/// <summary>
	/// Verifies that the requested resource exists and can be sent to the user.
	/// </summary>
	/// <returns>
	/// 	<c>true</c> if validation succeeds; otherwise <c>false</c>.
	/// </returns>
	public boolean checkResourceRequested() throws UnsupportedContentObjectTypeException, InvalidGalleryException, InvalidAlbumException, UnsupportedImageTypeException{
		boolean isAlbumThumbnail = (ResourceType == ContentType.EmptyAlbumThumbnail);

		if (isAlbumThumbnail || FileMisc.fileExists(getContentObjectFilePath())){
			// Our test succeeds. Call base method to continue validation.
			return true; //base.CheckResourceRequested();
		}else{
			//context.Response.StatusCode = 404;
			return false;
		}
	}

	//#endregion

	//#region Private methods

	/// <summary>
	/// Initialize the class level variables with information from the query String. Returns false if the 
	/// variables cannot be properly initialized.
	/// </summary>
	/// <param name="context">The HttpContext for the current request.</param>
	/// <returns>Returns true if all variables were initialized; returns false if there was a problem and 
	/// one or more variables could not be set.</returns>
	private boolean initializeVariables() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException{
		try {
			this.displayType = DisplayObjectType.valueOf(displayTypeStr);
		}catch(Exception ex) {
			return false;
		}
		/*context = context;

		if (!ExtractQueryStringParms(context.Request.Url.Query))
			return false;*/
		long gId = StringUtils.toLong(galleryIdStr);
		if (gId == Long.MIN_VALUE) {
			return false;
		}
		galleryIdInQueryString = gId;
		
		long moId = StringUtils.toLong(contentObjectIdStr);
		if (moId == Long.MIN_VALUE) {
			return false;
		}
		contentObjectId = moId;
		
		sendAsAttachment = (sendAsAttachmentStr != null && ((sendAsAttachmentStr.equals("1")) || (sendAsAttachmentStr.equalsIgnoreCase("TRUE"))));

		ResourceType = determineResourceType();

		return DisplayObjectType.isValidDisplayObjectType(this.displayType);
	}

	private ContentType determineResourceType() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException	{
		if (contentObjectId == 0){
			// User specified moid=0 in the query String, which is the signal to request the empty album thumbnail.
			return ContentType.EmptyAlbumThumbnail;
		}
		else if (shouldApplyWatermark()){
			return ContentType.ContentObjectWithWatermark;
		}else{
			return ContentType.ContentObject;
		}
	}

	private boolean shouldApplyWatermark() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException{
		// Apply watermark to thumbnails only when the config setting applyWatermarkToThumbnails = true.
		// Apply watermark to optimized and original images only when applyWatermark = true.
		if ((displayType == DisplayObjectType.Thumbnail) || getContentObject().getMimeType().getTypeCategory() == MimeTypeCategory.Image){
			boolean requiresWatermark = false;
			boolean applyWatermark = getGallerySettings().getApplyWatermark();
			boolean applyWatermarkToThumbnails = getGallerySettings().getApplyWatermarkToThumbnails();
			boolean isThumbnail = (displayType == DisplayObjectType.Thumbnail);

			//if (AppSettings.getInstance().License.IsInReducedFunctionalityMode && !isThumbnail)
			if (false){
				requiresWatermark = true;
			}else if ((applyWatermark && !isThumbnail) || (applyWatermark && applyWatermarkToThumbnails && isThumbnail)){
				// If the user belongs to a role with watermarks set to visible, then show it; otherwise don't show the watermark.
				if (!Utils.isUserAuthorized(SecurityActions.HideWatermark, RoleUtils.getMDSRolesForUser(), getContentObject().getParent().getId(), getGalleryId(), getContentObject().getIsPrivate(), ((AlbumBo)getContentObject().getParent()).getIsVirtualAlbum())){
					// Show the image without the watermark.
					requiresWatermark = true;
				}
			}

			return requiresWatermark;
		}else{
			return false; // Watermarks are never applied to non-image content objects.
		}
	}

	/// <summary>
	/// Extract information from the query String and assign to our class level variables. Return false if 
	/// something goes wrong and the variables cannot be set. This will happen when the query String is in 
	/// an unexpected format.
	/// </summary>
	/// <param name="queryString">The query String for the current request. Can be populated with 
	/// HttpContext.Request.Url.Query. Must start with a question mark (?).</param>
	/// <returns>Returns true if all relevant variables were assigned from the query String; returns false 
	/// if there was a problem.</returns>
	/*private boolean ExtractQueryStringParms(String queryString)
	{
		if (StringUtils.isEmpty(queryString)) return false;

		queryString = queryString.Remove(0, 1); // Strip off the ?

		boolean filepathIsEncrypted = AppSetting.Instance.EncryptContentObjectUrlOnClient;
		if (filepathIsEncrypted)
		{
			// Decode, then decrypt the query String. Note that we must replace spaces with a '+'. This is required when the the URL is
			// used in javascript to create the Silverlight media player. Apparently, Silverlight or the media player javascript decodes
			// the query String when it requests the URL, so that means any instances of '%2b' are decoded into '+' before it gets here.
			// Ideally, we wouldn't even call UrlDecode in this case, but we don't have a way of knowing that it has already been decoded.
			// So we decode anyway, which doesn't cause any harm *except* it converts '+' to a space, so we need to convert them back.
			queryString = HelperFunctions.Decrypt(HttpUtility.UrlDecode(queryString).Replace(" ", "+"));
		}

		//moid={0}&dt={1}g={2}
		foreach (String nameValuePair in queryString.Split(new[] { '&' }))
		{
			String[] nameOrValue = nameValuePair.Split(new[] { '=' });
			switch (nameOrValue[0])
			{
				case "g":
					{
						int gid;
						if (Int32.TryParse(nameOrValue[1], out gid))
							galleryIdInQueryString = gid;
						else
							return false;
						break;
					}
				case "moid":
					{
						int moid;
						if (Int32.TryParse(nameOrValue[1], out moid))
							contentObjectId = moid;
						else
							return false;
						break;
					}
				case "dt":
					{
						int dtInt;
						if (Int32.TryParse(nameOrValue[1], out dtInt))
						{
							if (DisplayObjectTypeEnumHelper.IsValidDisplayObjectType((DisplayObjectType)dtInt))
							{
								displayType = (DisplayObjectType)dtInt; break;
							}
							else
								return false;
						}
						else
							return false;
					}
				case "sa":
					{
						sendAsAttachment = ((nameOrValue[1].Equals("1", StringComparison.Ordinal)) || (nameOrValue[1].Equals("TRUE", StringComparison.OrdinalIgnoreCase)));
						break;
					}
				default: return false; // Unexpected query String parm. Return false so execution is aborted.
			}
		}

		ValidateDisplayType();

		return true;
	}*/

	/// <summary>
	/// If an optimized version is being requested, make sure a file name is specified for it. If not, switch to the
	/// original version. This switch will be necessary for most non-image content objects, since the client usually 
	/// requests optimized versions for everything.
	/// </summary>
	/// <remarks>This function became necessary when switching to the ID-based request in 2.4 (rather than the 
	/// file-based request). It was considered to change the requesting logic to ensure the correct display type 
	/// is specified, and while that seems preferable from an architectural perspective, it was more complex to 
	/// implement and potentially more fragile than this simple function.</remarks>
	private void ValidateDisplayType() throws UnsupportedContentObjectTypeException, InvalidGalleryException, InvalidAlbumException, UnsupportedImageTypeException{
		if ((displayType == DisplayObjectType.Optimized) && (StringUtils.isEmpty(getContentObjectFilePath()))){
			displayType = DisplayObjectType.Original;
			contentObjectFilePath = null;

			// Comment out the exception, as it generates unnecessary errors when bots request deleted items
			//if (StringUtils.isEmpty(ContentObjectFilePath))
			//{
			//  throw new InvalidContentObjectException(String.Format(CultureInfo.CurrentCulture, "A request was made to the MDS System HTTP handler to serve the optimized image for content object ID {0}, but either the content object does not exist or neither the optimized nor the original has a filename stored in the database, and therefore cannot be served.", _contentObjectId));
			//}
		}
	}

	public boolean isUserAuthorized() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException, UnsupportedImageTypeException{
		// If no content object is specified, then return true (this happens for empty album thumbnails).
		if ((ResourceType == ContentType.ContentObject) || (ResourceType == ContentType.ContentObjectWithWatermark)){
			SecurityActions requestedPermission = SecurityActions.ViewAlbumOrContentObject;

			if ((displayType == DisplayObjectType.Original)){
				boolean optFileDiffThanOriginal = !getContentObject().getOriginal().getFileName().equalsIgnoreCase(getContentObject().getOptimized().getFileName());
				
				if (optFileDiffThanOriginal)
					requestedPermission = SecurityActions.ViewOriginalContentObject;
			}

			return Utils.isUserAuthorized(requestedPermission, RoleUtils.getMDSRolesForUser(), getContentObject().getParent().getId(), getGalleryId(), getContentObject().getIsPrivate(),  ((AlbumBo)getContentObject().getParent()).getIsVirtualAlbum());
		}else{
			return true; // Non-content object requests are always valid (i.e. default album thumbnails)
		}
	}

	private MimeTypeBo getMimeTypeForContentObject() throws InvalidGalleryException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException{
		switch (displayType){
			case Thumbnail:
				return getContentObject().getThumbnail().getMimeType();
			case Optimized:
				return getContentObject().getOptimized().getMimeType();
			case Original:
				return getContentObject().getOriginal().getMimeType();
			default:
				return getContentObject().getOriginal().getMimeType();
		}
	}

	private InputStream getDefaultThumbnailStream() throws IOException, UnsupportedContentObjectTypeException, InvalidGalleryException, InvalidAlbumException, UnsupportedImageTypeException{
		BufferedImage bmp = getDefaultThumbnailBitmap();

		// now create an input stream for the thumbnail buffer and return it
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		ImageIO.write(bmp, "jpeg", baos);
		this.contentLength = baos.size();
		
		// now get the array
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

		return bais;
	}

	private BufferedImage getDefaultThumbnailBitmap() throws UnsupportedContentObjectTypeException, InvalidGalleryException, InvalidAlbumException, UnsupportedImageTypeException{
		//Return a bitmap of a default album image.  This will be used when no actual
		//image is available to serve as the pictorial view of the album.

		float ratio = getGallerySettings().getEmptyAlbumThumbnailWidthToHeightRatio();
		int maxLength = getGallerySettings().getMaxThumbnailLength();
		String imageText = getGallerySettings().getEmptyAlbumThumbnailText();
		String fontName = getGallerySettings().getEmptyAlbumThumbnailFontName();
		int fontSize = getGallerySettings().getEmptyAlbumThumbnailFontSize();
		Color bgColor = HelperFunctions.getColor(getGallerySettings().getEmptyAlbumThumbnailBackgroundColor());
		Color fontColor = HelperFunctions.getColor(getGallerySettings().getEmptyAlbumThumbnailFontColor());

		int rctWidth, rctHeight; //Image width and height

		if (ratio > 1){
			rctWidth = maxLength;
			rctHeight = (int) ((float)maxLength / ratio);
		}else{
			rctHeight = maxLength;
			rctWidth = (int)((float)maxLength * ratio);
		}

		BufferedImage bmp = null;
		Graphics2D g = null;
		try	{
			// If the font name does not match an installed font, .NET will substitute Microsoft Sans Serif.
			Font fnt = new Font(fontName, Font.PLAIN, fontSize);
			Rectangle rct = new Rectangle(0, 0, rctWidth, rctHeight);
			bmp = new BufferedImage((int)rct.getWidth(), (int)rct.getHeight(), BufferedImage.TYPE_INT_RGB);
			g = bmp.createGraphics();

			// Calculate x and y offset for text
			g.setFont(fnt);
			FontMetrics fm = g.getFontMetrics();
			//Rectangle2D textSize = fm.getStringBounds(imageText, g);
			int textWidth = fm.stringWidth(imageText);
			int textHeight = fm.getHeight();

			int x = (int) ((rctWidth - textWidth) / 2); //Starting point from left for the text
			int y = (int) ((rctHeight - textHeight) / 2); //Start point from top for the text

			if (x < 0) x = 0;
			if (y < 0) y = 0;

			// Generate image
			g.setColor(bgColor);
			//g.fillRect(rct.x, rct.y, rct.width, rct.height);
			g.fill(rct);
			g.setColor(fontColor);
			g.drawString(imageText, x, y + fm.getAscent());
		}catch(Exception ex){
			throw ex;
		}finally	{
			if (g != null)
				g.dispose();;
		}

		return bmp;
	}

	private InputStream getWatermarkedImageStream(HttpServletRequest request) throws IOException{
		BufferedImage watermarkedImage = null;
		try{
			try{
				watermarkedImage = ImageHelper.addWatermark(getContentObjectFilePath(), getContentObject().getGalleryId(), request);
			}catch (Exception ex){
				// Can't apply watermark to image. Substitute an error image and send that to the user.
				if (!(ex instanceof FileNotFoundException)){
					// Don't log FileNotFoundException exceptions. This helps avoid clogging the error log 
					// with entries caused by search engine retrieving content objects that have been moved or deleted.
					AppEventLogUtils.LogError(ex);
				}
				//watermarkedImage = ImageIO.read(FileMisc.getClassResFile("/images/error-xl.png", this.getClass()));
				watermarkedImage = ImageHelper.getImageResource("/images/error-xl.png", this.getClass());
			}

			// now create an input stream for the thumbnail buffer and return it
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			ImageIO.write(watermarkedImage, "jpeg", baos);
			this.contentLength = baos.size();
			
			// now get the array
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

			return bais;
		}finally{
		}
	}

	//#endregion
}
