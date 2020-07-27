package com.mds.aiotplayer.cm.content;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.SynchronizationInProgressException;
import com.mds.aiotplayer.cm.exception.SynchronizationTerminationRequestedException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.metadata.ContentObjectMetadataItem;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.core.ContentQueueItemStatus;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.core.MimeTypeCategory;
import com.mds.aiotplayer.core.Size;
import com.mds.aiotplayer.core.SynchronizationState;
import com.mds.aiotplayer.core.exception.ArgumentException;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.core.exception.ArgumentOutOfRangeException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.core.exception.WebException;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.util.FileMisc;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.StringUtils;

/// <summary>
/// Contains functionality for synchronizing the content object files on the hard drive with the records in the data store.
/// </summary>
public class SynchronizationManager {
	//#region Private Fields

	private long galleryId;
	private String thumbnailRootPath;
	private String optimizedRootPath;
	private long optimizedTriggerSizeKb;
	private int optimizedMaxLength;
	private String thumbnailPrefix;
	private String optimizedPrefix;
	private int fullContentObjectPathLength;
	
	private String userName;

	private boolean isRecursive;
	private boolean rebuildOptimized;
	private boolean rebuildThumbnail;
	private int lastTransactionCommitFileIndex;
	private GallerySettings gallerySetting;

	// About the synch status object: When a synch is started, we grab a reference to the singleton synch status for the gallery and
	// update its properties with the current synch info, then we persist to the database so other processes (such as an external 
	// utility) can check for synch status info. However, as the synch progresses we only update the in-memory version of the object and
	// do not write to the data store until the synch is complete, where we then mark the synch record as being complete.
	private SynchronizationStatus synchStatus;

	//#endregion

	//#region Constructor

	/// <summary>
	/// Instantiates a new <see cref="SynchronizationManager" /> object, with the properties 
	/// <see cref="IsRecursive" />, <see cref="RebuildOptimized" />, and <see cref="RebuildThumbnail" />
	/// all defaulted to true.
	/// </summary>
	/// <param name="galleryId">The value that uniquely identifies the gallery to be synchronized.</param>
	public SynchronizationManager(long galleryId) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		this.galleryId = galleryId;
		this.thumbnailRootPath = getGallerySettings().getFullThumbnailPath();
		this.optimizedRootPath = getGallerySettings().getFullOptimizedPath();
		this.optimizedTriggerSizeKb = getGallerySettings().getOptimizedImageTriggerSizeKb();
		this.optimizedMaxLength = getGallerySettings().getMaxOptimizedLength();
		this.thumbnailPrefix = getGallerySettings().getThumbnailFileNamePrefix();
		this.optimizedPrefix = getGallerySettings().getOptimizedFileNamePrefix();
		this.fullContentObjectPathLength = getGallerySettings().getFullContentObjectPath().length();

