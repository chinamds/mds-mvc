package com.mds.cm.util;

import com.mds.cm.util.CMUtils;
import com.mds.cm.util.ContentObjectUtils;
import com.mds.cm.content.AlbumBo;
import com.mds.cm.content.ContentObjectBo;
import com.mds.cm.content.ContentObjectBoCollection;
import com.mds.cm.content.GallerySettings;
import com.mds.cm.exception.CannotMoveDirectoryException;
import com.mds.cm.exception.CannotTransferAlbumToNestedDirectoryException;
import com.mds.cm.exception.GallerySecurityException;
import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidContentObjectException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.cm.model.Album;
import com.mds.common.utils.Reflections;
import com.mds.core.ContentObjectType;
import com.mds.core.SecurityActions;
import com.mds.core.exception.ArgumentNullException;
import com.mds.core.exception.NotSupportedException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.core.exception.WebException;
import com.mds.sys.util.UserUtils;
import com.mds.util.HelperFunctions;
import com.mds.util.StringUtils;
import com.mds.util.Utils;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

public final class TransferObject {
	
		    
    //#region Properties
	public static TransferType getTransType(HttpServletRequest request){
		TransferType _transferType = TransferType.None;
		String qsValue = Utils.getQueryStringParameterString(request, "tt");
		if (qsValue.equals("move"))
			_transferType = TransferType.Move;
		else if (qsValue.equals("copy"))
			_transferType = TransferType.Copy;
		//else
		//  throw new MDS.EventLogs.CustomExceptions.UnexpectedQueryStringException();

		return _transferType;
	}

	private static TransferObjectState getTransObjectState(HttpServletRequest request){
		return getTransferObjectState(null, request);
	}

	//#endregion
	
	/*private static void showTreeview(HttpServletRequest request, String[] ids){
		// Find out if the objects we are transferring consist of only content objects, only albums, or both.
		// We use this knowledge to set the RequiredSecurityPermission property on the treeview user control
		// so that only albums where the user has permission are available for selection.
		boolean hasAlbums = false;
		boolean hasContentObjects = false;
		int securityActions = SecurityActions.NotSpecified.value();
		for (String id : ids){
			if (id.startsWith("a")){
				securityActions = ((securityActions == 0) ? SecurityActions.AddChildAlbum.value() : securityActions | SecurityActions.AddChildAlbum.value());
				hasAlbums = true;
			}
			if (id.startsWith("m")){
				securityActions = (((int)securityActions == 0) ? SecurityActions.AddContentObject.value() : securityActions | SecurityActions.AddContentObject.value());
				hasContentObjects = true;
			}
			
			if (hasAlbums && hasContentObjects)
				break;
		}

		request.setAttribute("requiredSecurityPermissions", securityActions);

		if (UserCanAdministerSite || UserCanAdministerGallery)
		{
			// Show all galleries the current user can administer. This allows them to move/copy objects between galleries.
			// We could have tried to show galleries where user has add album permission but that would have complicated things.
			// Simpler for the rule to be "Users can transfer to other galleries only where they are admins for both galleries."
			tvUC.RootAlbumPrefix = String.Concat(Resources.MDS.Site_Gallery_Text, " '{GalleryDescription}': ");
			tvUC.Galleries = UserController.GetGalleriesCurrentUserCanAdminister();
		}

		AlbumBo albumToSelect = this.GetAlbum();
		if (albumToSelect.IsVirtualAlbum || !IsUserAuthorized(SecurityActions.AddChildAlbum, albumToSelect))
		{
			albumToSelect = AlbumController.GetHighestLevelAlbumWithAddPermission(hasAlbums, hasContentObjects, GalleryId);
		}

		if (albumToSelect == null)
		{
			RedirectToAlbumViewPage("msg={0}", ((int)MessageType.CannotTransferObjectInsufficientPermission).ToString(CultureInfo.InvariantCulture));
		}

		tvUC.SelectedAlbumIds.Clear();
		tvUC.SelectedAlbumIds.Add(albumToSelect.Id);
	}*/
	

