package com.mds.cm.content;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.StringUtils;

import com.mds.common.utils.Reflections;
import com.mds.core.exception.ArgumentNullException;
import com.mds.core.exception.BusinessException;
import com.mds.cm.exception.InvalidContentObjectException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.core.DisplayObjectType;
import com.mds.core.MimeTypeCategory;
import com.mds.core.Size;
import com.mds.cm.util.CMUtils;
import com.mds.util.FileMisc;
import com.mds.util.HelperFunctions;
import com.mds.i18n.util.I18nUtils;
import com.mds.cm.content.nullobjects.NullDisplayObjectCreator;
import com.mds.cm.content.nullobjects.NullMimeType;

/// <summary>
/// Represents a human viewable representation of a gallery object. Examples include the thumbnail, optimized, or full-size version
/// of an image, the video of a video file, and the content of a document.
/// </summary>
public class DisplayObject{
	//#region Private Fields

	private ContentObjectBo parent;
	private long contentObjectId;
	private int width;
	private int height;
	private String filename;
	private String filenamePhysicalPath = StringUtils.EMPTY;
	private long fileSizeKB;
	private File fileInfo;
	private MimeTypeBo mimeType;
	private DisplayObjectType displayType;
	private DisplayObjectCreator displayObjectCreator;
	private String externalHtmlSource;
	private MimeTypeCategory externalType;
	private String tempFilePath;

	//#endregion

	//#region Constructors
	public DisplayObject() {}

	/// <summary>
	/// Initializes a new instance of the <see cref="DisplayObject"/> class.
	/// </summary>
	/// <param name="width">The width of this object, in pixels.</param>
	/// <param name="height">The height of this object, in pixels.</param>
	/// <param name="filename">The name of the file representing this object. Example: sonorandesert.jpg</param>
	/// <param name="parent">The content object to which this display object applies.</param>
	/// <param name="displayType">The type of the display object.</param>
	/// <param name="displayObjectCreator">The object responsible for generating the file this display object points to.</param>
	private DisplayObject(int width, int height, String filename, ContentObjectBo parent, DisplayObjectType displayType, DisplayObjectCreator displayObjectCreator) throws InvalidGalleryException	{
		this.width = width;
		this.height = height;
		this.filename = filename;

		if (!StringUtils.isBlank(filename))	{
			this.mimeType = CMUtils.loadMimeType(parent.getGalleryId(), this.filename);
		}

		if (this.mimeType == null){
			this.mimeType = new NullMimeType();
		}

		this.parent = parent;
		this.displayType = displayType;
		this.displayObjectCreator = displayObjectCreator;
		this.displayObjectCreator.setParent(this);

		if (this.parent instanceof AlbumBo)	{
			this.contentObjectId = Long.MIN_VALUE;
		}else{
			this.contentObjectId = parent.getId();
		}
	}

	/// <summary>
	/// Initializes a new instance of the <see cref="DisplayObject"/> class.
	/// </summary>
	/// <param name="parent">The parent.</param>
	/// <param name="displayType">The display type.</param>
	/// <param name="mimeType">Specifies the category to which this mime type belongs. This usually corresponds to the first portion of 
	/// the full mime type description. (e.g. "image" if the full mime type is "image/jpeg").</param>
	protected DisplayObject(ContentObjectBo parent, DisplayObjectType displayType, MimeTypeCategory mimeType)	{
		if (displayType != DisplayObjectType.External)
			throw new BusinessException(MessageFormat.format("This overload of the DisplayObject constructor can only be called when the displayType parameter is DisplayObjectType.External. Instead, it was {0}.", displayType.toString()));

		this.width = Integer.MIN_VALUE;
		this.height = Integer.MIN_VALUE;
		this.filename = StringUtils.EMPTY;
		this.mimeType = MimeTypeBo.createInstance(mimeType);
		this.externalType = this.mimeType.getTypeCategory();
		this.parent = parent;
		this.displayType = displayType;
		this.displayObjectCreator = new NullDisplayObjectCreator(this);

		if (this.parent instanceof AlbumBo){
			this.contentObjectId = Long.MIN_VALUE;
		}else{
			this.contentObjectId = parent.getId();
		}

	}

