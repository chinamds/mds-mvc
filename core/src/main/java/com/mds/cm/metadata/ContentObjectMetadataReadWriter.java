package com.mds.cm.metadata;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.mds.cm.content.ContentObjectBo;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.util.CMUtils;
import com.mds.core.MetadataItemName;
import com.mds.core.exception.NotSupportedException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.util.DateUtils;

/// <summary>
/// An abstract base class that provides functionality for reading and writing metadata to and from a gallery 
/// object. The concrete implementations (<see cref="ContentObjectMetadataReadWriter" />, 
/// <see cref="ImageMetadataReadWriter" />, <see cref="AlbumMetadataReadWriter" />, 
/// <see cref="VideoMetadataReadWriter" />, <see cref="AudioMetadataReadWriter" />, etc.) inherit from this 
/// one and delegate to this class when the subclassed <see cref="IMetadataReadWriter.GetMetaValue" /> 
/// method does not provide a specific implementation.
/// </summary>
/// <remarks>For example, because the <see cref="MetadataItemName.DateAdded" /> metadata
/// item applies to all gallery objects, it is implemented in this class. Metadata specific to
/// images are implemented in the <see cref="ImageMetadataReadWriter" /> class (e.g. 
/// <see cref="MetadataItemName.FocalLength" />). Metadata that is common to all media
/// objects are implemented in <see cref="ContentObjectMetadataReadWriter" />.</remarks>
public abstract class ContentObjectMetadataReadWriter implements MetadataReadWriter{
	protected String dateTimeFormatString;
	protected ContentObjectBo contentObject;

	//#region Properties

	/// <summary>
	/// Gets the gallery object from which metadata is to be extracted.
	/// </summary>
	/// <value>An instance of <see cref="ContentObject" />.</value>
	public ContentObjectBo getContentObject() { 
		return contentObject; 
	}

	/// <summary>
	/// Gets or sets the format String to use for <see cref="DateTime" /> metadata values. The date type of each meta item
	/// is specified by the <see cref="IMetadataDefinition.DataType" /> property.
	/// </summary>
	/// <value>The date time format String.</value>
	public String getDateTimeFormatString(){
		if (StringUtils.isBlank(dateTimeFormatString)){
			try {
				dateTimeFormatString = CMUtils.loadGallerySetting(getContentObject().getGalleryId()).getMetadataDateTimeFormatString();
			} catch (UnsupportedContentObjectTypeException | InvalidGalleryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return dateTimeFormatString;
	}

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="ContentObjectMetadataReadWriter" /> class.
	/// </summary>
	/// <param name="galleryObject">The gallery object.</param>
	protected ContentObjectMetadataReadWriter(ContentObjectBo contentObject){
		this.contentObject = contentObject;
	}

	//#endregion

	//#region Methods

	/// <summary>
	/// Extracts a metadata instance for the specified <paramref name="metaName" />.
	/// </summary>
	/// <param name="metaName">Name of the metadata item to retrieve.</param>
	/// <returns>An instance that implements <see cref="IMetaValue" />.</returns>
	public MetaValue getMetaValue(MetadataItemName metaName){
		switch (metaName){
			case DateAdded:
				return new MetaValue(DateUtils.formatDate(contentObject.getDateAdded(), getDateTimeFormatString()), DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(contentObject.getDateAdded()));

			default:
				return null;
		}
	}

	/// <summary>
	/// Persists the meta value identified by <paramref name="metaName" /> to the media file. It is expected the meta item
	/// exists in <see cref="ContentObject.MetadataItems" />.
	/// </summary>
	/// <param name="metaName">Name of the meta item to persist.</param>
	/// <exception cref="System.NotSupportedException"></exception>
	public void saveMetaValue(MetadataItemName metaName){
		throw new NotSupportedException();
	}

	/// <summary>
	/// Permanently removes the meta value from the media file. The item is also removed from
	/// <see cref="ContentObject.MetadataItems" />.
	/// </summary>
	/// <param name="metaName">Name of the meta item to delete.</param>
	/// <exception cref="System.NotSupportedException"></exception>
	public void deleteMetaValue(MetadataItemName metaName){
		throw new NotSupportedException();
	}

	//#endregion
}