	/// <summary>
	/// Move or copy the objects. An exception is thrown if the user does not have the required permission or is 
	/// trying to transfer an object to itself.
	/// </summary>
	/// <exception cref="MDS.EventLogs.CustomExceptions.GallerySecurityException">Thrown when the logged on 
	/// user does not belong to a role that authorizes the moving or copying.</exception>
	/// <exception cref="MDS.EventLogs.CustomExceptions.CannotTransferAlbumToNestedDirectoryException">
	/// Thrown when the user tries to move or copy an album to one of its children albums.</exception>
	public static void transferObjects(String[] contentObjectIds, long gid, AlbumBo destAlbum, TransferType transferType) throws Exception	{
		//#region Get list of objects to move or copy

		// Convert the String array of IDs to integers. Also assign whether each is an album or content object.
		// (Determined by the first character of each ids String: a=album; m=content object)
		ContentObjectBoCollection objectsToMoveOrCopy = new ContentObjectBoCollection();
		for (int i = 0; i < contentObjectIds.length; i++){
			long id = StringUtils.toLong(contentObjectIds[i].substring(1));
			if (contentObjectIds[i].startsWith("a")){
				try	{
					AlbumBo album = AlbumUtils.loadAlbumInstance(id, false, true);

					objectsToMoveOrCopy.add(album);
				}
				catch (InvalidAlbumException ae) { /* Album may have been deleted by someone else, so just skip it. */ }
			}
			else if (contentObjectIds[i].startsWith("m"))	{
				// Grab a reference to the content object through the base page's album instead of using CMUtils.LoadContentObjectInstance().
				// This causes the base page's album object to have an accurate state of the child objects so that when we assign the
				// thumbnail object later in this page life cycle, it works correctly.
				ContentObjectBo contentObject = CMUtils.loadContentObjectInstance(id, true);
				//ContentObjectBo contentObject = this.GetAlbum().GetChildContentObjects(ContentObjectType.ContentObject).FindById(id, ContentObjectType.ContentObject);

				if (contentObject != null) /* Content object may have been deleted by someone else, so just skip it. */
				{
					objectsToMoveOrCopy.add(contentObject);
				}
			}else
				throw new WebException("Invalid object identifier in method transferObjects(). Expected: 'a' or 'm'. Found: " + contentObjectIds[i]);
		}

		//#endregion

		//#region Validate (throws exception if it doesn't validate)

		validateObjectsCanBeMovedOrCopied(objectsToMoveOrCopy, gid, destAlbum, transferType);

		//#endregion

		try	{
			//#region Move or copy each object

			for (ContentObjectBo contentObject : objectsToMoveOrCopy.values()){
				AlbumBo album = Reflections.as(contentObject, AlbumBo.class);
				if (album == null){
					if (transferType == TransferType.Move)
						moveContentObject(contentObject, destAlbum);
					else
						copyContentObject(contentObject, destAlbum);
				}else{
					if (transferType == TransferType.Move)
						moveAlbum(album, destAlbum);
					else
						copyAlbum(album, destAlbum);
				}
			}

			//#endregion

			// Resort the gallery objects in the album.
			CMUtils.loadAlbumInstance(destAlbum.getId(), false, true).sort(true, UserUtils.getLoginName());

		}catch(Exception ex){
			throw ex; 
		}
	}

