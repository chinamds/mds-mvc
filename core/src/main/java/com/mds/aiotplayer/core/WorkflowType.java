package com.mds.aiotplayer.core;

import java.util.List;

import com.google.common.collect.Lists;

/// <summary>
/// Identifies the amount of approval action applied to a content object.
/// </summary>
 public enum WorkflowType{
	/// <summary>
    /// Indicates that no approval type has been specified.
    /// </summary>
    NotSpecified(-1),
    
	ContentObject(0),
	
	DailyList(1),
	
	Playlist(2);
	 	
	private final int workflowType;
    
    private WorkflowType(int workflowType) {
        this.workflowType = workflowType;
    }
    
    public int value(){
    	return this.workflowType;
    }

		/// <summary>
	/// Determines if the contentObjectApproval parameter is one of the defined enumerations. This method is more efficient than using
	/// <see cref="Enum.IsDefined" />, since <see cref="Enum.IsDefined" /> uses reflection.
	/// </summary>
	/// <param name="contentObjectApproval">An instance of <see cref="WorkflowType" /> to test.</param>
	/// <returns>Returns true if contentObjectApproval is one of the defined items in the enumeration; otherwise returns false.</returns>
	public static boolean isWorkflowType(WorkflowType workflowType){
		switch (workflowType)	{
			case NotSpecified:
			case ContentObject:
			case DailyList:
			case Playlist:
				break;

			default:
				return false;
		}
		return true;
	}

	/// <summary>
	/// Parses the string into an instance of <see cref="WorkflowType" />. If <paramref name="contentObjectApproval"/>
	/// is null, empty, or an invalid value, then <paramref name="defaultFilter" /> is returned.
	/// </summary>
	/// <param name="contentObjectApproval">The content object approval to parse into an instance of <see cref="WorkflowType" />.</param>
	/// <param name="defaultFilter">The value to return if <paramref name="contentObjectApproval" /> is invalid.</param>
	/// <returns>Returns an instance of <see cref="WorkflowType" />.</returns>
	public static WorkflowType parse(String workflowType, WorkflowType defaultFilter)	{
		WorkflowType got = defaultFilter;
		try {
			got = WorkflowType.valueOf(workflowType);
		}catch(Exception ex) {
			got = defaultFilter;
		}

		return got;
	}
}