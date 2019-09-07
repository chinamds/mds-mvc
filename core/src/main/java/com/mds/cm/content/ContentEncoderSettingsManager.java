package com.mds.cm.content;

import com.google.common.collect.Maps;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.cm.content.ContentEncoderSettings;

/**
 * Created by kevin on 16/07/15 for Podcast Server
 */
public interface ContentEncoderSettingsManager {

	/// <summary>
	/// Adds the media encoder settings to the current collection.
	/// </summary>
	/// <param name="mediaEncoderSettings">The media encoder settings to add to the current collection.</param>
	void addRange(Iterable<ContentEncoderSettings> mediaEncoderSettings);

	/// <summary>
	/// Adds the specified item.
	/// </summary>
	/// <param name="item">The item.</param>
	void add(ContentEncoderSettings item);

	/// <summary>
	/// Verifies the items in the collection contain valid data.
	/// </summary>
	/// <exception cref="UnsupportedContentObjectTypeException">Thrown when one of the items references
	/// a file type not recognized by the application.</exception>
	void validate() throws UnsupportedContentObjectTypeException;

	/// <summary>
	/// Generates as string representation of the items in the collection. Use this to convert the collection
	/// to a form that can be stored in the gallery settings table.
	/// Example: Ex: ".avi||.mp4||-i {SourceFilePath} {DestinationFilePath}~~.avi||.flv||-i {SourceFilePath} {DestinationFilePath}"
	/// </summary>
	/// <returns>
	/// Returns a string representation of the items in the collection.
	/// </returns>
	/// <remarks>Each triple-pipe-delimited string represents an <see cref="IContentEncoderSettings"/> in the collection.
	/// Each of these, in turn, is double-pipe-delimited to separate the properties of the instance
	/// (e.g. ".avi||.mp4||-i {SourceFilePath} {DestinationFilePath}"). The order of the items in the
	/// return value maps to the <see cref="IContentEncoderSettings.Sequence"/>.</remarks>
	String serialize();
}
