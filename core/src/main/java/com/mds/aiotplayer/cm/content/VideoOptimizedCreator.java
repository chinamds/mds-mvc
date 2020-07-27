package com.mds.aiotplayer.cm.content;

import org.apache.commons.lang.StringUtils;

import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.core.ContentQueueItemConversionType;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.util.FileMisc;

/// <summary>
/// Provides functionality for creating and saving a web-friendly version of a <see cref="Video" /> gallery object.
/// </summary>
public class VideoOptimizedCreator extends DisplayObjectCreator{
	/// <summary>
	/// Initializes a new instance of the <see cref="VideoOptimizedCreator"/> class.
	/// </summary>
	/// <param name="contentObject">The content object.</param>
	public VideoOptimizedCreator(ContentObjectBo contentObject)	{
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
		if (!(isOptimizedVideoRequired())){
			return;
		}

		// Add to queue if an encoder setting exists for this file type.
		if (FFmpegWrapper.isAvailable() && ContentConversionQueue.getInstance().hasEncoderSetting(contentObject)){
			ContentConversionQueue.getInstance().add(contentObject, ContentQueueItemConversionType.CreateOptimized);
			ContentConversionQueue.getInstance().process();
		}
	}

	private boolean isOptimizedVideoRequired(){
		if (contentObject.isNew || isInQueue())
			return false;

		boolean optFileIsMissing = isOptimizedFileMissing();
		boolean overwriteFlag = contentObject.getRegenerateOptimizedOnSave();

		return (optFileIsMissing || overwriteFlag);
	}

	private boolean isInQueue(){
		return ContentConversionQueue.getInstance().isWaitingInQueueOrProcessing(contentObject.getId(), ContentQueueItemConversionType.CreateOptimized);
	}

	private boolean isOptimizedFileMissing(){
		// We need an optimized file if the opt. and original file names are the same or if the file doesn't exist.
		boolean optFileSameAsOriginal = StringUtils.equalsIgnoreCase(contentObject.getOptimized().getFileName(), contentObject.getOriginal().getFileName());

		return (optFileSameAsOriginal || !FileMisc.fileExists(contentObject.getOptimized().getFileNamePhysicalPath()));
	}
}
