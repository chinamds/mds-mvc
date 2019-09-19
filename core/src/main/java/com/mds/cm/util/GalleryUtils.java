package com.mds.cm.util;

import java.beans.EventHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Date;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.mail.internet.AddressException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.mds.common.Constants;
import com.mds.cm.content.AlbumBo;
import com.mds.cm.content.ContentConversionQueue;
import com.mds.cm.content.ContentObjectBo;
import com.mds.sys.util.MDSRole;
import com.mds.cm.content.GalleryBo;
import com.mds.cm.content.GalleryBoCollection;
import com.mds.cm.content.GalleryCreatedEventArgs;
import com.mds.cm.content.GalleryCreatedListener;
import com.mds.cm.content.GallerySettings;
import com.mds.cm.content.GallerySettingsEventArgs;
import com.mds.cm.content.Image;
import com.mds.sys.util.SecurityGuard;
import com.mds.cm.content.SynchronizationManager;
import com.mds.cm.content.SynchronizationStatus;
import com.mds.cm.content.UiTemplateBoCollection;
import com.mds.sys.util.UserAccount;
import com.mds.cm.exception.CannotWriteToDirectoryException;
import com.mds.cm.exception.GallerySecurityException;
import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidContentObjectException;
import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.SynchronizationInProgressException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.cm.rest.AppRest;
import com.mds.cm.rest.CMData;
import com.mds.cm.rest.CMDataLoadOptions;
import com.mds.cm.rest.ContentItem;
import com.mds.cm.rest.MediaItem;
import com.mds.cm.rest.SyncInitiator;
import com.mds.cm.rest.SyncOptions;
import com.mds.cm.rest.SynchStatusRest;
import com.mds.cm.rest.UserRest;
import com.mds.core.ApprovalStatus;
import com.mds.core.CacheItem;
import com.mds.core.ContentObjectType;
import com.mds.core.LongCollection;
import com.mds.core.SecurityActions;
import com.mds.core.SynchronizationState;
import com.mds.core.exception.ArgumentException;
import com.mds.core.exception.ArgumentNullException;
import com.mds.core.exception.InvalidEnumArgumentException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.core.exception.WebException;
import com.mds.i18n.util.I18nUtils;
import com.mds.pm.util.PlayersUtils;
import com.mds.sys.exception.InvalidUserException;
import com.mds.sys.model.RoleType;
import com.mds.sys.util.AppSettings;
import com.mds.sys.util.RoleUtils;
import com.mds.cm.util.CMUtils;
import com.mds.common.exception.RecordExistsException;
import com.mds.util.CacheUtils;
import com.mds.util.DateUtils;
import com.mds.util.FileMisc;
import com.mds.util.HelperFunctions;
import com.mds.util.StringUtils;
import com.mds.util.Utils;
import com.mds.sys.util.UserUtils;

/// <summary>
/// Contains functionality for interacting with galleries and gallery settings.
/// </summary>
public final class GalleryUtils implements GalleryCreatedListener {
	//#region Fields

	private static final Object _sharedLock = new Object();
	private static boolean _isInitialized;

	//#endregion

	//#region Properties

	/// <summary>
	/// Gets a value indicating whether the MDS System code has been initializaed.
	/// The code is initialized by calling <see cref="InitializeMDSApplication" />.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the code is initialized; otherwise, <c>false</c>.
	/// </value>
	public static boolean isInitialized(){
		return _isInitialized;
	}

	//#endregion

	//#region Public Methods

	/// <summary>
	/// Initialize the MDS System application. This method is designed to be run at application startup. The business layer
	/// is initialized with the current trust level and a few configuration settings. The business layer also initializes
	/// the data store, including verifying a minimal level of data integrity, such as at least one record for the root album.
	/// Initialization that requires an HttpContext is also performed. When this method completes, <see cref="IAppSetting.IsInitialized" />
	/// will be <c>true</c>, but <see cref="GalleryUserUtils.isInitialized" /> will be <c>true</c> only when an HttpContext instance
	/// exists. If this function is initially called from a place where an HttpContext doesn't exist, it will automatically be called 
	/// again later, eventually being called from a place where an HttpContext does exist, thus completing app initialization.
	/// </summary>
	public static void initializeMDSApplication(ServletContext context) throws Exception{
		try	{
			initializeApplication(context);

			synchronized (_sharedLock)
			{
				if (isInitialized())
					return;

				if (context != null){
					// Add a dummy value to session so that the session ID remains finalant. (This is required by RoleUtils.getRolesForUser())
					// Check for null session first. It will be null when this is triggered by a web method that does not have
					// session enabled (that is, the [WebMethod(EnableSession = true)] attribute). That's OK because the roles functionality
					// will still work (we might have to an extra data call, though), and we don't want the overhead of session for some web methods.
					/*if (HttpContext.Current.Session != null)
						HttpContext.Current.Session.add("1", "1");*/

					// Update the user accounts in a few gallery settings. The DotNetNuke version requires this call to happen when there
					// is an HttpContext, so to reduce differences between the two branches we put it here.
					addMembershipDataToGallerySettings();

					_isInitialized = true;
				}

				AppEventLogUtils.LogEvent("Application has started.");

				//InsertSampleUsersAndRoles();
			}
		//}catch (InterruptedException ie){
		}catch (CannotWriteToDirectoryException ex)	{
			// Let the error handler log it and try to redirect to a dedicated page for this error. The transfer will fail when the error occurs
			// during the app's init event, so when this happens don't re-throw (like we do in the generic catch below). This will allow the
			// initialize routine to run again from the GalleryPage finalructor, and when the error happens again, this time the handler will be able to redirect.
			AppEventLogUtils.HandleGalleryException(ex);
			//throw; // Don't re-throw
		}catch (Exception ex){
			// Let the error handler deal with it. It will decide whether to transfer the user to a friendly error page.
			// If the function returns, that means it didn't redirect, so we should re-throw the exception.
			AppEventLogUtils.HandleGalleryException(ex);
			throw ex;
		}
	}

	/// <summary>
	/// Get a list of galleries the current user can administer. Site administrators can view all galleries, while gallery
	/// administrators may have access to zero or more galleries.
	/// </summary>
	/// <returns>Returns an <see cref="GalleryBoCollection" /> containing the galleries the current user can administer.</returns>
	//[DataObjectMethod(DataObjectMethodType.Select)]
	public static GalleryBoCollection getGalleriesCurrentUserCanAdminister() throws InvalidMDSRoleException{
		return UserUtils.getGalleriesCurrentUserCanAdminister();
	}

