package com.mds.core;

/// <summary>
/// Specifies the status of the content object in the content object conversion queue.
/// </summary>
public enum ContentQueueItemStatus
{
	/// <summary>
	/// Specifies the unknown media queue status.
	/// </summary>
	Unknown(0),
	/// <summary>
	/// Specifies an error occurred while processing the media item.
	/// </summary>
	Error(1),
	/// <summary>
	/// Specifies the item is waiting to be processed.
	/// </summary>
	Waiting(2),
	/// <summary>
	/// Specifies the item is currently being processed.
	/// </summary>
	Processing(3),
	/// <summary>
	/// Specifies that processing is complete.
	/// </summary>
	Complete(4);
	
	private final int contentQueueItemStatus;
    
    private ContentQueueItemStatus(int contentQueueItemStatus) {
        this.contentQueueItemStatus = contentQueueItemStatus;
    }	
}