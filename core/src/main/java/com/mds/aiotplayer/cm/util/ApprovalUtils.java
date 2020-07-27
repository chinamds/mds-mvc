package com.mds.aiotplayer.cm.util;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;

import com.mds.aiotplayer.cm.content.ContentObjectApproval;
import com.mds.aiotplayer.cm.content.ContentObjectApprovalCollection;
import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.sys.util.SecurityGuard;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.rest.ApprovalItem;
import com.mds.aiotplayer.cm.rest.ContentApproval;
import com.mds.aiotplayer.cm.rest.ContentItem;
import com.mds.aiotplayer.core.ApprovalAction;
import com.mds.aiotplayer.core.ApprovalStatus;
import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.StringUtils;

/// <summary>
/// Contains functionality for interacting with metadata.
/// </summary>
public final class ApprovalUtils{
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
	/// <see cref="ApprovalItem.ContentId" />.</param>
	/// <param name="goType">Type of the content object. It is assigned to 
	/// <see cref="ApprovalItem.GTypeId" />.</param>
	/// <returns>An instance of <see cref="ApprovalItem" />.</returns>
	/// <exception cref="GallerySecurityException">Thrown when user does not have permission to
	/// view the requested item.</exception>
	/// <exception cref="InvalidContentObjectException">Thrown when the requested meta item or its
	/// associated content object does not exist in the data store.</exception>
	/// <exception cref="InvalidAlbumException">Thrown when the album associated with the
	/// meta item does not exist in the data store.</exception>
	public static ApprovalItem Get(long id, long contentObjectId, ContentObjectType goType) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, GallerySecurityException, InvalidMDSRoleException, InvalidGalleryException	{
		// Security check: Make sure user has permission to view item
		ContentObjectBo go;
		if (goType == ContentObjectType.Album){
			go = CMUtils.loadAlbumInstance(contentObjectId, false);
			SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), go.getId(), go.getGalleryId(), UserUtils.isAuthenticated(), go.getIsPrivate(), ((AlbumBo)go).getIsVirtualAlbum());
		}else{
			go = CMUtils.loadContentObjectInstance(contentObjectId);
			SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), go.getParent().getId(), go.getGalleryId(), UserUtils.isAuthenticated(), go.getParent().getIsPrivate(), ((AlbumBo)go.getParent()).getIsVirtualAlbum());
		}
		
		ContentObjectApproval ad = CMUtils.loadContentObjectApprovalItem(id, go);
		if (ad == null)
			throw new InvalidContentObjectException(MessageFormat.format("No metadata item with ID {0} could be found in the data store.", id));

		return new ApprovalItem(
						 ad.getId(),
						 contentObjectId,
						 goType,
						 ad.getApproveBy(),
						 ad.getSeq(),
						 ad.getApprovalAction(),
						 ad.getApproveDate()
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
	/// <returns>Returns an instance of <see cref="ContentObjectApprovalItemCollection" />.</returns>
	/// <exception cref="GallerySecurityException">Thrown when user does not have permission to
	/// view the requested item.</exception>
	/// <exception cref="InvalidContentObjectException">Thrown when the requested meta item or its
	/// associated content object does not exist in the data store.</exception>
	/// <exception cref="InvalidAlbumException">Thrown when the album associated with the
	/// meta item does not exist in the data store.</exception>
	public static ContentObjectApproval get(long contentObjectId, ContentObjectType goType) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, GallerySecurityException, InvalidMDSRoleException, InvalidGalleryException	{
		// Security check: Make sure user has permission to view item
		ContentObjectBo go;
		if (goType == ContentObjectType.Album){
			go = CMUtils.loadAlbumInstance(contentObjectId, false);
			SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), go.getId(), go.getGalleryId(), UserUtils.isAuthenticated(), go.getIsPrivate(), ((AlbumBo)go).getIsVirtualAlbum());
		}else{
			go = CMUtils.loadContentObjectInstance(contentObjectId);
			SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), go.getParent().getId(), go.getGalleryId(), UserUtils.isAuthenticated(), go.getParent().getIsPrivate(), ((AlbumBo)go.getParent()).getIsVirtualAlbum());
		}

		return getApprovalStatusCollection(contentObjectId, goType).getLatestApprovalItem();
	}

	/// <summary>
	/// Gets the meta items for specified <paramref name="contentItems" />, merging metadata
	/// when necessary. Specifically, tags and people tags are merged and updated with a count.
	/// Example: "Animal (3), Dog (2), Cat (1)" indicates three of the gallery items have the 
	/// 'Animal' tag, two have the 'Dog' tag, and one has the 'Cat' tag. Guaranteed to not 
	/// return null.
	/// </summary>
	/// <param name="contentItems">The gallery items for which to retrieve metadata.</param>
	/// <returns>Returns a collection of <see cref="ApprovalItem" /> items.</returns>
	/// <exception cref="InvalidAlbumException">Thrown when the requested album does not exist.</exception>
	/// <exception cref="InvalidContentObjectException">Thrown when the requested content object does not exist.</exception>
	public static ApprovalItem[] getApprovalItemsForContentItems(ContentItem[] contentItems) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, GallerySecurityException, InvalidMDSRoleException, InvalidGalleryException{
		if (contentItems == null || contentItems.length == 0)
			return new ApprovalItem[] { };
		
		// Get the approval item for the last item
		ContentItem lastGi = contentItems[contentItems.length - 1];
		ApprovalItem[] approvalItem = getApprovalItems(lastGi);

		return approvalItem;
	}

	/// <summary>
	/// Persists the metadata item to the data store.  Verifies user has permission to edit item,
	/// throwing <see cref="GallerySecurityException" /> if authorization is denied. 
	/// The value is validated before saving, and may be altered to conform to business rules, 
	/// such as removing HTML tags and javascript. The <paramref name="approvalItem" /> is returned,
	/// with the validated value assigned to the <see cref="ApprovalItem.Value" /> property.
	/// 
	/// The current implementation requires that an existing item exist in the data store and only 
	/// stores the contents of the <see cref="ApprovalItem.Value" /> property.
	/// </summary>
	/// <param name="approvalItem">An instance of <see cref="ApprovalItem" /> to persist to the data
	/// store.</param>
	/// <returns>An instance of <see cref="ApprovalItem" />.</returns>
	/// <exception cref="InvalidContentObjectException">Thrown when the requested meta item or its
	/// associated content object does not exist in the data store.</exception>
	/// <exception cref="GallerySecurityException">Thrown when user does not have permission to
	/// edit the requested item.</exception>
	public static ApprovalItem save(ApprovalItem approvalItem) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, GallerySecurityException, InvalidMDSRoleException, InvalidGalleryException{
		// Security check: Make sure user has permission to edit item
		ContentObjectBo go;
		if (approvalItem.GTypeId == ContentObjectType.Album.getValue()){
			go = CMUtils.loadAlbumInstance(approvalItem.ContentId, false);
			SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ApproveContentObject, RoleUtils.getMDSRolesForUser(), go.getId(), go.getGalleryId(), UserUtils.isAuthenticated(), go.getIsPrivate(), ((AlbumBo)go).getIsVirtualAlbum());
		}else{
			go = CMUtils.loadContentObjectInstance(approvalItem.ContentId);
			SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ApproveContentObject, RoleUtils.getMDSRolesForUser(), go.getParent().getId(), go.getGalleryId(), UserUtils.isAuthenticated(), go.getParent().getIsPrivate(), ((AlbumBo)go.getParent()).getIsVirtualAlbum());
		}
		
		ContentObjectApproval md = CMUtils.loadContentObjectApprovalItem(approvalItem.Id, go);
		if (md == null)
			throw new InvalidContentObjectException(MessageFormat.format("No approval item with ID {0} could be found in the data store.", approvalItem.Id));

		/*ApprovalStatus prevValue = md.ApprovalStatus;
		md.ApprovalStatus = (ApprovalStatus)approvalItem.ApprovalStatus;
		if (md.ApprovalStatus != prevValue)	{
			CMUtils.saveApprovalStatusItem(md);

			//HelperFunctions.purgeCache();
		}*/

		return approvalItem;
	}


	/// <summary>
	/// Updates the specified gallery items with the specified metadata value. The property <see cref="ContentApproval.ActionResult" />
	/// of <paramref name="contentItemApproval" /> is assigned when a validation error occurs, but remains null for a successful operation.
	/// </summary>
	/// <param name="contentItemApproval">An object containing the metadata instance to use as the source
	/// and the gallery items to be updated</param>
	/// <exception cref="GallerySecurityException">Thrown when the current user does not have
	/// permission to edit one or more of the specified gallery items.</exception>
	public static void saveContentApproval(ContentApproval contentItemApproval) throws UnsupportedContentObjectTypeException, GallerySecurityException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidGalleryException{
		persistContentApproval(contentItemApproval);
	}

	/// <summary>
	/// Gets a value indicating whether the logged-on user has edit permission for all of the <paramref name="contentItems" />.
	/// </summary>
	/// <param name="contentItems">A collection of <see cref="ContentItem" /> instances.</param>
	/// <returns><c>true</c> if the current user can edit the items; <c>false</c> otherwise.</returns>
	public static boolean canUserEditAllItems(Collection<ContentItem> contentItems) throws UnsupportedContentObjectTypeException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidGalleryException{
		try{
			for (ContentItem contentItem : contentItems){
				getContentObjectAndVerifyEditPermission(contentItem);
			}

			return true;
		}catch (GallerySecurityException ex){
			return false;
		}
	}

	//#endregion

	//#region Functions
	
	/// <overloads>
	/// Gets the metadata collection for the specified criteria. Guaranteed to not return null.
	/// </overloads>
	/// <summary>
	/// Gets the metadata collection for the specified <paramref name="contentItem" />.
	/// </summary>
	/// <param name="contentItem">The gallery item representing either an album or a content object.</param>
	/// <returns>Returns an instance of <see cref="ContentObjectApprovalItemCollection" />.</returns>
	/// <exception cref="InvalidAlbumException">Thrown when the requested album does not exist.</exception>
	/// <exception cref="InvalidContentObjectException">Thrown when the requested content object does not exist.</exception>
	private static ContentObjectApprovalCollection getApprovalStatusCollection(ContentItem contentItem) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException	{
		return getApprovalStatusCollection(contentItem.getId(), ContentObjectType.getContentObjectType(contentItem.ItemType));
	}

	/// <summary>
	/// Gets the metadata collection for the specified <paramref name="contentObjectId" /> and
	/// <paramref name="goType" />.
	/// </summary>
	/// <param name="contentObjectId">The ID for either an album or a content object.</param>
	/// <param name="goType">The type of content object.</param>
	/// <returns>Returns an instance of <see cref="ContentObjectApprovalItemCollection" />.</returns>
	/// <exception cref="InvalidAlbumException">Thrown when the requested album does not exist.</exception>
	/// <exception cref="InvalidContentObjectException">Thrown when the requested content object does not exist.</exception>
	private static ContentObjectApprovalCollection getApprovalStatusCollection(long contentObjectId, ContentObjectType goType) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException	{
		if (goType == ContentObjectType.Album)
			return CMUtils.loadAlbumInstance(contentObjectId, false).getApprovalItems();
		else
			return CMUtils.loadContentObjectInstance(contentObjectId).getApprovalItems();
	}

	private static ApprovalItem[] getApprovalItems(ContentItem lastGi) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, GallerySecurityException, InvalidMDSRoleException, InvalidGalleryException	{
		ApprovalItem[] approval;
		if (lastGi.ItemType == ContentObjectType.Album.getValue())
			approval = AlbumUtils.getApprovalItemsForAlbum(lastGi.getId());
		else
			approval = ContentObjectUtils.getApprovalItemsForContentObject(lastGi.getId());

		return approval;
	}

	/// <summary>
	/// Updates the gallery items with the specified metadata value. The property <see cref="ContentApproval.ActionResult" />
	/// of <paramref name="contentItemApproval" /> is assigned when a validation error occurs, but remains null for a successful operation.
	/// </summary>
	/// <param name="contentItemApproval">An object containing the metadata instance to use as the source
	/// and the gallery items to be updated.</param>
	/// <exception cref="GallerySecurityException">Thrown when the current user does not have
	/// permission to edit one or more of the specified gallery items.</exception>
	/// <exception cref="WebException">Thrown when the metadata instance is a tag-style item.</exception>
	private static void persistContentApproval(ContentApproval contentItemApproval) throws UnsupportedContentObjectTypeException, GallerySecurityException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidGalleryException	{
		for (ContentItem contentItem : contentItemApproval.ContentItems){
			ContentObjectBo contentObject = getContentObjectAndVerifyEditPermission(contentItem);
			if (contentObject == null)
				continue;

			ApprovalAction approvalAction = ApprovalAction.Approve;
			ContentObjectApproval approval;
			if ((approval = contentObject.getApprovalItems().tryGetApprovalItem(UserUtils.getLoginName())) != null)	{
				Date currentTimestamp = DateUtils.Now();

				approval.setApproveBy(UserUtils.getLoginName());
				//approval.setUserCode = UserUtils.getLoginName();
				approval.setApproveDate(currentTimestamp);
				approval.setApprovalAction(approvalAction);

				CMUtils.saveContentObjectApprovalItem(approval);
			}
			else
			{
				ContentObjectApprovalCollection approvalItems = CMUtils.createApprovalCollection();
				approvalItems.add(CMUtils.createApprovalItem(Long.MIN_VALUE, contentObject, UserUtils.getLoginName(), 1
						, approvalAction, DateUtils.Now(), true));
				contentObject.addApproval(approvalItems);

				CMUtils.saveContentObjectApprovalItem(approval);
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
	private static ContentObjectBo getContentObjectAndVerifyEditPermission(ContentItem contentItem) throws UnsupportedContentObjectTypeException, GallerySecurityException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidGalleryException	{
		ContentObjectBo contentObject = null;
		try	{
			if (contentItem.ItemType == ContentObjectType.Album.getValue())	{
				contentObject = CMUtils.loadAlbumInstance(contentItem.Id, false);
				SecurityGuard.throwIfUserNotAuthorized(SecurityActions.EditAlbum, RoleUtils.getMDSRolesForUser(), contentObject.getId(), contentObject.getGalleryId(), UserUtils.isAuthenticated(), contentObject.getIsPrivate(), ((AlbumBo)contentObject).getIsVirtualAlbum());
			}else{
				contentObject = CMUtils.loadContentObjectInstance(contentItem.Id);
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
	private static ContentObjectBo getContentObjectAndVerifyApprovalPermission(ContentItem contentItem) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, GallerySecurityException, InvalidMDSRoleException, InvalidGalleryException	{
		ContentObjectBo contentObject = null;
		try	{
			if (contentItem.ItemType == ContentObjectType.Album.getValue())	{
				contentObject = CMUtils.loadAlbumInstance(contentItem.Id, false);
				SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ApproveContentObject, RoleUtils.getMDSRolesForUser(), contentObject.getId(), contentObject.getGalleryId(), UserUtils.isAuthenticated(), contentObject.getIsPrivate(), ((AlbumBo)contentObject).getIsVirtualAlbum());
			}else{
				contentObject = CMUtils.loadContentObjectInstance(contentItem.Id);
				SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ApproveContentObject, RoleUtils.getMDSRolesForUser(), contentObject.getParent().getId(), contentObject.getGalleryId(), UserUtils.isAuthenticated(), contentObject.getParent().getIsPrivate(), ((AlbumBo)contentObject.getParent()).getIsVirtualAlbum());
			}

			if (!UserUtils.isAuthenticated()){
				// We have an anonymous user attempting to rate an item, but the AllowAnonymousRating setting is false.
				contentObject = null;
				throw new GallerySecurityException(MessageFormat.format("An anonymous user is attempting to approve a content object ({0} ID {1}), but the gallery is configured to not allow approval by anonymous users. The request is denied.", contentItem.ItemType, contentObject.getId()));
			}
		}catch (InvalidAlbumException ex){
			AppEventLogUtils.LogError(ex);
		}catch (InvalidContentObjectException ex){
			AppEventLogUtils.LogError(ex);
		}

		return contentObject;
	}
		

	//#endregion
}