/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.content;

import java.util.Arrays;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.core.ContentObjectRotation;
import com.mds.aiotplayer.core.Size;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.util.FileMisc;
import com.mds.aiotplayer.util.HelperFunctions;

/// <summary>
/// Provides functionality for creating and saving the thumbnail image files associated with <see cref="Image" /> gallery objects.
/// </summary>
public class ImageThumbnailCreator extends DisplayObjectCreator{
	/// <summary>
	/// Initializes a new instance of the <see cref="ImageThumbnailCreator"/> class.
	/// </summary>
	/// <param name="imageObject">The image object.</param>
	public ImageThumbnailCreator(Image imageObject)	{
		this.contentObject = imageObject;
	}

	/// <summary>
	/// Generate the file for this display object and save it to the file system. The routine may decide that
	/// a file does not need to be generated, usually because it already exists. However, it will always be
	/// created if the relevant flag is set on the parent ContentObjectBo. (Example: If
	/// <see cref="ContentObjectBo.RegenerateThumbnailOnSave" /> = true, the thumbnail file will 
	/// always be created.) No data is persisted to the data store.
	/// </summary>
	/// <exception cref="UnsupportedImageTypeException">Thrown when MDS System cannot process the image, 
	/// most likely because it is corrupt or an unsupported image type.</exception>
	public void generateAndSaveFile() throws InvalidGalleryException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException{
		// If necessary, generate and save the thumbnail version of the original image.
		if (!(isThumbnailImageRequired())){
			return; // No thumbnail image required.
		}

		GallerySettings gallerySetting = this.getGallerySettings();

		// Determine file name and path of the thumbnail image. If a file name has already been previously
		// calculated for this content object, re-use it. Otherwise generate a unique name.
		String newFilename = contentObject.getThumbnail().getFileName();
		String newFilePath = contentObject.getThumbnail().getFileNamePhysicalPath();

		if (StringUtils.isBlank(newFilename)){
			//FilenameUtils.getFullPathNoEndSeparator(this.contentObject.getOriginal().getFileInfo().getPath())
			String thumbnailPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(this.contentObject.getOriginal().getFileInfo().getParent()
					, gallerySetting.getFullThumbnailPath(), gallerySetting.getFullContentObjectPath());
			newFilename = generateJpegFilename(thumbnailPath, gallerySetting.getThumbnailFileNamePrefix());
			newFilePath = FilenameUtils.concat(thumbnailPath, newFilename);
		}

		boolean imageCreated = false;

		Size size = Size.Empty;
		if (ArrayUtils.indexOf(gallerySetting.getImageMagickFileTypes(), FileMisc.getExt(contentObject.getOriginal().getFileName()).toLowerCase()) >= 0)	{
			size = generateImageUsingImageMagick(newFilePath, gallerySetting.getMaxThumbnailLength(), gallerySetting.getThumbnailImageJpegQuality());

			imageCreated = !size.isEmpty();
		}

		if (!imageCreated){
			size = generateImageUsingDotNet(newFilePath, gallerySetting.getMaxThumbnailLength(), gallerySetting.getThumbnailImageJpegQuality());
		}

		if (!size.isEmpty()){
			contentObject.getThumbnail().setWidth(size.Width.intValue());
			contentObject.getThumbnail().setHeight(size.Height.intValue());
		}

		contentObject.getThumbnail().setFileName(newFilename);
		contentObject.getThumbnail().setFileNamePhysicalPath(newFilePath);

		int fileSize = (int)(contentObject.getThumbnail().getFileInfo().length() / 1024);

		contentObject.getThumbnail().setFileSizeKB((fileSize < 1 ? 1 : fileSize)); // Very small files should be 1, not 0.
	}

	private boolean isThumbnailImageRequired() throws InvalidGalleryException{
		// We must create a thumbnail image in the following circumstances:
		// 1. The file corresponding to a previously created thumbnail image file does not exist.
		//    OR
		// 2. The overwrite flag is true.
		//    OR
		// 3. There is a request to rotate the image.

		boolean thumbnailImageMissing = isThumbnailImageFileMissing(); // Test 1

		boolean overwriteFlag = contentObject.regenerateThumbnailOnSave; // Test 2

		boolean rotateIsRequested = (contentObject.rotation != ContentObjectRotation.NotSpecified);

		return (thumbnailImageMissing || overwriteFlag || rotateIsRequested);
	}

	private boolean isThumbnailImageFileMissing() throws InvalidGalleryException{
		// Does the thumbnail image file exist? (Maybe it was accidentally deleted or moved by the user,
		// or maybe it's a new object.)
		boolean thumbnailImageExists = false;
		if (FileMisc.fileExists(contentObject.getThumbnail().getFileNamePhysicalPath())){
			// Thumbnail image file exists.
			thumbnailImageExists = true;
		}

		boolean thumbnailImageIsMissing = !thumbnailImageExists;

		return thumbnailImageIsMissing;
	}
}
