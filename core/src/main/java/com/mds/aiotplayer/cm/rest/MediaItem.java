/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.rest;

import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.core.MimeTypeCategory;
import com.mds.aiotplayer.util.StringUtils;

/// <summary>
/// A client-optimized object that contains content object information. This class is used to 
/// pass information between the browser and the web server via AJAX callbacks.
/// </summary>
public class MediaItem{
	public MediaItem(long id, long albumId, String albumTitle, int index, String title, DisplayObjectRest[] views
			, boolean highResAvailable, boolean isDownloadable, int viewIndex, MimeTypeCategory mimeType, ContentObjectType itemType, MetaItemRest[] metaItems, ApprovalItem[] approvalItems) {
		this.Id = id;
		this.AlbumId = albumId;
		this.AlbumTitle = albumTitle;
		this.Index = index;
		this.Title = title;
		this.Views = views;
		this.HighResAvailable = highResAvailable;
		this.IsDownloadable = isDownloadable;
		this.ViewIndex = viewIndex;
		this.MimeType = mimeType.value();
		this.ItemType = itemType.getValue();
		this.MetaItems = metaItems;
		this.ApprovalItems = approvalItems;
	}
	
	public MediaItem(long id, long albumId, String albumTitle, int index, String title, DisplayObjectRest[] views
			, boolean highResAvailable, boolean isDownloadable, MimeTypeCategory mimeType, ContentObjectType itemType, MetaItemRest[] metaItems, ApprovalItem[] approvalItems) {
		this.Id = id;
		this.AlbumId = albumId;
		this.AlbumTitle = albumTitle;
		this.Index = index;
		this.Title = title;
		this.Views = views;
		this.HighResAvailable = highResAvailable;
		this.IsDownloadable = isDownloadable;
		this.MimeType = mimeType.value();
		this.ItemType = itemType.getValue();
		this.MetaItems = metaItems;
		this.ApprovalItems = approvalItems;
	}
	
	/// <summary>
	/// The content object ID.
	/// </summary>
	public long Id;

	/// <summary>
	/// Gets or sets the ID of the physical album this content object belongs to. This is useful when the item is packaged
	/// in a virtual album.
	/// </summary>
	public long AlbumId;

	/// <summary>
	/// Gets or sets the title of the physical album this content object belongs to. This is useful when the item is packaged
	/// in a virtual album.
	/// </summary>
	public String AlbumTitle;

	/// <summary>
	/// Specifies the one-based index of this content object among the others in the containing album.
	/// The first content object in an album has index = 1.
	/// </summary>
	public int Index;

	/// <summary>
	/// The content object title.
	/// </summary>
	public String Title;

	/// <summary>
	/// Gets or sets the views available for this content object.
	/// </summary>
	/// <value>The views.</value>
	public DisplayObjectRest[] Views;

	/// <summary>
	/// Indicates whether a high resolution version of this image exists and is available for viewing.
	/// </summary>
	public boolean HighResAvailable;

	/// <summary>
	/// Indicates whether a downloadable version of this content object exists and can be downloaded. External content objects
	/// cannot be downloaded.
	/// </summary>
	public boolean IsDownloadable;

	/// <summary>
	/// Gets or sets the index of the view currently being rendered. This value can be used to get 
	/// or set the desired view to display among the possibilities in <see cref="Views" />.
	/// </summary>
	/// <value>The index of the view currently being rendered.</value>
	public int ViewIndex;

	/// <summary>
	/// The MIME type of this content object.  Maps to the <see cref="MimeTypeCategory" />
	/// enumeration, so that 0=NotSet, 1=Other, 2=Image, 3=Video, 4=Audio
	/// </summary>
	public int MimeType;

	/// <summary>
	/// The type of this gallery item.  Maps to the <see cref="GalleryObjectType" /> enumeration.
	/// </summary>
	public int ItemType;

	/// <summary>
	/// Gets or sets the metadata available for this content object.
	/// </summary>
	/// <value>The metadata.</value>
	public MetaItemRest[] MetaItems;

	/// <summary>
	/// Gets or sets the approval status available for this content object.
	/// </summary>
	/// <value>The approval status.</value>
	public ApprovalItem[] ApprovalItems;
}
