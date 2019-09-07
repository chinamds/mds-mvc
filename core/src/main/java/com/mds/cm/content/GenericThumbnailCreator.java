package com.mds.cm.content;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.mds.core.Size;
import com.mds.sys.util.AppSettings;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.util.CMUtils;
import com.mds.util.FileMisc;
import com.mds.util.HelperFunctions;
import com.mds.util.StringUtils;

/// <summary>
/// Provides functionality for creating and saving the thumbnail image files associated with <see cref="GenericContentObject" /> gallery objects.
/// </summary>
public class GenericThumbnailCreator extends DisplayObjectCreator {
	/// <summary>
	/// Initializes a new instance of the <see cref="GenericThumbnailCreator"/> class.
	/// </summary>
	/// <param name="contentObject">The gallery object.</param>
	public GenericThumbnailCreator(ContentObjectBo contentObject){
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
		if (!(isThumbnailImageRequired()))	{
			return; // No thumbnail image required.
		}

		GallerySettings gallerySetting = CMUtils.loadGallerySetting(contentObject.getGalleryId());

		// Determine file name and path of the thumbnail image. If a file name has already been previously
		// calculated for this content object, re-use it. Otherwise generate a unique name.
		String newFilename = contentObject.getThumbnail().getFileName();
		String newFilePath = contentObject.getThumbnail().getFileNamePhysicalPath();

		if (StringUtils.isBlank(newFilename)){
			String thumbnailPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(this.contentObject.getOriginal().getFileInfo().getParent(), gallerySetting.getFullThumbnailPath(), gallerySetting.getFullContentObjectPath());
			newFilename = generateJpegFilename(thumbnailPath, gallerySetting.getThumbnailFileNamePrefix());
			newFilePath = FilenameUtils.concat(thumbnailPath, newFilename);
		}

		if (ArrayUtils.indexOf(gallerySetting.getImageMagickFileTypes(), FileMisc.getExt(contentObject.getOriginal().getFileName()).toLowerCase()) >= 0){
			generateThumbnailImageUsingImageMagick(newFilePath, gallerySetting);
		}else{
			generateGenericThumbnailImage(newFilePath, gallerySetting);
		}

		contentObject.getThumbnail().setFileName(newFilename);
		contentObject.getThumbnail().setFileNamePhysicalPath(newFilePath);

		int fileSize = (int)(contentObject.getThumbnail().getFileInfo().length() / 1024);

		contentObject.getThumbnail().setFileSizeKB((fileSize < 1 ? 1 : fileSize)); // Very small files should be 1, not 0.
	}
	
	public Dimension getPreferredSize(int w, int h) {
        return new Dimension(w, h);
    }

	private void generateThumbnailImageUsingImageMagick(String newFilePath, GallerySettings gallerySetting) throws IOException, InvalidGalleryException{
		// Generate a temporary filename to store the thumbnail created by ImageMagick.
		String tmpImageThumbnailPath = FilenameUtils.concat(AppSettings.getInstance().getTempUploadDirectory(), StringUtils.join(new Object[] {UUID.randomUUID(), ".jpg"}));

		// Request that ImageMagick create the thumbnail. If successful, the file will be created. If not, it fails silently.
		ImageMagick.generateImage(contentObject.getOriginal().getFileNamePhysicalPath(), tmpImageThumbnailPath, contentObject.getGalleryId());

		if (FileMisc.fileExists(tmpImageThumbnailPath)){ 
			try{
				// ImageMagick successfully created a thumbnail image. Now resize it to the width and height we need.
				BufferedImage originalBitmap = ImageIO.read(new File(tmpImageThumbnailPath));
				{
					Size newSize = calculateWidthAndHeight(new Size(originalBitmap.getWidth(), originalBitmap.getHeight()), gallerySetting.getMaxThumbnailLength(), false);

					// Get JPEG quality value (0 - 100). This is ignored if imgFormat = GIF.
					int jpegQuality = gallerySetting.getThumbnailImageJpegQuality();

					// Generate the new image and save to disk.
					Size size = ImageHelper.saveImageFile(originalBitmap, newFilePath, "JPG", newSize.Width, newSize.Height, jpegQuality);

					contentObject.getThumbnail().setWidth(size.Width.intValue());
					contentObject.getThumbnail().setHeight(size.Height.intValue());
				}
			}catch (Exception ex){
				//ex.Data.Add("MDS Info", MessageFormat.format("This error occurred while trying to process the ImageMagick-generated file {0}. The original file is {1}. A generic thumbnail image will be created instead.", tmpImageThumbnailPath, contentObject.Original.getFileNamePhysicalPath()));
				//EventLogs.EventLogController.RecordError(ex, AppSetting.Instance, contentObject.getGalleryId(), CMUtils.LoadGallerySettings());

				// Default to a generic thumbnail image.
				generateGenericThumbnailImage(newFilePath, gallerySetting);
			}

			try	{
				// Now delete the thumbnail image created by FFmpeg, but no worries if an error happens. The file is in the temp directory
				// which is cleaned out each time the app starts anyway.
				FileMisc.deleteExistsFile(tmpImageThumbnailPath);
			}catch (SecurityException ex){
				//ex.Data.Add("MDS Info", "This error was handled and did not interfere with the user experience.");
				//EventLogs.EventLogController.RecordError(ex, AppSetting.Instance, contentObject.getGalleryId(), CMUtils.LoadGallerySettings());
			}
		}else{
			// ImageMagick didn't create an image, so default to a generic one.
			generateGenericThumbnailImage(newFilePath, gallerySetting);
		}
	}

