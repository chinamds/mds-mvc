/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.rest;
/// <summary>
/// A simple object that contains synchronization options.
/// </summary>
public class SyncOptions {
	public String SyncId;
	public String UserName;
	public long AlbumIdToSynchronize;
	public boolean IsRecursive;
	public boolean RebuildThumbnails;
	public boolean RebuildOptimized;
	public SyncInitiator SyncInitiator;
}
  