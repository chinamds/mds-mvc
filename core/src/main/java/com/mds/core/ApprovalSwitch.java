package com.mds.core;

import java.util.List;

import com.google.common.collect.Lists;

/// <summary>
/// Identifies the amount of approval action applied to a content object.
/// </summary>
public enum ApprovalSwitch{
	/// <summary>
	/// Indicates that no approval has been specified.
	/// </summary>
	notspecified(0),

	/// <summary>
	/// content object need approval.
	/// </summary>
	content(1),

	/// <summary>
	/// playlist need approval.
	/// </summary>
	playlist(2),

	/// <summary>
	/// dailylist need approval.
	/// </summary>
	dailylist(4),

	/// <summary>
	/// user need approval.
	/// </summary>
	user(8),
	
	/// <summary>
	/// Gets all possible approval switch.
	/// </summary>
	All(content.value() | playlist.value()  | dailylist.value() | user.value());
	
	private final int approvalSwitch;
    
    private ApprovalSwitch(int approvalSwitch) {
        this.approvalSwitch = approvalSwitch;
    }
    
    public int value(){
    	return this.approvalSwitch;
    }

		/// <summary>
	/// Determines if the contentObjectApproval parameter is one of the defined enumerations. This method is more efficient than using
	/// <see cref="Enum.IsDefined" />, since <see cref="Enum.IsDefined" /> uses reflection.
	/// </summary>
	/// <param name="contentObjectApproval">An instance of <see cref="ApprovalSwitch" /> to test.</param>
	/// <returns>Returns true if contentObjectApproval is one of the defined items in the enumeration; otherwise returns false.</returns>
	public static boolean isApprovalSwitch(ApprovalSwitch approvalSwitch){
		switch (approvalSwitch)	{
			case notspecified:
			case content:
			case playlist:
			case dailylist:
			case user:
				break;

			default:
				return false;
		}
		return true;
	}

	/// <summary>
	/// Parses the string into an instance of <see cref="ApprovalSwitch" />. If <paramref name="contentObjectApproval"/>
	/// is null, empty, or an invalid value, then <paramref name="defaultFilter" /> is returned.
	/// </summary>
	/// <param name="contentObjectApproval">The content object approval to parse into an instance of <see cref="ApprovalSwitch" />.</param>
	/// <param name="defaultFilter">The value to return if <paramref name="contentObjectApproval" /> is invalid.</param>
	/// <returns>Returns an instance of <see cref="ApprovalSwitch" />.</returns>
	public static ApprovalSwitch parse(String approvalSwitch, ApprovalSwitch defaultFilter)	{
		ApprovalSwitch got = defaultFilter;
		try {
			got = ApprovalSwitch.valueOf(approvalSwitch);
		}catch(Exception ex) {
			got = defaultFilter;
		}

		return got;
	}
	
	public static ApprovalSwitch getApprovalSwitch(int approvalSwitch) {
		for(ApprovalSwitch value : ApprovalSwitch.values()) {
			if (value.value() == approvalSwitch)
				return value;
		}
		
		return ApprovalSwitch.notspecified;
	}
}