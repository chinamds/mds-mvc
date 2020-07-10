package com.mds.cm.content;

import static java.util.concurrent.CompletableFuture.runAsync;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.collections4.OrderedIterator;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.mds.common.Constants;
import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidContentObjectException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.cm.metadata.ContentObjectMetadataItem;
import com.mds.cm.model.ContentObject;
import com.mds.cm.model.GallerySetting;
import com.mds.core.CancelToken;
import com.mds.core.ContentObjectType;
import com.mds.core.ContentQueueItemConversionType;
import com.mds.core.ContentQueueItemStatus;
import com.mds.core.ContentQueueStatus;
import com.mds.core.MetadataItemName;
import com.mds.core.exception.ArgumentNullException;
import com.mds.core.exception.InvalidEnumArgumentException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.cm.util.CMUtils;
import com.mds.util.DateUtils;
import com.mds.util.FileMisc;
import com.mds.util.HelperFunctions;

/// <summary>
/// The Watermark class contains functionality for applying a text and/or image watermark to an image.
/// </summary>
public class ContentConversionQueue {

	//#region Private Static Fields

	private static volatile ContentConversionQueue instance;
	private static final Object sharedLock = new Object();

	//#endregion

	//#region Private Fields

	private long currentContentQueueId;
	private List<ContentEncoderSettings> attemptedEncoderSettings;

	//#endregion

	//#region Public Static Properties

	/// <summary>
	/// Gets a reference to the <see cref="ContentConversionQueue" /> singleton for this app domain.
	/// </summary>
	public static ContentConversionQueue getInstance(){
		if (instance == null){
			synchronized (sharedLock)
			{
				if (instance == null){
					ContentConversionQueue tempContentQueueItem = new ContentConversionQueue();
					instance = tempContentQueueItem;
				}
			}
		}

		return instance;
	}

	//#endregion

	//#region Properties

	/// <summary>
	/// Gets or sets the status of the media conversion queue.
	/// </summary>
	/// <value>The status of the media conversion queue.</value>
	public ContentQueueStatus Status;

	/// <summary>
	/// Gets the media items in the queue, including ones that have finished processing.
	/// </summary>
	/// <value>A collection of media queue items.</value>
	public Collection<ContentQueueItem> getContentQueueItems(){
		return ContentQueueItemDictionary.values();
	}

	/// <summary>
	/// Gets or sets an instance that can be used to cancel the media conversion process
	/// executing on the background thread.
	/// </summary>
	/// <value>An instance of <see cref="CancellationTokenSource" />.</value>
	//protected CancellationTokenSource CancelTokenSource { get; set; }
	protected CancelToken.Source CancelTokenSource;
	

	/// <summary>
	/// Gets or sets the media conversion task executing as an asynchronous operation.
	/// </summary>
	/// <value>An instance of <see cref="Task" />.</value>
	//protected Task Task { get; set; }
	protected Process Process;
	

	/// <summary>
	/// Gets the collection of encoder settings that have already been tried for the
	/// current media queue item.
	/// </summary>
	/// <value>An instance of <see cref="IContentEncoderSettingsCollection" />.</value>
	protected List<ContentEncoderSettings> getAttemptedEncoderSettings(){
		if (attemptedEncoderSettings == null)
			attemptedEncoderSettings = Lists.newArrayList();
		
		return attemptedEncoderSettings;
	}

	/// <summary>
	/// Gets or sets the media items in the queue, including ones that have finished processing.
	/// </summary>
	/// <value>A thread-safe dictionary of media queue items.</value>
	private ConcurrentHashMap<Long, ContentQueueItem> ContentQueueItemDictionary;

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="ContentConversionQueue"/> class.
	/// </summary>
	private ContentConversionQueue(){
		List<ContentQueueItem> items = CMUtils.loadContentQueues().stream().sorted(Comparator.comparing(ContentQueueItem::getDateAdded)).collect(Collectors.toList());

		ContentQueueItemDictionary = new ConcurrentHashMap<Long, ContentQueueItem>(items.stream().collect(Collectors.toMap(m->m.getId(), m->m))); //.ToDictionary(m -> m.ContentQueueId));

		reset();

		Status = ContentQueueStatus.Idle;
	}

	//#endregion

	//#region Public Methods

	/// <summary>
	/// Gets the specified media queue item or null if no item matching the ID exists.
	/// </summary>
	/// <param name="mediaQueueId">The media queue ID.</param>
	/// <returns>An instance of <see cref="ContentQueueItem" /> or null.</returns>
	public ContentQueueItem get(long mediaQueueId){
		return ContentQueueItemDictionary.get(mediaQueueId);
	}

	/// <summary>
	/// Adds the specified <paramref name="contentObject" /> to the queue. It will be processed in a first-in, first-out
	/// order. If the content object is already waiting in the queue, no action is taken.
	/// </summary>
	/// <param name="contentObject">The content object to be processed.</param>
	/// <param name="conversionType">Type of the conversion.</param>
	public void add(ContentObjectBo contentObject, ContentQueueItemConversionType conversionType)	{
		synchronized (sharedLock){
			ContentQueueItem mqItem = new ContentQueueItem();
			mqItem.ContentObjectId = contentObject.getId();
			mqItem.Status = ContentQueueItemStatus.Waiting;
			mqItem.ConversionType = conversionType;
			mqItem.RotationAmount = contentObject.calculateNeededRotation();
			mqItem.StatusDetail = "";
			mqItem.DateAdded = Calendar.getInstance().getTime();
			mqItem.DateConversionStarted = null;
			mqItem.DateConversionCompleted = null;

			CMUtils.saveContentQueue(mqItem);
			ContentQueueItemDictionary.put(mqItem.ContentQueueId, mqItem);
		}
	}

