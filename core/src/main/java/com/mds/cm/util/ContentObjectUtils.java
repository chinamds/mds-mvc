package com.mds.cm.util;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.mds.common.Constants;
import com.mds.cm.content.AddContentObjectSettings;
import com.mds.cm.content.AlbumBo;
import com.mds.cm.content.AlbumProfile;
import com.mds.cm.content.ContentConversionQueue;
import com.mds.cm.content.ContentObjectApproval;
import com.mds.cm.content.ContentObjectApprovalCollection;
import com.mds.cm.content.ContentObjectBo;
import com.mds.cm.content.ContentObjectProfile;
import com.mds.cm.content.ContentObjectProfileCollection;
import com.mds.cm.content.ContentObjectSearchOptions;
import com.mds.cm.content.ContentObjectSearcher;
import com.mds.sys.util.MDSRoleCollection;
import com.mds.cm.content.ExternalContentObject;
import com.mds.sys.util.SecurityGuard;
import com.mds.cm.content.UserProfile;
import com.mds.cm.content.ZipUtility;
import com.mds.cm.content.nullobjects.NullDisplayObject;
import com.mds.cm.exception.GallerySecurityException;
import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidContentObjectException;
import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.cm.metadata.ContentObjectMetadataItem;
import com.mds.cm.metadata.ContentObjectMetadataItemCollection;
import com.mds.cm.metadata.MetadataDefinitionCollection;
import com.mds.cm.rest.AlbumAction;
import com.mds.cm.rest.ApprovalItem;
import com.mds.cm.rest.ContentItem;
import com.mds.cm.rest.DisplayObjectRest;
import com.mds.cm.rest.MediaItem;
import com.mds.cm.rest.MetaItemRest;
import com.mds.common.utils.Reflections;
import com.mds.core.ActionResult;
import com.mds.core.ActionResultStatus;
import com.mds.core.ApprovalAction;
import com.mds.core.ApprovalStatus;
import com.mds.core.ContentObjectSearchType;
import com.mds.core.ContentObjectType;
import com.mds.core.ContentQueueItemConversionType;
import com.mds.core.DisplayObjectType;
import com.mds.core.MetadataItemName;
import com.mds.core.SecurityActions;
import com.mds.core.VirtualAlbumType;
import com.mds.core.exception.ArgumentException;
import com.mds.core.exception.ArgumentNullException;
import com.mds.core.exception.ArgumentOutOfRangeException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.core.exception.WebException;
import com.mds.cm.util.CMUtils;
import com.mds.util.DateUtils;
import com.mds.util.FileMisc;
import com.mds.util.HelperFunctions;
import com.mds.i18n.util.I18nUtils;
import com.mds.util.StringUtils;

import com.mds.sys.util.AppSettings;
import com.mds.sys.util.RoleUtils;
import com.mds.sys.util.UserUtils;

/// <summary>
/// Contains functionality for interacting with content objects (that is, content objects and albums). Typically web pages 
/// directly call the appropriate business layer objects, but when a task involves multiple steps or the functionality 
/// does not exist in the business layer, the methods here are used.
/// </summary>
public final class ContentObjectUtils{
	//#region Public Static Methods

	/// <summary>
	/// Persist the content object to the data store. This method updates the audit fields before saving. The currently logged
	/// on user is recorded as responsible for the changes. All content objects should be
	/// saved through this method rather than directly invoking the content object's Save method, unless you want to 
	/// manually update the audit fields yourself.
	/// </summary>
	/// <param name="contentObject">The content object to persist to the data store.</param>
	/// <remarks>When no user name is available through <see cref="UserUtils.getLoginName()" />, the String &lt;unknown&gt; is
	/// substituted. Since MDS requires users to be logged on to edit objects, there will typically always be a user name 
	/// available. However, in some cases one won't be available, such as when an error occurs during self registration and
	/// the exception handling code needs to delete the just-created user album.</remarks>
	public static void saveContentObject(ContentObjectBo contentObject) throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, InvalidGalleryException, WebException	{
		String userName = (StringUtils.isBlank(UserUtils.getLoginName()) ? I18nUtils.getMessage("site.Missing_Data_Text") : UserUtils.getLoginName());
		saveContentObject(contentObject, userName);
	}

	/// <summary>
	/// Persist the content object to the data store. This method updates the audit fields before saving. All content objects should be
	/// saved through this method rather than directly invoking the content object's Save method, unless you want to
	/// manually update the audit fields yourself.
	/// </summary>
	/// <param name="contentObject">The content object to persist to the data store.</param>
	/// <param name="userName">The user name to be associated with the modifications. This name is stored in the internal
	/// audit fields associated with this content object.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="contentObject" /> is null.</exception>
	public static void saveContentObject(ContentObjectBo contentObject, String userName) throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, InvalidGalleryException, WebException{
		if (contentObject == null)
			throw new ArgumentNullException("contentObject");

		Date currentTimestamp = DateUtils.Now();

		if (contentObject.getIsNew()){
			contentObject.setCreatedByUserName(userName);
			contentObject.setDateAdded(currentTimestamp);
		}

		if (contentObject.getHasChanges()){
			contentObject.setLastModifiedByUserName(userName);
			contentObject.setDateLastModified(currentTimestamp);
		}

		// Verify that any role needed for album ownership exists and is properly configured.
		RoleUtils.validateRoleExistsForAlbumOwner(Reflections.as(contentObject, AlbumBo.class));

		// Persist to data store.
		contentObject.save();
	}

	/// <summary>
	/// Move the specified object to the specified destination album. This method moves the physical files associated with this
	/// object to the destination album's physical directory. The object's Save() method is invoked to persist the changes to the
	/// data store. When moving albums, all the album's children, grandchildren, etc are also moved. 
	/// The audit fields are automatically updated before saving.
	/// </summary>
	/// <param name="contentObjectToMove">The content object to move.</param>
	/// <param name="destinationAlbum">The album to which the current object should be moved.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="contentObjectToMove" /> is null.</exception>
	public static void moveContentObject(ContentObjectBo contentObjectToMove, AlbumBo destinationAlbum) throws InvalidAlbumException, UnsupportedContentObjectTypeException, InvalidContentObjectException, UnsupportedImageTypeException, IOException, InvalidGalleryException	{
		if (contentObjectToMove == null)
			throw new ArgumentNullException("contentObjectToMove");

		String currentUser = UserUtils.getLoginName();
		Date currentTimestamp = DateUtils.Now();

		contentObjectToMove.setLastModifiedByUserName(currentUser);
		contentObjectToMove.setDateLastModified(currentTimestamp);

		contentObjectToMove.moveTo(destinationAlbum);
	}

	/// <summary>
	/// Copy the specified object and place it in the specified destination album. This method creates a completely separate copy
	/// of the original, including copying the physical files associated with this object. The copy is persisted to the data
	/// store and then returned to the caller. When copying albums, all the album's children, grandchildren, etc are also copied.
	/// The audit fields of the copied objects are automatically updated before saving.
	/// </summary>
	/// <param name="contentObjectToCopy">The content object to copy.</param>
	/// <param name="destinationAlbum">The album to which the current object should be copied.</param>
	/// <returns>
	/// Returns a new content object that is an exact copy of the original, except that it resides in the specified
	/// destination album, and of course has a new ID. Child objects are recursively copied.
	/// </returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="contentObjectToCopy" /> is null.</exception>
	public static ContentObjectBo copyContentObject(ContentObjectBo contentObjectToCopy, AlbumBo destinationAlbum) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, UnsupportedImageTypeException, InvalidGalleryException	{
		if (contentObjectToCopy == null)
			throw new ArgumentNullException("contentObjectToCopy");

		String currentUser = UserUtils.getLoginName();

		return contentObjectToCopy.copyTo(destinationAlbum, currentUser);
	}