	/// <summary>
	/// Gets the ID of the template gallery.
	/// </summary>
	/// <returns>System.Int32.</returns>
	public static long getTemplateGalleryId(){
		return CMUtils.getTemplateGalleryId();
	}

	/// <summary>
	/// Persist the <paramref name="gallery" /> to the data store.
	/// </summary>
	/// <param name="gallery">The gallery to persist to the data store.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="gallery" /> is null.</exception>
	//[DataObjectMethod(DataObjectMethodType.Insert)]
	public static void addGallery(GalleryBo gallery) throws RecordExistsException{
		if (gallery == null)
			throw new ArgumentNullException("gallery");

		gallery.save();
	}

	/// <summary>
	/// Permanently delete the specified <paramref name="gallery" /> from the data store, including all related records. This action cannot
	/// be undone.
	/// </summary>
	/// <param name="gallery">The gallery to delete.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="gallery" /> is null.</exception>
	//[DataObjectMethod(DataObjectMethodType.Delete)]
	public static void deleteGallery(GalleryBo gallery) throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException{
		if (gallery == null)
			throw new ArgumentNullException("gallery");

		gallery.delete();

		ProfileUtils.deleteProfileForGallery(gallery);
	}

	/// <summary>
	/// Persist the <paramref name="gallery" /> to the data store.
	/// </summary>
	/// <param name="gallery">The gallery to persist to the data store.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="gallery" /> is null.</exception>
	//[DataObjectMethod(DataObjectMethodType.Update)]
	public static void updateGallery(GalleryBo gallery) throws RecordExistsException{
		if (gallery == null)
			throw new ArgumentNullException("gallery");

		gallery.save();
	}

	/// <summary>
	/// Execute install-related activities such as creating sample objects or an administrator account.
	/// </summary>
	/// <param name="galleryId">The ID for the gallery where the sample objects are to be created.</param>
	public static void processInstallRequest(long galleryId) throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException, AddressException, InvalidUserException{
		UserAccount user = createAdministrator(galleryId);

		if (user != null){
			// User was successfully created or updated. Delete the install file and cancel the install request so that 
			// sample objects don't get created and the create user page converts to normal operation.
			updateRootAlbumTitleAfterAdminCreation(galleryId);
			deleteInstallFile();
			AppSettings.getInstance().setInstallationRequested(false);
		}

		createSampleObjects(galleryId);
	}

	/// <summary>
	/// Perform a synchronization according to the specified <paramref name="syncOptions" />. Any exceptions that occur during the
	/// sync are caught and logged to the event log. For auto-run syncs, the property <see cref="GallerySettings.LastAutoSync" /> 
	/// is set to the current date/time and persisted to the data store.
	/// NOTE: This method does not perform any security checks; the calling code must ensure the requesting user is authorized to run the sync.
	/// </summary>
	/// <param name="syncOptions">An object specifying the parameters for the synchronization operation.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="syncOptions" /> is null.</exception>
	public static void beginSync(SyncOptions syncOptions){
		if (syncOptions == null)
			throw new ArgumentNullException("syncOptions");

		AlbumBo album = null;

		try	{
			album = AlbumUtils.loadAlbumInstance(syncOptions.AlbumIdToSynchronize, true, true, true);

			AppEventLogUtils.LogEvent(StringUtils.format("{0} synchronization of album '{1}' (ID {2}) has started.", syncOptions.UserName, album.getTitle(), album.getId()), album.getGalleryId());

			SynchronizationManager synchMgr = new SynchronizationManager(album.getGalleryId());

			synchMgr.setRecursive(syncOptions.IsRecursive);
			synchMgr.setRebuildThumbnail(syncOptions.RebuildThumbnails);
			synchMgr.setRebuildOptimized(syncOptions.RebuildOptimized);

			synchMgr.synchronize(syncOptions.SyncId, album, syncOptions.UserName);

			if (syncOptions.SyncInitiator == SyncInitiator.AutoSync){
				// Update the date/time of this auto-sync and save to data store.
				GallerySettings gallerySettings = CMUtils.loadGallerySetting(album.getGalleryId(), true);
				gallerySettings.setLastAutoSync(DateUtils.Now());
				gallerySettings.save(false);

				// The above Save() only updated the database; now we need to update the in-memory copy of the settings.
				// We have to do this instead of simply calling gallerySettings.Save(true) because that overload causes the
				// gallery settings to be cleared and reloaded, and the reloading portion done by the AddMembershipDataToGallerySettings
				// function fails in DotNetNuke because there isn't a HttpContext.Current instance at this moment (because this code is
				// run on a separate thread).
				GallerySettings gallerySettingsReadOnly = CMUtils.loadGallerySetting(album.getGalleryId(), false);
				gallerySettingsReadOnly.setLastAutoSync(gallerySettings.getLastAutoSync());
			}

			AppEventLogUtils.LogEvent(StringUtils.format("{0} synchronization of album '{1}' (ID {2}) has finished.", syncOptions.UserName, album.getTitle(), album.getId()), album.getGalleryId());
		}catch (SynchronizationInProgressException se){
			String message = StringUtils.format("{0} synchronization of album '{1}' (ID {2}) could not be started because another one is in progress.", syncOptions.UserName, album != null ? album.getTitle() : "N/A", album != null ? Long.toString(album.getId()) : "N/A");
			AppEventLogUtils.LogEvent(message, album != null ? album.getGalleryId() : null);
		}catch (Exception ex){
			if (album != null){
				AppEventLogUtils.LogError(ex, album.getGalleryId());
			}else{
				AppEventLogUtils.LogError(ex);
			}

			String msg = StringUtils.format("{0} synchronization of album '{1}' (ID {2}) has encountered an error and could not be completed.", syncOptions.UserName, album != null ? album.getTitle() : "N/A", album != null ? Long.toString(album.getId()) : "N/A");
			AppEventLogUtils.LogEvent(msg, album != null ? album.getGalleryId() : null);
		}
	}

