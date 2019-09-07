package com.mds.cm.rest;

import com.mds.cm.content.GalleryBoCollection;
import com.mds.core.LongCollection;
import com.mds.core.SecurityActions;

/// <summary>
/// Represents settings for manipulating the display of a <see cref="TreeView" />.
/// </summary>
public class TreeViewOptions{
	/// <summary>
	/// A reference to the collection of album IDs whose associated checkboxes are to be selected, checked, and made visible.
	/// </summary>
	/// <value>A collection of integers.</value>
	public LongCollection SelectedAlbumIds;

	/// <summary>
	/// Gets or sets the base relative or absolute URL to invoke when a tree node is clicked. Leave this value as null or set to 
	/// an empty String when no navigation is desired. The album ID of the selected album is passed to 
	/// the URL as the query String parameter "aid". Example: "Gallery.aspx, http://site.com/gallery.aspx"
	/// </summary>
	/// <value>A String representing a relative or absolute URL.</value>
	public String NavigateUrl;

	/// <summary>
	/// Gets or sets a value indicating whether checkbox functionality is desired. The default value
	/// is <c>false</c>. When <c>false</c>, the property <see cref="TreeNode.ShowCheckBox" /> is ignored.
	/// </summary>
	/// <value><c>true</c> if checkbox functionality is desired; otherwise, <c>false</c>.</value>
	public boolean EnableCheckboxPlugin;

	/// <summary>
	/// Gets or sets the security permission the logged on user must have in order for an album to be displayed in the
	/// treeview. It may be a single value or some combination of valid enumeration values.
	/// </summary>
	public SecurityActions[] RequiredSecurityPermissions;

	/// <summary>
	/// Gets or sets a value indicating the top level album to render. When not specified, the <see cref="Galleries" /> property determines
	/// the root albums to be rendered.
	/// </summary>
	/// <value>The top level album to render.</value>
	public long RootAlbumId;

	/// <summary>
	/// Gets or sets a value to be prepended to the root album title in the treeview. The default value is <see cref="StringUtils.EMPTY" />.
	/// May contain the placeholder values {GalleryId} and/or {GalleryDescription}. If present, the placeholders are replaced by the 
	/// action values during databinding. Example: "Gallery {GalleryDescription}: "
	/// </summary>
	public String RootAlbumPrefix;

	/// <summary>
	/// Gets or sets the galleries to be rendered in the treeview. If not explicitly set, this defaults to the current gallery.
	/// If the <see cref="RootAlbumId" /> property is assigned, this property is ignored.
	/// </summary>
	/// <value>The galleries to be rendered in the treeview.</value>
	public GalleryBoCollection Galleries;
}