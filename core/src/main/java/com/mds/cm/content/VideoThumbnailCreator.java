package com.mds.cm.content;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

import com.mds.common.Constants;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.cm.metadata.ContentObjectMetadataItem;
import com.mds.core.MetadataItemName;
import com.mds.core.Size;
import com.mds.sys.util.AppSettings;
import com.mds.cm.util.CMUtils;
import com.mds.util.DateUtils;
import com.mds.util.FileMisc;
import com.mds.util.HelperFunctions;
import com.mds.util.StringUtils;

/// <summary>
/// Provides functionality for creating and saving the thumbnail image files associated with <see cref="Video" /> gallery objects.
/// </summary>
public class VideoThumbnailCreator extends DisplayObjectCreator{
	/// <summary>
	/// Initializes a new instance of the <see cref="VideoThumbnailCreator"/> class.
	/// </summary>
	/// <param name="contentObject">The gallery object.</param>
	public VideoThumbnailCreator(ContentObjectBo contentObject)	{
		this.contentObject = contentObject;
	}

	/// <summary>
	/// Generate the thumbnail image for this display object and save it to the file system. The routine may decide that
	/// a file does not need to be generated, usually because it already exists. However, it will always be
	/// created if the relevant flag is set on the parent <see cref="ContentObjectBo" />. (Example: If
	/// <see cref="ContentObjectBo.RegenerateThumbnailOnSave" /> = true, the thumbnail file will always be created.) No data is
	/// persisted to the data store.
	/// </summary>
	public void generateAndSaveFile() throws IOException, UnsupportedImageTypeException, InvalidGalleryException{
		// If necessary, generate and save the thumbnail version of the video.
		if (!(isThumbnailImageRequired())){
			return; // No thumbnail image required.
		}

		GallerySettings gallerySetting = CMUtils.loadGallerySetting(contentObject.getGalleryId());

		// Generate a temporary filename to store the thumbnail created by FFmpeg.
		String tmpVideoThumbnailPath = FilenameUtils.concat(AppSettings.getInstance().getTempUploadDirectory(), StringUtils.join(new Object[] {UUID.randomUUID(), ".jpg"}));

		// Request that FFmpeg create the thumbnail. If successful, the file will be created.
		FFmpegWrapper.generateThumbnail(contentObject.getOriginal().getFileNamePhysicalPath(), tmpVideoThumbnailPath, gallerySetting.getVideoThumbnailPosition(), contentObject.getGalleryId());

		// Verify image was created from video, trying again using a different video position setting if necessary.
		validateVideoThumbnail(tmpVideoThumbnailPath, gallerySetting.getVideoThumbnailPosition());

		// Determine file name and path of the thumbnail image. If a file name has already been previously
		// calculated for this content object, re-use it. Otherwise generate a unique name.
		String newFilename = contentObject.getThumbnail().getFileName();
		String newFilePath = contentObject.getThumbnail().getFileNamePhysicalPath();

		if (StringUtils.isBlank(newFilename)){
			String thumbnailPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(FilenameUtils.getFullPathNoEndSeparator(this.contentObject.getOriginal().getFileInfo().getPath()), gallerySetting.getFullThumbnailPath(), gallerySetting.getFullContentObjectPath());
			newFilename = generateJpegFilename(thumbnailPath, gallerySetting.getThumbnailFileNamePrefix());
			newFilePath = FilenameUtils.concat(thumbnailPath, newFilename);
		}

		if (FileMisc.fileExists(tmpVideoThumbnailPath)){
			// FFmpeg successfully created a thumbnail image the same size as the video. Now resize it to the width and height we need.
			BufferedImage originalBitmap = ImageIO.read(new File(tmpVideoThumbnailPath));
			
			Size newSize = calculateWidthAndHeight(new Size(originalBitmap.getWidth(), originalBitmap.getHeight()), gallerySetting.getMaxThumbnailLength(), false);

			// Get JPEG quality value (0 - 100). This is ignored if imgFormat = GIF.
			int jpegQuality = gallerySetting.getThumbnailImageJpegQuality();

			// Generate the new image and save to disk.
			Size size = ImageHelper.saveImageFile(originalBitmap, newFilePath, "JPG", newSize.Width, newSize.Height, jpegQuality);

			Size rotatedSize = executeAutoRotation(newFilePath, jpegQuality);
			if (!rotatedSize.isEmpty()){
				size = rotatedSize;
			}

			contentObject.getThumbnail().setWidth(size.Width.intValue());
			contentObject.getThumbnail().setHeight(size.Height.intValue());

			try
			{
				// Now delete the thumbnail image created by FFmpeg, but no worries if an error happens. The file is in the temp directory
				// which is cleaned out each time the app starts anyway.
				FileMisc.deleteExistsFile(tmpVideoThumbnailPath);
			}catch (SecurityException ex){
				//EventLogController.RecordError(ex, AppSetting.Instance, contentObject.getGalleryId(), CMUtils.LoadGallerySettings());
			}
		}else{
			// FFmpeg didn't run or no thumbnail image was created by FFmpeg. Build a generic video thumbnail.
			BufferedImage originalBitmap = ImageIO.read(VideoThumbnailCreator.class.getResourceAsStream("/images/GenericThumbnailImage_Video.jpg"));
			Size newSize = calculateWidthAndHeight(new Size(originalBitmap.getWidth(), originalBitmap.getHeight()), gallerySetting.getMaxThumbnailLength(), true);

			// Get JPEG quality value (0 - 100).
			int jpegQuality = gallerySetting.getThumbnailImageJpegQuality();

			// Generate the new image and save to disk.
			Size size = ImageHelper.saveImageFile(originalBitmap, newFilePath, "JPG", newSize.Width, newSize.Height, jpegQuality);

			contentObject.getThumbnail().setWidth(size.Width.intValue());
			contentObject.getThumbnail().setHeight(size.Height.intValue());
		}

		contentObject.getThumbnail().setFileName(newFilename);
		contentObject.getThumbnail().setFileNamePhysicalPath(newFilePath);

		int fileSize = (int)(contentObject.getThumbnail().getFileInfo().length() / 1024);

		contentObject.getThumbnail().setFileSizeKB((fileSize < 1 ? 1 : fileSize)); // Very small files should be 1, not 0.
	}

