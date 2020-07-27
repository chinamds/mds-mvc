package com.mds.aiotplayer.cm.content;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.cm.content.nullobjects.NullContentObject;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.core.ContentObjectDeleteValidationFailureReason;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.cm.util.CMUtils;

/// <summary>
/// Verifies an album can be safely deleted.
/// </summary>
public class AlbumDeleteValidator{
	private AlbumBo albumToDelete;
	private boolean validationFailure;
	private ContentObjectDeleteValidationFailureReason validationFailureReason;

	/// <summary>
	/// Gets a value indicating whether the album can be deleted. Call <see cref="Validate" /> before checking this property.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the album can be deleted; otherwise, <c>false</c>.
	/// </value>
	public boolean canBeDeleted(){
		return !this.validationFailure; 
	}

	/// <summary>
	/// Gets the message describing why an album cannot be deleted. Call <see cref="Validate" /> before checking this property.
	/// Returns null before the Validate method is invoked and when <see cref="CanBeDeleted" /> is <c>true</c>.
	/// </summary>
	/// <value>The message.</value>
	public ContentObjectDeleteValidationFailureReason getValidationFailureReason(){
		return this.validationFailureReason; 
	}

	/// <summary>
	/// Initializes a new instance of the <see cref="AlbumDeleteValidator"/> class.
	/// </summary>
	/// <param name="albumToDelete">The album to delete.</param>
	public AlbumDeleteValidator(AlbumBo albumToDelete){
		this.albumToDelete = albumToDelete;
	}

	/// <summary>
	/// Verifies that the album passed into the constructor can be safely deleted. This method causes the <see cref="CanBeDeleted" />
	/// and <see cref="ValidationFailureReason" /> properties to be set.
	/// </summary>
	public void validate() throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		checkForUserAlbumConflict();