	//#endregion

	//#region Public Static Methods

	/// <summary>
	/// Create a new display object instance with the specified properties. No data is retrieved from the
	/// data store. A lazy load is used to inflate the object when necessary
	/// </summary>
	/// <param name="parent">The content object to which this display object applies.</param>
	/// <returns>Returns an instance representing a new display object with default properties.</returns>
	public static DisplayObject createInstance(ContentObjectBo parent) throws InvalidGalleryException	{
		return createInstance(parent, StringUtils.EMPTY, Integer.MIN_VALUE, Integer.MIN_VALUE, DisplayObjectType.Unknown, new NullDisplayObjectCreator());
	}

	/// <summary>
	/// Create a new display object instance with the specified properties. No data is retrieved from the
	/// data store. A lazy load is used to inflate the object when necessary
	/// </summary>
	/// <param name="parent">The content object to which this display object applies. This will typically be
	/// an Album object.</param>
	/// <param name="sourceContentObjectId">The ID of the content object to use as the source for setting this 
	/// object's properties.</param>
	/// <param name="displayType">The display object type of the source content object to use to set this object's
	/// properties. For example, if displayType=Thumbnail, then use the properties of the source media
	/// object's thumbnail object to assign to this display object's properties.</param>
	/// <returns>Create a new display object instance with the specified properties.</returns>
	/// <remarks>This overload of CreateInstance() is typically used when instantiating albums.</remarks>
	public static DisplayObject createInstance(ContentObjectBo parent, long sourceContentObjectId, DisplayObjectType displayType) throws InvalidGalleryException	{
		DisplayObject newDisObject = createInstance(parent, StringUtils.EMPTY, Integer.MIN_VALUE, Integer.MIN_VALUE, displayType, new NullDisplayObjectCreator());

		newDisObject.contentObjectId = sourceContentObjectId;

		return newDisObject;
	}

	/// <summary>
	/// Create a new display object instance with the specified properties. No data is retrieved from the
	/// data store. A lazy load is used to inflate the object when necessary
	/// </summary>
	/// <param name="parent">The content object to which this display object applies.</param>
	/// <param name="fileName">The name of the file representing this object. Example: sonorandesert.jpg</param>
	/// <param name="width">The width of this object, in pixels.</param>
	/// <param name="height">The height of this object, in pixels.</param>
	/// <param name="displayType">The type of the display object.</param>
	/// <param name="displayObjectCreator">The object responsible for generating the file this display object points to.</param>
	/// <returns>Create a new display object instance with the specified properties.</returns>
	public static DisplayObject createInstance(ContentObjectBo parent, String fileName, int width, int height, DisplayObjectType displayType, DisplayObjectCreator displayObjectCreator) throws InvalidGalleryException	{
		return new DisplayObject(width, height, fileName, parent, displayType, displayObjectCreator);
	}

	/// <summary>
	/// Create a new display object instance with the specified properties. No data is retrieved from the
	/// data store. A lazy load is used to inflate the object when necessary
	/// </summary>
	/// <param name="parent">The content object to which this display object applies.</param>
	/// <param name="displayType">The type of the display object.</param>
	/// <param name="mimeType">Specifies the category to which this mime type belongs. This usually corresponds to the first portion of 
	/// the full mime type description. (e.g. "image" if the full mime type is "image/jpeg").</param>
	/// <returns>Create a new display object instance with the specified properties.</returns>
	public static DisplayObject createInstance(ContentObjectBo parent, DisplayObjectType displayType, MimeTypeCategory mimeType){
		if (displayType != DisplayObjectType.External)
			throw new BusinessException(MessageFormat.format("This overload of DisplayObject.CreateInstance can only be called when the displayType parameter is DisplayObjectType.External. Instead, it was {0}.", displayType.toString()));

		return new DisplayObject(parent, displayType, mimeType);
	}

