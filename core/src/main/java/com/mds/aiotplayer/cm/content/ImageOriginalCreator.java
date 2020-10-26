/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.content;

import java.io.IOException;
import java.text.MessageFormat;

import org.apache.commons.lang3.tuple.Pair;

import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.metadata.ContentObjectMetadataItem;
import com.mds.aiotplayer.core.ContentObjectRotation;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.core.Size;
import com.mds.aiotplayer.core.exception.BusinessException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.util.FileMisc;

/// <summary>
/// Contains functionality for manipulating the original image files associated with <see cref="Image" /> gallery objects.
/// The only time a new original image must be generated is when the user rotates it. This will only
/// occur for existing objects.
/// </summary>
public class ImageOriginalCreator extends DisplayObjectCreator{
	/// <summary>
	/// Initializes a new instance of the <see cref="ImageOriginalCreator"/> class.
	/// </summary>
	/// <param name="imageObject">The image object.</param>
	public ImageOriginalCreator(Image imageObject){
		this.contentObject = imageObject;
	}
	/// <summary>
	/// Generate the file for this display object and save it to the file system. The routine may decide that
	/// a file does not need to be generated, usually because it already exists. No data is
	/// persisted to the data store.
	/// </summary>
	/// <exception cref="UnsupportedImageTypeException">Thrown when MDS System cannot process the image, 
	/// most likely because it is corrupt or an unsupported image type.</exception>
	public void generateAndSaveFile() throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		// The only time we need to generate a new original image is when the user rotates it. This will only
		// occur for existing objects.
		if ((contentObject.isNew) || (contentObject.getRotation() == ContentObjectRotation.NotSpecified))
			return;

		// We have an existing object with a specific rotation requested. For example, if the requested rotation is
		// 0 degrees and the image is oriented 90 degrees CW, the file will be rotated 90 degrees CCW.
		String filePath = contentObject.getOriginal().getFileNamePhysicalPath();

		if (!FileMisc.fileExists(filePath))
			throw new BusinessException(MessageFormat.format("Cannot rotate image because no file exists at {0}.", filePath));

		Pair<ContentObjectRotation, Size> rotateResult = null;
		try {
			rotateResult = rotate(filePath, getGallerySettings().getOriginalImageJpegQuality());
		} catch (UnsupportedImageTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (rotateResult.getLeft().getContentObjectRotation() > ContentObjectRotation.Rotate0.getContentObjectRotation()){
			if (!rotateResult.getRight().isEmpty()){
				contentObject.getOriginal().setWidth(rotateResult.getRight().Width.intValue());
				contentObject.getOriginal().setHeight(rotateResult.getRight().Height.intValue());
			}

			refreshImageMetadata();

			int fileSize = (int)(contentObject.getOriginal().getFileInfo().length() / 1024);
			contentObject.getOriginal().setFileSizeKB((fileSize < 1 ? 1 : fileSize)); // Very small files should be 1, not 0.
		}else{
			// Turns out the file wasn't actually rotated, but we need to remove the orientation flag to prevent the 
			// auto-rotate functionality from doing the wrong thing in the future.
			ContentObjectMetadataItem metaItem;
			if ((metaItem = contentObject.getMetadataItems().tryGetMetadataItem(MetadataItemName.Orientation)) != null)	{
				metaItem.setIsDeleted(true);
				try {
					CMUtils.saveContentObjectMetadataItem(metaItem, contentObject.getLastModifiedByUserName());
				} catch (InvalidContentObjectException | UnsupportedContentObjectTypeException | InvalidAlbumException
						| UnsupportedImageTypeException | IOException | InvalidGalleryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				contentObject.getMetadataReadWriter().deleteMetaValue(MetadataItemName.Orientation);
			}
		}
	}

	/// <summary>
	/// Re-extract several metadata values from the file. Call this function when performing an action on a file
	/// that may render existing metadata items inaccurate, such as width and height. The new values are not persisted;
	/// it is expected a subsequent function will do that.
	/// </summary>
	private void refreshImageMetadata() throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		contentObject.extractMetadata(contentObject.getMetaDefinitions().find(MetadataItemName.Width));
		contentObject.extractMetadata(contentObject.getMetaDefinitions().find(MetadataItemName.Height));
		contentObject.extractMetadata(contentObject.getMetaDefinitions().find(MetadataItemName.Dimensions));
		contentObject.extractMetadata(contentObject.getMetaDefinitions().find(MetadataItemName.HorizontalResolution));
		contentObject.extractMetadata(contentObject.getMetaDefinitions().find(MetadataItemName.VerticalResolution));
		contentObject.extractMetadata(contentObject.getMetaDefinitions().find(MetadataItemName.Orientation));
	}
}