	/// <summary>
	/// Adds a media file to an album. Prior to calling this method, the file should exist in the 
	/// temporary upload directory (<see cref="GlobalConstants.TempUploadDirectory" />) in the 
	/// App_Data directory with the name <see cref="AddContentObjectSettings.FileNameOnServer" />. The
	/// file is copied to the destination album and given the name of 
	/// <see cref="AddContentObjectSettings.FileName" /> (instead of whatever name it currently has, which 
	/// may contain a GUID).
	/// </summary>
	/// <param name="settings">The settings that contain data and configuration options for the media file.</param>
	/// <exception cref="EventLogs.CustomExceptions.GallerySecurityException">Thrown when user is not authorized to add a content object to the album.</exception>
	public static List<ActionResult> addContentObject(AddContentObjectSettings settings) throws GallerySecurityException{
		List<ActionResult> results = createContentObjectFromFile(settings);

		HelperFunctions.purgeCache();

		return results;
	}

	/// <summary>
	/// Gets the content objects in the album. Includes albums and content objects.
	/// </summary>
	/// <param name="albumId">The album ID.</param>
	/// <param name="sortByMetaName">The sort by meta name id.</param>
	/// <param name="sortAscending">if set to <c>true</c> [sort ascending].</param>
	/// <returns>Returns an <see cref="List" /> instance of <see cref="ContentItem" />.</returns>
	/// <exception cref="InvalidAlbumException">Thrown when an album with the specified
	/// <paramref name="albumId" /> is not found in the data store.</exception>
	/// <exception cref="MDS.EventLogs.CustomExceptions.GallerySecurityException">Thrown when the user does not have at least one of the requested permissions to the
	/// specified album.</exception>
	public static List<ContentItem> getContentItemsInAlbum(long albumId, MetadataItemName sortByMetaName, boolean sortAscending, HttpServletRequest request) throws Exception{
		AlbumBo album = CMUtils.loadAlbumInstance(albumId, true);

		SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), album.getId(), album.getGalleryId(), UserUtils.isAuthenticated(), album.getIsPrivate(), album.getIsVirtualAlbum());

		List<ContentObjectBo> contentObjects;

		if (MetadataItemName.isValidFormattedMetadataItemName(sortByMetaName)){
			contentObjects = album.getChildContentObjects(ContentObjectType.All, ApprovalStatus.All, !UserUtils.isAuthenticated()).toSortedList(sortByMetaName, sortAscending, album.getGalleryId());
		}else{
			contentObjects = album.getChildContentObjects(ContentObjectType.All, ApprovalStatus.All, !UserUtils.isAuthenticated()).toSortedList();
		}

		return Lists.newArrayList(toContentItems(contentObjects, request));
	}

	//public static List<ContentItem> getContentItemsHavingTags(String[] tags, String[] people, long galleryId, MetadataItemName sortByMetaName, boolean sortAscending, ContentObjectType filter)
	//{
	//	AlbumBo album = getContentObjectsHavingTags(tags, people, filter, galleryId);

	//	List<ContentObjectBo> contentObjects;

	//	if (MetadataItemNameEnumHelper.IsValidFormattedMetadataItemName(sortByMetaName))
	//	{
	//		contentObjects = album.GetChildContentObjects(ContentObjectType.All, !UserUtils.isAuthenticated()).ToSortedList(sortByMetaName, sortAscending, album.getGalleryId());
	//	}
	//	else
	//	{
	//		contentObjects = album.GetChildContentObjects(ContentObjectType.All, !UserUtils.isAuthenticated()).ToSortedList();
	//	}

	//	return ToContentItems(contentObjects).AsQueryable();
	//}

	/// <summary>
	/// Return a virtual album containing content objects whose title or caption contain the specified search Strings and
	/// for which the current user has authorization to view. Guaranteed to not return null. A gallery 
	/// object is considered a match when all search terms are found in the relevant fields.
	/// </summary>
	/// <param name="searchStrings">The Strings to search for.</param>
	/// <param name="filter">A filter that limits the types of content objects that are returned.
	/// Maps to the <see cref="ContentObjectType" /> enumeration.</param>
	/// <param name="galleryId">The ID for the gallery containing the objects to search.</param>
	/// <returns>
	/// Returns an <see cref="AlbumBo" /> containing the matching items. This may include albums and media
	/// objects from different albums.
	/// </returns>
	public static AlbumBo getContentObjectsHavingTitleOrCaption(String[] searchStrings, ContentObjectType filter, ApprovalStatus approval, long galleryId) throws InvalidGalleryException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, InvalidMDSRoleException{
		if (searchStrings == null && approval == ApprovalStatus.All)
			throw new ArgumentNullException();

		AlbumBo tmpAlbum = CMUtils.createEmptyAlbumInstance(galleryId);
		tmpAlbum.setIsVirtualAlbum(true);
		tmpAlbum.setVirtualAlbumType(VirtualAlbumType.TitleOrCaption);
		tmpAlbum.setTitle(StringUtils.join(I18nUtils.getMessage("site.Search_Title"), StringUtils.join(searchStrings, I18nUtils.getMessage("site.Search_Concat")), getWithApprovalAlbumTitle(approval)));
		tmpAlbum.setCaption(StringUtils.EMPTY);

		ContentObjectSearchOptions searchOptions = new ContentObjectSearchOptions(
			galleryId,
			ContentObjectSearchType.SearchByTitleOrCaption,
			searchStrings,
			UserUtils.isAuthenticated(),
			RoleUtils.getMDSRolesForUser(),
			filter,
			approval
		);

		ContentObjectSearcher searcher = new ContentObjectSearcher(searchOptions);

		List<ContentObjectBo> contentObjects = searcher.find();
		for (ContentObjectBo contentObject : contentObjects){
			tmpAlbum.addContentObject(contentObject);
		}

		return tmpAlbum;
	}

	/// <summary>
	/// Return a virtual album containing content objects that match the specified search Strings and
	/// for which the current user has authorization to view. Guaranteed to not return null. A gallery 
	/// object is considered a match when all search terms are found in the relevant fields.
	/// </summary>
	/// <param name="searchStrings">The Strings to search for.</param>
	/// <param name="filter">A filter that limits the types of content objects that are returned.
	/// Maps to the <see cref="ContentObjectType" /> enumeration.</param>
	/// <param name="galleryId">The ID for the gallery containing the objects to search.</param>
	/// <returns>
	/// Returns an <see cref="AlbumBo" /> containing the matching items. This may include albums and media
	/// objects from different albums.
	/// </returns>
	public static AlbumBo getContentObjectsHavingSearchString(String[] searchStrings, ContentObjectType filter, ApprovalStatus approval, long galleryId) throws InvalidMDSRoleException, InvalidGalleryException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException{
		if (searchStrings == null && approval == ApprovalStatus.All)
			throw new ArgumentNullException();

		AlbumBo tmpAlbum = CMUtils.createEmptyAlbumInstance(galleryId);
		tmpAlbum.setIsVirtualAlbum(true);
		tmpAlbum.setVirtualAlbumType(VirtualAlbumType.Search);
		tmpAlbum.setTitle(StringUtils.join(I18nUtils.getMessage("site.Search_Title"), StringUtils.join(searchStrings, I18nUtils.getMessage("site.Search_Concat")), getWithApprovalAlbumTitle(approval)));
		tmpAlbum.setCaption(StringUtils.EMPTY);

		ContentObjectSearchOptions searchOptions = new ContentObjectSearchOptions(
			galleryId,
			ContentObjectSearchType.SearchByKeyword,
			searchStrings,
			UserUtils.isAuthenticated(),
			RoleUtils.getMDSRolesForUser(),
			filter,
			approval
		);

		ContentObjectSearcher searcher = new ContentObjectSearcher(searchOptions);

		List<ContentObjectBo> contentObjects = searcher.find();
		for (ContentObjectBo contentObject : contentObjects){
			tmpAlbum.addContentObject(contentObject);
		}

		return tmpAlbum;
	}

	/// <summary>
	/// Gets a virtual album containing content objects that match the specified <paramref name="tags" /> or <paramref name="people" />
	/// belonging to the specified <paramref name="galleryId" />. Guaranteed to not return null. The returned album 
	/// is a virtual one (<see cref="AlbumBo.getIsVirtualAlbum()" />=<c>true</c>) containing the collection of matching 
	/// items the current user has permission to view. Returns an empty album when no matches are found or the 
	/// query String does not contain the search terms.
	/// </summary>
	/// <param name="tags">The tags to search for. If specified, the <paramref name="people" /> parameter must be null.</param>
	/// <param name="people">The people to search for. If specified, the <paramref name="tags" /> parameter must be null.</param>
	/// <param name="filter">A filter that limits the types of content objects that are returned.
	/// Maps to the <see cref="ContentObjectType" /> enumeration.</param>
	/// <param name="galleryId">The ID of the gallery. Only objects in this gallery are returned.</param>
	/// <returns>An instance of <see cref="AlbumBo" />.</returns>
	/// <exception cref="System.ArgumentException">Throw when the tags and people parameters are both null or empty, or both
	/// have values.</exception>
	public static AlbumBo getContentObjectsHavingTags(String[] tags, String[] people, ContentObjectType filter, ApprovalStatus approval, long galleryId) throws InvalidGalleryException, InvalidMDSRoleException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException
	{
		if (((tags == null) || (tags.length == 0)) && ((people == null) || (people.length == 0)) && (approval == ApprovalStatus.All))
			throw new ArgumentException("ContentObjectUtils.GetContentObjectsHavingTags() requires the tags or people parameters to be specified, but they were both null or empty.");

		if ((tags != null) && (tags.length > 0) && (people != null) && (people.length > 0) && (approval == ApprovalStatus.All))
			throw new ArgumentException("ContentObjectUtils.GetContentObjectsHavingTags() requires EITHER the tags or people parameters to be specified, but not both. Instead, they were both populated.");

		ContentObjectSearchType searchType = (tags != null && tags.length > 0 ? ContentObjectSearchType.SearchByTag : ContentObjectSearchType.SearchByPeople);
		String[] searchTags = (searchType == ContentObjectSearchType.SearchByTag ? tags : people);

		AlbumBo tmpAlbum = CMUtils.createEmptyAlbumInstance(galleryId);
		tmpAlbum.setIsVirtualAlbum(true);
		tmpAlbum.setVirtualAlbumType((searchType == ContentObjectSearchType.SearchByTag ? VirtualAlbumType.Tag : VirtualAlbumType.People));
		tmpAlbum.setTitle(StringUtils.join(I18nUtils.getMessage("site.Tag_Title"), StringUtils.join(searchTags, I18nUtils.getMessage("site.Search_Concat")), getWithApprovalAlbumTitle(approval)));
		tmpAlbum.setCaption(StringUtils.EMPTY);

		ContentObjectSearcher searcher = new ContentObjectSearcher(new ContentObjectSearchOptions(
			searchType,
			searchTags,
			galleryId,
			RoleUtils.getMDSRolesForUser(),
			UserUtils.isAuthenticated(),
			filter,
			approval
		));

		List<ContentObjectBo> contentObjects = searcher.find();
		for (ContentObjectBo contentObject : contentObjects){
			tmpAlbum.addContentObject(contentObject);
		}

		return tmpAlbum;
	}

	/// <summary>
	/// Gets the content objects most recently added to the gallery having <paramref name="galleryId" />.
	/// </summary>
	/// <param name="top">The maximum number of results to return. Must be greater than zero.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <param name="filter">A filter that limits the types of content objects that are returned.</param>
	/// <returns>An instance of <see cref="AlbumBo" />.</returns>
	/// <exception cref="ArgumentException">Thrown when <paramref name="top" /> is less than or equal to zero.</exception>
	public static AlbumBo getMostRecentlyAddedContentObjects(int top, long galleryId, ContentObjectType filter, ApprovalStatus approval) throws InvalidGalleryException, InvalidMDSRoleException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException{
		if (top <= 0)
			throw new ArgumentException("The top parameter must contain a number greater than zero.", "top");

		AlbumBo tmpAlbum = CMUtils.createEmptyAlbumInstance(galleryId);

		tmpAlbum.setIsVirtualAlbum(true);
		tmpAlbum.setVirtualAlbumType(VirtualAlbumType.MostRecentlyAdded);
		tmpAlbum.setTitle(StringUtils.join(I18nUtils.getMessage("site.Recently_Added_Title"), getWithApprovalAlbumTitle(approval)));
		tmpAlbum.setCaption(StringUtils.EMPTY);
		tmpAlbum.setSortByMetaName(MetadataItemName.DateAdded);
		tmpAlbum.setSortAscending(false);

		ContentObjectSearcher searcher = new ContentObjectSearcher(new ContentObjectSearchOptions(
			ContentObjectSearchType.MostRecentlyAdded,
			galleryId,
			RoleUtils.getMDSRolesForUser(),
			UserUtils.isAuthenticated(),
			top,
			filter,
			approval
		));

		List<ContentObjectBo> contentObjects = searcher.find();
		for (ContentObjectBo contentObject : contentObjects){
			tmpAlbum.addContentObject(contentObject);
		}

		return tmpAlbum;
	}

	/// <summary>
	/// Gets the content objects having the specified <paramref name="rating" /> and belonging to the
	/// <paramref name="galleryId" />.
	/// </summary>
	/// <param name="rating">Identifies the type of rating to retrieve. Valid values: "highest", "lowest", "none", or a number
	/// from 0 to 5 in half-step increments (eg. 0, 0.5, 1, 1.5, ... 4.5, 5).</param>
	/// <param name="top">The maximum number of results to return. Must be greater than zero.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <param name="filter">A filter that limits the types of content objects that are returned.</param>
	/// <returns>An instance of <see cref="AlbumBo" />.</returns>
	/// <exception cref="ArgumentException">Thrown when <paramref name="top" /> is less than or equal to zero.</exception>
	public static AlbumBo getRatedContentObjects(String rating, int top, long galleryId, ContentObjectType filter, ApprovalStatus approval) throws InvalidGalleryException, InvalidMDSRoleException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException	{
		if (top <= 0)
			throw new ArgumentException("The top parameter must contain a number greater than zero.", "top");

		AlbumBo tmpAlbum = CMUtils.createEmptyAlbumInstance(galleryId);

		tmpAlbum.setIsVirtualAlbum(true);
		tmpAlbum.setVirtualAlbumType(VirtualAlbumType.Rated);
		tmpAlbum.setTitle(getRatedAlbumTitle(rating));
		tmpAlbum.setCaption(StringUtils.EMPTY);

		String[] ratingSortTrigger = new String[] {"lowest", "highest"};
		if (ArrayUtils.contains(ratingSortTrigger, rating)){
			// Sort on rating field for lowest or highest. All others use the default album sort setting.
			tmpAlbum.setSortByMetaName(MetadataItemName.Rating);
			tmpAlbum.setSortAscending(!rating.equalsIgnoreCase("highest" ));
		}

		ContentObjectSearcher searcher = new ContentObjectSearcher(new ContentObjectSearchOptions(
			ContentObjectSearchType.SearchByRating,
			new String[] { rating },
			galleryId,
			RoleUtils.getMDSRolesForUser(),
			UserUtils.isAuthenticated(),
			top,
			filter,
			approval
		));

		List<ContentObjectBo> contentObjects = searcher.find();
		for (ContentObjectBo contentObject : contentObjects){
			tmpAlbum.addContentObject(contentObject);
		}

		return tmpAlbum;
	}

	/// <summary>
	/// Gets the content objects having the specified <paramref name="rating" /> and belonging to the
	/// <paramref name="galleryId" />.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <param name="filter">A filter that limits the types of content objects that are returned.</param>
	/// <param name="approval">A filter that limits the types of approval status that are returned.</param>
	/// <returns>An instance of <see cref="AlbumBo" />.</returns>
	/// <exception cref="ArgumentException">Thrown when <paramref name="top" /> is less than or equal to zero.</exception>
	public static AlbumBo getApprovalContentObjects(long galleryId, ContentObjectType filter, ApprovalStatus approval) throws InvalidGalleryException, InvalidMDSRoleException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException
	{
		if (approval == ApprovalStatus.All)
			throw new ArgumentNullException();

		AlbumBo tmpAlbum = CMUtils.createEmptyAlbumInstance(galleryId);

		tmpAlbum.setIsVirtualAlbum(true);
		tmpAlbum.setVirtualAlbumType(VirtualAlbumType.Approval);
		tmpAlbum.setTitle(getApprovalAlbumTitle(approval));
		tmpAlbum.setCaption(StringUtils.EMPTY);

		ContentObjectSearcher searcher = new ContentObjectSearcher(new ContentObjectSearchOptions(
			ContentObjectSearchType.SearchByApproval,
			null,
			galleryId,
			RoleUtils.getMDSRolesForUser(),
			UserUtils.isAuthenticated(),
			filter,
			approval
		));

		List<ContentObjectBo> contentObjects = searcher.find();
		for (ContentObjectBo contentObject : contentObjects){
			tmpAlbum.addContentObject(contentObject);
		}

		return tmpAlbum;
	}
	
	/// <summary>
	/// Gets the content objects having the specified <paramref name="approval" /> and belonging to the
	/// <paramref name="galleryId" />.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <param name="filter">A filter that limits the types of content objects that are returned.</param>
	/// <param name="approval">A filter that limits the types of approval status that are returned.</param>
	/// <returns>An instance of <see cref="AlbumBo" />.</returns>
	/// <exception cref="ArgumentException">Thrown when <paramref name="top" /> is less than or equal to zero.</exception>
	public static AlbumBo getContentObjectsForPreview(long galleryId, List<ContentObjectBo> contentObjects) throws InvalidGalleryException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException{
		AlbumBo tmpAlbum = CMUtils.createEmptyAlbumInstance(galleryId);

		tmpAlbum.setIsVirtualAlbum(true);
		tmpAlbum.setVirtualAlbumType(VirtualAlbumType.ContentPreview);
		tmpAlbum.setTitle(I18nUtils.getMessage("site.contentPreview"));
		tmpAlbum.setCaption(StringUtils.EMPTY);

		for (ContentObjectBo contentObject : contentObjects){
			tmpAlbum.addContentObject(contentObject);
		}

		return tmpAlbum;
	}
	/// <summary>
	/// Sorts the gallery items passed to this method and return. No changes are made to the data store.
	/// When the album is virtual, the <see cref="AlbumRestAction.Album.ContentItems" /> property
	/// must be populated with the items to sort. For non-virtual albums (those with a valid ID), the 
	/// content objects are retrieved based on the ID and then sorted. The sort preference is saved to 
	/// the current user's profile, except when the album is virtual. The method incorporates security to
	/// ensure only authorized items are returned to the user.
	/// </summary>
	/// <param name="albumAction">An instance containing the album to sort and the sort preferences.</param>
	/// <returns>List{ContentItem}.</returns>
	/// <exception cref="MDS.EventLogs.CustomExceptions.GallerySecurityException">Thrown when 
	/// the user does not have view permission to the specified album.</exception>
	public static List<ContentItem> sortContentItems(AlbumAction albumAction, HttpServletRequest request) throws Exception{
		AlbumBo album;
		if (albumAction.Album.Id > Long.MIN_VALUE){
			album = CMUtils.loadAlbumInstance(albumAction.Album.Id, true);

			SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), album.getId(), album.getGalleryId(), UserUtils.isAuthenticated(), album.getIsPrivate(), album.getIsVirtualAlbum());

			persistUserSortPreference(album, albumAction.SortByMetaNameId, albumAction.SortAscending);
		}else{
			album = CMUtils.createAlbumInstance(albumAction.Album.Id, albumAction.Album.GalleryId);
			album.setIsVirtualAlbum((albumAction.Album.VirtualType != VirtualAlbumType.NotVirtual.value()));
			album.setVirtualAlbumType(VirtualAlbumType.getVirtualAlbumType(albumAction.Album.VirtualType));

			MDSRoleCollection roles = RoleUtils.getMDSRolesForUser();

			for (ContentItem contentItem : albumAction.Album.ContentItems){
				if (contentItem.IsAlbum){
					AlbumBo childAlbum = CMUtils.loadAlbumInstance(contentItem.Id, false);

					if (SecurityGuard.isUserAuthorized(SecurityActions.ViewAlbumOrContentObject, roles, childAlbum.getId(), childAlbum.getGalleryId(), UserUtils.isAuthenticated(), childAlbum.getIsPrivate(), childAlbum.getIsVirtualAlbum()))
						album.addContentObject(childAlbum);
				}else{
					ContentObjectBo contentObject = CMUtils.loadContentObjectInstance(contentItem.Id);

					if (SecurityGuard.isUserAuthorized(SecurityActions.ViewAlbumOrContentObject, roles, contentObject.getParent().getId(), contentObject.getGalleryId(), UserUtils.isAuthenticated(), contentObject.getParent().getIsPrivate(), ((AlbumBo)contentObject.getParent()).getIsVirtualAlbum()))
						album.addContentObject(contentObject);
				}
			}
		}

		List<ContentObjectBo> contentObjects = album
			.getChildContentObjects(ContentObjectType.All, ApprovalStatus.All, !UserUtils.isAuthenticated())
			.toSortedList(albumAction.SortByMetaNameId, albumAction.SortAscending, album.getGalleryId());

		return Lists.newArrayList(toContentItems(contentObjects, request));
	}

	/// <summary>
	/// Gets the content objects in the album (excludes albums).
	/// </summary>
	/// <param name="albumId">The album id.</param>
	/// <param name="sortByMetaName">The sort by meta name id.</param>
	/// <param name="sortAscending">if set to <c>true</c> [sort ascending].</param>
	/// <returns>Returns an <see cref="List" /> instance of <see cref="MediaItem" />.</returns>
	/// <exception cref="InvalidAlbumException">Thrown when an album with the specified 
	/// <paramref name = "albumId" /> is not found in the data store.</exception>
	/// <exception cref="MDS.EventLogs.CustomExceptions.GallerySecurityException">
	/// Throw when the user does not have view permission to the specified album.</exception>
	public static List<MediaItem> getMediaItemsInAlbum(long albumId, MetadataItemName sortByMetaName, boolean sortAscending, HttpServletRequest request) throws Exception	{
		AlbumBo album = CMUtils.loadAlbumInstance(albumId, true);
		SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), album.getId(), album.getGalleryId(), UserUtils.isAuthenticated(), album.getIsPrivate(), album.getIsVirtualAlbum());

		List<ContentObjectBo> contentObjects;

		if (MetadataItemName.isValidFormattedMetadataItemName(sortByMetaName))
		{
			contentObjects = album.getChildContentObjects(ContentObjectType.ContentObject, ApprovalStatus.All, !UserUtils.isAuthenticated()).toSortedList(sortByMetaName, sortAscending, album.getGalleryId());
		}else{
			contentObjects = album.getChildContentObjects(ContentObjectType.ContentObject, ApprovalStatus.All, !UserUtils.isAuthenticated()).toSortedList();
		}

		//var contentObjects = album.GetChildContentObjects(ContentObjectType.ContentObject, !UserUtils.isAuthenticated()).ToSortedList();

		return Lists.newArrayList(toMediaItems(contentObjects, request));
	}

	public static MetaItemRest[] getMetaItemsForContentObject(long id, HttpServletRequest request) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, GallerySecurityException, InvalidMDSRoleException, InvalidGalleryException{
		ContentObjectBo mo = CMUtils.loadContentObjectInstance(id);
		SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), mo.getParent().getId(), mo.getGalleryId(), UserUtils.isAuthenticated(), mo.getParent().getIsPrivate(), ((AlbumBo)mo.getParent()).getIsVirtualAlbum());

		return toMetaItems(mo.getMetadataItems().getVisibleItems(), mo, request);
	}

	public static MetaItemRest[] toMetaItems(ContentObjectMetadataItemCollection metadataItems, ContentObjectBo contentObject, HttpServletRequest request) throws InvalidAlbumException, UnsupportedContentObjectTypeException, InvalidMDSRoleException, InvalidGalleryException{
		MetaItemRest[] metaItems = new MetaItemRest[metadataItems.size()];
		MetadataDefinitionCollection metaDefs = CMUtils.loadGallerySetting(contentObject.getGalleryId()).getMetadataDisplaySettings();
		ContentObjectProfileCollection moProfiles = ProfileUtils.getProfile().getContentObjectProfiles();
		ApprovalAction nStatus = ApprovalAction.NotSpecified;
		String strApproveBy = "";
		String dtApproval = "";
		ContentObjectApproval moApproval = contentObject.getApprovalItems().getLatestApprovalItem();
		if (moApproval != null)	{
			nStatus = moApproval.getApprovalAction();
			strApproveBy = moApproval.getApproveBy();
			dtApproval = DateUtils.formatDate(moApproval.getApproveDate(), CMUtils.loadGallerySetting(contentObject.getGalleryId()).getMetadataDateTimeFormatString());
		}
		boolean bIsApprovalUser = SecurityGuard.isUserApprovalContent(RoleUtils.getMDSRolesForUser());

		for (int i = 0; i < metaItems.length; i++){
			ContentObjectMetadataItem md = metadataItems.get(i);

			metaItems[i] = new MetaItemRest(
					md.getContentObjectMetadataId(),
					contentObject.getId(),
					md.getMetadataItemName(),
					contentObject.getContentObjectType(),
					md.getDescription(),
					md.getValue(),
					metaDefs.find(md.getMetadataItemName()).IsEditable);

			/*if (md.getMetadataItemName() == MetadataItemName.Player || md.getMetadataItemName() == MetadataItemName.Caption)
			{
				if (!bIsApprovalUser)
				{
					metaItems[i].IsEditable = false;
				}
				//metaItems[i].Value = (metaItems[i].IsEditable ? metaItems[i].Value.SubString(0, metaItems[i].Value.IndexOf("||")) : metaItems[i].Value.SubString(metaItems[i].Value.IndexOf("||") + 2));
			}*/
			switch (md.getMetadataItemName()){
			case Title:
				metaItems[i].Value = metaItems[i].Value.replace("{album.root_Album_Default_Title}", I18nUtils.getMessage("album.root_Album_Default_Title"));
				break;
			case Caption:
				metaItems[i].Value = metaItems[i].Value.replace("{album.root_Album_Default_Summary}", I18nUtils.getMessage("album.root_Album_Default_Summary"));
				break;
			case Rating:
				replaceAvgRatingWithUserRating(metaItems[i], moProfiles);
				break;
			case Width:
				metaItems[i].Value = metaItems[i].Value.replace("{metadata.width_Units}", I18nUtils.getString("metadata.width_Units", request.getLocale()));
				break;
			case Height:
				metaItems[i].Value = metaItems[i].Value.replace("{metadata.height_Units}", I18nUtils.getString("metadata.height_Units", request.getLocale()));
				break;
			case ColorRepresentation:
				metaItems[i].Value = metaItems[i].Value.replace("{metadata.colorRepresentation_sRGB}", I18nUtils.getString("metadata.colorRepresentation_sRGB", request.getLocale()));
				metaItems[i].Value = metaItems[i].Value.replace("{metadata.colorRepresentation_Uncalibrated}", I18nUtils.getString("metadata.colorRepresentation_Uncalibrated", request.getLocale()));
				break;
			case ExposureCompensation:
				metaItems[i].Value = metaItems[i].Value.replace("{metadata.exposureCompensation_Suffix}", I18nUtils.getString("metadata.exposureCompensation_Suffix", request.getLocale()));
				break;
			case GpsAltitude:
				metaItems[i].Value = metaItems[i].Value.replace("{metadata.meters}", I18nUtils.getString("metadata.meters", request.getLocale()));
				break;
			case Approval:
				metaItems[i].Value = strApproveBy;
				metaItems[i].IsEditable = false;
				break;
			case ApproveStatus:
				/*if (!bIsApprovalUser)
				{
					metaItems[i].IsEditable = false;
				}*/
				if (metaItems[i].IsEditable){
					metaItems[i].Value = Integer.toString(nStatus.value());
				}else{
					metaItems[i].Value = getApprovalAction(nStatus);
				}
				break;
			case ApprovalDate:
				metaItems[i].Value = dtApproval;
				metaItems[i].IsEditable = false;
				break;
			default:
				break;
			}
		}

		return metaItems;
	}

	public static ApprovalItem[] getApprovalItemsForContentObject(long id) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, GallerySecurityException, InvalidMDSRoleException, InvalidGalleryException{
		ContentObjectBo mo = CMUtils.loadContentObjectInstance(id);
		SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), mo.getParent().getId(), mo.getGalleryId(), UserUtils.isAuthenticated(), mo.getParent().getIsPrivate(), ((AlbumBo)mo.getParent()).getIsVirtualAlbum());

		return toApprovalItems(mo.getApprovalItems(), mo);
	}

	public static ApprovalItem[] toApprovalItems(ContentObjectApprovalCollection approvals, ContentObjectBo contentObject){
		ApprovalItem[] approvalItems = new ApprovalItem[approvals.size()];
		for (int i = 0; i < approvalItems.length; i++)	{
			ContentObjectApproval ga = approvals.get(i);
			approvalItems[i] = new ApprovalItem(
				ga.getId(),
				contentObject.getId(),
				contentObject.getContentObjectType(),
				ga.getApproveBy(),
				ga.getSeq(),
				ga.getApprovalAction(),
				ga.getApproveDate()
			);
		}

		return approvalItems;
	}

	/// <summary>
	/// When the current user has previously rated an item, replace the average user rating with user's
	/// own rating.
	/// </summary>
	/// <param name="metaItem">The meta item. It must be a <see cref="MetadataItemName.Rating" /> item.</param>
	/// <param name="moProfiles"></param>
	private static void replaceAvgRatingWithUserRating(MetaItemRest metaItem, ContentObjectProfileCollection moProfiles){
		ContentObjectProfile moProfile = moProfiles.find(metaItem.ContentId);

		if (moProfile != null){
			metaItem.Desc = I18nUtils.getMessage("uc.metadata.UserRated_Rating_Lbl");
			metaItem.Value = moProfile.Rating;
		}
	}


	/// <summary>
	/// Returns the current version of MDS System.
	/// </summary>
	/// <returns>An instance of <see cref="MDSDataSchemaVersion" /> representing the version (e.g. "1.0.0").</returns>
	public static String getApprovalAction(ApprovalAction approvalAction){
		switch (approvalAction)	{
			case Approve:
				return I18nUtils.getMessage("uc.moView.Approval_Approved");
			case Reject:
				return I18nUtils.getMessage("uc.moView.Approval_Rejected");
			default:
				return I18nUtils.getMessage("uc.moView.Approval_NoAction");
		}
	}

	
	//public static List<MetaItemRest> getMetaItemsForContentObject(long id)
	//{
	//	var metadataItems = new ArrayList<MetaItemRest>();

	//	ContentObjectBo mo = CMUtils.loadContentObjectInstance(id);
	//	SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), mo.getParent().getId(), mo.getGalleryId(), UserUtils.isAuthenticated(), mo.getParent().getIsPrivate());

	//	for (ContentObjectMetadataItem md : mo.MetadataItems.GetVisibleItems())
	//	{
	//		metadataItems.add(new MetaItemRest
	//												{
	//													Id = md.ContentObjectMetadataId,
	//													TypeId = (int)md.getMetadataItemName(),
	//													Desc = md.Description,
	//													Value = md.Value,
	//													IsEditable = false
	//												});
	//	}

	//	return metadataItems.AsQueryable();
	//}

	/// <summary>
	/// Converts the <paramref name="contentObjects" /> to an enumerable collection of 
	/// <see cref="ContentItem" /> instances. Guaranteed to not return null.
	/// </summary>
	/// <param name="contentObjects">The content objects.</param>
	/// <returns>An enumerable collection of <see cref="ContentItem" /> instances.</returns>
	/// <exception cref="System.ArgumentNullException"></exception>
	public static ContentItem[] toContentItems(List<ContentObjectBo> contentObjects, HttpServletRequest request) throws Exception{
		if (contentObjects == null)
			throw new ArgumentNullException("contentObjects");

		List<ContentItem> gEntities = new ArrayList<ContentItem>(contentObjects.size());
		for(ContentObjectBo contentObject : contentObjects) {
			gEntities.add(toContentItem(contentObject, ContentObjectHtmlBuilder.getContentObjectHtmlBuilderOptions(contentObject, request)));
		}
		//gEntities.AddRange(contentObjects.Select(contentObject => ToContentItem(contentObject, ContentObjectHtmlBuilder.GetContentObjectHtmlBuilderOptions(contentObject))));

		return gEntities.toArray(new ContentItem[0]);
	}

	/// <summary>
	/// Converts the <paramref name="contentObjects" /> to an enumerable collection of 
	/// <see cref="MediaItem" /> instances. Guaranteed to not return null. Do not pass any 
	/// <see cref="AlbumBo" /> instances to this function.
	/// </summary>
	/// <param name="contentObjects">The content objects.</param>
	/// <returns>An enumerable collection of <see cref="MediaItem" /> instances.</returns>
	/// <exception cref="System.ArgumentNullException"></exception>
	public static MediaItem[] toMediaItems(List<ContentObjectBo> contentObjects, HttpServletRequest request) throws Exception{
		if (contentObjects == null)
			throw new ArgumentNullException("contentObjects");

		List<MediaItem> moEntities = new ArrayList<MediaItem>(contentObjects.size());
		ContentObjectHtmlBuilderOptions moBuilderOptions = ContentObjectHtmlBuilder.getContentObjectHtmlBuilderOptions(null, request);

		int i = 1;
		for(ContentObjectBo mo : contentObjects) {
			moEntities.add(toMediaItem(mo, i++, moBuilderOptions, request));
		}
		//moEntities.addAll(contentObjects.stream().map(mo -> toMediaItem(mo, i++, moBuilderOptions)).collect(Collectors.toList()));

		return moEntities.toArray(new MediaItem[0]);
	}

	/// <summary>
	/// Converts the <paramref name="contentObject" /> to an instance of <see cref="ContentItem" />.
	/// The instance can be JSON-serialized and sent to the browser.
	/// </summary>
	/// <param name="contentObject">The content object to convert to an instance of
	/// <see cref="ContentItem" />. It may be a content object or album.</param>
	/// <param name="moBuilderOptions">A set of properties to be used to build the HTML, JavaScript or URL for the 
	/// <paramref name="contentObject" />.</param>
	/// <returns>Returns an <see cref="ContentItem" /> object containing information
	/// about the requested item.</returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="contentObject" /> or 
	/// <paramref name="moBuilderOptions" /> is null.</exception>
	/// <exception cref="System.ArgumentOutOfRangeException">Thrown when <paramref name="moBuilderOptions" /> does
	/// has a null or empty <see cref="ContentObjectHtmlBuilderOptions.Browsers" /> property.</exception>
	public static ContentItem toContentItem(ContentObjectBo contentObject, ContentObjectHtmlBuilderOptions moBuilderOptions) throws Exception{
		if (contentObject == null)
			throw new ArgumentNullException("contentObject");

		if (moBuilderOptions == null)
			throw new ArgumentNullException("moBuilderOptions");

		if (moBuilderOptions.Browsers == null || moBuilderOptions.Browsers.isEmpty())
			throw new ArgumentOutOfRangeException("moBuilderOptions.Browsers", "The Browsers array property must have at least one element.");

		moBuilderOptions.ContentObject = contentObject;

		ContentItem gItem = new ContentItem(
							contentObject.getId(),
							contentObject.getTitle(),
							contentObject.getCaption(),
							getViews(moBuilderOptions).toArray(new DisplayObjectRest[0]),
							0,
							contentObject.getMimeType().getTypeCategory(),
							contentObject.getContentObjectType()
						);

		AlbumBo album = Reflections.as(contentObject, AlbumBo.class);
		if (album != null){
			gItem.IsAlbum = true;
			//gItem.DateStart = album.DateStart;
			//gItem.DateEnd = album.DateEnd;
			gItem.NumAlbums = album.getChildContentObjects(ContentObjectType.All, ApprovalStatus.All, !UserUtils.isAuthenticated()).count();
			gItem.NumContentItems = album.getChildContentObjects(ContentObjectType.ContentObject, ApprovalStatus.All, !UserUtils.isAuthenticated()).count();
		}

		return gItem;
	}

	/// <summary>
	/// Converts the <paramref name="contentObject"/> to an instance of <see cref="ContentItem" />.
	/// The returned object DOES have the <see cref="ContentItem.MetaItems" /> property assigned.
	/// The instance can be JSON-serialized and sent to the browser. Do not pass an 
	/// <see cref="AlbumBo" /> to this function.
	/// </summary>
	/// <param name="contentObject">The content object to convert to an instance of
	/// <see cref="ContentItem"/>.</param>
	/// <param name="indexInAlbum">The one-based index of this content object within its album. This value is assigned to 
	/// <see cref="ContentItem.Index" />.</param>
	/// <param name="moBuilderOptions">A set of properties to be used to build the HTML, JavaScript or URL for the 
	/// <paramref name="contentObject" />.</param>
	/// <returns>Returns an <see cref="ContentItem"/> object containing information
	/// about the requested content object.</returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="contentObject" /> or 
	/// <paramref name="moBuilderOptions" /> is null.</exception>
	/// <exception cref="System.ArgumentOutOfRangeException">Thrown when <paramref name="moBuilderOptions" /> does
	/// has a null or empty <see cref="ContentObjectHtmlBuilderOptions.Browsers" /> property.</exception>
	public static MediaItem toMediaItem(ContentObjectBo contentObject, int indexInAlbum, ContentObjectHtmlBuilderOptions moBuilderOptions, HttpServletRequest request) throws Exception{
		if (contentObject == null)
			throw new ArgumentNullException("contentObject");

		if (moBuilderOptions == null)
			throw new ArgumentNullException("moBuilderOptions");

		if (moBuilderOptions.Browsers == null || moBuilderOptions.Browsers.isEmpty())
			throw new ArgumentOutOfRangeException("moBuilderOptions.Browsers", "The Browsers array property must have at least one element.");

		moBuilderOptions.ContentObject = contentObject;

		boolean isBeingProcessed = ContentConversionQueue.getInstance().isWaitingInQueueOrProcessing(contentObject.getId(), ContentQueueItemConversionType.CreateOptimized);

		MediaItem moEntity = new MediaItem(
							contentObject.getId(),
							contentObject.getParent().getId(),
							contentObject.getParent().getTitle(),
							indexInAlbum,
							contentObject.getTitle(),
							getViews(moBuilderOptions).toArray(new DisplayObjectRest[0]),
							isBeingProcessed || (!StringUtils.isBlank(contentObject.getOptimized().getFileName())) && (contentObject.getOriginal().getFileName() != contentObject.getOptimized().getFileName()),
							!(contentObject instanceof ExternalContentObject),
							contentObject.getMimeType().getTypeCategory(),
							contentObject.getContentObjectType(),
							toMetaItems(contentObject.getMetadataItems().getVisibleItems(), contentObject, request),
							null//toApprovalItems(contentObject.getApprovalItems(), contentObject)
						);

		return moEntity;
	}

	//#endregion

	//#region Private Static Methods

	/// <summary>
	/// Creates the content object from the file specified in <paramref name="options" />.
	/// </summary>
	/// <param name="options">The options.</param>
	/// <returns>List{ActionResult}.</returns>
	/// <exception cref="EventLogs.CustomExceptions.GallerySecurityException">Thrown when user is not authorized to add a content object to the album.</exception>
	/// <remarks>This function can be invoked from a thread that does not have access to the current HTTP context (for example, when
	/// uploading ZIP files). Therefore, be sure nothing in this body (or the functions it calls) uses HttpContext.Current, or at 
	/// least check it for null first.</remarks>
	private static List<ActionResult> createContentObjectFromFile(AddContentObjectSettings options) throws GallerySecurityException	{
		String sourceFilePath = FilenameUtils.concat(AppSettings.getInstance().getTempUploadDirectory(), options.FileNameOnServer);

		try	{
			AlbumBo album = AlbumUtils.loadAlbumInstance(options.AlbumId, true, true);

			if (UserUtils.getSession() != null) //HttpContext.Current != null
				SecurityGuard.throwIfUserNotAuthorized(SecurityActions.AddContentObject, RoleUtils.getMDSRolesForUser(), album.getId(), album.getGalleryId(), UserUtils.isAuthenticated(), album.getIsPrivate(), album.getIsVirtualAlbum());
			else{
				// We are extracting files from a zip archive (we know this because this is the only scenario that happens on a background
				// thread where HttpContext.Current is null). Tweak the security check slightly to ensure the HTTP context isn't used.
				// The changes are still secure because options.CurrentUserName is assigned in the server's API method.
				SecurityGuard.throwIfUserNotAuthorized(SecurityActions.AddContentObject, RoleUtils.getMDSRolesForUser(options.CurrentUserName), album.getId(), album.getGalleryId(), !StringUtils.isBlank(options.CurrentUserName), album.getIsPrivate(), album.getIsVirtualAlbum());
			}

			String extension = FileMisc.getExt(options.FileName);
			if (extension != null && extension.equalsIgnoreCase(".zip" ) && options.ExtractZipFile){
				List<ActionResult> result = null;

				// Extract the files from the zipped file.
				ZipUtility zip = new ZipUtility(options.CurrentUserName, RoleUtils.getMDSRolesForUser(options.CurrentUserName));
				File fs = new File(sourceFilePath);
				result = zip.extractZipFile(fs, album, options.DiscardOriginalFile);

				album.sortAsync(true, options.CurrentUserName, true);

				return result;
			}else{
				String albumPhysicalPath = album.getFullPhysicalPathOnDisk();
				String filename = HelperFunctions.validateFileName(albumPhysicalPath, options.FileName);
				String filepath = FilenameUtils.concat(albumPhysicalPath, filename);

				moveFile(filepath, sourceFilePath);

				ActionResult result = createContentObject(filepath, album, options);

				album.sort(true, options.CurrentUserName);

				return Lists.newArrayList( result );
			}
		}catch (GallerySecurityException ex){
			throw ex;
		}catch (Exception ex){
			ex.printStackTrace();
			AppEventLogUtils.LogError(ex);
			return Lists.newArrayList(
						new ActionResult(
							ActionResultStatus.Error.toString(),
							options.FileName,
							"The event log may have additional details.",
							null)
					);
		}finally{
			try
			{
				// If the file still exists in the temp directory, delete it. Typically this happens when we've
				// extracted the contents of a zip file (since other files will have already been moved to the dest album.)
				FileMisc.deleteFile(sourceFilePath);
			}
			//catch (IOException ie) { } // Ignore an error; not a big deal if it continues to exist in the temp directory
			catch (SecurityException se) { } // Ignore an error; not a big deal if it continues to exist in the temp directory
		}
	}

	private static void moveFile(String filepath, String sourceFilePath) throws InterruptedException, IOException{
		// Move file to album. If IOException happens, wait 1 second and try again, up to 10 times.
		int counter = 0;
		final int maxTries = 10;

		while (true){
			try	{
				FileMisc.moveFileThrow(sourceFilePath, filepath);
				break;
			}catch (IOException ex)	{
				counter++;
				//ex.Data.add("CannotMoveFile", MessageFormat.format("This error occurred while trying to move file '{0}' to '{1}'. This error has occurred {2} times. The system will try again up to a maximum of {3} attempts.", sourceFilePath, filepath, counter, maxTries));
				AppEventLogUtils.LogError(ex);

				if (counter >= maxTries)
					throw ex;

				Thread.sleep(1000);
			}
		}
	}

	private static ActionResult createContentObject(String filePath, AlbumBo album, AddContentObjectSettings options) throws InvalidAlbumException, IOException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, InvalidGalleryException, WebException{
		ActionResult result = new ActionResult(FilenameUtils.getName(filePath));

		try	{
			ContentObjectBo go = CMUtils.createContentObjectInstance(filePath, album);
			saveContentObject(go, options.CurrentUserName);

			if (options.DiscardOriginalFile){
				go.deleteOriginalFile();
				saveContentObject(go);
			}

			result.Status = ActionResultStatus.Success.toString();
		}catch (UnsupportedContentObjectTypeException ex){
			try	{
				FileMisc.deleteFile(filePath);
			}catch (SecurityException se) { } // Ignore an error; the file will continue to exist in the destination album directory

			result.Status = ActionResultStatus.Error.toString();
			result.Message = ex.getMessage();
		}

		return result;
	}

	/// <summary>
	/// Determines whether the <paramref name="contentObject" /> has an optimized content object.
	/// </summary>
	/// <param name="contentObject">The content object.</param>
	/// <returns>
	///   <c>true</c> if it has an optimized content object; otherwise, <c>false</c>.
	/// </returns>
	/// <exception cref="System.ArgumentNullException"></exception>
	private static boolean hasOptimizedVersion(ContentObjectBo contentObject){
		if (contentObject == null)
			throw new ArgumentNullException("contentObject");

		if (contentObject.getContentObjectType() == ContentObjectType.Album)
			return false;

		boolean inQueue = ContentConversionQueue.getInstance().isWaitingInQueueOrProcessing(contentObject.getId(), ContentQueueItemConversionType.CreateOptimized);
		boolean hasOptFile = !StringUtils.isBlank(contentObject.getOptimized().getFileName());
		boolean optFileDifferentThanOriginal = (contentObject.getOptimized().getFileName() != contentObject.getOriginal().getFileName());

		return (inQueue || (hasOptFile && optFileDifferentThanOriginal));
	}

	/// <summary>
	/// Determines whether the <paramref name="contentObject" /> has an original content object.
	/// Generally, all content objects do have one and all albums do not.
	/// </summary>
	/// <param name="contentObject">The content object.</param>
	/// <returns>
	///   <c>true</c> if it has an original content object; otherwise, <c>false</c>.
	/// </returns>
	/// <exception cref="System.ArgumentNullException"></exception>
	private static boolean hasOriginalVersion(ContentObjectBo contentObject){
		if (contentObject == null)
			throw new ArgumentNullException("contentObject");

		return !(contentObject.getOriginal() instanceof NullDisplayObject);
	}

	/// <summary>
	/// Gets a collection of views corresponding to the content object and other specs in <paramref name="moBuilderOptions" />.
	/// </summary>
	/// <param name="moBuilderOptions">A set of properties to be used when building the output.</param>
	/// <returns>Returns a collection of <see cref="DisplayObject" /> instances.</returns>
	private static List<DisplayObjectRest> getViews(ContentObjectHtmlBuilderOptions moBuilderOptions) throws Exception{
		List<DisplayObjectRest> views = new ArrayList<DisplayObjectRest>(3);

		moBuilderOptions.DisplayType = DisplayObjectType.Thumbnail;

		ContentObjectHtmlBuilder moBuilder = new ContentObjectHtmlBuilder(moBuilderOptions);

		views.add(new DisplayObjectRest(
			DisplayObjectType.Thumbnail.value(),
			moBuilder.getMimeType().getTypeCategory().value(),
			moBuilder.generateHtml(),
			moBuilder.generateScript(),
			moBuilder.getWidth(),
			moBuilder.getHeight(),
			moBuilder.getContentObjectUrl()
		));

		if (hasOptimizedVersion(moBuilderOptions.ContentObject)){
			moBuilderOptions.DisplayType = DisplayObjectType.Optimized;

			moBuilder = new ContentObjectHtmlBuilder(moBuilderOptions);

			views.add(new DisplayObjectRest(
				DisplayObjectType.Optimized.value(),
				moBuilder.getMimeType().getTypeCategory().value(),
				moBuilder.generateHtml(),
				moBuilder.generateScript(),
				moBuilder.getWidth(),
				moBuilder.getHeight(),
				moBuilder.getContentObjectUrl()
			));
		}

		if (hasOriginalVersion(moBuilderOptions.ContentObject))	{
			moBuilderOptions.DisplayType = moBuilderOptions.ContentObject.getOriginal().getDisplayType(); // May be Original or External

			moBuilder = new ContentObjectHtmlBuilder(moBuilderOptions);

			views.add(new DisplayObjectRest(
				DisplayObjectType.Original.value(),
				moBuilder.getMimeType().getTypeCategory().value(),
				moBuilder.generateHtml(),
				moBuilder.generateScript(),
				moBuilder.getWidth(),
				moBuilder.getHeight(),
				moBuilder.getContentObjectUrl()
			));
		}

		return views;
	}
	
	public static DisplayObjectRest getView(ContentObjectHtmlBuilderOptions moBuilderOptions) throws Exception{
		DisplayObjectRest view = null;

		ContentObjectHtmlBuilder moBuilder = new ContentObjectHtmlBuilder(moBuilderOptions);

		if (hasOptimizedVersion(moBuilderOptions.ContentObject)){
			moBuilderOptions.DisplayType = DisplayObjectType.Optimized;

			moBuilder = new ContentObjectHtmlBuilder(moBuilderOptions);

			view = new DisplayObjectRest(
				DisplayObjectType.Optimized.value(),
				moBuilder.getMimeType().getTypeCategory().value(),
				moBuilder.generateHtml(),
				moBuilder.generateScript(),
				moBuilder.getWidth(),
				moBuilder.getHeight(),
				moBuilder.getContentObjectUrl()
			);
		} else if (hasOriginalVersion(moBuilderOptions.ContentObject))	{
			moBuilderOptions.DisplayType = moBuilderOptions.ContentObject.getOriginal().getDisplayType(); // May be Original or External

			moBuilder = new ContentObjectHtmlBuilder(moBuilderOptions);

			view = new DisplayObjectRest(
				DisplayObjectType.Original.value(),
				moBuilder.getMimeType().getTypeCategory().value(),
				moBuilder.generateHtml(),
				moBuilder.generateScript(),
				moBuilder.getWidth(),
				moBuilder.getHeight(),
				moBuilder.getContentObjectUrl()
			);
		} else {
			moBuilderOptions.DisplayType = DisplayObjectType.Thumbnail;

			view = new DisplayObjectRest(
				DisplayObjectType.Thumbnail.value(),
				moBuilder.getMimeType().getTypeCategory().value(),
				moBuilder.generateHtml(),
				moBuilder.generateScript(),
				moBuilder.getWidth(),
				moBuilder.getHeight(),
				moBuilder.getContentObjectUrl()
			);
		}

		return view;
	}
	
	public static ContentObjectHtmlBuilder getContentObjectHtmlBuilder(ContentObjectHtmlBuilderOptions moBuilderOptions) throws Exception{
    	if (hasOptimizedVersion(moBuilderOptions.ContentObject)){
			moBuilderOptions.DisplayType = DisplayObjectType.Optimized;
		} else if (hasOriginalVersion(moBuilderOptions.ContentObject))	{
			moBuilderOptions.DisplayType = moBuilderOptions.ContentObject.getOriginal().getDisplayType(); // May be Original or External
		} else {
			moBuilderOptions.DisplayType = DisplayObjectType.Thumbnail;
		}
    	
    	return new ContentObjectHtmlBuilder(moBuilderOptions);
	}

	/// <summary>
	/// Persists the current user's sort preference for the specified <paramref name="album" />. No action is taken if the 
	/// album is virtual. Anonymous user data is stored in session only; logged on users' data are permanently stored.
	/// </summary>
	/// <param name="album">The album whose sort preference is to be preserved.</param>
	/// <param name="sortByMetaName">Name of the metadata item to sort by.</param>
	/// <param name="sortAscending">Indicates the sort direction.</param>
	private static void persistUserSortPreference(AlbumBo album, MetadataItemName sortByMetaName, boolean sortAscending) throws JsonProcessingException{
		if (album.getIsVirtualAlbum())
			return;

		UserProfile profile = ProfileUtils.getProfile();

		AlbumProfile aProfile = profile.getAlbumProfiles().find(album.getId());

		if (aProfile == null){
			profile.getAlbumProfiles().addAlbumProfile(new AlbumProfile(album.getId(), sortByMetaName, sortAscending));
		}else{
			aProfile.SortByMetaName = sortByMetaName;
			aProfile.SortAscending = sortAscending;
		}

		ProfileUtils.saveProfile(profile);
	}

	/// <summary>
	/// Gets the title for the album that is appropriate for the specified <paramref name="rating" />.
	/// </summary>
	/// <param name="rating">The rating. Valid values include "highest", "lowest", "none", or a decimal.</param>
	/// <returns>System.String.</returns>
	private static String getRatedAlbumTitle(String rating)	{
		switch (rating.toLowerCase()){
			case "highest":
				return I18nUtils.getMessage("site.Highest_Rated_Title"); // "Highest rated items"
			case "lowest":
				return I18nUtils.getMessage("site.Lowest_Rated_Title"); // "Lowest rated items"
			case "none":
				return I18nUtils.getMessage("site.None_Rated_Title"); // "Items without a rating"
			default:
				return I18nUtils.getMessage("site.Rated_Title", rating); // "Items with a rating of 3"
		}
	}

	/// <summary>
	/// Gets the title for the album that is appropriate for the specified <paramref name="rating" />.
	/// </summary>
	/// <param name="rating">The approval status. Valid values include "approved", "rejected", "none", or a decimal.</param>
	/// <returns>System.String.</returns>
	private static String getApprovalAlbumTitle(ApprovalStatus approval){
		switch (approval)
		{
			case Approved:
				return I18nUtils.getMessage("site.Approved_Title"); // "Approved items"
			case Rejected:
				return I18nUtils.getMessage("site.Rejected_Title"); // "Rejected items"
			case NotSpecified:
				return I18nUtils.getMessage("site.NoAction_Title"); // "Items without approval"
			default:
				return I18nUtils.getMessage("site.Approval_Title", approval.toString()); // "Items with approval status of 3"
		}
	}

	/// <summary>
	/// Gets the title for the album that is appropriate for the specified <paramref name="rating" />.
	/// </summary>
	/// <param name="rating">The approval status. Valid values include "approved", "rejected", "none", or a decimal.</param>
	/// <returns>System.String.</returns>
	private static String getWithApprovalAlbumTitle(ApprovalStatus approval){
		switch (approval){
			case Approved:
				return StringUtils.join(I18nUtils.getMessage("site.Search_Concat"), I18nUtils.getMessage("search.Status_Approved")); // "Approved items"
			case Rejected:
				return StringUtils.join(I18nUtils.getMessage("site.Search_Concat"), I18nUtils.getMessage("search.Status_Rejected")); // "Rejected items"
			case NotSpecified:
				return StringUtils.join(I18nUtils.getMessage("site.Search_Concat"), I18nUtils.getMessage("search.Status_NoAction")); // "Items without approval"
			default:
				return "";
		}
	}

	//#endregion
}
