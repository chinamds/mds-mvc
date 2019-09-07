package com.mds.cm.service;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.mds.cm.rest.MetaItemRest;
import com.mds.cm.rest.TagRest;
import com.mds.pm.rest.PlayerItem;

/// <summary>
/// Contains methods for Web API access to metadata.
/// </summary>
@WebService
@Path("/meta")
public interface MetaService{
	//#region Methods
	
	/// <summary>
	/// Gets a list of tags the current user can view. Guaranteed to not return null.
	/// </summary>
	/// <param name="q">The search term. Only tags that begin with this String are returned.
	/// Specify null or an empty String to return all tags.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <param name="top">The number of tags to return. Values less than zero are treated the same as zero,
	/// meaning no tags will be returned. Specify <see cref="int.MaxValue" /> to return all tags.</param>
	/// <param name="sortBy">The property to sort the tags by. Specify "count" to sort by tag frequency or
	/// "value" to sort by tag name. When not specified, defaults to "notspecified".</param>
	/// <param name="sortAscending">Specifies whether to sort the tags in ascending order. Specify <c>true</c>
	/// for ascending order or <c>false</c> for descending order. When not specified, defaults to <c>false</c>.</param>
	/// <returns>Iterable{Tag}.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when an error occurs.</exception>
	@Path("tags")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Iterable<TagRest> getTags(@QueryParam("q") String q, @QueryParam("galleryId") long galleryId, @QueryParam("top") @DefaultValue("2147483647") int top, @QueryParam("sortBy") @DefaultValue("notspecified") String sortBy, @QueryParam("sortAscending") @DefaultValue("false") boolean sortAscending);

	/// <summary>
	/// Gets a list of players the current user can view. Guaranteed to not return null.
	/// </summary>
	/// <param name="q">The search term. Only tags that begin with this String are returned.
	/// Specify null or an empty String to return all tags.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <param name="top">The number of tags to return. Values less than zero are treated the same as zero,
	/// meaning no tags will be returned. Specify <see cref="int.MaxValue" /> to return all tags.</param>
	/// <param name="sortBy">The property to sort the tags by. Specify "count" to sort by tag frequency or
	/// "value" to sort by tag name. When not specified, defaults to "notspecified".</param>
	/// <param name="sortAscending">Specifies whether to sort the tags in ascending order. Specify <c>true</c>
	/// for ascending order or <c>false</c> for descending order. When not specified, defaults to <c>false</c>.</param>
	/// <returns>Iterable{Player}.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when an error occurs.</exception>
	@Path("Players")
	@Produces({ MediaType.APPLICATION_JSON })
	public Iterable<PlayerItem> getPlayers(@DefaultValue("2147483647") int top, @DefaultValue("notspecified") String sortBy, @DefaultValue("false") boolean sortAscending);

	/// <summary>
	/// Gets a JSON String representing the tags used in the specified gallery. The JSON can be used as the
	/// data source for the jsTree jQuery widget. Only tags the current user has permission to view are
	/// included. The tag tree has a root node containing a single level of tags. Throws an exception when
	/// the application is not running an Enterprise License.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <param name="top">The number of tags to return. Values less than zero are treated the same as zero,
	/// meaning no tags will be returned. Specify <see cref="int.MaxValue" /> to return all tags.</param>
	/// <param name="sortBy">The property to sort the tags by. Specify "count" to sort by tag frequency or
	/// "value" to sort by tag name. When not specified, defaults to "count".</param>
	/// <param name="sortAscending">Specifies whether to sort the tags in ascending order. Specify <c>true</c>
	/// for ascending order or <c>false</c> for descending order. When not specified, defaults to <c>false</c>.</param>
	/// <param name="expanded">if set to <c>true</c> the tree is configured to display in an expanded form.</param>
	/// <returns>System.String.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when an error occurs.</exception>
	@Path("gettagtreeasjson")
	@GET
	public String getTagTreeAsJson(long galleryId, @DefaultValue("2147483647") int top, @DefaultValue("count") String sortBy, @DefaultValue("false") boolean sortAscending, @DefaultValue("false") boolean expanded, @Context HttpServletRequest request);

