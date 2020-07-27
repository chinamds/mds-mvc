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