	/// <summary>
	/// Removes the item from the queue. If the item is currently being processed, the task
	/// is cancelled.
	/// </summary>
	/// <param name="contentObjectId">The content object ID.</param>
	public void remove(long contentObjectId) throws InterruptedException{
		List<ContentQueueItem> items = ContentQueueItemDictionary.values().stream().filter(m -> m.ContentObjectId == contentObjectId).collect(Collectors.toList());
		for (ContentQueueItem item : items){
			removeContentQueueItem(item.ContentQueueId);
		}
	}

	/// <summary>
	/// Removes the item from the queue. If the item is currently being processed, the task
	/// is cancelled.
	/// </summary>
	/// <param name="mediaQueueId">The media queue ID.</param>
	public void removeContentQueueItem(long mediaQueueId) throws InterruptedException{
		ContentQueueItem item;
		if ((item = ContentQueueItemDictionary.get(mediaQueueId)) != null)	{
			ContentQueueItem currentItem = getCurrentContentQueueItem();
			if ((currentItem != null) && (currentItem.ContentQueueId == mediaQueueId)){
				CancelTokenSource.cancel();

				if (Process != null){
					Process.wait(20000); // Wait up to 20 seconds
				}

				getInstance().Status = ContentQueueStatus.Idle;
			}

			//CMUtils.GetDataProvider().ContentQueueItem_Delete(item);
			CMUtils.deleteContentQueue(item);

			ContentQueueItemDictionary.remove(mediaQueueId);
		}
	}

	/// <summary>
	/// Deletes all queue items older than 180 days.
	/// </summary>
	public void deleteOldQueueItems() throws InterruptedException{
		Date purgeDate = DateUtils.getDateStart(DateUtils.addDays(DateUtils.Now(), -180));

		List<ContentQueueItem> items = ContentQueueItemDictionary.values().stream().filter(m -> m.DateAdded.before(purgeDate)).collect(Collectors.toList());
		for (ContentQueueItem item : items)	{
			removeContentQueueItem(item.ContentQueueId);
		}
	}

	/// <summary>
	/// Processes the items in the queue asyncronously. If the instance is already processing 
	/// items, no additional action is taken.
	/// </summary>
	public void process(){
		if (FFmpegWrapper.isAvailable()){
			processNextItemInQueue(true);
		}
	}

	/// <summary>
	/// Gets the media item currently being processed. If no item is being processed, the value 
	/// will be null.
	/// </summary>
	/// <returns>Returns the media item currently being processed, or null if no items are being processed.</returns>
	public ContentQueueItem getCurrentContentQueueItem()	{
		return ContentQueueItemDictionary.get(currentContentQueueId);
	}

	/// <summary>
	/// Determines whether the specified content object undergoing the specified <paramref name="conversionType" /> 
	/// is currently being processed by the media queue or is waiting in the queue.
	/// </summary>
	/// <param name="contentObjectId">The ID of the content object.</param>
	/// <param name="conversionType">Type of the conversion. If the parameter is omitted, then a matching 
	/// content object having any conversion type will cause the method to return <c>true</c>.</param>
	/// <returns>Returns <c>true</c> if the content object is currently being processed by the media queue
	/// or is waiting in the queue; otherwise, <c>false</c>.</returns>
	public boolean isWaitingInQueueOrProcessing(long contentObjectId) {
		return isWaitingInQueueOrProcessing(contentObjectId, ContentQueueItemConversionType.Unknown);
	}
	
	public boolean isWaitingInQueueOrProcessing(long contentObjectId, ContentQueueItemConversionType conversionType){
		ContentQueueItem item = getCurrentContentQueueItem();

		if ((item != null) && item.ContentObjectId == contentObjectId && (item.ConversionType == conversionType 
				|| conversionType == ContentQueueItemConversionType.Unknown))
			return true;
		else
			return isWaitingInQueue(contentObjectId, conversionType);
	}

