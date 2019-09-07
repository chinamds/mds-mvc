package com.mds.cm.content;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import com.mds.cm.exception.GallerySecurityException;
import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidContentObjectException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.cm.util.CMUtils;
import com.mds.common.utils.Reflections;
import com.mds.core.ActionResult;
import com.mds.core.ActionResultStatus;
import com.mds.core.ApprovalStatus;
import com.mds.core.ContentObjectType;
import com.mds.core.DisplayObjectType;
import com.mds.core.SecurityActions;
import com.mds.core.exception.ArgumentException;
import com.mds.core.exception.ArgumentNullException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.sys.util.AppSettings;
import com.mds.sys.util.MDSRoleCollection;
import com.mds.sys.util.SecurityGuard;
import com.mds.util.FileMisc;
import com.mds.util.HelperFunctions;
import com.mds.util.StringUtils;

/// <summary>
/// Contains methods for creating and extracting ZIP archives.
/// </summary>
public class ZipUtility{
	//#region Private Fields

	private final List<ActionResult> fileExtractionResults = new ArrayList<ActionResult>();
	private ZipInputStream zipStream;
	//private static final Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
	private boolean hasBeenDisposed; // Used by Dispose() methods
	private Hashtable<String, String> albumAndDirectoryNamesLookupTable;
	private String userName;
	private MDSRoleCollection roles;
	private boolean isAuthenticated;
	private boolean discardOriginalImage;

	// 1 - 9, with 9 being the highest compression. We use the lowest compression because most gallery objects are already
	// in compressed formats (jpg, wmv, wma, mp3, etc.). Presumably the lower compression results in better performance, 
	// although I can't find any documentation to support this. In my own testing there was very little difference in the
	// resulting file size whether I used 1 or 9 (I did not attempt to measure performance.)
	private final static int ZIP_COMPRESSION_LEVEL = 1;

	//#endregion

	//#region Constructors

	/// <summary>
	/// Create a <see cref="ZipUtility" /> instance with the specified parameters.
	/// </summary>
	/// <param name="userName">The user name of the logged on user. May be null or empty, although some functions, 
	/// such as <see cref="ExtractZipFile"/>, require a valid user and will throw an exception if not present.</param>
	/// <param name="roles">The MDS System roles the logged on user belongs to.</param>
	public ZipUtility(String userName, MDSRoleCollection roles){
		this.userName = userName;
		if (this.userName ==  null)
			this.userName = StringUtils.EMPTY;
		this.roles = roles;
		this.isAuthenticated = !StringUtils.isBlank(this.userName);
	}

	//#endregion

	//#region Public Methods

	/// <summary>
	/// Analyze the specified ZIP file for embedded files and directories. Create albums and content objects from the
	/// files. Skip any files whose type is not enabled within MDS System. Return a list of skipped files
	/// and the reason why they were skipped.
	/// </summary>
	/// <param name="fileStream">A stream representing a ZIP file containing directories and files to be extracted
	/// to the MDS System library.</param>
	/// <param name="parentAlbum">The album that should contain the top-level directories and files found in the ZIP
	/// file.</param>
	/// <param name="discardOriginalImage">Indicates whether to delete the original image file after the thumbnail/
	/// original images have been created. Ignored for non-image files.</param>
	/// <returns>
	/// Returns a <see cref="System.Collections.Generic.List{T}"/> where the key is the name
	/// of the skipped file and the value is the reason for the file being skipped.
	/// </returns>
	public List<ActionResult> extractZipFile(File fileStream, AlbumBo parentAlbum, boolean discardOriginalImage) throws ZipException, IOException, InvalidContentObjectException, InvalidAlbumException, UnsupportedImageTypeException, UnsupportedContentObjectTypeException, GallerySecurityException, InvalidGalleryException{
		if (StringUtils.isBlank(this.userName))
			throw new UnsupportedOperationException("A username was not specified in the ZipUtility constructor. Content objects extracted from a ZIP archive must be associated with a logged on user.");

		this.albumAndDirectoryNamesLookupTable = new Hashtable<String, String>(10);

		try
		{
			FileInputStream fis = new FileInputStream(fileStream);
			//this.zipStream = new ZipFile(fileStream);
			this.zipStream = new ZipInputStream(fis);
			this.discardOriginalImage = discardOriginalImage;
			ZipEntry zipContentFile;
		    while ((zipContentFile = zipStream.getNextEntry()) != null) {
		    	//zipContentFile = zipStream.entries().nextElement();
				AlbumBo album = verifyAlbumExistsAndReturnReference(zipContentFile, parentAlbum);

				if (FileMisc.getExt(zipContentFile.getName()).equalsIgnoreCase(".zip" )){
					// We have a ZIP file embedded within the parent zip file. Recursively extract the contents of this file.
					extractEmbeddedZipFile(zipContentFile, parentAlbum);
				}else{
					addContentObjectToGallery(zipContentFile, album);
				}
			}
		}
		finally
		{
			this.zipStream.close();
			this.zipStream = null;
		}

		return this.fileExtractionResults;
	}

