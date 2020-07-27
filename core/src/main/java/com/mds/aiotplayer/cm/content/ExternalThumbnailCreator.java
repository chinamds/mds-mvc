package com.mds.aiotplayer.cm.content;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.core.Size;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.util.FileMisc;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.StringUtils;

/// <summary>
/// Provides functionality for creating and saving the thumbnail image files associated with <see cref="ExternalContentObject" /> gallery objects.
/// </summary>
public class ExternalThumbnailCreator extends DisplayObjectCreator{
	/// <summary>
	/// Initializes a new instance of the <see cref="ExternalThumbnailCreator"/> class.
	/// </summary>
	/// <param name="contentObject">The gallery object.</param>
	public ExternalThumbnailCreator(ContentObjectBo contentObject)	{
		this.contentObject = contentObject;
	}

	/// <summary>
	/// Generate the thumbnail image for this display object and save it to the file system. The routine may decide that
	/// a file does not need to be generated, usually because it already exists. However, it will always be
	/// created if the relevant flag is set on the parent <see cref="ContentObjectBo" />. (Example: If
	/// <see cref="ContentObjectBo.RegenerateThumbnailOnSave" /> = true, the thumbnail file will always be created.) No data is
	/// persisted to the data store.
	/// </summary>
	public void generateAndSaveFile() throws IOException, InvalidGalleryException{
		// If necessary, generate and save the thumbnail version of the original image.
		if (!(isThumbnailImageRequired())){
			return; // No thumbnail image required.
		}

		GallerySettings gallerySetting = CMUtils.loadGallerySetting(contentObject.getGalleryId());
		
		// Determine file name and path of the thumbnail image. If a file name has already been previously
		// calculated for this content object, re-use it. Otherwise generate a unique name.
		String newFilename = contentObject.getThumbnail().getFileName();
		String newFilePath = contentObject.getThumbnail().getFileNamePhysicalPath();

		if (StringUtils.isBlank(newFilename)){
			String thumbnailPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(this.contentObject.getParent().getFullPhysicalPath(), gallerySetting.getFullThumbnailPath(), gallerySetting.getFullContentObjectPath());
			newFilename = HelperFunctions.validateFileName(thumbnailPath, generateNewFilename(gallerySetting.getThumbnailFileNamePrefix()));
			newFilePath = FilenameUtils.concat(thumbnailPath, newFilename);
		}

		// Get reference to the bitmap from which the thumbnail image will be generated.
		BufferedImage originalBitmap = getGenericThumbnailBitmap(contentObject.getMimeType());
		Size newSize = calculateWidthAndHeight(new Size(originalBitmap.getWidth(), originalBitmap.getHeight()), gallerySetting.getMaxThumbnailLength(), true);

		// Get JPEG quality value (0 - 100). This is ignored if imgFormat = GIF.
		int jpegQuality = gallerySetting.getThumbnailImageJpegQuality();

		// Generate the new image and save to disk.
		Size size = ImageHelper.saveImageFile(originalBitmap, newFilePath, "JPG", newSize.Width, newSize.Height, jpegQuality);

		contentObject.getThumbnail().setWidth(size.Width.intValue());
		contentObject.getThumbnail().setHeight(size.Height.intValue());

		contentObject.getThumbnail().setFileName(newFilename);
		contentObject.getThumbnail().setFileNamePhysicalPath(newFilePath);

		int fileSize = (int)(contentObject.getThumbnail().getFileInfo().length() / 1024);

		contentObject.getThumbnail().setFileSizeKB((fileSize < 1 ? 1 : fileSize)); // Very small files should be 1, not 0.
	}

	private static BufferedImage getGenericThumbnailBitmap(MimeTypeBo mimeType) throws IOException{
		BufferedImage thumbnailBitmap;

		switch (mimeType.getMajorType().toUpperCase())
		{
			case "AUDIO": thumbnailBitmap = ImageIO.read(FileMisc.getClassResFile("/images/GenericThumbnailImage_Audio.jpg", mimeType.getClass())); break;
			case "VIDEO": thumbnailBitmap = ImageIO.read(FileMisc.getClassResFile("/images/GenericThumbnailImage_Video.jpg", mimeType.getClass())); break;
			case "IMAGE": thumbnailBitmap = ImageIO.read(FileMisc.getClassResFile("/images/GenericThumbnailImage_Image.jpg", mimeType.getClass())); break;
			default: thumbnailBitmap = ImageIO.read(FileMisc.getClassResFile("/images/GenericThumbnailImage_Unknown.jpg", mimeType.getClass())); break;
		}

		return thumbnailBitmap;
	}

	private boolean isThumbnailImageRequired() throws InvalidGalleryException	{
		// We must create a thumbnail image in the following circumstances:
		// 1. The file corresponding to a previously created thumbnail image file does not exist.
		//    OR
		// 2. The overwrite flag is true.

		boolean thumbnailImageMissing = isThumbnailImageFileMissing(); // Test 1

		boolean overwriteFlag = contentObject.regenerateThumbnailOnSave; // Test 2

		return (thumbnailImageMissing || overwriteFlag);
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

	private static String generateNewFilename(String filenamePrefix){
		return MessageFormat.format("{0}{1}.jpg", filenamePrefix, Constants.ExternalContentObjectFilename);
	}
}