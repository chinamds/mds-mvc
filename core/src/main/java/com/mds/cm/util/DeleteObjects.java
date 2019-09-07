package com.mds.cm.util;

import com.mds.cm.util.CMUtils;
import com.mds.cm.util.ContentObjectUtils;
import com.mds.cm.content.AlbumBo;
import com.mds.cm.content.ContentObjectBo;
import com.mds.cm.content.ContentObjectBoCollection;
import com.mds.cm.content.GallerySettings;
import com.mds.cm.exception.CannotDeleteAlbumException;
import com.mds.cm.exception.CannotMoveDirectoryException;
import com.mds.cm.exception.CannotTransferAlbumToNestedDirectoryException;
import com.mds.cm.exception.GallerySecurityException;
import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidContentObjectException;
import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.cm.exception.InvalidGalleryException;
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
/// <summary>
/// A page-like user control that handles the Delete objects task.
/// </summary>
public final class DeleteObjects{
	public static boolean deleteObjects(String[] selectedItems, boolean userCanDeleteContentObject, boolean userCanDeleteChildAlbum, boolean chkDeleteDbRecordsOnly) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException, IOException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, WebException, CannotDeleteAlbumException {
		// Convert the String array of IDs to integers. Also assign whether each is an album or content object.
		// (Determined by the first character of each id's String: a=album; m=content object)
		for(String selectedItem : selectedItems){
			long id = StringUtils.toLong(selectedItem.substring(1));// 'a' or 'm'

			if (selectedItem.startsWith("m")){
				ContentObjectBo go;
				try
				{
					go = CMUtils.loadContentObjectInstance(id);
				}
				catch (InvalidContentObjectException ce)
				{
					continue; // Content object may have been deleted by someone else, so just skip it.
				}

				if (userCanDeleteContentObject)
				{
					if (chkDeleteDbRecordsOnly)
					{
						go.deleteFromGallery();
					}
					else
					{
						go.delete();
					}
				}
			}

			if (selectedItem.startsWith("a")){
				AlbumBo album;
				try
				{
					album = AlbumUtils.loadAlbumInstance(id, false);
				}
				catch (InvalidAlbumException ae)
				{
					continue; // Album may have been deleted by someone else, so just skip it.
				}

				if (userCanDeleteChildAlbum)
				{
					AlbumUtils.deleteAlbum(album, !chkDeleteDbRecordsOnly);
				}
			}
		}

		//HelperFunctions.purgeCache();

		return true;
	}

	public static boolean validateBeforeObjectDeletion(String[] idsToDelete) throws UnsupportedContentObjectTypeException, CannotDeleteAlbumException, GallerySecurityException, InvalidAlbumException, InvalidMDSRoleException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException, IOException, WebException{
		// Before we delete any objects, make sure we can safely do so. Currently, we only check albums.
		for (String idString : idsToDelete)	{
			// Each idString is an 'a' (album) or 'm' (content object) concatenated with the ID. Ex: "a230", "m20947"
			long id = StringUtils.toLong(idString.substring(1));

			if (idString.startsWith("a")){
				// Step 1: Load album to delete. If it doesn't exist, just continue (maybe someone else has just deleted it)
				AlbumBo albumToDelete;
				try	{
					albumToDelete = AlbumUtils.loadAlbumInstance(id, false);
				}catch (InvalidAlbumException ae) { 
					continue; 
				}				
				
				// Step 2: Run the validation. If it fails, inform user.
				AlbumUtils.validateBeforeAlbumDelete(albumToDelete);
			}
		}

		return true;
	}

	//#endregion
}