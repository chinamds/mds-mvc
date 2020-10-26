/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.core;

import com.mds.aiotplayer.util.StringUtils;

//
// Summary:
//     Specifies alignment of content on the drawing surface.
public enum ContentAlignment{
    //
    // Summary:
    //     Content is vertically aligned at the top, and horizontally aligned on the left.
    TopLeft(1),
    //
    // Summary:
    //     Content is vertically aligned at the top, and horizontally aligned at the center.
    TopCenter(2),
    //
    // Summary:
    //     Content is vertically aligned at the top, and horizontally aligned on the right.
    TopRight(4),
    //
    // Summary:
    //     Content is vertically aligned in the middle, and horizontally aligned on the
    //     left.
    MiddleLeft(16),
    //
    // Summary:
    //     Content is vertically aligned in the middle, and horizontally aligned at the
    //     center.
    MiddleCenter(32),
    //
    // Summary:
    //     Content is vertically aligned in the middle, and horizontally aligned on the
    //     right.
    MiddleRight(64),
    //
    // Summary:
    //     Content is vertically aligned at the bottom, and horizontally aligned on the
    //     left.
    BottomLeft(256),
    //
    // Summary:
    //     Content is vertically aligned at the bottom, and horizontally aligned at the
    //     center.
    BottomCenter(512),
    //
    // Summary:
    //     Content is vertically aligned at the bottom, and horizontally aligned on the
    //     right.
    BottomRight(1024);
	
	private final int contentAlignment;
    
    private ContentAlignment(int contentAlignment) {
        this.contentAlignment = contentAlignment;
    }
    
    public int value() {
    	return contentAlignment;
    }

	/// <summary>
	/// Determines if the <paramref name="contentAlignment" /> parameter is one of the defined enumerations. This method is 
	/// more efficient than using <see cref="Enum.IsDefined" />, since <see cref="Enum.IsDefined" /> uses reflection.
	/// </summary>
	/// <param name="contentAlignment">A of <see cref="System.Drawing.ContentAlignment" /> to test.</param>
	/// <returns>Returns true if contentAlignment is one of the defined items in the enumeration; otherwise returns false.</returns>
	public static boolean isValidContentAlignment(ContentAlignment contentAlignment){
		switch (contentAlignment){
			case BottomCenter:
			case BottomLeft:
			case BottomRight:
			case MiddleCenter:
			case MiddleLeft:
			case MiddleRight:
			case TopCenter:
			case TopLeft:
			case TopRight:
				break;
	
			default:
				return false;
		}
		return true;
	}
	
	public static ContentAlignment getContentAlignment(String contentAlignment) {
		for(ContentAlignment value : ContentAlignment.values()) {
			if (value.toString().equalsIgnoreCase(contentAlignment))
				return value;
		}
		
		return ContentAlignment.TopLeft;
	}
	
    public static ContentAlignment parse(String contentAlignment) {
		int val = StringUtils.toInteger(contentAlignment);
		for(ContentAlignment value : ContentAlignment.values()) {
			if (value.value() == val)
				return value;
		}
		
		return ContentAlignment.valueOf(contentAlignment);
	}
}