	/// <summary>
	/// Throw exception if the specified albums and/or content objects cannot be moved or copied for any reason, 
	/// such as lack of user permission or trying to move/copy objects to itself.
	/// </summary>
	/// <param name="objectsToMoveOrCopy">The albums or content objects to move or copy.</param>
	/// <exception cref="MDS.EventLogs.CustomExceptions.GallerySecurityException">Thrown when the logged on 
	/// user does not belong to a role that authorizes the moving or copying.</exception>
	/// <exception cref="MDS.EventLogs.CustomExceptions.CannotTransferAlbumToNestedDirectoryException">
	/// Thrown when the user tries to move or copy an album to one of its children albums.</exception>
	private static void validateObjectsCanBeMovedOrCopied(ContentObjectBoCollection objectsToMoveOrCopy, long galleryId, AlbumBo destAlbum, TransferType transferType) throws GallerySecurityException, UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, CannotTransferAlbumToNestedDirectoryException, InvalidMDSRoleException{
		boolean movingOrCopyingAtLeastOneAlbum = false;
		boolean movingOrCopyingAtLeastOneContentObject = false;

		//#region Validate the albums and content objects we are moving or copying

		boolean securityCheckCompleteForAlbum = false;
		boolean securityCheckCompleteForContentObject = false;

		for (ContentObjectBo contentObject : objectsToMoveOrCopy.values()){
			if (contentObject instanceof AlbumBo)	{
				validateAlbumCanBeMovedOrCopied((AlbumBo)contentObject, destAlbum);

				if (!securityCheckCompleteForAlbum) // Only need to check albums once, since all albums belong to same parent.
				{
					validateSecurityForAlbumOrContentObject(contentObject, galleryId, SecurityActions.DeleteAlbum, transferType);
					securityCheckCompleteForAlbum = true;
				}

				movingOrCopyingAtLeastOneAlbum = true; // used below
			}else{
				// Make sure file type is enabled (external objects don't have files so we don't check them)
				if (contentObject.getContentObjectType() != ContentObjectType.External && !HelperFunctions.isFileAuthorizedForAddingToGallery(contentObject.getOriginal().getFileName(), contentObject.getGalleryId()))
				{
					throw new UnsupportedContentObjectTypeException(contentObject.getOriginal().getFileName());
				}

				if (!securityCheckCompleteForContentObject) // Only need to check content objects once, since they all belong to same parent.
				{
					validateSecurityForAlbumOrContentObject(contentObject.getParent(), galleryId, SecurityActions.DeleteContentObject, transferType);
					securityCheckCompleteForContentObject = true;
				}

				movingOrCopyingAtLeastOneContentObject = true; // used below
			}
		}

		//#endregion

		//#region Validate user has permission to add objects to destination album

		if (destAlbum.getGalleryId() == galleryId){
			if (CMUtils.loadGallerySetting(galleryId).getContentObjectPathIsReadOnly()){
				// Rebind treeview, making sure current album is re-selected.
				throw new GallerySecurityException("Cannot move or copy objects to a read only gallery");
			}
		} else {
			// User is transferring objects to another gallery. Make sure the user is an admin for the gallery
			// and that it is writeable.
			boolean isReadOnly = CMUtils.loadGallerySetting(destAlbum.getGalleryId()).getContentObjectPathIsReadOnly();
			boolean userIsNotAdmin = UserUtils.getGalleriesCurrentUserCanAdminister().stream().allMatch(g -> g.getGalleryId() != destAlbum.getGalleryId());

			if (isReadOnly || userIsNotAdmin){
				// Rebind treeview, making sure current album is re-selected.
				throw new GallerySecurityException("Cannot move or copy objects to a read only gallery");
			}
		}

		if (movingOrCopyingAtLeastOneAlbum && (!isUserAuthorized(SecurityActions.AddChildAlbum, destAlbum.getId(), destAlbum.getGalleryId(), destAlbum.getIsVirtualAlbum()))){
			throw new GallerySecurityException(StringUtils.format("User '{0}' does not have permission '{1}' for album ID {2}.", UserUtils.getLoginName(), SecurityActions.AddChildAlbum, destAlbum.getId()));
		}

		if (movingOrCopyingAtLeastOneContentObject && (!isUserAuthorized(SecurityActions.AddContentObject, destAlbum.getId(), destAlbum.getGalleryId(), destAlbum.getIsVirtualAlbum()))) {
			throw new GallerySecurityException(StringUtils.format("User '{0}' does not have permission '{1}' for album ID {2}.", UserUtils.getLoginName(), SecurityActions.AddContentObject, destAlbum.getId()));
		}

		//#endregion
	}

	/// <summary>
	/// Throw exception if user does not have permission to move the specified gallery object out of the current album.
	/// Moving an album or content object means we are essentially deleting it from the source album, so make sure user has 
	/// the appropriate delete permission for the current album. Does not validate user has permission to add objects to 
	/// destination album. Assumes each gallery object is contained in the current album as retrieved by MdsPage.GetAlbum().
	/// No validation is performed if we are copying since no special permissions are needed for copying (except a check 
	/// on the destination album, which we do elsewhere).
	/// </summary>
	/// <param name="contentObjectToMoveOrCopy">The album or content object to move or copy.</param>
	/// <param name="securityActions">The security permission to validate.</param>
	/// <exception cref="MDS.EventLogs.CustomExceptions.GallerySecurityException">Thrown when the logged on 
	/// user does not belong to a role that authorizes the specified security action.</exception>
	private static void validateSecurityForAlbumOrContentObject(ContentObjectBo contentObjectToMoveOrCopy, long galleryId, SecurityActions securityActions, TransferType transferType) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, GallerySecurityException, InvalidMDSRoleException	{
		if (transferType == TransferType.Move)	{
			if (!isUserAuthorized(securityActions, contentObjectToMoveOrCopy.getId(), galleryId, ((AlbumBo)contentObjectToMoveOrCopy).getIsVirtualAlbum()))	{
				throw new GallerySecurityException(StringUtils.format("User '{0}' does not have permission '{1}' for album ID {2}.", UserUtils.getLoginName(), securityActions, contentObjectToMoveOrCopy.getId()));
			}
		}
	}
	
