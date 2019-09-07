package com.mds.cm.rest;

import java.util.Date;

import com.mds.core.ApprovalAction;
import com.mds.core.ContentObjectType;

/// <summary>
/// A client-optimized object that stores a piece of information describing a gallery object.
/// </summary>
public class ApprovalItem{
	public ApprovalItem() {
	}
	
	public ApprovalItem(long id, long contentId, ContentObjectType contentType, String approveBy, int seq, ApprovalAction approvalAction, Date approveDate) {
		this.Id = id;
		this.ContentId = contentId;
		this.GTypeId = contentType.getValue();
		this.ApproveBy = approveBy;
		this.Seq = seq;
		this.Action = approvalAction.value();
		this.ApproveDate = approveDate;
	}
	/// <summary>
	/// Gets the unique ID for this instance. Maps to Approval Id in the Approval table.
	/// </summary>
	/// <value>An long integer</value>
	public long Id;

	/// <summary>
	/// Gets or sets a value that indentifies the content object or album this instance is associated with.
	/// Refer to <see cref="GTypeId" /> to determine which type of ID it is.
	/// </summary>
	/// <value>The value that uniquely indentifies the content object or album this instance is associated with.</value>
	public long ContentId;

	/// <summary>
	/// Gets a value that identifies the type of gallery item this instance describes. (e.g. album, image, etc).
	/// The value maps to the numerical value of the <see cref="MDS.Business.GalleryObjectType" /> enumeration.
	/// </summary>
	/// <value>An integer</value>
	public int GTypeId;

	/// <summary>
	/// Gets a value that uniquely identifies the type of metadata item for this instance
	/// (e.g. Filename, date picture taken, etc). The value maps to the numerical value of the
	/// <see cref="ApprovaldataItemName" /> enumeration, which also maps to ApprovalName in the Approvaldata table.
	/// </summary>
	/// <value>An integer</value>
	public String ApproveBy;

	/// <summary>
	/// Gets a value that identifies the type of gallery item this instance describes. (e.g. album, image, etc).
	/// The value maps to the numerical value of the <see cref="MDS.Business.GalleryObjectType" /> enumeration.
	/// </summary>
	/// <value>An short integer</value>
	public int Seq;

	/// <summary>
	/// Gets the description of the metadata item. Examples: "File name", "Date picture taken"
	/// </summary>
	/// <value>A short integer.</value>
	public int Action;

	/// <summary>
	/// Gets the value of the metadata item. Examples: "MyImageFilename.jpg", "Jan 30, 2014 9:38:21 AM"
	/// </summary>
	/// <value>A datetime.</value>
	public Date ApproveDate;

	/// <summary>
	/// Gets a value indicating whether this instance is editable.
	/// </summary>
	/// <value>
	/// <c>true</c> if this instance is editable; otherwise, <c>false</c>.
	/// </value>
	public boolean IsEditable;
}