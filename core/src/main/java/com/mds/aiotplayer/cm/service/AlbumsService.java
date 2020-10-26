/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service;

import java.util.HashMap;
import java.util.List;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mds.aiotplayer.cm.rest.AlbumAction;
import com.mds.aiotplayer.cm.rest.AlbumRest;
import com.mds.aiotplayer.cm.rest.CMData;
import com.mds.aiotplayer.cm.rest.ContentItem;
import com.mds.aiotplayer.cm.rest.JsTreeNode;
import com.mds.aiotplayer.cm.rest.MediaItem;
import com.mds.aiotplayer.cm.rest.MetaItemRest;
import com.mds.aiotplayer.cm.rest.TreeView;
import com.mds.aiotplayer.common.Constants;

/// <summary>
/// Contains methods for Web API access to albums.
/// </summary>
@WebService
@Path("/albumrests")
public interface AlbumsService{
	/// <summary>
	/// Gets the album with the specified <paramref name="id" />. The properties 
	/// <see cref="AlbumRest.ContentItems" /> and <see cref="AlbumRest.ContentItems" /> 
	/// are set to null to keep the instance small. Example: api/albums/4/
	/// </summary>
	/// <param name="id">The album ID.</param>
	/// <returns>An instance of <see cref="AlbumRest" />.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException"></exception>
	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public AlbumRest get(@PathParam("id") long id, @Context HttpServletRequest request);

	/// <summary>
	/// Gets a comprehensive set of data about the specified album.
	/// </summary>
	/// <param name="id">The album ID.</param>
	/// <param name="top">Specifies the number of child gallery objects to retrieve. Specify 0 to retrieve all items.</param>
	/// <param name="skip">Specifies the number of child gallery objects to skip.</param>
	/// <returns>An instance of <see cref="CMData" />.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">
	/// </exception>
	/// <exception cref="Response">
	/// </exception>
	/// <exception cref="StringContent"></exception>
	@Path("/{id}/Inflated")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public CMData getInflatedAlbum(@PathParam("id") long id, @QueryParam("top") @DefaultValue("0") int top, @QueryParam("skip") @DefaultValue("0") int skip, @Context HttpServletRequest request);

	/// <summary>
	/// Gets the gallery items for the specified album, optionally sorting the results.
	/// </summary>
	/// <param name="id">The album ID.</param>
	/// <param name="sortByMetaNameId">The name of the metadata item to sort on.</param>
	/// <param name="sortAscending">If set to <c>true</c> sort in ascending order.</param>
	/// <returns>List{ContentItem}.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException"></exception>
	@Path("/{id}/contentitems")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public List<ContentItem> getContentItemsForAlbumId(@PathParam("id") long id, @QueryParam("sortByMetaNameId") @DefaultValue("NotSpecified") String sortByMetaNameId, @QueryParam("sortAscending") @DefaultValue("true") boolean sortAscending, @Context HttpServletRequest request);
	
    /// <summary>
    /// Defines an api that returns JSON in a format that is consumable by the JsTree jQuery plug-in.
    /// This can be called when a user clicks on a treeview node to dynamically load that node's contents.
    /// JsTree home page: http://www.jstree.com
    /// </summary>
	@Path("/gettreeview")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public void getTreeView(@QueryParam("id") long id, @QueryParam("secaction") int secaction, @QueryParam("sc") boolean sc, @QueryParam("navurl") String navurl, @Context HttpServletRequest request, @Context HttpServletResponse response);
	
	/// <summary>
	/// Gets the gallery items for the specified album, optionally sorting the results.
	/// </summary>
	/// <param name="id">The album ID.</param>
	/// <param name="showContentType">show 'album' or 'contentobject' or 'all'.</param>
	/// <param name="secaction">If set to <c>true</c> sort in ascending order.</param>
	/// <param name="sc">Whether checkboxes are being used.</param>	
	/// <param name="navurl">nav url.</param>
	/// <returns>thumbview data.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException"></exception>
	@Path("/gettreepicker")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public List<HashMap<String,Object>> getTreePicker(@QueryParam("id") long id, @QueryParam("sct") String showContentType, @QueryParam("ts") String thumbSize
			, @QueryParam("secaction") int secaction, @QueryParam("sc") boolean sc, @QueryParam("navurl") String navurl, @Context HttpServletRequest request, @Context HttpServletResponse response);
	
	@Path("/treeTable")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public HashMap<String, Object> albumsTreeTable(@QueryParam("id") String id, @Context HttpServletRequest request);

