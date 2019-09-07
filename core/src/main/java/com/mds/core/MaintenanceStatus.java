package com.mds.core;

/// <summary>
/// Specifies the status of the MDS System maintenance task.
/// </summary>
public enum MaintenanceStatus
{
	/// <summary>
	/// Specifies that the maintenance task has not begun.
	/// </summary>
	NotStarted(0),
	/// <summary>
	/// Specifies that the maintenance task has begun.
	/// </summary>
	InProgress(1),
	/// <summary>
	/// Specifies that the maintenance task is complete.
	/// </summary>
	Complete(2);
	
	private final int maintenanceStatus;
    
    private MaintenanceStatus(int maintenanceStatus) {
        this.maintenanceStatus = maintenanceStatus;
    }
}