		checkForDefaultContentObjectConflict();
	}

	/// <summary>
	/// Checks the album to be deleted to see if it is specified as the user album container or if one of its children is the user
	/// album container. If user albums are disabled, no action is taken. If a problem is found, the member variables are updated
	/// with details.
	/// </summary>
	private void checkForUserAlbumConflict() throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		GallerySettings gallerySetting = CMUtils.loadGallerySetting(this.albumToDelete.getGalleryId());

		if (!gallerySetting.getEnableUserAlbum())
			return;

		ContentObjectBo userAlbumParent;
		try	{
			userAlbumParent = CMUtils.loadAlbumInstance(gallerySetting.getUserAlbumParentAlbumId(), false);
		}catch (InvalidAlbumException | UnsupportedContentObjectTypeException | UnsupportedImageTypeException | InvalidContentObjectException | InvalidGalleryException ex){
			// User album doesn't exist. Record the error and then return because there is no problem with deleting the current album.
			//String galleryDescription = CMUtils.LoadGallery(gallerySetting.GalleryId).Description;
			//String msg = MessageFormat.format(CultureInfo.CurrentCulture, Resources.error.User_Album_Parent_Invalid_Ex_Msg, galleryDescription, this.albumToDelete.Id);
			//EventLogController.RecordError(new WebException(msg, ex), AppSetting.Instance, this.albumToDelete.GalleryId, CMUtils.LoadGallerySettings());
			return;
		}

		// Test #1: Are we trying to delete the album that is specified as the user album parent album?
		if (userAlbumParent.getId() == this.albumToDelete.getId()){
			this.validationFailure = true;
			this.validationFailureReason = ContentObjectDeleteValidationFailureReason.AlbumSpecifiedAsUserAlbumContainer;
			return;
		}

		// Test #2: Does the user album parent album exist somewhere below the album we want to delete?
		ContentObjectBo albumParent = userAlbumParent.getParent();
		while (!(albumParent instanceof NullContentObject))	{
			if (albumParent.getId() == this.albumToDelete.getId()){
				this.validationFailure = true;
				this.validationFailureReason = ContentObjectDeleteValidationFailureReason.AlbumContainsUserAlbumContainer;
				return;
			}
			albumParent = albumParent.getParent();
		}
	}

	/// <summary>
	/// If a default content object or album is specified, make sure it isn't contained in the album we want to delete.
	/// If a problem is found, the member variables are updated with details.
	/// </summary>
	private void checkForDefaultContentObjectConflict()	{
		if (this.validationFailureReason != ContentObjectDeleteValidationFailureReason.NotSet)	{
			return; // We have already identified a validation failure, so just return so we don't overwrite it.
		}

		// Get a list of gallery control settings we need to test.
		List<GalleryControlSettings> gcsList = new ArrayList<GalleryControlSettings>();
		List<GalleryControlSettings> gcsAll = CMUtils.loadGalleryControlSettings();
		for (GalleryControlSettings gcs : gcsAll){
			if ((gcs.getGalleryId() == this.albumToDelete.getGalleryId()) && (gcs.getAlbumId() != null || gcs.getContentObjectId() != null)){
				gcsList.add(gcs);
			}
		}

		if (!gcsList.isEmpty())	{
			return; // Return, since there is nothing to validate
		}

		for (GalleryControlSettings gcs : gcsList){
			checkForDefaultContentObjectAlbumConflict(gcs);

			if (this.validationFailureReason == ContentObjectDeleteValidationFailureReason.NotSet){
				// We only bother checking the next item if we haven't yet encountered a validation failure.
				checkForDefaultContentObjectContentObjectConflict(gcs);
			}
		}
	}

	private void checkForDefaultContentObjectAlbumConflict(GalleryControlSettings gcs){
		if (gcs.getAlbumId() != null){
			return; // No default album is specified, so there is nothing to test.
		}

		// Test #1: If an album is specified as the default gallery object, is it the album we are deleting?
		if (gcs.getAlbumId() == this.albumToDelete.getId()){
			this.validationFailure = true;
			this.validationFailureReason = ContentObjectDeleteValidationFailureReason.AlbumSpecifiedAsDefaultContentObject;
			return;
		}

		// Test #2: If an album is specified as the default gallery object, is it contained within the hierarchy of
		// the album we are deleting?
		ContentObjectBo defaultGalleryAlbum = getDefaultGalleryAlbum(gcs);

		if (defaultGalleryAlbum != null){
			ContentObjectBo albumParent = defaultGalleryAlbum.getParent();
			while (!(albumParent instanceof NullContentObject))	{
				if (albumParent.getId() == this.albumToDelete.getId()){
					this.validationFailure = true;
					this.validationFailureReason = ContentObjectDeleteValidationFailureReason.AlbumContainsDefaultContentObjectAlbum;
					return;
				}
				albumParent = albumParent.getParent();
			}
		}
	}

	private void checkForDefaultContentObjectContentObjectConflict(GalleryControlSettings gcs){
		if (gcs.getContentObjectId() != null)	{
			return; // No default content object is specified, so there is nothing to test.
		}

		// If a content object is specified as the default gallery object, is it contained within the hierarchy of
		// the album we are deleting?
		ContentObjectBo defaultGalleryContentObject = getDefaultGalleryContentObject(gcs);

		if (defaultGalleryContentObject != null){
			ContentObjectBo albumParent = defaultGalleryContentObject.getParent();
			while (!(albumParent instanceof NullContentObject))	{
				if (albumParent.getId() == this.albumToDelete.getId()){
					this.validationFailure = true;
					this.validationFailureReason = ContentObjectDeleteValidationFailureReason.AlbumContainsDefaultContentObjectContentObject;
					return;
				}
				albumParent = albumParent.getParent();
			}
		}
	}

	private ContentObjectBo getDefaultGalleryContentObject(GalleryControlSettings gcs){
		if (gcs.getContentObjectId() != null){
			return null; // We should never get here because the calling method should have already verified there is a value, but we'll be extra safe.
		}

		ContentObjectBo defaultGalleryContentObject = null;
		try	{
			defaultGalleryContentObject = CMUtils.loadContentObjectInstance(gcs.getContentObjectId());
		}catch (InvalidContentObjectException | UnsupportedContentObjectTypeException | InvalidAlbumException | UnsupportedImageTypeException | InvalidGalleryException ex){
			// Content object doesn't exist. This won't prevent us from deleting the album but we should note the issue, since
			// it can cause problems to specify a content object that doesn't exist for the default gallery object.
			//String galleryDescription = CMUtils.LoadGallery(this.albumToDelete.GalleryId).Description;
			//String msg = MessageFormat.format(CultureInfo.CurrentCulture, Resources.error.Default_Gallery_Object_ContentObject_Invalid_Ex_Msg, galleryDescription, this.albumToDelete.Id);
			//EventLogController.RecordError(new BusinessException(msg, ex), AppSetting.Instance, this.albumToDelete.GalleryId, CMUtils.LoadGallerySettings());
		}

		return defaultGalleryContentObject;
	}

	private ContentObjectBo getDefaultGalleryAlbum(GalleryControlSettings gcs){
		if (gcs.getAlbumId() != null){
			return null; // We should never get here because the calling method should have already verified there is a value, but we'll be extra safe.
		}

		ContentObjectBo defaultGalleryAlbum = null;
		try	{
			defaultGalleryAlbum = CMUtils.loadAlbumInstance(gcs.getAlbumId(), false);
		}catch (InvalidAlbumException | UnsupportedContentObjectTypeException | UnsupportedImageTypeException | InvalidContentObjectException | InvalidGalleryException ex){
			// Album doesn't exist. This won't prevent us from deleting the album but we should note the issue, since
			// it can cause problems to specify an album that doesn't exist for the default gallery object.
			//String galleryDescription = CMUtils.LoadGallery(this.albumToDelete.GalleryId).Description;
			//String msg = MessageFormat.format(CultureInfo.CurrentCulture, Resources.error.Default_Gallery_Object_Album_Invalid_Ex_Msg, galleryDescription, this.albumToDelete.Id);
			//EventLogController.RecordError(new BusinessException(msg, ex), AppSetting.Instance, this.albumToDelete.GalleryId, CMUtils.LoadGallerySettings());
		}

		return defaultGalleryAlbum;
	}
}
