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
public enum ContentObjectSearchType
{
	/// <summary>
	/// Indicates that no search type has been specified.
	/// </summary>
	NotSpecified(0),

	/// <summary>
	/// Indicates that a search by title or caption is specified.
	/// </summary>
	SearchByTitleOrCaption(1),

	/// <summary>
	/// Indicates that a search by tag is specified.
	/// </summary>
	SearchByTag(2),

	/// <summary>
	/// Indicates that a search for people is specified.
	/// </summary>
	SearchByPeople(3),

	/// <summary>
	/// Indicates that a search by keyword is specified.
	/// </summary>
	SearchByKeyword(4),

	/// <summary>
	/// Indicates a request for the highest album the current user can view.
	/// </summary>
	HighestAlbumUserCanView(5),

	/// <summary>
	/// Indicates the most recently added content objects.
	/// </summary>
	MostRecentlyAdded(6),

	/// <summary>
	/// Indicates that a search by rating is specified.
	/// </summary>
	SearchByRating(7),

    /// <summary>
	/// Indicates that a search by approval is specified.
	/// </summary>
	SearchByApproval(8);
	
	private final int contentObjectSearchType;
    
    private ContentObjectSearchType(int contentObjectSearchType) {
        this.contentObjectSearchType = contentObjectSearchType;
    }	
}

