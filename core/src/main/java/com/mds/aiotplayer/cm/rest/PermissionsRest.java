package com.mds.aiotplayer.cm.rest;

/// <summary>
/// A data object that contains permissions relevant to the current user. The instance can be serialized to JSON and
/// subsequently used in the browser as a data object.
/// </summary>
public class PermissionsRest
{
	/// <summary>
	/// Represents the ability to view an album or content object. Does not include the ability to view high resolution
	/// versions of images. Includes the ability to download the content object and view a slide show.
	/// </summary>
	public boolean ViewAlbumOrContentObject;

	/// <summary>
	/// Represents the ability to view the original content object, if it exists.
	/// </summary>
	public boolean ViewOriginalContentObject;

	/// <summary>
	/// Represents the ability to create a new album within the current album. This includes the ability to move or
	/// copy an album into the current album.
	/// </summary>
	public boolean AddChildAlbum;

	/// <summary>
	/// Represents the ability to add a new content object to the current album. This includes the ability to move or
	/// copy a content object into the current album.
	/// </summary>
	public boolean AddContentObject;

	/// <summary>
	/// Represents the ability to edit an album's title, summary, and begin and end dates. Also includes rearranging the
	/// order of objects within the album and assigning the album's thumbnail image. Does not include the ability to
	/// add or delete child albums or content objects.
	/// </summary>
	public boolean EditAlbum;

	/// <summary>
	/// Represents the ability to edit a content object's caption, rotate it, and delete the high resolution version of
	/// an image.
	/// </summary>
	public boolean EditContentObject;

	/// <summary>
	/// Represents the ability to delete the current album. This permission is required to move 
	/// albums to another album, since it is effectively deleting it from the current album's parent.
	/// </summary>
	public boolean DeleteAlbum;

	/// <summary>
	/// Represents the ability to delete child albums within the current album.
	/// </summary>
	public boolean DeleteChildAlbum;

	/// <summary>
	/// Represents the ability to delete content objects within the current album. This permission is required to move 
	/// content objects to another album, since it is effectively deleting it from the current album.
	/// </summary>
	public boolean DeleteContentObject;

	/// <summary>
	/// Represents the ability to approve content objects within the current album. This permission is required to move 
	/// content objects to another album, since it is effectively approving it from the current album.
	/// </summary>
	public boolean ApproveContentObject;

	/// <summary>
	/// Represents the ability to synchronize content objects on the hard drive with records in the data store.
	/// </summary>
	public boolean Synchronize;

	/// <summary>
	/// Represents the ability to administer a particular gallery. Automatically includes all other permissions except
	/// AdministerSite.
	/// </summary>
	public boolean AdministerGallery;

	/// <summary>
	/// Represents the ability to administer all aspects of MDS System. Automatically includes all other permissions.
	/// </summary>
	public boolean AdministerSite;

	/// <summary>
	/// Represents the ability to not render a watermark over content objects.
	/// </summary>
	public boolean HideWatermark;
}