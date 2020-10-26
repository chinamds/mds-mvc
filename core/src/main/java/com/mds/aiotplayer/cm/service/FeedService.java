/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import com.mds.aiotplayer.cm.content.AlbumBo;

/// <summary>
/// Contains methods for Web API access to RSS/Atom feeds. The feeds are generated through the
/// <see cref="AlbumSyndicationFeedFormatter" /> attribute.
/// </summary>
/// <remarks>The formatter <see cref="AlbumSyndicationFeedFormatter" /> validates that the application
/// is running an Enterprise License, throwing a <see cref="GallerySecurityException" /> when it isn't.
/// This propagates to the client as an HTTP 503 error. If a more specific error is desired on the client
/// (eg. 403 Forbidden), then move the license validation to the <see cref="FeedController" /> class.
/// </remarks>
//[AlbumSyndicationFeedFormatter]
@WebService
@Path("/feed")
public interface FeedService {
	/// <summary>
	/// Gets an album representing the specified <paramref name="id" />.
	/// </summary>
	/// <param name="id">The ID of the album to retrieve. Required.</param>
	/// <param name="sortByMetaNameId">The name of the metadata item to sort on. Optional. Defaults to 
	/// <see cref="MetadataItemName.DateAdded" /> when not specified.</param>
	/// <param name="sortAscending">If set to <c>true</c> sort in ascending order. Optional. Defaults to
	/// <c>false</c> when not specified.</param>
	/// <param name="destinationUrl">The URL, relative to the website root, that page hyperlinks should point to.
	/// Ex: "/dev/ds/default.aspx" Optional. When not specified, URLs will point to the application root.</param>
	/// <returns>Returns an instance of <see cref="Atom10FeedFormatter" /> or <see cref="Rss20FeedFormatter" />
	/// representing the specified parameters.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when an error occurs.</exception>
	@GET
	@Path("album")
	@Produces({"application/atom+xml", "application/rss+xml"}) 
	public AlbumBo getById(@QueryParam("id") long id, @QueryParam("sortByMetaNameId") @DefaultValue("NotSpecified") String sortByMetaNameId, @QueryParam("sortAscending") @DefaultValue("true") boolean sortAscending, @QueryParam("destinationUrl") String destinationUrl, @Context HttpServletRequest request);

	/// <summary>
	/// Gets an album containing the gallery objects having the specified <paramref name="q" /> String.
	/// </summary>
	/// <param name="q">The tag to search for. Required. May contain multiple tags separated by
	/// the '+' character. The '+' character must be encoded as %2b when used in an URL.</param>
	/// <param name="sortByMetaNameId">The name of the metadata item to sort on. Optional. Defaults to
	/// <see cref="MetadataItemName.DateAdded" /> when not specified.</param>
	/// <param name="sortAscending">If set to <c>true</c> sort in ascending order. Optional. Defaults to
	/// <c>false</c> when not specified.</param>
	/// <param name="destinationUrl">The URL, relative to the website root, that page hyperlinks should point to.
	/// Ex: "/dev/ds/default.aspx" Optional. When not specified, URLs will point to the application root.</param>
	/// <param name="filter">A filter that limits the types of gallery objects that are returned.
	/// Maps to the <see cref="GalleryObjectType" /> enumeration. Optional. When not specified, defaults to
	/// <see cref="GalleryObjectType.All" /></param>
	/// <param name="galleryId">The gallery ID. Only items in this gallery are returned. Optional. When not 
	/// specified, the first gallery is assumed.</param>
	/// <returns>Returns an instance of <see cref="Atom10FeedFormatter" /> or <see cref="Rss20FeedFormatter" />
	/// representing the specified parameters.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when an error occurs.</exception>
	@GET
	@Path("Title")
	@Produces({"application/atom+xml", "application/rss+xml"})
	public AlbumBo getByTitle(@QueryParam("q") String q, @QueryParam("sortByMetaNameId") @DefaultValue("NotSpecified") String sortByMetaNameId, @QueryParam("sortAscending") @DefaultValue("true") boolean sortAscending, @QueryParam("destinationUrl") String destinationUrl, @QueryParam("filter") @DefaultValue("all") String filter, @QueryParam("galleryId") @DefaultValue("0x8000000000000000L") long galleryId, @Context HttpServletRequest request);

