package com.mds.aiotplayer.core;

import java.util.List;

import com.google.common.collect.Lists;

/// <summary>
/// Identifies the amount of approval action applied to a content object.
/// </summary>
public enum ApprovalAction {
    /// <summary>
    /// No approval action has been specified.
    /// </summary>
    NotSpecified (0),
    /// <summary>
    /// apply.
    /// </summary>
    Apply(1),
    /// <summary>
    /// approve.
    /// </summary>
    Approve(2),
    /// <summary>
    /// cancel.
    /// </summary>
    Reject(4),

    All(Apply.value() | Approve.value() | Reject.value());
	
	private final int approvalAction;
    
    private ApprovalAction(int approvalAction) {
        this.approvalAction = approvalAction;
    }
    
    public int value(){
    	return this.approvalAction;
    }

		/// <summary>
	/// Determines if the contentObjectApproval parameter is one of the defined enumerations. This method is more efficient than using
	/// <see cref="Enum.IsDefined" />, since <see cref="Enum.IsDefined" /> uses reflection.
	/// </summary>
	/// <param name="contentObjectApproval">An instance of <see cref="ApprovalStatus" /> to test.</param>
	/// <returns>Returns true if contentObjectApproval is one of the defined items in the enumeration; otherwise returns false.</returns>
	public static boolean isApprovalAction(ApprovalAction approvalAction){
		switch (approvalAction)	{
			case NotSpecified:
			case Apply:
			case Approve:
			case Reject:
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
	public static ApprovalAction parse(String approvalAction, ApprovalAction defaultFilter)	{
		ApprovalAction got = defaultFilter;
		try {
			got = ApprovalAction.valueOf(approvalAction);
		}catch(Exception ex) {
			got = defaultFilter;
		}

		return got;
	}
}