package com.mds.aiotplayer.core;

/// <summary>
/// Specifies a reason why an album or content object cannot be deleted.
/// </summary>
public enum ContentObjectDeleteValidationFailureReason
{
	/// <summary>
	/// The default value to use when no validation failure exists or it has not yet been calculated.
	/// </summary>
	NotSet(0),
	/// <summary>
	/// The album cannot be deleted because it is configured as the user album container.
	/// </summary>
	AlbumSpecifiedAsUserAlbumContainer(1),
	/// <summary>
	/// The album cannot be deleted because it contains the user album container.
	/// </summary>
	AlbumContainsUserAlbumContainer(2),
	/// <summary>
	/// The album cannot be deleted because it is configured as the default content object.
	/// </summary>
	AlbumSpecifiedAsDefaultContentObject(3),
	/// <summary>
	/// The album cannot be deleted because it contains an album configured as the default content object.
	/// </summary>
	AlbumContainsDefaultContentObjectAlbum(4),
	/// <summary>
	/// The album cannot be deleted because it contains a content object configured as the default content object.
	/// </summary>
	AlbumContainsDefaultContentObjectContentObject(5); 
	
	private final int contentObjectDeleteValidationFailureReason;
    
    private ContentObjectDeleteValidationFailureReason(int contentObjectDeleteValidationFailureReason) {
        this.contentObjectDeleteValidationFailureReason = contentObjectDeleteValidationFailureReason;
    }	
}