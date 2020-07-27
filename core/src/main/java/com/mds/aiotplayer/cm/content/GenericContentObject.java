package com.mds.aiotplayer.cm.content;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.mds.aiotplayer.cm.content.nullobjects.NullDisplayObjectCreator;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.metadata.ContentObjectMetadataItemCollection;
import com.mds.aiotplayer.cm.metadata.MetadataDefinition;
import com.mds.aiotplayer.cm.model.Metadata;
import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.core.DisplayObjectType;
import com.mds.aiotplayer.core.MimeTypeCategory;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.i18n.util.I18nUtils;

/// <summary>
/// The <see cref="GenericContentObject" /> class represents a content object within MDS System that is not one of the other media types
/// that have specific classes for handling their functionality. That is, any content object that is not an <see cref="Image" />, 
/// <see cref="Video" />, <see cref="Audio" />, or <see cref="ExternalContentObject" /> object is a <see cref="GenericContentObject" />.
/// </summary>
public class GenericContentObject extends ContentObjectBo  implements ContentObjectListener{
	//#region Constructors

	/// <summary>
	/// Initializes a new instance of a <see cref="GenericContentObject" /> object.
	/// </summary>
	/// <param name="file">A <see cref="FileInfo"/> object containing the original file for this object. This is intended to be 
	/// specified when creating a new content object from a file. Specify null when instantiating an object for an existing database
	/// record.</param>
	/// <param name="parentAlbum">The album that contains this object. This is a required parameter.</param>
	/// <exception cref="MDS.EventLogs.CustomExceptions.InvalidContentObjectException">Thrown when the  
	/// <paramref name="file"/> refers to a file that is not in the same directory as the parent album's directory.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="parentAlbum" /> is null.</exception>
	/// <remarks>This constructor does not verify that <paramref name="file"/> refers to a file type that is enabled in the 
	/// configuration file.</remarks>
	public GenericContentObject(File file, AlbumBo parentAlbum) throws InvalidContentObjectException, InvalidGalleryException	{
		 this(Long.MIN_VALUE, parentAlbum, StringUtils.EMPTY,
				 Integer.MIN_VALUE, Integer.MIN_VALUE, Long.MIN_VALUE, StringUtils.EMPTY, Integer.MIN_VALUE, Integer.MIN_VALUE, Long.MIN_VALUE, Integer.MIN_VALUE,
				 StringUtils.EMPTY, DateUtils.Now(), StringUtils.EMPTY, DateUtils.MinValue, parentAlbum != null ? parentAlbum.isPrivate : false, false, file, null);
	}

