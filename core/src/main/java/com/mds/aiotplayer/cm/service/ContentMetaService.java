package com.mds.aiotplayer.cm.service;

import java.util.List;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mds.aiotplayer.cm.rest.ContentItemMeta;
import com.mds.aiotplayer.cm.content.AddContentObjectSettings;
import com.mds.aiotplayer.cm.rest.ContentItem;
import com.mds.aiotplayer.cm.rest.MetaItemRest;

/// <summary>
/// Contains methods for Web API access for modifying metadata tags for multiple gallery objects.
/// Use <see cref="MetaController" /> for updating a metadata item for a single gallery object.
/// </summary>
@WebService
@Path("/contentitemmeta")
public interface ContentMetaService{
	/// <summary>
	/// Gets the meta items for the specified <paramref name="contentItems" />.
	/// </summary>
	/// <param name="contentItems">An array of <see cref="ContentItem" /> instances.</param>
	/// <returns>Returns a merged set of metadata.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException"></exception>
	@POST
	@Path("/contentitems")
	@Consumes( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({ MediaType.APPLICATION_JSON })
	public List<MetaItemRest> getMetaItemsForContentItems(ContentItem[] contentItems, @Context HttpServletRequest request);

	/// <summary>
	/// Gets a value indicating whether the logged-on user has edit permission for all of the <paramref name="contentItems" />.
	/// </summary>
	/// <param name="contentItems">A collection of <see cref="ContentItem" /> instances.</param>
	/// <returns><c>true</c> if the current user can edit the items; <c>false</c> otherwise.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException"></exception>
	@POST
	@Path("/canuseredit")
	@Consumes( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public boolean canUserEdit(Iterable<ContentItem> contentItems);

	/// <summary>
	/// Updates the content items with the specified metadata value. <see cref="Entity.ContentMeta.ActionResult" />
	/// contains details about the success or failure of the operation.
	/// </summary>
	/// <param name="contentItemMeta">An instance of <see cref="Entity.ContentMeta" /> that defines
	/// the tag value to be added and the content items it is to be added to. It is expected that only
	/// the MTypeId and Value properties of <see cref="Entity.ContentMeta.MetaItem" /> are populated.</param>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when the current user does not have permission
	/// to carry out the operation or an internal server error occurs.</exception>
	@PUT
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({MediaType.APPLICATION_JSON})
	public ContentItemMeta putContentMeta(ContentItemMeta contentItemMeta);
	
	@PUT
	@Path("/addfile")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({MediaType.APPLICATION_JSON})
	public ContentItemMeta putTest(AddContentObjectSettings settings);

	/// <summary>
	/// Deletes the meta tag value from the specified content items.
	/// </summary>
	/// <param name="contentItemMeta">An instance of <see cref="Entity.ContentMeta" /> that defines
	/// the tag value to be added and the content items it is to be added to.</param>
	/// <exception cref="System.Web.Http.HttpResponseException"></exception>
	@DELETE
	public Response deleteContentMeta(ContentItemMeta contentItemMeta);
}