/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service.impl;

import java.util.List;

import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mds.aiotplayer.cm.content.ContentConversionQueue;
import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.cm.content.ContentQueueItem;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.service.ContentQueueItemService;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.cm.util.AppEventLogUtils;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.HelperFunctions;

/// <summary>
/// Contains methods for Web API access to the media processing queue.
/// </summary>
@Service("contentQueueItemManager")
@WebService(serviceName = "ContentQueueItemService", endpointInterface = "com.mds.aiotplayer.cm.service.ContentQueueItemService")
public class ContentQueueItemManagerImpl implements ContentQueueItemService {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	/// <summary>
	/// Permanently deletes the specified queue items from the data store. Current user must be
	/// a gallery administrator for the gallery the media item belongs to; otherwise no action 
	/// is taken.
	/// </summary>
	/// <param name="mediaQueueIds">The media queue IDs.</param>
	@Transactional
	public Response delete(long[] mediaQueueIds){
		try{
			for (long mediaQueueId : mediaQueueIds){
				delete(mediaQueueId);
			}

			//return Response.status(200, "Successfully deleted...").build(); // { Content = new StringContent("Successfully deleted...") };
			log.info("Successfully deleted...");
			
			return Response.ok().build();
		}catch (InvalidContentObjectException ce){
			// HTTP specification says the DELETE method must be idempotent, so deleting a nonexistent item must have 
			// the same effect as deleting an existing one. So we do nothing here and let the method return HttpStatusCode.OK.
			//return Response.status(200, "Successfully deleted...").build();//new Response(HttpStatusCode.OK) { Content = new StringContent("Successfully deleted...") };
			log.info("Successfully deleted...");
			
			return Response.ok().build();
		}catch (GallerySecurityException ge){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

	/// <summary>
	/// Permanently deletes the specified queue item from the data store. Current user must be
	/// a gallery administrator for the gallery the media item belongs to; otherwise no action 
	/// is taken.
	/// </summary>
	/// <param name="id">The media queue ID.</param>
	private void delete(long id) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InterruptedException, GallerySecurityException, InvalidMDSRoleException, InvalidGalleryException{
		ContentQueueItem item = ContentConversionQueue.getInstance().get(id);

		if (item == null)
			return;

		ContentObjectBo mo = CMUtils.loadContentObjectInstance(item.ContentObjectId);

		if (UserUtils.isCurrentUserGalleryAdministrator(mo.getGalleryId()))	{
			ContentConversionQueue.getInstance().removeContentQueueItem(id);
		}else{
			throw new GallerySecurityException();
		}
	}
}