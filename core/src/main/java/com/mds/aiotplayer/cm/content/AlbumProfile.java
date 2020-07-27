package com.mds.aiotplayer.cm.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.cm.util.CMUtils;

/// <summary>
/// An object that contains user preferences for an album.
/// </summary>
public class AlbumProfile{
	//#region Properties

	/// <summary>
	/// Gets or sets the album ID.
	/// </summary>
	/// <value>An integer.</value>
    @JsonProperty(value = "Id")
	public long AlbumId;

	/// <summary>
	/// Gets or sets the metadata name to sort the album by.
	/// </summary>
	/// <value>An instance of <see cref="MetadataItemName" />.</value>
	public MetadataItemName SortByMetaName;

	/// <summary>
	/// Indicates the direction the album is to be sorted. A value of <c>true</c> indicates ascending 
	/// order; a value of <c>false</c> indicates descending order.
	/// </summary>
	/// <value><c>true</c> if ascending order; otherwise, <c>false</c>.</value>
	public boolean SortAscending;

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="AlbumProfile" /> class.
	/// </summary>
	public AlbumProfile(){
	}

	/// <summary>
	/// Initializes a new instance of the <see cref="AlbumProfile" /> class.
	/// </summary>
	/// <param name="albumId">The album ID.</param>
	/// <param name="sortByMetaName">The metadata name to sort the album by.</param>
	/// <param name="sortAscending">Indicates the direction the album is to be sorted.</param>
	public AlbumProfile(long albumId, MetadataItemName sortByMetaName, boolean sortAscending){
		AlbumId = albumId;
		SortByMetaName = sortByMetaName;
		SortAscending = sortAscending;
	}

	//#endregion

	//#region Methods

	/// <summary>
	/// Perform a deep copy of this item.
	/// </summary>
	/// <returns>Returns a deep copy of this item.</returns>
	public AlbumProfile Copy(){
		return CMUtils.createAlbumProfile(AlbumId, SortByMetaName, SortAscending);
	}

	//#endregion
}
