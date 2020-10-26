/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.rest;

import com.mds.aiotplayer.core.MetadataItemName;

/// <summary>
/// A simple object for specifying actions to take on an album.
/// </summary>
public class AlbumAction{
	public AlbumRest Album;
	public MetadataItemName SortByMetaNameId;
	public boolean SortAscending;
}
