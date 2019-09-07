package com.mds.cm.util;

import java.util.ArrayList;
import java.util.List;

import com.mds.cm.content.ContentEncoderSettings;
import com.mds.cm.content.ContentEncoderSettingsCollection;
import com.mds.cm.content.MimeTypeBo;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.rest.ContentEncoderSettingsRest;
import com.mds.cm.rest.FileExtension;
import com.mds.core.MimeTypeCategory;
import com.mds.core.exception.ArgumentNullException;
import com.mds.cm.util.CMUtils;

/// <summary>
/// Contains functionality for performing web-related tasks on media encoder settings.
/// </summary>
public final class ContentEncoderSettingsUtils{
	/// <summary>
	/// Gets an array of data entities representing the specified <paramref name="mediaEncoderSettings" />.
	/// The instance can be converted to a JSON String and sent to the browser.
	/// </summary>
	/// <param name="mediaEncoderSettings">The settings to convert.</param>
	/// <returns>
	/// An array of <see cref="ContentEncoderSettingsRest" /> instances.
	/// </returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="mediaEncoderSettings"/> is null.</exception>
	public static ContentEncoderSettingsRest[] toEntities(ContentEncoderSettingsCollection mediaEncoderSettings){
		if (mediaEncoderSettings == null)
			throw new ArgumentNullException("mediaEncoderSettings");

		List<ContentEncoderSettingsRest> entities = new ArrayList<ContentEncoderSettingsRest>();

		for (ContentEncoderSettings encoderSetting : mediaEncoderSettings){
			entities.add(toContentEncoderSettingsEntity(encoderSetting));
		}

		return entities.toArray(new ContentEncoderSettingsRest[0]);
	}

	/// <summary>
	/// Converts the <paramref name="entities" /> to a <see cref="ContentEncoderSettingsCollection" /> collection. 
	/// Returns an empty collection if entities is null.
	/// </summary>
	/// <param name="entities">An array of <see cref="ContentEncoderSettingsRest" /> items.</param>
	/// <returns>Returns an instance that implements <see cref="ContentEncoderSettingsCollection" />.</returns>
	public static ContentEncoderSettingsCollection toContentEncoderSettingsCollection(ContentEncoderSettingsRest[] entities){
		if (entities == null)
			return new ContentEncoderSettingsCollection();

		ContentEncoderSettingsCollection items = new ContentEncoderSettingsCollection();

		int seq = 1;
		for (ContentEncoderSettingsRest entity : entities){
			items.add(new ContentEncoderSettings(entity.SourceFileExtension, entity.DestinationFileExtension, entity.EncoderArguments, seq));
			seq++;
		}

		return items;
	}

	/// <summary>
	/// Gets an array of file extensions that may be used in a media encoder setting. Does not include the
	/// 'All audio' or 'All video' items.
	/// </summary>
	/// <returns>An array of <see cref="FileExtension" /> items.</returns>
	public static FileExtension[] getAvailableFileExtensions() throws InvalidGalleryException{
		List<FileExtension> availFileExtensions = new ArrayList<FileExtension>();

		for (MimeTypeBo mimeType : CMUtils.loadMimeTypes())	{
			if ((mimeType.getTypeCategory() == MimeTypeCategory.Video) || (mimeType.getTypeCategory() == MimeTypeCategory.Audio)){
				FileExtension fileExtension = new FileExtension();
				fileExtension.Value = mimeType.getExtension();
				fileExtension.Text = mimeType.getExtension();
				availFileExtensions.add(fileExtension);
			}
		}

		return availFileExtensions.toArray(new FileExtension[0]);
	}

	private static ContentEncoderSettingsRest toContentEncoderSettingsEntity(ContentEncoderSettings mediaEncoderSettings){
		ContentEncoderSettingsRest contentEncoderSettingsRest  = new ContentEncoderSettingsRest();
		contentEncoderSettingsRest.SourceFileExtension = mediaEncoderSettings.getSourceFileExtension();
		contentEncoderSettingsRest.DestinationFileExtension = mediaEncoderSettings.getDestinationFileExtension();
		contentEncoderSettingsRest.EncoderArguments = mediaEncoderSettings.getEncoderArguments();
		
		return contentEncoderSettingsRest;
	}
}