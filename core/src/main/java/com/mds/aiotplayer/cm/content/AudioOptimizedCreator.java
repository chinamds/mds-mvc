/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.content;

import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.core.ContentQueueItemConversionType;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.util.FileMisc;
import com.mds.aiotplayer.util.StringUtils;

/// <summary>
/// Provides functionality for creating and saving a web-friendly version of a <see cref="Audio" /> gallery object.
/// </summary>
public class AudioOptimizedCreator extends DisplayObjectCreator{
	/// <summary>
	/// Initializes a new instance of the <see cref="AudioOptimizedCreator"/> class.
	/// </summary>
	/// <param name="contentObject">The content object.</param>
	public AudioOptimizedCreator(ContentObjectBo contentObject)	{
		this.contentObject = contentObject;
	}

	/// <summary>
	/// Generate the file for this display object and save it to the file system. The routine may decide that
	/// a file does not need to be generated, usually because it already exists. However, it will always be
	/// created if the relevant flag is set on the parent <see cref="ContentObjectBo" />. (Example: If
	/// <see cref="ContentObjectBo.RegenerateThumbnailOnSave" /> = true, the thumbnail file will always be created.) No data is
	/// persisted to the data store.
	/// </summary>
	@Override
	public void generateAndSaveFile() throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		// If necessary, generate and save the optimized version of the original file.
		if (!(isOptimizedAudioRequired()))	{
			return;
		}

		// Add to queue if an encoder setting exists for this file type.
		if (FFmpegWrapper.isAvailable() && ContentConversionQueue.getInstance().hasEncoderSetting(contentObject)) {
			ContentConversionQueue.getInstance().add(contentObject, ContentQueueItemConversionType.CreateOptimized);
			ContentConversionQueue.getInstance().process();
		}
	}

	private boolean isOptimizedAudioRequired() {
		if (contentObject.getIsNew())
			return false;
		else
			return (requiresOptimizedAudio() || contentObject.regenerateOptimizedOnSave);
	}

	private boolean requiresOptimizedAudio(){
		if (ContentConversionQueue.getInstance().isWaitingInQueueOrProcessing(contentObject.getId(), ContentQueueItemConversionType.CreateOptimized)){
			// File is already in the queue, so we don't need to create one
			return false;
		}

		// We need an optimized file if the opt. and original file names are the same or if the file doesn't exist.
		boolean optFileSameAsOriginal = StringUtils.equalsIgnoreCase(contentObject.getOptimized().getFileName(), contentObject.getOriginal().getFileName());

		return (optFileSameAsOriginal || !FileMisc.fileExists(contentObject.getOptimized().getFileNamePhysicalPath()));
	}
}
