package com.mds.cm.service;

import java.util.List;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import com.mds.cm.content.AddContentObjectSettings;
import com.mds.cm.rest.CMData;
import com.mds.cm.rest.MediaItem;
import com.mds.cm.rest.MetaItemRest;
import com.mds.core.ActionResult;

/// <summary>
/// Contains methods for Web API access to content objects.
/// </summary>
@WebService
@Path("/contentitems")
public interface ContentItemsService{
	/// <summary>
	/// Gets the content object with the specified <paramref name="id" />.
	/// Example: api/mediaitems/4/get
	/// </summary>
	/// <param name="id">The content object ID.</param>
	/// <returns>An instance of <see cref="MediaItem" />.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when the content object is not found, the user
	/// doesn't have permission to view it, or some other error occurs.</exception>
	@GET
	public MediaItem get(long id, @Context HttpServletRequest request);

	/// <summary>
	/// Gets a comprehensive set of data about the specified content object.
	/// </summary>
	/// <param name="id">The content object ID.</param>
	/// <returns>An instance of <see cref="CMData" />.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when the content object is not found, the user
	/// doesn't have permission to view it, or some other error occurs.</exception>
	@Path("/{id}/Inflated")
	@GET
	public CMData getInflatedContentObject(@PathParam("id") long id, @Context HttpServletRequest request);

	/// <summary>
	/// Gets the meta items for the specified content object <paramref name="id" />.
	/// </summary>
	/// <param name="id">The content object ID.</param>
	/// <returns>List{MetaItemRest}.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when the content object is not found, the user
	/// doesn't have permission to view it, or some other error occurs.</exception>
	@Path("/{id}/meta")
	@GET
	public List<MetaItemRest> getMetaItemsForContentObjectId(@PathParam("id") long id, @Context HttpServletRequest request);

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
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when the user does not have permission to add media
	/// objects or some other error occurs.</exception>
	@Path("createfromfile")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({MediaType.APPLICATION_JSON})
	//public List<ActionResult> createFromFile(AddContentObjectSettings settings, @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request);
	public void createFromFile(AddContentObjectSettings settings, @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request);

	/// <summary>
	/// Persists the media item to the data store. The current implementation requires that
	/// an existing item exist in the data store.
	/// </summary>
	/// <param name="mediaItem">An instance of <see cref="MediaItem"/> to persist to the data 
	/// store.</param>
	/// <exception cref="System.Web.Http.HttpResponseException"></exception>
	@POST
	public ActionResult putContentItem(MediaItem mediaItem);

	/// <summary>
	/// Permanently deletes the specified content object from the file system and data store. No action is taken if the
	/// user does not have delete permission.
	/// </summary>
	/// <param name="id">The ID of the content object to be deleted.</param>
	//public Response DeleteContentItem(MediaItem mediaItem)
	@DELETE
	@Path("{id}")
	public void delete(@PathParam("id") long id);
	
	@GET
	@Path("/getmedia")
   /* @Produces({"image/png", "image/jpg", "image/jpeg", "image/gif", "image/bmp", "video/mp4", "video/mov", MediaType.APPLICATION_OCTET_STREAM})*/
	public void getMedia(@QueryParam("moid") String id, @QueryParam("dt") String displayType, @QueryParam("g") String galleryId, @QueryParam("sa") String sendAsAttachment, @Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception;
}