	/// <summary>
	/// Verify the image was created from the video. If not, it might be because the video is shorter than the position
	/// where we tried to grab the image. If this is the case, try again, except grab an image from the beginning of the video.
	/// </summary>
	/// <param name="tmpVideoThumbnailPath">The video thumbnail path.</param>
	/// <param name="videoThumbnailPosition">The position, in seconds, in the video where the thumbnail is generated from a frame.</param>
	private void validateVideoThumbnail(String tmpVideoThumbnailPath, int videoThumbnailPosition){
		if (!FileMisc.fileExists(tmpVideoThumbnailPath)){
			ContentObjectMetadataItem metadataItem;
			if ((metadataItem = contentObject.getMetadataItems().tryGetMetadataItem(MetadataItemName.Duration))!= null)	{
				//Duration duration = DateUtils.parseDuration(metadataItem.getValue());
				long duration = Long.MIN_VALUE; 
				try {
					duration = DateUtils.getSecondsFromFormattedDuration(metadataItem.getValue());
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Duration duration = Duration.ofMillis(DateUtils.parseDuration(metadataItem.getValue()));
				if (duration !=  Long.MIN_VALUE){
					if (duration < videoThumbnailPosition * 1000){
						// Video is shorter than the number of seconds where we are suppossed to grab the thumbnail.
						// Try again, except use 1 second instead of the gallery setting.
						final int videoThumbnailPositionFallback = 1;
						FFmpegWrapper.generateThumbnail(contentObject.getOriginal().getFileNamePhysicalPath(), tmpVideoThumbnailPath, videoThumbnailPositionFallback, contentObject.getGalleryId());
					}
				}
			}
		}
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