package com.mds.cm.service.impl;

import static java.util.concurrent.CompletableFuture.runAsync;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mds.common.Constants;
import com.mds.cm.content.AlbumBo;
import com.mds.cm.content.GalleryBo;
import com.mds.cm.content.GallerySettings;
import com.mds.cm.exception.GallerySecurityException;
import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidContentObjectException;
import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.cm.rest.SyncInitiator;
import com.mds.cm.rest.SyncOptions;
import com.mds.cm.rest.SynchStatusRest;
import com.mds.cm.service.TaskService;
import com.mds.cm.util.AlbumUtils;
import com.mds.cm.util.AppEventLogUtils;
import com.mds.cm.util.GalleryUtils;
import com.mds.sys.util.RoleUtils;
import com.mds.core.EventType;
import com.mds.core.SecurityActions;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.core.exception.WebException;
import com.mds.cm.util.CMUtils;
import com.mds.util.HelperFunctions;
import com.mds.sys.util.UserUtils;

/// <summary>
/// Contains Web API methods for invoking actions in MDS System.
/// </summary>
@Service("taskManager")
@WebService(serviceName = "TaskService", endpointInterface = "com.mds.cm.service.TaskService")
public class TaskManagerImpl implements TaskService{
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	/// <overloads>
	/// Synchronize the files in the content objects directory with the data store.
	/// </overloads>
	/// <summary>
	/// Invoke a synchronization having the specified options. It is initiated on a background thread and the current thread
	/// is immediately returned.
	/// </summary>
	/// <param name="syncOptions">An object containing settings for the synchronization.</param>
	/// <returns>An instance of <see cref="Response" />.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when the caller does not have permission to start a
	/// synchronization.</exception>
	//public void startSync(SyncOptions syncOptions, final AsyncResponse asyncResponse, HttpServletRequest request)	{
	public Response startSync(SyncOptions syncOptions, HttpServletRequest request)	{
		try	{
			//#region Check user authorization

			if (!UserUtils.isAuthenticated())
				throw new WebApplicationException(Response.Status.FORBIDDEN);

			AlbumBo album = AlbumUtils.loadAlbumInstance(syncOptions.AlbumIdToSynchronize, true, true, false);

			if (!UserUtils.isUserAuthorized(SecurityActions.Synchronize, RoleUtils.getMDSRolesForUser(), syncOptions.AlbumIdToSynchronize, album.getGalleryId(), false, album.getIsVirtualAlbum()))
				throw new WebApplicationException(Response.Status.FORBIDDEN);

			//#endregion

			syncOptions.SyncId = getSyncId(request);
			syncOptions.UserName = UserUtils.getLoginName();

			//Task.CMUtils.startNew(() => GalleryUtils.BeginSync(syncOptions), TaskCreationOptions.LongRunning);
			/*runAsync(new Runnable() {
		           public void run() {
		        	   GalleryUtils.beginSync(syncOptions);
				   }
			}); */
			CompletableFuture
			.runAsync(new Runnable() {
	           public void run() {
	        	   GalleryUtils.beginSync(syncOptions);
			   }
			});
			
			log.info("Synchronization started...");
			
			//return Response.status(200, "Synchronization started...").build(); //(HttpStatusCode.OK) { Content = new StringContent("Synchronization started...") };		
			return Response.ok().build();
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

	/// <summary>
	/// Invoke a synchronization having the specified parameters. It is initiated on a background thread and the current thread
	/// is immediately returned. This method is designed to be remotely invoked, using an URL like this:
	/// http://localhost/dev/ds/api/task/startsync?albumId=156&amp;isRecursive=false&amp;rebuildThumbnails=false&amp;rebuildOptimized=false&amp;password=1234
	/// </summary>
	/// <param name="albumId">The album ID for the album to synchronize. Specify 0 to force synchronizing all galleries
	/// from the root album.</param>
	/// <param name="isRecursive">If set to <c>true</c> the synchronization continues drilling 
	/// down into directories below the current one.</param>
	/// <param name="rebuildThumbnails">if set to <c>true</c> the thumbnail image for each media 
	/// object is deleted and overwritten with a new one based on the original file. Applies to 
	/// all content objects.</param>
	/// <param name="rebuildOptimized">if set to <c>true</c> the optimized version of each media 
	/// object is deleted and overwritten with a new one based on the original file. Only relevant 
	/// for images and for video/audio files when FFmpeg is installed and an applicable encoder
	/// setting exists.</param>
	/// <param name="password">The password that authorizes the caller to invoke a 
	/// synchronization.</param>
	/// <returns>System.String.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when <paramref name="albumId" />
	/// does not represent an existing album or some other error occurs.
	/// </exception>
	/// <remarks>NOTE TO DEVELOPER: If you change the name of this controller or method, update the property 
	/// <see cref="MDS.Web.Pages.Admin.albums.SyncAlbumUrl" />.</remarks>
	
	//public void startSync(long albumId, boolean isRecursive, boolean rebuildThumbnails, boolean rebuildOptimized, String password, final AsyncResponse asyncResponse)	{
	public String startSync(long albumId, boolean isRecursive, boolean rebuildThumbnails, boolean rebuildOptimized, String password)	{
		try	{
			SyncOptions syncOptions = getRemoteSyncOptions(albumId, isRecursive, rebuildThumbnails, rebuildOptimized);

			if (albumId > 0){
				startRemoteSync(syncOptions, password);
			}else if (albumId == 0)	{
				startRemoteSyncForAllGalleries(syncOptions, password);
			}else{
				throw new InvalidAlbumException();
			}
		}catch (InvalidAlbumException ae){
			throw new WebApplicationException(MessageFormat.format("Could not find album with ID = {0}", albumId), Response.Status.NOT_FOUND); //(HttpStatusCode.NotFound)
		}catch (WebApplicationException we)	{
			// Just rethrow - we don't want to log these.
			throw we;
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}

		return "Starting synchronization on background thread...";
	}

	/// <summary>
	/// Retrieves the status of a synchronization for the gallery having the ID <paramref name="id"/>.
	/// </summary>
	/// <param name="id">The gallery ID. We must name the parameter 'id' rather than galleryId because the routing
	/// defined in <see cref="MDS.Web.HttpModule.MDSHttpApplication" /> requires that it have this name.</param>
	/// <returns>An instance of <see cref="SynchStatusRest" />.</returns>
	
	public SynchStatusRest statusSync(long id, HttpServletRequest request)	{
		try{
			return GalleryUtils.getSyncStatus(getSyncId(request), id);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

	/// <summary>
	/// Aborts a synchronization for the gallery having the ID <paramref name="id"/>.
	/// </summary>
	/// <param name="id">The gallery ID. We must name the parameter 'id' rather than galleryId because the routing
	/// defined in <see cref="MDS.Web.HttpModule.MDSHttpApplication" /> requires that it have this name.</param>
	/// <returns>A String.</returns>
	
	public String abortSync(long id, HttpServletRequest request){
		try	{
			GalleryUtils.abortSync(getSyncId(request), id);

			return "Aborting synchronization...";
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

	/// <summary>
	/// Logs off the current user.
	/// </summary>
	/// <returns>A String.</returns>
	
	public String logoff()
	{
		try{
			UserUtils.logOffUser();

			return "Current user has been logged off...";
		}catch (Exception ex) {
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

	/// <summary>
	/// Clears all caches relevant to the specified album <paramref name="id" />. The logged on user 
	/// must have edit permission to this album.
	/// </summary>
	/// <param name="id">The album ID. All caches containing data relevant to this album are 
	/// cleared.</param>
	
	public String purgeCache(long id){
		AlbumBo album = null;

		try{
			album = AlbumUtils.loadAlbumInstance(id, false);

			// Get a list of all actions that require purging the cache. User must have one of these
			// permissions in order to purge the cache.
			final int securityActions = SecurityActions.AddChildAlbum.value() | SecurityActions.EditAlbum.value() 
					| SecurityActions.DeleteChildAlbum.value() | SecurityActions.AddContentObject.value() | SecurityActions.EditContentObject.value() 
					| SecurityActions.DeleteContentObject.value();

			if (!UserUtils.isUserAuthorized(securityActions, id, album.getGalleryId(), album.getIsPrivate(), album.getIsVirtualAlbum()))
				return "Insufficient permission for purging the cache.";

			HelperFunctions.purgeCache();
			return "Cache purged...";
		}catch (Exception ex){
			if (album != null)
				AppEventLogUtils.LogError(ex, album.getGalleryId());
			else
				AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

	//#region Private Methods

	/// <summary>
	/// Gets the value of the X-ServerTask-TaskId request header.
	/// </summary>
	/// <returns>System.String.</returns>
	private static String getSyncId(HttpServletRequest request)	{
		return request.getHeader("X-ServerTask-TaskId");
	}

	/// <summary>
	/// Starts a synchronization on a background thread for the album specified in <paramref name="syncOptions" />.
	/// </summary>
	/// <param name="syncOptions">The synchronization options.</param>
	/// <param name="password">The password that allows remote access to the synchronization API.</param>
	//private static void startRemoteSync(SyncOptions syncOptions, String password, final AsyncResponse asyncResponse) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException{
	private static void startRemoteSync(SyncOptions syncOptions, String password) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException{
		AlbumBo album = AlbumUtils.loadAlbumInstance(syncOptions.AlbumIdToSynchronize, true, true, true);

		if (!validateRemoteSync(album, password)){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}

		//Task.CMUtils.startNew(() => GalleryUtils.BeginSync(syncOptions), TaskCreationOptions.LongRunning);
		/*runAsync(new Runnable() {
	           public void run() {
	        	   GalleryUtils.beginSync(syncOptions);
			   }
		}); */
		CompletableFuture.runAsync(new Runnable() {
           public void run() {
        	   GalleryUtils.beginSync(syncOptions);
		   }
		});
		/*.supplyAsync(()->{
			GalleryUtils.beginSync(syncOptions);
			
			return "Starting synchronization on background thread...";
		})
		.thenApply((result) -> asyncResponse.resume(result));*/
	}

	/// <summary>
	/// Starts a synchronization on a background thread for all galleries.
	/// </summary>
	/// <param name="syncOptions">The synchronization options.</param>
	/// <param name="password">The password that allows remote access to the synchronization API.</param>
	//private static void startRemoteSyncForAllGalleries(SyncOptions syncOptions, String password, final AsyncResponse asyncResponse) throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException{
	private static void startRemoteSyncForAllGalleries(SyncOptions syncOptions, String password) throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException{
		// User is requesting that all galleries be synchronized.
		for (GalleryBo gallery : CMUtils.loadGalleries()){
			AlbumBo rootAlbum = CMUtils.loadRootAlbumInstance(gallery.getGalleryId(), false, false);

			if (!validateRemoteSync(rootAlbum, password))
				continue;

			SyncOptions copiedSyncOptions = copySyncOptions(syncOptions);
			copiedSyncOptions.AlbumIdToSynchronize = rootAlbum.getId();

			//Task.CMUtils.startNew(() => GalleryUtils.BeginSync(copiedSyncOptions), TaskCreationOptions.LongRunning);
			/*runAsync(new Runnable() {
		           public void run() {
		        	   GalleryUtils.beginSync(copiedSyncOptions);
				   }
			});*/
			CompletableFuture.runAsync(new Runnable() {
	           public void run() {
	        	   GalleryUtils.beginSync(copiedSyncOptions);
			   }
			});
			/*.supplyAsync(()->{
				GalleryUtils.beginSync(copiedSyncOptions);
				
				return "Starting synchronization on background thread...";
			})
			.thenApply((result) -> asyncResponse.resume(result));*/
		}
	}

	/// <summary>
	/// Generate an instance of <see cref="SyncOptions" /> corresponding to the specified parameters and configured for remotely
	/// initiating a synchronization. 
	/// </summary>
	/// <param name="albumId">The ID of the album to synchronize.</param>
	/// <param name="isRecursive">If set to <c>true</c> the synchronization continues drilling 
	/// down into directories below the current one.</param>
	/// <param name="rebuildThumbnails">if set to <c>true</c> the thumbnail image for each media 
	/// object is deleted and overwritten with a new one based on the original file. Applies to 
	/// all content objects.</param>
	/// <param name="rebuildOptimized">if set to <c>true</c> the optimized version of each media 
	/// object is deleted and overwritten with a new one based on the original file. Only relevant 
	/// for images and for video/audio files when FFmpeg is installed and an applicable encoder
	/// setting exists.</param>
	/// <returns>An instance of <see cref="SyncOptions" />.</returns>
	private static SyncOptions getRemoteSyncOptions(long albumId, boolean isRecursive, boolean rebuildThumbnails, boolean rebuildOptimized)	{
		SyncOptions syncOptions = new SyncOptions();
		syncOptions.AlbumIdToSynchronize = albumId;
		syncOptions.IsRecursive = isRecursive;
		syncOptions.RebuildThumbnails = rebuildThumbnails;
		syncOptions.RebuildOptimized = rebuildOptimized;
		syncOptions.SyncId = UUID.randomUUID().toString();
		syncOptions.SyncInitiator = SyncInitiator.RemoteApp;
		syncOptions.UserName = Constants.SystemUserName;
		
		return syncOptions;
	}

	/// <summary>
	/// Generate a new instance of <see cref="SyncOptions" /> having the same properties as <paramref name="syncOptions" />, 
	/// with the exception of <see cref="SyncOptions.SyncId" />, which is set to a new value.
	/// </summary>
	/// <param name="syncOptions">The synchronization options to copy.</param>
	/// <returns>An instance of <see cref="SyncOptions" />.</returns>
	private static SyncOptions copySyncOptions(SyncOptions syncOptions)	{
		SyncOptions syncOptionsCopy = new SyncOptions();
		syncOptionsCopy.AlbumIdToSynchronize = syncOptions.AlbumIdToSynchronize;
		syncOptionsCopy.IsRecursive = syncOptions.IsRecursive;
		syncOptionsCopy.RebuildThumbnails = syncOptions.RebuildThumbnails;
		syncOptionsCopy.RebuildOptimized = syncOptions.RebuildOptimized;
		syncOptionsCopy.SyncId = UUID.randomUUID().toString();
		syncOptionsCopy.SyncInitiator = syncOptions.SyncInitiator;
		syncOptionsCopy.UserName = syncOptions.UserName;
		
		return syncOptionsCopy;
	}

	/// <summary>
	/// Validates that remote syncing is enabled and that the specified <paramref name="password" /> is valid.
	/// </summary>
	/// <param name="album">The album to synchronize.</param>
	/// <param name="password">The password that allows remote access to the synchronization API.</param>
	/// <returns><c>true</c> if validation succeeds, <c>false</c> otherwise</returns>
	private static boolean validateRemoteSync(AlbumBo album, String password) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		GallerySettings gallerySettings = CMUtils.loadGallerySetting(album.getGalleryId());

		if (!gallerySettings.getEnableRemoteSync())	{
			AppEventLogUtils.LogEvent(MessageFormat.format( "Cannot start synchronization: A web request to start synchronizing album '{0}' (ID {1}) was received, but the gallery is currently configured to disallow remote synchronizations. This feature can be enabled on the Albums page in the Site admin area.", album.getTitle(), album.getId()), album.getGalleryId(), EventType.Info);

			return false;
		}

		if (!gallerySettings.getRemoteAccessPassword().equals(password))	{
			AppEventLogUtils.LogEvent(MessageFormat.format( "Cannot start synchronization: A web service request to start synchronizing album '{0}' (ID {1}) was received, but the specified password is incorrect.", album.getTitle(), album.getId()), album.getGalleryId(), EventType.Info);

			return false;
		}

		return true;
	}

	//#endregion
}