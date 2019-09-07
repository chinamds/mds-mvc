package com.mds.core;

/// <summary>
/// Identifies the amount of rotation applied to a content object.
/// </summary>
public enum ContentObjectRotation
{
	/// <summary>
	/// Indicates that no rotation has been specified.
	/// </summary>
	NotSpecified(0),

	/// <summary>
	/// The current orientation is to be preserved.
	/// </summary>
	Rotate0(1),

	/// <summary>
	/// Rotate clockwise by 90 degrees.
	/// </summary>
	Rotate90(2),

	/// <summary>
	/// Rotate clockwise by 180 degrees.
	/// </summary>
	Rotate180(3),

	/// <summary>
	/// Rotate clockwise by 270 degrees.
	/// </summary>
	Rotate270(4);
	
	private final int contentObjectRotation;
    
    private ContentObjectRotation(int contentObjectRotation) {
        this.contentObjectRotation = contentObjectRotation;
    }

	public int getContentObjectRotation() {
		return contentObjectRotation;
	}	
}