	private static boolean isUserAuthorized(SecurityActions securityActions, long albumId, long galleryId, boolean isVirtualAlbum) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException{
		if (((securityActions == SecurityActions.ViewAlbumOrContentObject) || (securityActions == SecurityActions.ViewOriginalContentObject))
				&& (!Utils.isAuthenticated()))
			throw new NotSupportedException("Wrong method call: You must call the overload of GalleryView.isUserAuthorized that has the isPrivate parameter when the security action is ViewAlbumOrContentObject or ViewOriginalImage and the user is anonymous (not logged on).");

		return Utils.isUserAuthorized(securityActions, albumId, galleryId, false, isVirtualAlbum);
	}

	/// <summary>
	/// Throw exception if user is trying to move or copy an album to one of its children albums.
	/// </summary>
	/// <param name="albumToMoveOrCopy">The album to move or copy.</param>
	/// <exception cref="MDS.EventLogs.CustomExceptions.CannotTransferAlbumToNestedDirectoryException">
	/// Thrown when the user tries to move or copy an album to one of its children albums.</exception>
	private static void validateAlbumCanBeMovedOrCopied(AlbumBo albumToMoveOrCopy, AlbumBo destAlbum) throws CannotTransferAlbumToNestedDirectoryException	{
		AlbumBo albumParent = destAlbum;

		while (!albumParent.isRootAlbum()){
			if (albumParent.getId() == albumToMoveOrCopy.getId()){
				throw new CannotTransferAlbumToNestedDirectoryException();
			}
			albumParent = (AlbumBo)albumParent.getParent();
		}
	}

	private static void moveAlbum(AlbumBo albumToMove, AlbumBo destAlbum) throws CannotMoveDirectoryException, UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidContentObjectException, UnsupportedImageTypeException, InvalidGalleryException{
		try	{
			ContentObjectUtils.moveContentObject(albumToMove, destAlbum);
		}catch (IOException ex)	{
			throw new CannotMoveDirectoryException(StringUtils.format("Error while trying to move album {0}.", albumToMove.getId()), ex);
		}
	}

	// Note: Use the following version of UpdateRoleSecurityForMovedAlbum to enforce the rule that moved albums
	// must retain all role permissions it had in its original location, even inherited ones. This would go in 
	// the Album class and be invoked from the MoveTo method.
	///// <summary>
	///// Validate that the moved album retains its original role permissions. If the role is inherited from
	///// a parent album, then explicitly assign that role to the moved album. If the role is explicitly assigned to
	///// the moved album, then check to see if the destination album already includes that role. If it does, there is 
	///// no need to apply it twice, so remove the explicitly assigned role from the moved album and let it simply
	///// inherit the role.
	///// </summary>
	///// <param name="movedAlbum">The album that has just been moved to a new destination album.</param>
	//private void UpdateRoleSecurityForMovedAlbum(AlbumBo movedAlbum)
	//{
	//  foreach (IMDSSystemRole role in this.GetMDSSystemRoles())
	//  {
	//    if (role.AllAlbumIds.Contains(movedAlbum.Id))
	//    {
	//      // This role applies to this object.
	//      if (role.RootAlbumIds.Contains(movedAlbum.Id))
	//      {
	//        // The album is directly specified in this role, but if any of this album's new parents are explicitly
	//        // specified, then it is not necessary to specify it at this level. Iterate through all the album's new 
	//        // parent albums to see if this is the case.
	//        AlbumBo albumToCheck = (AlbumBo)movedAlbum.Parent;
	//        while (true)
	//        {
	//          if (role.RootAlbumIds.Contains(albumToCheck.Id))
	//          {
	//            role.RootAlbumIds.Remove(movedAlbum.Id);
	//            role.Save();
	//            break;
	//          }
	//          albumToCheck = (AlbumBo)albumToCheck.Parent;

	//          if (albumToCheck.IsRootAlbum)
	//            break;
	//        }
	//      }
	//      else
	//      {
	//        // The album inherits its role from a parent. If the new parent is not included in this role,
	//        // then add this object so that the role permissions carry over to the new location.
	//        if (!role.AllAlbumIds.Contains(this.DestinationAlbum.Id))
	//        {
	//          role.RootAlbumIds.Add(movedAlbum.Id);
	//          role.Save();
	//        }
	//      }
	//    }
	//  }
	//}

	private static void copyAlbum(AlbumBo albumToCopy, AlbumBo destAlbum) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, UnsupportedImageTypeException, InvalidGalleryException	{
		ContentObjectUtils.copyContentObject(albumToCopy, destAlbum);
	}

