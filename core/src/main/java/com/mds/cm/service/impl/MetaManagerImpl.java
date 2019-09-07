package com.mds.cm.service.impl;

import java.text.MessageFormat;
import java.util.List;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Service;

import com.mds.cm.content.TagSearchOptions;
import com.mds.cm.exception.GallerySecurityException;
import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidContentObjectException;
import com.mds.cm.rest.MetaItemRest;
import com.mds.cm.rest.TagRest;
import com.mds.cm.service.MetaService;
import com.mds.cm.util.AppEventLogUtils;
import com.mds.cm.util.MetadataUtils;
import com.mds.core.EventType;
import com.mds.core.MetadataItemName;
import com.mds.core.TagSearchType;
import com.mds.pm.rest.PlayerItem;
import com.mds.pm.util.PlayersUtils;
import com.mds.sys.util.AppSettings;
import com.mds.sys.util.UserUtils;
import com.mds.util.HelperFunctions;

/// <summary>
/// Contains methods for Web API access to metadata.
/// </summary>
@Service("metaManager")
@WebService(serviceName = "MetaService", endpointInterface = "com.mds.cm.service.MetaService")
public class MetaManagerImpl implements MetaService {
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
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when an error occurs.</exception>
	public Iterable<TagRest> getTags(String q, long galleryId, int top, String sortBy, boolean sortAscending){
		try{
			TagSearchOptions.TagProperty sortProperty;
			if (!TagSearchOptions.TagProperty.isValidTagProperty(sortBy)){
				sortProperty = TagSearchOptions.TagProperty.NotSpecified;
			}else {
				sortProperty = TagSearchOptions.TagProperty.valueOf(sortBy);
			}

			return MetadataUtils.getTags(TagSearchType.TagsUserCanView, q, galleryId, top, sortProperty, sortAscending);
		}catch (Exception ex){
			//AppEventLogUtils.LogError(ex);
			throw new WebApplicationException("Server Error", ex, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

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
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when an error occurs.</exception>
	public Iterable<PlayerItem> getPlayers(int top, String sortBy, boolean sortAscending){
		try	{
			return PlayersUtils.getPlayers();
		}catch (Exception ex){
			//AppEventLogUtils.LogError(ex);
			throw new WebApplicationException("Server Error", ex, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

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
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when an error occurs.</exception>
	public String getTagTreeAsJson(long galleryId, int top, String sortBy, boolean sortAscending, boolean expanded, HttpServletRequest request)	{
		try	{
			validateEnterpriseLicense();

			TagSearchOptions.TagProperty sortProperty;
			if (!TagSearchOptions.TagProperty.isValidTagProperty(sortBy)){
				sortProperty = TagSearchOptions.TagProperty.NotSpecified;
			}else {
				sortProperty = TagSearchOptions.TagProperty.valueOf(sortBy);
			}

			return MetadataUtils.getTagTreeAsJson(request, TagSearchType.TagsUserCanView, galleryId, top, sortProperty, sortAscending, expanded);
		}catch (GallerySecurityException ge){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}catch (Exception ex){
			//AppEventLogUtils.LogError(ex);
			throw new WebApplicationException("Server Error", ex, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}	

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
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when an error occurs.</exception>
	public String getPeopleTreeAsJson(long galleryId, int top, String sortBy, boolean sortAscending, boolean expanded, HttpServletRequest request)	{
		try{
			validateEnterpriseLicense();
			
			TagSearchOptions.TagProperty sortProperty;
			if (!TagSearchOptions.TagProperty.isValidTagProperty(sortBy)){
				sortProperty = TagSearchOptions.TagProperty.NotSpecified;
			}else {
				sortProperty = TagSearchOptions.TagProperty.valueOf(sortBy);
			}

			return MetadataUtils.getTagTreeAsJson(request, TagSearchType.PeopleUserCanView, galleryId, top, sortProperty, sortAscending, expanded);
		}catch (GallerySecurityException ge){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}catch (Exception ex){
			//AppEventLogUtils.LogError(ex);
			throw new WebApplicationException("Server Error", ex, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

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
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when an error occurs.</exception>
	public Iterable<TagRest> getPeople(String q, long galleryId, int top, String sortBy, boolean sortAscending){
		try	{
			TagSearchOptions.TagProperty sortProperty;
			if (!TagSearchOptions.TagProperty.isValidTagProperty(sortBy)){
				sortProperty = TagSearchOptions.TagProperty.NotSpecified;
			}else {
				sortProperty = TagSearchOptions.TagProperty.valueOf(sortBy);
			}

			return MetadataUtils.getTags(TagSearchType.PeopleUserCanView, q, galleryId, top, sortProperty, sortAscending);
		}catch (Exception ex){
			//AppEventLogUtils.LogError(ex);
			throw new WebApplicationException("Server Error", ex, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/// <summary>
	/// Persists the metadata item to the data store. The current implementation requires that
	/// an existing item exist in the data store and only stores the contents of the
	/// <see cref="MetaItemRest.Value" /> property.
	/// </summary>
	/// <param name="metaItem">An instance of <see cref="MetaItemRest" /> to persist to the data
	/// store.</param>
	/// <returns>MetaItemRest.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when an album or content object associated
	/// with the meta item doesn't exist or an error occurs.</exception>
	public MetaItemRest putMetaItem(MetaItemRest metaItem){
		try	{
			return MetadataUtils.save(metaItem);
		}catch (InvalidAlbumException ae){
			throw new WebApplicationException(MessageFormat.format("Could not find album with ID {0}", metaItem.ContentId), Response.Status.NOT_FOUND);
			//ReasonPhrase = "Album Not Found"
		}catch (InvalidContentObjectException ce){
			throw new WebApplicationException(MessageFormat.format("One of the following errors occurred: (1) Could not find meta item with ID {0} (2) Could not find content object with ID {1} ", metaItem.getId(), metaItem.ContentId), Response.Status.NOT_FOUND);
			//ReasonPhrase = "Content Object/Metadata Item Not Found"
		}catch (GallerySecurityException ge){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
			//ReasonPhrase = "Server Error - PutMetaItem"
		}
	}

	/// <summary>
	/// Rebuilds the meta name having ID <paramref name="metaNameId" /> for all items in the gallery having ID 
	/// <paramref name="galleryId" />. The action is executed asyncronously and returns immediately.
	/// </summary>
	/// <param name="metaNameId">ID of the meta item. This must match the enumeration value of <see cref="MetadataItemName" />.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when an error occurs.</exception>
	
	public void rebuildItemForGallery(String metaNameId, long galleryId){
		try	{
			if (UserUtils.isCurrentUserGalleryAdministrator(galleryId))	{
				MetadataItemName metaName = MetadataItemName.valueOf(metaNameId);
				if (MetadataItemName.isValidFormattedMetadataItemName(metaName)){
					MetadataUtils.rebuildItemForGalleryAsync(metaName, galleryId);
				}
			}
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

	//#endregion

	//#region Functions

	/// <summary>
	/// Verifies the application is running an Enterprise License, throwing a <see cref="GallerySecurityException" />
	/// if it is not.
	/// </summary>
	/// <exception cref="GallerySecurityException">Thrown when the application is not running an Enterprise License.
	/// </exception>
	private static void validateEnterpriseLicense() throws GallerySecurityException	{
		//if (AppSettings.getInstance().getLicense().getLicenseType() != LicenseLevel.Enterprise)	{
		if (false) {
			AppEventLogUtils.LogEvent("Attempt to use a feature that requires an Enterprise License.", null, EventType.Warning);

			throw new GallerySecurityException("Attempt to use a feature that requires an Enterprise License.");
		}
	}

	// WARNING: Given the current API, there is no way to verify the user has permission to 
	// view the specified meta ID, so we'll comment out this method to ensure it isn't used.
	///// <summary>
	///// Gets the meta item with the specified <paramref name="id" />.
	///// Example: api/meta/4/
	///// </summary>
	///// <param name="id">The value that uniquely identifies the metadata item.</param>
	///// <returns>An instance of <see cref="MetaItemRest" />.</returns>
	///// <exception cref="System.Web.Http.WebApplicationException"></exception>
	//public MetaItemRest Get(long id)
	//{
	//	try
	//	{
	//		return MetadataUtils.Get(id);
	//	}
	//	catch (InvalidContentObjectException)
	//	{
	//		throw new WebApplicationException(Response.Status.NOT_FOUND
	//		{
	//			Content = new StringContent(MessageFormat.format("Could not find meta item with ID = {0}", id)),
	//			ReasonPhrase = "Content Object Not Found"
	//		});
	//	}
	//	catch (GallerySecurityException)
	//	{
	//		throw new WebApplicationException(Response.Status.FORBIDDEN);
	//	}
	//}

	//#endregion
}