	/// <summary>
	/// Gets a JSON String representing the tags used in the specified gallery. The JSON can be used as the 
	/// data source for the jsTree jQuery widget. Only tags the current user has permission to view are
	/// included. The tag tree has a root node containing a single level of tags. Throws an exception when
	/// the application is not running an Enterprise License.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <param name="top">The number of tags to return. Values less than zero are treated the same as zero,
	/// meaning no tags will be returned. Specify <see cref="int.MaxValue" /> to return all tags.</param>
	/// <param name="sortBy">The property to sort the tags by. Specify "count" to sort by tag frequency or 
	/// "value" to sort by tag name. When not specified, defaults to "count".</param>
	/// <param name="sortAscending">Specifies whether to sort the tags in ascending order. Specify <c>true</c>
	/// for ascending order or <c>false</c> for descending order. When not specified, defaults to <c>false</c>.</param>
	/// <param name="expanded">if set to <c>true</c> the tree is configured to display in an expanded form.</param>
	/// <returns>System.String.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when an error occurs.</exception>
	@Path("getpeopletreeasjson")
	@GET
	public String getPeopleTreeAsJson(@QueryParam("galleryId") long galleryId, @QueryParam("top") @DefaultValue("2147483647") int top, @QueryParam("sortBy") @DefaultValue("count") String sortBy, @QueryParam("sortAscending") @DefaultValue("false") boolean sortAscending, @QueryParam("expanded") @DefaultValue("false") boolean expanded, @Context HttpServletRequest request);

	/// <summary>
	/// Gets a list of people the current user can view. Guaranteed to not return null.
	/// </summary>
	/// <param name="q">The search term. Only tags that begin with this String are returned.
	/// Specify null or an empty String to return all tags.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <param name="top">The number of tags to return. Values less than zero are treated the same as zero,
	/// meaning no tags will be returned. Specify <see cref="int.MaxValue" /> to return all tags.</param>
	/// <param name="sortBy">The property to sort the tags by. Specify "count" to sort by tag frequency or
	/// "value" to sort by tag name. When not specified, defaults to "notspecified".</param>
	/// <param name="sortAscending">Specifies whether to sort the tags in ascending order. Specify <c>true</c>
	/// for ascending order or <c>false</c> for descending order. When not specified, defaults to <c>false</c>.</param>
	/// <returns>Iterable{Tag}.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when an error occurs.</exception>
	@Path("people")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Iterable<TagRest> getPeople(@QueryParam("q") String q, @QueryParam("galleryId") long galleryId, @QueryParam("top") @DefaultValue("2147483647") int top, @QueryParam("sortBy") @DefaultValue("notspecified") String sortBy, @QueryParam("sortAscending") @DefaultValue("false") boolean sortAscending);

	/// <summary>
	/// Persists the metadata item to the data store. The current implementation requires that
	/// an existing item exist in the data store and only stores the contents of the
	/// <see cref="MetaItemRest.Value" /> property.
	/// </summary>
	/// <param name="metaItem">An instance of <see cref="MetaItemRest" /> to persist to the data
	/// store.</param>
	/// <returns>MetaItemRest.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when an album or content object associated
	/// with the meta item doesn't exist or an error occurs.</exception>
	@POST
	public MetaItemRest putMetaItem(MetaItemRest metaItem);

	/// <summary>
	/// Rebuilds the meta name having ID <paramref name="metaNameId" /> for all items in the gallery having ID 
	/// <paramref name="galleryId" />. The action is executed asyncronously and returns immediately.
	/// </summary>
	/// <param name="metaNameId">ID of the meta item. This must match the enumeration value of <see cref="MetadataItemName" />.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when an error occurs.</exception>
	@POST
	@Path("RebuildMetaItem")
	public void rebuildItemForGallery(@QueryParam("metaNameId") String metaNameId, @QueryParam("galleryId") long galleryId);

	//#endregion
}