		this.isRecursive = true;
		this.rebuildOptimized = true;
		this.rebuildThumbnail = true;
	}

	//#endregion

	//#region Public Properties

	/// <summary>
	/// Indicates whether the synchronization continues drilling down into directories
	/// below the current one. The default value is true.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the synchronization procedure recursively
	/// synchronizes all directories within the current one; otherwise, <c>false</c>.
	/// </value>
	public boolean isRecursive(){
		return this.isRecursive;
	}
	
	public void setRecursive(boolean isRecursive){
		this.isRecursive = isRecursive;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the optimized version is deleted and overwritten 
	/// with a new one based on the original file. Only relevant for images and for video/audio 
	/// files when FFmpeg is installed and an applicable encoder setting exists. The default 
	/// value is true.
	/// </summary>
	/// <value><c>true</c> if optimized images are overwritten during a synchronization; 
	/// otherwise, <c>false</c>.</value>
	public boolean isRebuildOptimized(){
		return this.rebuildOptimized;
	}
	
	public void setRebuildOptimized(boolean rebuildOptimized){
		this.rebuildOptimized = rebuildOptimized;
	}

	/// <summary>
	/// Gets or sets the user name for the logged on user. This is used for the audit fields in the album and media
	/// objects.
	/// </summary>
	/// <value>The user name for the logged on user.</value>
	private String getUserName() { 
		return this.userName;
	}
	
	private void setUserName(String userName) { 
		this.userName = userName;
	}

	/// <summary>
	/// Gets or sets a value indicating whether a thumbnail image is deleted and overwritten 
	/// with a new one based on the original file. Applies to all content objects. The default 
	/// value is true.
	/// </summary>
	/// <value><c>true</c> if thumbnail images are overwritten during a synchronization; 
	/// otherwise, <c>false</c>.</value>
	public boolean isRebuildThumbnail()	{
		return this.rebuildThumbnail;
	}
	
	public void setRebuildThumbnail(boolean rebuildThumbnail)	{
		this.rebuildThumbnail = rebuildThumbnail;
	}

	//#endregion

	//#region Private Properties

	private GallerySettings getGallerySettings() throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		if (this.gallerySetting == null){
			this.gallerySetting = CMUtils.loadGallerySetting(this.galleryId);
		}

		return this.gallerySetting;
	}

	//#endregion

	//#region Public Methods

	/// <summary>
	/// Synchronize the content object library, starting with the root album. Optionally specify that only the 
	/// specified album is synchronized. If <see cref="IsRecursive" /> = true, then child albums are recursively synchronized;
	/// otherwise, only the root album (or the specified album if that overload is used) is synchronized.
	/// </summary>
	/// <param name="synchId">A GUID that uniquely identifies the synchronization. If another synchronization is in 
	/// progress, a <see cref="MDS.SynchronizationInProgressException" /> exception is thrown.</param>
	/// <param name="userName">The user name for the logged on user. This is used for the audit fields in the album 
	/// and content objects.</param>
	/// <exception cref="MDS.SynchronizationInProgressException">
	/// Thrown if another synchronization is in progress.</exception>
	public void synchronize(String synchId, String userName) throws Exception{
		synchronize(synchId, CMUtils.loadRootAlbumInstance(this.galleryId, true, true), userName);
	}

	/// <summary>
	/// Synchronize the content object library, starting with the root album. Optionally specify that only the 
	/// specified album is synchronized. If <see cref="IsRecursive" /> = true, then child albums are recursively synchronized;
	/// otherwise, only the root album (or the specified album if that overload is used) is synchronized.
	/// </summary>
	/// <param name="synchId">A GUID that uniquely identifies the synchronization. If another synchronization is in 
	/// progress, a <see cref="MDS.SynchronizationInProgressException" /> exception is thrown.</param>
	/// <param name="userName">The user name for the logged on user. This is used for the audit fields in the album 
	/// and content objects.</param>
	/// <param name="album">The album to synchronize.</param>
	/// <exception cref="MDS.SynchronizationInProgressException">
	/// Thrown if another synchronization is in progress.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="album" /> is null.</exception>
	public void synchronize(String synchId, AlbumBo album, String userName) throws Exception	{
		if (album == null)
			throw new ArgumentNullException("album");

		try{
			initialize(synchId, album, userName); // Will throw SynchronizationInProgressException if another is in progress. Will be caught be upstream code.

			File albumDirectory = new File(album.getFullPhysicalPathOnDisk());

			// Synchronize the files in this album. No recursive action.
			synchronizeContentObjectFiles(albumDirectory, album);

			if (isRecursive){
				// Synchronize the child directories and their files. Acts recursively.
				synchronizeChildDirectories(albumDirectory, album);
			}

			AlbumBo.assignAlbumThumbnail(album, false, true, this.userName);

			album.sortAsync(true, this.userName, true);

			if (this.synchStatus != null)
				this.synchStatus.finish();
		}catch (SynchronizationTerminationRequestedException te){
			// The user has canceled the synchronization. Swallow the exception and return.
			return;
		}catch (SynchronizationInProgressException ie){
			// Another sync is in progress. We don't want the generic catch below to change the sync state, so we intercept it here.
			throw ie;
		}catch(Exception ex){
			if (this.synchStatus != null)
				updateStatus(0, null, null, SynchronizationState.Error, true);

			throw ex;
		}finally {
			HelperFunctions.purgeCache();
		}
	}

	//#endregion

	//#region Private methods

	private void initialize(String synchId, AlbumBo album, String userName) throws SynchronizationInProgressException, RecordExistsException, WebException, UnsupportedContentObjectTypeException, InvalidGalleryException	{
		if (album == null)
			throw new ArgumentNullException("album");

		if (StringUtils.isBlank(userName))
			throw new ArgumentNullException("userName");

		if (!album.getIsWritable()){
			throw new WebException(StringUtils.format("The album is not writable (ID {0}, Title='{1}')", album.getId(), album.getTitle()));
		}

		this.userName = userName;

		// Tell the status instance we are starting a new synchronization. It will throw
		// SynchronizationInProgressException if another is in progress.
		this.synchStatus = SynchronizationStatus.start(synchId, album.getGalleryId());

		this.synchStatus.update(SynchronizationState.NotSet, countFiles(album.getFullPhysicalPathOnDisk()), null, null, null, null, true);
	}

	/// <summary>
	/// Get the number of files in the specified directory path, including any subdirectories if
	/// IsRecursive = true. But don't count any optimized or thumbnail files.
	/// </summary>
	/// <param name="directoryPath"></param>
	/// <returns></returns>
	/// <exception cref="System.IO.DirectoryNotFoundException">Thrown if the specified directory does not exist.</exception>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when <paramref name="directoryPath" /> is null or an empty String.</exception>
	private int countFiles(String directoryPath) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		if (StringUtils.isBlank(directoryPath))
			throw new ArgumentOutOfRangeException("directoryPath");

		int countTotal;

		try	{
			//countTotal = Directory.GetFiles(directoryPath).Length;
			File file = new File(directoryPath);
			countTotal = file.list().length;
		}catch (SecurityException se){
			return 0;
		}

		// Get a count of the thumbnail and optimized images, but only if they are stored in the content objects directory.      
		int countThumbnail = 0;
		File file = new File(directoryPath);
		if (getGallerySettings().getFullThumbnailPath().equals(getGallerySettings().getFullContentObjectPath())){
			try	{
				
				countThumbnail = file.list(new PrefixFileFilter(getGallerySettings().getThumbnailFileNamePrefix())).length;
				//countThumbnail = Directory.GetFiles(directoryPath, getGallerySettings().getThumbnailFileNamePrefix + "*").Length;
			}
			catch (SecurityException se) { }
		}

		int countOptimized = 0;
		if (getGallerySettings().getFullOptimizedPath().equals(getGallerySettings().getFullContentObjectPath())){
			try	{
				countOptimized = file.list(new PrefixFileFilter(getGallerySettings().getOptimizedFileNamePrefix())).length;
				
				//countOptimized = Directory.GetFiles(directoryPath, getGallerySettings().getOptimizedFileNamePrefix + "*").Length;
			}catch (SecurityException se) { }
		}

		String[] dirs = null;
		try	{
			dirs = file.list(DirectoryFileFilter.DIRECTORY);
			//dirs = Directory.GetDirectories(directoryPath);
		}catch (SecurityException se) { }

		if (this.isRecursive && (dirs != null)){
			for (String dir : dirs)	{
				countTotal += countFiles(dir);
			}
		}

		int totalNumFiles = countTotal - countThumbnail - countOptimized;

		// If we compute a number < 0, then just return 0.
		return (totalNumFiles < 0 ? 0 : totalNumFiles);
	}

	/// <summary>
	/// Ensure the directories and content object files within parentDirectory have corresponding albums 
	/// and content objects. An exception is thrown if parentAlbum.FullPhysicalPathOnDisk does not equal
	/// parentDirectory.FullName. If IsRecursive = true, this method recursively calls itself.
	/// </summary>
	/// <param name="parentDirectory">A DirectoryInfo instance corresponding to the FullPhysicalPathOnDisk
	/// property of parentAlbum.</param>
	/// <param name="parentAlbum">An album instance. Directories under the parentDirectory parameter will be
	/// added (or updated if they already exist) as child albums of this instance.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="parentDirectory" /> or <paramref name="parentAlbum" /> is null.</exception>
	private void synchronizeChildDirectories(File parentDirectory, AlbumBo parentAlbum) throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, SynchronizationInProgressException, RecordExistsException, InvalidContentObjectException, SynchronizationTerminationRequestedException, InvalidGalleryException{
		//#region Parameter validation

		if (parentDirectory == null)
			throw new ArgumentNullException("parentDirectory");

		if (parentAlbum == null)
			throw new ArgumentNullException("parentAlbum");

		if (!parentDirectory.getPath().equalsIgnoreCase(parentAlbum.getFullPhysicalPathOnDisk()))
			throw new ArgumentException(MessageFormat.format("Synchronization error. parentAlbum.FullPhysicalPathOnDisk must be equal to parentDirectory.FullName. parentDirectory.FullName='{0}'; parentAlbum.FullPhysicalPathOnDisk='{1}'", parentDirectory.getPath(), parentAlbum.getFullPhysicalPathOnDisk()));

		//#endregion

		// Recursively traverse all subdirectories and their files and synchronize each object we find.
		// Skip any hidden directories.
		File[] childDirectories;
		try{
			childDirectories = parentDirectory.listFiles((FileFilter)DirectoryFileFilter.DIRECTORY);
			//childDirectories = parentDirectory.GetDirectories();
		}catch (SecurityException se){
			return;
		}

		for (File subdirectory : childDirectories)	{
			if (subdirectory.isHidden()){
				this.synchStatus.getSkippedContentObjects().add(new ImmutablePair<String, String>(StringUtils.removeStart(subdirectory.getPath(), this.fullContentObjectPathLength + 1), I18nUtils.getMessage("synchronizationStatus.Hidden_Directory_Msg")));
				continue;
			}

			AlbumBo childAlbum = synchronizeDirectory(subdirectory, parentAlbum);

			try
			{
				synchronizeContentObjectFiles(subdirectory, childAlbum);

				synchronizeChildDirectories(subdirectory, childAlbum);
			}
			catch (SecurityException se)
			{
				childAlbum.deleteFromGallery();
			}
		}

		deleteOrphanedAlbumRecords(parentAlbum);
	}

	/// <summary>
	/// Synchronizes the content object files in the <paramref name="directory" /> associated with the <paramref name="album" />.
	/// Does not act recursively.
	/// </summary>
	/// <param name="directory">The directory.</param>
	/// <param name="album">The album.</param>
	/// <exception cref="UnauthorizedAccessException">Thrown when the IIS app pool identity cannot access the files in the directory.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="album" /> or <paramref name="directory" /> is null.</exception>
	/// <exception cref="ArgumentException">Thrown when the full directory path of <paramref name="directory" /> does not match the directory path of 
	/// <paramref name="album" />.</exception>
	private void synchronizeContentObjectFiles(File directory, AlbumBo album) throws SynchronizationInProgressException, RecordExistsException, IOException, InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, SynchronizationTerminationRequestedException, InvalidGalleryException	{
		//#region Parameter validation

		if (directory == null)
			throw new ArgumentNullException("directory");

		if (album == null)
			throw new ArgumentNullException("album");

		if (!directory.getPath().equalsIgnoreCase(album.getFullPhysicalPath()))
			throw new ArgumentException(MessageFormat.format("Error in SynchronizeContentObjectFiles(): The full directory path of the parameter 'directory' does not match the directory path of the parameter 'album'. directory.FullName='{0}'; album.FullPhysicalPath='{1}'", directory.getPath(), album.getFullPhysicalPath()));

		//#endregion

		//Update the content object table in the database with the file attributes of all
		//files in the directory passed to this function. Skip any hidden files.
		File[] files;
		try{
			files = directory.listFiles();
		}catch (SecurityException se){
			this.synchStatus.getSkippedContentObjects().add(new ImmutablePair<String, String>(directory.getName(), I18nUtils.getMessage("synchronizationStatus.Restricted_Directory_Msg")));
			throw se;
		}

		// First sort by the filename.
		Arrays.sort(files, new Comparator<File>() {
			    @Override
			    public int compare(File o1, File o2) {
			        return o1.getName().compareToIgnoreCase(o2.getName());
			    }
		    }); // Don't use Ordinal or OrdinalIgnoreCase, as it sorts unexpectedly (e.g. 100.pdf comes before _100.pdf)

		for (File file : files){
			if (file.isHidden()){
				this.synchStatus.getSkippedContentObjects().add(new ImmutablePair<String, String>(StringUtils.removeStart(file.getPath(), this.fullContentObjectPathLength + 1), I18nUtils.getMessage("synchronizationStatus.Hidden_File_Msg")));
				continue;
			}

			//#region Process thumbnail or optimized image

			if (StringUtils.startsWithIgnoreCase(file.getName(), this.thumbnailPrefix))	{
				// We have a thumbnail image. If we are storing thumbnails in a different directory, delete the file, but only if the path
				// is writeable. The user may have just specified a new thumbnail path, and we need to delete all the previous thumbnails 
				// from their original location.
				if (this.thumbnailRootPath != getGallerySettings().getFullContentObjectPath() && !getGallerySettings().getContentObjectPathIsReadOnly()){
					Files.delete(file.toPath());
				}
				continue;
			}

			if (StringUtils.startsWithIgnoreCase(file.getName(), this.optimizedPrefix))	{
				// We have an optimized image. If we are storing optimized images in a different directory, delete the file, but only if the path
				// is writeable. The user may have just specified a new optimized path, and we need to delete all the previous optimized images 
				// from their original location.
				if (this.optimizedRootPath != getGallerySettings().getFullContentObjectPath() && !getGallerySettings().getContentObjectPathIsReadOnly()){
					Files.delete(file.toPath());
				}
				continue;
			}

			//#endregion

			// See if this file is an existing content object.
			ContentObjectBo contentObject = album
				.getChildContentObjects(ContentObjectType.ContentObject).stream()
				.filter(mo -> mo.getOriginal().getFileNamePhysicalPath().equalsIgnoreCase(file.getPath())).findFirst().orElse(null);

			if (contentObject != null){
				// Found an existing content object matching the file on disk. Update properties, but only if its file extension
				// is enabled. (If this is a content object that had been added to MDS System but its file type was 
				// subsequently disabled, we do not want to synchronize it - we want its info in the data store to be deleted.)
				if (HelperFunctions.isFileAuthorizedForAddingToGallery(file.getName(), album.getGalleryId()))	{
					updateExistingContentObject(contentObject);
				}
			}else{
				// No content object exists for this file. Create a new one.
				createNewContentObject(album, file);
			}

			int newFileIndex = this.synchStatus.getCurrentFileIndex() + 1;
			if (newFileIndex < this.synchStatus.getTotalFileCount())	{
				boolean persistToDatabase = (this.synchStatus.getCurrentFileIndex() % 100) == 0; // Save to DB every 100 files

				updateStatus(newFileIndex, file.getParent(), file.getName(), SynchronizationState.NotSet, persistToDatabase);
			}

			synchronized (this.synchStatus){
				if (this.synchStatus.isShouldTerminate()){
					// Immediately set this property back to false so that we don't trigger this code again, then throw a special exception
					// that will be caught and used to cancel the synch.
					this.synchStatus.update(SynchronizationState.Aborted, null, StringUtils.EMPTY, null, StringUtils.EMPTY, false, true);
					throw new SynchronizationTerminationRequestedException();
				}
			}
		}

		// Synchronize any external content objects previously added. No recursive action.
		synchronizeExternalContentObjects(album);

		deleteOrphanedContentObjectRecords(album);

		deleteOrphanedThumbnailAndOptimizedFiles(album);
	}
	
	private void updateStatus(int currentFileIndex) throws SynchronizationInProgressException, RecordExistsException{
		updateStatus(currentFileIndex, null);
	}
	
	private void updateStatus(int currentFileIndex, String filepath) throws SynchronizationInProgressException, RecordExistsException{
		updateStatus(currentFileIndex, filepath, null);
	}
	
	private void updateStatus(int currentFileIndex, String filepath, String filename) throws SynchronizationInProgressException, RecordExistsException{
		updateStatus(currentFileIndex, filepath, filename, SynchronizationState.NotSet);
	}
	
	private void updateStatus(int currentFileIndex, String filepath, String filename, SynchronizationState syncState) throws SynchronizationInProgressException, RecordExistsException{
		updateStatus(currentFileIndex, filepath, filename, syncState, false);
	}

	private void updateStatus(int currentFileIndex, String filepath, String filename, SynchronizationState syncState, boolean persistToDatabase) throws SynchronizationInProgressException, RecordExistsException{
		String currentFilePath = (filepath != null ? StringUtils.stripStart(StringUtils.removeStart(filepath, this.fullContentObjectPathLength), File.separator) : null);

		this.synchStatus.update(syncState, null, filename, currentFileIndex, currentFilePath, null, persistToDatabase);
	}

	private void createNewContentObject(AlbumBo album, File file) throws InvalidContentObjectException, InvalidAlbumException, IOException, UnsupportedImageTypeException, InvalidGalleryException{
		try
		{
			ContentObjectBo contentObject = CMUtils.createContentObjectInstance(file, album);
			HelperFunctions.updateAuditFields(contentObject, userName);
			contentObject.save();

			if (!getGallerySettings().getContentObjectPathIsReadOnly() && (getGallerySettings().getDiscardOriginalImageDuringImport()))	{
				contentObject.deleteOriginalFile();
				contentObject.save();
			}

			contentObject.setIsSynchronized(true);
		}catch (UnsupportedContentObjectTypeException ce)	{
			this.synchStatus.getSkippedContentObjects().add(new ImmutablePair<String, String>(StringUtils.removeStart(file.getPath(), this.fullContentObjectPathLength + 1), I18nUtils.getMessage("synchronizationStatus.Disabled_File_Type_Msg")));
		}
	}

	private void updateExistingContentObject(ContentObjectBo contentObject) throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidGalleryException{
		contentObject.setRegenerateThumbnailOnSave(rebuildThumbnail);
		contentObject.setRegenerateOptimizedOnSave(rebuildOptimized);

		// Check for existence of thumbnail.
		if (!FileMisc.fileExists(contentObject.getThumbnail().getFileNamePhysicalPath())){
			contentObject.setRegenerateThumbnailOnSave(true);
		}

		switch (contentObject.getContentObjectType()){
			case Image:
				evaluateOriginalImage((Image)contentObject);
				evaluateOptimizedImage((Image)contentObject);
				break;

			case Video:
			case Audio:
				evaluateOptimizedVideoAudio(contentObject);
				break;

			default:
				updateNonImageWidthAndHeight(contentObject);
				break;
		}

		updateMetadataFilename(contentObject);

		HelperFunctions.updateAuditFields(contentObject, userName);
		contentObject.save();
		contentObject.setIsSynchronized(true);
	}

	private void synchronizeExternalContentObjects(AlbumBo album) throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidGalleryException{
		List<ContentObjectBo> contentObjects = album.getChildContentObjects(ContentObjectType.External).values();
		for (ContentObjectBo contentObject : contentObjects){
			contentObject.setIsSynchronized(true);

			// Check for existence of thumbnail.
			if (rebuildThumbnail || !FileMisc.fileExists(contentObject.getThumbnail().getFileNamePhysicalPath()))	{
				contentObject.setRegenerateThumbnailOnSave(true);
				HelperFunctions.updateAuditFields(contentObject, userName);
				contentObject.save();
				contentObject.setIsSynchronized(true);
			}
		}
	}

	/// <summary>
	/// Find, or create if necessary, the album corresponding to the specified directory and set it as the 
	/// child of the parentAlbum parameter.
	/// </summary>
	/// <param name="directory">The directory for which to obtain a matching album object.</param>
	/// <param name="parentAlbum">The album that contains the album at the specified directory.</param>
	/// <returns>Returns an album object corresponding to the specified directory and having the specified
	/// parent album.</returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="directory" /> or <paramref name="parentAlbum" /> is null.</exception>
	/// <exception cref="ArgumentException">Thrown when </exception>
	/// <exception cref="ArgumentException">Thrown when the full directory path of the parent of <paramref name="directory" /> does not match the 
	/// directory path of <paramref name="parentAlbum" />.</exception>
	private AlbumBo synchronizeDirectory(File directory, AlbumBo parentAlbum) throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidGalleryException{
		//#region Parameter validation

		if (directory == null)
			throw new ArgumentNullException("directory");

		if (parentAlbum == null)
			throw new ArgumentNullException("parentAlbum");

		if (!directory.getParent().equalsIgnoreCase(StringUtils.stripEnd(parentAlbum.getFullPhysicalPathOnDisk(), File.separator)))
			throw new ArgumentException(MessageFormat.format("Error in SynchronizeDirectory(). directory.getParent().FullName='{0}'; parentAlbum.FullPhysicalPathOnDisk='{1}'", directory.getParent(), StringUtils.stripEnd(parentAlbum.getFullPhysicalPathOnDisk(),  File.separator)));

		//#endregion

		AlbumBo childAlbum = (AlbumBo)parentAlbum.getChildContentObjects(ContentObjectType.Album).stream()
			.filter(a -> a.getFullPhysicalPathOnDisk() == directory.getPath()).findFirst().orElse(null);

		if (childAlbum != null)	{
			// Found the album. Update properties.
			childAlbum.setIsPrivate((parentAlbum.getIsPrivate() ? true : childAlbum.getIsPrivate())); // Only set to private if parent is private
			childAlbum.setRegenerateThumbnailOnSave(rebuildThumbnail);
		}else{
			// No album exists for this directory. Create a new one.
			childAlbum = CMUtils.createEmptyAlbumInstance(parentAlbum.getGalleryId());
			childAlbum.setParent(parentAlbum);

			String directoryName = directory.getName();
			childAlbum.setTitle(directoryName);
			//childAlbum.ThumbnailContentObjectId = 0; // not needed
			childAlbum.setDirectoryName(directoryName);
			childAlbum.setFullPhysicalPathOnDisk(FilenameUtils.concat(parentAlbum.getFullPhysicalPathOnDisk(), directoryName));
			childAlbum.setIsPrivate(parentAlbum.getIsPrivate());
		}

		childAlbum.setIsSynchronized(true);

		if (childAlbum.getIsNew() || childAlbum.getHasChanges()){
			HelperFunctions.updateAuditFields(childAlbum, userName);
			childAlbum.save();
		}

		// Commit the transaction to the database for every 100 content objects that are processed.
		if ((this.synchStatus.getCurrentFileIndex() - this.lastTransactionCommitFileIndex) >= 100)
		{
			//HelperFunctions.CommitTransaction();
			//HelperFunctions.BeginTransaction();
			this.lastTransactionCommitFileIndex = this.synchStatus.getCurrentFileIndex();
		}

		return childAlbum;
	}

	private static void deleteOrphanedAlbumRecords(AlbumBo album) throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidGalleryException{
		// Delete album records that weren't sync'd.
		List<ContentObjectBo> childAlbums = album.getChildContentObjects(ContentObjectType.Album).stream().filter(a -> !a.getIsSynchronized()).collect(Collectors.toList());
		for (ContentObjectBo childAlbum : childAlbums){
			childAlbum.deleteFromGallery();
		}
	}

	private static void deleteOrphanedContentObjectRecords(AlbumBo album) throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidGalleryException{
		// Delete content object records that weren't sync'd.
		List<ContentObjectBo> orphanContentObjects = album.getChildContentObjects(ContentObjectType.ContentObject).stream()
			.filter(mo -> !mo.getIsSynchronized() && mo.getContentObjectType() != ContentObjectType.External).collect(Collectors.toList());

		for (ContentObjectBo contentObject : orphanContentObjects){
			contentObject.deleteFromGallery();
		}
	}

	/// <summary>
	/// Delete any thumbnail and optimized files that do not have matching content objects.
	/// This can occur when a user manually transfers (e.g. uses Windows Explorer)
	/// original files to a new directory and leaves the thumbnail and optimized
	/// files in the original directory or when a user deletes the original media file in 
	/// Explorer. This function *only* deletes files that begin the the thumbnail and optimized
	/// prefix (e.g. zThumb_, zOpt_).
	/// </summary>
	/// <param name="album">The album whose directory is to be processed for orphaned image files.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="album" /> is null.</exception>
	private void deleteOrphanedThumbnailAndOptimizedFiles(AlbumBo album) throws InvalidGalleryException{
		if (album == null)
			throw new ArgumentNullException("album");

		// STEP 1: Get list of directories that may contain thumbnail or optimized images for the current album
		String originalPath = album.getFullPhysicalPathOnDisk();
		String thumbnailPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(album.getFullPhysicalPathOnDisk(), getGallerySettings().getFullThumbnailPath(), getGallerySettings().getFullContentObjectPath());
		String optimizedPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(album.getFullPhysicalPathOnDisk(), getGallerySettings().getFullOptimizedPath(), getGallerySettings().getFullContentObjectPath());

		List<String> albumPaths = new ArrayList<String>(3);

		// The original path may contain thumbnails or optimized images when the thumbnail/optimized path is the same as the original path
		if ((getGallerySettings().getFullThumbnailPath().equalsIgnoreCase(getGallerySettings().getFullContentObjectPath() )) ||
			(getGallerySettings().getFullOptimizedPath().equalsIgnoreCase(getGallerySettings().getFullContentObjectPath() )))
		{
			albumPaths.add(originalPath);
		}

		if (!albumPaths.contains(thumbnailPath))
			albumPaths.add(thumbnailPath);

		if (!albumPaths.contains(optimizedPath))
			albumPaths.add(optimizedPath);


		String thumbnailPrefix = getGallerySettings().getThumbnailFileNamePrefix();
		String optimizedPrefix = getGallerySettings().getOptimizedFileNamePrefix();

		ContentObjectBoCollection contentObjects = album.getChildContentObjects(ContentObjectType.ContentObject);

		// STEP 2: Loop through each path and make sure all thumbnail and optimized files in each directory have 
		// matching content objects. Delete any files that do not.
		for (String albumPath : albumPaths){
			if (!FileMisc.fileExists(albumPath))
				return;

			File directory = new File(albumPath);

			// Loop through each file in the directory.
			File[] files;
			try
			{
				files = directory.listFiles();
			}
			catch (SecurityException se)
			{
				return;
			}

			Collection<ContentQueueItem> queueItems = getCurrentAndCompleteContentQueueItems();

			for (File file : files)	{
				if ((StringUtils.startsWithIgnoreCase(file.getName(), thumbnailPrefix )) || (StringUtils.startsWithIgnoreCase(file.getName(), optimizedPrefix))){
					// This file is a thumbnail or optimized file.

					// TEST 1: Check to see if any content object in this album refers to it.
					boolean foundContentObject = false;
					for (ContentObjectBo contentObject : contentObjects.values()){
						if ((contentObject.getOptimized().getFileName().equalsIgnoreCase(file.getName() )) ||
							(contentObject.getThumbnail().getFileName().equalsIgnoreCase(file.getName() ))){
							foundContentObject = true;
							break;
						}
					}

					if (!foundContentObject){
						// TEST 2: Maybe the encoder engine is currently creating the file or just finished it.

						// First check to see if we started processing a new media item since we started this loop.
						// If so, add it to our list of queue items.
						ContentQueueItem currentQueueItem = ContentConversionQueue.getInstance().getCurrentContentQueueItem();
						if (currentQueueItem != null && !queueItems.stream().anyMatch(mq -> mq.ContentQueueId == currentQueueItem.ContentQueueId)){
							queueItems = CollectionUtils.union(queueItems, Lists.newArrayList(currentQueueItem));
						}

						// See if this file is mentioned in any of the media queue items
						foundContentObject = queueItems.stream().anyMatch(mq -> mq.StatusDetail.contains(file.getName()));
					}

					if (!foundContentObject){
						// No content object in this album refers to this thumbnail or optimized image. Smoke it!
						try	{
							Files.delete(file.toPath());
						}catch (IOException ex)	{
							// An exception occurred, probably because the account ASP.NET is running under does not
							// have permission to delete the file. Let's record the error, but otherwise ignore it.
							//EventLogController.RecordError(ex, AppSetting.Instance, this.galleryId, CMUtils.loadGallerySettings());
						}catch (SecurityException ex){
							// An exception occurred, probably because the account ASP.NET is running under does not
							// have permission to delete the file. Let's record the error, but otherwise ignore it.
							//EventLogController.RecordError(ex, AppSetting.Instance, this.galleryId, CMUtils.loadGallerySettings());
						}
						catch (Exception ex)
						{
							// An exception occurred, probably because the account ASP.NET is running under does not
							// have permission to delete the file. Let's record the error, but otherwise ignore it.
							//EventLogController.RecordError(ex, AppSetting.Instance, this.galleryId, CMUtils.loadGallerySettings());
						}
					}
				}
			}
		}
	}

	private static Collection<ContentQueueItem> getCurrentAndCompleteContentQueueItems(){
		List<ContentQueueItem> queueItems = ContentConversionQueue.getInstance().getContentQueueItems().stream().filter(mq -> mq.Status == ContentQueueItemStatus.Complete).collect(Collectors.toList());
		
		ContentQueueItem currentQueueItem = ContentConversionQueue.getInstance().getCurrentContentQueueItem();
		if (currentQueueItem != null){
			queueItems = ListUtils.union(queueItems, Lists.newArrayList(currentQueueItem));
		}

		return queueItems;
	}

	private boolean doesOriginalExceedOptimizedTriggers(ContentObjectBo contentObject){
		// Note: This function also exists in the ImageOptimizedCreator class.

		// Test 1: Is the file size of the original greater than OptimizedImageTriggerSizeKB?
		boolean isOriginalFileSizeGreaterThanTriggerSize = false;

		if (contentObject.getOriginal().getFileSizeKB() > this.optimizedTriggerSizeKb)
		{
			isOriginalFileSizeGreaterThanTriggerSize = true;
		}

		// Test 2: Is the width or length of the original greater than the MaxOptimizedLength?
		boolean isOriginalLengthGreaterThanMaxAllowedLength = false;

		double originalWidth = 0;
		double originalHeight = 0;
		try
		{
			Size size = contentObject.getOriginal().getSize();
			originalWidth = size.Width;
			originalHeight = size.Height;
		}
		catch (UnsupportedImageTypeException ex){
			//EventLogController.RecordError(ex, AppSetting.Instance, this.galleryId, CMUtils.loadGallerySettings());
		}

		if ((originalWidth > this.optimizedMaxLength) || (originalHeight > this.optimizedMaxLength)){
			isOriginalLengthGreaterThanMaxAllowedLength = true;
		}

		return (isOriginalFileSizeGreaterThanTriggerSize | isOriginalLengthGreaterThanMaxAllowedLength);
	}

	/// <summary>
	/// If the rebuild thumbnail or rebuild image options are selected, then get the latest statistics about the 
	/// original image. Perhaps the user edited the object (such as rotating) in another program.
	/// </summary>
	/// <param name="contentObject">The content object whose original image is to be checked.</param>
	private void evaluateOriginalImage(Image contentObject)	{
		if (contentObject == null)
			return;

		if (this.rebuildThumbnail || this.rebuildOptimized)	{
			try	{
				Size size = contentObject.getOriginal().getSize();
				contentObject.getOriginal().setWidth(size.Width.intValue());
				contentObject.getOriginal().setHeight(size.Height.intValue());
			}catch (UnsupportedImageTypeException ex) { }

			int fileSize = (int)(contentObject.getOriginal().getFileInfo().length() / 1024);
			contentObject.getOriginal().setFileSizeKB((fileSize < 1 ? 1 : fileSize)); // Very small files should be 1, not 0.
		}
	}

	/// <summary>
	/// Evaluates the optimized file for video and audio objects. If the optimized file doesn't exist or is no 
	/// longer wanted, update the optimized properties to match those of the original. This helps when the
	/// encoder is configured to ignore this particular file type.
	/// </summary>
	/// <param name="contentObject">The content object.</param>
	private void evaluateOptimizedVideoAudio(ContentObjectBo contentObject) throws InvalidGalleryException{
		if (contentObject == null)
			return;

		boolean optFileMissing = !FileMisc.fileExists(contentObject.getOptimized().getFileNamePhysicalPath());
		boolean optFileIsDifferentThanOriginal = !contentObject.getOptimized().getFileName().equalsIgnoreCase(contentObject.getOriginal().getFileName() );
		boolean optFileNotWanted = (rebuildOptimized && optFileIsDifferentThanOriginal && !ContentConversionQueue.getInstance().hasEncoderSetting(contentObject));

		if (optFileMissing || optFileNotWanted){
			// If the file exists, it will later be deleted in DeleteOrphanedThumbnailAndOptimizedFiles.
			contentObject.getOptimized().setFileName(contentObject.getOriginal().getFileName());
			contentObject.getOptimized().setWidth(contentObject.getOriginal().getWidth());
			contentObject.getOptimized().setHeight(contentObject.getOriginal().getHeight());
			contentObject.getOptimized().setFileSizeKB(contentObject.getOriginal().getFileSizeKB());
		}
	}

	/// <summary>
	/// Check that the optimized image exists. <paramref name="contentObject"/> *must* be an <see cref="Image"/> type.
	/// If "overwrite compressed" option is selected, also check whether it the optimized version is really needed.
	/// </summary>
	/// <param name="contentObject">The content object whose optimized image is to be checked.</param>
	/// <remarks>Note that the ValidateSave() method in the ContentObject class also checks for the existence of 
	/// the thumbnail and optimized images. However, we need to do it here because the UpdateAuditFields method
	/// that is called after this function is executed updates the audit fields only when HasChanges = true. If 
	/// we don't check for these images, then the content object might have HasChanges = false, which causes the 
	/// audit fields to remain unchanged. But then if ValidateSave updates them, we'll get an error because the 
	/// ContentObject class doesn't update the audit fields (it knows nothing about the current user.)</remarks>
	private void evaluateOptimizedImage(Image contentObject) throws InvalidGalleryException{
		if (contentObject == null)
			return;

		// Check for existence of optimized image.
		if (!FileMisc.fileExists(contentObject.getOptimized().getFileNamePhysicalPath()))	{
			// Optimized image doesn't exist, but maybe we don't need it anyway. Check for this possibility.
			if (doesOriginalExceedOptimizedTriggers(contentObject))	{
				contentObject.setRegenerateOptimizedOnSave(true); // Yup, we need to generate the opt. image.
			}else{
				// The original isn't big enough to need an optimized image, so make sure the optimized properties
				// are the same as the original's properties.
				contentObject.getOptimized().setFileName(contentObject.getOriginal().getFileName());
				contentObject.getOptimized().setWidth(contentObject.getOriginal().getWidth());
				contentObject.getOptimized().setHeight(contentObject.getOriginal().getHeight());
				contentObject.getOptimized().setFileSizeKB(contentObject.getOriginal().getFileSizeKB());
			}
		}else{
			// We have an image where the optimized image exists. But perhaps the user changed some optimized trigger settings
			// and we no longer need the optimized image. Check for this possibility, and if true, update the optimized properties
			// to be the same as the original. Note: We only check if user selected the "overwrite compressed" option - this is 
			// because checking the dimensions of an image is very resource intensive, so we'll only do this if necessary.
			if (rebuildOptimized && !doesOriginalExceedOptimizedTriggers(contentObject)) {
				contentObject.getOptimized().setFileName(contentObject.getOriginal().getFileName());
				contentObject.getOptimized().setWidth(contentObject.getOriginal().getWidth());
				contentObject.getOptimized().setHeight(contentObject.getOriginal().getHeight());
				contentObject.getOptimized().setFileSizeKB(contentObject.getOriginal().getFileSizeKB());
			}
		}
	}

	//#endregion

	//#region Private Static Methods

	/// <summary>
	/// Update the width and height values to the default values specified for audio, and generic objects.
	/// This method has no effect on <see cref="Image"/>, <see cref="Video" /> or <see cref="ExternalContentObject"/> objects.
	/// </summary>
	/// <param name="contentObject">The <see cref="ContentObjectBo"/> whose <see cref="DisplayObject.Width"/> and 
	/// <see cref="DisplayObject.Height"/> properties of the <see cref="ContentObjectBo.Original"/> property is to be 
	/// updated with the current default values.</param>
	/// <remarks>We don't want to overwrite the width and height for videos because they may have been assigned valid
	/// values. See <see cref="ContentConversionQueue.GetTargetWidth" /> for how this can happen.</remarks>
	private void updateNonImageWidthAndHeight(ContentObjectBo contentObject) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		if ((contentObject instanceof GenericContentObject) && (contentObject.getMimeType().getTypeCategory() == MimeTypeCategory.Other)){
			// We want to update the width and height only when the TypeCategory is Other. If we don't check for this, we might
			// assign a width and height to a corrupt JPG that is being treated as a GenericContentObject.
			contentObject.getOriginal().setWidth(getGallerySettings().getDefaultGenericObjectWidth());
			contentObject.getOriginal().setHeight(getGallerySettings().getDefaultGenericObjectHeight());
		}else if (contentObject instanceof Audio){
			contentObject.getOriginal().setWidth(getGallerySettings().getDefaultAudioPlayerWidth());
			contentObject.getOriginal().setHeight(getGallerySettings().getDefaultAudioPlayerHeight());
		}
	}

	/// <summary>
	/// Updates the filename metadata item with the current file name. If the metadata item does not exist, no action is taken.
	/// </summary>
	/// <param name="contentObject">The content object.</param>
	private static void updateMetadataFilename(ContentObjectBo contentObject){
		ContentObjectMetadataItem metaItem;
		if ((metaItem = contentObject.getMetadataItems().tryGetMetadataItem(MetadataItemName.FileName)) != null){
			metaItem.setValue(contentObject.getOriginal().getFileName());
		}
	}

	//#endregion
}