	/// <summary>
	/// Gets an album containing the gallery objects having the specified <paramref name="q" /> String.
	/// </summary>
	/// <param name="q">The tag to search for. Required. May contain multiple tags separated by
	/// the '+' character. The '+' character must be encoded as %2b when used in an URL.</param>
	/// <param name="sortByMetaNameId">The name of the metadata item to sort on. Optional. Defaults to
	/// <see cref="MetadataItemName.DateAdded" /> when not specified.</param>
	/// <param name="sortAscending">If set to <c>true</c> sort in ascending order. Optional. Defaults to
	/// <c>false</c> when not specified.</param>
	/// <param name="destinationUrl">The URL, relative to the website root, that page hyperlinks should point to.
	/// Ex: "/dev/ds/default.aspx" Optional. When not specified, URLs will point to the application root.</param>
	/// <param name="filter">A filter that limits the types of gallery objects that are returned.
	/// Maps to the <see cref="GalleryObjectType" /> enumeration. Optional. When not specified, defaults to
	/// <see cref="GalleryObjectType.All" /></param>
	/// <param name="galleryId">The gallery ID. Only items in this gallery are returned. Optional. When not 
	/// specified, the first gallery is assumed.</param>
	/// <returns>Returns an instance of <see cref="Atom10FeedFormatter" /> or <see cref="Rss20FeedFormatter" />
	/// representing the specified parameters.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when an error occurs.</exception>
	@GET
	@Path("Search")
	@Produces({"application/atom+xml", "application/rss+xml"})
	public AlbumBo getBySearch(@QueryParam("q") String q, @QueryParam("q") @DefaultValue("NotSpecified") String sortByMetaNameId, @QueryParam("q") @DefaultValue("true") boolean sortAscending, @QueryParam("q") String destinationUrl, @QueryParam("q") @DefaultValue("all") String filter, @QueryParam("q") @DefaultValue("0x8000000000000000L") long galleryId, @Context HttpServletRequest request);

	/// <summary>
	/// Gets an album containing the gallery objects having the specified <paramref name="q" />.
	/// </summary>
	/// <param name="q">The tag to search for. Required. May contain multiple tags separated by
	/// the '+' character. The '+' character must be encoded as %2b when used in an URL.</param>
	/// <param name="sortByMetaNameId">The name of the metadata item to sort on. Optional. Defaults to
	/// <see cref="MetadataItemName.DateAdded" /> when not specified.</param>
	/// <param name="sortAscending">If set to <c>true</c> sort in ascending order. Optional. Defaults to
	/// <c>false</c> when not specified.</param>
	/// <param name="destinationUrl">The URL, relative to the website root, that page hyperlinks should point to.
	/// Ex: "/dev/ds/default.aspx" Optional. When not specified, URLs will point to the application root.</param>
	/// <param name="filter">A filter that limits the types of gallery objects that are returned.
	/// Maps to the <see cref="GalleryObjectType" /> enumeration. Optional. When not specified, defaults to
	/// <see cref="GalleryObjectType.All" />.</param>
	/// <param name="galleryId">The gallery ID. Only items in this gallery are returned. Optional. When not 
	/// specified, the first gallery is assumed.</param>
	/// <returns>Returns an instance of <see cref="Atom10FeedFormatter" /> or <see cref="Rss20FeedFormatter" />
	/// representing the specified parameters.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when an error occurs.</exception>
	@GET
	@Path("Tag")
	@Produces({"application/atom+xml", "application/rss+xml"})
	public AlbumBo getByTag(@QueryParam("q") String q, @QueryParam("sortByMetaNameId") @DefaultValue("NotSpecified") String sortByMetaNameId, @QueryParam("sortAscending") @DefaultValue("true") boolean sortAscending, @QueryParam("destinationUrl") String destinationUrl, @DefaultValue("all") String filter, @QueryParam("galleryId") @DefaultValue("0x8000000000000000L") long galleryId, @Context HttpServletRequest request);

	/// <summary>
	/// Gets an album containing the gallery objects having the specified <paramref name="q" />.
	/// </summary>
	/// <param name="q">The people tag to search for. Required. May contain multiple tags separated by
	/// the '+' character. The '+' character must be encoded as %2b when used in an URL.</param>
	/// <param name="sortByMetaNameId">The name of the metadata item to sort on. Optional. Defaults to
	/// <see cref="MetadataItemName.DateAdded" /> when not specified.</param>
	/// <param name="sortAscending">If set to <c>true</c> sort in ascending order. Optional. Defaults to
	/// <c>false</c> when not specified.</param>
	/// <param name="destinationUrl">The URL, relative to the website root, that page hyperlinks should point to.
	/// Ex: "/dev/ds/default.aspx" Optional. When not specified, URLs will point to the application root.</param>
	/// <param name="filter">A filter that limits the types of gallery objects that are returned.
	/// Maps to the <see cref="GalleryObjectType" /> enumeration. Optional. When not specified, defaults to
	/// <see cref="GalleryObjectType.All" />.</param>
	/// <param name="galleryId">The gallery ID. Only items in this gallery are returned. Optional. When not 
	/// specified, the first gallery is assumed.</param>
	/// <returns>Returns an instance of <see cref="Atom10FeedFormatter" /> or <see cref="Rss20FeedFormatter" />
	/// representing the specified parameters.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when an error occurs.</exception>
	@GET
	@Path("People")
	@Produces({"application/atom+xml", "application/rss+xml"})
	public AlbumBo getByPeople(String q, @DefaultValue("NotSpecified") String sortByMetaNameId, @DefaultValue("true") boolean sortAscending, @DefaultValue("NotSpecified") String destinationUrl, @DefaultValue("all") String filter, @DefaultValue("0x8000000000000000L") long galleryId, @Context HttpServletRequest request);

