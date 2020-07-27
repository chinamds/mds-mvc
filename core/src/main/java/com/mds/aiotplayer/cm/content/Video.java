package com.mds.aiotplayer.cm.content;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;

import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.metadata.ContentObjectMetadataItem;
import com.mds.aiotplayer.cm.metadata.ContentObjectMetadataItemCollection;
import com.mds.aiotplayer.cm.metadata.MetadataDefinition;
import com.mds.aiotplayer.cm.model.Metadata;
import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.core.ContentQueueItemConversionType;
import com.mds.aiotplayer.core.DisplayObjectType;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.core.MimeTypeCategory;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.i18n.util.I18nUtils;

/// <summary>
/// The Video class represents a content object within MDS System that is a video.
/// </summary>
public class Video extends ContentObjectBo implements ContentObjectListener{
	//#region Private Fields


	//#endregion

	//#region Properties

	/// <summary>
	/// Gets the gallery object type.
	/// </summary>
	/// <value>
	/// An instance of <see cref="ContentObjectType" />.
	/// </value>
	public ContentObjectType getContentObjectType()	{
		return ContentObjectType.Video;
	}

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of a <see cref="Video" /> object.
	/// </summary>
	/// <param name="videoFile">A <see cref="FileInfo"/> object containing the original video file for this object. This is intended to be 
	/// specified when creating a new content object from a file. Specify null when instantiating an object for an existing database
	/// record.</param>
	/// <param name="parentAlbum">The album that contains this object. This is a required parameter.</param>
	/// <exception cref="MDS.EventLogs.CustomExceptions.InvalidContentObjectException">Thrown when 
	/// <paramref name="videoFile"/> refers to a file that is not in the same directory as the parent album's directory.</exception>
	/// <exception cref="MDS.EventLogs.CustomExceptions.UnsupportedContentObjectTypeException">Thrown when
	/// <paramref name="videoFile"/> is specified (not null) and its file extension does not correspond to an video MIME
	/// type, as determined by the MIME type definition in the configuration file.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="parentAlbum" /> is null.</exception>
	/// <remarks>This constructor does not verify that <paramref name="videoFile"/> refers to a file type that is enabled in the 
	/// configuration file.</remarks>
	public Video(File videoFile, AlbumBo parentAlbum) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidGalleryException	{
		 this(Long.MIN_VALUE, parentAlbum, StringUtils.EMPTY,
				Integer.MIN_VALUE, Integer.MIN_VALUE, Long.MIN_VALUE, StringUtils.EMPTY, Integer.MIN_VALUE, Integer.MIN_VALUE,
				Long.MIN_VALUE, StringUtils.EMPTY, Integer.MIN_VALUE, Integer.MIN_VALUE, Long.MIN_VALUE, Integer.MIN_VALUE,
				StringUtils.EMPTY, DateUtils.Now(), StringUtils.EMPTY, DateUtils.MinValue, parentAlbum != null ? parentAlbum.isPrivate : false, false, videoFile, null);
	}

	/// <summary>
	/// Initializes a new instance of an <see cref="Video" /> object.
	/// </summary>
	/// <param name="id">The ID that uniquely identifies this object. Specify Long.MIN_VALUE for a new object.</param>
	/// <param name="parentAlbum">The album that contains this object. This is a required parameter.</param>
	/// <param name="thumbnailFilename">The filename of the thumbnail image.</param>
	/// <param name="thumbnailWidth">The width (px) of the thumbnail image.</param>
	/// <param name="thumbnailHeight">The height (px) of the thumbnail image.</param>
	/// <param name="thumbnailSizeKb">The size (KB) of the thumbnail image.</param>
	/// <param name="optimizedFilename">The filename of the optimized image.</param>
	/// <param name="optimizedWidth">The width (px) of the optimized image.</param>
	/// <param name="optimizedHeight">The height (px) of the optimized image.</param>
	/// <param name="optimizedSizeKb">The size (KB) of the optimized image.</param>
	/// <param name="originalFilename">The filename of the original image.</param>
	/// <param name="originalWidth">The width (px) of the original image.</param>
	/// <param name="originalHeight">The height (px) of the original image.</param>
	/// <param name="originalSizeKb">The size (KB) of the original image.</param>
	/// <param name="sequence">An integer that represents the order in which this image should appear when displayed.</param>
	/// <param name="createdByUsername">The user name of the account that originally added this object to the data store.</param>
	/// <param name="dateAdded">The date this image was added to the data store.</param>
	/// <param name="lastModifiedByUsername">The user name of the account that last modified this object.</param>
	/// <param name="dateLastModified">The date this object was last modified.</param>
	/// <param name="isPrivate">Indicates whether this object should be hidden from un-authenticated (anonymous) users.</param>
	/// <param name="isInflated">A boolean indicating whether this object is fully inflated.</param>
	/// <param name="videoFile">A <see cref="FileInfo"/> object containing the original video file for this object. This is intended to be 
	///   specified when creating a new content object from a file. Specify null when instantiating an object for an existing database
	///   record.</param>
	/// <param name="metadata">A collection of <see cref="Data.MetadataDto" /> instances containing metadata for the
	///   object. Specify null if not available.</param>
	/// <exception cref="MDS.EventLogs.CustomExceptions.InvalidContentObjectException">Thrown when
	/// <paramref name="videoFile"/> is specified (not null) and the file it refers to is not in the same directory
	/// as the parent album's directory.</exception>
	/// <exception cref="MDS.EventLogs.CustomExceptions.UnsupportedContentObjectTypeException">Thrown when
	/// <paramref name="videoFile"/> is specified (not null) and its file extension does not correspond to an video MIME
	/// type, as determined by the MIME type definition in the configuration file.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="parentAlbum" /> is null.</exception>
	/// <remarks>This constructor does not verify that <paramref name="videoFile"/> refers to a file type that is enabled in the 
	/// configuration file.</remarks>
	public Video(long id, AlbumBo parentAlbum, String thumbnailFilename, int thumbnailWidth, int thumbnailHeight, long thumbnailSizeKb, 
			String optimizedFilename, int optimizedWidth, int optimizedHeight, long optimizedSizeKb, String originalFilename, 
			int originalWidth, int originalHeight, long originalSizeKb, int sequence, String createdByUsername, Date dateAdded, 
			String lastModifiedByUsername, Date dateLastModified, boolean isPrivate, boolean isInflated, File videoFile, 
			Iterable<Metadata> metadata) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		
		super();
		