	/// <summary>
	/// Extracts the next file from ZIP archive specified in <paramref name="fileStream" /> and save to the directory
	/// specified in <paramref name="destPath" />. The name may be changed slightly to ensure uniqueness in the directory.
	/// The full path to the extracted file is returned. If no file is found in the ZIP archive, an emptry String is returned.
	/// </summary>
	/// <param name="fileStream">A stream representing a ZIP file containing directories and files to be extracted
	/// to the MDS System library.</param>
	/// <param name="destPath">The full path to the directory where the extracted file is to be saved.</param>
	/// <returns>Returns the full path to the extracted file.</returns>
	public String extractNextFileFromZip(File fileStream, String destPath) throws ZipException, IOException{
		//this.zipStream = new ZipFile(fileStream);
		this.zipStream = new ZipInputStream(new FileInputStream(fileStream));

		ZipEntry zipContentFile;
		if ((zipContentFile = this.zipStream.getNextEntry()) != null){
			String uniqueFilename = HelperFunctions.validateFileName(destPath, zipContentFile.getName());
			String uniqueFilepath = FilenameUtils.concat(destPath, uniqueFilename);

			extractFileFromZipStream(uniqueFilepath);

			return uniqueFilepath;
		}

		return StringUtils.EMPTY;
	}

	/// <summary>
	/// Creates a ZIP archive, returned as a <see cref="ZipOutputStream"/>, containing the specified <paramref name="albumIds">albums
	/// </paramref> and <paramref name="contentObjectIds">content objects</paramref>. Only content objects associated with a 
	/// physical file are included (in other words, external content objects are excluded). The archive is created in memory
	/// and is not stored on disk.
	/// </summary>
	/// <param name="parentAlbumId">The ID of the album containing the <paramref name="albumIds"/> and <paramref name="contentObjectIds"/>.
	/// When <paramref name="albumIds"/> or <paramref name="contentObjectIds"/> belong to more than one album, such as when a user is 
	/// downloading multiple albums contained within a virtual album, specify <see cref="Int32.MinValue"/>.</param>
	/// <param name="albumIds">The ID's of the albums to add to the ZIP archive. It's child albums and content objects are recursively 
	/// added. Each album must exist within the parent album, but does not have to be an immediate child (it can be a grandchild, etc).</param>
	/// <param name="contentObjectIds">The ID's of the content objects to add to the archive. Each content object must exist within the parent album,
	/// but does not have to be an immediate child (it can be a grandchild, etc).</param>
	/// <param name="imageSize">Size of the image to add to the ZIP archive. This parameter applies only to <see cref="Image"/> 
	/// content objects.</param>
	/// <returns>Returns a <see cref="MemoryStream"/> of a ZIP archive that contains the specified albums and content objects.</returns>
	public ByteArrayOutputStream createZipStream(long parentAlbumId, List<Long> albumIds, List<Long> contentObjectIds, DisplayObjectType imageSize, HttpServletRequest request) throws IOException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, InvalidGalleryException	{
		String currentItemBasePath;
		String basePath = null;
		boolean applyWatermark = true; // Will be overwritten later
		try{
			// Get the path to the parent album. This will fail when parentAlbumId does not refer to a valid album.
			AlbumBo album = CMUtils.loadAlbumInstance(parentAlbumId, false);

			basePath = StringUtils.join(album.getFullPhysicalPathOnDisk(), File.separator);

			applyWatermark = this.determineIfWatermarkIsToBeApplied(album);
		}catch (InvalidAlbumException ae) { /* Ignore for now; we'll check basePath later */ }

		//MemoryStream ms = new MemoryStream(); 
		ByteArrayOutputStream ms = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(ms);

		zos.setLevel(ZIP_COMPRESSION_LEVEL);

		if (albumIds != null){
			for (long albumId : albumIds){
				AlbumBo album;
				try{
					album = CMUtils.loadAlbumInstance(albumId, true);
				}catch (InvalidAlbumException ex){
					//EventLogUtils.RecordError(ex, AppSettings.getInstance());
					continue; // Gallery object may have been deleted by someone else, so just skip it.
				}

				if (StringUtils.isBlank(basePath))	{
					// The base path wasn't assigned because albumParentId does not refer to a valid album. Instead we will use the path
					// of the current album's parent.
					currentItemBasePath = StringUtils.join(album.getParent().getFullPhysicalPathOnDisk(), File.separator);

					applyWatermark = determineIfWatermarkIsToBeApplied(album);
				}else{
					currentItemBasePath = basePath;
				}

				addZipEntry(zos, album, imageSize, currentItemBasePath, applyWatermark, request);
			}
		}

		if (contentObjectIds != null){
			for (long contentObjectId : contentObjectIds) {
				ContentObjectBo contentObject;
				try{
					contentObject = CMUtils.loadContentObjectInstance(contentObjectId);
				}catch (ArgumentException ex){
					//EventLogs.EventLogUtils.RecordError(ex, AppSetting.Instance);
					continue; // Gallery object may have been deleted by someone else, so just skip it.
				}catch (InvalidContentObjectException ex){
					//EventLogs.EventLogUtils.RecordError(ex, AppSetting.Instance);
					continue; // Gallery object may have been deleted by someone else, so just skip it.
				}

				if (StringUtils.isBlank(basePath)){
					// The base path wasn't assigned because albumParentId does not refer to a valid album. Instead we will use the path
					// of the current content object's album.
					currentItemBasePath = StringUtils.join(contentObject.getParent().getFullPhysicalPathOnDisk(), File.separator);

					applyWatermark = determineIfWatermarkIsToBeApplied((AlbumBo)contentObject.getParent());
				}else{
					currentItemBasePath = basePath;
				}

				addFileZipEntry(zos, contentObject, imageSize, currentItemBasePath, applyWatermark, request);
			}
		}

		zos.finish();

		return ms;
	}