	//#endregion
	
	//#region Public Properties

	/// <summary>
	/// Gets or sets the gallery object this display object applies to.
	/// </summary>
	/// <value>The gallery object this display object applies to.</value>
	public ContentObjectBo getParent()	{
		return this.parent;
	}
	
	public void setParent(ContentObjectBo parent)	{
		this.parent = parent;
	}

	/// <summary>
	/// Gets or sets the width of this object, in pixels.
	/// </summary>
	/// <value>The width of this object, in pixels.</value>
	public int getWidth()	{
		verifyObjectIsInflated();

		return this.width;
	}
	
	public void setWidth(int width)	{
		this.parent.setHasChanges((this.width == width ? this.parent.getHasChanges() : true));
		this.width = width;
	}

	/// <summary>
	/// Gets or sets the height of this object, in pixels.
	/// </summary>
	/// <value>The height of this object, in pixels.</value>
	public int getHeight()	{
			verifyObjectIsInflated();

			return this.height;
	}
	
	public void setHeight(int height)	{
			this.parent.setHasChanges(  (this.height == height ? this.parent.getHasChanges() : true));
			this.height = height;
	}

	/// <summary>
	/// Gets or sets the file representing this display object. Accessing this property causes the file to be
	/// generated if it does not exist (thumbnail images only; also, for Image instances, will generate the optimized image).
	/// Returns null for external objects (<see cref="ExternalType" /> = MimeTypeCategory.External).
	/// </summary>
	/// <value>The file representing this display object, or null when this instance represents and external object
	/// (<see cref="ExternalType" /> = MimeTypeCategory.External).</value>
	/// <exception cref="MDS.EventLogs.CustomExceptions.InvalidContentObjectException">Thrown if the file 
	/// is located in a different directory than the directory of this object's containing album.</exception>
	public File getFileInfo(){
		if ((this.fileInfo == null) && (this.displayType != DisplayObjectType.External)){
			if ((StringUtils.isBlank(this.filenamePhysicalPath)) || (!FileMisc.fileExists(this.filenamePhysicalPath)))	{
				try {
					this.generateAndSaveFile();
				} catch (IOException | UnsupportedImageTypeException | InvalidGalleryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				assert !StringUtils.isBlank(this.filenamePhysicalPath) : "DisplayObject.FilenamePhysicalPath should not be empty after executing GenerateAndSaveFile().";
			}

			this.fileInfo = new File(this.filenamePhysicalPath);
		}

		return this.fileInfo;
	}
	
	public void setFileInfo(File fileInfo) throws InvalidContentObjectException, InvalidGalleryException{
		//#region Validation

		// Validate: Make sure the file is in the same directory as the album. Thumbnail and optimized files may be in a separate directory
		// as specified in the configuration file.
		if (fileInfo != null){
			AlbumBo parentAlbum = Reflections.as(AlbumBo.class, this.parent.getParent());
			if (parentAlbum != null){
				String albumOriginalPath = parentAlbum.getFullPhysicalPathOnDisk();

				GallerySettings gallerySetting = CMUtils.loadGallerySetting(this.parent.getGalleryId());

				String albumOptimizedPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(albumOriginalPath, gallerySetting.getFullOptimizedPath(), gallerySetting.getFullContentObjectPath());
				String albumThumbnailPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(albumOriginalPath, gallerySetting.getFullThumbnailPath(), gallerySetting.getFullContentObjectPath());

				String newDirPath = fileInfo.getParent();

				if (!((StringUtils.equalsIgnoreCase(newDirPath, albumOriginalPath))
					|| (StringUtils.equalsIgnoreCase(newDirPath, albumOptimizedPath))
					|| (StringUtils.equalsIgnoreCase(newDirPath, albumThumbnailPath)))){
					throw new InvalidContentObjectException(MessageFormat.format(I18nUtils.getMessage("displayObject.fileInfo_Ex_Msg"), fileInfo.getName(), parentAlbum.getId(), albumOriginalPath, albumOptimizedPath, albumThumbnailPath));
				}
			}
		}else{
			throw new ArgumentNullException("value");
		}

		//#endregion

		this.fileInfo = fileInfo;
		this.setFileName(fileInfo.getName());
		this.setFileNamePhysicalPath(fileInfo.getPath());

		this.mimeType = CMUtils.loadMimeType(this.parent.getGalleryId(), fileInfo.getName());

		if (this.mimeType == null){
			this.mimeType = new NullMimeType();
		}

	}

	/// <summary>
	/// Gets or sets the name of the file representing this object. Example: sonorandesert.jpg
	/// </summary>
	/// <value>The name of the file representing this object.</value>
	public String getFileName()	{
			return this.filename;
	}
	
	public void setFileName(String filename) throws InvalidGalleryException	{
		this.parent.setHasChanges((this.filename == filename ? this.parent.getHasChanges() : true));
		this.filename = (filename == null ? StringUtils.EMPTY : filename);

		if (!StringUtils.isBlank(filename))
			this.mimeType = CMUtils.loadMimeType(this.parent.getGalleryId(), this.filename);
	}

	/// <summary>
	/// Gets or sets the physical path to this object, including the object's name. Example:
	/// C:\Inetpub\wwwroot\MDS\contentobjects\Summer 2005\sunsets\desert sunsets\sonorandesert.jpg
	/// </summary>
	/// <value>The physical path to this object, including the object's nam.</value>
	public String getFileNamePhysicalPath()	{
		if (StringUtils.isBlank(this.filenamePhysicalPath))	{
			verifyObjectIsInflated();
		}

		return this.filenamePhysicalPath;
	}
	
	public void setFileNamePhysicalPath(String filenamePhysicalPath)	{
		this.filenamePhysicalPath = filenamePhysicalPath;
	}

	/// <summary>
	/// Gets or sets the physical path to a temporary version of this object. This property can be used as a holding area for
	/// an intermediate file that is created while processing the object, such as when ImageMagick is used to create a JPEG
	/// version of an object that is subsequently used by both the thumbnail and optimized image generators.
	/// Example: C:\Inetpub\wwwroot\MDS\App_Data\_Temp\sonorandesert.jpg
	/// </summary>
	/// <value>The physical path to a temporary version of this object.</value>
	public String getTempFilePath()	{
			return tempFilePath;
	}
	
	public void setTempFilePath(String tempFilePath)	{
			this.tempFilePath = tempFilePath;
	}

	/// <summary>
	/// Gets or sets the size of the file, in KB, for this display object.
	/// </summary>
	/// <value>The size of the file, in KB, for this display object.</value>
	public long getFileSizeKB()	{
			return this.fileSizeKB;
	}
	
	public void setFileSizeKB(long fileSizeKB)	{
			this.parent.setHasChanges((this.fileSizeKB == fileSizeKB ? this.parent.getHasChanges() : true));
			this.fileSizeKB = fileSizeKB;
	}

	/// <summary>
	/// Gets the MIME type for this display object. The MIME type is determined from the extension of the <see cref="FileName"/> property. 
	/// Returns a <see cref="NullObjects.NullMimeType" /> object if the <see cref="FileName"/> property has not been set or a 
	/// MIME type cannot be determined from the file's extension.
	/// </summary>
	/// <value>The MIME type for this display object.</value>
	public MimeTypeBo getMimeType(){
		return this.mimeType;
	}

	/// <summary>
	/// Gets or sets the ID of the content object that contains the file specified in this object. For albums, it refers to the 
	/// content object used to represent the thumbnail image. For all other objects, it refers to this object's parent ID.
	/// </summary>
	/// <value>
	/// The ID of the content object that contains the file specified in this object.
	/// </value>
	public long getContentObjectId()	{
			return this.contentObjectId;
	}
	
	public void setContentObjectId(long contentObjectId)	{
			this.contentObjectId = contentObjectId;
	}

	/// <summary>
	/// Gets or sets the type of the display object.
	/// </summary>
	/// <value>The type of the display object.</value>
	public DisplayObjectType getDisplayType()	{
			return this.displayType;
	}
	
	public void setDisplayType(DisplayObjectType displayType)	{
			this.displayType = displayType;
	}

	/// <summary>
	/// Gets or sets the object responsible for generating the file this display object points to.
	/// </summary>
	/// <value>
	/// The object responsible for generating the file this display object points to.
	/// </value>
	public DisplayObjectCreator getDisplayObjectCreator()	{
			return this.displayObjectCreator;
	}
	
	public void setDisplayObjectCreator(DisplayObjectCreator displayObjectCreator)	{
			this.displayObjectCreator = displayObjectCreator;
	}

	/// <summary>
	/// Gets or sets the HTML that defines an externally stored content object, such as videos hosted at YouTube. For local
	/// content objects, this property is an empty String.
	/// </summary>
	/// <example> 
	/// For example, for a YouTube video it may look like this:
	/// <code>
	/// <![CDATA[
	///		<object width="425" height="344">
	///			<param name="movie" value="http://www.youtube.com/v/0tNzoCw9xms&hl=en"></param>
	///			<param name="allowFullScreen" value="true"></param>
	///			<embed src="http://www.youtube.com/v/0tNzoCw9xms&hl=en" type="application/x-shockwave-flash" allowfullscreen="true" width="425" height="344"></embed>
	///		</object>]]> 
	/// </code>
	/// </example> 
	/// <value>The HTML that defines an externally stored content object, such as YouTube or Silverlight.net.</value>
	public String getExternalHtmlSource()	{
			return this.externalHtmlSource;
	}
	
	public void setExternalHtmlSource(String externalHtmlSource)	{
			this.externalHtmlSource = externalHtmlSource;
	}

	/// <summary>
	/// Gets or sets the MIME type category for an externally stored content object, such as videos hosted at YouTube or Silverlight.live.com.
	/// This property is not relevant for locally stored content objects.
	/// </summary>
	/// <value>The MIME type category for an externally stored content object.</value>
	public MimeTypeCategory getExternalType()	{
			return this.externalType;
	}
	
	public void setExternalType(MimeTypeCategory externalType)	{
			this.externalType = externalType;
	}

	//#endregion


	//#region Public Methods

	/// <summary>
	/// Generate the file for this display object and save it to the file system. The routine may decide that
	/// a file does not need to be generated, usually because it already exists. No data is persisted to the data
	/// store.
	/// </summary>
	public void generateAndSaveFile() throws IOException, UnsupportedImageTypeException, InvalidGalleryException{
		this.displayObjectCreator.generateAndSaveFile();
	}

	/// <summary>
	/// Gets the width and height of this display object. The value is calculated from the physical file. Returns an empty
	/// <see cref="System.Windows.Size" /> instance if the value cannot be computed or is not applicable to the object
	/// (for example, for audio files and external content objects).
	/// </summary>
	/// <returns><see cref="System.Windows.Size" />.</returns>
	public Size getSize() throws UnsupportedImageTypeException	{
		return this.displayObjectCreator.getSize(this);
	}

	//#endregion

	//#region Public Override Methods

	/// <summary>
	/// Serves as a hash function for a particular type. The hash code is based on the <see cref="FileNamePhysicalPath" /> property.
	/// </summary>
	/// <returns>
	/// A hash code for the current <see cref="T:System.Object"/>.
	/// </returns>
	public int hashCode()	{
		return   new  HashCodeBuilder( 17 ,  37 )
	             .append(filenamePhysicalPath)
	             .toHashCode();
	}

	//#endregion

	//#region Private Methods

	private void verifyObjectIsInflated()	{
		if (this.parent.getIsNew()){
			return; // Don't inflate for new objects - there's nothing to get from the data store.
		}

		if (!this.parent.getIsInflated()){
			this.parent.inflate();
			assert this.contentObjectId > Long.MIN_VALUE : "Inflating the parent of this DisplayObject should cause _contentObjectId to be populated, but it did not.";
		}
	}

	//#endregion
}
