package com.mds.cm.content;

import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.mds.cm.content.nullobjects.NullDisplayObject;
import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidContentObjectException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.cm.metadata.ContentObjectMetadataItemCollection;
import com.mds.cm.model.Metadata;
import com.mds.core.ContentObjectType;
import com.mds.core.DisplayObjectType;
import com.mds.core.MimeTypeCategory;
import com.mds.core.exception.ArgumentNullException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.cm.util.CMUtils;
import com.mds.util.DateUtils;
import com.mds.util.HelperFunctions;
import com.mds.i18n.util.I18nUtils;

/// <summary>
/// The <see cref="ExternalContentObject" /> class represents a content object within MDS System that references an 
/// externally stored object, such as a video on YouTube or Silverlight.streaming.net.
/// </summary>
public class ExternalContentObject extends ContentObjectBo  implements ContentObjectListener{
	//#region Private Fields


	//#endregion

	//#region Properties

	/// <summary>
	/// Gets the gallery object type.
	/// </summary>
	/// <value>
	/// An instance of <see cref="ContentObjectType" />.
	/// </value>
	public ContentObjectType getContentObjectType(){
		return ContentObjectType.External;
	}

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of a ExternalContentObject object.
	/// </summary>
	/// <param name="externalHtmlSource">The HTML that defines an externally stored content object, such as one hosted at 
	/// YouTube or Silverlight.live.com.</param>
	/// <param name="mimeType">Specifies the category to which this mime type belongs. This usually corresponds to the first portion of 
	/// the full mime type description. (e.g. "image" if the full mime type is "image/jpeg").</param>
	/// <param name="parentAlbum">The album that contains this object. This is a required parameter.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="parentAlbum" /> is null.</exception>
	public ExternalContentObject(String externalHtmlSource, MimeTypeCategory mimeType, AlbumBo parentAlbum) throws InvalidGalleryException{
		this(Long.MIN_VALUE, parentAlbum, StringUtils.EMPTY, Integer.MIN_VALUE, Integer.MIN_VALUE, Long.MIN_VALUE, externalHtmlSource, mimeType, Integer.MIN_VALUE, StringUtils.EMPTY,
				DateUtils.Now(), StringUtils.EMPTY, DateUtils.MinValue, parentAlbum != null ? parentAlbum.isPrivate : false, false, null);
	}