	/// <summary>
	/// Creates a ZIP archive, returned as a <see cref="ZipOutputStream"/>, containing the specified <paramref name="filePath"/>.
	/// The archive is created in memory and is not stored on disk. If <paramref name="filePath" /> is an image, no attempt is made to apply a
	/// watermark, even if watermarking is enabled.
	/// </summary>
	/// <param name="filePath">The full path to a file to be added to a ZIP archive.</param>
	/// <returns>Returns a <see cref="MemoryStream"/> of a ZIP archive that contains the specified file.</returns>
	public ByteArrayOutputStream createZipStream(String filePath, HttpServletRequest request) throws IOException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		//MemoryStream ms = new MemoryStream();
		ByteArrayOutputStream ms = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(ms);

		zos.setLevel(ZIP_COMPRESSION_LEVEL);

		addFileZipEntry(zos, filePath, null, new File(filePath).getParent(), request);

		zos.finish();

		return ms;
	}

	/// <summary>
	/// Creates a ZIP archive, returned as a <see cref="ZipOutputStream"/>, containing the specified <paramref name="content"/>
	/// and having the specified <paramref name="fileNameForZip" />. The archive is created in memory and is not stored on disk.
	/// </summary>
	/// <param name="content">The text to be added to the ZIP archive.</param>
	/// <param name="fileNameForZip">The name to be given to the <paramref name="content" /> within the ZIP archive.</param>
	/// <returns>Returns a <see cref="MemoryStream"/> of a ZIP archive that contains the specified <paramref name="content" />.</returns>
	public static ByteArrayOutputStream createZipStream(String content, String fileNameForZip) throws UnsupportedEncodingException, IOException{
		//MemoryStream ms = new MemoryStream();
		ByteArrayOutputStream ms = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(ms);

		zos.setLevel(ZIP_COMPRESSION_LEVEL);

		addFileZipEntry(zos, content, fileNameForZip);

		zos.finish();

		return ms;
	}

	/// <summary>
	/// Gets the full path to the content object file, returning the thumbnail, compressed, or 
	/// original file as specified in <paramref name="mediaSize"/>.
	/// If a content object does not have a physical file (for example, external content objects), then return <see cref="StringUtils.EMPTY"/>.
	/// Ex: C:\Inetpub\wwwroot\MDS\contentobjects\Summer 2005\sunsets\desert sunsets\sonorandesert.jpg
	/// </summary>
	/// <param name="contentObject">The content object for which to return a path to the media file.</param>
	/// <param name="mediaSize">Size of the media file to return.</param>
	/// <returns>Returns the full path to the content object file.</returns>
	private static String getContentFilePath(ContentObjectBo contentObject, DisplayObjectType mediaSize) throws InvalidGalleryException {
		String filePath = StringUtils.EMPTY;

		switch (mediaSize){
			case Thumbnail:
				filePath = contentObject.getThumbnail().getFileNamePhysicalPath();
				break;
			case Optimized:
				filePath = contentObject.getOptimized().getFileNamePhysicalPath();
				break;
			case Original:
				filePath = contentObject.getOriginal().getFileNamePhysicalPath();
				break;
		}

		if (StringUtils.isBlank(filePath)){
			filePath = contentObject.getOriginal().getFileNamePhysicalPath();
		}

		return filePath;
	}

	/// <summary>
	/// Gets the name of the requested media file, without the prefix (e.g. "zThumb_", "zOpt_").
	/// If a content object does not have a physical file (for example, external content objects), then return <see cref="StringUtils.EMPTY"/>.
	/// Ex: C:\Inetpub\wwwroot\MDS\contentobjects\Summer 2005\sunsets\desert sunsets\sonorandesert.jpg
	/// </summary>
	/// <param name="contentObject">The content object for which to return a path to the media file.</param>
	/// <param name="mediaSize">Size of the media file to return.</param>
	/// <returns>Returns the full path to the content object file.</returns>
	private static String getContentFileNameForZip(ContentObjectBo contentObject, DisplayObjectType mediaSize) throws InvalidGalleryException{
		String fileName = StringUtils.EMPTY;
		GallerySettings gallerySetting = CMUtils.loadGallerySetting(contentObject.getGalleryId());

		switch (mediaSize){
			case Thumbnail:
				fileName = contentObject.getThumbnail().getFileName().replace(gallerySetting.getThumbnailFileNamePrefix(), StringUtils.EMPTY);
				break;
			case Optimized:
				fileName = contentObject.getOptimized().getFileName().replace(gallerySetting.getOptimizedFileNamePrefix(), StringUtils.EMPTY);
				break;
			case Original:
				fileName = contentObject.getOriginal().getFileNamePhysicalPath();
				break;
		}

		if (StringUtils.isBlank(fileName)){
			fileName = contentObject.getOriginal().getFileName();
		}

		return fileName;
	}

	/// <summary>
	/// Adds the content objects in the <paramref name="album"/> to the ZIP archive. Only content objects associated with a 
	/// physical file are added (that is, external content objects are excluded).
	/// </summary>
	/// <param name="zos">The ZipOutputStream (ZIP archive) the content object file is to be added to.</param>
	/// <param name="album">The album to be added to the ZIP archive.</param>
	/// <param name="imageSize">Size of the image to add to the ZIP archive. This parameter applies only to <see cref="Image"/> 
	/// content objects.</param>
	/// <param name="basePath">The full path to the directory containing the highest-level media file to be added
	/// to the ZIP archive. Must include trailing slash. Ex: C:\Inetpub\wwwroot\MDS\contentobjects\Summer 2005\sunsets\</param>
	/// <param name="applyWatermark">Indicates whether to apply a watermark to images as they are added to the archive.
	/// Applies only for content objects in the <see cref="album"/> that are an <see cref="Image"/>.</param>
	private void addZipEntry(ZipOutputStream zos, AlbumBo album, DisplayObjectType imageSize, String basePath, boolean applyWatermark, HttpServletRequest request) throws IOException, InvalidGalleryException{
		List<AlbumBo> childAlbums= album.getChildContentObjects(ContentObjectType.Album, ApprovalStatus.All, !this.isAuthenticated).toAlbums();
		for (AlbumBo childAlbum : childAlbums)	{
			addZipEntry(zos, childAlbum, imageSize, basePath, applyWatermark, request);
		}

		List<ContentObjectBo> contentObjects =  album.getChildContentObjects(ContentObjectType.ContentObject, ApprovalStatus.All, !this.isAuthenticated).values();
		for (ContentObjectBo contentObject : contentObjects)	{
			addFileZipEntry(zos, contentObject, imageSize, basePath, applyWatermark, request);
		}
	}

	/// <overloads>Adds an object to the ZIP archive.</overloads>
	/// <summary>
	/// Adds the file associated with the <paramref name="contentObject"/> to the ZIP archive.
	/// </summary>
	/// <param name="zos">The ZipOutputStream (ZIP archive) the content object file is to be added to.</param>
	/// <param name="contentObject">The content object to be added to the ZIP archive.</param>
	/// <param name="mediaSize">Size of the media file to add to the ZIP archive.</param>
	/// <param name="basePath">The full path to the directory containing the highest-level media file to be added
	/// to the ZIP archive. Must include trailing slash. Ex: C:\Inetpub\wwwroot\MDS\contentobjects\Summer 2005\sunsets\</param>
	/// <param name="applyWatermark">Indicates whether to apply a watermark to images as they are added to the archive.
	/// Applies only when <paramref name="contentObject"/> is an <see cref="Image"/>.</param>
	private static void addFileZipEntry(ZipOutputStream zos, ContentObjectBo contentObject, DisplayObjectType mediaSize, String basePath, boolean applyWatermark, HttpServletRequest request) throws IOException, InvalidGalleryException{
		// Get the path to the file we'll be adding to the zip file.
		String filePath = getContentFilePath(contentObject, mediaSize);

		// Get the name we want to use for the file we are adding to the zip file.
		String fileNameForZip = getContentFileNameForZip(contentObject, mediaSize);

		if ((!StringUtils.isBlank(filePath)) && (!StringUtils.isBlank(fileNameForZip))){
			addFileZipEntry(zos, filePath, fileNameForZip, basePath, (contentObject instanceof Image), applyWatermark, contentObject.getGalleryId(), request);
		}
	}

	/// <summary>
	/// Adds the file specified in <paramref name="filePath"/> to the ZIP archive. If <paramref name="fileNameForZip"/>
	/// is specified, use that filename as the name of the file in the ZIP archive.
	/// </summary>
	/// <param name="zos">The ZipOutputStream (ZIP archive) the content object file is to be added to.</param>
	/// <param name="filePath">The full path to the content object file to be added to the ZIP archive.
	/// Ex: C:\Inetpub\wwwroot\MDS\contentobjects\Summer 2005\sunsets\desert sunsets\zOpt_sonorandesert.jpg</param>
	/// <param name="fileNameForZip">The full path to the file whose name is to be used to name the file specified
	/// by <paramref name="filePath"/> in the ZIP archive. If null or empty, the actual filename is used. This path
	/// does not have to refer to an existing file on disk, but it must begin with <paramref name="basePath"/>.
	/// Ex: C:\Inetpub\wwwroot\MDS\contentobjects\Summer 2005\sunsets\desert sunsets\sonorandesert.jpg</param>
	/// <param name="basePath">The full path to the directory containing the highest-level media file to be added
	/// to the ZIP archive. Must include trailing slash. Ex: C:\Inetpub\wwwroot\MDS\contentobjects\Summer 2005\sunsets\</param>
	private static void addFileZipEntry(ZipOutputStream zos, String filePath, String fileNameForZip, String basePath, HttpServletRequest request) throws IOException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		addFileZipEntry(zos, filePath, fileNameForZip, basePath, false, false, Long.MIN_VALUE, request);
	}

	/// <summary>
	/// Adds the file specified in <paramref name="filePath"/> to the ZIP archive. If <paramref name="fileNameForZip"/>
	/// is specified, use that filename as the name of the file in the ZIP archive.
	/// </summary>
	/// <param name="zos">The ZipOutputStream (ZIP archive) the content object file is to be added to.</param>
	/// <param name="filePath">The full path to the content object file to be added to the ZIP archive.
	/// Ex: C:\Inetpub\wwwroot\MDS\contentobjects\Summer 2005\sunsets\desert sunsets\zOpt_sonorandesert.jpg</param>
	/// <param name="fileNameForZip">The full path to the file whose name is to be used to name the file specified
	/// by <paramref name="filePath"/> in the ZIP archive. If null or empty, the actual filename is used. This path
	/// does not have to refer to an existing file on disk, but it must begin with <paramref name="basePath"/>.
	/// Ex: C:\Inetpub\wwwroot\MDS\contentobjects\Summer 2005\sunsets\desert sunsets\sonorandesert.jpg</param>
	/// <param name="basePath">The full path to the directory containing the highest-level media file to be added
	/// to the ZIP archive. Must include trailing slash. Ex: C:\Inetpub\wwwroot\MDS\contentobjects\Summer 2005\sunsets\</param>
	/// <param name="isImage">Indicates whether the file specified in <paramref name="filePath"/> is an image. If it
	/// is, and <paramref name="applyWatermark"/> is <c>true</c>, a watermark is applied to the image as it is inserted
	/// into the archive.</param>
	/// <param name="applyWatermark">Indicates whether to apply a watermark to images as they are added to the archive.
	/// This parameter is ignored when <paramref name="isImage"/> is <c>false</c>. When this parameter is <c>true</c>, the
	/// <paramref name="galleryId" /> must be specified.</param>
	/// <param name="galleryId">The ID for the gallery associated with the <paramref name="filePath" />. Since each gallery can
	/// have its own watermark, this value is used to ensure the correct watermark is used. This parameter is ignored when
	/// <paramref name="isImage" /> or <paramref name="applyWatermark" /> is <c>false</c>.</param>
	/// <exception cref="ArgumentException">Thrown when <paramref name="isImage" /> is <c>true</c>, <paramref name="applyWatermark" /> 
	/// is <c>true</c> and the <paramref name="galleryId" /> is <see cref="Int32.MinValue" />.</exception>
	private static void addFileZipEntry(ZipOutputStream zos, String filePath, String fileNameForZip, String basePath, boolean isImage, boolean applyWatermark, long galleryId, HttpServletRequest request) throws IOException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		if (isImage && applyWatermark && (galleryId == Long.MIN_VALUE))	{
			throw new ArgumentException("You must specify a gallery ID when the isImage and applyWatermark parameters are set to true.");
		}

		int bufferSize = AppSettings.getInstance().getContentObjectDownloadBufferSize();
		byte[] buffer = new byte[bufferSize];

		//#region Determine ZIP entry name

		// Get name of the file as it will be stored in the ZIP archive. This is the fragment of the full file path
		// after the base path. Ex: If basePath="C:\Inetpub\wwwroot\MDS\contentobjects\Summer 2005\sunsets\"
		// and filePath="C:\Inetpub\wwwroot\MDS\contentobjects\Summer 2005\sunsets\desert sunsets\zOpt_sonorandesert.jpg",
		// then zipEntryName="desert sunsets\zOpt_sonorandesert.jpg". The ZIP algorithm will automatically sense the 
		// embedded directory ("desert sunsets") and create it.
		String zipEntryName;
		if (StringUtils.isBlank(fileNameForZip)){
			zipEntryName = filePath.replace(basePath, StringUtils.EMPTY);
		}else{
			zipEntryName = fileNameForZip.replace(basePath, StringUtils.EMPTY);
		}

		//#endregion

		ByteArrayOutputStream stream = createStream(filePath, isImage, applyWatermark, galleryId, request);
		ZipEntry entry = new ZipEntry(zipEntryName);
		entry.setSize(stream.size());
		zos.putNextEntry(entry);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(stream.toByteArray());

		int byteCount;
		while ((byteCount = bais.read(buffer, 0, buffer.length)) > 0){
			zos.write(buffer, 0, byteCount);
		}
	}

	/// <summary>
	/// Adds the specified <paramref name="content" /> to the ZIP archive, giving it the specified <paramref name="fileNameForZip" />.
	/// </summary>
	/// <param name="zos">The ZipOutputStream (ZIP archive) the <paramref name="content" /> is to be added to.</param>
	/// <param name="content">The text to be added to the ZIP archive.</param>
	/// <param name="fileNameForZip">The name to be given to the <paramref name="content" /> within the ZIP archive.</param>
	private static void addFileZipEntry(ZipOutputStream zos, String content, String fileNameForZip) throws UnsupportedEncodingException, IOException{
		int bufferSize = AppSettings.getInstance().getContentObjectDownloadBufferSize();
		byte[] buffer = new byte[bufferSize];

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		stream.write(content.getBytes("utf-8"));
		{
			ZipEntry entry = new ZipEntry(fileNameForZip);
			entry.setSize(stream.size());
			zos.putNextEntry(entry);
			
	        ByteArrayInputStream bais = new ByteArrayInputStream(stream.toByteArray());

			int byteCount;		
			while ((byteCount = bais.read(buffer, 0, buffer.length)) > 0)	{
				zos.write(buffer, 0, byteCount);
			}
		}
	}

	/// <summary>
	/// Creates a stream for the specified <paramref name="filePath"/>. If <paramref name="isImage"/> is <c>true</c> and
	/// <paramref name="applyWatermark"/> is <c>true</c>, then a <see cref="MemoryStream"/> is created containing a
	/// watermarked version of the file. Otherwise, a <see cref="FileStream"/> is returned.
	/// </summary>
	/// <param name="filePath">The full path to the content object file to be added to the ZIP archive.
	/// Ex: C:\Inetpub\wwwroot\MDS\contentobjects\Summer 2005\sunsets\desert sunsets\zOpt_sonorandesert.jpg</param>
	/// <param name="isImage">Indicates whether the file specified in <paramref name="filePath"/> is an image. If it
	/// is, and <paramref name="applyWatermark"/> is <c>true</c>, a watermark is applied to the image as it is inserted
	/// into the archive.</param>
	/// <param name="applyWatermark">Indicates whether to apply a watermark to images as they are added to the archive.
	/// Applies only when <paramref name="isImage"/> is <c>true</c>.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>
	/// Returns a <see cref="MemoryStream"/> or <see cref="FileStream"/> for the specified <paramref name="filePath"/>
	/// The position of the stream is zero to allow it to be read.
	/// </returns>
	/// <exception cref="ArgumentException">Thrown when <paramref name="applyWatermark" /> is <c>true</c> and the <paramref name="galleryId" />
	/// is <see cref="Int32.MinValue" />.</exception>
	private static ByteArrayOutputStream createStream(String filePath, boolean isImage, boolean applyWatermark, long galleryId, HttpServletRequest request) throws IOException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		if (isImage && applyWatermark && (galleryId == Long.MIN_VALUE))	{
			throw new ArgumentException("You must specify a gallery ID when the isImage and applyWatermark parameters are set to true.");
		}

		ByteArrayOutputStream stream = null;
		if (isImage && applyWatermark){
			// Apply watermark to file and return.
			//stream = new MemoryStream();
			stream = new ByteArrayOutputStream();

			BufferedImage watermarkedImage = ImageHelper.addWatermark(filePath, galleryId, request);
			{
				//watermarkedImage.Save(stream, "jpeg");
				ImageIO.write(watermarkedImage, "jpeg", stream);
				//stream.Position = 0;
			}
		}else{
			FileInputStream fis = new FileInputStream(new File(filePath));
			BufferedInputStream in = new BufferedInputStream(fis);
            
            stream = new ByteArrayOutputStream();
            int bytesRead;
            while (-1 != (bytesRead = in.read())) {
            	stream.write(bytesRead);
            }
            in.close();
			//stream = File.OpenRead(filePath);
		}
		
		return stream;
	}

	//#endregion

	//#region Private Methods

	/// <summary>
	/// Adds the <paramref name="zipContentFile"/> as a content object to the <paramref name="album"/>.
	/// </summary>
	/// <param name="zipContentFile">A reference to a file in a ZIP archive.</param>
	/// <param name="album">The album to which the file should be added as a content object.</param>
	private void addContentObjectToGallery(ZipEntry zipContentFile, AlbumBo album) throws InvalidContentObjectException, InvalidAlbumException, IOException, UnsupportedImageTypeException, InvalidGalleryException{
		String zipFileName = FilenameUtils.getName(zipContentFile.getName()).trim();

		if (zipFileName.length() == 0)
			return;

		String uniqueFilename = HelperFunctions.validateFileName(album.getFullPhysicalPathOnDisk(), zipFileName);
		String uniqueFilepath = FilenameUtils.concat(album.getFullPhysicalPathOnDisk(), uniqueFilename);

		// Extract the file from the zip stream and save as the specified filename.
		extractFileFromZipStream(uniqueFilepath);

		// Get the file we just saved to disk.
		File contentObjectFile = new File(uniqueFilepath);

		try
		{
			ContentObjectBo contentObject = CMUtils.createContentObjectInstance(contentObjectFile, album);
			HelperFunctions.updateAuditFields(contentObject, this.userName);
			contentObject.save();

			if (discardOriginalImage){
				contentObject.deleteOriginalFile();
				contentObject.save();
			}

			this.fileExtractionResults.add(new ActionResult(
																ActionResultStatus.Success.toString(),
																contentObjectFile.getName(),
																StringUtils.EMPTY,
																null
															));
		}catch (UnsupportedContentObjectTypeException ex){
			this.fileExtractionResults.add(new ActionResult(
															 ActionResultStatus.Error.toString(),
															 contentObjectFile.getName(),
															 ex.getMessage(),
															 null
															));

			try {
				Files.delete(contentObjectFile.toPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void extractFileFromZipStream(String uniqueFilepath) throws IOException{
		File fs = new File(uniqueFilepath);
		{
			extractFileToStream(this.zipStream, fs);
		}
	}

	private static void extractFileToStream(ZipInputStream zipStream, File destStream) throws IOException{
		int bufferSize = AppSettings.getInstance().getContentObjectDownloadBufferSize();
		byte[] data = new byte[bufferSize];

		int byteCount;
		OutputStream outputStream = new FileOutputStream(destStream);
		while ((byteCount = zipStream.read(data, 0, data.length)) > 0){
			outputStream.write(data, 0, byteCount);
		}
		outputStream.close();
	}

	private AlbumBo verifyAlbumExistsAndReturnReference(ZipEntry zipContentFile, AlbumBo rootParentAlbum) throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, GallerySecurityException, InvalidGalleryException{
		// Get the directory path of the next file or directory within the zip file.
		// Ex: album1\album2\album3, album1
		String zipDirectoryPath = FilenameUtils.getFullPathNoEndSeparator(zipContentFile.getName());

		String[] directoryNames = StringUtils.split(zipDirectoryPath, File.separator);

		String albumFullPhysicalPath = rootParentAlbum.getFullPhysicalPathOnDisk();
		AlbumBo currentAlbum = rootParentAlbum;

		for (String directoryNameFromZip : directoryNames){
			String shortenedDirName = getPreviouslyCreatedTruncatedAlbumName(albumFullPhysicalPath, directoryNameFromZip);

			// Ex: c:\inetpub\wwwroot\dcmsystem\mypics\2006\album1
			albumFullPhysicalPath = FilenameUtils.concat(albumFullPhysicalPath, shortenedDirName);

			AlbumBo newAlbum = null;

			if (FileMisc.fileExists(albumFullPhysicalPath))	{
				// Directory exists, so there is probably an album corresponding to it. Find it.
				ContentObjectBoCollection childContentObjects = currentAlbum.getChildContentObjects(ContentObjectType.Album);
				for (ContentObjectBo childContentObject : childContentObjects.values())	{
					if (childContentObject.getFullPhysicalPathOnDisk().equalsIgnoreCase(albumFullPhysicalPath )){
						newAlbum = Reflections.as(childContentObject, AlbumBo.class); 
						break;
					}
				}

				if (newAlbum == null){
					// No album in the database matches that directory. Add it.

					// Before we add the album, we need to make sure the user has permission to add the album. Check if user
					// is authenticated and if the current album is the one passed into this method. It can be assumed that any
					// other album we encounter has been created by this method and we checked for permission when it was created.
					if (this.isAuthenticated && (currentAlbum.getId() == rootParentAlbum.getId()))
						SecurityGuard.throwIfUserNotAuthorized(SecurityActions.AddChildAlbum, this.roles, currentAlbum.getId(), currentAlbum.getGalleryId(), this.isAuthenticated, currentAlbum.getIsPrivate(), currentAlbum.getIsVirtualAlbum());

					newAlbum = CMUtils.createEmptyAlbumInstance(currentAlbum.getGalleryId());
					newAlbum.setParent(currentAlbum);
					newAlbum.setIsPrivate(currentAlbum.getIsPrivate());
					newAlbum.setDirectoryName(directoryNameFromZip);
					HelperFunctions.updateAuditFields(newAlbum, this.userName);
					newAlbum.save();
				}
			}else{
				// The directory doesn't exist. Create an album.

				// Before we add the album, we need to make sure the user has permission to add the album. Check if user
				// is authenticated and if the current album is the one passed into this method. It can be assumed that any
				// other album we encounter has been created by this method and we checked for permission when it was created.
				if (this.isAuthenticated && (currentAlbum.getId() == rootParentAlbum.getId()))
					SecurityGuard.throwIfUserNotAuthorized(SecurityActions.AddChildAlbum, this.roles, currentAlbum.getId(), currentAlbum.getGalleryId(), this.isAuthenticated, currentAlbum.getIsPrivate(), currentAlbum.getIsVirtualAlbum());

				newAlbum = CMUtils.createEmptyAlbumInstance(currentAlbum.getGalleryId());
				newAlbum.setIsPrivate(currentAlbum.getIsPrivate());
				newAlbum.setParent(currentAlbum);
				newAlbum.setTitle(directoryNameFromZip);
				HelperFunctions.updateAuditFields(newAlbum, this.userName);
				newAlbum.save();

				// If the directory name written to disk is different than the name from the zip file, add it to
				// our hash table.
				if (!directoryNameFromZip.equals(newAlbum.getDirectoryName())){
					this.albumAndDirectoryNamesLookupTable.put(FilenameUtils.concat(currentAlbum.getFullPhysicalPathOnDisk(), directoryNameFromZip), FilenameUtils.concat(currentAlbum.getFullPhysicalPathOnDisk(), newAlbum.getDirectoryName()));
				}

			}
			currentAlbum = newAlbum;
		}

		return currentAlbum;
	}

	/// <summary>
	/// Return the shortened directory name, if one exists, corresponding to the directory name from the zip 
	/// file. If no shortened version exists, or it hasn't yet been created, return the directoryNameFromZip 
	/// parameter.
	/// </summary>
	/// <param name="directoryPathOnDisk">The full path of the directory, as currently stored on the disk,
	/// that contains (or will contain) the directory specified in the directoryNameFromZip parameter.</param>
	/// <param name="directoryNameFromZip">The directory name as retrieved from the zip file.</param>
	/// <returns>Return the shortened directory name, if one exists, corresponding to the directory name from the zip 
	/// file. If no shortened version exists, or it hasn't yet been created, return the directoryNameFromZip 
	/// parameter.</returns>
	/// <example>Say a zip file contains a directory named 'ThisIsAReallyLongDirectoryName'. When we first
	/// encounter this directory name, a shortened version is automatically created when the album is created,
	/// such as 'ThisIsAReallyLong'. (The purpose is to prevent too many long-named nested directories
	/// from exceeding the OS's  limit.) A record of this is added to the hash table. Now, as we process
	/// subsequent items in the zip file within the same directory, this method will return the shortened
	/// version. This is used by the calling method to add these subsequent items to this directory rather than 
	/// to a new one.</example>
	private String getPreviouslyCreatedTruncatedAlbumName(String directoryPathOnDisk, String directoryNameFromZip){
		// The directory name (directoryNameFromZip), as it comes from the zip file, may exceed our maximum length.
		// When this happens, a record is inserted into the _albumAndDirectoryNamesLookupTable hash table
		// with the original directory name as it comes from the zip file (key) and the shortened 
		// version that is used as the actual directory name in the content objects directory (value).
		// (Note that full directory paths are stored in the hash table to differentiate directories with the 
		// same names but at different heirarchies.)
		String fullDirectoryPath = FilenameUtils.concat(directoryPathOnDisk, directoryNameFromZip);
		String shortenedDirectoryName = directoryNameFromZip;

		for (Map.Entry<String, String> de : this.albumAndDirectoryNamesLookupTable.entrySet()){
			if (de.getKey().equals(fullDirectoryPath)){
				String shortenedPath = de.getValue();
				shortenedDirectoryName = shortenedPath.substring(shortenedPath.lastIndexOf(File.separatorChar) + 1);
			}
		}
		return shortenedDirectoryName;
	}

	/// <summary>
	/// Process a ZIP file that is embedded within the parent ZIP file. Its contents are extracted and turned into 
	/// albums and content objects just like items in the parent ZIP file.
	/// </summary>
	/// <param name="zipFile">A reference to a ZIP file contained within the parent ZIP file. Notice that we don't do
	/// anything with this parameter other than verify that its extension is "ZIP". That's because we actually extract
	/// the file from the parent ZIP file by calling the ExtractFileFromZipStream method, which extracts the file from 
	/// the class-level member variable _zipStream</param>
	/// <param name="parentAlbum">The album that should contain the top-level directories and files found in the ZIP
	/// file.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="parentAlbum" /> is null.</exception>
	/// <exception cref="ArgumentException">Thrown when the file extension of <paramref name="zipFile" /> is not "zip".</exception>
	private void extractEmbeddedZipFile(ZipEntry zipFile, AlbumBo parentAlbum) throws ZipException, IOException, InvalidContentObjectException, InvalidAlbumException, UnsupportedImageTypeException, UnsupportedContentObjectTypeException, GallerySecurityException, InvalidGalleryException{
		//#region Validation

		if (!FileMisc.getExt(zipFile.getName()).equalsIgnoreCase(".zip" ))
			throw new ArgumentException(StringUtils.join("The zipFile parameter of the method ExtractEmbeddedZipFile in class ZipUtility must be a ZIP file. Instead, it had the file extension ", FileMisc.getExt(zipFile.getName()), "."));

		if (parentAlbum == null)
			throw new ArgumentNullException("parentAlbum");

		//#endregion

		String filepath = FilenameUtils.concat(parentAlbum.getFullPhysicalPathOnDisk(), UUID.randomUUID().toString() + ".zip");
		try{
			extractFileFromZipStream(filepath);
			ZipUtility zip = new ZipUtility(this.userName, this.roles);
			{
				this.fileExtractionResults.addAll(zip.extractZipFile(new File(filepath), parentAlbum, true));
			}
		}finally {
			FileMisc.deleteFile(filepath);
		}
	}

	/// <summary>
	/// Determines if watermark is to be applied to images in the specified <paramref name="album" />.
	/// </summary>
	/// <param name="album">The album.</param>
	/// <returns>
	/// Returns <c>true</c> if a watermark must be applied, <c>false</c> if not.
	/// </returns>
	private boolean determineIfWatermarkIsToBeApplied(AlbumBo album) throws InvalidAlbumException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		boolean applyWatermark = false;
		boolean applyWatermarkConfig = CMUtils.loadGallerySetting(album.getGalleryId()).getApplyWatermark();
		boolean userHasNoWatermarkPermission = SecurityGuard.isUserAuthorized(SecurityActions.HideWatermark, this.roles, album.getId(), album.getGalleryId(), this.isAuthenticated, false, album.getIsVirtualAlbum());

		/*if (AppSettings.getInstance().License.IsInReducedFunctionalityMode || (applyWatermarkConfig && !userHasNoWatermarkPermission)){
			applyWatermark = true;
		}*/
		if (applyWatermarkConfig && !userHasNoWatermarkPermission){
			applyWatermark = true;
		}
		
		return applyWatermark;
	}

	//#endregion

	//#region IDisposable

	/// <summary>
	/// Performs application-defined tasks associated with freeing, releasing, or resetting unmanaged resources.
	/// </summary>
	public void dispose() throws IOException{
		dispose(true);
		//GC.SuppressFinalize(this);
	}

	/// <summary>
	/// Releases unmanaged and - optionally - managed resources
	/// </summary>
	/// <param name="disposing"><c>true</c> to release both managed and unmanaged resources; <c>false</c> to release only unmanaged resources.</param>
	protected void dispose(boolean disposing) throws IOException{
		if (!this.hasBeenDisposed)
		{
			// Dispose of resources held by this instance.
			if (this.zipStream != null){
				this.zipStream.close();;
				this.zipStream = null;
			}

			// Set the sentinel.
			this.hasBeenDisposed = true;
		}
	}

	//#endregion

}
