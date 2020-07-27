package com.mds.aiotplayer.cm.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import static java.util.concurrent.CompletableFuture.runAsync;

import java.io.IOException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.content.ContentObjectApproval;
import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.cm.content.ContentObjectBoCollection;
import com.mds.aiotplayer.cm.content.ContentObjectProfile;
import com.mds.aiotplayer.sys.util.SecurityGuard;
import com.mds.aiotplayer.cm.content.TagSearchOptions;
import com.mds.aiotplayer.cm.content.UserProfile;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.metadata.ContentObjectMetadataItem;
import com.mds.aiotplayer.cm.metadata.ContentObjectMetadataItemCollection;
import com.mds.aiotplayer.cm.metadata.MetadataDefinition;
import com.mds.aiotplayer.cm.metadata.MetadataDefinitionCollection;
import com.mds.aiotplayer.cm.rest.ContentItem;
import com.mds.aiotplayer.cm.rest.ContentItemMeta;
import com.mds.aiotplayer.cm.rest.MetaItemRest;
import com.mds.aiotplayer.cm.rest.TagRest;
import com.mds.aiotplayer.cm.rest.TreeView;
import com.mds.aiotplayer.core.ActionResult;
import com.mds.aiotplayer.core.ActionResultStatus;
import com.mds.aiotplayer.core.ApprovalAction;
import com.mds.aiotplayer.core.ApprovalStatus;
import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.core.TagSearchType;
import com.mds.aiotplayer.core.exception.ArgumentException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.core.exception.WebException;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.common.mapper.JsonMapper;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.util.Utils;

/// <summary>
/// Contains functionality for interacting with metadata.
/// </summary>
public final class MetadataUtils {
	//#region Methods

