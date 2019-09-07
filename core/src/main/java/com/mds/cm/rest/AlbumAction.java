package com.mds.cm.rest;

import com.mds.core.MetadataItemName;

/// <summary>
/// A simple object for specifying actions to take on an album.
/// </summary>
public class AlbumAction{
	public AlbumRest Album;
	public MetadataItemName SortByMetaNameId;
	public boolean SortAscending;
}
