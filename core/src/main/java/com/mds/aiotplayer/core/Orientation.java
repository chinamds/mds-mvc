/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.core;

import com.mds.aiotplayer.util.StringUtils;

/// <summary>
/// Specifies the image orientation viewed in terms of rows and columns.
/// </summary>
/// <remarks>
/// For an explanation of these values, see http://www.impulseadventure.com/photo/exif-orientation.html
/// "Normal" | "Rotate90" | 
/// "Rotate180" | "Rotate270" | "FlipH" | "FlipV" | 
/// "FlipHRotate90" | "FlipVRotate90"
/// </remarks> 
public enum Orientation{
	///<summary>Indicates that no orientation has yet been assigned.</summary>
	NotInitialized(0, ""),
	///<summary>Indicates that no orientation value exists for an object.</summary>
	None(65535, "None"),
	///<summary>Indicates the item is right side up in a normal orientation.</summary>
	Normal(1, "Normal"),
	///<summary>Indicates the item is mirrored horizontally.</summary>
	Mirrored(2, "Mirrored"),
	///<summary>Indicates the item is upside down.</summary>
	Rotated180(3, "Rotated 180"),
	///<summary>Indicates the item is mirrored vertically.</summary>
	Flipped(4, "Flipped"),
	///<summary>Indicates the item is mirrored vertically and rotated 90 degrees clockwise.</summary>
	FlippedAndRotated90(5, "Flipped and rotated 90?CW"),
	///<summary>Indicates the item is rotated 90 degrees counter clockwise.</summary>
	Rotated270(6, "Rotated 90?CCW"),
	///<summary>Indicates the item is mirrored vertically and rotated 90 degrees counter clockwise .</summary>
	FlippedAndRotated270(7, "Flipped and rotated 90?CCW"),
	///<summary>Indicates the item is rotated 90 degrees clockwise.</summary>
	Rotated90(8, "Rotated 90?CW");
	
	private final int orientation;
	private final String description;
    private Orientation(int orientation, String description) {
        this.orientation = orientation;
        this.description = description;
    }
    
	public String getDescription() {
		return description;
	}
	
	public int getOrientation() {
		return orientation;
	}
	
	public static Orientation getOrientation(int orientation) {
		for(Orientation value : Orientation.values()) {
			if (value.getOrientation() == orientation)
				return value;
		}
		
		return Orientation.NotInitialized;
	}
}