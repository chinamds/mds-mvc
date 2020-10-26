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
/// Specifies the style of slide show used.
/// </summary>
public enum SlideShowType{
	/// <summary>
	/// The default value to use when the slide show type is unknown or it is not relevant to specify.
	/// </summary>
	NotSet(0),
	/// <summary>
	/// Specifies that slide show images are displayed in their normal position within the page. Use this
	/// when it would be inappropriate for the <see cref="FullScreen" /> option to take over the 
	/// entire screen area.
	/// </summary>
	Inline (1),
	/// <summary>
	/// Specifies that the slide show is shown using a full screen viewer.
	/// </summary>
	FullScreen(2);
	
	private final int slideShowType;
    
    private SlideShowType(int slideShowType) {
        this.slideShowType = slideShowType;
    }
    
    public int value() {
    	return slideShowType;
    }
    
    public static SlideShowType getSlideShowType(String slideShowType) {
		for(SlideShowType value : SlideShowType.values()) {
			if (value.toString().equalsIgnoreCase(slideShowType))
				return value;
		}
		
		return SlideShowType.NotSet;
	}
    
    public static SlideShowType parse(String slideShowType) {
		int val = StringUtils.toInteger(slideShowType);
		for(SlideShowType value : SlideShowType.values()) {
			if (value.value() == val)
				return value;
		}
		
		return SlideShowType.valueOf(slideShowType);
	}
}