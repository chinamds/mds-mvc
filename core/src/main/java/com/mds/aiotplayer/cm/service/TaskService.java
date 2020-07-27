package com.mds.aiotplayer.cm.service;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mds.aiotplayer.cm.rest.SyncOptions;
import com.mds.aiotplayer.cm.rest.SynchStatusRest;

/// <summary>
/// Contains Web API methods for invoking actions in MDS System.
/// </summary>
@WebService
@Path("/task")
public interface TaskService{
	/// <overloads>
	/// Synchronize the files in the content objects directory with the data store.
	/// </overloads>
	/// <summary>
	/// Invoke a synchronization having the specified options. It is initiated on a background thread and the current thread
	/// is immediately returned.
	/// </summary>
	/// <param name="syncOptions">An object containing settings for the synchronization.</param>
	/// <returns>An instance of <see cref="Response" />.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when the caller does not have permission to start a
	/// synchronization.</exception>
	@POST
	@Path("/startsync")
	@Consumes({MediaType.APPLICATION_JSON})
	Response startSync(SyncOptions syncOptions, @Context HttpServletRequest request);
	//void startSync(SyncOptions syncOptions, @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request);

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
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when <paramref name="albumId" />
	/// does not represent an existing album or some other error occurs.
	/// </exception>
	/// <remarks>NOTE TO DEVELOPER: If you change the name of this controller or method, update the property 
	/// <see cref="MDS.Web.Pages.Admin.albums.SyncAlbumUrl" />.</remarks>
	@GET
	@Path("/startsync")
	String startSync(@QueryParam("id") long albumId, @QueryParam("isRecursive") boolean isRecursive, @QueryParam("rebuildThumbnails") boolean rebuildThumbnails
			, @QueryParam("rebuildOptimized") boolean rebuildOptimized, @QueryParam("password") String password);
	/*void startSync(@QueryParam("id") long albumId, @QueryParam("isRecursive") boolean isRecursive, @QueryParam("rebuildThumbnails") boolean rebuildThumbnails
			, @QueryParam("rebuildOptimized") boolean rebuildOptimized, @QueryParam("password") String password, @Suspended final AsyncResponse asyncResponse);*/

	/// <summary>
	/// Retrieves the status of a synchronization for the gallery having the ID <paramref name="id"/>.
	/// </summary>
	/// <param name="id">The gallery ID. We must name the parameter 'id' rather than galleryId because the routing
	/// defined in <see cref="MDS.Web.HttpModule.MDSHttpApplication" /> requires that it have this name.</param>
	/// <returns>An instance of <see cref="SynchStatusRest" />.</returns>
	@GET
	@Path("/{id}/statussync")
	@Produces({ MediaType.APPLICATION_JSON })
	SynchStatusRest statusSync(@PathParam("id") long id, @Context HttpServletRequest request);

	/// <summary>
	/// Aborts a synchronization for the gallery having the ID <paramref name="id"/>.
	/// </summary>
	/// <param name="id">The gallery ID. We must name the parameter 'id' rather than galleryId because the routing
	/// defined in <see cref="MDS.Web.HttpModule.MDSHttpApplication" /> requires that it have this name.</param>
	/// <returns>A String.</returns>
	@GET
	@Path("/{id}/abortsync")
	String abortSync(@PathParam("id") long id, @Context HttpServletRequest request);

	/// <summary>
	/// Logs off the current user.
	/// </summary>
	/// <returns>A String.</returns>
	@POST
	@Path("/logoff")
	String logoff();

	/// <summary>
	/// Clears all caches relevant to the specified album <paramref name="id" />. The logged on user 
	/// must have edit permission to this album.
	/// </summary>
	/// <param name="id">The album ID. All caches containing data relevant to this album are 
	/// cleared.</param>
	@GET
	@Path("/{id}/purgecache")
	String purgeCache(@PathParam("id") long id);
}