	/// <summary>
	/// Initializes a new instance of a <see cref="GenericContentObject" /> object.
	/// </summary>
	/// <param name="id">The ID that uniquely identifies this object. Specify Integer.MIN_VALUE for a new object.</param>
	/// <param name="parentAlbum">The album that contains this object. This is a required parameter.</param>
	/// <param name="thumbnailFilename">The filename of the thumbnail image.</param>
	/// <param name="thumbnailWidth">The width (px) of the thumbnail image.</param>
	/// <param name="thumbnailHeight">The height (px) of the thumbnail image.</param>
	/// <param name="thumbnailSizeKb">The size (KB) of the thumbnail image.</param>
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
	/// <param name="file">A <see cref="FileInfo"/> object containing the original file for this object. This is intended to be 
	///   specified when creating a new content object from a file. Specify null when instantiating an object for an existing database
	///   record.</param>
	/// <param name="metadata">A collection of <see cref="Data.MetadataDto" /> instances containing metadata for the
	///   object. Specify null if not available.</param>
	/// <exception cref="MDS.EventLogs.CustomExceptions.InvalidContentObjectException">Thrown when 
	/// the <paramref name="file"/> parameter is specified (not null) and the file it refers to is not in the same directory
	/// as the parent album's directory.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="parentAlbum" /> is null.</exception>
	/// <remarks>This constructor does not verify that <paramref name="file"/> refers to a file type that is enabled in the 
	/// configuration file.</remarks>
	public GenericContentObject(long id, AlbumBo parentAlbum, String thumbnailFilename, int thumbnailWidth, int thumbnailHeight, long thumbnailSizeKb
			, String originalFilename, int originalWidth, int originalHeight, long originalSizeKb, int sequence, String createdByUsername, Date dateAdded
			, String lastModifiedByUsername, Date dateLastModified, boolean isPrivate, boolean isInflated, File file, Iterable<Metadata> metadata) throws InvalidContentObjectException, InvalidGalleryException	{
		
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
		this.setThumbnail(DisplayObject.createInstance(this, thumbnailFilename, thumbnailWidth, thumbnailHeight, DisplayObjectType.Thumbnail, new GenericThumbnailCreator(this)));
		this.getThumbnail().setFileSizeKB(thumbnailSizeKb);
		if (thumbnailFilename.length() > 0)	{
			// The thumbnail is stored in either the album's physical path or an alternate location (if thumbnailPath config setting is specified) .
			String thumbnailPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(parentPhysicalPath, gallerySetting.getFullThumbnailPath(), gallerySetting.getFullContentObjectPath());
			this.getThumbnail().setFileNamePhysicalPath(FilenameUtils.concat(thumbnailPath, thumbnailFilename));
		}

		// GenericContentObject instances do not have an optimized version.
		this.setOptimized(DisplayObject.createInstance(this, originalFilename, originalWidth, originalHeight, DisplayObjectType.Optimized, new NullDisplayObjectCreator()));

		// Original file
		this.setOriginal(DisplayObject.createInstance(this, originalFilename, originalWidth, originalHeight, DisplayObjectType.Original, new NullDisplayObjectCreator()));
		this.getOriginal().setExternalHtmlSource(StringUtils.EMPTY);
		this.getOriginal().setExternalType(MimeTypeCategory.NotSet);

		if (file != null){
			this.getOptimized().setFileInfo(file); // Will throw InvalidContentObjectException if the file's directory is not the same as the album's directory.
			this.getOriginal().setFileInfo(file); // Will throw InvalidContentObjectException if the file's directory is not the same as the album's directory.

			if (this.getMimeType().getTypeCategory() == MimeTypeCategory.Other){
				// Specify a default width and height for any object other than audio, video, and image. We leave those to their default
				// value of Integer.MIN_VALUE because we do not accurately know their real width and height. For example, a corrupt image file 
				// will be rejected by the Image class (an UnsupportedImageTypeException is thrown) and will be routed to this class instead.
				// In this case, we don't know it's real width and height. Similarly, audio and video files are normally handled by the
				// Audio and Video classes. If one of them ends up here, we need to treat it generically and not assign a width and height.
				this.getOptimized().setWidth(gallerySetting.getDefaultGenericObjectWidth());
				this.getOptimized().setHeight(gallerySetting.getDefaultGenericObjectHeight());

				this.getOriginal().setWidth(gallerySetting.getDefaultGenericObjectWidth());
				this.getOriginal().setHeight(gallerySetting.getDefaultGenericObjectHeight());
			}

			int fileSize = (int)(file.length() / 1024);
			this.getOptimized().setFileSizeKB((fileSize < 1 ? 1 : fileSize)); // Very small files should be 1, not 0.
			this.getOriginal().setFileSizeKB((fileSize < 1 ? 1 : fileSize)); // Very small files should be 1, not 0.

			if (isNew){
				extractMetadata();
			}

			//// Assign the title, resorting to the filename if necessary.
			//if (StringUtils.isBlank(title))
			//{
			//	SetTitle();

			//	if (StringUtils.isBlank(this.Title))
			//	{
			//		this.Title = file.Name;
			//	}
			//}
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
		//this.Saved += Image_Saved;
		addContentObjectListener(this);
	}

	//#endregion

	//#region Properties

	/// <summary>
	/// Gets the gallery object type.
	/// </summary>
	/// <value>
	/// An instance of <see cref="ContentObjectType" />.
	/// </value>
	public ContentObjectType getContentObjectType()	{
		return ContentObjectType.Generic;
	}

	//#endregion

	//#region Methods

	/// <summary>
	/// Inflate the current object by loading all properties from the data store. If the object is already inflated 
	/// (<see cref="IContentObject.isInflated"/>=true), no action is taken.
	/// </summary>
	public void inflate(){
		// If this is not a new object, and it has not been inflated
		// from the database, go to the database and retrieve the info for this object.
		if ((!this.isNew) && (!this.isInflated)){
			try {
				CMUtils.loadGenericContentObjectInstance(this);
			} catch (InvalidContentObjectException | UnsupportedContentObjectTypeException | InvalidAlbumException
					| UnsupportedImageTypeException | InvalidGalleryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if ((!this.isInflated) || (this.hasChanges))
				throw new UnsupportedOperationException(I18nUtils.getMessage("genericContentObject.inflate_Ex_Msg", this.isInflated, this.hasChanges));
		}
	}

	/// <summary>
	/// Gets a value indicating whether the specified <paramref name="metaDef" />
	/// applies to the current gallery object.
	/// </summary>
	/// <param name="metaDef">The metadata definition.</param>
	/// <returns><c>true</c> when the specified metadata item should be displayed; otherwise <c>false</c>.</returns>
	public boolean metadataDefinitionApplies(MetadataDefinition metaDef) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		switch (metaDef.getMetadataItemName()){
			case HtmlSource: return false;
			default:
				return super.metadataDefinitionApplies(metaDef);
		}
	}

	//#endregion

	//#region Event Handlers

	@Override
	public void saved(ContentObjectEvent e){
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
