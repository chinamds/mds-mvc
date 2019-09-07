package com.mds.cm.content;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.mds.cm.exception.SynchronizationInProgressException;
import com.mds.cm.util.CMUtils;
import com.mds.common.exception.RecordExistsException;
import com.mds.core.SynchronizationState;
import com.mds.core.exception.ArgumentOutOfRangeException;
import com.mds.i18n.util.I18nUtils;
import com.mds.util.DateUtils;
import com.mds.util.StringUtils;

/// <summary>
/// Provides functionality for retrieving and storing the status of a synchronization.
/// </summary>
/// <remarks>This class is managed as a singleton on a per gallery instance, which means only once instance exists for 
/// each gallery in the current app domain.</remarks>
public class SynchronizationStatus {
	//#region Private fields
	
	private final Object lockObject = new Object();
	
	private long galleryId;
	private String synchId;
	private int totalFileCount;
	private int currentFileIndex;
	private final List<ImmutablePair<String, String>> skippedContentObjects = new ArrayList<ImmutablePair<String, String>>();
	private boolean shouldTerminate;
	private SynchronizationState synchState;
	private String currentFileName;
	private String currentFilePath;
	private Date beginTimestampUtc;
	
	//#endregion
	
	//#region Constructors
	
	/// <summary>
	/// Initializes a new instance of the <see cref="SynchronizationStatus"/> class for the specified <paramref name="galleryId" />.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	public SynchronizationStatus(long galleryId)	{
		this(galleryId, StringUtils.EMPTY, SynchronizationState.Complete, 0, StringUtils.EMPTY, 0, StringUtils.EMPTY);
	}
	
	/// <summary>
	/// Initializes a new instance of the <see cref="SynchronizationStatus"/> class with the specified properties.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <param name="synchId">The GUID that uniquely identifies the current synchronization.</param>
	/// <param name="synchStatus">The status of the current synchronization.</param>
	/// <param name="totalFileCount">The total number of files in the directory or directories that are being processed in the current
	/// synchronization.</param>
	/// <param name="currentFileName">The name of the current file being processed.</param>
	/// <param name="currentFileIndex">The zero-based index value of the current file being processed. This is a number from 0 to 
	/// <see cref="TotalFileCount"/> - 1.</param>
	/// <param name="currentFilePath">The path to the current file being processed.</param>
	public SynchronizationStatus(long galleryId, String synchId, SynchronizationState synchStatus, int totalFileCount, String currentFileName
			, int currentFileIndex, String currentFilePath)	{
	  this.galleryId = galleryId;
	  this.synchId = synchId;
	  this.synchState = synchStatus;
	  this.totalFileCount = totalFileCount;
	  this.currentFileName = currentFileName;
	  this.currentFileIndex = currentFileIndex;
	  this.currentFilePath = currentFilePath;
	}
	
	//#endregion
	
	//#region Public Properties
	
	/// <summary>
	/// Gets a GUID that uniquely identifies the current synchronization.
	/// </summary>
	/// <value>The GUID that uniquely identifies the current synchronization.</value>
	public String getSynchId(){
		return this.synchId;
	}
	
	/// <summary>
	/// Gets the value that uniquely identifies the gallery.
	/// </summary>
	/// <value>The value that uniquely identifies the gallery.</value>
	public long getGalleryId(){
		return this.galleryId;
	}
	
	/// <summary>
	/// Gets the total number of files in the directory or directories that are being processed in the current
	/// synchronization. The number includes all files, not just ones that MDS System recognizes as
	/// valid content objects.
	/// </summary>
	/// <value>
	/// The total number of files in the directory or directories that are being processed in the current
	/// synchronization.
	/// </value>
	public int getTotalFileCount(){
		return this.totalFileCount;
	}
	
	/// <summary>
	/// Gets the date and time of when the sync was started.
	/// </summary>
	/// <value>The date and time of when the sync was started.</value>
	public Date getBeginTimestampUtc() {
		return beginTimestampUtc;
	}
	
	private void setBeginTimestampUtc(Date beginTimestampUtc){ 
		this.beginTimestampUtc = beginTimestampUtc; 
	}
	
	/// <summary>
	/// Gets or sets the zero-based index value of the current file being processed. This is a number from 0 to <see cref="TotalFileCount"/> - 1.
	/// data store; only this instance is updated.
	/// </summary>
	/// <value>The zero-based index value of the current file being processed.</value>
	public int getCurrentFileIndex(){
		return this.currentFileIndex;
	}
	
	/// <summary>
	/// Gets the name of the current file being processed (e.g. DesertSun.jpg).
	/// </summary>
	/// <value>
	/// The name of the current file being processed (e.g. DesertSun.jpg).
	/// </value>
	public String getCurrentFileName(){
		return this.currentFileName;
	}
	
	/// <summary>
	/// Gets the path to the current file being processed. The path is relative to the content objects
	/// directory. For example, if the content objects directory is C:\mypics\ and the file currently being processed is
	/// in C:\mypics\vacations\india\, this property is vacations\india\.
	/// </summary>
	/// <value>
	/// The path to the current file being processed, relative to the content objects directory (e.g. vacations\india\).
	/// </value>
	public String getCurrentFilePath(){
		return this.currentFilePath;
	}
	
	/// <summary>
	/// Gets a list of all files that were encountered during the synchronization but were not added. The key contains
	/// the name of the file; the value contains the reason why the object was skipped. Guaranteed to not return null.
	/// </summary>
	/// <value>
	/// The list of all files that were encountered during the synchronization but were not added.
	/// </value>
	public List<ImmutablePair<String, String>> getSkippedContentObjects(){
		return this.skippedContentObjects;
	}
	
	/// <summary>
	/// Gets the status of the current synchronization. This property will never return
	/// <see cref="MDS.Business.Interfaces.SynchronizationState.AnotherSynchronizationInProgress"/>. To find out if another
	/// synchronization is in progress, call the static Start method and catch
	/// <see cref="MDS.SynchronizationInProgressException" />.
	/// </summary>
	/// <value>The status of the current synchronization.</value>
	public SynchronizationState getStatus(){
		return this.synchState;
	}
	
	/// <summary>
	/// Gets a value indicating whether the current synchronization should be terminated. This is typically set
	/// by code that is observing the synchronization, such as a progress indicator. This property is periodically
	/// queried by the code running the synchronization to discover if a cancellation has been requested, and
	/// subsequently carrying out the request if it has.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the current synchronization should be terminated; otherwise, <c>false</c>.
	/// </value>
	public boolean isShouldTerminate()	{
		return this.shouldTerminate;
	}
	
	//#endregion
	
	//#region Public Static Methods
	
	/// <summary>
	/// Gets a reference to the <see cref="SynchronizationStatus" /> instance for the specified 
	/// <paramref name="galleryId">gallery ID</paramref>. The properties are NOT updated with the latest values from the data store;
	/// to do this call the <see cref="RefreshFromDataStore" /> method.
	/// </summary>
	/// <param name="galleryId">The ID of the gallery whose synchronization status you want to retrieve.</param>
	/// <returns>Gets a reference to the <see cref="SynchronizationStatus" /> instance for the specified 
	/// <paramref name="galleryId">gallery ID</paramref>.</returns>
	public static SynchronizationStatus getInstance(long galleryId)	{
	  return CMUtils.loadSynchronizationStatus(galleryId);
	}
	
	/// <summary>
	/// Begins the process of a new synchronization by updating the status object and the Synchronize table in the
	/// data store. Throws an exception if another synchronization is already in process.
	/// </summary>
	/// <param name="synchId">A GUID String that uniquely identifies the current synchronization.</param>
	/// <param name="galleryId">The ID of the gallery to be synchronized.</param>
	/// <returns>Returns an instance of <see cref="SynchronizationStatus" /> representing the current synchronization.</returns>
	/// <exception cref="MDS.SynchronizationInProgressException">
	/// Thrown when a synchronization with another synchId is already in progress.</exception>
	public static SynchronizationStatus start(String synchId, long galleryId) throws SynchronizationInProgressException, RecordExistsException{
	  SynchronizationStatus syncStatus = getInstance(galleryId);
	
	  syncStatus.begin(synchId);
	
	  return syncStatus;
	}
	
	//#endregion
	
	//#region Public Methods
	
	/// <summary>
	/// Perform actions that are required at the start of a synchronization. This includes resetting the properties of the
	/// current instance and setting the status to <see cref="SynchronizationState.SynchronizingFiles"/>. The new values are
	/// persisted to the data store. Throws a SynchronizationInProgressException if another synchronization is already in progress.
	/// </summary>
	/// <param name="synchId">The GUID that uniquely identifies the synchronization.</param>
	public void begin(String synchId) throws SynchronizationInProgressException, RecordExistsException{
		synchronized (this.lockObject){
			refreshFromDataStore();
		
			boolean syncInProgress = (this.synchState == SynchronizationState.SynchronizingFiles || this.synchState == SynchronizationState.PersistingToDataStore);
		
			if ((!synchId.equalsIgnoreCase(this.synchId)) && syncInProgress){
				throw new SynchronizationInProgressException();
			}
		
			this.beginTimestampUtc = DateUtils.Now(); //.UtcNow;
			this.synchId = synchId;
			this.totalFileCount = 0;
			this.currentFileIndex = 0;
			this.synchState = SynchronizationState.SynchronizingFiles;
			this.skippedContentObjects.clear();
		
			// Save to data store. Even though it might have been valid to start the synchronizing above, by the time
			// we try to save to the data store, someone else may have started it (for example, from another application).
			//  So the data provider will check one more time just before saving our data, throwing an exception if necessary.
			save();
		}
	}
	
	/// <summary>
	/// Updates the current instance with the new values, persisting the changes to the data store if <paramref name="persistToDataStore"/>
	/// is <c>true</c>. Specify a null value for each parameter that should not be updated (the existing value will be retained).
	/// </summary>
	/// <param name="synchStatus">The status of the current synchronization.</param>
	/// <param name="totalFileCount">The total number of files in the directory or directories that are being processed in the current
	/// synchronization.</param>
	/// <param name="currentFileName">The name of the current file being processed.</param>
	/// <param name="currentFileIndex">The zero-based index value of the current file being processed. This is a number from 0 to
	/// <see cref="TotalFileCount"/> - 1.</param>
	/// <param name="currentFilePath">The path to the current file being processed.</param>
	/// <param name="shouldTerminate">Indicates whether the current synchronization should be terminated.</param>
	/// <param name="persistToDataStore">If set to <c>true</c> persist the new values to the data store.</param>
	public void update(SynchronizationState synchStatus, Integer totalFileCount, String currentFileName, Integer currentFileIndex, String currentFilePath
			, Boolean shouldTerminate, boolean persistToDataStore) throws SynchronizationInProgressException, RecordExistsException{
			synchronized (this.lockObject)  {
				if (synchStatus != SynchronizationState.NotSet)	{
					this.synchState = synchStatus;
				}
			
				if (totalFileCount != null){
					if (totalFileCount < 0) {
						throw new ArgumentOutOfRangeException(I18nUtils.getMessage("synchronizationStatus.TotalFileCount_Ex_Msg", totalFileCount));
					}
			
					this.totalFileCount = totalFileCount;
				}
			
				if (currentFileName != null){
					this.currentFileName = currentFileName;
				}
			
				if (currentFileIndex != null){
					if ((currentFileIndex < 0) || ((currentFileIndex > 0) && (currentFileIndex >= this.totalFileCount))) {
						throw new ArgumentOutOfRangeException(I18nUtils.getMessage("synchronizationStatus.CurrentFileIndex_Ex_Msg", currentFileIndex, this.totalFileCount));
					}
			
					this.currentFileIndex = currentFileIndex;
				}
			
				if (currentFilePath != null){
					this.currentFilePath = currentFilePath;
				}
			
				if (shouldTerminate != null){
					this.shouldTerminate = shouldTerminate;
				}
			
				if (persistToDataStore)	{
					save();
				}
		  }
	}
	
	/// <summary>
	/// Set a flag (<see cref="ShouldTerminate"/>) to indicate the current synchronization should be cancelled. This property is periodically
	/// queried by the code running the synchronization to discover if a cancellation has been requested, and subsequently carrying out
	/// the request if it has.
	/// </summary>
	/// <param name="synchId">The GUID that uniquely identifies the synchronization. If the value does not match the synch ID
	/// of the current instance, no action is taken.</param>
	public void cancelSynchronization(String synchId){
		synchronized (lockObject) {
			if (this.synchId.equalsIgnoreCase(synchId)){
			  this.shouldTerminate = true;
			}
		}
	}
	
	/// <summary>
	/// Completes the current synchronization by updating the status instance and the Synchronize table in the
	/// data store. Calling this method is required before subsequent synchronizations can be performed.
	/// </summary>
	public void finish() throws SynchronizationInProgressException, RecordExistsException {
		synchronized (lockObject) {
			// Updates database to show synchronization is no longer occuring.
			// Should be called when synchronization is finished.
			this.synchState = SynchronizationState.Complete;
		
			// Don't reset the file counts in case the UI wants to know how many files were processed.
			//this.currentFileIndex = 0;
			//this.totalFileCount = 0;
		
			save();
		}
	}
	
	//#endregion
	
	//#region Private Functions
	
	/// <summary>
	/// Persist the current state of this instance to the data store.
	/// </summary>
	private void save() throws SynchronizationInProgressException, RecordExistsException{
		synchronized (lockObject){
			CMUtils.saveSynchronizationStatus(this);
		}
	}
	
	/// <summary>
	/// Update the properties of this instance with the latest data from the data store.
	/// </summary>
	private void refreshFromDataStore() throws RecordExistsException	{
		synchronized (lockObject) {
			//SynchronizationStatus synchFromDataStore = CMUtils.getDataProvider().SynchronizeRetrieveStatus(galleryId, new Factory());
			SynchronizationStatus synchFromDataStore = CMUtils.getFromDataStore(this);
		
			this.synchId = synchFromDataStore.getSynchId();
			this.totalFileCount = synchFromDataStore.getTotalFileCount();
			this.currentFileIndex = synchFromDataStore.getCurrentFileIndex();
			this.synchState = synchFromDataStore.getStatus();
		}
	}
	
	//#endregion
}
