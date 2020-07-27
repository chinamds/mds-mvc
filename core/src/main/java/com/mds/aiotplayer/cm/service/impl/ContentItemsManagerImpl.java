package com.mds.aiotplayer.cm.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

import static java.util.concurrent.CompletableFuture.runAsync;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.content.AddContentObjectSettings;
import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.sys.util.SecurityGuard;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.rest.CMData;
import com.mds.aiotplayer.cm.rest.CMDataLoadOptions;
import com.mds.aiotplayer.cm.rest.MediaItem;
import com.mds.aiotplayer.cm.rest.MetaItemRest;
import com.mds.aiotplayer.cm.service.ContentItemsManager;
import com.mds.aiotplayer.cm.service.ContentItemsService;
import com.mds.aiotplayer.cm.util.AppEventLogUtils;
import com.mds.aiotplayer.cm.util.ContentObjectUtils;
import com.mds.aiotplayer.cm.util.GalleryUtils;
import com.mds.aiotplayer.cm.util.MediaSourceBuilder;
import com.mds.aiotplayer.common.service.MultiPartFileSenderService;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.core.ActionResult;
import com.mds.aiotplayer.core.ActionResultStatus;
import com.mds.aiotplayer.core.ApprovalStatus;
import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.core.DisplayObjectType;
import com.mds.aiotplayer.core.EventType;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.util.ContentObjectHtmlBuilder;
import com.mds.aiotplayer.util.FileMisc;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.sys.util.UserUtils;

/// <summary>
/// Contains methods for Web API access to content objects.
/// </summary>
@Service("contentItemsManager")
@WebService(serviceName = "ContentItemsService", endpointInterface = "com.mds.aiotplayer.cm.service.ContentItemsService")
public class ContentItemsManagerImpl implements ContentItemsManager, ContentItemsService{
	
	@Resource MultiPartFileSenderService multiPartFileSenderService;
	/// <summary>
	/// Gets the content object with the specified <paramref name="id" />.
	/// Example: api/mediaitems/4/get
	/// </summary>
	/// <param name="id">The content object ID.</param>
	/// <returns>An instance of <see cref="MediaItem" />.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when the content object is not found, the user
	/// doesn't have permission to view it, or some other error occurs.</exception>
	@Override
	public MediaItem get(long id, HttpServletRequest request){
		try	{
			ContentObjectBo contentObject = CMUtils.loadContentObjectInstance(id);
			SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), contentObject.getParent().getId(), contentObject.getGalleryId(), UserUtils.isAuthenticated(), contentObject.getParent().getIsPrivate(), ((AlbumBo)contentObject.getParent()).getIsVirtualAlbum());
			List<ContentObjectBo> siblings = contentObject.getParent().getChildContentObjects(ContentObjectType.ContentObject, ApprovalStatus.All, !UserUtils.isAuthenticated()).toSortedList();
			int contentObjectIndex = siblings.indexOf(contentObject);