		if (parentAlbum == null)
			throw new ArgumentNullException("parentAlbum");

		this.setId(id);
		this.setParent(parentAlbum);
		this.setGalleryId(this.parent.getGalleryId());
		//this.Title = title;
		this.setSequence(sequence);
		this.setCreatedByUserName(createdByUsername);
		this.setDateAdded(dateAdded);
		this.setLastModifiedByUserName(lastModifiedByUsername);
		this.setDateLastModified(dateLastModified);
		this.setIsPrivate(isPrivate);

		this.saveBehavior = CMUtils.getContentObjectSaveBehavior(this);
		this.deleteBehavior = CMUtils.getContentObjectDeleteBehavior(this);
		this.metadataReadWriter = CMUtils.getMetadataReadWriter(this);

		String parentPhysicalPath = this.parent.getFullPhysicalPathOnDisk();

		GallerySettings gallerySetting = CMUtils.loadGallerySetting(galleryId);

		// Thumbnail image
		this.setThumbnail(DisplayObject.createInstance(this, thumbnailFilename, thumbnailWidth, thumbnailHeight, DisplayObjectType.Thumbnail, new VideoThumbnailCreator(this)));
		this.getThumbnail().setFileSizeKB(thumbnailSizeKb);
		if (thumbnailFilename.length() > 0)	{
			// The thumbnail is stored in either the album's physical path or an alternate location (if thumbnailPath config setting is specified) .
			String thumbnailPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(parentPhysicalPath, gallerySetting.getFullThumbnailPath(), gallerySetting.getFullContentObjectPath());
			this.getThumbnail().setFileNamePhysicalPath(FilenameUtils.concat(thumbnailPath, thumbnailFilename));
		}

		// Optimized video
		this.setOptimized(DisplayObject.createInstance(this, optimizedFilename, optimizedWidth, optimizedHeight, DisplayObjectType.Optimized, new VideoOptimizedCreator(this)));
		this.getOptimized().setFileSizeKB(optimizedSizeKb);
		if (optimizedFilename.length() > 0)	{
			// Calcululate the full file path to the optimized video. If the optimized filename is equal to the original filename, then no
			// optimized version exists, and we'll just point to the original. If the names are different, then there is a separate optimized
			// image file, and it is stored in either the album's physical path or an alternate location (if optimizedPath config setting is specified).
			String optimizedPath = parentPhysicalPath;

			if (optimizedFilename != originalFilename)
				optimizedPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(parentPhysicalPath, gallerySetting.getFullOptimizedPath(), gallerySetting.getFullContentObjectPath());

			this.getOptimized().setFileNamePhysicalPath(FilenameUtils.concat(optimizedPath, optimizedFilename));
		}

		// Original video file
		this.setOriginal(DisplayObject.createInstance(this, originalFilename, originalWidth, originalHeight, DisplayObjectType.Original, new VideoOriginalCreator(this)));
		this.getOriginal().setExternalHtmlSource(StringUtils.EMPTY);
		this.getOriginal().setExternalType(MimeTypeCategory.NotSet);