	// Note: Use the following version of UpdateRoleSecurityForCopiedAlbum to enforce the rule that copied albums
	// must retain all role permissions it had in its original location, even inherited ones. This would go in 
	// the Album class and be invoked from the CopyTo method.
	///// <summary>
	///// Validate that the copied album includes the same role permissions as the source album. If a role is
	///// already applied at the destination album, then there is no need to specify it twice, so just let the
	///// copied album inherit from its parent. This method does not remove any role permissions from the copied
	///// album; it only ensures that the copied album includes the role permissions as the original album. It 
	///// may end up with additional permissions that are inherited through the copied album's parent.
	///// </summary>
	///// <param name="sourceAlbumId">The ID of the album the copy was made from.</param>
	///// <param name="copiedAlbum">The album that was just copied.</param>
	//private void UpdateRoleSecurityForCopiedAlbum(int sourceAlbumId, ContentObjectBo copiedAlbum)
	//{
	//  foreach (IMDSSystemRole role in this.GetMDSSystemRoles())
	//  {
	//    if (role.AllAlbumIds.Contains(sourceAlbumId))
	//    {
	//      // This role applies to the original album. Apply it to the copied album also, unless it will already inherit
	//      // this role from the destination album.
	//      if (!role.AllAlbumIds.Contains(this.DestinationAlbum.Id))
	//      {
	//        role.RootAlbumIds.Add(copiedAlbum.Id);
	//        role.Save();
	//      }
	//    }
	//  }
	//}

	private static void moveContentObject(ContentObjectBo contentObjectToMove, AlbumBo destAlbum) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidContentObjectException, UnsupportedImageTypeException, IOException, InvalidGalleryException	{
		ContentObjectUtils.moveContentObject(contentObjectToMove, destAlbum);
	}

	private static void copyContentObject(ContentObjectBo contentObjectToCopy, AlbumBo destAlbum) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, UnsupportedImageTypeException, InvalidGalleryException
	{
		ContentObjectUtils.copyContentObject(contentObjectToCopy, destAlbum);
	}


	/// <summary>
	/// Determine the current state of this page.
	/// </summary>
	/// <returns>Returns the current state of this page.</returns>
	public static TransferObjectState getTransferObjectState(String showNextPage, HttpServletRequest request){
		// Is the album ID specified on the query String? Notice that we check for the presence of the "aid"
		// parameter instead of checking to see if the value is greater than 0. This is because we might get to
		// this page from a page showing a virtual album, in which case the aid parameter will be passed as
		// int.MinValue.
		boolean isAlbumIdSpecified = Utils.isQueryStringParameterPresent(request, "aid");

		long contentObjectId = Utils.getQueryStringParameterInt64(request, "moid");
		Boolean skipStep1 = Utils.getQueryStringParameterBoolean(request, "skipstep1");

		if (skipStep1 == null)
			skipStep1 = false;

		TransferObjectState transferState = TransferObjectState.None;

		if ((isAlbumIdSpecified) && (skipStep1 == false)){
			// Not postback, must allow user to select objects to copy/move (step 1)
			if (getTransType(request) == TransferType.Copy)
				transferState = TransferObjectState.ObjectsCopyStep1;
			else
				transferState = TransferObjectState.ObjectsMoveStep1;
		}else if ((isAlbumIdSpecified) && (getTransType(request) == TransferType.Move) && (skipStep1== true)){
			// Not postback, user selected 'Move album' link. We'll display the treeview to allow the user to select the destination album. 
			transferState = TransferObjectState.AlbumMoveStep2;
		}else if ((isAlbumIdSpecified) && (getTransType(request) == TransferType.Copy) && (skipStep1 == true)){
			// Not postback, user selected 'Copy album' link. We'll display the treeview to allow the user to select the destination album. 
			transferState = TransferObjectState.AlbumCopyStep2;
		}
		else if ((contentObjectId > 0))	{
			// Not postback, user already selected a content object to copy/move from a different web page, 
			// We'll display the treeview to allow the user to select the destination album. 
			if (getTransType(request) == TransferType.Copy)
				transferState = TransferObjectState.ContentObjectCopyStep2;
			else
				transferState = TransferObjectState.ContentObjectMoveStep2;
		}else if (showNextPage != null && showNextPage.equals("1"))	{
			// This is a postback where we want to show the treeview control, thus allowing the user to select
			// the destination album to copy/move the objects to.
			if (getTransType(request) == TransferType.Copy)
				transferState = TransferObjectState.ObjectsCopyStep2;
			else
				transferState = TransferObjectState.ObjectsMoveStep2;
		}else if (showNextPage != null && showNextPage.equals("0")){
			// This is a postback where we do not want to show the treeview control, which can only mean the user has 
			// finished selecting the destination album and is ready to transfer the objects.
			transferState = TransferObjectState.ReadyToTransfer;
		}

		return transferState;
	}

	//#endregion
}