	/// <summary>
	/// Initializes a new instance of a ExternalContentObject object.
	/// </summary>
	/// <param name="id">The ID that uniquely identifies this object. Specify Integer.MIN_VALUE for a new object.</param>
	/// <param name="parentAlbum">The album that contains this object. This is a required parameter.</param>
	/// <param name="thumbnailFilename">The filename of the thumbnail image.</param>
	/// <param name="thumbnailWidth">The width (px) of the thumbnail image.</param>
	/// <param name="thumbnailHeight">The height (px) of the thumbnail image.</param>
	/// <param name="thumbnailSizeKb">The size (KB) of the thumbnail image.</param>
	/// <param name="externalHtmlSource">The HTML that defines an externally stored content object, such as one hosted at
	///   YouTube or Silverlight.live.com.</param>
	/// <param name="mimeType">Specifies the category to which this mime type belongs. This usually corresponds to the first portion of
	///   the full mime type description. (e.g. "image" if the full mime type is "image/jpeg").</param>
	/// <param name="sequence">An integer that represents the order in which this image should appear when displayed.</param>
	/// <param name="createdByUsername">The user name of the account that originally added this object to the data store.</param>
	/// <param name="dateAdded">The date this image was added to the data store.</param>
	/// <param name="lastModifiedByUsername">The user name of the account that last modified this object.</param>
	/// <param name="dateLastModified">The date this object was last modified.</param>
	/// <param name="isPrivate">Indicates whether this object should be hidden from un-authenticated (anonymous) users.</param>
	/// <param name="isInflated">A boolean indicating whether this object is fully inflated.</param>
	/// <param name="metadata">A collection of <see cref="Data.MetadataDto" /> instances containing metadata for the
	///   object. Specify null if not available.</param>
	/// <exception cref="MDS.EventLogs.CustomExceptions.InvalidContentObjectException">Thrown when
	/// the file parameter is specified (not null) and the file it refers to is not in the same directory
	/// as the parent album's directory.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="parentAlbum" /> is null.</exception>
	public ExternalContentObject(long id, AlbumBo parentAlbum, String thumbnailFilename, int thumbnailWidth, int thumbnailHeight, long thumbnailSizeKb
			, String externalHtmlSource, MimeTypeCategory mimeType, int sequence, String createdByUsername, Date dateAdded, String lastModifiedByUsername
			, Date dateLastModified, boolean isPrivate, boolean isInflated, Iterable<Metadata> metadata) throws InvalidGalleryException{
		
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

		String parentPhysicalPath = this.getParent().getFullPhysicalPathOnDisk();

		GallerySettings gallerySetting = CMUtils.loadGallerySetting(galleryId);

		// Thumbnail image
		this.setThumbnail(DisplayObject.createInstance(this, thumbnailFilename, thumbnailWidth, thumbnailHeight, DisplayObjectType.Thumbnail, new ExternalThumbnailCreator(this)));
		this.getThumbnail().setFileSizeKB(thumbnailSizeKb);
		if (thumbnailFilename.length() > 0){
			// The thumbnail is stored in either the album's physical path or an alternate location (if thumbnailPath config setting is specified).
			String thumbnailPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(parentPhysicalPath, gallerySetting.getFullThumbnailPath(), gallerySetting.getFullContentObjectPath());
			this.getThumbnail().setFileNamePhysicalPath(FilenameUtils.concat(thumbnailPath, thumbnailFilename));
		}

		// ExternalContentObject instances do not have an optimized version.
		this.setOptimized(new NullDisplayObject());

		// Original file
		this.setOriginal(DisplayObject.createInstance(this, DisplayObjectType.External, mimeType));
		this.getOriginal().setExternalHtmlSource(externalHtmlSource);

		switch (mimeType){
			case Audio:
				this.getOriginal().setWidth(gallerySetting.getDefaultAudioPlayerWidth());
				this.getOriginal().setHeight(gallerySetting.getDefaultAudioPlayerHeight());
				break;
			case Video:
				this.getOriginal().setWidth(gallerySetting.getDefaultVideoPlayerWidth());
				this.getOriginal().setHeight(gallerySetting.getDefaultVideoPlayerHeight());
				break;
			case Image:
			case Other:
				this.getOriginal().setWidth(gallerySetting.getDefaultGenericObjectWidth());
				this.getOriginal().setHeight(gallerySetting.getDefaultGenericObjectHeight());
				break;
		}

		if (isNew){
			extractMetadata();
		}

		if (metadata != null)
			addMeta(ContentObjectMetadataItemCollection.fromMetaDtos(this, metadata));

		/*if (approvaldata != null)
			AddApproval(ContentObjectApprovalCollection.FromApprovalDtos(this, approvaldata));
*/
		this.isInflated = isInflated;

		// Setting the previous properties has caused HasChanges = true, but we don't want this while
		// we're instantiating a new object. Reset to false.
		this.hasChanges = false;

		// Set up our event handlers.
		//this.Saving += new EventHandler(Image_Saving); // Don't need
		//this.Saved += Image_Saved;
		addContentObjectListener(this);
	}

	//#endregion

	//#region Methods

	/// <summary>
	/// Inflate the current object by loading all properties from the data store. If the object is already inflated 
	/// (<see cref="ContentObjectBo.IsInflated"/>=true), no action is taken.
	/// </summary>
	public void inflate(){
		// If this is not a new object, and it has not been inflated
		// from the database, go to the database and retrieve the info for this object.
		if ((!this.isNew) && (!this.isInflated)){
			try {
				CMUtils.loadExternalContentObjectInstance(this);
			} catch (InvalidContentObjectException | UnsupportedContentObjectTypeException | InvalidAlbumException
					| UnsupportedImageTypeException | InvalidGalleryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if ((!this.isInflated) || (this.hasChanges))
				throw new UnsupportedOperationException(I18nUtils.getMessage("genericContentObject.inflate_Ex_Msg", this.isInflated, this.hasChanges));
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
		if (this.thumbnail.getContentObjectId() == Integer.MIN_VALUE){
			this.thumbnail.setContentObjectId (this.id);
		}

		if (this.optimized.getContentObjectId() == Integer.MIN_VALUE){
			this.optimized.setContentObjectId (this.id);
		}

		if (this.getOriginal().getContentObjectId() == Integer.MIN_VALUE){
			this.getOriginal().setContentObjectId (this.id);
		}

		//#endregion
	}

	//#endregion
}
