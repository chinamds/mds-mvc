package com.mds.cm.content;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.core.ContentObjectRotation;
import com.mds.core.Size;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.cm.util.CMUtils;
import com.mds.util.FileMisc;
import com.mds.util.HelperFunctions;
import com.mds.util.StringUtils;

/// <summary>
/// Provides functionality for creating and saving the thumbnail image files associated with <see cref="Image" /> gallery objects.
/// </summary>
public class ImageOptimizedCreator extends DisplayObjectCreator{
	/// <summary>
	/// Initializes a new instance of the <see cref="ImageOptimizedCreator"/> class.
	/// </summary>
	/// <param name="contentObject">The content object.</param>
	public ImageOptimizedCreator(ContentObjectBo contentObject)	{
		this.contentObject = contentObject;
	}

	/// <summary>
	/// Generate the file for this display object and save it to the file system. The routine may decide that
	/// a file does not need to be generated, usually because it already exists. However, it will always be
	/// created if the relevant flag is set on the parent <see cref="ContentObjectBo" />. (Example: If
	/// <see cref="ContentObjectBo.RegenerateThumbnailOnSave" /> = true, the thumbnail file will always be created.) No data is
	/// persisted to the data store.
	/// </summary>
	/// <exception cref="UnsupportedImageTypeException">Thrown when MDS System cannot process the image, 
	/// most likely because it is corrupt or an unsupported image type.</exception>
	public void generateAndSaveFile() throws InvalidGalleryException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException{
		// If necessary, generate and save the optimized version of the original image.
		if (!(isOptimizedImageRequired())){
			boolean rotateIsRequested = (contentObject.getRotation() != ContentObjectRotation.NotSpecified);

			if (rotateIsRequested || ((contentObject.getIsNew()) && (StringUtils.isBlank(contentObject.getOptimized().getFileName())))){
				// One of the following is true:
				// 1. The original is being rotated and there isn't a separate optimized image.
				// 2. This is a new object that doesn't need a separate optimized image.
				// In either case, set the optimized properties equal to the original properties.
				contentObject.getOptimized().setFileName(contentObject.getOriginal().getFileName());
				contentObject.getOptimized().setWidth(contentObject.getOriginal().getWidth());
				contentObject.getOptimized().setHeight(contentObject.getOriginal().getHeight());
				contentObject.getOptimized().setFileSizeKB(contentObject.getOriginal().getFileSizeKB());
			}
			return; // No optimized image required.
		}

		GallerySettings gallerySetting = CMUtils.loadGallerySetting(contentObject.getGalleryId());

		// Determine file name and path of the optimized image. If a file name has already been previously
		// calculated for this content object, re-use it. Otherwise generate a unique name.
		String newFilename = contentObject.getOptimized().getFileName();
		String newFilePath = contentObject.getOptimized().getFileNamePhysicalPath();

		if (StringUtils.isBlank(newFilename)){
			String optimizedPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(FilenameUtils.getFullPathNoEndSeparator(this.contentObject.getOriginal().getFileInfo().getPath()), gallerySetting.getFullOptimizedPath(), gallerySetting.getFullContentObjectPath());
			newFilename = generateJpegFilename(optimizedPath, gallerySetting.getOptimizedFileNamePrefix());
			newFilePath = FilenameUtils.concat(optimizedPath, newFilename);
		}

		boolean imageCreated = false;

		Size size = Size.Empty;
		if (ArrayUtils.indexOf(gallerySetting.getImageMagickFileTypes(), FileMisc.getExt(contentObject.getOriginal().getFileName()).toLowerCase()) >= 0){
			size = generateImageUsingImageMagick(newFilePath, gallerySetting.getMaxOptimizedLength(), gallerySetting.getOptimizedImageJpegQuality());

			imageCreated = !size.isEmpty();
		}

		if (!imageCreated){
			log.info("ImageOptimizedCreator - generateAndSaveFile - generateImageUsingImageMagick failed, try generateImageUsingDotNet");
			size = generateImageUsingDotNet(newFilePath, gallerySetting.getMaxOptimizedLength(), gallerySetting.getOptimizedImageJpegQuality());
		}

		if (!size.isEmpty()){
			contentObject.getOptimized().setWidth(size.Width.intValue());
			contentObject.getOptimized().setHeight(size.Height.intValue());
		}

		contentObject.getOptimized().setFileName(newFilename);
		contentObject.getOptimized().setFileNamePhysicalPath(newFilePath);

		int fileSize = (int)(contentObject.getOptimized().getFileInfo().length() / 1024);

		contentObject.getOptimized().setFileSizeKB((fileSize < 1 ? 1 : fileSize)); // Very small files should be 1, not 0.
	}

