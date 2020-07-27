package com.mds.aiotplayer.core;

import com.mds.aiotplayer.util.StringUtils;

/// <summary>
/// Specifies the type of the display object.
/// </summary>
public enum DisplayObjectType{
	/// <summary>
	/// Gets the Unknown display object type.
	/// </summary>
	Unknown(0),
	/// <summary>
	/// Gets the Thumbnail display object type.
	/// </summary>
	Thumbnail(1),
	/// <summary>
	/// Gets the Optimized display object type.
	/// </summary>
	Optimized(2),
	/// <summary>
	/// Gets the Original display object type.
	/// </summary>
	Original(3),
	/// <summary>
	/// Gets the display object type that represents a content object that is external to MDS System (e.g. YouTube, Silverlight).
	/// </summary>
	External(4);
	
    private final int displayObjectType;
    

    private DisplayObjectType(int displayObjectType) {
        this.displayObjectType = displayObjectType;
    }
    
    public int value() {
    	return displayObjectType;
    }
    	
    /// <summary>
	/// Determines if the displayType parameter is one of the defined enumerations. This method is more efficient than using
	/// <see cref="Enum.IsDefined" />, since <see cref="Enum.IsDefined" /> uses reflection.
	/// </summary>
	/// <param name="displayType">A <see cref="DisplayObjectType" /> to test.</param>
	/// <returns>Returns true if displayType is one of the defined items in the enumeration; otherwise returns false.</returns>
    public static boolean isValidDisplayObjectType(DisplayObjectType displayType){
		switch (displayType){
			case External:
			case Optimized:
			case Original:
			case Thumbnail:
			case Unknown:
				break;

			default:
				return false;
		}
		return true;
	}
    
    public static DisplayObjectType parse(String displayType) {
		int val = StringUtils.toInteger(displayType);
		for(DisplayObjectType value : DisplayObjectType.values()) {
			if (value.value() == val)
				return value;
		}
		
		return DisplayObjectType.valueOf(displayType);
	}
}