	/// <summary>
	/// Gets an album containing the gallery objects most recently added to the gallery. Only items the current user 
	/// is authorized to view are returned.
	/// </summary>
	/// <param name="top">The maximum number of results to return. Must be a value greater than zero. Optional. When
	/// not specified, defaults to fifty.</param>
	/// <param name="sortByMetaNameId">The name of the metadata item to sort on. Optional. Defaults to 
	/// <see cref="MetadataItemName.DateAdded" /> when not specified.</param>
	/// <param name="sortAscending">If set to <c>true</c> sort in ascending order. Optional. Defaults to
	/// <c>false</c> when not specified.</param>
	/// <param name="destinationUrl">The URL, relative to the website root, that page hyperlinks should point to.
	/// Ex: "/dev/ds/default.aspx" Optional. When not specified, URLs will point to the application root.</param>
	/// <param name="filter">A filter that limits the types of gallery objects that are returned.
	/// Maps to the <see cref="GalleryObjectType" /> enumeration. Optional. When not specified, defaults to
	/// <see cref="GalleryObjectType.ContentObject" /></param>
	/// <param name="galleryId">The gallery ID. Only items in this gallery are returned. Optional. When not specified,
	/// the first gallery is assumed.</param>
	/// <returns>Returns an instance of <see cref="Atom10FeedFormatter" /> or <see cref="Rss20FeedFormatter" />
	/// representing the specified parameters.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when an error occurs.</exception>
	@GET
	@Path("Latest")
	@Produces({"application/atom+xml", "application/rss+xml"})
	public AlbumBo getLatest(@DefaultValue("50") int top, @DefaultValue("DateAdded") String sortByMetaNameId, @DefaultValue("false") boolean sortAscending, String destinationUrl, @DefaultValue("contentobject") String filter, @DefaultValue("0x8000000000000000L") long galleryId, @Context HttpServletRequest request);

	/// <summary>
	/// Gets an album containing the gallery objects with the specified <paramref name="rating" />. Only items the current user 
	/// is authorized to view are returned.
	/// </summary>
	/// <param name="rating">Identifies the type of rating to retrieve. Valid values: "highest", "lowest", "none", or a number
	/// from 0 to 5 in half-step increments (eg. 0, 0.5, 1, 1.5, ... 4.5, 5).</param>
	/// <param name="top">The maximum number of results to return. Must be a value greater than zero. Optional. When
	/// not specified, defaults to fifty.</param>
	/// <param name="sortByMetaNameId">The name of the metadata item to sort on. Optional. Defaults to 
	/// <see cref="MetadataItemName.DateAdded" /> when not specified.</param>
	/// <param name="sortAscending">If set to <c>true</c> sort in ascending order. Optional. Defaults to
	/// <c>false</c> when not specified.</param>
	/// <param name="destinationUrl">The URL, relative to the website root, that page hyperlinks should point to.
	/// Ex: "/dev/ds/default.aspx" Optional. When not specified, URLs will point to the application root.</param>
	/// <param name="filter">A filter that limits the types of gallery objects that are returned.
	/// Maps to the <see cref="GalleryObjectType" /> enumeration. Optional. When not specified, defaults to
	/// <see cref="GalleryObjectType.ContentObject" /></param>
	/// <param name="galleryId">The gallery ID. Only items in this gallery are returned. Optional. When not specified,
	/// the first gallery is assumed.</param>
	/// <returns>Returns an instance of <see cref="Atom10FeedFormatter" /> or <see cref="Rss20FeedFormatter" />
	/// representing the specified parameters.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when an error occurs.</exception>
	@GET
	@Path("Rating")
	@Produces({"application/atom+xml", "application/rss+xml"})
	public AlbumBo getByRating(@QueryParam("rating") String rating, @QueryParam("top") @DefaultValue("50") int top, @QueryParam("sortByMetaNameId") @DefaultValue("DateAdded") String sortByMetaNameId, @QueryParam("sortAscending") @DefaultValue("false") boolean sortAscending, @QueryParam("destinationUrl") String destinationUrl, @QueryParam("filter") @DefaultValue("contentobject") String filter, @QueryParam("galleryId") @DefaultValue("0x8000000000000000L") long galleryId, @Context HttpServletRequest request);
}
