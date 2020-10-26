/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.util;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static java.util.concurrent.CompletableFuture.runAsync;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.content.AlbumDeleteValidator;
import com.mds.aiotplayer.cm.content.AlbumProfile;
import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.cm.content.ContentObjectBoCollection;
import com.mds.aiotplayer.sys.util.MDSRole;
import com.mds.aiotplayer.sys.util.MDSRoleCollection;
import com.mds.aiotplayer.cm.content.GalleryBo;
import com.mds.aiotplayer.cm.content.GalleryBoCollection;
import com.mds.aiotplayer.cm.content.GallerySettings;
import com.mds.aiotplayer.sys.util.SecurityGuard;
import com.mds.aiotplayer.cm.content.UserGalleryProfile;
import com.mds.aiotplayer.cm.content.UserProfile;
import com.mds.aiotplayer.cm.content.nullobjects.NullContentObject;
import com.mds.aiotplayer.cm.exception.CannotDeleteAlbumException;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.rest.AlbumRest;
import com.mds.aiotplayer.cm.rest.ApprovalItem;
import com.mds.aiotplayer.cm.rest.CMDataLoadOptions;
import com.mds.aiotplayer.cm.rest.ContentItem;
import com.mds.aiotplayer.cm.rest.JsTreeNode;
import com.mds.aiotplayer.cm.rest.MetaItemRest;
import com.mds.aiotplayer.cm.rest.PermissionsRest;
import com.mds.aiotplayer.cm.rest.TreeNode;
import com.mds.aiotplayer.cm.rest.TreeView;
import com.mds.aiotplayer.core.ApprovalStatus;
import com.mds.aiotplayer.core.CacheItem;
import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.core.ResourceId;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.core.exception.InvalidEnumArgumentException;
import com.mds.aiotplayer.core.exception.NotSupportedException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.core.exception.WebException;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.sys.model.MenuFunction;
import com.mds.aiotplayer.sys.util.AppSettings;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.common.utils.Reflections;
import com.mds.aiotplayer.util.CacheUtils;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.util.Utils;

/// <summary>
/// Contains functionality for interacting with albums. Typically web pages directly call the appropriate business layer objects,
/// but when a task involves multiple steps or the functionality does not exist in the business layer, the methods here are
/// used.
/// </summary>
public final class AlbumUtils{
	//#region Public Static Methods

	/// <summary>
	/// Generate a read-only, inflated <see cref="AlbumBo" /> instance with optionally inflated child content objects. Metadata 
	/// for content objects are automatically loaded. The album's <see cref="AlbumBo.ThumbnailContentObjectId" /> property is set 
	/// to its value from the data store, but the <see cref="ContentObjectBo.Thumbnail" /> property is only inflated when 
	/// accessed. Guaranteed to not return null.
	/// </summary>
	/// <param name="albumId">The <see cref="ContentObjectBo.getId()">ID</see> that uniquely identifies the album to retrieve.</param>
	/// <param name="inflateChildContentObjects">When true, the child content objects of the album are added and inflated.
	/// Child albums are added but not inflated. When false, they are not added or inflated.</param>
	/// <returns>Returns an inflated album instance with all properties set to the values from the data store.</returns>
	/// <exception cref="InvalidAlbumException">Thrown when an album with the specified <paramref name = "albumId" /> 
	/// is not found in the data store.</exception>
	public static AlbumBo loadAlbumInstance(long albumId, boolean inflateChildContentObjects) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException{
		return loadAlbumInstance(albumId, inflateChildContentObjects, false, true);
	}

	/// <summary>
	/// Generate an inflated <see cref="AlbumBo" /> instance with optionally inflated child content objects. Metadata 
	/// for content objects are automatically loaded. Use the <paramref name="isWritable" /> parameter to specify a writeable, 
	/// thread-safe instance that can be modified and persisted to the data store. The 
	/// album's <see cref="AlbumBo.ThumbnailContentObjectId" /> property is set to its value from the data store, but the 
	/// <see cref="ContentObjectBo.Thumbnail" /> property is only inflated when accessed. Guaranteed to not return null.
	/// </summary>
	/// <param name="albumId">The <see cref="ContentObjectBo.getId()">ID</see> that uniquely identifies the album to retrieve.</param>
	/// <param name="inflateChildContentObjects">When true, the child content objects of the album are added and inflated.
	/// Child albums are added but not inflated. When false, they are not added or inflated.</param>
	/// <param name="isWritable">When set to <c>true</c> then return a unique instance that is not shared across threads.</param>
	/// <returns>Returns an inflated album instance with all properties set to the values from the data store.</returns>
	/// <exception cref="InvalidAlbumException">Thrown when an album with the specified <paramref name = "albumId" /> 
	/// is not found in the data store.</exception>
	public static AlbumBo loadAlbumInstance(long albumId, boolean inflateChildContentObjects, boolean isWritable) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException{
		return loadAlbumInstance(albumId, inflateChildContentObjects, isWritable, true);
	}

	/// <summary>
	/// Generate an inflated <see cref="AlbumBo" /> instance with optionally inflated child content objects, and optionally specifying
	/// whether to suppress the loading of content object metadata. Use the <paramref name="isWritable" />
	/// parameter to specify a writeable, thread-safe instance that can be modified and persisted to the data store. The 
	/// album's <see cref="AlbumBo.ThumbnailContentObjectId" /> property is set to its value from the data store, but the 
	/// <see cref="ContentObjectBo.Thumbnail" /> property is only inflated when accessed. Guaranteed to not return null.
	/// </summary>
	/// <param name="albumId">The <see cref="ContentObjectBo.getId()">ID</see> that uniquely identifies the album to retrieve.</param>
	/// <param name="inflateChildContentObjects">When true, the child content objects of the album are added and inflated.
	/// Child albums are added but not inflated. When false, they are not added or inflated.</param>
	/// <param name="isWritable">When set to <c>true</c> then return a unique instance that is not shared across threads.</param>
	/// <param name="allowMetadataLoading">If set to <c>false</c>, the metadata for content objects are not loaded.</param>
	/// <returns>Returns an inflated album instance with all properties set to the values from the data store.</returns>
	/// <exception cref="InvalidAlbumException">Thrown when an album with the specified <paramref name = "albumId" /> 
	/// is not found in the data store.</exception>
	public static AlbumBo loadAlbumInstance(long albumId, boolean inflateChildContentObjects, boolean isWritable, boolean allowMetadataLoading) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException	{
		AlbumBo album = CMUtils.loadAlbumInstance(albumId, inflateChildContentObjects, isWritable, allowMetadataLoading);

		validateAlbumOwner(album);

		return album;
	}

	/// <summary>
	/// Creates an album, assigns the user name as the owner, saves it, and returns the newly created album.
	/// A profile entry is created containing the album ID. Returns null if the ID specified in the gallery settings
	/// for the parent album does not represent an existing album. That is, returns null if <see cref="GallerySettings.getUserAlbumParentAlbumId()" />
	/// does not match an existing album.
	/// </summary>
	/// <param name="userName">The user name representing the user who is the owner of the album.</param>
	/// <param name="galleryId">The gallery ID for the gallery in which the album is to be created.</param>
	/// <returns>
	/// Returns the newly created user album. It has already been persisted to the database.
	/// Returns null if the ID specified in the gallery settings for the parent album does not represent an existing album.
	/// That is, returns null if <see cref="GallerySettings.getUserAlbumParentAlbumId()" />
	/// does not match an existing album.
	/// </returns>
	public static AlbumBo createUserAlbum(String userName, long galleryId) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException, InvalidAlbumException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException	{
		GallerySettings gallerySetting = CMUtils.loadGallerySetting(galleryId);

		String albumNameTemplate = gallerySetting.getUserAlbumNameTemplate();

		AlbumBo parentAlbum;
		try	{
			parentAlbum = AlbumUtils.loadAlbumInstance(gallerySetting.getUserAlbumParentAlbumId(), false);
		}catch (InvalidAlbumException ex){
			// The parent album does not exist. Record the error and return null.
			String galleryDescription = HelperFunctions.htmlEncode(CMUtils.loadGallery(gallerySetting.getGalleryId()).getDescription());
			String msg = I18nUtils.getMessage("error.User_Album_Parent_Invalid_Ex_Msg", galleryDescription, gallerySetting.getUserAlbumParentAlbumId());
			AppEventLogUtils.LogError(new WebException(msg, ex), galleryId);
			return null;
		}

		AlbumBo album = CMUtils.createEmptyAlbumInstance(parentAlbum.getGalleryId());

		album.setTitle(albumNameTemplate.replace("{UserName}", userName));
		album.setCaption(gallerySetting.getUserAlbumSummaryTemplate());
		album.setOwnerUserName(userName);
		//newAlbum.ThumbnailContentObjectId = 0; // not needed
		album.setParent(parentAlbum);
		album.setIsPrivate(parentAlbum.getIsPrivate());
		ContentObjectUtils.saveContentObject(album, userName);

		saveAlbumIdToProfile(album.getId(), userName, album.getGalleryId());

		HelperFunctions.purgeCache();

		return album;
	}