	/// <summary>
	/// Gets the meta item with the specified <paramref name="id" />. Since the current API
	/// cannot efficient look up the content object ID and type, those are included as required
	/// parameters. They are assigned to the corresponding properties of the returned instance.
	/// Verifies user has permission to view item, throwing <see cref="GallerySecurityException" /> 
	/// if authorization is denied.
	/// </summary>
	/// <param name="id">The value that uniquely identifies the metadata item.</param>
	/// <param name="contentObjectId">The content object ID. It is assigned to 
	/// <see cref="MetaItemRest.ContentId" />.</param>
	/// <param name="goType">Type of the content object. It is assigned to 
	/// <see cref="MetaItemRest.GTypeId" />.</param>
	/// <returns>An instance of <see cref="MetaItemRest" />.</returns>
	/// <exception cref="GallerySecurityException">Thrown when user does not have permission to
	/// view the requested item.</exception>
	/// <exception cref="InvalidContentObjectException">Thrown when the requested meta item or its
	/// associated content object does not exist in the data store.</exception>
	/// <exception cref="InvalidAlbumException">Thrown when the album associated with the
	/// meta item does not exist in the data store.</exception>
	public static MetaItemRest get(long id, long contentObjectId, ContentObjectType goType) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidContentObjectException, UnsupportedImageTypeException, GallerySecurityException, InvalidMDSRoleException, InvalidGalleryException	{
		ContentObjectMetadataItem md = CMUtils.loadContentObjectMetadataItem(id);
		if (md == null)
			throw new InvalidContentObjectException(MessageFormat.format("No metadata item with ID {0} could be found in the data store.", id));

		// Security check: Make sure user has permission to view item
		ContentObjectBo go;
		if (goType == ContentObjectType.Album){
			go = CMUtils.loadAlbumInstance(contentObjectId, false);
			SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), go.getId(), go.getGalleryId(), UserUtils.isAuthenticated(), go.getIsPrivate(), ((AlbumBo)go).getIsVirtualAlbum());
		}else{
			go = CMUtils.loadContentObjectInstance(contentObjectId);
			SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), go.getParent().getId(), go.getGalleryId(), UserUtils.isAuthenticated(), go.getParent().getIsPrivate(), ((AlbumBo)go.getParent()).getIsVirtualAlbum());
		}

		boolean isEditable = CMUtils.loadGallerySetting(go.getGalleryId()).getMetadataDisplaySettings().find(md.getMetadataItemName()).IsEditable;
		
		return new MetaItemRest(
						 md.getContentObjectMetadataId(),
						 contentObjectId,
						 md.getMetadataItemName(),
						 goType,
						 md.getDescription(),
						 md.getValue(),
						 isEditable
					 );
	}

	/// <summary>
	/// Gets the requested <paramref name="metaName" /> instance for the specified <paramref name="contentObjectId" />
	/// having the specified <paramref name="goType" />. Returns null if no metadata item exists.
	/// Verifies user has permission to view item, throwing <see cref="GallerySecurityException" /> 
	/// if authorization is denied.
	/// </summary>
	/// <param name="contentObjectId">The ID for either an album or a content object.</param>
	/// <param name="goType">The type of content object.</param>
	/// <param name="metaName">Name of the metaitem to return.</param>
	/// <returns>Returns an instance of <see cref="ContentObjectMetadataItemCollection" />.</returns>
	/// <exception cref="GallerySecurityException">Thrown when user does not have permission to
	/// view the requested item.</exception>
	/// <exception cref="InvalidContentObjectException">Thrown when the requested meta item or its
	/// associated content object does not exist in the data store.</exception>
	/// <exception cref="InvalidAlbumException">Thrown when the album associated with the
	/// meta item does not exist in the data store.</exception>
	public static ContentObjectMetadataItem get(long contentObjectId, ContentObjectType goType, MetadataItemName metaName) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, GallerySecurityException, InvalidMDSRoleException, InvalidGalleryException{
		// Security check: Make sure user has permission to view item
		ContentObjectBo go;
		if (goType == ContentObjectType.Album){
			go = CMUtils.loadAlbumInstance(contentObjectId, false);
			SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), go.getId(), go.getGalleryId(), UserUtils.isAuthenticated(), go.getIsPrivate(), ((AlbumBo)go).getIsVirtualAlbum());
		}else{
			go = CMUtils.loadContentObjectInstance(contentObjectId);
			SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), go.getParent().getId(), go.getGalleryId(), UserUtils.isAuthenticated(), go.getParent().getIsPrivate(), ((AlbumBo)go.getParent()).getIsVirtualAlbum());
		}

		ContentObjectMetadataItem md = getContentObjectMetadataItemCollection(contentObjectId, goType).tryGetMetadataItem(metaName);

		return md;
	}

	/// <summary>
	/// Gets the meta items for specified <paramref name="contentItems" />, merging metadata
	/// when necessary. Specifically, tags and people tags are merged and updated with a count.
	/// Example: "Animal (3), Dog (2), Cat (1)" indicates three of the gallery items have the 
	/// 'Animal' tag, two have the 'Dog' tag, and one has the 'Cat' tag. Guaranteed to not 
	/// return null.
	/// </summary>
	/// <param name="contentItems">The gallery items for which to retrieve metadata.</param>
	/// <returns>Returns a collection of <see cref="MetaItemRest" /> items.</returns>
	/// <exception cref="InvalidAlbumException">Thrown when the requested album does not exist.</exception>
	/// <exception cref="InvalidContentObjectException">Thrown when the requested content object does not exist.</exception>
	public static Collection<MetaItemRest> getMetaItemsForContentItems(ContentItem[] contentItems, HttpServletRequest request) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, GallerySecurityException, InvalidMDSRoleException, IOException, InvalidGalleryException, WebException{
		if (contentItems == null || contentItems.length == 0)
			return Lists.newArrayList();

		MetadataItemName[] tagNames = new MetadataItemName[] { MetadataItemName.Tags, MetadataItemName.People };
		String[] tagValues = new String[tagNames.length]; // Eventually will contain the merged tag values

		// Iterate through each tag type and generate the merge tag value.
		for (int i = 0; i < tagNames.length; i++){
			//String[] tags = getTagListForContentItems(Arrays.asList(contentItems), tagNames[i]);

			tagValues[i] = getTagsWithCount(getTagListForContentItems(Arrays.asList(contentItems), tagNames[i]));
		}

		// Get the metadata for the last item and merge our computed tags into it
		ContentItem lastGi = contentItems[contentItems.length - 1];
		MetaItemRest[] meta = getMetaItems(lastGi, request);

		for (int i = 0; i < tagValues.length; i++){
			MetaItemRest tagMi = getMetaItemForTag(meta, tagNames[i], lastGi);
			tagMi.Value = tagValues[i];
		}

		return Lists.newArrayList(meta);
	}

	/// <summary>
	/// Persists the metadata item to the data store.  Verifies user has permission to edit item,
	/// throwing <see cref="GallerySecurityException" /> if authorization is denied. 
	/// The value is validated before saving, and may be altered to conform to business rules, 
	/// such as removing HTML tags and javascript. The <paramref name="metaItem" /> is returned,
	/// with the validated value assigned to the <see cref="MetaItemRest.Value" /> property.
	/// 
	/// The current implementation requires that an existing item exist in the data store and only 
	/// stores the contents of the <see cref="MetaItemRest.Value" /> property.
	/// </summary>
	/// <param name="metaItem">An instance of <see cref="MetaItemRest" /> to persist to the data
	/// store.</param>
	/// <returns>An instance of <see cref="MetaItemRest" />.</returns>
	/// <exception cref="InvalidContentObjectException">Thrown when the requested meta item or its
	/// associated content object does not exist in the data store.</exception>
	/// <exception cref="GallerySecurityException">Thrown when user does not have permission to
	/// edit the requested item.</exception>
	public static MetaItemRest save(MetaItemRest metaItem) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, GallerySecurityException, InvalidMDSRoleException, IOException, InvalidGalleryException	{
		ContentObjectMetadataItem md = CMUtils.loadContentObjectMetadataItem(metaItem.getId());
		if (md == null)
			throw new InvalidContentObjectException(MessageFormat.format("No metadata item with ID {0} could be found in the data store.", metaItem.getId()));

		// Security check: Make sure user has permission to edit item
		ContentObjectBo go;
		if (metaItem.MTypeId == ContentObjectType.Album.getValue())
		{
			go = CMUtils.loadAlbumInstance(metaItem.ContentId, false);
			SecurityGuard.throwIfUserNotAuthorized(SecurityActions.EditAlbum, RoleUtils.getMDSRolesForUser(), go.getId(), go.getGalleryId(), UserUtils.isAuthenticated(), go.getIsPrivate(), ((AlbumBo)go).getIsVirtualAlbum());
		}
		else
		{
			go = CMUtils.loadContentObjectInstance(metaItem.ContentId);
			SecurityGuard.throwIfUserNotAuthorized(SecurityActions.EditContentObject, RoleUtils.getMDSRolesForUser(), go.getParent().getId(), go.getGalleryId(), UserUtils.isAuthenticated(), go.getParent().getIsPrivate(), ((AlbumBo)go.getParent()).getIsVirtualAlbum());
		}

		String prevValue = md.getValue();

		md.setValue(HelperFunctions.cleanHtmlTags(metaItem.Value, go.getGalleryId()));

		if (md.getValue() != prevValue)	{
			CMUtils.saveContentObjectMetadataItem(md, UserUtils.getLoginName());

			HelperFunctions.purgeCache();
		}

		return metaItem;
	}

	/// <summary>
	/// Permanently deletes the meta item from the specified gallery items.
	/// </summary>
	/// <param name="contentItemTag">An instance of <see cref="ContentItemMeta" /> containing the 
	/// meta item to be deleted and the gallery items from which the item is to be deleted.</param>
	/// <exception cref="GallerySecurityException">Thrown when the current user does not have
	/// permission to edit one of the specified gallery items.</exception>
	public static void delete(ContentItemMeta contentItemTag) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, GallerySecurityException, InvalidMDSRoleException, InvalidContentObjectException, InvalidAlbumException, IOException, InvalidGalleryException{
		for (ContentItem gi : contentItemTag.getContentItems()){
			ContentObjectBo go;
			try{
				if (gi.ItemType == ContentObjectType.Album.getValue()){
					go = CMUtils.loadAlbumInstance(gi.getId(), false);
					SecurityGuard.throwIfUserNotAuthorized(SecurityActions.EditAlbum, RoleUtils.getMDSRolesForUser(), go.getId(), go.getGalleryId(), UserUtils.isAuthenticated(), go.getIsPrivate(), ((AlbumBo)go).getIsVirtualAlbum());
				}
				else
				{
					go = CMUtils.loadContentObjectInstance(gi.getId());
					SecurityGuard.throwIfUserNotAuthorized(SecurityActions.EditContentObject, RoleUtils.getMDSRolesForUser(), go.getParent().getId(), go.getGalleryId(), UserUtils.isAuthenticated(), go.getParent().getIsPrivate(), ((AlbumBo)go.getParent()).getIsVirtualAlbum());
				}
			}catch (InvalidAlbumException ex){
				AppEventLogUtils.LogError(ex);
				continue;
			}catch (InvalidContentObjectException ex){
				AppEventLogUtils.LogError(ex);
				continue;
			}

			ContentObjectMetadataItem md = go.getMetadataItems().tryGetMetadataItem(MetadataItemName.getMetadataItemName(contentItemTag.getMetaItem().MTypeId));

			if (md != null){
				md.setIsDeleted(true);

				CMUtils.saveContentObjectMetadataItem(md, UserUtils.getLoginName());
			}
		}

		HelperFunctions.purgeCache();
	}

	/// <summary>
	/// Updates the specified gallery items with the specified metadata value. The property <see cref="ContentItemMeta.ActionResult" />
	/// of <paramref name="contentItemMeta" /> is assigned when a validation error occurs, but remains null for a successful operation.
	/// </summary>
	/// <param name="contentItemMeta">An object containing the metadata instance to use as the source
	/// and the gallery items to be updated</param>
	/// <exception cref="GallerySecurityException">Thrown when the current user does not have
	/// permission to edit one or more of the specified gallery items.</exception>
	public static void saveContentItemMeta(ContentItemMeta contentItemMeta) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidAlbumException, UnsupportedImageTypeException, WebException, GallerySecurityException, InvalidMDSRoleException, IOException, InvalidGalleryException{
		MetadataItemName metaName = MetadataItemName.getMetadataItemName(contentItemMeta.getMetaItem().MTypeId);
		if (metaName == MetadataItemName.Tags || metaName == MetadataItemName.People){
			addTag(contentItemMeta);
		}else if (metaName == MetadataItemName.Rating){
			persistRating(contentItemMeta);
		}else if (metaName == MetadataItemName.ApproveStatus){
			persistApproval(contentItemMeta);
		}else{
			persistContentItemMeta(contentItemMeta);
		}
	}

	/// <summary>
	/// Gets a value indicating whether the logged-on user has edit permission for all of the <paramref name="contentItems" />.
	/// </summary>
	/// <param name="contentItems">A collection of <see cref="ContentItem" /> instances.</param>
	/// <returns><c>true</c> if the current user can edit the items; <c>false</c> otherwise.</returns>
	public static boolean canUserEditAllItems(Iterable<ContentItem> contentItems) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidMDSRoleException, InvalidGalleryException{
		try{
			for (ContentItem contentItem : contentItems){
				getContentObjectAndVerifyEditPermission(contentItem);
			}

			return true;
		}catch (GallerySecurityException ge){
			return false;
		}
	}

	/// <summary>
	/// Deletes the tags from the specified gallery items. This method is intended only for tag-style
	/// metadata items, such as descriptive tags and people. It is assumed the metadata item in
	/// the data store is a comma-separated list of tags, and the passed in to this method is to 
	/// be removed from it. No action is taken on a content object if the tag already exists or the
	/// specified content object does not exist.
	/// </summary>
	/// <param name="contentItemTag">An instance of <see cref="ContentItemMeta" /> containing the tag
	/// and the gallery items the tag is to be removed from.</param>
	/// <exception cref="GallerySecurityException">Thrown when the current user does not have
	/// permission to edit one of the specified gallery items.</exception>
	public static void deleteTag(ContentItemMeta contentItemTag) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidAlbumException, UnsupportedImageTypeException, IOException, GallerySecurityException, InvalidMDSRoleException, InvalidGalleryException{
		for (ContentItem gi : contentItemTag.getContentItems()){
			ContentObjectBo go;
			try{
				if (gi.ItemType == ContentObjectType.Album.getValue()){
					go = CMUtils.loadAlbumInstance(gi.getId(), false);
					SecurityGuard.throwIfUserNotAuthorized(SecurityActions.EditAlbum, RoleUtils.getMDSRolesForUser(), go.getId(), go.getGalleryId(), UserUtils.isAuthenticated(), go.getIsPrivate(), ((AlbumBo)go).getIsVirtualAlbum());
				}else{
					go = CMUtils.loadContentObjectInstance(gi.getId());
					SecurityGuard.throwIfUserNotAuthorized(SecurityActions.EditContentObject, RoleUtils.getMDSRolesForUser(), go.getParent().getId(), go.getGalleryId(), UserUtils.isAuthenticated(), go.getParent().getIsPrivate(), ((AlbumBo)go.getParent()).getIsVirtualAlbum());
				}
			}catch (InvalidAlbumException ex){
				AppEventLogUtils.LogError(ex);
				continue;
			}catch (InvalidContentObjectException ex){
				AppEventLogUtils.LogError(ex);
				continue;
			}

			ContentObjectMetadataItem md = go.getMetadataItems().tryGetMetadataItem(MetadataItemName.getMetadataItemName(contentItemTag.getMetaItem().MTypeId));

			// Split tag into array, add it if it's not already there, and save
			List<String> tags = Arrays.asList(StringUtils.split(md.getValue(), "," ));
			if (tags.stream().anyMatch(t->t.equalsIgnoreCase(contentItemTag.getMetaItem().Value))){
				tags.remove(contentItemTag.getMetaItem().Value);

				md.setValue(String.join(", ", tags));

				CMUtils.saveContentObjectMetadataItem(md, UserUtils.getLoginName());
			}
		}

		HelperFunctions.purgeCache();
	}

	/// <summary>
	/// Rebuilds the <paramref name="metaName" /> for all items in the gallery having ID <paramref name="galleryId" />.
	/// The action is executed asynchronously and returns immediately.
	/// </summary>
	/// <param name="metaName">Name of the meta item.</param>
	/// <param name="galleryId">The gallery ID.</param>
	public static void rebuildItemForGalleryAsync(MetadataItemName metaName, long galleryId) throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException{
		AlbumBo album = CMUtils.loadRootAlbumInstance(galleryId);
		MetadataDefinitionCollection metaDefs = CMUtils.loadGallerySetting(galleryId).getMetadataDisplaySettings();
		String userName = UserUtils.getLoginName();

		//Task.CMUtils.startNew(() => StartRebuildMetaItem(metaDefs.Find(metaName), album, userName), TaskCreationOptions.LongRunning);
		runAsync(new Runnable() {
	           public void run() {
	        	   try {
					startRebuildMetaItem(metaDefs.find(metaName), album, userName);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			   }
		});
	}

	/// <summary>
	/// Gets a list of tags or people corresponding to the specified parameters.
	/// Guaranteed to not return null.
	/// </summary>
	/// <param name="tagSearchType">Type of the search.</param>
	/// <param name="searchTerm">The search term. Only tags that begin with this String are returned.
	/// Specify null or an empty String to return all tags.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <param name="top">The number of tags to return. Values less than zero are treated the same as zero,
	/// meaning no tags will be returned. Specify <see cref="Integer.MIN_VALUE" /> to return all tags.</param>
	/// <param name="sortBy">The property to sort the tags by. Specify <see cref="TagSearchOptions.TagProperty.Count" />
	/// to sort by tag frequency or <see cref="TagSearchOptions.TagProperty.Value" /> to sort by tag name. 
	/// When not specified, defaults to <see cref="TagSearchOptions.TagProperty.NotSpecified" />.</param>
	/// <param name="sortAscending">Specifies whether to sort the tags in ascending order. Specify <c>true</c>
	/// for ascending order or <c>false</c> for descending order. When not specified, defaults to <c>false</c>.</param>
	/// <returns>Iterable{Tag}.</returns>
	public static Iterable<TagRest> getTags(TagSearchType tagSearchType, String searchTerm, long galleryId) throws InvalidMDSRoleException{
		return getTags(tagSearchType, searchTerm, galleryId, Integer.MIN_VALUE);
	}
	
	public static Iterable<TagRest> getTags(TagSearchType tagSearchType, String searchTerm, long galleryId, int top) throws InvalidMDSRoleException{
		return getTags(tagSearchType, searchTerm, galleryId, top, TagSearchOptions.TagProperty.NotSpecified);
	}
	
	public static Iterable<TagRest> getTags(TagSearchType tagSearchType, String searchTerm, long galleryId, int top, TagSearchOptions.TagProperty sortBy) throws InvalidMDSRoleException{
		return getTags(tagSearchType, searchTerm, galleryId, top, sortBy, false);
	}
	
	public static Iterable<TagRest> getTags(TagSearchType tagSearchType, String searchTerm, long galleryId, int top, TagSearchOptions.TagProperty sortBy, boolean sortAscending) throws InvalidMDSRoleException{
		return getTags(getTagSearchOptions(tagSearchType, searchTerm, galleryId, top, sortBy, sortAscending));
	}

	/// <summary>
	/// Gets a list of tags or people corresponding to the specified <paramref name="searchOptions" />.
	/// Guaranteed to not return null.
	/// </summary>
	/// <param name="searchOptions">The search options.</param>
	/// <returns>Iterable{Tag}.</returns>
	private static Iterable<TagRest> getTags(TagSearchOptions searchOptions){
		/*var searcher = new TagSearcher(searchOptions);

		return searcher.Find();*/
		return Lists.newArrayList();
	}

	/// <summary>
	/// Gets a JSON String representing the tags used in the specified gallery. The JSON can be used as the
	/// data source for the jsTree jQuery widget. Only tags the current user has permission to view are
	/// included. The tag tree has a root node containing a single level of tags.
	/// </summary>
	/// <param name="tagSearchType">Type of search.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <param name="top">The number of tags to return. Values less than zero are treated the same as zero,
	/// meaning no tags will be returned. Specify <see cref="Integer.MIN_VALUE" /> to return all tags.</param>
	/// <param name="sortBy">The property to sort the tags by. Specify <see cref="TagSearchOptions.TagProperty.Count" />
	/// to sort by tag frequency or <see cref="TagSearchOptions.TagProperty.Value" /> to sort by tag name. 
	/// When not specified, defaults to <see cref="TagSearchOptions.TagProperty.Count" />.</param>
	/// <param name="sortAscending">Specifies whether to sort the tags in ascending order. Specify <c>true</c>
	/// for ascending order or <c>false</c> for descending order. When not specified, defaults to <c>false</c>.</param>
	/// <param name="expanded">if set to <c>true</c> the tree is configured to display in an expanded form.</param>
	/// <returns>System.String.</returns>
	public static String getTagTreeAsJson(HttpServletRequest request, TagSearchType tagSearchType, long galleryId) throws InvalidMDSRoleException {
		return getTagTreeAsJson(request, tagSearchType, galleryId, Integer.MIN_VALUE);
	}
	
	public static String getTagTreeAsJson(HttpServletRequest request, TagSearchType tagSearchType, long galleryId, int top) throws InvalidMDSRoleException {
		return getTagTreeAsJson(request, tagSearchType, galleryId, top, TagSearchOptions.TagProperty.Count);
	}
	
	public static String getTagTreeAsJson(HttpServletRequest request, TagSearchType tagSearchType, long galleryId, int top, TagSearchOptions.TagProperty sortBy) throws InvalidMDSRoleException {
		return getTagTreeAsJson(request, tagSearchType, galleryId, top, sortBy, false);
	}
	
	public static String getTagTreeAsJson(HttpServletRequest request, TagSearchType tagSearchType, long galleryId, int top, TagSearchOptions.TagProperty sortBy, boolean sortAscending) throws InvalidMDSRoleException {
		return getTagTreeAsJson(request, tagSearchType, galleryId, top, sortBy, sortAscending, false);
	}
	
	public static String getTagTreeAsJson(HttpServletRequest request, TagSearchType tagSearchType, long galleryId, int top, TagSearchOptions.TagProperty sortBy, boolean sortAscending, boolean expanded) throws InvalidMDSRoleException	{
		TagSearchOptions tagSearchOptions = getTagSearchOptions(tagSearchType, null, galleryId, top, sortBy, sortAscending, expanded);

		return JsonMapper.getInstance().toJson(getTagTree(tagSearchOptions, request));
	}

	//#endregion

	//#region Functions

	/// <summary>
	/// Generate a collection of all tag values that exist associated with the specified
	/// <paramref name="contentItems" /> having the specified <paramref name="tagName" />.
	/// Individual tag values will be repeated when they belong to multiple gallery items.
	/// </summary>
	/// <param name="contentItems">The gallery items.</param>
	/// <param name="tagName">Name of the tag.</param>
	/// <returns>Returns a collection of Strings.</returns>
	/// <exception cref="InvalidAlbumException">Thrown when the requested album does not exist.</exception>
	/// <exception cref="InvalidContentObjectException">Thrown when the requested content object does not exist.</exception>
	private static Collection<String> getTagListForContentItems(Collection<ContentItem> contentItems, MetadataItemName tagName) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException	{
		List<String> tagList = new ArrayList<String>();
		for (ContentItem contentItem : contentItems){
			ContentObjectMetadataItemCollection metas = getContentObjectMetadataItemCollection(contentItem);
			tagList.addAll(getTagList(metas, tagName));
		}
		return tagList;
	}

	/// <summary>
	/// Gets the collection of tag values having the specified <paramref name="tagName" />.
	/// </summary>
	/// <param name="metas">The metadata items.</param>
	/// <param name="tagName">Name of the tag.</param>
	/// <returns>Returns a collection of Strings.</returns>
	private static Collection<String> getTagList(ContentObjectMetadataItemCollection metas, MetadataItemName tagName)	{
		ContentObjectMetadataItem mdTag;
		if (( mdTag = metas.tryGetMetadataItem(tagName)) != null){
			return HelperFunctions.toListFromCommaDelimited(mdTag.getValue());
		}else
			return Lists.newArrayList();
	}

	/// <overloads>
	/// Gets the metadata collection for the specified criteria. Guaranteed to not return null.
	/// </overloads>
	/// <summary>
	/// Gets the metadata collection for the specified <paramref name="contentItem" />.
	/// </summary>
	/// <param name="contentItem">The gallery item representing either an album or a content object.</param>
	/// <returns>Returns an instance of <see cref="ContentObjectMetadataItemCollection" />.</returns>
	/// <exception cref="InvalidAlbumException">Thrown when the requested album does not exist.</exception>
	/// <exception cref="InvalidContentObjectException">Thrown when the requested content object does not exist.</exception>
	private static ContentObjectMetadataItemCollection getContentObjectMetadataItemCollection(ContentItem contentItem) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException{
		return getContentObjectMetadataItemCollection(contentItem.getId(), ContentObjectType.getContentObjectType(contentItem.ItemType));
	}

	/// <summary>
	/// Gets the metadata collection for the specified <paramref name="contentObjectId" /> and
	/// <paramref name="goType" />.
	/// </summary>
	/// <param name="contentObjectId">The ID for either an album or a content object.</param>
	/// <param name="goType">The type of content object.</param>
	/// <returns>Returns an instance of <see cref="ContentObjectMetadataItemCollection" />.</returns>
	/// <exception cref="InvalidAlbumException">Thrown when the requested album does not exist.</exception>
	/// <exception cref="InvalidContentObjectException">Thrown when the requested content object does not exist.</exception>
	private static ContentObjectMetadataItemCollection getContentObjectMetadataItemCollection(long contentObjectId, ContentObjectType goType) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException{
		if (goType == ContentObjectType.Album)
			return CMUtils.loadAlbumInstance(contentObjectId, false).getMetadataItems();
		else
			return CMUtils.loadContentObjectInstance(contentObjectId).getMetadataItems();
	}

	/// <summary>
	/// Process the <paramref name="tags" /> and return a comma-delimited String containing the
	/// tag values and their counts. Ex: "Animal (3), Dog (2), Cat (1)"
	/// </summary>
	/// <param name="tags">The tags to process.</param>
	/// <returns>Returns a String.</returns>
	private static String getTagsWithCount(Collection<String> tags){
		// Group the tags by their value and build up a unique list containing the value and their
		// count in parenthesis.
		List<String> tagsGrouped = new ArrayList<String>();
		Map<String, Long> result =
				tags.stream().collect(
                        Collectors.groupingBy(
                                Function.identity(), Collectors.counting()
                        )
                );

        Map<String, Long> finalMap = new LinkedHashMap<>();

        //Sort a map and add to finalMap
        result.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue()) //.reversed()
                .forEachOrdered(e -> finalMap.put(e.getKey(), e.getValue()));

		for (Map.Entry<String, Long> item : finalMap.entrySet()){
			tagsGrouped.add(MessageFormat.format("{0} ({1})", item.getKey(), item.getValue()));
		}

		return String.join(", ", tagsGrouped);
	}

	private static MetaItemRest[] getMetaItems(ContentItem lastGi, HttpServletRequest request) throws UnsupportedContentObjectTypeException, GallerySecurityException, InvalidAlbumException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException{
		MetaItemRest[] meta;
		if (lastGi.ItemType == ContentObjectType.Album.getValue())
			meta = AlbumUtils.getMetaItemsForAlbum(lastGi.getId(), request);
		else
			meta = ContentObjectUtils.getMetaItemsForContentObject(lastGi.getId(), request);
		return meta;
	}

	private static MetaItemRest getMetaItemForTag(MetaItemRest[] meta, MetadataItemName tagName, ContentItem contentItem) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException{
		MetaItemRest tagMi = Arrays.stream(meta).filter(m -> m.MTypeId == tagName.value()).findFirst().orElse(null);
		if (tagMi != null){
			return tagMi;
		}else{
			// Last item doesn't have a tag. Create one. This code path should be pretty rare.
			long galleryId;
			if (contentItem.IsAlbum)
				galleryId = AlbumUtils.loadAlbumInstance(contentItem.getId(), false).getGalleryId();
			else
				galleryId = CMUtils.loadContentObjectInstance(contentItem.getId()).getGalleryId();

			boolean isEditable = CMUtils.loadGallerySetting(galleryId).getMetadataDisplaySettings().find(tagName).IsEditable;

			tagMi = new MetaItemRest(
					Long.MIN_VALUE,
					contentItem.getId(),
					tagName,
					ContentObjectType.getContentObjectType(contentItem.ItemType),
					tagName.toString(),
					StringUtils.EMPTY,
					isEditable
				);

			meta = (MetaItemRest[]) ArrayUtils.add(meta, tagMi);

			return tagMi;
		}
	}

	/// <summary>
	/// Adds the tag to the specified gallery items. This method is intended only for tag-style
	/// metadata items, such as descriptive tags and people. It is assumed the metadata item in
	/// the data store is a comma-separated list of tags, and the value passed in to this method is to 
	/// be added to it. No action is taken on a content object if the tag already exists or the
	/// specified content object does not exist.
	/// </summary>
	/// <param name="contentItemTag">An instance of <see cref="ContentItemMeta" /> that defines
	/// the tag value to be added and the gallery items it is to be added to.</param>
	/// <exception cref="GallerySecurityException">Thrown when the current user does not have
	/// permission to edit one or more of the specified gallery items.</exception>
	/// <exception cref="WebException">Thrown when the metadata instance is not a tag-style item.</exception>
	private static void addTag(ContentItemMeta contentItemTag) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, GallerySecurityException, InvalidMDSRoleException, InvalidContentObjectException, InvalidAlbumException, IOException, WebException, InvalidGalleryException	{
		MetadataItemName metaName = MetadataItemName.getMetadataItemName(contentItemTag.getMetaItem().MTypeId);

		if (metaName != MetadataItemName.Tags && metaName != MetadataItemName.People)
			throw new WebException(MessageFormat.format("The AddTag function is designed to persist tag-style metadata items. The item that was passed ({0}) does not qualify.", metaName.toString()));

		for (ContentItem contentItem : contentItemTag.getContentItems())	{
			ContentObjectBo contentObject = getContentObjectAndVerifyEditPermission(contentItem);
			if (contentObject == null)
				continue;

			ContentObjectMetadataItem md;
			if ((md = contentObject.getMetadataItems().tryGetMetadataItem(MetadataItemName.getMetadataItemName(contentItemTag.getMetaItem().MTypeId))) != null){
				// Split tag into array, add it if it's not already there, and save
				List<String> tags = Lists.newArrayList(StringUtils.split(md.getValue(), "," ));
				if (!tags.stream().anyMatch(t->t.equalsIgnoreCase(contentItemTag.getMetaItem().Value))){
					tags.add(HelperFunctions.cleanHtmlTags(contentItemTag.getMetaItem().Value, contentObject.getGalleryId()));

					md.setValue(String.join(", ", tags));

					CMUtils.saveContentObjectMetadataItem(md, UserUtils.getLoginName());
				}
			}
		}

		HelperFunctions.purgeCache();
	}

	/// <summary>
	/// Updates the gallery items with the specified metadata value. The property <see cref="ContentItemMeta.ActionResult" />
	/// of <paramref name="contentItemMeta" /> is assigned when a validation error occurs, but remains null for a successful operation.
	/// </summary>
	/// <param name="contentItemMeta">An object containing the metadata instance to use as the source
	/// and the gallery items to be updated.</param>
	/// <exception cref="GallerySecurityException">Thrown when the current user does not have
	/// permission to edit one or more of the specified gallery items.</exception>
	/// <exception cref="WebException">Thrown when the metadata instance is a tag-style item.</exception>
	private static void persistContentItemMeta(ContentItemMeta contentItemMeta) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidAlbumException, UnsupportedImageTypeException, WebException, GallerySecurityException, InvalidMDSRoleException, IOException, InvalidGalleryException{
		MetadataItemName metaName = MetadataItemName.getMetadataItemName(contentItemMeta.getMetaItem().MTypeId);

		if (metaName == MetadataItemName.Tags || metaName == MetadataItemName.People)
			throw new WebException("The PersistContentItemMeta function is not designed to persist tag-style metadata items.");

		for (ContentItem contentItem : contentItemMeta.getContentItems()){
			ContentObjectBo contentObject = getContentObjectAndVerifyEditPermission(contentItem);
			if (contentObject == null)
				continue;

			if (contentItemMeta.getMetaItem().MTypeId == MetadataItemName.Title.value() && StringUtils.isBlank(contentItemMeta.getMetaItem().Value) 
					&& Arrays.asList(contentItemMeta.getContentItems()).stream().anyMatch(g -> g.IsAlbum)){
				contentItemMeta.setActionResult(new ActionResult(
													ActionResultStatus.Error.toString(),
													"Cannot save changes",
													"An album title cannot be set to a blank String.",
													null
												));
				return;
			}

			ContentObjectMetadataItem metaItem;
			if ((metaItem = contentObject.getMetadataItems().tryGetMetadataItem(metaName)) != null && metaItem.getIsEditable()){
				metaItem.setValue(HelperFunctions.cleanHtmlTags(contentItemMeta.getMetaItem().Value, contentObject.getGalleryId()));
				CMUtils.saveContentObjectMetadataItem(metaItem, UserUtils.getLoginName());
			}else{
				// Get a writeable instance of the content object and create new metadata instance.
				if (contentItem.ItemType == ContentObjectType.Album.getValue())
					contentObject = CMUtils.loadAlbumInstance(contentItem.getId(), false, true);
				else
					contentObject = CMUtils.loadContentObjectInstance(contentItem.getId(), true);

				// Add the new metadata item.
				MetadataDefinition metaDef = CMUtils.loadGallerySetting(contentObject.getGalleryId()).getMetadataDisplaySettings().find(metaName);

				if (metaDef.IsEditable && contentObject.metadataDefinitionApplies(metaDef)){
					ContentObjectMetadataItemCollection metaItems = CMUtils.createMetadataCollection();
					metaItems.add(CMUtils.createMetadataItem(Long.MIN_VALUE, contentObject, null, contentItemMeta.getMetaItem().Value, true, metaDef));
					contentObject.addMeta(metaItems);

					ContentObjectUtils.saveContentObject(contentObject);
				}
			}
		}

		HelperFunctions.purgeCache();
	}

	/// <summary>
	/// Gets the content object for the specified <paramref name="contentItem" /> and verifies current
	/// user has edit permission, throwing a <see cref="GallerySecurityException" /> if needed. Returns
	/// null if no content object or album having the requested ID exists.
	/// </summary>
	/// <param name="contentItem">The gallery item.</param>
	/// <returns>An instance of <see cref="ContentObjectBo" /> corresponding to <paramref name="contentItem" />.</returns>
	/// <exception cref="GallerySecurityException">Thrown when the current user does not have
	/// permission to edit the specified gallery items.</exception>
	private static ContentObjectBo getContentObjectAndVerifyEditPermission(ContentItem contentItem) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, GallerySecurityException, InvalidMDSRoleException, InvalidGalleryException	{
		ContentObjectBo contentObject = null;
		try{
			if (contentItem.ItemType == ContentObjectType.Album.getValue()){
				contentObject = CMUtils.loadAlbumInstance(contentItem.getId(), false);
				SecurityGuard.throwIfUserNotAuthorized(SecurityActions.EditAlbum, RoleUtils.getMDSRolesForUser(), contentObject.getId(), contentObject.getGalleryId(), UserUtils.isAuthenticated(), contentObject.getIsPrivate(), ((AlbumBo)contentObject).getIsVirtualAlbum());
			}else{
				contentObject = CMUtils.loadContentObjectInstance(contentItem.getId());
				SecurityGuard.throwIfUserNotAuthorized(SecurityActions.EditContentObject, RoleUtils.getMDSRolesForUser(), contentObject.getParent().getId(), contentObject.getGalleryId(), UserUtils.isAuthenticated(), contentObject.getParent().getIsPrivate(), ((AlbumBo)contentObject.getParent()).getIsVirtualAlbum());
			}
		}catch (InvalidAlbumException ex){
			AppEventLogUtils.LogError(ex);
		}catch (InvalidContentObjectException ex){
			AppEventLogUtils.LogError(ex);
		}

		return contentObject;
	}

	/// <summary>
	/// Gets the read-only content object for the specified <paramref name="contentItem" /> and verifies current
	/// user has the ability to edit its rating, throwing a <see cref="GallerySecurityException" /> if needed. Returns
	/// null if no content object or album having the requested ID exists.
	/// </summary>
	/// <param name="contentItem">The gallery item.</param>
	/// <returns>An instance of <see cref="ContentObjectBo" /> corresponding to <paramref name="contentItem" />.</returns>
	/// <exception cref="GallerySecurityException">Thrown when the current user does not have
	/// permission to edit the specified gallery items.</exception>
	/// <remarks>Editing a rating works a little different than other metadata: Anonymous users are allowed
	/// to apply a rating as long as <see cref="GallerySettings.AllowAnonymousRating" /> is <c>true</c> and all
	/// logged on users are allowed to rate an item.</remarks>
	private static ContentObjectBo getContentObjectAndVerifyEditRatingPermission(ContentItem contentItem) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, GallerySecurityException, InvalidMDSRoleException, InvalidGalleryException{
		ContentObjectBo contentObject = null;
		try{
			if (contentItem.ItemType == ContentObjectType.Album.getValue())	{
				contentObject = CMUtils.loadAlbumInstance(contentItem.getId(), false);
				SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), contentObject.getId(), contentObject.getGalleryId(), UserUtils.isAuthenticated(), contentObject.getIsPrivate(), ((AlbumBo)contentObject).getIsVirtualAlbum());
			}else{
				contentObject = CMUtils.loadContentObjectInstance(contentItem.getId());
				SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), contentObject.getParent().getId(), contentObject.getGalleryId(), UserUtils.isAuthenticated(), contentObject.getParent().getIsPrivate(), ((AlbumBo)contentObject.getParent()).getIsVirtualAlbum());
			}

			if (!UserUtils.isAuthenticated() && !CMUtils.loadGallerySetting(contentObject.getGalleryId()).getAllowAnonymousRating())	{
				// We have an anonymous user attempting to rate an item, but the AllowAnonymousRating setting is false.
				contentObject = null;
				throw new GallerySecurityException(MessageFormat.format("An anonymous user is attempting to rate a content object ({0} ID {1}), but the gallery is configured to not allow ratings by anonymous users. The request is denied.", ContentObjectType.getContentObjectType(contentItem.ItemType), contentObject.getId()));
			}
		}catch (InvalidAlbumException ex){
			AppEventLogUtils.LogError(ex);
		}catch (InvalidContentObjectException ex){
			AppEventLogUtils.LogError(ex);
		}

		return contentObject;
	}

	/// <summary>
	/// Gets the read-only content object for the specified <paramref name="contentItem" /> and verifies current
	/// user has the ability to edit its rating, throwing a <see cref="GallerySecurityException" /> if needed. Returns
	/// null if no content object or album having the requested ID exists.
	/// </summary>
	/// <param name="contentItem">The gallery item.</param>
	/// <returns>An instance of <see cref="ContentObjectBo" /> corresponding to <paramref name="contentItem" />.</returns>
	/// <exception cref="GallerySecurityException">Thrown when the current user does not have
	/// permission to edit the specified gallery items.</exception>
	/// <remarks>Editing a rating works a little different than other metadata: Anonymous users are allowed
	/// to apply a rating as long as <see cref="GallerySettings.AllowAnonymousRating" /> is <c>true</c> and all
	/// logged on users are allowed to rate an item.</remarks>
	private static ContentObjectBo getContentObjectAndVerifyApprovalPermission(ContentItem contentItem) throws GallerySecurityException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidMDSRoleException, InvalidGalleryException{
		ContentObjectBo contentObject = null;
		try{
			if (contentItem.ItemType == ContentObjectType.Album.getValue()) {
				contentObject = CMUtils.loadAlbumInstance(contentItem.getId(), false);
				SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ApproveContentObject, RoleUtils.getMDSRolesForUser(), contentObject.getId(), contentObject.getGalleryId(), UserUtils.isAuthenticated(), contentObject.getIsPrivate(), ((AlbumBo)contentObject).getIsVirtualAlbum());
			}else{
				contentObject = CMUtils.loadContentObjectInstance(contentItem.getId());
				SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ApproveContentObject, RoleUtils.getMDSRolesForUser(), contentObject.getParent().getId(), contentObject.getGalleryId(), UserUtils.isAuthenticated(), contentObject.getParent().getIsPrivate(), ((AlbumBo)contentObject.getParent()).getIsVirtualAlbum());
			}

			if (!UserUtils.isAuthenticated()){
				// anonymous user can't approve an item.
				contentObject = null;
				throw new GallerySecurityException(MessageFormat.format("An anonymous user can't approve a content object ({0} ID {1}), The request is denied.", ContentObjectType.getContentObjectType(contentItem.ItemType), contentObject.getId()));
			}
		}catch (InvalidAlbumException ex){
			AppEventLogUtils.LogError(ex);
		}catch (InvalidContentObjectException ex){
			AppEventLogUtils.LogError(ex);
		}

		return contentObject;
	}

	private static void startRebuildMetaItem(MetadataDefinition metaDef, ContentObjectBo contentObject, String userName) throws Exception{
		try{
			AppEventLogUtils.LogEvent(MessageFormat.format("INFO: Starting to rebuild metadata item '{0}' for all objects in gallery {1}.", metaDef.MetadataItem, contentObject.getGalleryId()), contentObject.getGalleryId());

			rebuildMetaItem(metaDef, contentObject, userName);

			HelperFunctions.purgeCache();

			AppEventLogUtils.LogEvent(MessageFormat.format("INFO: Successfully finished rebuilding metadata item '{0}' for all objects in gallery {1}.", metaDef.MetadataItem, contentObject.getGalleryId()), contentObject.getGalleryId());
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex, contentObject.getGalleryId());
			AppEventLogUtils.LogEvent(MessageFormat.format("CANCELLED: The rebuilding of metadata item '{0}' for all objects in gallery {1} has been cancelled due to the previously logged error.", metaDef.MetadataItem, contentObject.getGalleryId()), contentObject.getGalleryId());
			throw ex;
		}
	}

	private static void rebuildMetaItem(MetadataDefinition metaDef, ContentObjectBo contentObject, String userName) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidAlbumException, UnsupportedImageTypeException, IOException, InvalidGalleryException{
		contentObject.extractMetadata(metaDef);

		ContentObjectMetadataItem metaItem;
		if ((metaItem = contentObject.getMetadataItems().tryGetMetadataItem(metaDef.getMetadataItemName())) != null)	{
			CMUtils.saveContentObjectMetadataItem(metaItem, userName);
		}

		if (contentObject.getContentObjectType() == ContentObjectType.Album){
			ContentObjectBoCollection gos = contentObject.getChildContentObjects();
			for (ContentObjectBo go : gos.values() ){
				rebuildMetaItem(metaDef, go, userName);
			}
		}
	}

	/// <summary>
	/// Apply a user's rating to the gallery items specified in <paramref name="contentItemMeta" /> and persist to the
	/// data store. A record of the user's rating is stored in their profile. If rating is not a number, then no action
	/// is taken. If rating is less than 0 or greater than 5, it is assigned to 0 or 5 so that the value is guaranteed
	/// to be between those values.
	/// </summary>
	/// <param name="contentItemMeta">An instance containing the rating metadata item and the content objects to which
	/// it applies.</param>
	/// <exception cref="WebException">Thrown when the metadata item is not <see cref="MetadataItemName.ApproveStatus" />.</exception>
	private static void persistApproval(ContentItemMeta contentItemMeta) throws WebException, UnsupportedContentObjectTypeException, GallerySecurityException, UnsupportedImageTypeException, InvalidMDSRoleException, InvalidGalleryException{
		MetadataItemName metaName = MetadataItemName.getMetadataItemName(contentItemMeta.getMetaItem().MTypeId);

		if (metaName != MetadataItemName.ApproveStatus)
			throw new WebException(MessageFormat.format("The PersistApproval function is designed to store 'Approve Status' metadata items, but {0} was passed.", metaName));

		Date dtApproval = DateUtils.Now();
		String strApprovalDate = "";
		for (ContentItem contentItem : contentItemMeta.getContentItems()){
			ContentObjectBo contentObject = getContentObjectAndVerifyApprovalPermission(contentItem);
			if (contentObject == null)
				continue;

			short nStatus = StringUtils.toShort(contentItemMeta.getMetaItem().Value);
			
			Pair<ContentObjectApproval, ApprovalStatus>  approvalResult = persistApprovalInHistory(contentObject, ApprovalStatus.getApprovalStatus(nStatus), dtApproval);
			if (approvalResult.getLeft() != null && approvalResult.getRight() != ApprovalStatus.NotSpecified)
			{
				ContentObjectMetadataItem approvalItem;
				if ((approvalItem = contentObject.getMetadataItems().tryGetMetadataItem(metaName)) != null)	{
					approvalItem.setValue(contentItemMeta.getMetaItem().Value);
				}else{
					// No approval item found for this gallery item. Create one (if business rules allow).
					MetadataDefinition metaDefApproval = CMUtils.loadGallerySetting(contentObject.getGalleryId()).getMetadataDisplaySettings().find(metaName);

					if (contentObject.metadataDefinitionApplies(metaDefApproval)){
						ContentObjectMetadataItemCollection metaItems = CMUtils.createMetadataCollection();
						ContentObjectMetadataItem newApprovalItem = CMUtils.createMetadataItem(Long.MIN_VALUE, contentObject, null, contentItemMeta.getMetaItem().Value, true, metaDefApproval);
						metaItems.add(newApprovalItem);
						contentObject.addMeta(metaItems);
					}
				}

				if ((approvalItem = contentObject.getMetadataItems().tryGetMetadataItem(MetadataItemName.Approval)) != null){
					approvalItem.setValue(approvalResult.getLeft().getApproveBy());
				}else{
					// No approval item found for this gallery item. Create one (if business rules allow).
					MetadataDefinition metaDefApproval = CMUtils.loadGallerySetting(contentObject.getGalleryId()).getMetadataDisplaySettings().find(MetadataItemName.Approval);

					if (contentObject.metadataDefinitionApplies(metaDefApproval)){
						ContentObjectMetadataItemCollection metaItems = CMUtils.createMetadataCollection();
						ContentObjectMetadataItem newApprovalItem = CMUtils.createMetadataItem(Long.MIN_VALUE, contentObject, null, approvalResult.getLeft().getApproveBy(), true, metaDefApproval);
						metaItems.add(newApprovalItem);
						contentObject.addMeta(metaItems);
					}
				}

				strApprovalDate = DateUtils.formatDate(approvalResult.getLeft().getApproveDate(), CMUtils.loadGallerySetting(contentObject.getGalleryId()).getMetadataDateTimeFormatString());
				if ((approvalItem = contentObject.getMetadataItems().tryGetMetadataItem(MetadataItemName.ApprovalDate)) != null){
					approvalItem.setValue(strApprovalDate);
				}else{
					// No approval item found for this gallery item. Create one (if business rules allow).
					MetadataDefinition metaDefApproval = CMUtils.loadGallerySetting(contentObject.getGalleryId()).getMetadataDisplaySettings().find(MetadataItemName.ApprovalDate);

					if (contentObject.metadataDefinitionApplies(metaDefApproval)){
						ContentObjectMetadataItemCollection metaItems = CMUtils.createMetadataCollection();
						ContentObjectMetadataItem newApprovalItem = CMUtils.createMetadataItem(Long.MIN_VALUE, contentObject, null, strApprovalDate, true, metaDefApproval);
						metaItems.add(newApprovalItem);
						contentObject.addMeta(metaItems);
					}
				}
				contentObject.approvalFileAction(approvalResult.getLeft());
			}else if (approvalResult.getLeft() != null){
				strApprovalDate = DateUtils.formatDate(approvalResult.getLeft().getApproveDate(), CMUtils.loadGallerySetting(contentObject.getGalleryId()).getMetadataDateTimeFormatString());
			}
/*#if false
			ContentObjectMetadataItem approvalItem;
			if (contentObject.MetadataItems.TryGetMetadataItem(metaName, out approvalItem))
			{
				if (approvalItem.IsEditable)
				{
					approvalItem.Value = contentItemMeta.MetaItem.Value;
					CMUtils.saveContentObjectMetadataItem(approvalItem, UserUtils.getLoginName());

					short nStatus = 0;
					short.TryParse(contentItemMeta.MetaItem.Value, out nStatus);
					PersistApprovalInHistory(contentObject, nStatus);
				}
			}
			else
			{
				// No approval item found for this gallery item. Create one (if business rules allow).
				var metaDefApproval = CMUtils.loadGallerySetting(contentObject.getGalleryId()).getMetadataDisplaySettings().Find(metaName);

				if (metaDefApproval.IsEditable && contentObject.MetadataDefinitionApplies(metaDefApproval))
				{
					var metaItems = CMUtils.createMetadataCollection();
					var newApprovalItem = CMUtils.createMetadataItem(Integer.MIN_VALUE, contentObject, null, contentItemMeta.MetaItem.Value, true, metaDefApproval);
					metaItems.add(newApprovalItem);
					contentObject.AddMeta(metaItems);

					CMUtils.saveContentObjectMetadataItem(newApprovalItem, UserUtils.getLoginName());
					//CMUtils.saveApprovalStatusItem();.SaveContentObjectMetadataItem(ratingCountItem, UserUtils.getLoginName());
					short nStatus = 0;
					short.TryParse(contentItemMeta.MetaItem.Value, out nStatus);
					PersistApprovalInHistory(contentObject, nStatus);
				}
			}
#endif*/
		}

		contentItemMeta.getMetaItem().Value += ";";
		contentItemMeta.getMetaItem().Value += UserUtils.getLoginName();
		contentItemMeta.getMetaItem().Value += ";";
		contentItemMeta.getMetaItem().Value += strApprovalDate;

		HelperFunctions.purgeCache();
	}

	/// <summary>
	/// Persists the user's rating in their user profile.
	/// </summary>
	/// <param name="contentObjectId">The content object ID of the item being rated.</param>
	/// <param name="userRating">The user rating (e.g. "2.8374").</param>
	private static Pair<ContentObjectApproval, ApprovalStatus> persistApprovalInHistory(ContentObjectBo contentObject, ApprovalStatus approvalStatus, Date dtApproval){
		ApprovalStatus approvalResult = approvalStatus;

		ContentObjectApproval approval = null;
		/*if ((approval = contentObject.getApprovalItems().tryGetApprovalItem(UserUtils.getLoginName())) != null){
			if (approval.getApprovalAction() != ApprovalAction.Approve)
			{
				approvalResult = ApprovalStatus.NotSpecified;
			}else{
				ApprovalStatus approvalStatusOrg = approval.ApprovalStatus;
				if (approval.ApproveBy.CompareTo(UserUtils.getLoginName()) == 0)
				{
					approval.ApprovalStatus = approvalStatus;
					approval.ApproveBy = UserUtils.getLoginName();
					approval.strUserCode = UserUtils.getLoginName();
					approval.dtLastModify = dtApproval;
					CMUtils.saveApprovalStatusItem(approval);
				}else{
					approval = CMUtils.createApprovalItem(Integer.MIN_VALUE, contentObject, UserUtils.getLoginName(), 1, approvalStatus, UserUtils.getLoginName(), StringUtils.EMPTY, DateUtils.Now, dtApproval, true);
					CMUtils.saveContentObjectApprovalItem(approval);
				}
				
				if (approvalStatusOrg == ApprovalStatus.Approved){
					approvalResult = ApprovalStatus.Unapprove;
				}
			}
		}else{
			if (approvalStatus != ApprovalStatus.NotSpecified){
				approval = CMUtils.createApprovalItem(Long.MIN_VALUE, contentObject, UserUtils.getLoginName(), 1, approvalStatus, UserUtils.getLoginName(), StringUtils.EMPTY, DateUtils.Now, dtApproval, true);
				CMUtils.saveContentObjectApprovalItem(approval);
			}
			//contentObject.ApprovalItems.add();
		}*/

		return new ImmutablePair<ContentObjectApproval, ApprovalStatus>(approval, approvalResult);
	}

	/// <summary>
	/// Apply a user's rating to the gallery items specified in <paramref name="contentItemMeta" /> and persist to the
	/// data store. A record of the user's rating is stored in their profile. If rating is not a number, then no action
	/// is taken. If rating is less than 0 or greater than 5, it is assigned to 0 or 5 so that the value is guaranteed
	/// to be between those values.
	/// </summary>
	/// <param name="contentItemMeta">An instance containing the rating metadata item and the content objects to which
	/// it applies.</param>
	/// <exception cref="WebException">Thrown when the metadata item is not <see cref="MetadataItemName.Rating" />.</exception>
	private static void persistRating(ContentItemMeta contentItemMeta) throws WebException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, GallerySecurityException, InvalidMDSRoleException, InvalidContentObjectException, InvalidAlbumException, IOException, InvalidGalleryException{
		MetadataItemName metaName = MetadataItemName.getMetadataItemName(contentItemMeta.getMetaItem().MTypeId);

		if (metaName != MetadataItemName.Rating)
			throw new WebException(MessageFormat.format("The PersistRating function is designed to store 'Rating' metadata items, but {0} was passed.", metaName));

		// If rating is not a number, then return without doing anything; otherwise verify rating is between 0 and 5.
		final byte minRating = 0;
		final byte maxRating = 5;
		float rating = StringUtils.toFloat(contentItemMeta.getMetaItem().Value);
		if (rating != Float.MIN_VALUE){
			if (rating < minRating)
				contentItemMeta.getMetaItem().Value = Byte.toString(minRating);

			if (rating > maxRating)
				contentItemMeta.getMetaItem().Value = Byte.toString(maxRating);
		}
		else
			return; // Can't parse rating, so return without doing anything

		for (ContentItem contentItem : contentItemMeta.getContentItems()){
			ContentObjectBo contentObject = getContentObjectAndVerifyEditRatingPermission(contentItem);
			if (contentObject == null)
				continue;

			ContentObjectMetadataItem ratingItem;
			if ((ratingItem = contentObject.getMetadataItems().tryGetMetadataItem(metaName)) != null){
				if (ratingItem.getIsEditable()){
					// We have an existing rating item. Incorporate the user's rating into the average and persist.
					ratingItem.setValue(calculateAvgRating(contentObject, ratingItem, contentItemMeta.getMetaItem().Value));

					CMUtils.saveContentObjectMetadataItem(ratingItem, UserUtils.getLoginName());

					persistRatingInUserProfile(contentObject.getId(), contentItemMeta.getMetaItem().Value);
				}
			}else{
				// No rating item found for this gallery item. Create one (if business rules allow).
				MetadataDefinition metaDefRating = CMUtils.loadGallerySetting(contentObject.getGalleryId()).getMetadataDisplaySettings().find(metaName);

				if (metaDefRating.IsEditable && contentObject.metadataDefinitionApplies(metaDefRating)){
					ContentObjectMetadataItemCollection metaItems = CMUtils.createMetadataCollection();
					ContentObjectMetadataItem newRatingItem = CMUtils.createMetadataItem(Long.MIN_VALUE, contentObject, null, contentItemMeta.getMetaItem().Value, true, metaDefRating);
					metaItems.add(newRatingItem);
					contentObject.addMeta(metaItems);

					ContentObjectMetadataItem ratingCountItem = getRatingCountMetaItem(contentObject);
					ratingCountItem.setValue("1"); // This is the first rating

					CMUtils.saveContentObjectMetadataItem(newRatingItem, UserUtils.getLoginName());
					CMUtils.saveContentObjectMetadataItem(ratingCountItem, UserUtils.getLoginName());

					persistRatingInUserProfile(contentItem.getId(), contentItemMeta.getMetaItem().Value);
				}
			}
		}

		HelperFunctions.purgeCache();
	}

	/// <summary>
	/// Incorporate the new <paramref name="userRatingStr" /> into the current <paramref name="ratingItem" />
	/// belonging to the <paramref name="contentObject" />. Automatically increments and saves the rating count
	/// meta item. Detects when a user has previously rated the item and reverses the effects of the previous 
	/// rating before applying the new one. Returns a <see cref="System.Single" /> converted to a String to 4 
	/// decimal places (e.g. "2.4653").
	/// </summary>
	/// <param name="contentObject">The content object being rated.</param>
	/// <param name="ratingItem">The rating metadata item.</param>
	/// <param name="userRatingStr">The user rating to be applied to the content object rating.</param>
	/// <returns>Returns a <see cref="System.String" /> representing the new rating.</returns>
	private static String calculateAvgRating(ContentObjectBo contentObject, ContentObjectMetadataItem ratingItem, String userRatingStr) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidAlbumException, UnsupportedImageTypeException, IOException, InvalidGalleryException	{
		ContentObjectMetadataItem ratingCountItem = getRatingCountMetaItem(contentObject);
		int ratingCount = StringUtils.toInteger(ratingCountItem.getValue());

		float currentAvgRating, userRating;
		currentAvgRating = StringUtils.toFloat(ratingItem.getValue());
		userRating = StringUtils.toFloat(userRatingStr);

		ContentObjectProfile moProfile = ProfileUtils.getProfile().getContentObjectProfiles().find(ratingItem.getContentObject().getId());
		if (moProfile != null)	{
			// User has previously rated this item. Reverse the influence that rating had on the item's average rating.
			currentAvgRating = removeUsersPreviousRating(ratingItem.getValue(), ratingCount, moProfile.Rating);

			// Subtract the user's previous rating from the total rating count while ensuring the # >= 0.
			ratingCount = Math.max(ratingCount - 1, 0);
		}

		// Increment the rating count and persist.
		ratingCount++;
		ratingCountItem.setValue(Integer.toString(ratingCount));

		CMUtils.saveContentObjectMetadataItem(ratingCountItem, UserUtils.getLoginName());

		// Calculate the new rating.
		float newAvgRating = ((currentAvgRating * (ratingCount - 1)) + userRating) / (ratingCount);

		return String.format("%.4f", newAvgRating); // Store rating to 4 decimal places
	}

	/// <summary>
	/// Reverse the influence a user's rating had on the content object's average rating. The new average rating
	/// is returned. Returns zero if <paramref name="currentAvgRatingStr" /> or <paramref name="userPrevRatingStr" />
	/// can't be converted to a <see cref="System.Single" /> or if <paramref name="ratingCount" /> is less than
	/// or equal to one.
	/// </summary>
	/// <param name="currentAvgRatingStr">The current average rating for the content object as a String (e.g. "2.8374").</param>
	/// <param name="ratingCount">The number of times tje content object has been rated.</param>
	/// <param name="userPrevRatingStr">The user rating whose effect must be removed from the average rating (e.g. "2.5").</param>
	/// <returns><see cref="System.Single" />.</returns>
	private static float removeUsersPreviousRating(String currentAvgRatingStr, int ratingCount, String userPrevRatingStr){
		if (ratingCount <= 1)
			return 0f;

		float currentAvgRating, userRating;
		if ((currentAvgRating = StringUtils.toFloat(userPrevRatingStr)) == Float.MIN_VALUE)
			return 0f;

		if ((userRating = StringUtils.toFloat(currentAvgRatingStr)) == Float.MIN_VALUE)
			return 0f;

		return ((currentAvgRating * ratingCount) - userRating) / (ratingCount - 1);
	}

	/// <summary>
	/// Gets the rating count meta item for the <paramref name="contentObject" />, creating one - and persisting it
	/// to the data store - if necessary.
	/// </summary>
	/// <param name="contentObject">The content object.</param>
	/// <returns><see cref="ContentObjectMetadataItem" />.</returns>
	private static ContentObjectMetadataItem getRatingCountMetaItem(ContentObjectBo contentObject) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidAlbumException, UnsupportedImageTypeException, IOException, InvalidGalleryException{
		ContentObjectMetadataItem metaItem;
		if ((metaItem = contentObject.getMetadataItems().tryGetMetadataItem(MetadataItemName.RatingCount)) != null)	{
			return metaItem;
		}else{
			return createRatingCountMetaItem(contentObject);
		}
	}

	/// <summary>
	/// Create a rating count meta item for the <paramref name="contentObject" /> and persist it to the data store.
	/// </summary>
	/// <param name="contentObject">The content object.</param>
	/// <returns>An instance of <see cref="ContentObjectMetadataItem" />.</returns>
	private static ContentObjectMetadataItem createRatingCountMetaItem(ContentObjectBo contentObject) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidAlbumException, UnsupportedImageTypeException, IOException, InvalidGalleryException{
		// Create the rating count item, add it to the content object, and save.
		MetadataDefinition metaDef = CMUtils.loadGallerySetting(contentObject.getGalleryId()).getMetadataDisplaySettings().find(MetadataItemName.RatingCount);
		ContentObjectMetadataItemCollection metaItems = CMUtils.createMetadataCollection();
		ContentObjectMetadataItem ratingCountItem = CMUtils.createMetadataItem(Long.MIN_VALUE, contentObject, null, "0", true, metaDef);
		metaItems.add(ratingCountItem);
		contentObject.addMeta(metaItems);

		CMUtils.saveContentObjectMetadataItem(ratingCountItem, UserUtils.getLoginName());

		return ratingCountItem;
	}

	/// <summary>
	/// Persists the user's rating in their user profile.
	/// </summary>
	/// <param name="contentObjectId">The content object ID of the item being rated.</param>
	/// <param name="userRating">The user rating (e.g. "2.8374").</param>
	private static void persistRatingInUserProfile(long contentObjectId, String userRating) throws JsonProcessingException	{
		UserProfile profile = ProfileUtils.getProfile();

		ContentObjectProfile moProfile = profile.getContentObjectProfiles().find(contentObjectId);

		if (moProfile == null){
			profile.getContentObjectProfiles().add(new ContentObjectProfile(contentObjectId, userRating));
		}else{
			moProfile.Rating = userRating;
		}

		ProfileUtils.saveProfile(profile);
	}

	/// <summary>
	/// Gets a tree representing the tags used in a gallery. The tree has a root node that serves as the tag container.
	/// It contains a flat list of child nodes for the tags.
	/// </summary>
	/// <param name="tagSearchOptions">The options that specify what kind of tags to return and how they should be
	/// calculated and displayed.</param>
	/// <returns>Returns an instance of <see cref="TreeView" />. Guaranteed to not return null.</returns>
	private static TreeView getTagTree(TagSearchOptions tagSearchOptions, HttpServletRequest request){
		Iterable<TagRest> tags = getTags(tagSearchOptions);
		int id = 0;
		TreeView tv = new TreeView();
		String baseUrl = Utils.getCurrentPageUrl(request);
		String qsParm = getTagTreeNavUrlQsParm(tagSearchOptions.SearchType);

		com.mds.aiotplayer.cm.rest.TreeNode rootNode = new com.mds.aiotplayer.cm.rest.TreeNode();
		rootNode.setText(getTagTreeRootNodeText(tagSearchOptions.SearchType));
		//ToolTip = "Tags in gallery";
		rootNode.setId(StringUtils.join("tv_tags_", id++));
		rootNode.setDataId("root");
		rootNode.setExpanded(tagSearchOptions.TagTreeIsExpanded);

		rootNode.addCssClass("jstree-root-node");

		tv.getNodes().add(rootNode);

		for (TagRest tag : tags){
			com.mds.aiotplayer.cm.rest.TreeNode treeNode = new com.mds.aiotplayer.cm.rest.TreeNode();
			treeNode.setText(MessageFormat.format("{0} ({1})", tag.Value, tag.Count));
			treeNode.setToolTip(I18nUtils.getMessage("site.Tag_Tree_Node_Tt", tag.Value));
			treeNode.setId(StringUtils.join("tv_tags_", id++));
			treeNode.setDataId(tag.Value);
			treeNode.setNavigateUrl(Utils.addQueryStringParameter(baseUrl, StringUtils.join(qsParm, "=", Utils.urlEncode(tag.Value))));
			
			rootNode.getNodes().add(treeNode);
		}

		return tv;
	}

	private static String getTagTreeRootNodeText(TagSearchType searchType){
		switch (searchType)	{
			case AllTagsInGallery:
			case TagsUserCanView:
				return I18nUtils.getMessage("site.Tag_Tree_Root_Node_Title");

			case AllPeopleInGallery:
			case PeopleUserCanView:
				return I18nUtils.getMessage("site.People_Tree_Root_Node_Title");

			default:
				throw new ArgumentException(MessageFormat.format("This function is not expecting TagSearchType={0}", searchType));
		}
	}

	private static String getTagTreeNavUrlQsParm(TagSearchType searchType){
		switch (searchType)	{
			case AllTagsInGallery:
			case TagsUserCanView:
				return "tag";

			case AllPeopleInGallery:
			case PeopleUserCanView:
				return "people";

			default:
				throw new ArgumentException(MessageFormat.format("This function is not expecting TagSearchType={0}", searchType));
		}
	}
	
	private static TagSearchOptions getTagSearchOptions(TagSearchType searchType, String searchTerm, long galleryId) throws InvalidMDSRoleException{
		return getTagSearchOptions(searchType, searchTerm, galleryId, Integer.MIN_VALUE);
	}
	
	private static TagSearchOptions getTagSearchOptions(TagSearchType searchType, String searchTerm, long galleryId, int numTagsToRetrieve) throws InvalidMDSRoleException{
		return getTagSearchOptions(searchType, searchTerm, galleryId, numTagsToRetrieve, TagSearchOptions.TagProperty.NotSpecified);
	}
	
	private static TagSearchOptions getTagSearchOptions(TagSearchType searchType, String searchTerm, long galleryId, int numTagsToRetrieve, TagSearchOptions.TagProperty sortProperty) throws InvalidMDSRoleException{
		return getTagSearchOptions(searchType, searchTerm, galleryId, numTagsToRetrieve, sortProperty, true);
	}
	
	private static TagSearchOptions getTagSearchOptions(TagSearchType searchType, String searchTerm, long galleryId, int numTagsToRetrieve, TagSearchOptions.TagProperty sortProperty, boolean sortAscending) throws InvalidMDSRoleException{
		return getTagSearchOptions(searchType, searchTerm, galleryId, numTagsToRetrieve, sortProperty, sortAscending, false);
	}

	private static TagSearchOptions getTagSearchOptions(TagSearchType searchType, String searchTerm, long galleryId, int numTagsToRetrieve, TagSearchOptions.TagProperty sortProperty, boolean sortAscending, boolean expanded) throws InvalidMDSRoleException{
		TagSearchOptions tagSearchOptions = new TagSearchOptions();
		tagSearchOptions.GalleryId = galleryId;
		tagSearchOptions.SearchType = searchType;
		tagSearchOptions.SearchTerm = searchTerm;
		tagSearchOptions.IsUserAuthenticated = UserUtils.isAuthenticated();
		tagSearchOptions.Roles = RoleUtils.getMDSRolesForUser();
		tagSearchOptions.NumTagsToRetrieve = numTagsToRetrieve;
		tagSearchOptions.SortProperty = sortProperty;
		tagSearchOptions.SortAscending = sortAscending;
		tagSearchOptions.TagTreeIsExpanded = expanded;
		
		return tagSearchOptions;
	}

	//#endregion
}