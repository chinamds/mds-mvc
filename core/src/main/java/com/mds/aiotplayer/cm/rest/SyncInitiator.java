/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.rest;

/// <summary>
/// An enumeration that stores values for possible objects that can initiate a synchronization.
/// </summary>
public enum SyncInitiator{
	/// <summary>
	/// 
	/// </summary>
	Unknown(0),
	/// <summary>
	/// 
	/// </summary>
	LoggedOnGalleryUser(1),
	/// <summary>
	/// 
	/// </summary>
	AutoSync(2),
	/// <summary>
	/// 
	/// </summary>
	RemoteApp(3);

	private final int syncInitiator;

	private SyncInitiator(int syncInitiator) {
		this.syncInitiator = syncInitiator;
	}	
}