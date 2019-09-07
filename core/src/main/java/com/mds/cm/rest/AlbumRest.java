package com.mds.cm.rest;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

/// <summary>
/// A simple object that contains album information. This class is used to pass information between the browser and the web server
/// via AJAX callbacks.
/// </summary>
@JsonInclude(value=JsonInclude.Include.NON_DEFAULT)
public class AlbumRest{
	/// <summary>
	/// The album ID.
	/// </summary>
	public long Id;

	/// <summary>
	/// The ID of the gallery to which the album belongs.
	/// </summary>
	public long GalleryId;

	/// <summary>
	/// The album title.
	/// </summary>
	public String Title;

	/// <summary>
	/// The album caption.
	/// </summary>
	public String Caption;

	/// <summary>
	/// The album owner. Populated only when the
	/// user is a gallery administrator or higher.
	/// </summary>
	public String Owner;

	/// <summary>
	/// A comma-delimited list of owners the current album inherits from parent albums. Populated only when the
	/// user is a gallery administrator or higher.
	/// </summary>
	public String InheritedOwners;

	/// <summary>
	/// The starting date of this album.
	/// </summary>
	public Date DateStart;

	/// <summary>
	/// The ending date of this album.
	/// </summary>
	public Date DateEnd;

	/// <summary>
	/// Indicates whether this album is hidden from anonymous users.
	/// </summary>
	public boolean IsPrivate;

	/// <summary>
	/// Gets or sets the type of the virtual album.  Maps to the <see cref="VirtualAlbumType" /> enumeration.
	/// </summary>
	public int VirtualType;

	/// <summary>
	/// Gets or sets the RSS URL for the album. Will be null when an RSS URL is not valid (eg. for virtual root
	/// albums or when not running an Enterprise license.)
	/// </summary>
	public String RssUrl;

	/// <summary>
	/// Gets the ID of the metadata item name the album is sorted by. Maps to <see cref="Business.Metadata.MetadataItemName" />.
	/// </summary>
	public int SortById;
	
	/// <summary>
	/// Indicates whether the album is sorted in ascending (<c>true</c>) or descending (<c>false</c>) order.
	/// </summary>
	public boolean SortUp;

	/// <summary>
	/// Gets the number of gallery objects in the album (includes albums and content objects).
	/// </summary>
	public int NumContentItems;

	/// <summary>
	/// Gets the number of child albums in the album.
	/// </summary>
	public int NumAlbums;

	/// <summary>
	/// Gets the number of content objects in the album (excludes albums).
	/// </summary>
	public int NumMediaItems;

	/// <summary>
	/// Gets a summarized view of all items in this album. Includes both albums and content objects.
	/// </summary>
	public ContentItem[] ContentItems;

	/// <summary>
	/// Gets the content objects in the album (excludes albums).
	/// </summary>
	public MediaItem[] MediaItems;

	/// <summary>
	/// Gets the permissions the current user has for the album.
	/// </summary>
	public PermissionsRest Permissions;

	/// <summary>
	/// Gets or sets the metadata available for this album.
	/// </summary>
	/// <value>The metadata.</value>
	public MetaItemRest[] MetaItems;

	/// <summary>
	/// Gets or sets the approval data available for this album.
	/// </summary>
	/// <value>The Approval data.</value>
	public ApprovalItem[] ApprovalItems;
}


