package com.mds.cm.rest;

import com.mds.core.ActionResult;

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