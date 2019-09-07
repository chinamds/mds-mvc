package com.mds.core;

/// <summary>
/// Specifies the status of content object conversion queue.
/// </summary>
public enum ContentQueueStatus
{
	/// <summary>
	/// Specifies the unknown content object conversion queue status.
	/// </summary>
	Unknown(0),
	/// <summary>
	/// Specifies that the content object conversion queue is not processing any items.
	/// </summary>
	Idle(1),
	/// <summary>
	/// Specifies that an item in the content object conversion queue is currently being processed.
	/// </summary>
	Processing(2);
	
	private final int contentQueueStatus;
    
    private ContentQueueStatus(int contentQueueStatus) {
        this.contentQueueStatus = contentQueueStatus;
    }	
}
