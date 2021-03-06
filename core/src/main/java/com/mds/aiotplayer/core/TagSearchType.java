/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.core;

/// <summary>
/// Identifies the type of search being performed.
/// </summary>
public enum TagSearchType
{
	/// <summary>
	/// Indicates that no search type has been specified.
	/// </summary>
	NotSpecified,

	/// <summary>
	/// Indicates a request for all tags in the current gallery.
	/// </summary>
	AllTagsInGallery,

	/// <summary>
	/// Indicates a request for all people in the current gallery.
	/// </summary>
	AllPeopleInGallery,

	/// <summary>
	/// Indicates a request for all tags visible to the current user in the current gallery.
	/// </summary>
	TagsUserCanView,

	/// <summary>
	/// Indicates a request for all people visible to the current user in the current gallery.
	/// </summary>
	PeopleUserCanView;
}