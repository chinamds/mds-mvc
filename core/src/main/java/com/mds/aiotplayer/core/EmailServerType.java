/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.core;

import java.util.List;

import com.google.common.collect.Lists;

/// <summary>
/// Identifies the amount of approval action applied to a content object.
/// </summary>
 public enum EmailServerType{
	/// <summary>
    /// Indicates that no email server type has been specified.
    /// </summary>
    notspecified(-1),
    
  /// <summary>
    /// Indicates that general email server.
    /// </summary>
    general(0),
	
    /// <summary>
    /// Indicates that exchange server.
    /// </summary>
    exchange(1);
	 	
	private final int emailServerType;
    
    private EmailServerType(int emailServerType) {
        this.emailServerType = emailServerType;
    }
    
    public int value(){
    	return this.emailServerType;
    }

		/// <summary>
	/// Determines if the contentObjectApproval parameter is one of the defined enumerations. This method is more efficient than using
	/// <see cref="Enum.IsDefined" />, since <see cref="Enum.IsDefined" /> uses reflection.
	/// </summary>
	/// <param name="contentObjectApproval">An instance of <see cref="EmailServerType" /> to test.</param>
	/// <returns>Returns true if contentObjectApproval is one of the defined items in the enumeration; otherwise returns false.</returns>
	public static boolean isEmailServerType(EmailServerType emailServerType){
		switch (emailServerType)	{
			case notspecified:
			case general:
			case exchange:
				break;

			default:
				return false;
		}
		return true;
	}

	/// <summary>
	/// Parses the string into an instance of <see cref="EmailServerType" />. If <paramref name="contentObjectApproval"/>
	/// is null, empty, or an invalid value, then <paramref name="defaultFilter" /> is returned.
	/// </summary>
	/// <param name="contentObjectApproval">The content object approval to parse into an instance of <see cref="EmailServerType" />.</param>
	/// <param name="defaultFilter">The value to return if <paramref name="contentObjectApproval" /> is invalid.</param>
	/// <returns>Returns an instance of <see cref="EmailServerType" />.</returns>
	public static EmailServerType parse(String emailServerType, EmailServerType defaultFilter)	{
		EmailServerType got = defaultFilter;
		try {
			got = EmailServerType.valueOf(emailServerType);
		}catch(Exception ex) {
			got = defaultFilter;
		}

		return got;
	}
}