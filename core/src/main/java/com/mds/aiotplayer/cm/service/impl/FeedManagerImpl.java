/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service.impl;

import java.text.MessageFormat;
import java.util.List;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Service;

import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.content.FeedFormatterOptions;
import com.mds.aiotplayer.sys.util.SecurityGuard;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.service.FeedService;
import com.mds.aiotplayer.cm.util.AppEventLogUtils;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.util.ContentObjectUtils;
import com.mds.aiotplayer.core.ApprovalStatus;
import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.util.Utils;

/// <summary>
/// Contains methods for Web API access to RSS/Atom feeds. The feeds are generated through the
/// <see cref="AlbumSyndicationFeedFormatter" /> attribute.
/// </summary>
/// <remarks>The formatter <see cref="AlbumSyndicationFeedFormatter" /> validates that the application
/// is running an Enterprise License, throwing a <see cref="GallerySecurityException" /> when it isn't.
/// This propagates to the client as an HTTP 503 error. If a more specific error is desired on the client
/// (eg. 403 Forbidden), then move the license validation to the <see cref="FeedUtils" /> class.
/// </remarks>
//[AlbumSyndicationFeedFormatter]
@Service("feedManager")
@WebService(serviceName = "FeedService", endpointInterface = "com.mds.aiotplayer.cm.service.FeedService")
public class FeedManagerImpl implements FeedService{
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
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when an error occurs.</exception>
	
	public AlbumBo getById(long id, String sortByMetaNameId, boolean sortAscending, String destinationUrl, HttpServletRequest request){
		AlbumBo album = null;
		Long validateGalleryId = null;
		try	{
			album = CMUtils.loadAlbumInstance(id, true);
			SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), album.getId(), album.getGalleryId(), UserUtils.isAuthenticated(), album.getIsPrivate(), album.getIsVirtualAlbum());
			validateGalleryId = (album != null ? album.getGalleryId() : null);

			album.setFeedFormatterOptions(new FeedFormatterOptions(
					MetadataItemName.valueOf(sortByMetaNameId),
					sortAscending,
					StringUtils.isBlank(destinationUrl) ? StringUtils.join(Utils.getAppRoot(request), "/") : destinationUrl
				));