			return ContentObjectUtils.toMediaItem(contentObject, contentObjectIndex + 1, ContentObjectHtmlBuilder.getContentObjectHtmlBuilderOptions(contentObject, request), request);
		}catch (InvalidContentObjectException ce){
			throw new WebApplicationException(MessageFormat.format("Could not find content object with ID = {0}", id), Response.Status.NOT_FOUND);
			//ReasonPhrase = "Content Object Not Found"
		}catch (GallerySecurityException ge){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

	/// <summary>
	/// Gets a comprehensive set of data about the specified content object.
	/// </summary>
	/// <param name="id">The content object ID.</param>
	/// <returns>An instance of <see cref="CMData" />.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when the content object is not found, the user
	/// doesn't have permission to view it, or some other error occurs.</exception>
	@Override
	public CMData getInflatedContentObject(long id, HttpServletRequest request)	{
		try{
			ContentObjectBo contentObject = CMUtils.loadContentObjectInstance(id);
			return GalleryUtils.getCMDataForContentObject(contentObject, (AlbumBo)contentObject.getParent(), new CMDataLoadOptions(true, false), request);
		}catch (InvalidContentObjectException ce){
			throw new WebApplicationException(MessageFormat.format("Could not find content object with ID = {0}", id), Response.Status.NOT_FOUND);
			//ReasonPhrase = "Content Object Not Found"
		}catch (GallerySecurityException ge){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

	/// <summary>
	/// Gets the meta items for the specified content object <paramref name="id" />.
	/// </summary>
	/// <param name="id">The content object ID.</param>
	/// <returns>List{MetaItemRest}.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when the content object is not found, the user
	/// doesn't have permission to view it, or some other error occurs.</exception>
	@Override
	public List<MetaItemRest> getMetaItemsForContentObjectId(long id, HttpServletRequest request){
		// GET /api/mediaobjects/12/meta - Gets metadata items for content object #12
		try{
			return Lists.newArrayList(ContentObjectUtils.getMetaItemsForContentObject(id, request));
		}catch (InvalidContentObjectException ce){
			throw new WebApplicationException(MessageFormat.format("Could not find content object with ID = {0}", id), Response.Status.NOT_FOUND);
			//ReasonPhrase = "Content Object Not Found"
		}catch (GallerySecurityException ge){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

	/// <summary>
	/// Adds a media file to an album. Prior to calling this method, the file should exist in the
	/// temporary upload directory (<see cref="GlobalConstants.TempUploadDirectory" />) in the
	/// App_Data directory with the name <see cref="AddContentObjectSettings.FileNameOnServer" />. The
	/// file is copied to the destination album and given the name of
	/// <see cref="AddContentObjectSettings.FileName" /> (instead of whatever name it currently has, which
	/// may contain a GUID).
	/// </summary>
	/// <param name="settings">The settings that contain data and configuration options for the media file.</param>
	/// <returns>List{ActionResult}.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when the user does not have permission to add media
	/// objects or some other error occurs.</exception>
	//public List<ActionResult> createFromFile(AddContentObjectSettings settings, final AsyncResponse asyncResponse, HttpServletRequest request){
	@Override
	public void createFromFile(AddContentObjectSettings settings, final AsyncResponse asyncResponse, HttpServletRequest request){
		try{
			settings.CurrentUserName = UserUtils.getLoginName();
			String fileExt = FileMisc.getExt(settings.FileName);

			if (fileExt != null && fileExt.equalsIgnoreCase(".zip")){
				CompletableFuture
				.supplyAsync(()->{
		            	try {
		        		   List<ActionResult> results = ContentObjectUtils.addContentObject(settings);

		        		   // Since we don't have access to the user's session here, let's create a log entry.
		        		   logUploadZipFileResults(results, settings);
						} catch (UnsupportedContentObjectTypeException | UnsupportedImageTypeException
								| InvalidContentObjectException | GallerySecurityException | InvalidGalleryException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            	
		            	return Lists.newArrayList((new ActionResult(settings.FileName, ActionResultStatus.Async.toString())));
				})
				.thenApply((result) -> asyncResponse.resume(result));
				/*List<ActionResult> results = ContentObjectUtils.addContentObject(settings);

     		    // Since we don't have access to the user's session here, let's create a log entry.
     		    logUploadZipFileResults(results, settings);

     		    asyncResponse.resume(results);*/
				//return Lists.newArrayList((new ActionResult(settings.FileName, ActionResultStatus.Async.toString())));
			}else{
				List<ActionResult> results = ContentObjectUtils.addContentObject(settings);

				HelperFunctions.addResultToSession(request, results);

				//return results;
				asyncResponse.resume(results);
			}
		}catch (GallerySecurityException ge){
			AppEventLogUtils.LogEvent("Unauthorized access detected. The security system prevented a user from adding a content object.", null, EventType.Warning);
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

	private static void logUploadZipFileResults(List<ActionResult> results, AddContentObjectSettings settings) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException{
		boolean isSuccessful = results.stream().allMatch(r -> r.Status == ActionResultStatus.Success.toString());
		Long galleryId = null;
		AlbumBo album = null;
		try	{
			album = CMUtils.loadAlbumInstance(settings.AlbumId, false);
			galleryId = album.getGalleryId();
		}
		catch (InvalidAlbumException ae) { }

		if (isSuccessful){
			AppEventLogUtils.LogEvent(MessageFormat.format( "{0} files were successfully extracted from the file '{1}' and added to album '{2}'.", results.size(), settings.FileName, album != null ? album.getTitle() : "<Unknown>"), galleryId);

			return;
		}

		// If we get here at least one of the results was an info, warning, error, etc.
		List<ActionResult> succesfulResults = results.stream().filter(m -> m.Status == ActionResultStatus.Success.toString()).collect(Collectors.toList());
		List<ActionResult> unsuccesfulResults = results.stream().filter(m -> m.Status != ActionResultStatus.Success.toString()).collect(Collectors.toList());
		String msg = MessageFormat.format( "{0} items in the uploaded file '{1}' were added to the gallery, but {2} files were skipped. Review the details for additional information. The file was uploaded by user {3}.", succesfulResults.size(), settings.FileName, unsuccesfulResults.size(), settings.CurrentUserName);
		UnsupportedContentObjectTypeException ex = new UnsupportedContentObjectTypeException(msg, null);

		int i = 1;
		for (ActionResult result : unsuccesfulResults){
			ex.Data.put("File " + i++, MessageFormat.format( "{0}: {1}", result.Title, result.Message));
		}

		AppEventLogUtils.LogError(ex, galleryId);
	}

	/// <summary>
	/// Persists the media item to the data store. The current implementation requires that
	/// an existing item exist in the data store.
	/// </summary>
	/// <param name="mediaItem">An instance of <see cref="MediaItem"/> to persist to the data 
	/// store.</param>
	/// <exception cref="System.Web.Http.WebApplicationException"></exception>
	@Override
	public ActionResult putContentItem(MediaItem mediaItem){
		try	{
			ContentObjectBo mo = CMUtils.loadContentObjectInstance(mediaItem.Id, true);

			boolean isUserAuthorized = UserUtils.isUserAuthorized(SecurityActions.EditContentObject, mo.getParent().getId(), mo.getGalleryId(), mo.getIsPrivate(), ((AlbumBo)mo.getParent()).getIsVirtualAlbum());
			if (!isUserAuthorized || mo.getApprovalStatus() == ApprovalStatus.Approved)	{
				AppEventLogUtils.LogEvent(MessageFormat.format( "Unauthorized access detected. The security system prevented a user from editing content object {0}.", mo.getId()), mo.getGalleryId(), EventType.Warning);

				throw new WebApplicationException(Response.Status.FORBIDDEN);
			}

			mo.setTitle(mediaItem.Title);
			ContentObjectUtils.saveContentObject(mo);

			HelperFunctions.purgeCache();

			return new ActionResult(
				ActionResultStatus.Success.toString(),
				StringUtils.EMPTY,
				StringUtils.EMPTY,
				mediaItem);
		}catch (InvalidContentObjectException ce){
			throw new WebApplicationException(MessageFormat.format("Could not find media item with ID = {0}", mediaItem.Id), Response.Status.NOT_FOUND);
			//ReasonPhrase = "Content Object Not Found"
		}catch (WebApplicationException we){
			throw we; // Rethrow, since we've already logged it above
		}
		catch (Exception ex)
		{
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

	/// <summary>
	/// Permanently deletes the specified content object from the file system and data store. No action is taken if the
	/// user does not have delete permission.
	/// </summary>
	/// <param name="id">The ID of the content object to be deleted.</param>
	//public Response DeleteContentItem(MediaItem mediaItem)
	@Override
	public void delete(long id){
		ContentObjectBo mo = null;

		try
		{
			mo = CMUtils.loadContentObjectInstance(id);
			boolean isUserAuthorized = UserUtils.isUserAuthorized(SecurityActions.DeleteContentObject, mo.getParent().getId(), mo.getGalleryId(), mo.getIsPrivate(), ((AlbumBo)mo.getParent()).getIsVirtualAlbum());
			//var isApproveUserAuthorized = UserUtils.isUserAuthorized(SecurityActions.ApproveContentObject, mo.getParent().getId(), mo.getGalleryId(), mo.getIsPrivate(), ((AlbumBo)mo.getParent()).getIsVirtualAlbum());
			boolean isGalleryReadOnly = CMUtils.loadGallerySetting(mo.getGalleryId()).getContentObjectPathIsReadOnly();
			if (!isUserAuthorized || isGalleryReadOnly)// || (mo.ApprovalStatus == ApprovalStatus.Approved && !isApproveUserAuthorized))
			{
				AppEventLogUtils.LogEvent(MessageFormat.format( "Unauthorized access detected. The security system prevented a user from deleting content object {0}.", mo.getId()), mo.getGalleryId(), EventType.Warning);

				throw new WebApplicationException(Response.Status.FORBIDDEN);
			}

			mo.delete();
			HelperFunctions.purgeCache();
		}catch (InvalidContentObjectException ce){
			// HTTP specification says the DELETE method must be idempotent, so deleting a nonexistent item must have 
			// the same effect as deleting an existing one. So we do nothing here and let the method return HttpStatusCode.OK.
		}catch (WebApplicationException we)	{
			throw we; // Rethrow, since we've already logged it above
		}catch (Exception ex){
			if (mo != null)
				AppEventLogUtils.LogError(ex, mo.getGalleryId());
			else
				AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}
	
	@Override
	public void getMedia(String id, String displayType, String galleryId, String sendAsAttachment, HttpServletRequest request, HttpServletResponse response) throws Exception {
		MediaSourceBuilder mediaSourceBuilder = new MediaSourceBuilder(id, displayType, galleryId, sendAsAttachment);
		//mediaSourceBuilder.initializeRequest(id, displayType, galleryId, request);
		/*DisplayObjectType dt = DisplayObjectType.parse(displayType);
		if (id>0) {
			ContentObjectBo contentObject = CMUtils.loadContentObjectInstance(id);
			String contentFile = null;
			if (contentObject != null) {
				switch (dt)			{
					case Thumbnail:
						contentFile = contentObject.getThumbnail().getFileNamePhysicalPath();
						break;
					case Optimized:
						contentFile = contentObject.getOptimized().getFileNamePhysicalPath();
						break;
					case Original:
						contentFile = contentObject.getOriginal().getFileNamePhysicalPath();
						break;
				}
			}*/
		    	
	    	//return new FileSystemResource(contentFile);
	    	//File file = new File(contentFile);
		try {
	    	multiPartFileSenderService.fromMediaSourceBuilder(mediaSourceBuilder)
	        .with(request)
	        .with(response)
	        .serveResource();
		}finally {
			//mediaSourceBuilder.CleanUpResources();
		}
		//}
	}
}