	private boolean isOptimizedImageRequired() throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		// We must create an optimized image in the following circumstances:
		// 1. The file corresponding to a previously created optimized image file does not exist.
		//    OR
		// 2. The overwrite flag is true.
		//    OR
		// 3. There is a request to rotate the image.
		//    AND
		// 4. The size of width/height dimensions of the original exceed the optimized triggers.
		//    OR
		// 5. The original image is not a JPEG.
		// In other words: image required = ((1 || 2 || 3) && (4 || 5))

		boolean optimizedImageMissing = isOptimizedImageFileMissing(); // Test 1

		boolean overwriteFlag = contentObject.getRegenerateOptimizedOnSave(); // Test 2

		boolean rotateIsRequested = (contentObject.getRotation() != ContentObjectRotation.NotSpecified); // Test 3

		boolean originalExceedsOptimizedDimensionTriggers = false;
		boolean isOriginalNonJpegImage = false;
		if (optimizedImageMissing || overwriteFlag || rotateIsRequested){
			// Only need to run test 3 and 4 if test 1 or test 2 is true.
			originalExceedsOptimizedDimensionTriggers = doesOriginalExceedOptimizedDimensionTriggers(); // Test 4

			isOriginalNonJpegImage = isOriginalNonJpegImage(); // Test 5
		}

		return ((optimizedImageMissing || overwriteFlag || rotateIsRequested) && (originalExceedsOptimizedDimensionTriggers || isOriginalNonJpegImage));
	}

	private boolean isOriginalNonJpegImage(){
		// Return true if the original image is not a JPEG.
		String[] jpegImageTypes = new String[] { ".jpg", ".jpeg" };
		String originalFileExtension = FileMisc.getExt(contentObject.getOriginal().getFileName()).toLowerCase();

		boolean isOriginalNonJpegImage = ArrayUtils.indexOf(jpegImageTypes, originalFileExtension) < 0;

		return isOriginalNonJpegImage;
	}

	private boolean doesOriginalExceedOptimizedDimensionTriggers() throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		GallerySettings gallerySetting = CMUtils.loadGallerySetting(contentObject.getGalleryId());

		// Test 1: Is the file size of the original greater than OptimizedImageTriggerSizeKB?
		boolean isOriginalFileSizeGreaterThanTriggerSize = contentObject.getOriginal().getFileSizeKB() > gallerySetting.getOptimizedImageTriggerSizeKb();

		// Test 2: Is the width or length of the original greater than the MaxOptimizedLength?
		boolean isOriginalLengthGreaterThanMaxAllowedLength = false;
		int optimizedMaxLength = gallerySetting.getMaxOptimizedLength();
		double originalWidth = 0;
		double originalHeight = 0;

		try	{
			Size size = contentObject.getOriginal().getSize();
			originalWidth = size.Width;
			originalHeight = size.Height;
		}catch (UnsupportedImageTypeException ex) { }

		if ((originalWidth > optimizedMaxLength) || (originalHeight > optimizedMaxLength)){
			isOriginalLengthGreaterThanMaxAllowedLength = true;
		}

		return (isOriginalFileSizeGreaterThanTriggerSize | isOriginalLengthGreaterThanMaxAllowedLength);
	}

	private boolean isOptimizedImageFileMissing(){
		// Does the optimized image file exist? (Maybe it was accidentally deleted or moved by the user,
		// or maybe it's a new object.)
		return !FileMisc.fileExists(contentObject.getOptimized().getFileNamePhysicalPath());
	}
}
