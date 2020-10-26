/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.core;

/// <summary>
/// Indicates the current state of a synchronization process.
/// </summary>
public enum SynchronizationState{
	/// <summary>
	/// The default value to use when the state is unknown or it is not relevant to specify.
	/// </summary>
	NotSet(0),
	/// <summary>
	/// The synchronization is complete and there is no current activity.
	/// </summary>
	Complete(1),
	/// <summary>
	/// Indicates the current user is performing a synchronization. During this state no changes will be
	/// persisted to the data store. The changes will be saved to the data store in the next state
	/// PersistingToDataStore.
	/// </summary>
	SynchronizingFiles(2),
	/// <summary>
	/// Indicates the files have been synchronized and now the changes are being persisted to the data store.
	/// </summary>
	PersistingToDataStore(3),
	/// <summary>
	/// An error occurred during the most recent synchronization.
	/// </summary>
	Error(4),
	/// <summary>
	/// Indicates another synchronization is already in progress.
	/// </summary>
	AnotherSynchronizationInProgress(5),
	/// <summary>
	/// Indicates the synchronization was cancelled by the user.
	/// </summary>
	Aborted(6);
	
	private final int synchronizationState;
    
    private SynchronizationState(int synchronizationState) {
        this.synchronizationState = synchronizationState;
    }
}