	/// <summary>
	/// Get a reference to the highest level album in the specified <paramref name="galleryId" /> the current user has permission 
	/// to add albums to. Returns null if no album meets this criteria.
	/// </summary>
	/// <param name="galleryId">The ID of the gallery.</param>
	/// <returns>Returns a reference to the highest level album the user has permission to add albums to.</returns>
	public static AlbumBo getHighestLevelAlbumWithCreatePermission(long galleryId) throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException	{
		// Step 1: Loop through the roles and compile a list of album IDs where the role has create album permission.
		GalleryBo gallery = CMUtils.loadGallery(galleryId);
		List<Long> rootAlbumIdsWithCreatePermission = new ArrayList<Long>();

		for (MDSRole role : RoleUtils.getMDSRolesForUser()){
			if (role.getGalleries().contains(gallery)){
				if (role.getAllowAddChildAlbum()){
					for (long albumId : role.getRootAlbumIds()){
						if (!rootAlbumIdsWithCreatePermission.contains(albumId))
							rootAlbumIdsWithCreatePermission.add(albumId);
					}
				}
			}
		}

		// Step 2: Loop through our list of album IDs. If any album belongs to another gallery, remove it. If any album has an ancestor 
		// that is also in the list, then remove it. We only want a list of top level albums.
		List<Long> albumIdsToRemove = new ArrayList<Long>();
		for (long albumIdWithCreatePermission : rootAlbumIdsWithCreatePermission){
			ContentObjectBo album = AlbumUtils.loadAlbumInstance(albumIdWithCreatePermission, false);

			if (album.getGalleryId() != galleryId){
				// Album belongs to another gallery. Mark it for deletion.
				albumIdsToRemove.add(albumIdWithCreatePermission);
			}else{
				while (true){
					album = Reflections.as(album.getParent(), AlbumBo.class);
					if (album == null)
						break;

					if (rootAlbumIdsWithCreatePermission.contains(album.getId())){
						// Album has an ancestor that is also in the list. Mark it for deletion.
						albumIdsToRemove.add(albumIdWithCreatePermission);
						break;
					}
				}
			}
		}

		for (long albumId : albumIdsToRemove){
			rootAlbumIdsWithCreatePermission.remove(albumId);
		}

		// Step 3: Starting with the root album, start iterating through the child albums. When we get to
		// one in our list, we can conclude that is the highest level album for which the user has create album permission.
		return findFirstMatchingAlbumRecursive(CMUtils.loadRootAlbumInstance(galleryId), rootAlbumIdsWithCreatePermission);
	}

	/// <summary>
	/// Get a reference to the highest level album in the specified <paramref name="galleryId" /> the current user has permission to 
	/// add albums and/or content objects to. Returns null if no album meets this criteria.
	/// </summary>
	/// <param name="verifyAddAlbumPermissionExists">Specifies whether the current user must have permission to add child albums
	/// to the album.</param>
	/// <param name="verifyAddContentObjectPermissionExists">Specifies whether the current user must have permission to add content objects
	/// to the album.</param>
	/// <param name="galleryId">The ID of the gallery.</param>
	/// <returns>
	/// Returns a reference to the highest level album the user has permission to add albums and/or content objects to.
	/// </returns>
	public static AlbumBo getHighestLevelAlbumWithAddPermission(boolean verifyAddAlbumPermissionExists, boolean verifyAddContentObjectPermissionExists, long galleryId) throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException	{
		// Step 1: Loop through the roles and compile a list of album IDs where the role has the required permission.
		// If the verifyAddAlbumPermissionExists parameter is true, then the user must have permission to add child albums.
		// If the verifyAddContentObjectPermissionExists parameter is true, then the user must have permission to add content objects.
		// If either parameter is false, then the absense of that permission does not disqualify an album.
		GalleryBo gallery = CMUtils.loadGallery(galleryId);

		List<Long> rootAlbumIdsWithPermission = new ArrayList<Long>();
		for (MDSRole role : RoleUtils.getMDSRolesForUser()){
			if (role.getGalleries().contains(gallery)){
				boolean albumPermGranted = (verifyAddAlbumPermissionExists ? role.getAllowAddChildAlbum() : true);
				boolean contentObjectPermGranted = (verifyAddContentObjectPermissionExists ? role.getAllowAddContentObject() : true);

				if (albumPermGranted && contentObjectPermGranted){
					// This role satisfies the requirements, so add each album to the list.
					for (long albumId : role.getRootAlbumIds()){
						if (!rootAlbumIdsWithPermission.contains(albumId))
							rootAlbumIdsWithPermission.add(albumId);
					}
				}
			}
		}

		// Step 2: Loop through our list of album IDs. If any album belongs to another gallery, remove it. If any album has an ancestor 
		// that is also in the list, then remove it. We only want a list of top level albums.
		List<Long> albumIdsToRemove = new ArrayList<Long>();
		for (long albumIdWithPermission : rootAlbumIdsWithPermission){
			ContentObjectBo album = AlbumUtils.loadAlbumInstance(albumIdWithPermission, false);

			if (album.getGalleryId() != galleryId){
				// Album belongs to another gallery. Mark it for deletion.
				albumIdsToRemove.add(albumIdWithPermission);
			}else{
				while (true){
					album = Reflections.as(album.getParent(), AlbumBo.class);
					if (album == null)
						break;

					if (rootAlbumIdsWithPermission.contains(album.getId()))	{
						// Album has an ancestor that is also in the list. Mark it for deletion.
						albumIdsToRemove.add(albumIdWithPermission);
						break;
					}
				}
			}
		}

		for (long albumId : albumIdsToRemove){
			rootAlbumIdsWithPermission.remove(albumId);
		}

		// Step 3: Starting with the root album, start iterating through the child albums. When we get to
		// one in our list, we can conclude that is the highest level album for which the user has create album permission.
		return findFirstMatchingAlbumRecursive(CMUtils.loadRootAlbumInstance(galleryId), rootAlbumIdsWithPermission);
	}

