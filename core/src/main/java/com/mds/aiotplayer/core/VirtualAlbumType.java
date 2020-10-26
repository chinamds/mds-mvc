/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.core;

/// <summary>
/// Identifies the type of virtual album.
/// </summary>
public enum VirtualAlbumType {
	/// <summary>
	/// Indicates that no virtual album type has been specified.
	/// </summary>
	NotSpecified(0),

	/// <summary>
	/// Specifies that the album is not a virtual album.
	/// </summary>
	NotVirtual(1),

	/// <summary>
	/// Specifies that the album is a virtual container whose purpose is to hold child objects the user
	/// has permission to access. This is used when a restricted permission user has access to two albums
	/// without a common parent. In this case, a virtual album is created to serve as the container.
	/// </summary>
	Root(2),

	/// <summary>
	/// Indicates that a virtual album contains the results of a tag search.
	/// </summary>
	Tag(3),

	/// <summary>
	/// Indicates that a virtual album contains the results of a people search.
	/// </summary>
	People(4),

	/// <summary>
	/// Indicates that a virtual album contains the results of a keyword search.
	/// </summary>
	Search(5),

	/// <summary>
	/// Indicates that a virtual album contains the results of a title/caption search.
	/// </summary>
	TitleOrCaption(6),

	/// <summary>
	/// Indicates the most recently added gallery objects.
	/// </summary>
	MostRecentlyAdded(7),

	/// <summary>
	/// Indicates gallery objects having a specific rating.
	/// </summary>
	Rated(8),

    /// <summary>
	/// Indicates gallery objects's approval status.
	/// </summary>
	Approval(9),
		
	/// <summary>
	/// Indicates content objects's for preview.
	/// </summary>
	ContentPreview(10);
	
	private final int virtualAlbumType;
    
    private VirtualAlbumType(int virtualAlbumType) {
        this.virtualAlbumType = virtualAlbumType;
    }
    
    public int value() {
    	return virtualAlbumType;
    }
    
    public static VirtualAlbumType getVirtualAlbumType(int virtualAlbumType) {
		for(VirtualAlbumType value : VirtualAlbumType.values()) {
			if (value.value() == virtualAlbumType)
				return value;
		}
		
		return VirtualAlbumType.NotSpecified;
	}
}