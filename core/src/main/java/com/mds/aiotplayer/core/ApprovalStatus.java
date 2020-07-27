package com.mds.aiotplayer.core;

import java.util.List;

import com.google.common.collect.Lists;

/// <summary>
/// Identifies the amount of approval action applied to a content object.
/// </summary>
public enum ApprovalStatus{
	/// <summary>
	/// Indicates that no approval has been specified.
	/// </summary>
	NotSpecified(0),

	/// <summary>
	/// content object has been approved.
	/// </summary>
	Waiting(1),
	
	/// <summary>
	/// approving.
	/// </summary>
	Approving(2),

	/// <summary>
	/// content object has been approved.
	/// </summary>
	Approved(4),

	/// <summary>
	/// Approval status of content object has been rejected.
	/// </summary>
	Rejected(8),

	/// <summary>
	/// Reject status of content object has been cancelled.
	/// </summary>
	Cancelled(16),
	
	/// <summary>
    /// HR applications has been rejected that book date range expired. 
    /// </summary>
    Expired(32),

	/// <summary>
	/// Gets all possible content object approval status.
	/// </summary>
	All(Waiting.value() | Approving.value() | Approved.value()  | Rejected.value() | Cancelled.value() | Expired.value());
	
	private final int approvalStatus;
    
    private ApprovalStatus(int approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
    
    public int value(){
    	return this.approvalStatus;
    }

		/// <summary>
	/// Determines if the contentObjectApproval parameter is one of the defined enumerations. This method is more efficient than using
	/// <see cref="Enum.IsDefined" />, since <see cref="Enum.IsDefined" /> uses reflection.
	/// </summary>
	/// <param name="contentObjectApproval">An instance of <see cref="ApprovalStatus" /> to test.</param>
	/// <returns>Returns true if contentObjectApproval is one of the defined items in the enumeration; otherwise returns false.</returns>
	public static boolean isApprovalStatus(ApprovalStatus approvalStatus){
		switch (approvalStatus)	{
			case NotSpecified:
			case Waiting:
			case Approving:
			case Approved:
			case Rejected:
			case Cancelled:
			case Expired:
				break;

			default:
				return false;
		}
		return true;
	}

	/// <summary>
	/// Parses the string into an instance of <see cref="ApprovalStatus" />. If <paramref name="contentObjectApproval"/>
	/// is null, empty, or an invalid value, then <paramref name="defaultFilter" /> is returned.
	/// </summary>
	/// <param name="contentObjectApproval">The content object approval to parse into an instance of <see cref="ApprovalStatus" />.</param>
	/// <param name="defaultFilter">The value to return if <paramref name="contentObjectApproval" /> is invalid.</param>
	/// <returns>Returns an instance of <see cref="ApprovalStatus" />.</returns>
	public static ApprovalStatus parse(String approvalStatus, ApprovalStatus defaultFilter)	{
		ApprovalStatus got = defaultFilter;
		try {
			got = ApprovalStatus.valueOf(approvalStatus);
		}catch(Exception ex) {
			got = defaultFilter;
		}

		return got;
	}
	
	public static ApprovalStatus getApprovalStatus(int approvalStatus) {
		for(ApprovalStatus value : ApprovalStatus.values()) {
			if (value.value() == approvalStatus)
				return value;
		}
		
		return ApprovalStatus.NotSpecified;
	}
}