	/// <summary>
	/// Gets the meta items for the specified album <paramref name="id" />.
	/// </summary>
	/// <param name="id">The album ID.</param>
	/// <returns></returns>
	/// <exception cref="MDS.EventLogs.CustomExceptions.GallerySecurityException">Thrown when the 
	/// user does not have view permission to the specified album.</exception>
	public static MetaItemRest[] getMetaItemsForAlbum(long id, HttpServletRequest request) throws GallerySecurityException, InvalidAlbumException, UnsupportedContentObjectTypeException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException
	{
		AlbumBo album = CMUtils.loadAlbumInstance(id, false);
		SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), album.getId(), album.getGalleryId(), UserUtils.isAuthenticated(), album.getIsPrivate(), album.getIsVirtualAlbum());

		return ContentObjectUtils.toMetaItems(album.getMetadataItems().getVisibleItems(), album, request);
	}

	/// <summary>
	/// Gets the Approval items for the specified album <paramref name="id" />.
	/// </summary>
	/// <param name="id">The album ID.</param>
	/// <returns></returns>
	/// <exception cref="MDS.EventLogs.CustomExceptions.GallerySecurityException">Thrown when the 
	/// user does not have view permission to the specified album.</exception>
	public static ApprovalItem[] getApprovalItemsForAlbum(long id) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, GallerySecurityException, InvalidMDSRoleException, InvalidGalleryException{
		AlbumBo album = CMUtils.loadAlbumInstance(id, false);
		SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), album.getId(), album.getGalleryId(), UserUtils.isAuthenticated(), album.getIsPrivate(), album.getIsVirtualAlbum());

		return ContentObjectUtils.toApprovalItems(album.getApprovalItems(), album);
	}

	/// <summary>
	/// Converts the <paramref name="albums" /> to an enumerable collection of 
	/// <see cref="AlbumRest" /> instances. Guaranteed to not return null.
	/// </summary>
	/// <param name="albums">The albums.</param>
	/// <returns>An enumerable collection of <see cref="AlbumRest" /> instances.</returns>
	/// <exception cref="System.ArgumentNullException"></exception>
	public static AlbumRest[] toAlbumEntities(List<ContentObjectBo> albums, CMDataLoadOptions options, HttpServletRequest request) throws Exception	{
		if (albums == null)
			throw new ArgumentNullException("albums");

		List<AlbumRest> albumEntities = new ArrayList<AlbumRest>(albums.size());

		for (ContentObjectBo album : albums){
			albumEntities.add(toAlbumEntity((AlbumBo)album, options, request));
		}

		return albumEntities.toArray(new AlbumRest[0]);
	}

	/// <summary>
	/// Gets a data entity containing information about the current album. The instance can be JSON-parsed and sent to the
	/// browser. Returns null if the requested album does not exist or the user does not have permission to view it.
	/// </summary>
	/// <param name="album">The album.</param>
	/// <param name="options">Specifies options for configuring the return data. To use default
	/// settings, specify an empty instance with properties left at default values.</param>
	/// <returns>
	/// Returns <see cref="AlbumRest" /> object containing information about the current album.
	/// </returns>
	/// <overloads>
	/// Converts the <paramref name="album" /> to an instance of <see cref="AlbumRest" />.
	///   </overloads>
	public static AlbumRest toAlbumEntity(AlbumBo album, CMDataLoadOptions options, HttpServletRequest request) throws Exception	{
		try	{
			return toAlbumEntity(album, getPermissionsEntity(album), options, request);
		}catch (InvalidAlbumException ae) { return null; }
		//catch (GallerySecurityException ge) { return null; }
	}

	/// <summary>
	/// Gets a data entity containing album information for the specified <paramref name="album" />. Returns an object with empty
	/// properties if the user does not have permission to view the specified album. The instance can be JSON-parsed and sent to the
	/// browser.
	/// </summary>
	/// <param name="album">The album to convert to an instance of <see cref="MDS.Web.AlbumRest" />.</param>
	/// <param name="perms">The permissions the current user has for the album.</param>
	/// <param name="options">Specifies options for configuring the return data. To use default
	/// settings, specify an empty instance with properties left at default values.</param>
	/// <returns>
	/// Returns an <see cref="MDS.Web.AlbumRest" /> object containing information about the requested album.
	/// </returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="album" /> is null.</exception>
	/// <exception cref="System.ArgumentNullException"></exception>
	public static AlbumRest toAlbumEntity(AlbumBo album, PermissionsRest perms, CMDataLoadOptions options, HttpServletRequest request) throws Exception{
		if (album == null)
			throw new ArgumentNullException("album");

		AlbumRest albumEntity = new AlbumRest();

		albumEntity.Id = album.getId();
		albumEntity.GalleryId = album.getGalleryId();
		albumEntity.Title = album.getTitle().replace("{album.root_Album_Default_Title}", I18nUtils.getMessage("album.root_Album_Default_Title"));
		albumEntity.Caption = album.getCaption().replace("{album.root_Album_Default_Summary}", I18nUtils.getMessage("album.root_Album_Default_Summary"));
		albumEntity.Owner = (perms.AdministerGallery ? album.getOwnerUserName() : null);
		albumEntity.InheritedOwners = (perms.AdministerGallery ? String.join(", ", album.getInheritedOwners()) : null);
		albumEntity.DateStart = album.getDateStart();
		albumEntity.DateEnd = album.getDateEnd();
		albumEntity.IsPrivate = album.getIsPrivate();
		albumEntity.VirtualType = album.getVirtualAlbumType().value();
		albumEntity.RssUrl = getRssUrl(album, request);
		albumEntity.Permissions = perms;
		albumEntity.MetaItems = ContentObjectUtils.toMetaItems(album.getMetadataItems().getVisibleItems(), album, request);
		albumEntity.NumAlbums = album.getChildContentObjects(ContentObjectType.Album, ApprovalStatus.All, !UserUtils.isAuthenticated()).count();

		// Optionally load gallery items
		if (options.LoadContentItems){
			AlbumProfile albumSortDef = ProfileUtils.getProfile().getAlbumProfiles().find(album.getId());

			List<ContentObjectBo> items;
			if (albumSortDef != null){
				items = album
					.getChildContentObjects(options.Filter, options.ApprovalFilter, !UserUtils.isAuthenticated())
					.toSortedList(albumSortDef.SortByMetaName, albumSortDef.SortAscending, album.getGalleryId());

				albumEntity.SortById = albumSortDef.SortByMetaName.value();
				albumEntity.SortUp = albumSortDef.SortAscending;
			}else{
				if (album.getIsVirtualAlbum()){
					items = album.getChildContentObjects(options.Filter, options.ApprovalFilter, !UserUtils.isAuthenticated()).toSortedList(album.getSortByMetaName(), album.getSortAscending(), album.getGalleryId());
				}else{
					// Real (non-virtual) albums are already sorted on their Seq property, so return items based on that.
					items = album.getChildContentObjects(options.Filter, options.ApprovalFilter, !UserUtils.isAuthenticated()).toSortedList();
				}

				albumEntity.SortById = album.getSortByMetaName().value();
				albumEntity.SortUp = album.getSortAscending();
			}

			if (options.NumContentItemsToRetrieve > 0)
				items = items.stream().skip(options.NumContentItemsToSkip).limit(options.NumContentItemsToRetrieve).collect(Collectors.toList());

			albumEntity.ContentItems = ContentObjectUtils.toContentItems(items, request);
			albumEntity.NumContentItems = albumEntity.ContentItems.length;
		}else{
			albumEntity.NumContentItems = album.getChildContentObjects(options.Filter, options.ApprovalFilter, !UserUtils.isAuthenticated()).count();
		}

		// Optionally load media items
		if (options.LoadMediaItems){
			List<ContentObjectBo> items;

			if (album.getIsVirtualAlbum()){
				items = album.getChildContentObjects(ContentObjectType.ContentObject, ApprovalStatus.All, !UserUtils.isAuthenticated()).toSortedList(album.getSortByMetaName(), album.getSortAscending(), album.getGalleryId());
			}else{
				// Real (non-virtual) albums are already sorted on their Seq property, so return items based on that.
				items = album.getChildContentObjects(ContentObjectType.ContentObject, ApprovalStatus.All, !UserUtils.isAuthenticated()).toSortedList();
			}

			//List<ContentObjectBo> items = album.GetChildContentObjects(ContentObjectType.ContentObject, !UserUtils.isAuthenticated()).ToSortedList();
			albumEntity.NumMediaItems = items.size();
			albumEntity.MediaItems = ContentObjectUtils.toMediaItems(items, request);
		}else{
			albumEntity.NumMediaItems = album.getChildContentObjects(ContentObjectType.ContentObject, ApprovalStatus.All, !UserUtils.isAuthenticated()).count();
		}

		return albumEntity;
	}

	/// <summary>
	/// Gets a data entity containing permission information for the specified <paramref name="album" />.
	/// The instance can be JSON-parsed and sent to the browser. The permissions take into account whether the media files
	/// are configured as read only (<see cref="GallerySettings.ContentObjectPathIsReadOnly" />).
	/// </summary>
	/// <returns>
	/// Returns <see cref="PermissionsRest"/> object containing permission information.
	/// </returns>
	private static PermissionsRest getPermissionsEntity(AlbumBo album) 
			throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, InvalidMDSRoleException	{
		long albumId = album.getId();
		long galleryId = album.getGalleryId();
		boolean isPrivate = album.getIsPrivate();
		boolean isVirtual = album.getIsVirtualAlbum();
		AlbumBo rootAlbum = CMUtils.loadRootAlbumInstance(album.getGalleryId());
		MDSRoleCollection roles = RoleUtils.getMDSRolesForUser();
		boolean isAdmin = UserUtils.isUserAuthorized(SecurityActions.AdministerSite, roles, rootAlbum.getId(), galleryId, rootAlbum.getIsPrivate(), isVirtual);
		boolean isGalleryAdmin = isAdmin || UserUtils.isUserAuthorized(SecurityActions.AdministerGallery, roles, rootAlbum.getId(), galleryId, rootAlbum.getIsPrivate(), isVirtual);
		boolean isGalleryWriteable = !CMUtils.loadGallerySetting(galleryId).getContentObjectPathIsReadOnly();

		PermissionsRest perms = new PermissionsRest();

		perms.AdministerGallery = isGalleryAdmin;
		perms.AdministerSite = isAdmin;

		if (album.getIsVirtualAlbum()){
			// When we have a virtual album we use the permissions assigned to the root album. 
			perms.ViewAlbumOrContentObject = isGalleryAdmin || UserUtils.isUserAuthorized(SecurityActions.ViewAlbumOrContentObject, roles, rootAlbum.getId(), galleryId, rootAlbum.getIsPrivate(), rootAlbum.getIsVirtualAlbum());
			perms.ViewOriginalContentObject = isGalleryAdmin || UserUtils.isUserAuthorized(SecurityActions.ViewOriginalContentObject, roles, rootAlbum.getId(), galleryId, rootAlbum.getIsPrivate(), rootAlbum.getIsVirtualAlbum());
			perms.AddChildAlbum = isGalleryWriteable && (isGalleryAdmin || UserUtils.isUserAuthorized(SecurityActions.AddChildAlbum, roles, rootAlbum.getId(), galleryId, rootAlbum.getIsPrivate(), rootAlbum.getIsVirtualAlbum()));
			perms.AddContentObject = isGalleryWriteable && (isGalleryAdmin || UserUtils.isUserAuthorized(SecurityActions.AddContentObject, roles, rootAlbum.getId(), galleryId, rootAlbum.getIsPrivate(), rootAlbum.getIsVirtualAlbum()));
			perms.EditAlbum = false;
			perms.EditContentObject = (isGalleryAdmin || UserUtils.isUserAuthorized(SecurityActions.EditContentObject, roles, rootAlbum.getId(), galleryId, rootAlbum.getIsPrivate(), rootAlbum.getIsVirtualAlbum()));
			perms.DeleteAlbum = isGalleryWriteable && (isGalleryAdmin || UserUtils.isUserAuthorized(SecurityActions.DeleteAlbum, roles, rootAlbum.getId(), galleryId, rootAlbum.getIsPrivate(), rootAlbum.getIsVirtualAlbum()));
			perms.DeleteChildAlbum = isGalleryWriteable && (isGalleryAdmin || UserUtils.isUserAuthorized(SecurityActions.DeleteChildAlbum, roles, rootAlbum.getId(), galleryId, rootAlbum.getIsPrivate(), rootAlbum.getIsVirtualAlbum()));
			perms.DeleteContentObject = isGalleryWriteable && (isGalleryAdmin || UserUtils.isUserAuthorized(SecurityActions.DeleteContentObject, roles, rootAlbum.getId(), galleryId, rootAlbum.getIsPrivate(), rootAlbum.getIsVirtualAlbum()));
			perms.Synchronize = isGalleryAdmin || UserUtils.isUserAuthorized(SecurityActions.Synchronize, roles, rootAlbum.getId(), galleryId, rootAlbum.getIsPrivate(), rootAlbum.getIsVirtualAlbum());
			perms.HideWatermark = UserUtils.isUserAuthorized(SecurityActions.HideWatermark, roles, rootAlbum.getId(), galleryId, rootAlbum.getIsPrivate(), rootAlbum.getIsVirtualAlbum());
		}else{
			perms.ViewAlbumOrContentObject = isGalleryAdmin || UserUtils.isUserAuthorized(SecurityActions.ViewAlbumOrContentObject, roles, albumId, galleryId, isPrivate, isVirtual);
			perms.ViewOriginalContentObject = isGalleryAdmin || UserUtils.isUserAuthorized(SecurityActions.ViewOriginalContentObject, roles, albumId, galleryId, isPrivate, isVirtual);
			perms.AddChildAlbum = isGalleryWriteable && (isGalleryAdmin || UserUtils.isUserAuthorized(SecurityActions.AddChildAlbum, roles, albumId, galleryId, isPrivate, isVirtual));
			perms.AddContentObject = isGalleryWriteable && (isGalleryAdmin || UserUtils.isUserAuthorized(SecurityActions.AddContentObject, roles, albumId, galleryId, isPrivate, isVirtual));
			perms.EditAlbum = (isGalleryAdmin || UserUtils.isUserAuthorized(SecurityActions.EditAlbum, roles, albumId, galleryId, isPrivate, isVirtual));
			perms.EditContentObject = (isGalleryAdmin || UserUtils.isUserAuthorized(SecurityActions.EditContentObject, roles, albumId, galleryId, isPrivate, isVirtual));
			perms.DeleteAlbum = isGalleryWriteable && (isGalleryAdmin || UserUtils.isUserAuthorized(SecurityActions.DeleteAlbum, roles, albumId, galleryId, isPrivate, isVirtual));
			perms.DeleteChildAlbum = isGalleryWriteable && (isGalleryAdmin || UserUtils.isUserAuthorized(SecurityActions.DeleteChildAlbum, roles, albumId, galleryId, isPrivate, isVirtual));
			perms.DeleteContentObject = isGalleryWriteable && (isGalleryAdmin || UserUtils.isUserAuthorized(SecurityActions.DeleteContentObject, roles, albumId, galleryId, isPrivate, isVirtual));
			perms.Synchronize = isGalleryAdmin || UserUtils.isUserAuthorized(SecurityActions.Synchronize, roles, albumId, galleryId, isPrivate, isVirtual);
			perms.HideWatermark = UserUtils.isUserAuthorized(SecurityActions.HideWatermark, roles, albumId, galleryId, isPrivate, isVirtual);
		}

		return perms;
	}

	/// <summary>
	/// Update the album with the specified properties in the albumEntity parameter. Only the following properties are
	/// persisted: <see cref="AlbumRest.DateStart" />, <see cref="AlbumRest.DateEnd" />, <see cref="AlbumRest.SortById" />,
	/// <see cref="AlbumRest.SortUp" />, <see cref="AlbumRest.getIsPrivate()" />, <see cref="AlbumRest.Owner" />
	/// </summary>
	/// <param name="album">An <see cref="AlbumRest" /> instance containing data to be persisted to the data store.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="album" /> is null.</exception>
	/// <exception cref="MDS.EventLogs.CustomExceptions.GallerySecurityException">Thrown when the 
	/// user does not have edit permission to the specified album.</exception>
	public static void updateAlbumInfo(AlbumRest album) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException	{
		if (album == null)
			throw new ArgumentNullException("album");

		if (album.Owner == I18nUtils.getMessage("uc.album.Header_Edit_Album_No_Owner_Text")){
			album.Owner = StringUtils.EMPTY;
		}

		AlbumBo alb = AlbumUtils.loadAlbumInstance(album.Id, false, true);

		// Update remaining properties if user has edit album permission.
		SecurityGuard.throwIfUserNotAuthorized(SecurityActions.EditAlbum, RoleUtils.getMDSRolesForUser(), alb.getId(), alb.getGalleryId(), UserUtils.isAuthenticated(), alb.getIsPrivate(), alb.getIsVirtualAlbum());

		// OBSOLETE: As of 3.0, the title is updated through the metadata controller.
		//if (alb.Title != album.Title)
		//{
		//	GallerySettings gallerySetting = CMUtils.loadGallerySetting(alb.getGalleryId());

		//	alb.Title = Utils.CleanHtmlTags(album.Title, alb.getGalleryId());
		//	if ((!alb.IsRootAlbum) && (gallerySetting.SynchAlbumTitleAndDirectoryName))
		//	{
		//		// Root albums do not have a directory name that reflects the album's title, so only update this property for non-root albums.
		//		alb.DirectoryName = HelperFunctions.ValidateDirectoryName(alb.getParent().FullPhysicalPath, alb.Title, gallerySetting.DefaultAlbumDirectoryNameLength);
		//	}
		//}

		//alb.Summary = Utils.CleanHtmlTags(album.Summary, alb.getGalleryId());

		alb.setDateStart(album.DateStart);
		alb.setDateEnd(album.DateEnd);
		alb.setSortByMetaName(MetadataItemName.getMetadataItemName(album.SortById));
		alb.setSortAscending(album.SortUp);

		if (album.IsPrivate != alb.getIsPrivate())
		{
			if (!album.IsPrivate && alb.getParent().getIsPrivate())
			{
				throw new NotSupportedException("Cannot make album public: It is invalid to make an album public when it's parent album is private.");
			}
			alb.setIsPrivate(album.IsPrivate);

			String userName = UserUtils.getLoginName();
			//Task.CMUtils.startNew(() => SynchIsPrivatePropertyOnChildContentObjects(alb, userName));

			runAsync(new Runnable() {
				           public void run() {
				        	   synchIsPrivatePropertyOnChildContentObjects(alb, userName);
						   }
					}); 
		}

		// If the owner has changed, update it, but only if the user is administrator.
		if (album.Owner != alb.getOwnerUserName()){
			if (UserUtils.isUserAuthorized(SecurityActions.AdministerSite.value() | SecurityActions.AdministerGallery.value(), RoleUtils.getMDSRolesForUser(), alb.getId(), alb.getGalleryId(), alb.getIsPrivate(), alb.getIsVirtualAlbum()))	{
				if (!StringUtils.isBlank(alb.getOwnerUserName())){
					// Another user was previously assigned as owner. Delete role since this person will no longer be the owner.
					RoleUtils.deleteSystemRole(alb.getOwnerRoleName(), Long.MIN_VALUE);
				}

				if (UserUtils.getUsersCurrentUserCanView(alb.getGalleryId()).contains(album.Owner) || StringUtils.isBlank(album.Owner))	{
					// ContentObjectUtils.SaveContentObject will make sure there is a role created for this user.
					alb.setOwnerUserName(StringUtils.isBlank(album.Owner) ? StringUtils.EMPTY : album.Owner);
				}
			}
		}

		ContentObjectUtils.saveContentObject(alb);
		HelperFunctions.purgeCache();
	}

	/// <overloads>
	/// Permanently delete this album from the data store and optionally the hard drive.
	/// </overloads>
	/// <summary>
	/// Permanently delete this album from the data store and optionally the hard drive. Validation is performed prior to deletion to ensure
	/// current user has delete permission and the album can be safely deleted. The validation is contained in the method 
	/// <see cref="ValidateBeforeAlbumDelete"/> and may be invoked separately if desired.
	/// </summary>
	/// <param name="albumId">The ID of the album to delete.</param>
	/// <exception cref="CannotDeleteAlbumException">Thrown when the album does not meet the requirements for safe deletion.
	/// This includes detecting when the content objects path is read only and when the album is or contains the user album
	/// parent album and user albums are enabled.</exception>
	/// <exception cref="InvalidAlbumException">Thrown when <paramref name="albumId" /> does not represent an existing album.</exception>
	/// <exception cref="GallerySecurityException">Thrown when the current user does not have permission to delete the album.</exception>
	public static void deleteAlbum(long albumId) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, InvalidMDSRoleException, GallerySecurityException, InvalidGalleryException, WebException, CannotDeleteAlbumException{
		deleteAlbum(AlbumUtils.loadAlbumInstance(albumId, false));
	}

	/// <summary>
	/// Permanently delete this album from the data store and optionally the hard drive. Validation is performed prior to deletion to ensure
	/// current user has delete permission and the album can be safely deleted. The validation is contained in the method 
	/// <see cref="ValidateBeforeAlbumDelete"/> and may be invoked separately if desired.
	/// </summary>
	/// <param name="album">The album to delete. If null, the function returns without taking any action.</param>
	/// <param name="deleteFromFileSystem">if set to <c>true</c> the files and directories associated with the album
	/// are deleted from the hard disk. Set this to <c>false</c> to delete only the database records.</param>
	/// <exception cref="CannotDeleteAlbumException">Thrown when the album does not meet the requirements for safe deletion.
	/// This includes detecting when the content objects path is read only and when the album is or contains the user album
	/// parent album and user albums are enabled.</exception>
	/// <exception cref="GallerySecurityException">Thrown when the current user does not have permission to delete the album.</exception>
	public static void deleteAlbum(AlbumBo album) throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, InvalidGalleryException, WebException, CannotDeleteAlbumException	{
		deleteAlbum(album, true);
	}
	
	public static void deleteAlbum(AlbumBo album, boolean deleteFromFileSystem) throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, InvalidGalleryException, WebException, CannotDeleteAlbumException	{
		if (album == null)
			return;

		validateBeforeAlbumDelete(album);

		onBeforeAlbumDelete(album);

		if (deleteFromFileSystem){
			album.delete();
		}else{
			album.deleteFromGallery();
		}

		HelperFunctions.purgeCache();
	}

	/// <summary>
	/// Verifies that the album meets the prerequisites to be safely deleted but does not actually delete the album. Throws a
	/// <see cref="CannotDeleteAlbumException" /> when deleting it would violate a business rule. Throws a
	/// <see cref="GallerySecurityException" /> when the current user does not have permission to delete the album.
	/// </summary>
	/// <param name="albumToDelete">The album to delete.</param>
	/// <remarks>This function is automatically called when using the <see cref="DeleteAlbum(AlbumBo, boolean)"/> method, so it is not necessary to 
	/// invoke when using that method. Typically you will call this method when there are several items to delete and you want to 
	/// check all of them before deleting any of them, such as we have on the Delete Objects page.</remarks>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="albumToDelete" /> is null.</exception>
	/// <exception cref="CannotDeleteAlbumException">Thrown when the album does not meet the 
	/// requirements for safe deletion.</exception>
	/// <exception cref="GallerySecurityException">Thrown when the current user does not have permission to delete the album.</exception>
	public static void validateBeforeAlbumDelete(AlbumBo albumToDelete) throws CannotDeleteAlbumException, UnsupportedContentObjectTypeException, GallerySecurityException, InvalidAlbumException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException, IOException, WebException	{
		if (albumToDelete == null)
			throw new ArgumentNullException("albumToDelete");

		AlbumBo userAlbum = UserUtils.getUserAlbum(UserUtils.getLoginName(), albumToDelete.getGalleryId());
		boolean curUserDeletingOwnUserAlbum = (userAlbum != null && userAlbum.getId() == albumToDelete.getId());
		// Skip security check when user is deleting their own user album. Normally this won't happen (the menu action for deleting will be 
		// disabled), but it will happen when they delete their user album or their account on the account page, and this is one situation 
		// where it is OK for them to delete their album.
		if (!curUserDeletingOwnUserAlbum){
			SecurityGuard.throwIfUserNotAuthorized(SecurityActions.DeleteAlbum, RoleUtils.getMDSRolesForUser(), albumToDelete.getId(), albumToDelete.getGalleryId(), UserUtils.isAuthenticated(), albumToDelete.getIsPrivate(), albumToDelete.getIsVirtualAlbum());
		}

		if (CMUtils.loadGallerySetting(albumToDelete.getGalleryId()).getContentObjectPathIsReadOnly()){
			throw new CannotDeleteAlbumException(I18nUtils.getMessage("task.deleteAlbum.Cannot_Delete_ContentPathIsReadOnly"));
		}

		AlbumDeleteValidator validator = new AlbumDeleteValidator(albumToDelete);

		validator.validate();

		if (!validator.canBeDeleted()){
			switch (validator.getValidationFailureReason()){
				case AlbumSpecifiedAsUserAlbumContainer:
				case AlbumContainsUserAlbumContainer:
					{
						String albumTitle = StringUtils.join("'", albumToDelete.getTitle(), "' (ID# ", Long.toString(albumToDelete.getId()), ")");
						String msg = I18nUtils.getMessage("task.deleteAlbum.Cannot_Delete_Contains_User_Album_Parent_Ex_Msg", albumTitle);

						throw new CannotDeleteAlbumException(msg);
					}
				case AlbumSpecifiedAsDefaultContentObject:
				case AlbumContainsDefaultContentObjectAlbum:
				case AlbumContainsDefaultContentObjectContentObject:
					{
						String albumTitle = StringUtils.join("'", albumToDelete.getTitle(), "' (ID# ", Long.toString(albumToDelete.getId()), ")");
						String msg = I18nUtils.getMessage("task.deleteAlbum.Cannot_Delete_Contains_Default_Gallery_Object_Ex_Msg", albumTitle);

						throw new CannotDeleteAlbumException(msg);
					}
				default:
					throw new InvalidEnumArgumentException(MessageFormat.format("The function ValidateBeforeAlbumDelete is not designed to handle the enumeration value {0}. The function must be updated.", validator.getValidationFailureReason()));
			}
		}
	}

	/// <summary>
	/// Gets the URL to the specified <paramref name="album" />.
	/// </summary>
	/// <param name="album">The album.</param>
	/// <returns>System.String.</returns>
	/// <exception cref="System.InvalidOperationException">Thrown when the function encounters a virtual album
	/// type it was not designed to handle.</exception>
	public static String getUrl(AlbumBo album, HttpServletRequest request){
		String appPath = Utils.getCurrentPageUrl(request);
		String strApproval = Utils.getQueryStringParameterString(request, "approval");

		switch (album.getVirtualAlbumType())	{
			case NotSpecified:
			case NotVirtual:
				if (strApproval == ""){
					return Utils.getUrl(request, ResourceId.album, "aid={0}", album.getId());
				}else{
					return Utils.getUrl(request, ResourceId.album, "aid={0}&approval={1}", album.getId(), strApproval);
				}
			case Root:
				return appPath;
			case TitleOrCaption:
				if (strApproval == ""){
					return Utils.getUrl(request, ResourceId.album, "title={0}", Utils.urlEncode(Utils.getQueryStringParameterString(request, "title")));
				}else{
					return Utils.getUrl(request, ResourceId.album, "title={0}&approval={1}", Utils.urlEncode(Utils.getQueryStringParameterString(request, "title")), strApproval);
				}
			case Tag:
				if (strApproval == ""){
					return Utils.getUrl(request, ResourceId.album, "tag={0}", Utils.urlEncode(Utils.getQueryStringParameterString(request, "tag")));
				}else{
					return Utils.getUrl(request, ResourceId.album, "tag={0}&approval={1}", Utils.urlEncode(Utils.getQueryStringParameterString(request, "tag")), strApproval);
				}
			case People:
				if (strApproval == ""){
					return Utils.getUrl(request, ResourceId.album, "people={0}", Utils.urlEncode(Utils.getQueryStringParameterString(request, "people")));
				}else{
					return Utils.getUrl(request, ResourceId.album, "people={0}&approval={1}", Utils.urlEncode(Utils.getQueryStringParameterString(request, "people")), strApproval);
				}
			case Search:
				if (strApproval == ""){
					return Utils.getUrl(request, ResourceId.album, "search={0}", Utils.urlEncode(Utils.getQueryStringParameterString(request, "search")));
				}else{
					return Utils.getUrl(request, ResourceId.album, "search={0}&approval={1}", Utils.urlEncode(Utils.getQueryStringParameterString(request, "search")), strApproval);
				}
			case MostRecentlyAdded:
				if (strApproval == ""){
					return Utils.getUrl(request, ResourceId.album, "latest={0}", Utils.getQueryStringParameterInt32(request, "latest"));
				}else{
					return Utils.getUrl(request, ResourceId.album, "latest={0}&approval={1}", Utils.getQueryStringParameterInt32(request, "latest"), strApproval);
				}
			case Rated:
				if (strApproval == ""){
					return Utils.getUrl(request, ResourceId.album, "rating={0}&top={1}", Utils.getQueryStringParameterString(request, "rating"), Utils.getQueryStringParameterInt32(request, "top"));
				}else{
					return Utils.getUrl(request, ResourceId.album, "rating={0}&top={1}&approval={2}", Utils.getQueryStringParameterString(request, "rating"), Utils.getQueryStringParameterInt32(request, "top"), strApproval);
				}
			case Approval:
				return Utils.getUrl(request, ResourceId.album, "approval={0}&top={1}", Utils.getQueryStringParameterString(request, "approval"), Utils.getQueryStringParameterInt32(request, "top"));
			default:
				throw new UnsupportedOperationException(MessageFormat.format("The method AlbumUtils.getUrl() encountered a VirtualAlbumType ({0}) it was not designed to handle. The developer must update this method.", album.getVirtualAlbumType()));
		}
	}

	/// <summary>
	/// Gets the RSS URL for the specified <paramref name="album" />. Returns null if the user is not 
	/// running the Enterprise version or no applicable RSS URL exists for the album. For example, 
	/// virtual root albums that are used for restricted users will return null.
	/// </summary>
	/// <param name="album">The album.</param>
	/// <returns>System.String.</returns>
	public static String getRssUrl(AlbumBo album, HttpServletRequest request){
		/*if (AppSettings.getInstance().License.LicenseType != LicenseLevel.Enterprise){
			return null;
		}*/

		switch (album.getVirtualAlbumType())	{
			case NotVirtual:
				return StringUtils.join(Utils.getAppRoot(request), "/services/api/feed/album?id=", album.getId());
			case TitleOrCaption:
				return StringUtils.format("{0}/services/api/feed/title?q={1}&galleryid={2}", Utils.getAppRoot(request), Utils.getQueryStringParameterString(request, "title"), album.getGalleryId());
			case Search:
				return StringUtils.format("{0}/services/api/feed/search?q={1}&galleryid={2}", Utils.getAppRoot(request), Utils.getQueryStringParameterString(request, "search"), album.getGalleryId());
			case Tag:
				return StringUtils.format("{0}/services/api/feed/tag?q={1}&galleryid={2}", Utils.getAppRoot(request), Utils.getQueryStringParameterString(request, "tag"), album.getGalleryId());
			case People:
				return StringUtils.format("{0}/services/api/feed/people?q={1}&galleryid={2}", Utils.getAppRoot(request), Utils.getQueryStringParameterString(request, "people"), album.getGalleryId());
			case MostRecentlyAdded:
				return StringUtils.format("{0}/services/api/feed/latest?galleryid={1}", Utils.getAppRoot(request), album.getGalleryId());
			case Rated:
				return StringUtils.format("{0}/services/api/feed/rating?rating={1}&top={2}&galleryid={3}", Utils.getAppRoot(request), Utils.getQueryStringParameterString(request, "rating"), Utils.getQueryStringParameterInt32(request, "top"), album.getGalleryId());
			default:
				return null;
		}
	}

	/// <summary>
	/// Sorts the <paramref name="contentItems" /> in the order in which they are passed.
	/// This method is used when a user is manually sorting an album and has dragged an item to a new position.
	/// </summary>
	/// <param name="contentItems">The content objects to sort. Their position in the array indicates the desired
	/// sequence. Only <see cref="ContentItem.getId()" /> and <see cref="ContentItem.ItemType" /> need be
	/// populated.</param>
	/// <param name="userName">Name of the logged on user.</param>
	public static void sort(ContentItem[] contentItems, String userName) throws Exception{
		if (contentItems == null || contentItems.length == 0){
			return;
		}

		try	{
			// To improve performance, grab a writable collection of all the items in the album containing the first item.
			// At the time this function was written, the contentItems parameter will only include items in a single album,
			// so this step allows us to get all the items in a single step. For robustness and to support all possible usage,
			// the code in the iteration manually loads a writable instance if it's not in the collection.
			ContentObjectBoCollection contentObjects = getWritableSiblingContentObjects(contentItems[0]);

			int seq = 1;
			for (ContentItem contentItem : contentItems){
				// Loop through each item and update its Sequence property to match the order in which it was passed.
				ContentItem item = contentItem;

				ContentObjectBo contentObject = contentObjects.stream().filter(go -> go.getId() == item.Id && go.getContentObjectType().getValue() == item.ItemType).findFirst().orElse(null);

				if (contentObject == null){
					// Not found, so load it manually. This is not expected to ever occur when manually sorting an album, but we 
					// include it for robustness.
					if (contentItem.ItemType == ContentObjectType.Album.getValue())	{
						contentObject = CMUtils.loadAlbumInstance(contentItem.Id, false, true);
					}else{
						contentObject = CMUtils.loadContentObjectInstance(contentItem.Id, true);
					}
				}

				contentObject.setSequence(seq);
				ContentObjectUtils.saveContentObject(contentObject, userName);
				seq++;
			}

			HelperFunctions.purgeCache();
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);

			throw ex;
		}
	}

	/// <summary>
	/// Re-sort the items in the album according to the criteria and store this updated sequence in the
	/// database. Callers must have <see cref="SecurityActions.EditAlbum" /> permission.
	/// </summary>
	/// <param name="albumId">The album ID.</param>
	/// <param name="sortByMetaNameId">The name of the metadata item to sort on.</param>
	/// <param name="sortAscending">If set to <c>true</c> sort in ascending order.</param>
	/// <exception cref="System.Web.Http.HttpResponseException"></exception>
	public static void sort(long albumId, int sortByMetaNameId, boolean sortAscending) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, GallerySecurityException, InvalidMDSRoleException, IOException, InvalidGalleryException, WebException	{
		AlbumBo album = loadAlbumInstance(albumId, true, true);

		SecurityGuard.throwIfUserNotAuthorized(SecurityActions.EditAlbum, RoleUtils.getMDSRolesForUser(), album.getId(), album.getGalleryId(), UserUtils.isAuthenticated(), album.getIsPrivate(), album.getIsVirtualAlbum());

		MetadataItemName oldSortByMetaName = album.getSortByMetaName();
		boolean oldSortAscending = album.getSortAscending();

		album.setSortByMetaName(MetadataItemName.getMetadataItemName(sortByMetaNameId));
		album.setSortAscending(sortAscending);

		reverseCustomSortIfNeeded(album, oldSortByMetaName, oldSortAscending);

		album.sort(true, UserUtils.getLoginName());

		HelperFunctions.purgeCache();
	}
	
	public static TreeView getTreeView(long id, int secaction, boolean sc, String navurl, HttpServletRequest request) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, GallerySecurityException, IOException, InvalidGalleryException, WebException{
		// We'll use a TreeView instance to generate the appropriate JSON structure 
		TreeView tv = new TreeView();
		  
		AlbumBo parentAlbum = AlbumUtils.loadAlbumInstance(id, true);
		
		List<AlbumBo> childAlbums = parentAlbum.getChildContentObjects(ContentObjectType.Album, ApprovalStatus.All, !Utils.isAuthenticated()).toAlbumSortedList();
		for (AlbumBo childAlbum : childAlbums){
			TreeNode node = new TreeNode();
			node.setId(StringUtils.join("tv_", Long.toString(childAlbum.getId())));
			node.setText(Utils.removeHtmlTags(childAlbum.getTitle()));
			node.setDataId(Long.toString(childAlbum.getId()));
		
			if (!StringUtils.isEmpty(navurl)){
				node.setNavigateUrl(Utils.addQueryStringParameter(navurl, StringUtils.join("aid=", Long.toString(childAlbum.getId()))));
		    }
		
		    boolean isUserAuthorized = true;
		    if (SecurityActions.isValidSecurityAction(secaction)) {
		      isUserAuthorized = UserUtils.isUserAuthorized(secaction, RoleUtils.getMDSRolesForUser(), childAlbum.getId(), childAlbum.getGalleryId(), childAlbum.getIsPrivate(), childAlbum.getIsVirtualAlbum());
		    }
		    
		    node.setShowCheckBox(isUserAuthorized && sc);
		    node.setSelectable(isUserAuthorized);
		
		    if (!childAlbum.getChildContentObjects(ContentObjectType.Album, ApprovalStatus.All, !UserUtils.isAuthenticated()).values().isEmpty())  {
		      node.setChildren();
		    }
		
		    tv.getNodes().addTreeNode(node);
		  }
				  
		  return tv;
	}
	
	public static HashMap<String, Object> albumsTreeTable(long id, HttpServletRequest request) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, GallerySecurityException, IOException, InvalidGalleryException, WebException{

		List<Map<String, Object>> mapList = Lists.newArrayList();
		if (id < 1) {
			GalleryBoCollection galleries = CMUtils.loadLoginUserGalleries();
			for (GalleryBo gallery : galleries) {
				AlbumBo parentAlbum = CMUtils.loadRootAlbumInstance(gallery.getGalleryId());
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", parentAlbum.getId());
				map.put("pid", (parentAlbum.getParent() instanceof NullContentObject) ? 0 : parentAlbum.getParent().getId());
				map.put("code", parentAlbum.getTitle().replace("{album.root_Album_Default_Title}", I18nUtils.getMessage("album.root_Album_Default_Title")));
				map.put("gallery", gallery.getName());
				map.put("createdBy", parentAlbum.getCreatedByUserName());
				map.put("dateAdded", DateUtils.jsonSerializer(parentAlbum.getDateAdded()));
								
				mapList.add(map);
				
				mapList = toTreeTable(parentAlbum, mapList);
			}			
		}else {
			AlbumBo parentAlbum = AlbumUtils.loadAlbumInstance(id, true);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", parentAlbum.getId());
			map.put("pid", (parentAlbum.getParent() instanceof NullContentObject) ? 0 : parentAlbum.getParent().getId());
			map.put("code", parentAlbum.getTitle().replace("{album.root_Album_Default_Title}", I18nUtils.getMessage("album.root_Album_Default_Title")));
			map.put("gallery", CMUtils.loadGallery(parentAlbum.getGalleryId()).getName());
			map.put("createdBy", parentAlbum.getCreatedByUserName());
			map.put("dateAdded", DateUtils.jsonSerializer(parentAlbum.getDateAdded()));
							
			mapList.add(map);
			
			mapList = toTreeTable(parentAlbum, mapList);
		}
		  
		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
	    resultData.put("total", mapList.size());
		resultData.put("rows", mapList);
		
		return resultData;
	}

	//#endregion

	//#region Private Static Methods
	
	/**
	 * convert menuFunction data to bootstrap table tree grid format(http://issues.wenzhixin.net.cn/bootstrap-table/#extensions/treegrid.html)
	 * {
	 *	  [
		    {
		      "id": 1,
		      "pid": 0,
		      "status": 1,
		      "name": "system management",
		      "permissionValue": "open:system:get"
		    },
		    {
		      "id": 2,
		      "pid": 0,
		      "status": 1,
		      "name": "dictory management",
		      "permissionValue": "open:dict:get"
		    },
	 *	  ]
	 *	}
	 * @param list: menuFunctions
	 * @return
	 * @throws InvalidGalleryException 
	 */
    public static List<Map<String, Object>> toTreeTable(AlbumBo parentAlbum, List<Map<String, Object>> mapList) throws InvalidGalleryException {
		//List<Map<String, Object>> mapList = Lists.newArrayList();
    	List<AlbumBo> childAlbums = parentAlbum.getChildContentObjects(ContentObjectType.Album, ApprovalStatus.All, !Utils.isAuthenticated()).toAlbumSortedList();
		for (int i=0; i<childAlbums.size(); i++){
			AlbumBo e = childAlbums.get(i);
			
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pid", (e.getParent() instanceof NullContentObject) ? 0 : e.getParent().getId());
			map.put("code", e.getTitle().replace("{album.root_Album_Default_Title}", I18nUtils.getMessage("album.root_Album_Default_Title")));
			map.put("gallery", CMUtils.loadGallery(parentAlbum.getGalleryId()).getName());
			map.put("createdBy", e.getCreatedByUserName());
			map.put("dateAdded", DateUtils.jsonSerializer(e.getDateAdded()));
							
			mapList.add(map);
			mapList = toTreeTable(e, mapList);
		}
		
		return mapList;
	}   

	/// <summary>
	/// Performs any necessary actions that must occur before an album is deleted. Specifically, it deletes the owner role 
	/// if one exists for the album, but only when this album is the only one assigned to the role. It also clears out  
	/// <see cref="GallerySettings.getUserAlbumParentAlbumId()" /> if the album's ID matches it. This function recursively calls
	/// itself to make sure all child albums are processed.
	/// </summary>
	/// <param name="album">The album to be deleted, or one of its child albums.</param>
	private static void onBeforeAlbumDelete(AlbumBo album) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException	{
		// If there is an owner role associated with this album, and the role is not assigned to any other albums, delete it.
		if (!StringUtils.isBlank(album.getOwnerRoleName())){
			MDSRole role = RoleUtils.getMDSRoles().getRole(album.getOwnerRoleName(), Long.MIN_VALUE);

			if ((role != null) && (role.getAllAlbumIds().size() == 1) && role.getAllAlbumIds().contains(album.getId()))	{
				RoleUtils.deleteSystemRole(role.getRoleName(), Long.MIN_VALUE);
			}
		}

		// If the album is specified as the user album container, clear out the setting. The ValidateBeforeAlbumDelete()
		// function will throw an exception if user albums are enabled, so this should only happen when user albums
		// are disabled, so it is safe to clear it out.
		int userAlbumParentAlbumId = CMUtils.loadGallerySetting(album.getGalleryId()).getUserAlbumParentAlbumId();
		if (album.getId() == userAlbumParentAlbumId){
			GallerySettings gallerySettingsWriteable = CMUtils.loadGallerySetting(album.getGalleryId(), true);
			gallerySettingsWriteable.setUserAlbumParentAlbumId(0);
			gallerySettingsWriteable.save();
		}

		// Recursively validate child albums.
		List<ContentObjectBo> childObjs = album.getChildContentObjects(ContentObjectType.Album).values();
		for (ContentObjectBo childAlbum : childObjs){
			onBeforeAlbumDelete((AlbumBo)childAlbum);
		}
	}

	/// <summary>
	/// Finds the first album within the heirarchy of the specified <paramref name="album"/> whose ID is in 
	/// <paramref name="albumIds"/>. Acts recursively in an across-first, then-down search pattern, resulting 
	/// in the highest level matching album to be returned. Returns null if there are no matching albums.
	/// </summary>
	/// <param name="album">The album to be searched to see if it, or any of its children, matches one of the IDs
	/// in <paramref name="albumIds"/>.</param>
	/// <param name="albumIds">Contains the IDs of the albums to search for.</param>
	/// <returns>Returns the first album within the heirarchy of the specified <paramref name="album"/> whose ID is in 
	/// <paramref name="albumIds"/>.</returns>
	private static AlbumBo findFirstMatchingAlbumRecursive(AlbumBo album, Collection<Long> albumIds){
		// Is the current album in the list?
		if (albumIds.contains(album.getId()))
			return album;

		// Nope, so look at the child albums of this album.
		AlbumBo albumToSelect = null;
		List<ContentObjectBo> childAlbums = album.getChildContentObjects(ContentObjectType.Album).toSortedList();

		for (ContentObjectBo childAlbum : childAlbums){
			if (albumIds.contains(childAlbum.getId())){
				albumToSelect = (AlbumBo)childAlbum;
				break;
			}
		}

		// Not the child albums either, so iterate through the children of the child albums. Act recursively.
		if (albumToSelect == null){
			for (ContentObjectBo childAlbum : childAlbums){
				albumToSelect = findFirstMatchingAlbumRecursive((AlbumBo)childAlbum, albumIds);

				if (albumToSelect != null)
					break;
			}
		}

		return albumToSelect; // Returns null if no matching album is found
	}

	private static void saveAlbumIdToProfile(long albumId, String userName, long galleryId) throws JsonProcessingException, UnsupportedContentObjectTypeException, InvalidGalleryException	{
		UserProfile profile = ProfileUtils.getProfile(userName);

		UserGalleryProfile pg = profile.getGalleryProfile(galleryId);
		pg.setUserAlbumId(albumId);

		ProfileUtils.saveProfile(profile);
	}

	/// <summary>
	/// Set the IsPrivate property of all child albums and content objects of the specified album to have the same value
	/// as the specified album. This can be a long running operation and should be scheduled on a background thread.
	/// This function, and its decendents, have no dependence on the HTTP Context.
	/// </summary>
	/// <param name="album">The album whose child objects are to be updated to have the same IsPrivate value.</param>
	/// <param name="userName">Name of the current user.</param>
	private static void synchIsPrivatePropertyOnChildContentObjects(AlbumBo album, String userName)	{
		try	{
			synchIsPrivatePropertyOnChildContentObjectsRecursive(album, userName);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);
		}

		HelperFunctions.purgeCache();
	}

	/// <summary>
	/// Set the IsPrivate property of all child albums and content objects of the specified album to have the same value
	/// as the specified album.
	/// </summary>
	/// <param name="album">The album whose child objects are to be updated to have the same IsPrivate value.</param>
	/// <param name="userName">Name of the current user.</param>
	private static void synchIsPrivatePropertyOnChildContentObjectsRecursive(AlbumBo album, String userName) throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, InvalidGalleryException, WebException{
		album.inflate(true);
		List<ContentObjectBo> childObjs = album.getChildContentObjects(ContentObjectType.Album).values();
		for (ContentObjectBo childObj : childObjs){
			AlbumBo childAlbum = (AlbumBo)childObj;
			childAlbum.inflate(true); // The above Inflate() does not inflate child albums, so we need to explicitly inflate it.
			childAlbum.setIsPrivate(album.getIsPrivate());
			ContentObjectUtils.saveContentObject(childAlbum, userName);
			synchIsPrivatePropertyOnChildContentObjectsRecursive(childAlbum, userName);
		}

		childObjs = album.getChildContentObjects(ContentObjectType.ContentObject).values();
		for (ContentObjectBo childContentObject : childObjs){
			childContentObject.setIsPrivate(album.getIsPrivate());
			ContentObjectUtils.saveContentObject(childContentObject, userName);
		}
	}

	/// <summary>
	/// Inspects the specified <paramref name="album" /> to see if the <see cref="AlbumBo.getOwnerUserName()" /> is an existing user.
	/// If not, the property is cleared out (which also clears out the <see cref="AlbumBo.getOwnerRoleName()" /> property).
	/// </summary>
	/// <param name="album">The album to inspect.</param>
	private static void validateAlbumOwner(AlbumBo album) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException{
		if ((!StringUtils.isBlank(album.getOwnerUserName())) && (!UserUtils.getAllUsers().contains(album.getOwnerUserName())))
		{
			if (RoleUtils.getUsersInRole(album.getOwnerRoleName(), Long.MIN_VALUE).length == 0)	{
				RoleUtils.deleteSystemRole(album.getOwnerRoleName(), Long.MIN_VALUE);
			}

			if (album.getIsWritable()){
				album.setOwnerUserName(StringUtils.EMPTY); // This will also clear out the OwnerRoleName property.

				ContentObjectUtils.saveContentObject(album);
			}else{
				// Load a writeable version and update the database, then do the same update to our in-memory instance.
				AlbumBo albumWritable = CMUtils.loadAlbumInstance(album.getId(), false, true);

				albumWritable.setOwnerUserName(StringUtils.EMPTY); // This will also clear out the OwnerRoleName property.

				ContentObjectUtils.saveContentObject(albumWritable);

				// Update our local in-memory object to match the one we just saved.
				album.setOwnerUserName(StringUtils.EMPTY);
				album.setOwnerRoleName(StringUtils.EMPTY);
			}

			// Remove this item from cache so that we don't have any old copies floating around.
			ConcurrentHashMap<Long, AlbumBo> albumCache = (ConcurrentHashMap<Long, AlbumBo>)CacheUtils.get(CacheItem.cm_albums);

			if (albumCache != null)	{
				albumCache.remove(album.getId());
			}
		}
	}

	/// <summary>
	/// Gets a writable collection of the content objects in the album containing <paramref name="contentItem" />, including 
	/// <paramref name="contentItem" />. If <paramref name="contentItem" /> does not represent a valid object, an empty 
	/// collection is returned. Guaranteed to not return null.
	/// </summary>
	/// <param name="contentItem">A gallery item. The object must have the <see cref="ContentItem.getId()" /> and 
	/// <see cref="ContentItem.ItemType" /> properties specified; the others are optional.</param>
	/// <returns>An instance of <see cref="ContentObjectBoCollection" />.</returns>
	private static ContentObjectBoCollection getWritableSiblingContentObjects(ContentItem contentItem) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException	{
		ContentObjectBo parentAlbum;

		try	{
			long parentAlbumId;
			if (contentItem.ItemType == ContentObjectType.Album.getValue()){
				parentAlbumId = loadAlbumInstance(contentItem.Id, false).getParent().getId();
			}else{
				parentAlbumId = CMUtils.loadContentObjectInstance(contentItem.Id).getParent().getId();
			}

			parentAlbum = loadAlbumInstance(parentAlbumId, true, true);
		}catch (InvalidAlbumException ae){
			parentAlbum = new NullContentObject();
		}catch (InvalidContentObjectException ce){
			parentAlbum = new NullContentObject();
		}

		return parentAlbum.getChildContentObjects();
	}

	/// <summary>
	/// Reverse the content objects in the <paramref name="album" /> if they are custom sorted and the user
	/// clicked the reverse sort button (i.e. changed the <paramref name="previousSortAscending" /> value).
	/// This can't be handled by the normal sort routine because we aren't actually sorting on any particular
	/// metadata value.
	/// </summary>
	/// <param name="album">The album whose items are to be sorted.</param>
	/// <param name="previousSortByMetaName">The name of the metadata property the album was previously sorted on.</param>
	/// <param name="previousSortAscending">Indicates whether the album was previously sorted in ascending order.</param>
	private static void reverseCustomSortIfNeeded(AlbumBo album, MetadataItemName previousSortByMetaName, boolean previousSortAscending){
		boolean albumIsCustomSortedAndUserIsChangingSortDirection = ((album.getSortByMetaName() == MetadataItemName.NotSpecified)
			&& (album.getSortByMetaName() == previousSortByMetaName)
			&& (album.getSortAscending() != previousSortAscending));

		if (albumIsCustomSortedAndUserIsChangingSortDirection)	{
			// Album is being manually sorted and user clicked the reverse button.
			int seq = 1;
			for (ContentObjectBo contentObject : album.getChildContentObjects().toSortedList().stream().sorted(Collections.reverseOrder()).collect(Collectors.toList()))	{
				contentObject.setSequence(seq);
				seq++;
			}
		}
	}

	//#endregion
}
