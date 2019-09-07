package com.mds.cm.rest;
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
  