/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.core;

/// <summary>
/// Specifies the category describing the result of an action.
/// </summary>
public enum ActionResultStatus{
	/// <summary>
	/// Gets the NotSet value, which indicates that no assignment has been made.
	/// </summary>
	NotSet(0),
	/// <summary>
	/// Specifies that the result was successful.
	/// </summary>
	Success(1),
	/// <summary>
	/// Specifies that an error occurred while processing the action.
	/// </summary>
	Error(2),
	/// <summary>
	/// Specifies that a warning occurred while processing the action.
	/// </summary>
	Warning(3),
	/// <summary>
	/// Specifies a piece of information related to the action.
	/// </summary>
	Info(4),
	/// <summary>
	/// Specifies that an action is being executed asyncronously and its exact status has not yet been determined.
	/// </summary>
	Async(5);

	private final int actionResultStatus;
    
    private ActionResultStatus(int actionResultStatus) {
        this.actionResultStatus = actionResultStatus;
    }
}