		if (videoFile != null){
			this.getOptimized().setFileInfo(videoFile); // Will throw InvalidContentObjectException if the file's directory is not the same as the album's directory.
			this.getOriginal().setFileInfo(videoFile); // Will throw InvalidContentObjectException if the file's directory is not the same as the album's directory.

			if (this.getMimeType().getTypeCategory() != MimeTypeCategory.Video){
				throw new UnsupportedContentObjectTypeException(this.getOriginal().getFileInfo());
			}

			this.getOptimized().setWidth(gallerySetting.getDefaultVideoPlayerWidth());
			this.getOptimized().setHeight(gallerySetting.getDefaultVideoPlayerHeight());

			this.getOriginal().setWidth(gallerySetting.getDefaultVideoPlayerWidth());
			this.getOriginal().setHeight(gallerySetting.getDefaultVideoPlayerHeight());

			int fileSize = (int)(videoFile.length() / 1024);
			this.getOptimized().setFileSizeKB((fileSize < 1 ? 1 : fileSize)); // Very small files should be 1, not 0.
			this.getOriginal().setFileSizeKB((fileSize < 1 ? 1 : fileSize)); // Very small files should be 1, not 0.

			if (this.isNew){
				extractMetadata();

				// Grab the width and height that was extracted, if any.
				ContentObjectMetadataItem metaItem;
				if ((metaItem = getMetadataItems().tryGetMetadataItem(MetadataItemName.Width)) != null)
					this.getOriginal().setWidth(StringUtils.toInteger(metaItem.getRawValue()));

				if (( metaItem = getMetadataItems().tryGetMetadataItem(MetadataItemName.Height)) != null)
					this.getOriginal().setHeight(StringUtils.toInteger(metaItem.getRawValue()));
			}
		}else{
			this.getOriginal().setFileNamePhysicalPath(FilenameUtils.concat(parentPhysicalPath, originalFilename));
			this.getOriginal().setFileSizeKB(originalSizeKb);
		}

		if (metadata != null)
			addMeta(ContentObjectMetadataItemCollection.fromMetaDtos(this, metadata));

		/*if (approvaldata != null)
			AddApproval(ContentObjectApprovalCollection.FromApprovalDtos(this, approvaldata));*/

		this.isInflated = isInflated;

		// Setting the previous properties has caused HasChanges = true, but we don't want this while
		// we're instantiating a new object. Reset to false.
		this.hasChanges = false;

		// Set up our event handlers.
		//this.Saving += new EventHandler(Image_Saving); // Don't need
		//this.Saved += ContentObject_Saved;
		addContentObjectListener(this);
	}

	//#endregion

	//#region Methods

	/// <summary>
	/// Inflate the current object by loading all properties from the data store. If the object is already inflated 
	/// (<see cref="ContentObjectBo.IsInflated"/>=true), no action is taken.
	/// </summary>
	@Override
	public void inflate(){
		// If this is not a new object, and it has not been inflated
		// from the database, go to the database and retrieve the info for this object.
		if ((!this.isNew) && (!this.isInflated))	{
			try {
				CMUtils.loadVideoInstance(this);
			} catch (InvalidContentObjectException | UnsupportedContentObjectTypeException | InvalidAlbumException
					| UnsupportedImageTypeException | InvalidGalleryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if ((!this.isInflated) || (this.hasChanges))
				throw new UnsupportedOperationException(I18nUtils.getMessage("video.inflate_Ex_Msg", this.isInflated, this.hasChanges));
		}
	}

	/// <summary>
	/// Gets a value indicating whether the specified <paramref name="metaDef" />
	/// applies to the current gallery object.
	/// </summary>
	/// <param name="metaDef">The metadata definition.</param>
	/// <returns><c>true</c> when the specified metadata item should be displayed; otherwise <c>false</c>.</returns>
	@Override
	public boolean metadataDefinitionApplies(MetadataDefinition metaDef) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		switch (metaDef.getMetadataItemName())	{
			case HtmlSource: return false;
			default:
				return super.metadataDefinitionApplies(metaDef);
		}
	}

	 //#endregion

	//#region Event Handlers

	@Override
	public void saved(ContentObjectEvent e)	{
		// This event is fired when the Save() method is called, after all data is saved.

		//#region Assign DisplayObject.ContentObjectId

		// If the ContentObjectId has not yet been assigned, do so now. This will occur after a content object is first
		// saved, since that is when the ID is generated.
		if (this.thumbnail.getContentObjectId() == Long.MIN_VALUE){
			this.thumbnail.setContentObjectId(this.id);
		}

		if (this.optimized.getContentObjectId() == Long.MIN_VALUE){
			this.optimized.setContentObjectId(this.id);
		}

		if (this.getOriginal().getContentObjectId() == Long.MIN_VALUE){
			this.getOriginal().setContentObjectId(this.id);
		}

		//#endregion

		// Create web-friendly version *after* item has been persisted to data store.
		// We need to do it this way because we need the content object ID for the media queue table.
		if (!ContentConversionQueue.getInstance().isWaitingInQueueOrProcessing(this.id, ContentQueueItemConversionType.CreateOptimized)){
			try {
				this.optimized.generateAndSaveFile();
			} catch (IOException | UnsupportedImageTypeException | InvalidGalleryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	//#endregion
}
