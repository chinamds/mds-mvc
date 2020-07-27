package com.mds.aiotplayer.cm.metadata;

import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.core.MetadataItemName;

/// <summary>
/// Provides functionality for reading and writing metadata to or from a gallery object.
/// </summary>
public interface MetadataReadWriter
{
	/// <summary>
	/// Gets the gallery object from which metadata is to be extracted.
	/// </summary>
	/// <value>An instance of <see cref="ContentObject" />.</value>
	ContentObjectBo getContentObject();

	/// <summary>
	/// Gets or sets the format string to use for <see cref="DateTime" /> metadata values. The date type of each meta item
	/// is specified by the <see cref="IMetadataDefinition.DataType" /> property.
	/// </summary>
	String getDateTimeFormatString();

	/// <summary>
	/// Extracts a metadata instance for the specified <paramref name="metaName" />.
	/// </summary>
	/// <param name="metaName">Name of the metadata item to retrieve.</param>
	/// <returns>An instance that implements <see cref="IMetaValue" />.</returns>
	MetaValue getMetaValue(MetadataItemName metaName);

	/// <summary>
	/// Persists the meta value identified by <paramref name="metaName" /> to the media file. It is expected the meta item
	/// exists in <see cref="IContentObject.MetadataItems" />.
	/// </summary>
	/// <param name="metaName">Name of the meta item to persist.</param>
	void saveMetaValue(MetadataItemName metaName);

	/// <summary>
	/// Permanently removes the meta value from the media file. The item is also removed from 
	/// <see cref="ContentObject.MetadataItems" />.
	/// </summary>
	/// <param name="metaName">Name of the meta item to delete.</param>
	void deleteMetaValue(MetadataItemName metaName);
}