	/// <summary>
	/// Determines whether the specified content object has an applicable encoder setting.
	/// </summary>
	/// <param name="contentObject">The content object.</param>
	/// <returns>
	/// 	<c>true</c> if the content object has an encoder setting; otherwise, <c>false</c>.
	/// </returns>
	public boolean hasEncoderSetting(ContentObjectBo contentObject) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		// Return true if the encoder args for the first match has a value; otherwise false.
		return getEncoderSettings(contentObject.getOriginal().getMimeType(), contentObject.getGalleryId()).stream()
			.anyMatch(encoderSetting -> !StringUtils.isBlank(encoderSetting.getEncoderArguments()));
	}

	//#endregion

	//#region Private Methods

	/// <summary>
	/// Determines whether the specified content object undergoing the specified <paramref name="conversionType" /> 
	/// is currently waiting to be processed in the media queue.
	/// </summary>
	/// <param name="contentObjectId">The content object ID.</param>
	/// <param name="conversionType">Type of the conversion. If the parameter omitted, then a matching 
	/// content object having any conversion type will cause the method to return <c>true</c>.</param>
	/// <returns>Returns <c>true</c> if the content object is currently being processed by the media queue;
	/// otherwise, <c>false</c>.</returns>
	private boolean isWaitingInQueue(long contentObjectId){
		return isWaitingInQueue(contentObjectId, ContentQueueItemConversionType.Unknown);
	}
	
	private boolean isWaitingInQueue(long contentObjectId, ContentQueueItemConversionType conversionType){
		return ContentQueueItemDictionary.values().stream().anyMatch(mq ->
			mq.ContentObjectId == contentObjectId &&
			mq.Status == ContentQueueItemStatus.Waiting &&
			(mq.ConversionType == conversionType || conversionType == ContentQueueItemConversionType.Unknown)
			);
	}

	/// <summary>
	/// Processes the next item in the queue. If the instance is already processing items, the 
	/// action is canceled.
	/// </summary>
	private void processNextItemInQueue(boolean useBackgroundThread){
		if (Status == ContentQueueStatus.Processing)
			return;

		reset();

		ContentQueueItem mqItem = getNextItemInQueue();

		if (mqItem == null)
			return;

		// We have an item to process.
		Status = ContentQueueStatus.Processing;
		this.currentContentQueueId = mqItem.ContentQueueId;

		//CancelTokenSource = new CancellationTokenSource();
		CancelTokenSource = new CancelToken.Source();

		if (useBackgroundThread) {
			//Task = Task.CMUtils.StartNew(ProcessItem);
			runAsync(new Runnable() {
		           public void run() {
		        	   processItem();
				   }
			}); 
		}else {
			processItem();
		}
	}

	/// <summary>
	/// Processes the current media queue item. This can be a long running process and is 
	/// intended to be invoked on a background thread.
	/// </summary>
	private void processItem(){
		try	{
			if (!beginProcessItem())
				return;

			ContentConversionSettings conversionResults = executeContentConversion();

			onContentConversionComplete(conversionResults);
		}catch (Exception ex){
			ex.printStackTrace();
			// I know it's bad form to catch all exceptions, but I don't know how to catch all
			// non-fatal exceptions (like ArgumentNullException) while letting the catastrophic
			// ones go through (like StackOverFlowException) unless we explictly catch and then
			// rethrow them, but that seems like it could have its own issues.
			//EventLogs.EventLogController.RecordError(ex, AppSetting.Instance, null, CMUtils.LoadGallerySettings());
		}finally	{
			getInstance().Status = ContentQueueStatus.Idle;
		}

		processNextItemInQueue(false);
	}

	/// <summary>
	/// Executes the actual media conversion, returning an object that contains settings and the 
	/// results of the conversion. Returns null if the content object has been deleted since it was
	/// first put in the queue.
	/// </summary>
	/// <returns>Returns an instance of <see cref="ContentConversionSettings" /> containing settings and
	/// results used in the conversion, or null if the content object no longer exists.</returns>
	private ContentConversionSettings executeContentConversion() throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		ContentObjectBo contentObject;
		try
		{
			ContentQueueItem queueItem = getCurrentContentQueueItem();
			contentObject = CMUtils.loadContentObjectInstance(queueItem.ContentObjectId, true);
			contentObject.setRotation(queueItem.RotationAmount);
		}catch (InvalidContentObjectException | UnsupportedContentObjectTypeException | InvalidAlbumException | UnsupportedImageTypeException | InvalidGalleryException ex){
			return null;
		}

		return executeContentConversion(contentObject, getEncoderSetting(contentObject));
	}

	/// <summary>
	/// Executes the actual media conversion, returning an object that contains settings and the
	/// results of the conversion.
	/// </summary>
	/// <param name="contentObject">The content object.</param>
	/// <param name="encoderSetting">The encoder setting that defines the conversion parameters.</param>
	/// <returns>
	/// Returns an instance of <see cref="ContentConversionSettings"/> containing settings and
	/// results used in the conversion.
	/// </returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="contentObject" /> or
	/// <paramref name="encoderSetting" /> is null.</exception>
	private ContentConversionSettings executeContentConversion(ContentObjectBo contentObject, ContentEncoderSettings encoderSetting) throws UnsupportedContentObjectTypeException, InvalidGalleryException	{
		if (contentObject == null)
			throw new ArgumentNullException("contentObject");

		if (encoderSetting == null)
			throw new ArgumentNullException("encoderSetting");

		ContentQueueItem mqi = getCurrentContentQueueItem();

		switch (mqi.ConversionType)	{
			case CreateOptimized:
				return createOptimizedContentObject(contentObject, encoderSetting);

			case RotateVideo:
				return rotateVideo(contentObject);

			default:
				throw new InvalidEnumArgumentException(MessageFormat.format("ContentConversionQueue.ExecuteContentConversion is not designed to handled the enumeration value {0}. It must be updated.", mqi.ConversionType));
		}
	}

	private ContentConversionSettings rotateVideo(ContentObjectBo contentObject) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		GallerySettings gallerySetting = CMUtils.loadGallerySetting(contentObject.getGalleryId());

		// Determine file name and path of the new file.
		String dirName = FilenameUtils.getFullPathNoEndSeparator(contentObject.getOriginal().getFileNamePhysicalPath());
		if (dirName == null)
			dirName = StringUtils.EMPTY;
		String newFilename = HelperFunctions.validateFileName(dirName, contentObject.getOriginal().getFileName());
		String newFilePath = FilenameUtils.concat(dirName, newFilename);

		final String args = "-i \"\"{SourceFilePath}\"\" -vf \"\"{AutoRotateFilter}\"\" -q:a 0 -q:v 0 -acodec copy -metadata:s:v:0 rotate=0 \"\"{DestinationFilePath}\"\"";
		ContentEncoderSettings encoderSetting = new ContentEncoderSettings(FileMisc.getExt(newFilename), FileMisc.getExt(contentObject.getOriginal().getFileName()), args, 0);

		ContentConversionSettings mediaSettings = new ContentConversionSettings();
		mediaSettings.FilePathSource = contentObject.getOriginal().getFileNamePhysicalPath();
		mediaSettings.FilePathDestination = newFilePath;
		mediaSettings.EncoderSetting = encoderSetting;
		mediaSettings.GalleryId = contentObject.getGalleryId();
		mediaSettings.ContentQueueId = currentContentQueueId;
		mediaSettings.TimeoutMs = gallerySetting.getContentEncoderTimeoutMs();
		mediaSettings.ContentObjectId = contentObject.getId();
		mediaSettings.TargetWidth = 0;
		mediaSettings.TargetHeight = 0;
		mediaSettings.FFmpegArgs = StringUtils.EMPTY;
		mediaSettings.FFmpegOutput = StringUtils.EMPTY;
		//mediaSettings.CancellationToken = CancelTokenSource.Token;
		mediaSettings.CancellationToken = CancelTokenSource.getToken();

		mediaSettings.FFmpegOutput = FFmpegWrapper.createContent(mediaSettings);
		mediaSettings.FileCreated = validateFile(mediaSettings.FilePathDestination);

		return mediaSettings;
	}

	private ContentConversionSettings createOptimizedContentObject(ContentObjectBo contentObject, ContentEncoderSettings encoderSetting) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		getAttemptedEncoderSettings().add(encoderSetting);

		GallerySettings gallerySetting = CMUtils.loadGallerySetting(contentObject.getGalleryId());

		// Determine file name and path of the new file.
		String optimizedPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(contentObject.getOriginal().getFileInfo().getParent(), gallerySetting.getFullOptimizedPath(), gallerySetting.getFullContentObjectPath());
		String fileNameWithoutExtension = FilenameUtils.getBaseName(contentObject.getOriginal().getFileInfo().getName());
		String newFilename = generateNewFilename(optimizedPath, fileNameWithoutExtension, encoderSetting.getDestinationFileExtension(), gallerySetting.getOptimizedFileNamePrefix());
		String newFilePath = FilenameUtils.concat(optimizedPath, newFilename);

		ContentConversionSettings mediaSettings = new ContentConversionSettings();
		mediaSettings.FilePathSource = contentObject.getOriginal().getFileNamePhysicalPath();
		mediaSettings.FilePathDestination = newFilePath;
		mediaSettings.EncoderSetting = encoderSetting;
		mediaSettings.GalleryId = contentObject.getGalleryId();
		mediaSettings.ContentQueueId = currentContentQueueId;
		mediaSettings.TimeoutMs = gallerySetting.getContentEncoderTimeoutMs();
		mediaSettings.ContentObjectId = contentObject.getId();
		mediaSettings.TargetWidth = getTargetWidth(contentObject, gallerySetting, encoderSetting);
		mediaSettings.TargetHeight = getTargetHeight(contentObject, gallerySetting, encoderSetting);
		mediaSettings.FFmpegArgs = StringUtils.EMPTY;
		mediaSettings.FFmpegOutput = StringUtils.EMPTY;
		mediaSettings.CancellationToken = CancelTokenSource.getToken();

		mediaSettings.FFmpegOutput = FFmpegWrapper.createContent(mediaSettings);
		mediaSettings.FileCreated = validateFile(mediaSettings.FilePathDestination);

		if (!mediaSettings.FileCreated){
			// Could not create the requested version of the file. Record the event, then try again,
			// using the next encoder setting (if one exists).
			String msg = MessageFormat.format("FAILURE: FFmpeg was not able to create file '{0}'.", FilenameUtils.getName(mediaSettings.FilePathDestination));
			recordEvent(msg, mediaSettings);

			ContentEncoderSettings nextEncoderSetting = getEncoderSetting(contentObject);
			if (nextEncoderSetting != null)	{
				return executeContentConversion(contentObject, nextEncoderSetting);
			}
		}

		return mediaSettings;
	}

	/// <summary>
	/// Gets the encoder setting to use for processing the <paramref name="contentObject" />.
	/// If more than one encoder setting is applicable, this function automatically returns 
	/// the first item that has not yet been tried. If no items are applicable, returns
	/// null.
	/// </summary>
	/// <param name="contentObject">The content object.</param>
	/// <returns>An instance of <see cref="ContentEncoderSettings" /> or null.</returns>
	private ContentEncoderSettings getEncoderSetting(ContentObjectBo contentObject) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		List<ContentEncoderSettings> encoderSettings = getEncoderSettings(contentObject.getOriginal().getMimeType(), contentObject.getGalleryId());

		return encoderSettings.stream().filter(encoderSetting -> attemptedEncoderSettings.stream().allMatch(es -> es.getSequence() != encoderSetting.getSequence())).findFirst().orElse(null);
	}

	private static List<ContentEncoderSettings> getEncoderSettings(MimeTypeBo mimeType, long galleryId) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		return CMUtils.loadGallerySetting(galleryId).getContentEncoderSettings().stream()
			.filter(es -> ((es.getSourceFileExtension().equals(mimeType.getExtension())) ||
							(es.getSourceFileExtension().equals(StringUtils.join(new String[] {"*", mimeType.getMajorType()})))))
			.sorted(Comparator.comparing(ContentEncoderSettings::getSequence)).collect(Collectors.toList());
	}

	/// <summary>
	/// Performs post-processing tasks on the content object and media queue items. Specifically, 
	/// if the file was successfully created, updates the content object instance with information 
	/// about the new file. Updates the media queue instance and resets the status of the 
	/// conversion queue.
	/// </summary>
	/// <param name="settings">An instance of <see cref="ContentConversionSettings" /> containing
	/// settings and results used in the conversion. May be null.</param>
	private void onContentConversionComplete(ContentConversionSettings settings) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, IOException, InvalidGalleryException{
		try	{
			switch (getCurrentContentQueueItem().ConversionType){
				case CreateOptimized:
					onContentConversionCompleteOptimizedCreated(settings);
					break;
				case RotateVideo:
					onContentConversionCompleteVideoRotated(settings);
					break;
			}
		}finally{
			HelperFunctions.purgeCache();

			completeProcessItem(settings);
		}
	}

	/// <summary>
	/// Performs post-processing tasks on the content object after an optimized file has been created. Specifically, 
	/// if the file was successfully created, update the content object instance with information 
	/// about the new file. No action is taken if <paramref name="settings" /> is null.
	/// </summary>
	/// <param name="settings">An instance of <see cref="ContentConversionSettings" /> containing
	/// settings and results used in the conversion. May be null.</param>
	private static void onContentConversionCompleteOptimizedCreated(ContentConversionSettings settings) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, IOException, InvalidGalleryException	{
		if (settings == null)
			return;

		ContentObjectBo contentObject = CMUtils.loadContentObjectInstance(settings.ContentObjectId, true);

		// Step 1: Update the content object with info about the newly created file.
		if (settings.FileCreated){
			String msg = MessageFormat.format("FFmpeg created file '{0}'.", FilenameUtils.getName(settings.FilePathDestination));
			recordEvent(msg, settings);

			if (contentObject.getContentObjectType() == ContentObjectType.Video){
				int width = FFmpegWrapper.parseOutputVideoWidth(settings.FFmpegOutput);
				int height = FFmpegWrapper.parseOutputVideoHeight(settings.FFmpegOutput);

				if (width > Integer.MIN_VALUE)
					contentObject.getOptimized().setWidth(width);

				if (height > Integer.MIN_VALUE)
					contentObject.getOptimized().setHeight(height);
			}else{
				contentObject.getOptimized().setWidth(settings.TargetWidth);
				contentObject.getOptimized().setHeight(settings.TargetHeight);
			}

			// Step 2: If we already had an optimized file and we just created a second one, delete the first one
			// and rename the new one to match the first one.
			boolean optFileDifferentThanOriginal = !StringUtils.equalsIgnoreCase(contentObject.getOptimized().getFileName(), contentObject.getOriginal().getFileName());
			boolean optFileDifferentThanCreatedFile = !StringUtils.equalsIgnoreCase(contentObject.getOptimized().getFileName(), FilenameUtils.getName(settings.FilePathDestination));

			if (optFileDifferentThanOriginal && optFileDifferentThanCreatedFile && FileMisc.fileExists(contentObject.getOptimized().getFileNamePhysicalPath()))	{
				String curFilePath = contentObject.getOptimized().getFileNamePhysicalPath();
				FileMisc.deleteFile(curFilePath);

				boolean optFileExtDifferentThanCreatedFileExt = !FileMisc.getExt(curFilePath).equalsIgnoreCase(FileMisc.getExt(settings.FilePathDestination));
				if (optFileExtDifferentThanCreatedFileExt){
					// Extension of created file is different than current optimized file. This can happen, for example, when syncing after
					// changing encoder settings to produce MP4's instead of FLV's. Use the filename of the current optimized file and combine
					// it with the extension of the created file.
					String newOptFileName = FilenameUtils.concat(FilenameUtils.getBaseName(curFilePath), FileMisc.getExt(settings.FilePathDestination));
					String newOptFilePath = StringUtils.join(new String[] {FilenameUtils.getFullPathNoEndSeparator(curFilePath), File.separator, newOptFileName});

					if (!settings.FilePathDestination.equalsIgnoreCase(newOptFilePath))	{
						// Calculated file name differs from the one that was generated, so rename it, deleting any existing file first.
						if (FileMisc.fileExists(newOptFilePath)){
							FileMisc.deleteFile(newOptFilePath);
						}

						FileMisc.moveFile(settings.FilePathDestination, newOptFilePath);
						settings.FilePathDestination = newOptFilePath;
					}

					contentObject.getOptimized().setFileName(newOptFileName);
					contentObject.getOptimized().setFileNamePhysicalPath(newOptFilePath);
				}else{
					FileMisc.moveFile(settings.FilePathDestination, curFilePath);
					settings.FilePathDestination = curFilePath;
				}
			}else{
				// We typically get here when the content object is first added.
				contentObject.getOptimized().setFileName(FilenameUtils.getName(settings.FilePathDestination));
				contentObject.getOptimized().setFileNamePhysicalPath(settings.FilePathDestination);
			}

			// Now that we have the optimized file name all set, grab it's size.
			int fileSize = (int)(contentObject.getOptimized().getFileInfo().length() / 1024);
			contentObject.getOptimized().setFileSizeKB((fileSize < 1 ? 1 : fileSize)); // Very small files should be 1, not 0.
		}

		// Step 3: Save and finish up.
		contentObject.setLastModifiedByUserName(Constants.SystemUserName);
		contentObject.setDateLastModified(Calendar.getInstance().getTime());
		contentObject.save();
	}

	/// <summary>
	/// Performs post-processing tasks on the content object after a video has been rotated. Specifically, 
	/// if the file was successfully created, update the content object instance with information 
	/// about the new file. No action is taken if <paramref name="settings" /> is null.
	/// </summary>
	/// <param name="settings">An instance of <see cref="ContentConversionSettings" /> containing
	/// settings and results used in the conversion. May be null.</param>
	/// <remarks>This function is invoked only when a video is manually rotated by the user, and 
	/// only for the original video file. Videos that are auto-rotated will be the optimized ones
	/// and will end up running the <see cref="OnContentConversionCompleteOptimizedCreated(ContentConversionSettings)" />
	/// function instead of this one.</remarks>
	private static void onContentConversionCompleteVideoRotated(ContentConversionSettings settings) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, IOException, InvalidGalleryException	{
		if (settings == null)
			return;

		ContentObjectBo contentObject = CMUtils.loadContentObjectInstance(settings.ContentObjectId, true);
		if (settings.FileCreated){
			String msg = MessageFormat.format("FFmpeg created file '{0}'.", FilenameUtils.getName(settings.FilePathDestination));
			recordEvent(msg, settings);

			// Step 1: Update the width and height of the original video file, if we have that info.
			int originalWidth = FFmpegWrapper.parseOutputVideoWidth(settings.FFmpegOutput);
			int originalHeight = FFmpegWrapper.parseOutputVideoHeight(settings.FFmpegOutput);

			if (originalWidth > Integer.MIN_VALUE)
				contentObject.getOriginal().setWidth(originalWidth);

			if (originalHeight > Integer.MIN_VALUE)
				contentObject.getOriginal().setHeight(originalHeight);

			// Step 2: Delete the original file and rename the new one to match the original.
			if ((settings.FilePathDestination != contentObject.getOriginal().getFileNamePhysicalPath()) && FileMisc.fileExists(contentObject.getOriginal().getFileNamePhysicalPath())){
				String curFilePath = contentObject.getOriginal().getFileNamePhysicalPath();
				FileMisc.deleteFile(curFilePath);
				FileMisc.moveFile(settings.FilePathDestination, curFilePath);
				settings.FilePathDestination = curFilePath;
			}else{
				// I don't expect we'll ever get here, but just to be safe...
				contentObject.getOriginal().setFileName(FilenameUtils.getName(settings.FilePathDestination));
				contentObject.getOriginal().setFileNamePhysicalPath(settings.FilePathDestination);
			}

			int fileSize = (int)(contentObject.getOriginal().getFileInfo().length() / 1024);
			contentObject.getOriginal().setFileSizeKB((fileSize < 1 ? 1 : fileSize)); // Very small files should be 1, not 0.

			refreshOriginalVideoMetadata(contentObject);
		}

		// Step 3: Save and finish up.
		contentObject.setLastModifiedByUserName(Constants.SystemUserName);
		contentObject.setDateLastModified(Calendar.getInstance().getTime());
		contentObject.setRegenerateOptimizedOnSave( true);
		contentObject.setRegenerateThumbnailOnSave(true);
		contentObject.save();
	}

	/// <summary>
	/// Complete processing the current media item by updating the media queue instance and 
	/// reseting the status of the conversion queue.
	/// </summary>
	/// <param name="settings">An instance of <see cref="ContentConversionSettings" /> containing 
	/// settings and results used in the conversion. A null value is acceptable.</param>
	private void completeProcessItem(ContentConversionSettings settings){
		// Update status and persist to data store
		ContentQueueItem mqItem = getCurrentContentQueueItem();

		mqItem.DateConversionCompleted = Calendar.getInstance().getTime();
		if (settings != null && settings.FileCreated){
			mqItem.Status = ContentQueueItemStatus.Complete;
		}else{
			mqItem.Status = ContentQueueItemStatus.Error;

			String fileName = (settings != null && !StringUtils.isBlank(settings.FilePathSource) ? FilenameUtils.getName(settings.FilePathSource) : "<Unknown>");
			String msg = MessageFormat.format("Unable to process file '{0}'.", fileName);
			recordEvent(msg, settings);
		}

		//CMUtils.GetDataProvider().ContentQueueItem_Save(mediaQueueDto);
		CMUtils.saveContentQueue(mqItem);

		// Update the item in the collection.
		//ContentQueueItems[mediaQueueDto.ContentQueueId] = mediaQueueDto;

		reset();
	}

	/// <summary>
	/// Begins processing the current media item, returning <c>true</c> when the action succeeds. 
	/// Specifically, a few properties are updated and the item is persisted to the data store.
	/// If the item cannot be processed (may be null or has a status other than 'Waiting'), this
	/// function returns <c>false</c>.
	/// </summary>
	/// <returns>Returns <c>true</c> when the item has successfully started processing; otherwise 
	/// <c>false</c>.</returns>
	private boolean beginProcessItem(){
		ContentQueueItem mqItem = getCurrentContentQueueItem();

		if (mqItem == null)
			return false;

		if (!mqItem.Status.equals(ContentQueueItemStatus.Waiting)){
			processNextItemInQueue(false);
			return false;
		}

		mqItem.Status = ContentQueueItemStatus.Processing;
		mqItem.DateConversionStarted = Calendar.getInstance().getTime();

		//CMUtils.GetDataProvider().ContentQueueItem_Save(mediaQueueDto);
		CMUtils.saveContentQueue(mqItem);

		// Update the item in the collection.
		//ContentQueueItems[mediaQueueDto.ContentQueueId] = mediaQueueDto;

		return true;
	}

	/// <summary>
	/// Determine name of new file and ensure it is unique in the directory.
	/// </summary>
	/// <param name="dirPath">The path to the directory where the file is to be created.</param>
	/// <param name="fileNameWithoutExtension">The file name without extension.</param>
	/// <param name="fileExtension">The file extension.</param>
	/// <param name="filenamePrefix">A string to prepend to the filename. Example: "zThumb_"</param>
	/// <returns>
	/// Returns the name of the new file name and ensures it is unique in the directory.
	/// </returns>
	private static String generateNewFilename(String dirPath, String fileNameWithoutExtension, String fileExtension, String filenamePrefix)	{
		String optimizedFilename = StringUtils.join(new String[] {filenamePrefix, fileNameWithoutExtension, fileExtension});

		optimizedFilename = HelperFunctions.validateFileName(dirPath, optimizedFilename);

		return optimizedFilename;
	}

	/// <summary>
	/// Gets the next item in the queue with a status of <see cref="ContentQueueItemStatus.Waiting" />,
	/// returning null if the queue is empty or no eligible items exist.
	/// </summary>
	/// <returns>Returns an instance of <see cref="ContentQueueItem" />, or null.</returns>
	private ContentQueueItem getNextItemInQueue(){
		return ContentQueueItemDictionary.values().stream().sorted(Comparator.comparing(ContentQueueItem::getDateAdded))
				.filter(m -> m.Status == ContentQueueItemStatus.Waiting).findFirst().orElse(null);
	}

	/// <summary>
	/// Validate the specified file, returning <c>true</c> if it exists and has a non-zero length;
	/// otherwise returning <c>false</c>. If the file exists but the length is zero, it is deleted.
	/// </summary>
	/// <param name="filePath">The full path to the file.</param>
	/// <returns>Returns <c>true</c> if <paramref name="filePath" /> exists and has a non-zero length;
	/// otherwise returns <c>false</c>.</returns>
	private static boolean validateFile(String filePath){
		if (FileMisc.fileExists(filePath))	{
			File fi = new File(filePath);

			if (fi.length() > 0)
				return true;
			else{
				fi.delete();
				return false;
			}
		}else
			return false;
	}

	private static void recordEvent(String msg, ContentConversionSettings settings)	{
		Long galleryId = null;
		Map<String, String> data = null;

		if (settings != null){
			galleryId = settings.GalleryId;

			data = new HashMap<String, String>();
			data.put("FFmpeg args", settings.FFmpegArgs);
			data.put("FFmpeg output", settings.FFmpegOutput);
			data.put("StackTrace", ArrayUtils.toString(Thread.currentThread().getStackTrace())); //new Throwable().getStackTrace() 
		}

		//EventLogs.EventLogController.recordEvent(msg, EventType.Info, galleryId, CMUtils.LoadGallerySettings(), AppSetting.Instance, data);
	}

	/// <summary>
	/// Update settings to prepare for the conversion of a media item.
	/// </summary>
	private void reset(){
		currentContentQueueId = Long.MIN_VALUE;
		getAttemptedEncoderSettings().clear();

		// Update the status of any 'Processing' items to 'Waiting'. This is needed to reset any items that 
		// were being processed but were never finished (this can happen if the app pool recycles).
		List<ContentQueueItem> items =  ContentQueueItemDictionary.values().stream().filter(m -> m.Status == ContentQueueItemStatus.Processing).collect(Collectors.toList());
		for(ContentQueueItem item : items){
			changeStatus(item, ContentQueueItemStatus.Waiting);
		}
	}

	/// <summary>
	/// Update the status of the <paramref name="item" /> to the specified <paramref name="status" />.
	/// </summary>
	/// <param name="item">The item whose status is to be updated.</param>
	/// <param name="status">The status to update the item to.</param>
	private static void changeStatus(ContentQueueItem item, ContentQueueItemStatus status){
		item.Status = status;
		CMUtils.saveContentQueue(item);
	}

	/// <summary>
	/// Gets the target width for the optimized version of the <paramref name="contentObject"/>. This value is applied to 
	/// the {Width} replacement parameter in the encoder settings, if present. The first matching rule is returned:
	/// 1. The <paramref name="contentObject" /> has a width meta value.
	/// 2. The width of the original file (videos only).
	/// 3. The default value for the media type (e.g. <see cref="GallerySetting.DefaultVideoPlayerWidth" /> for video and
	/// cref="GallerySetting.DefaultAudioPlayerWidth" /> for audio).
	/// </summary>
	/// <param name="contentObject">The content object.</param>
	/// <param name="gallerySettings">The gallery settings.</param>
	/// <param name="encoderSetting">An instance of <see cref="ContentEncoderSettings" />.</param>
	/// <returns>System.Int32.</returns>
	/// <exception cref="System.ComponentModel.InvalidEnumArgumentException">Thrown when the <paramref name="contentObject" /> is not a
	/// video, audio, or generic item.</exception>
	private static int getTargetWidth(ContentObjectBo contentObject, GallerySettings gallerySettings, ContentEncoderSettings encoderSetting){
		ContentObjectMetadataItem miWidth;
		if ((miWidth = contentObject.getMetadataItems().tryGetMetadataItem(MetadataItemName.Width)) != null){
			int width  = HelperFunctions.parseInteger(miWidth.getValue());
			if (width > 0)
				return width;
		}

		switch (contentObject.getContentObjectType()){
			case Video:
				int width = FFmpegWrapper.parseSourceVideoWidth(FFmpegWrapper.getOutput(contentObject.getOriginal().getFileNamePhysicalPath(), contentObject.getGalleryId()));

				return width > Integer.MIN_VALUE ? width : gallerySettings.getDefaultVideoPlayerWidth();

			case Audio:
				return gallerySettings.getDefaultAudioPlayerWidth();

			case Generic: // Should never hit this because we don't encode generic objects, but for completeness let's put it in
				return gallerySettings.getDefaultGenericObjectWidth();

			default:
				throw new InvalidEnumArgumentException(MessageFormat.format("ContentConversionQueue.GetTargetWidth was not designed to handle the enum value {0}. The function must be updated.", contentObject.getContentObjectType()));
		}
	}

	/// <summary>
	/// Gets the target height for the optimized version of the <paramref name="contentObject"/>. This value is applied to 
	/// the {Height} replacement parameter in the encoder settings, if present. The first matching rule is returned:
	/// 1. The <paramref name="contentObject" /> has a height meta value.
	/// 2. The height of the original file (videos only).
	/// 3. The default value for the media type (e.g. <see cref="GallerySetting.DefaultVideoPlayerHeight" /> for video and
	/// cref="GallerySetting.DefaultAudioPlayerHeight" /> for audio).
	/// </summary>
	/// <param name="contentObject">The content object.</param>
	/// <param name="gallerySettings">The gallery settings.</param>
	/// <param name="encoderSetting">An instance of <see cref="ContentEncoderSettings" />.</param>
	/// <returns>System.Int32.</returns>
	/// <exception cref="System.ComponentModel.InvalidEnumArgumentException">Thrown when the <paramref name="contentObject" /> is not a
	/// video, audio, or generic item.</exception>
	private static int getTargetHeight(ContentObjectBo contentObject, GallerySettings gallerySettings, ContentEncoderSettings encoderSetting){
		ContentObjectMetadataItem miHeight;
		if ((miHeight = contentObject.getMetadataItems().tryGetMetadataItem(MetadataItemName.Height)) != null){
			int height  = HelperFunctions.parseInteger(miHeight.getValue());
			if (height > 0)
				return height;
		}

		switch (contentObject.getContentObjectType()){
			case Video:
				int height = FFmpegWrapper.parseSourceVideoHeight(FFmpegWrapper.getOutput(contentObject.getOriginal().getFileNamePhysicalPath(), contentObject.getGalleryId()));

				return height > Integer.MIN_VALUE ? height : contentObject.getOriginal().getHeight();

			case Audio:
				return gallerySettings.getDefaultAudioPlayerHeight();

			case Generic: // Should never hit this because we don't encode generic objects, but for completeness let's put it in
				return gallerySettings.getDefaultGenericObjectHeight();

			default:
				throw new InvalidEnumArgumentException(MessageFormat.format("ContentConversionQueue.GetTargetHeight was not designed to handle the enum value {0}. The function must be updated.", contentObject.getContentObjectType()));
		}
	}

	/// <summary>
	/// Re-extract several metadata values from the file. Call this function when performing an action on a file
	/// that may render existing metadata items inaccurate, such as width and height. The new values are not persisted;
	/// it is expected a subsequent function will do that.
	/// </summary>
	private static void refreshOriginalVideoMetadata(ContentObjectBo contentObject) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		
		contentObject.extractMetadata(contentObject.getMetaDefinitions().find(MetadataItemName.Width));
		contentObject.extractMetadata(contentObject.getMetaDefinitions().find(MetadataItemName.Height));
		contentObject.extractMetadata(contentObject.getMetaDefinitions().find(MetadataItemName.VideoFormat));
		contentObject.extractMetadata(contentObject.getMetaDefinitions().find(MetadataItemName.BitRate));
		contentObject.extractMetadata(contentObject.getMetaDefinitions().find(MetadataItemName.VideoBitRate));
		contentObject.extractMetadata(contentObject.getMetaDefinitions().find(MetadataItemName.Dimensions));
		contentObject.extractMetadata(contentObject.getMetaDefinitions().find(MetadataItemName.FileSizeKb));
		contentObject.extractMetadata(contentObject.getMetaDefinitions().find(MetadataItemName.Orientation));
	}

	//#endregion
}