			return album;
		}catch (InvalidAlbumException ae){
			throw new WebApplicationException(MessageFormat.format("Could not find album with ID = {0}", id), Response.Status.NOT_FOUND);
			//ReasonPhrase = "Album Not Found"
		}catch (GallerySecurityException ge){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex, validateGalleryId);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

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
	/// Maps to the <see cref="ContentObjectType" /> enumeration. Optional. When not specified, defaults to
	/// <see cref="ContentObjectType.All" /></param>
	/// <param name="galleryId">The gallery ID. Only items in this gallery are returned. Optional. When not 
	/// specified, the first gallery is assumed.</param>
	/// <returns>Returns an instance of <see cref="Atom10FeedFormatter" /> or <see cref="Rss20FeedFormatter" />
	/// representing the specified parameters.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when an error occurs.</exception>
	
	public AlbumBo getByTitle(String q, String sortByMetaNameId, boolean sortAscending, String destinationUrl, String filter, long galleryId, HttpServletRequest request){
		AlbumBo album = null;
		Long validateGalleryId = null;
		try	{
			album = ContentObjectUtils.getContentObjectsHavingTitleOrCaption(Utils.toArray(q), ContentObjectType.parse(filter, ContentObjectType.All), ApprovalStatus.All, validateGallery(galleryId));
			validateGalleryId = (album != null ? album.getGalleryId() : null);

			album.setFeedFormatterOptions(new FeedFormatterOptions(
					MetadataItemName.valueOf(sortByMetaNameId),
					sortAscending,
					StringUtils.isBlank(destinationUrl) ? StringUtils.join(Utils.getAppRoot(request), "/") : destinationUrl
				));

			return album;
		}catch (GallerySecurityException ge){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex, validateGalleryId);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

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
	/// Maps to the <see cref="ContentObjectType" /> enumeration. Optional. When not specified, defaults to
	/// <see cref="ContentObjectType.All" /></param>
	/// <param name="galleryId">The gallery ID. Only items in this gallery are returned. Optional. When not 
	/// specified, the first gallery is assumed.</param>
	/// <returns>Returns an instance of <see cref="Atom10FeedFormatter" /> or <see cref="Rss20FeedFormatter" />
	/// representing the specified parameters.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when an error occurs.</exception>
	
	public AlbumBo getBySearch(String q, String sortByMetaNameId, boolean sortAscending, String destinationUrl, String filter, long galleryId, HttpServletRequest request){
		AlbumBo album = null;
		Long validateGalleryId = null;
		try	{
			album = ContentObjectUtils.getContentObjectsHavingSearchString(Utils.toArray(q), ContentObjectType.parse(filter, ContentObjectType.All), ApprovalStatus.All, validateGallery(galleryId));
			validateGalleryId = (album != null ? album.getGalleryId() : null);

			album.setFeedFormatterOptions(new FeedFormatterOptions(
					MetadataItemName.valueOf(sortByMetaNameId),
					sortAscending,
					StringUtils.isBlank(destinationUrl) ? StringUtils.join(Utils.getAppRoot(request), "/") : destinationUrl
				));

			return album;
		}catch (GallerySecurityException ge){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex, validateGalleryId);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

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
	/// Maps to the <see cref="ContentObjectType" /> enumeration. Optional. When not specified, defaults to
	/// <see cref="ContentObjectType.All" />.</param>
	/// <param name="galleryId">The gallery ID. Only items in this gallery are returned. Optional. When not 
	/// specified, the first gallery is assumed.</param>
	/// <returns>Returns an instance of <see cref="Atom10FeedFormatter" /> or <see cref="Rss20FeedFormatter" />
	/// representing the specified parameters.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when an error occurs.</exception>
	
	public AlbumBo getByTag(String q, String sortByMetaNameId, boolean sortAscending, String destinationUrl, String filter, long galleryId, HttpServletRequest request)	{
		AlbumBo album = null;
		Long validateGalleryId = null;
		try	{
			album = ContentObjectUtils.getContentObjectsHavingTags(Utils.toArray(q), null, ContentObjectType.parse(filter, ContentObjectType.All), ApprovalStatus.All, validateGallery(galleryId));
			validateGalleryId = album != null ? album.getGalleryId() : null;

			album.setFeedFormatterOptions(new FeedFormatterOptions(
					MetadataItemName.valueOf(sortByMetaNameId),
					sortAscending,
					StringUtils.isBlank(destinationUrl) ? StringUtils.join(Utils.getAppRoot(request), "/") : destinationUrl
				));

			return album;
		}catch (GallerySecurityException ge){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex, validateGalleryId);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

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
	/// Maps to the <see cref="ContentObjectType" /> enumeration. Optional. When not specified, defaults to
	/// <see cref="ContentObjectType.All" />.</param>
	/// <param name="galleryId">The gallery ID. Only items in this gallery are returned. Optional. When not 
	/// specified, the first gallery is assumed.</param>
	/// <returns>Returns an instance of <see cref="Atom10FeedFormatter" /> or <see cref="Rss20FeedFormatter" />
	/// representing the specified parameters.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when an error occurs.</exception>
	
	public AlbumBo getByPeople(String q, String sortByMetaNameId, boolean sortAscending, String destinationUrl, String filter/* = "all"*/, long galleryId, HttpServletRequest request){
		AlbumBo album = null;
		Long validateGalleryId = null;
		try	{
			album = ContentObjectUtils.getContentObjectsHavingTags(null, Utils.toArray(q), ContentObjectType.parse(filter, ContentObjectType.All), ApprovalStatus.All, validateGallery(galleryId));
			validateGalleryId = (album != null ? album.getGalleryId() : null);

			album.setFeedFormatterOptions(new FeedFormatterOptions(
					MetadataItemName.valueOf(sortByMetaNameId),
					sortAscending,
					StringUtils.isBlank(destinationUrl) ? StringUtils.join(Utils.getAppRoot(request), "/") : destinationUrl
				));

			return album;
		}catch (GallerySecurityException ge){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex, validateGalleryId);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

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
	/// Maps to the <see cref="ContentObjectType" /> enumeration. Optional. When not specified, defaults to
	/// <see cref="ContentObjectType.ContentObject" /></param>
	/// <param name="galleryId">The gallery ID. Only items in this gallery are returned. Optional. When not specified,
	/// the first gallery is assumed.</param>
	/// <returns>Returns an instance of <see cref="Atom10FeedFormatter" /> or <see cref="Rss20FeedFormatter" />
	/// representing the specified parameters.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when an error occurs.</exception>
	
	public AlbumBo getLatest(int top/* = 50*/, String sortByMetaNameId/* = (int)MetadataItemName.DateAdded*/, boolean sortAscending/* = false*/, String destinationUrl, String filter/* = "contentobject"*/, long galleryId, HttpServletRequest request){
		AlbumBo album = null;
		Long validateGalleryId = null;
		try{
			album = ContentObjectUtils.getMostRecentlyAddedContentObjects(top, validateGallery(galleryId), ContentObjectType.parse(filter, ContentObjectType.ContentObject), ApprovalStatus.All);
			validateGalleryId = (album != null ? album.getGalleryId() : null);

			album.setFeedFormatterOptions(new FeedFormatterOptions(
					MetadataItemName.valueOf(sortByMetaNameId),
					sortAscending,
					StringUtils.isBlank(destinationUrl) ? StringUtils.join(Utils.getAppRoot(request), "/") : destinationUrl
				));

			return album;
		}catch (GallerySecurityException ge){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex, validateGalleryId);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

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
	/// Maps to the <see cref="ContentObjectType" /> enumeration. Optional. When not specified, defaults to
	/// <see cref="ContentObjectType.ContentObject" /></param>
	/// <param name="galleryId">The gallery ID. Only items in this gallery are returned. Optional. When not specified,
	/// the first gallery is assumed.</param>
	/// <returns>Returns an instance of <see cref="Atom10FeedFormatter" /> or <see cref="Rss20FeedFormatter" />
	/// representing the specified parameters.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when an error occurs.</exception>
	public AlbumBo getByRating(String rating, int top/* = 50*/, String sortByMetaNameId/* = (int)MetadataItemName.DateAdded*/, boolean sortAscending/* = false*/, String destinationUrl, String filter/* = "contentobject"*/, long galleryId, HttpServletRequest request){
		AlbumBo album = null;
		Long validateGalleryId = null;
		try	{
			album = ContentObjectUtils.getRatedContentObjects(rating, top, validateGallery(galleryId), ContentObjectType.parse(filter, ContentObjectType.ContentObject), ApprovalStatus.All);
			validateGalleryId = (album != null ? album.getGalleryId() : null);

			album.setFeedFormatterOptions(new FeedFormatterOptions(
					MetadataItemName.valueOf(sortByMetaNameId),
					sortAscending,
					StringUtils.isBlank(destinationUrl) ? StringUtils.join(Utils.getAppRoot(request), "/") : destinationUrl
				));

			return album;
		}catch (GallerySecurityException ge){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex, validateGalleryId);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

	/// <summary>
	/// Verifies that <paramref name="galleryId" /> corresponds to an actual, non-template gallery and that 
	/// the current user has access to the gallery. When <paramref name="galleryId" /> is 
	/// <see cref="Int32.MinValue" />, then the ID of the first gallery is returned. If 
	/// <paramref name="galleryId" /> is greater than <see cref="Int32.MinValue" /> and is not
	/// valid, a <see cref="InvalidGalleryException" /> is thrown.
	/// </summary>
	/// <param name="galleryId">The gallery ID. Specify <see cref="Int32.MinValue" /> to have this function
	/// return the ID of the first non-template gallery.</param>
	/// <returns>System.Int32.</returns>
	/// <exception cref="InvalidGalleryException">Thrown when the <paramref name="galleryId" /> is invalid.</exception>
	/// <exception cref="GallerySecurityException">Thrown when the user is anonymous and the <paramref name="galleryId" />
	/// is configured to disallow anonymous browsing.</exception>
	private static long validateGallery(long galleryId) throws InvalidGalleryException, GallerySecurityException{
		if (galleryId == Long.MIN_VALUE){
			galleryId = CMUtils.loadGalleries().stream().findFirst().orElse(null).getGalleryId();
		}else{
			// Verify the gallery ID maps to an actual gallery (exception will be thrown if not).
			CMUtils.loadGallery(galleryId);
		}

		if (!UserUtils.isAuthenticated() && !CMUtils.loadGallerySetting(galleryId).getAllowAnonymousBrowsing()){
			// Anonymous user but the gallery does not allow anonymous users.
			throw new GallerySecurityException();
		}

		// If we get here then the gallery ID is valid.
		return galleryId;
	}
}
