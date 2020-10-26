/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.core;

/// <summary>
/// Specifies how the Gallery user control should render content objects.
/// </summary>
public enum ViewMode
{
	/// <summary>
	/// The default value to use when the view mode is unknown or it is not relevant to specify.
	/// </summary>
	NotSet(0),
	/// <summary>
	/// Specifies that the entire contents of an album be displayed as a set of thumbnails.
	/// </summary>
	Multiple(1),
	/// <summary>
	/// Specifies that the content objects be displayed one at a time.
	/// </summary>
	Single(2),
	/// <summary>
	/// Specifies that the content objects be displayed one at a time in a random order.
	/// </summary>
	SingleRandom(3);
	
	private final int viewMode;
    
    private ViewMode(int viewMode) {
        this.viewMode = viewMode;
    }
}