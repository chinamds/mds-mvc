/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.rest;

import com.mds.aiotplayer.core.ApprovalStatus;
import com.mds.aiotplayer.core.ContentObjectType;

/// <summary>
/// Allows specifying options for populating an instance of <see cref="MDSData" />.
/// </summary>
public class CMDataLoadOptions{
	public CMDataLoadOptions() {
	}
	
	public CMDataLoadOptions(boolean loadContentItems, boolean loadMediaItems) {
		this.LoadContentItems = loadContentItems;
		this.LoadMediaItems = loadMediaItems;
	}
	
	public CMDataLoadOptions(boolean loadContentItems, boolean loadMediaItems, ContentObjectType filter, ApprovalStatus approvalFilter) {
		this.LoadContentItems = loadContentItems;
		this.LoadMediaItems = loadMediaItems;
		this.Filter = filter;
		this.ApprovalFilter = approvalFilter;
	}
	/// <summary>
	/// </summary>
	public boolean LoadContentItems;

	/// <summary>
	/// Specifies that <see cref="Album.MediaItems" /> should be populated with the content objects
	/// belonging to the album.
	/// </summary>
	public boolean LoadMediaItems;

	/// <summary>
	/// Specifies the number of content items to retrieve. A value of zero (the default) or less indicates all items are to be retrieved.
	/// </summary>
	public int NumContentItemsToRetrieve;

	/// <summary>
	/// Specifies the number of content items to skip. Use this property along with <see cref="NumContentItemsToRetrieve" />
	/// to support paged results.
	/// </summary>
	public int NumContentItemsToSkip;

	/// <summary>
	/// A filter specifying the type of content objects to load. Defaults to
	/// <see cref="Business.ContentObjectType.All" /> when not specified.
	/// </summary>
	public ContentObjectType Filter = ContentObjectType.All;

	/// <summary>
	/// A filter specifying the approval status of content objects to load. Defaults to
	/// <see cref="Business.ApprovalStatus.All" /> when not specified.
	/// </summary>
	public ApprovalStatus ApprovalFilter = ApprovalStatus.All;
}


