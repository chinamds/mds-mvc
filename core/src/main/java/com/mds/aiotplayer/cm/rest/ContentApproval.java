/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.rest;

import com.mds.aiotplayer.core.ActionResult;

/// <summary>
/// A client-optimized object that wraps a meta item and the content items it applies to.
/// </summary>
public class ContentApproval{
	/// <summary>
	/// An array of <see cref="ContentItem" /> instances.
	/// </summary>
	/// <value>The content items.</value>
	public ContentItem[] ContentItems;

	/// <summary>
	/// Gets or sets the meta item that applies to <see cref="ContentItems" />.
	/// </summary>
	/// <value>The meta item.</value>
	public ApprovalItem ApprovalItem;

	/// <summary>
	/// Gets or sets information about an action applied to this instance (e.g. when saving).
	/// </summary>
	public ActionResult ActionResult;
}