	/// <summary>
	/// Retrieves the status of a synchronization for the gallery having the ID <paramref name="galleryId" /> and the
	/// synchronization ID <paramref name="syncId" />.
	/// </summary>
	/// <param name="syncId">The synchronization ID.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>An instance of <see cref="SynchStatusRest" />.</returns>
	public static SynchStatusRest getSyncStatus(String syncId, long galleryId){
		SynchronizationStatus synchStatus = SynchronizationStatus.getInstance(galleryId);
		SynchStatusRest synchStatusWeb = new SynchStatusRest();

		try	{
			if (!StringUtils.isBlank(synchStatus.getSynchId()) && !synchStatus.getSynchId().equalsIgnoreCase(syncId )){
				synchStatusWeb.SynchId = syncId;
				synchStatusWeb.TotalFileCount = 0;
				synchStatusWeb.CurrentFileIndex = 0;
				synchStatusWeb.CurrentFile = StringUtils.EMPTY;
				synchStatusWeb.Status = SynchronizationState.AnotherSynchronizationInProgress.toString();
				synchStatusWeb.StatusForUI = I18nUtils.getMessage("task.synch.Progress_Status_SynchInProgressException_Hdr");
				synchStatusWeb.PercentComplete = calculatePercentComplete(synchStatus);
				synchStatusWeb.SyncRate = StringUtils.EMPTY;

				return synchStatusWeb;
			}

			synchStatusWeb.SynchId = synchStatus.getSynchId();
			synchStatusWeb.Status = synchStatus.getStatus().toString();
			synchStatusWeb.TotalFileCount = synchStatus.getTotalFileCount();
			synchStatusWeb.CurrentFileIndex = (synchStatus.getStatus() == SynchronizationState.Complete ? synchStatus.getTotalFileCount() : synchStatus.getCurrentFileIndex());
			synchStatusWeb.StatusForUI = getFriendlyStatusText(synchStatus);
			synchStatusWeb.PercentComplete = calculatePercentComplete(synchStatus);
			synchStatusWeb.SyncRate = calculateSyncRate(synchStatus);

			if ((synchStatus.getCurrentFilePath() != null) && (synchStatus.getCurrentFileName() != null)){
				try	{
					synchStatusWeb.CurrentFile = FilenameUtils.concat(synchStatus.getCurrentFilePath(), synchStatus.getCurrentFileName());
				}catch (ArgumentException ex){
					synchStatusWeb.CurrentFile = StringUtils.EMPTY;

					ex.Data.put("INFO", "This error was handled and should not affect the user experience unless it occurs frequently.");
					ex.Data.put("synchStatus.CurrentFilePath", synchStatus.getCurrentFilePath());
					ex.Data.put("synchStatus.CurrentFileName", synchStatus.getCurrentFileName());
					AppEventLogUtils.LogError(ex, synchStatus.getGalleryId());
				}
			}

			// Update the Skipped Files, but only when the sync is complete. 
			synchronized (synchStatus){
				if (synchStatus.getStatus() == SynchronizationState.Complete){
					if (synchStatus.getSkippedContentObjects().size() > Constants.MaxNumberOfSkippedObjectsToDisplayAfterSynch)	{
						// We have a large number of skipped content objects. We don't want to send it all to the browsers, because it might take
						// too long or cause an error if it serializes to a String longer than Integer.MIN_VALUE, so let's trim it down.
						synchStatus.getSkippedContentObjects().subList(Constants.MaxNumberOfSkippedObjectsToDisplayAfterSynch, synchStatus.getSkippedContentObjects().size() - Constants.MaxNumberOfSkippedObjectsToDisplayAfterSynch).clear();
					}
					synchStatusWeb.SkippedFiles = synchStatus.getSkippedContentObjects();
				}
			}
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex, synchStatus.getGalleryId());

			synchStatusWeb.StatusForUI = "An error occurred while retrieving the status";
		}

