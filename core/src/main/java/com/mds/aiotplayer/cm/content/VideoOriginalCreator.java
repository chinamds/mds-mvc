/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.content;

import java.text.MessageFormat;

import com.mds.aiotplayer.core.ContentObjectRotation;
import com.mds.aiotplayer.core.ContentQueueItemConversionType;
import com.mds.aiotplayer.core.exception.BusinessException;
import com.mds.aiotplayer.util.FileMisc;

/// <summary>
/// Contains functionality for manipulating the original video file associated with <see cref="Video" /> gallery objects.
/// The only time a new original video must be generated is when the user rotates it. This will only
/// occur for existing objects.
/// </summary>
public class VideoOriginalCreator extends DisplayObjectCreator{
	/// <summary>
	/// Initializes a new instance of the <see cref="VideoOriginalCreator"/> class.
	/// </summary>
	/// <param name="videoObject">The video object.</param>
	public VideoOriginalCreator(Video videoObject){
		this.contentObject = videoObject;
	}
	/// <summary>
	/// Generate the file for this display object and save it to the file system. The routine may decide that
	/// a file does not need to be generated, usually because it already exists. No data is
	/// persisted to the data store.
	/// </summary>
	public void generateAndSaveFile(){
		// The only time we need to generate a new original video is when the user rotates it. This will only
		// occur for existing objects.
		if ((contentObject.isNew) || (contentObject.getRotation() == ContentObjectRotation.NotSpecified))
			return;

		// We have an existing object with a specific rotation requested. For example, if the requested rotation is
		// 0 degrees and the image is oriented 90 degrees CW, the file will be rotated 90 degrees CCW.
		String filePath = contentObject.getOriginal().getFileNamePhysicalPath();

		if (!FileMisc.fileExists(filePath))
			throw new BusinessException(MessageFormat.format("Cannot rotate video because no file exists at {0}.", filePath));

		ContentConversionQueue.getInstance().add(contentObject, ContentQueueItemConversionType.RotateVideo);
		ContentConversionQueue.getInstance().process();
	}
}

