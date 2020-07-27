package com.mds.aiotplayer.cm.service;

import javax.jws.WebService;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/// <summary>
/// Contains methods for Web API access to the media processing queue.
/// </summary>
@WebService
@Path("/contentqueueitem")
public interface ContentQueueItemService{
	/// <summary>
	/// Permanently deletes the specified queue items from the data store. Current user must be
	/// a gallery administrator for the gallery the media item belongs to; otherwise no action 
	/// is taken.
	/// </summary>
	/// <param name="mediaQueueIds">The media queue IDs.</param>
	public Response delete(long[] mediaQueueIds);
}