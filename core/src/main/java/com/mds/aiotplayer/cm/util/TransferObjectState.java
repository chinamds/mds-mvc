/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.util;


	/// <summary>
	/// Indicates the current state of the transfer.
	/// </summary>
public enum TransferObjectState	{
	None,
	AlbumMoveStep2,
	AlbumCopyStep2,
	ContentObjectMoveStep2,
	ContentObjectCopyStep2,
	ObjectsMoveStep1,
	ObjectsMoveStep2,
	ObjectsCopyStep1,
	ObjectsCopyStep2,
	ReadyToTransfer
}
