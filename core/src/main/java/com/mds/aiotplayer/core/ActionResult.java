/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.core;

import javax.xml.bind.annotation.XmlRootElement;

/// <summary>
/// A data object containing information about the result of an action. The object may be serialized into
/// JSON and used by the browser.
/// </summary>
@XmlRootElement(name = "actionResult")
public class ActionResult{
	public ActionResult(String status, String title, String message, Object actionTarget) {
		this.Status = status;
		this.Title = title;
		this.Message = message;
		this.ActionTarget=actionTarget;
	}
	
	public ActionResult(String title, String status) {
		this.Status = status;
		this.Title = title;
	}
	
	public ActionResult(String title) {
		this.Title = title;
	}
	
	public ActionResult() {
	}
	
	/// <summary>
	/// Gets or sets the category describing the result of this action. The value must
	/// map to the string representation of the <see cref="ActionResultStatus" /> enumeration.
	/// </summary>
	public String Status;

	/// <summary>
	/// Gets or sets a title describing the action result.
	/// </summary>
	public String Title;

	/// <summary>
	/// Gets or sets an explanatory message describing the action result.
	/// </summary>
	public String Message;

	/// <summary>
	/// Gets or sets the object that is the target of the action. The object must be serializable.
	/// </summary>
	public Object ActionTarget;
}