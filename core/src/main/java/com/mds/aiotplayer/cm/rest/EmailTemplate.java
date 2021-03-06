/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.rest;

/// <summary>
/// Specifies a template that can be used to create an e-mail message.
/// </summary>
public class EmailTemplate
{
	/// <summary>
	/// The e-mail subject.
	/// </summary>
	public String Subject;

	/// <summary>
	/// The e-mail body.
	/// </summary>
	public String Body;

	/// <summary>
	/// A value that identifies the type of e-mail template.
	/// </summary>
	public EmailTemplateForm EmailTemplateId;
}