	private void generateGenericThumbnailImage(String newFilePath, GallerySettings gallerySetting) throws IOException, InvalidGalleryException	{
		// Build a generic thumbnail.
		BufferedImage originalBitmap = getGenericThumbnailBitmap(contentObject.getMimeType());
		Size newSize = calculateWidthAndHeight(new Size(originalBitmap.getWidth(), originalBitmap.getHeight()), gallerySetting.getMaxThumbnailLength(), true);

		// Get JPEG quality value (0 - 100).
		int jpegQuality = gallerySetting.getThumbnailImageJpegQuality();

		// Generate the new image and save to disk.
		Size size = ImageHelper.saveImageFile(originalBitmap, newFilePath, "JPG", newSize.Width, newSize.Height, jpegQuality);

		contentObject.getThumbnail().setWidth(size.Width.intValue());
		contentObject.getThumbnail().setHeight(size.Height.intValue());
	}

	private static BufferedImage getGenericThumbnailBitmap(MimeTypeBo mimeType) throws IOException	{
		BufferedImage thumbnailBitmap = null;
		
		switch (mimeType.getMajorType().toUpperCase()){
			case "AUDIO": thumbnailBitmap = ImageHelper.getImageResource("/images/GenericThumbnailImage_Audio.jpg", mimeType.getClass()); break;
			case "VIDEO": thumbnailBitmap = ImageHelper.getImageResource("/images/GenericThumbnailImage_Video.jpg", mimeType.getClass()); break;
			case "IMAGE": thumbnailBitmap = ImageHelper.getImageResource("/images/GenericThumbnailImage_Image.png", mimeType.getClass()); break;
			case "APPLICATION": thumbnailBitmap = getGenericThumbnailBitmapByFileExtension(mimeType.getExtension()); break;
			default: thumbnailBitmap = ImageHelper.getImageResource("/images/GenericThumbnailImage_Unknown.jpg", mimeType.getClass()); break;
		}

		return thumbnailBitmap;
	}

	private static BufferedImage getGenericThumbnailBitmapByFileExtension(String fileExtension) throws IOException{
		BufferedImage thumbnailBitmap = null;

		switch (fileExtension){
			case ".doc":
			case ".dot":
			case ".docm":
			case ".dotm":
			case ".dotx":
			case ".docx": thumbnailBitmap = ImageHelper.getImageResource("/images/GenericThumbnailImage_Doc.jpg", GenericThumbnailCreator.class); break;
			case ".xls":
			case ".xlam":
			case ".xlsb":
			case ".xlsm":
			case ".xltm":
			case ".xltx":
			case ".xlsx": thumbnailBitmap = ImageHelper.getImageResource("/images/GenericThumbnailImage_Excel.jpg", GenericThumbnailCreator.class); break;
			case ".ppt":
			case ".pps":
			case ".pptx":
			case ".potm":
			case ".ppam":
			case ".ppsm": thumbnailBitmap = ImageHelper.getImageResource("/images/GenericThumbnailImage_PowerPoint.jpg", GenericThumbnailCreator.class); break;
			case ".pdf": thumbnailBitmap = ImageHelper.getImageResource("/images/GenericThumbnailImage_PDF.jpg", GenericThumbnailCreator.class); break;
			default: thumbnailBitmap = ImageHelper.getImageResource("/images/GenericThumbnailImage_Unknown.jpg", GenericThumbnailCreator.class); break;
		}
		return thumbnailBitmap;
	}

	private boolean isThumbnailImageRequired() throws InvalidGalleryException{
		// We must create a thumbnail image in the following circumstances:
		// 1. The file corresponding to a previously created thumbnail image file does not exist.
		//    OR
		// 2. The overwrite flag is true.

		boolean thumbnailImageMissing = isThumbnailImageFileMissing(); // Test 1

		boolean overwriteFlag = contentObject.getRegenerateThumbnailOnSave(); // Test 2

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

	/// <summary>
	/// Determine name of new file and ensure it is unique in the directory. (Example: If original = puppy.jpg,
	/// thumbnail = zThumb_puppy.jpg)
	/// </summary>
	/// <param name="thumbnailPath">The path to the directory where the thumbnail file is to be created.</param>
	/// <param name="imgFormat">The image format of the thumbnail.</param>
	/// <param name="filenamePrefix">A String to prepend to the filename. Example: "zThumb_"</param>
	/// <returns>
	/// Returns the name of the new thumbnail file name and ensure it is unique in the directory.
	/// </returns>
	private String generateNewFilename(String thumbnailPath, String imgFormat, String filenamePrefix){
		String nameWithoutExtension = FilenameUtils.getBaseName(contentObject.getOriginal().getFileInfo().getPath());
		String thumbnailFilename = MessageFormat.format("{0}{1}.{2}", filenamePrefix, nameWithoutExtension, imgFormat.toLowerCase());

		thumbnailFilename = HelperFunctions.validateFileName(thumbnailPath, thumbnailFilename);

		return thumbnailFilename;
	}
}