		return synchStatusWeb;
	}

	/// <summary>
	/// Terminates the synchronization with the specified <paramref name="syncId"/> and <paramref name="galleryId" />.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <param name="syncId">The synchronization ID representing the synchronization to cancel.</param>
	public static void abortSync(String syncId, long galleryId)	{
		SynchronizationStatus.getInstance(galleryId).cancelSynchronization(syncId);
	}

	/// <summary>
	/// Gets a collection of all UI templates from the data store. Returns an empty collection if no
	/// errors exist.
	/// </summary>
	/// <returns>Returns a collection of all UI templates from the data store.</returns>
	public static UiTemplateBoCollection getUiTemplates()	{
		return CMUtils.loadUiTemplates();
	}
	
	/// <summary>
	/// Gets the gallery data for the specified <paramref name="contentObject" />.
	/// <see cref="CMData.Settings" /> is set to null because those values
	/// are calculated from control-specific properties that are not known at this time (it is
	/// expected that that property is assigned by subsequent code - including javascript -
	/// when that data is able to be calculated). Guaranteed to not return null.
	/// </summary>
	/// <param name="contentObject">The content object.</param>
	/// <param name="contentObjectContainer">The content object container.</param>
	/// <param name="options">Specifies options for configuring the return data. To use default
	/// settings, specify an empty instance with properties left at default values.</param>
	/// <returns>Returns an instance of <see cref="CMData" />.</returns>
	/// <exception cref="GallerySecurityException">Thrown when the current user does not have
	/// permission to access the <paramref name="contentObject" />.</exception>
	public static CMData getCMDataForContentPreview(ContentObjectBo contentObject, AlbumBo contentObjectContainer, CMDataLoadOptions options, HttpServletRequest request) throws Exception{
		SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), contentObject.getParent().getId(), contentObject.getGalleryId(), UserUtils.isAuthenticated(), contentObject.getParent().getIsPrivate(), ((AlbumBo)contentObject.getParent()).getIsVirtualAlbum());

		CMData data = new CMData();
		data.setApp(getAppEntity(contentObject.getGalleryId(), request));
		data.setSettings(null);
		data.setAlbum(AlbumUtils.toAlbumEntity(contentObjectContainer, options, request));
		data.setActiveMetaItems(null); // Assigned on client
		data.setActiveApprovalItems(null); // Assigned on client
		data.setActiveContentItems(null); // Assigned on client
		data.setResource(ResourceUtils.getResourceEntity());

		data.setMediaItem(getCurrentMedaiItem(data, contentObject, request));

		// Assign user, but only grab the required fields. We do this to prevent unnecessary user data from traveling the wire.
		UserRest user = UserUtils.getUserEntity(UserUtils.getLoginName(), contentObject.getGalleryId());
		UserRest dataUser = new UserRest();
		dataUser.UserName = user.UserName;
		dataUser.IsAuthenticated = user.IsAuthenticated;
		dataUser.CanAddAlbumToAtLeastOneAlbum = user.CanAddAlbumToAtLeastOneAlbum;
		dataUser.CanAddContentToAtLeastOneAlbum = user.CanAddContentToAtLeastOneAlbum;
		dataUser.UserAlbumId = user.UserAlbumId;
		data.setUser(dataUser);

		return data;
	}

	/// <summary>
	/// Gets the gallery data for the specified <paramref name="contentObject" />.
	/// <see cref="CMData.Settings" /> is set to null because those values
	/// are calculated from control-specific properties that are not known at this time (it is
	/// expected that that property is assigned by subsequent code - including javascript -
	/// when that data is able to be calculated). Guaranteed to not return null.
	/// </summary>
	/// <param name="contentObject">The content object.</param>
	/// <param name="contentObjectContainer">The content object container.</param>
	/// <param name="options">Specifies options for configuring the return data. To use default
	/// settings, specify an empty instance with properties left at default values.</param>
	/// <returns>Returns an instance of <see cref="CMData" />.</returns>
	/// <exception cref="GallerySecurityException">Thrown when the current user does not have
	/// permission to access the <paramref name="contentObject" />.</exception>
	public static CMData getCMDataForContentObject(ContentObjectBo contentObject, AlbumBo contentObjectContainer, CMDataLoadOptions options, HttpServletRequest request) throws Exception{
		SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), contentObject.getParent().getId(), contentObject.getGalleryId(), UserUtils.isAuthenticated(), contentObject.getParent().getIsPrivate(), ((AlbumBo)contentObject.getParent()).getIsVirtualAlbum());

		CMData data = new CMData();
		data.setApp(getAppEntity(contentObject.getGalleryId(), request));
		data.setSettings(null);
		data.setAlbum(AlbumUtils.toAlbumEntity(contentObjectContainer, options, request));
		data.setActiveMetaItems(null); // Assigned on client
		data.setActiveApprovalItems(null); // Assigned on client
		data.setActiveContentItems(null); // Assigned on client
		data.setResource(ResourceUtils.getResourceEntity());
		data.setPlayerItems(PlayersUtils.getAllPlayerItems());

		data.setMediaItem(getCurrentMedaiItem(data, contentObject, request));

		// Assign user, but only grab the required fields. We do this to prevent unnecessary user data from traveling the wire.
		UserRest user = UserUtils.getUserEntity(UserUtils.getLoginName(), contentObject.getGalleryId());
		UserRest dataUser = new UserRest();
		dataUser.UserName = user.UserName;
		dataUser.IsAuthenticated = user.IsAuthenticated;
		dataUser.CanAddAlbumToAtLeastOneAlbum = user.CanAddAlbumToAtLeastOneAlbum;
		dataUser.CanAddContentToAtLeastOneAlbum = user.CanAddContentToAtLeastOneAlbum;
		dataUser.UserAlbumId = user.UserAlbumId;
		data.setUser(dataUser);

		return data;
	}

	/// <summary>
	/// Gets the gallery data for the specified <paramref name="album" />.
	/// <see cref="CMData.ContentItem" /> is set to null since no particular content object
	/// is in context. <see cref="CMData.Settings" /> is also set to null because those values
	/// are calculated from control-specific properties that are not known at this time (it is 
	/// expected that that property is assigned by subsequent code - including javascript - 
	/// when that data is able to be calculated). Guaranteed to not return null.
	/// </summary>
	/// <param name="album">The album.</param>
	/// <param name="options">Specifies options for configuring the return data. To use default
	/// settings, specify an empty instance with properties left at default values.</param>
	/// <returns>Returns an instance of <see cref="CMData" />.</returns>
	/// <exception cref="GallerySecurityException">Thrown when the current user does not have
	/// permission to access the <paramref name="album" />.</exception>
	public static CMData getCMDataForAlbum(AlbumBo album, CMDataLoadOptions options, HttpServletRequest request) throws Exception{
		SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), album.getId(), album.getGalleryId(), UserUtils.isAuthenticated(), album.getIsPrivate(), album.getIsVirtualAlbum());

		CMData data = new CMData();
		data.setApp(getAppEntity(album.getGalleryId(), request));
		data.setSettings(null);
		data.setAlbum(AlbumUtils.toAlbumEntity(album, options, request));
		data.setMediaItem(null);
		data.setActiveMetaItems(null); // Assigned on client
		data.setActiveApprovalItems(null); // Assigned on client
		data.setActiveContentItems(null); // Assigned on client
		data.setResource(ResourceUtils.getResourceEntity());
		data.setPlayerItems(PlayersUtils.getAllPlayerItems());

		// Assign user, but only grab the required fields. We do this to prevent unnecessary user data from traveling the wire.
		UserRest user = UserUtils.getUserEntity(UserUtils.getLoginName(), album.getGalleryId());
		UserRest dataUser = new UserRest();
		dataUser.UserName = user.UserName;
		dataUser.IsAuthenticated = user.IsAuthenticated;
		dataUser.CanAddAlbumToAtLeastOneAlbum = user.CanAddAlbumToAtLeastOneAlbum;
		dataUser.CanAddContentToAtLeastOneAlbum = user.CanAddContentToAtLeastOneAlbum;
		dataUser.UserAlbumId = user.UserAlbumId;
		data.setUser(dataUser);

		return data;
	}

	//#endregion

	//#region Private Functions

	/// <summary>
	/// Find the <paramref name="contentObject" /> in <see cref="CMData.Album.ContentItems" />,
	/// configure it, and return it as an instance of <see cref="ContentItem" />. The metadata are
	/// assigned to the <see cref="ContentItem.MetaItems" /> property.
	/// </summary>
	/// <param name="data">The gallery data. It is expected, though not necessary, for the 
	/// <paramref name="contentObject" /> to be represented as one of the items in 
	/// <see cref="CMData.Album.ContentItems" />.</param>
	/// <param name="contentObject">The content object.</param>
	/// <returns>Returns an instance of <see cref="ContentItem" />.</returns>
	private static MediaItem getCurrentMedaiItem(CMData data, ContentObjectBo contentObject, HttpServletRequest request) throws Exception{
		MediaItem mediaItem = null;

		if (data.getAlbum().MediaItems != null)
			mediaItem = Arrays.asList(data.getAlbum().MediaItems).stream().filter(mo -> mo.Id == contentObject.getId()).findFirst().orElse(null);

		if (mediaItem == null)
			mediaItem = ContentObjectUtils.toMediaItem(contentObject, 0, ContentObjectHtmlBuilder.getContentObjectHtmlBuilderOptions(contentObject, request), request);

		mediaItem.MetaItems = ContentObjectUtils.toMetaItems(contentObject.getMetadataItems().getVisibleItems(), contentObject, request);
		mediaItem.ApprovalItems = ContentObjectUtils.toApprovalItems(contentObject.getApprovalItems(), contentObject);

		return mediaItem;
	}

	/// <summary>
	/// Initialize the components of the MDS System application that do not require access to an HttpContext.
	/// This method is designed to be run at application startup. The business layer
	/// is initialized with the current trust level and a few configuration settings. The business layer also initializes
	/// the data store, including verifying a minimal level of data integrity, such as at least one record for the root album.
	/// </summary>
	/// <remarks>This is the only method, apart from those invoked through web services, that is not handled by the global error
	/// handling routine in Gallery.cs. This method wraps its calls in a try..catch that passes any exceptions to
	/// <see cref="AppEventLogUtils.HandleGalleryException(Exception, int?)"/>. If that method does not transfer the user to a friendly error page, the exception
	/// is re-thrown.</remarks>
	private static void initializeApplication(ServletContext context) throws CannotWriteToDirectoryException	{
		synchronized (_sharedLock)
		{
			if (AppSettings.getInstance().isInitialized())
				return;

			String msg = checkForDbCompactionRequest();

			//Gallery.GalleryCreated += new EventHandler<GalleryCreatedEventArgs>(GalleryCreated);

			//GallerySettings.GallerySettingsSaved += new EventHandler<GallerySettingsEventArgs>(GallerySettingsSaved);

			// Set web-related variables in the business layer and initialize the data store.
			initializeBusinessLayer(context);

			AppSettings.getInstance().setInstallationRequested(AppSettings.getInstallRequested());

			// Make sure installation has its own unique encryption key.
			validateEncryptionKey();

			ContentConversionQueue.getInstance().process();

			// If there is a message from the DB compaction, record it now. We couldn't do it before because the DB
			// wasn't fully initialized.
			if (!StringUtils.isBlank(msg))
				AppEventLogUtils.LogEvent(msg);
		}
	}

	/// <summary>
	/// Check for the app setting 'CompactDatabaseOnStartup' in web.config. If true, then compact and repair the
	/// database. Applies only to SQL CE, this can be used if the database is corrupt and the user is not able to
	/// navigate to the Site admin page to manually invoke the operation.
	/// </summary>
	/// <returns>Returns a message indicating the result of the operation, or null if no operation was performed.</returns>
	private static String checkForDbCompactionRequest(){
		String msg = null;
		
		return msg;
	}

	private static void insertSampleUsersAndRoles() throws UnsupportedContentObjectTypeException, InvalidMDSRoleException, GallerySecurityException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, WebException, AddressException, InvalidUserException{
		// Get list of all album IDs
		List<Long> albumIds = new ArrayList<Long>();
		List<MDSRole> roles = RoleUtils.getMDSRoles();
		for (MDSRole role : roles){
			if (role.getRoleType() == RoleType.sa){
				albumIds.addAll(role.getAllAlbumIds());
				Collections.sort(albumIds);
			}
		}

		//// Create roles and assign each one to a random album
		//Random rdm = new Random();
		final int numRoles = 100;
		for (int i = 0; i < numRoles; i++){
			long albumId;
			do{
				albumId = ThreadLocalRandom.current().nextLong(albumIds.get(0), albumIds.get(albumIds.size() - 1));
			} while (!albumIds.contains(albumId));

			LongCollection roleAlbums = new LongCollection();
			roleAlbums.add(albumId);
			if (i==0) {
				RoleUtils.createRole(Long.MIN_VALUE, "Role " + i, RoleType.sa, true, false, true, false, true, false, true, false, false, true, false, false, false, roleAlbums);
			}else {
				RoleUtils.createRole(Long.MIN_VALUE, "Role " + i, RoleType.ga, true, false, true, false, true, false, true, false, false, true, false, false, false, roleAlbums);
			}
		}

		// Create users and assign to random number of roles.
		final int numUsers = 100;
		for (int i = 0; i < numUsers; i++){
			int numRolesToAssignToUser = ThreadLocalRandom.current().nextInt(0, 5); // Add up to 5 roles to user
			List<String> roleNames = new ArrayList<String>(numRolesToAssignToUser);
			for (int j = 0; j < numRolesToAssignToUser; j++){
				// Pick a random role
				String roleName = "Role " + ThreadLocalRandom.current().nextInt(0, numRoles - 1);
				if (!roleNames.contains(roleName))
					roleNames.add(roleName);
			}

			String userName = "User " + i;
			if (UserUtils.getUserAccount(userName, false) == null)	{
				UserUtils.createUser(userName, "111", StringUtils.EMPTY, roleNames.toArray(new String[0]), false, 1);
			}
		}
	}

	/// <summary>
	/// Set up the business layer with information about this web application, such as its trust level and a few settings
	/// from the configuration file.
	/// </summary>
	/// <exception cref="MDS.EventLogs.CustomExceptions.CannotWriteToDirectoryException">
	/// Thrown when MDS System is unable to write to, or delete from, the content objects directory.</exception>
	private static void initializeBusinessLayer(ServletContext context) throws CannotWriteToDirectoryException{
		// Determine the trust level this web application is running in and set to a global variable. This will be used 
		// throughout the application to gracefully degrade when we are not at Full trust.
		//ApplicationTrustLevel trustLevel = Utils.GetCurrentTrustLevel();

		// Get the application path so that the business layer (and any dependent layers) has access to it. Don't use 
		// HttpContext.Current.Request.PhysicalApplicationPath because in some cases HttpContext.Current won't be available
		// (for example, when the DotNetNuke search engine indexer causes this code to trigger).
		//String physicalApplicationPath = AppDomain.CurrentDomain.BaseDirectory.SubString(0, AppDomain.CurrentDomain.BaseDirectory.length - 1);
		String physicalApplicationPath = StringUtils.stripEnd(context.getRealPath("/"), File.separator);
		//physicalApplicationPath = physicalApplicationPath.substring(0, physicalApplicationPath.length() - 1);
		physicalApplicationPath = physicalApplicationPath.replace("/", File.separator);

		// Pass these values to our global app settings instance, where the values can be used throughout the application.
		AppSettings.getInstance().initialize(physicalApplicationPath, Constants.APP_NAME, Utils.getGalleryResourcesPath());
	}

	/// <summary>
	/// Verify that the encryption key in the application settings has been changed from its original, default value. The key is 
	/// updated with a new value if required. Each installation should have a unique key.
	/// </summary>
	private static void validateEncryptionKey(){
		// This function is called from a function using a lock, so we don't need to do our own locking.
		if (AppSettings.getInstance().getEncryptionKey() != null && AppSettings.getInstance().getEncryptionKey().equals(Constants.ENCRYPTION_KEY)){
			AppSettings.getInstance().save(null, null, null, null, Utils.generateNewEncryptionKey(), null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		}
	}

	/// <summary>
	/// Adds the user account information to gallery settings. Since the business layer does not have a reference to System.Web.dll,
	/// it could not load membership data when the gallery settings were first initialized. We know that information now, so let's
	/// populate the user accounts with the user data.
	/// </summary>
	private static void addMembershipDataToGallerySettings() throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		// The UserAccount objects should have been created and initially populated with the UserName property,
		// so we'll use the user name to retrieve the user's info and populate the rest of the properties on each object.
		GalleryBoCollection galleries = CMUtils.loadGalleries(); 
		for (GalleryBo gallery : galleries){
			GallerySettings gallerySetting = CMUtils.loadGallerySetting(gallery.getGalleryId());

			// Populate user account objects with membership data
/*			for (UserAccount userAccount in gallerySetting.UsersToNotifyWhenAccountIsCreated){
				UserUtils.LoadUser(userAccount);
			}*/

			for (UserAccount userAccount : gallerySetting.getUsersToNotifyWhenErrorOccurs()){
				UserUtils.loadUser(userAccount);
			}
		}
	}

	/// <summary>
	/// Handles the <see cref="MDS.Gallery.GalleryCreated" /> event.
	/// </summary>
	/// <param name="sender">The sender.</param>
	/// <param name="e">The <see cref="MDS.GalleryCreatedEventArgs"/> instance containing the event data.</param>
	@Override
	public void galleryCreated(GalleryCreatedEventArgs e){
	}

	/// <summary>
	/// Handles the <see cref="MDS.GallerySettings.GallerySettingsSaved" /> event.
	/// </summary>
	/// <param name="sender">The sender.</param>
	/// <param name="e">The <see cref="EventArgs"/> instance containing the event data.</param>
	@Override
	public void gallerySettingsSaved(GallerySettingsEventArgs e) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		// Finish populating those properties that weren't populated in the business layer.
		addMembershipDataToGallerySettings();
	}

	/// <summary>
	/// Gets a data entity containing application-level properties. The instance can be JSON-parsed and sent to the 
	/// browser.
	/// </summary>
	/// <returns>Returns an instance of <see cref="App" />.</returns>
	private static AppRest getAppEntity(long galleryId, HttpServletRequest request) throws UnsupportedContentObjectTypeException, InvalidGalleryException, InvalidAlbumException, InvalidMDSRoleException{
		AppRest app =  new AppRest();
		app.GalleryResourcesPath = Utils.getGalleryResourcesPath();
		app.SkinPath = Utils.getSkinPath(request);
		app.CurrentPageUrl = Utils.getCurrentPageUrl(request);
		app.AppUrl = Utils.getAppUrl(request);
		app.LatestUrl = Utils.getLatestUrl(galleryId, request);
		app.TopRatedUrl = Utils.getTopRatedUrl(galleryId, request);
		app.HostUrl = Utils.getHostUrl(request);
		app.WaitingForApprovalUrl = Utils.getWaitingForApprovalUrl(galleryId, request);
		//app.IsInReducedFunctionalityMode = AppSettings.getInstance().getLicense().IsInReducedFunctionalityMode;
		
		return app;
	}

	private static String getFriendlyStatusText(SynchronizationStatus status){
		switch (status.getStatus())	{
			case AnotherSynchronizationInProgress:
				return I18nUtils.getMessage("task.synch.Progress_Status_SynchInProgressException_Hdr");
			case Complete:
				return StringUtils.join(status.getStatus(), getProgressCount(status));
			case Error:
				return StringUtils.join(status.getStatus(), getProgressCount(status));
			case PersistingToDataStore:
				return StringUtils.join(I18nUtils.getMessage("task.synch.Progress_Status_PersistingToDataStore_Hdr"), getProgressCount(status));
			case SynchronizingFiles:
				return StringUtils.join(I18nUtils.getMessage("task.synch.Progress_Status_SynchInProgress_Hdr"), getProgressCount(status));
			case Aborted:
				return StringUtils.join(I18nUtils.getMessage("task.synch.Progress_Status_Aborted_Hdr"), getProgressCount(status));
			default: throw new InvalidEnumArgumentException("The getFriendlyStatusText() method in synchronize.aspx encountered a SynchronizationState enum value it was not designed for. This method must be updated.");
		}
	}

	private static int calculatePercentComplete(SynchronizationStatus synchStatus){
		if (synchStatus.getStatus() == SynchronizationState.SynchronizingFiles)
			return (int)(((double)synchStatus.getCurrentFileIndex() / (double)synchStatus.getTotalFileCount()) * 100);
		else
			return 100;
	}

	private static String calculateSyncRate(SynchronizationStatus synchStatus){
		if (synchStatus.getCurrentFileIndex() == 0)
			return StringUtils.EMPTY;

		//var elapsedTime = ZonedDateTime.now(ZoneOffset.UTC).Subtract(synchStatus.getBeginTimestampUtc()).TotalSeconds; UTC datetime todo
    	LocalDateTime start = synchStatus.getBeginTimestampUtc().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    	//LocalDate end = DateUtils.Now.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    	Duration duration = Duration.between(start, LocalDateTime.now());
		long elapsedTime = duration.getSeconds();

		return MessageFormat.format("{0, number, #.#} {1}", (synchStatus.getCurrentFileIndex() / elapsedTime), I18nUtils.getMessage("task.synch.Progress_SynchRate_Units"));
	}

	private static String getProgressCount(SynchronizationStatus status){
		int curFileIndex = (status.getStatus() == SynchronizationState.Complete ? status.getTotalFileCount() : status.getCurrentFileIndex());

		return I18nUtils.getMessage("task.synch.Progress_Status", curFileIndex, status.getTotalFileCount());
	}

	/// <summary>
	/// Create a sample album and content object. This method is intended to be invoked once just after the application has been 
	/// installed.
	/// </summary>
	/// <param name="galleryId">The ID for the gallery where the sample objects are to be created.</param>
	public static void createSampleObjects(long galleryId) throws IOException, UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException{
		/*if (!AppSettings.getInstance().getInstallationRequested()){
			return;
		}*/

		if (CMUtils.loadGallerySetting(galleryId).getContentObjectPathIsReadOnly())	{
			return;
		}

		Date currentTimestamp = DateUtils.Now();
		AlbumBo sampleAlbum = null;

		List<AlbumBo> albums = CMUtils.loadRootAlbumInstance(galleryId).getChildContentObjects(ContentObjectType.Album).toAlbums();
		for (AlbumBo album : albums){
			if (album.getDirectoryName().equalsIgnoreCase("Demonstration")){
				sampleAlbum = album;
				break;
			}
		}
		
		if (sampleAlbum == null){
			// Create sample album.
			sampleAlbum = CMUtils.createEmptyAlbumInstance(galleryId);

			sampleAlbum.setParent(CMUtils.loadRootAlbumInstance(galleryId));
			sampleAlbum.setTitle("Demonstration");
			sampleAlbum.setDirectoryName("Demonstration");
			sampleAlbum.setCaption(I18nUtils.getMessage("site.Welcome_Msg"));
			sampleAlbum.setCreatedByUserName(Constants.SystemUserName);
			sampleAlbum.setDateAdded(currentTimestamp);
			sampleAlbum.setLastModifiedByUserName(Constants.SystemUserName);
			sampleAlbum.setDateLastModified(currentTimestamp);
			sampleAlbum.save();
		}

		// Look for sample image in sample album.
		ContentObjectBo sampleImage = sampleAlbum.getChildContentObjects(ContentObjectType.Image).stream()
			.filter(image -> image.getOriginal().getFileName() == Constants.SAMPLE_IMAGE_FILENAME).findFirst().orElse(null);

		if (sampleImage == null){
			// Sample image not found. Pull image from assembly and save to disk (if needed), then create a content object from it.
			String sampleDirPath = FilenameUtils.concat(CMUtils.loadGallerySetting(galleryId).getFullContentObjectPath(), sampleAlbum.getDirectoryName());
			String sampleImageFilepath = FilenameUtils.concat(sampleDirPath, Constants.SAMPLE_IMAGE_FILENAME);

			if (!FileMisc.fileExists(sampleImageFilepath)){
				//File stream = FileMisc.getClassResFile("/images/" + Constants.SAMPLE_IMAGE_FILENAME, GalleryUtils.class);
				InputStream stream = GalleryUtils.class.getResourceAsStream("/images/" + Constants.SAMPLE_IMAGE_FILENAME);
				if (stream != null){
					FileUtils.copyInputStreamToFile(stream, new File(sampleImageFilepath));
				}
			}

			if (FileMisc.fileExists(sampleImageFilepath)){
				// Temporarily change a couple settings so that the thumbnail and compressed images are high quality.
				GallerySettings gallerySettings = CMUtils.loadGallerySetting(galleryId);
				int optTriggerSizeKb = gallerySettings.getOptimizedImageTriggerSizeKb();
				int thumbImageJpegQuality = gallerySettings.getThumbnailImageJpegQuality();
				gallerySettings.setThumbnailImageJpegQuality(95);
				gallerySettings.setOptimizedImageTriggerSizeKb(200);

				// Create the content object from the file.
				ContentObjectBo image = CMUtils.createImageInstance(new File(sampleImageFilepath), sampleAlbum);
				image.setTitle("Multimedia distribution service");
				image.setCreatedByUserName(Constants.SystemUserName);
				image.setDateAdded(currentTimestamp);
				image.setLastModifiedByUserName(Constants.SystemUserName);
				image.setDateLastModified(currentTimestamp);
				image.setApprovalStatus(ApprovalStatus.Approved);
				image.save();

				// Restore the default settings.
				gallerySettings.setOptimizedImageTriggerSizeKb(optTriggerSizeKb);
				gallerySettings.setThumbnailImageJpegQuality(thumbImageJpegQuality);
			}
		}
	}

	private static UserAccount createAdministrator(long galleryId) throws IOException, UnsupportedContentObjectTypeException, AddressException, InvalidUserException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, InvalidMDSRoleException, GallerySecurityException	{
		UserRest user = getAdminUserFromInstallTextFile();

		if (user == null)
			return null;

		user.GalleryId = galleryId;

		/*if (UserUtils.MembershipMds.GetType().toString() == Constants.ActiveDirectoryMembershipProviderName)
		{
			return CreateActiveDirectoryAdministrator(user);
		}
		else
		{
			return CreateMembershipAdministrator(user);
		}*/
		
		return createMembershipAdministrator(user);
	}

	/// <summary>
	/// Configures the <paramref name="user" /> as a site administrator in the gallery. The user must already exist in
	/// Active Directory. A System Administrator role is created if it does not exist.
	/// </summary>
	/// <param name="user">The user to configure as a site administrator in the gallery. The only property that is
	/// references is <see cref="User.UserName" />.</param>
	/// <returns>Returns an <see cref="UserAccount" /> representing the admin account, or null if <paramref name="user" />
	/// did not specify a username.</returns>
	/// <exception cref="System.ArgumentNullException">Thrown when <paramref name="user" /> is null.</exception>
	/// <exception cref="MDS.EventLogs.CustomExceptions.InvalidUserException">Thrown when the <paramref name="user" />
	/// does not exist in Active Directory.</exception>
	private static UserAccount createActiveDirectoryAdministrator(UserRest user) throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, InvalidMDSRoleException, InvalidUserException{
		if (user == null)
			throw new ArgumentNullException();

		/*if (UserUtils.MembershipMds.GetType().toString() != Constants.ActiveDirectoryMembershipProviderName)
		{
			throw new UnsupportedOperationException(MessageFormat.format("The function CreateActiveDirectoryAdministrator should be called only when using ActiveDirectoryMembershipProvider. Instead, {0} was detected.", UserUtils.MembershipMds.GetType()));
		}*/

		String sysAdminRole = RoleUtils.validateSysAdminRole();

		UserAccount userAccount = null;
		if (!StringUtils.isBlank(user.UserName)){
			userAccount = UserUtils.getUserAccount(user.UserName, false);

			if (userAccount == null){
				throw new InvalidUserException(MessageFormat.format("The Active Directory account {0} does not exist. Edit the text file at {1} to specify an existing AD account.", user.UserName, AppSettings.getInstallFilePath()));
			}

			if (!RoleUtils.isUserInRole(user.UserName, sysAdminRole)){
				RoleUtils.addUserToRole(user.UserName, sysAdminRole);
			}
		}

		return userAccount;
	}

	/// <summary>
	/// Configures the <paramref name="user" /> as a site administrator in the gallery. The user is created if it doesn't
	/// exist. If the user exists, the user's password is updated with the specified password. A System Administrator role
	/// is created if it does not exist.
	/// </summary>
	/// <param name="user">The user to configure as a site administrator in the gallery. The <see cref="User.UserName" /> 
	/// and <see cref="User.Password" /> properties must both be specified. If both are null or empty, null is returned.</param>
	/// <returns>Returns an <see cref="UserAccount" /> representing the admin account.</returns>
	/// <exception cref="System.ArgumentNullException">Thrown when <paramref name="user" /> is null.</exception>
	/// <exception cref="MDS.EventLogs.CustomExceptions.InvalidUserException">Thrown when <paramref name="user" />
	/// does not specify a username and password.</exception>
	private static UserAccount createMembershipAdministrator(UserRest user) throws InvalidUserException, UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, InvalidMDSRoleException, AddressException, GallerySecurityException{
		if (user == null)
			throw new ArgumentNullException();

		if (StringUtils.isBlank(user.UserName) && StringUtils.isBlank(user.Password))
			return null;

		if (!StringUtils.isBlank(user.UserName) && StringUtils.isBlank(user.Password)){
			throw new InvalidUserException(MessageFormat.format("No password was specified. Add a line to the text file at {0} that specifies a password. Example: Password=MyPassword", AppSettings.getInstallFilePath()));
		}

		UserAccount userAccount = UserUtils.getUserAccount(user.UserName, false);

		if (userAccount != null){
			/*if (!UserUtils.MembershipMds.ValidateUser(user.UserName, user.Password)){
				// Password doesn't match. Try to update.
				if (!UserUtils.EnablePasswordRetrieval)	{
					throw new Exception(MessageFormat.format("Cannot change password because the membership's password retrieval setting is disabled. The password specified in {0} does not match the existing password for user {1}, so an attempt was made to change it. However, the membership provider does not allow it. Things you can try: (1) Specify a different username in the text file. (2) Enter the correct password for the user in the text file. (3) Edit web.config to allow password retrieval: Set enablePasswordRetrieval=\"true\" in the membership section.", UserUtils.installFilePath, user.UserName));
				}

				if (!UserUtils.ChangePassword(user.UserName, UserUtils.GetPassword(user.UserName), user.Password)){
					throw new Exception(MessageFormat.format("Cannot change password. The password specified in {0} does not match the existing password for user {1}, so an attempt was made to change it. However, the membership provider wouldn't allow it and did not specify a reason. Things you can try: (1) Specify a different username in the text file. (2) Enter a different password for the user in the text file, taking care to meet length and complexity requirements.", UserUtils.installFilePath, user.UserName));
				}
			}*/

			RoleUtils.validateSysAdminRole();
			if (!RoleUtils.isUserInRole(user.UserName, I18nUtils.getMessage("site.Sys_Admin_Role_Name"))){
				RoleUtils.addUserToRole(user.UserName, I18nUtils.getMessage("site.Sys_Admin_Role_Name"));
			}
		}else{
			// User account doesn't exist. Create it.
			user.Roles = new String[] { RoleUtils.validateSysAdminRole() };

			userAccount = UserUtils.createUser(user);
		}

		return userAccount;
	}

	/// <summary>
	/// Gets a <see cref="User" /> instance having the properties specified in <see cref="UserUtils.installFilePath" />.
	/// Supports these properties: UserName, Password, Email. Returns null if none of these exist in the text file.
	/// </summary>
	/// <returns>An instance of <see cref="User" />, or null.</returns>
	public static UserRest getAdminUserFromInstallTextFile() throws IOException{
		UserRest user = null;

		try{
			BufferedReader sr = new BufferedReader(new FileReader(AppSettings.getInstallFilePath()));
			String lineText = sr.readLine();
			while (lineText != null){
				String[] kvp = StringUtils.split(lineText, '=');

				if (kvp.length == 2){
					if (kvp[0] == "UserName"){
						if (user == null)
							user = new UserRest();

						user.UserName = kvp[1].trim(); // Found username row
					}

					if (kvp[0] == "Password"){
						if (user == null)
							user = new UserRest();

						user.Password = kvp[1].trim(); // Found password row
					}

					if (kvp[0] == "Email"){
						if (user == null)
							user = new UserRest();

						user.Email = kvp[1].trim(); // Found email row
					}
				}

				lineText = sr.readLine();
			}
		}catch (FileNotFoundException ex) { }

		return user;
	}

	/// <summary>
	/// Updates the root album title so that it no longer contains the message about creating an admin account.
	/// </summary>
	private static void updateRootAlbumTitleAfterAdminCreation(long galleryId) throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException{
		AlbumBo album = CMUtils.loadRootAlbumInstance(galleryId, true, true);
		album.setCaption(I18nUtils.getMessage("site.Welcome_Msg"));
		ContentObjectUtils.saveContentObject(album);
		CacheUtils.remove(CacheItem.cm_albums);
	}

	private static void deleteInstallFile(){
		try{
			FileMisc.deleteFile(AppSettings.getInstallFilePath());
		}catch (Exception ex){
			// IIS account indentiy doesn't have permission to delete install.txt. Tell user to it manually.
			//ex.Data.add("Info", MessageFormat.format("You must manually delete the file at {0}", AppSettings.getInstallFilePath()));
			AppEventLogUtils.LogError(ex);
		}
	}

	//#endregion
}