	/// <summary>
	/// Gets the media items for the specified album.
	/// </summary>
	/// <param name="id">The album ID.</param>
	/// <param name="sortByMetaNameId">The name of the metadata item to sort on.</param>
	/// <param name="sortAscending">If set to <c>true</c> sort in ascending order.</param>
	/// <returns>List{MediaItem}.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException"></exception>
	@Path("/{id}/mediaitems")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public List<MediaItem> getMediaItemsForAlbumId(@PathParam("id") long id, @QueryParam("sortByMetaNameId") @DefaultValue("NotSpecified") String sortByMetaNameId, @QueryParam("sortAscending") @DefaultValue("true") boolean sortAscending, @Context HttpServletRequest request);

	/// <summary>
	/// Gets the meta items for the specified album <paramref name="id" />.
	/// </summary>
	/// <param name="id">The album ID.</param>
	/// <returns></returns>
	/// <exception cref="System.Web.Http.HttpResponseException"></exception>
	@Path("/{id}/meta")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public List<MetaItemRest> getMetaItemsForAlbumId(@PathParam("id") long id, @Context HttpServletRequest request);

	/// <summary>
	/// Persists the <paramref name="album" /> to the data store. Only the following properties are persisted: 
	/// <see cref="AlbumRest.DateStart" />, <see cref="AlbumRest.DateEnd" />, <see cref="AlbumRest.SortById" />,
	/// <see cref="AlbumRest.SortUp" />, <see cref="AlbumRest.IsPrivate" />, <see cref="AlbumRest.Owner" />
	/// </summary>
	/// <param name="album">The album to persist.</param>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when the album isn't found in the data store,
	/// the current user doesn't have permission to edit the album, or some other error occurs.
	/// </exception>
	@POST
	@Path("/post")
	public void post(AlbumRest album);

	/// <summary>
	/// Deletes the album with the specified <paramref name="id" /> from the data store.
	/// </summary>
	/// <param name="id">The ID of the album to delete.</param>
	/// <returns>An instance of <see cref="Response" />.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when the current user doesn't have
	/// permission to delete the album, deleting the album would violate a business rule, or some other
	/// error occurs.
	/// </exception>
	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") long id);
	
	@DELETE
	@Path("/{ids}/albums")
	public Response deleteAlbums(@PathParam("ids") String albumIds);

	/// <summary>
	/// Sorts the <paramref name="contentItems" /> in the order in which they are passed.
	/// This method is used when a user is manually sorting an album and has dragged an item to a new position.
	/// The operation occurs asynchronously and returns immediately.
	/// </summary>
	/// <param name="contentItems">The gallery objects to sort. Their position in the array indicates the desired
	/// sequence. Only <see cref="ContentItem.Id" /> and <see cref="ContentItem.ItemType" /> need be 
	/// populated.</param>
	@POST
	@Path("/sortcontentobjects")
	public void sort(ContentItem[] contentItems);
	
	/// <summary>
	/// Re-sort the items in the album according to the criteria and store this updated sequence in the
	/// database. Callers must have <see cref="SecurityActions.EditAlbum" /> permission.
	/// </summary>
	/// <param name="id">The album ID.</param>
	/// <param name="sortByMetaNameId">The name of the metadata item to sort on.</param>
	/// <param name="sortAscending">If set to <c>true</c> sort in ascending order.</param>
	/// <exception cref="System.Web.Http.HttpResponseException"></exception>
	@POST
	@Path("/{id}/sortalbum")
	public void sort(@PathParam("id") long id, @QueryParam("sortByMetaNameId") String sortByMetaNameId, @QueryParam("sortAscending") boolean sortAscending);

	/// <summary>
	/// Sorts the gallery items passed to this method and return. No changes are made to the data store.
	/// When the album is virtual, the <see cref="AlbumRestAction.Album.ContentItems" /> property
	/// must be populated with the items to sort. For non-virtual albums (those with a valid ID), the 
	/// gallery objects are retrieved based on the ID and then sorted. The sort preference is saved to 
	/// the current user's profile, except when the album is virtual. The method incorporates security to
	/// ensure only authorized items are returned to the user.
	/// </summary>
	/// <param name="albumAction">An instance containing the album to sort and the sort preferences.</param>
	/// <returns>List{ContentItem}.</returns>
	@POST
	@Path("/getsortedalbum")
	public List<ContentItem> sort(AlbumAction albumAction, @Context HttpServletRequest request);
}