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
/// Specifies the visual transition effect to use when moving from one content object to another.
/// These values map to the jQuery UI effects: http://docs.jquery.com/UI/Effects
/// </summary>
public enum ContentObjectTransitionType{
	/// <summary>
	/// No visual transition effect.
	/// </summary>
	None(0),
	/// <summary>
	/// Fading from the old to the new content object.
	/// </summary>
	Blind(1),
	Bounce(2),
	Clip(3),
	Drop(4),
	Explode(5),
	Fade(6),
	Fold(7),
	Highlight(8),
	Puff(9),
	Pulsate(10),
	Scale(11),
	Shake(12),
	Size(13),
	Slide(14),
	Transfer(15);
	
	private final int contentObjectTransitionType;
    
    private ContentObjectTransitionType(int contentObjectTransitionType) {
        this.contentObjectTransitionType = contentObjectTransitionType;
    }
    
    public int value() {
    	return contentObjectTransitionType;
    }
	
	/// <summary>
	/// Determines if the transitionType parameter is one of the defined enumerations. This method is more efficient than using
	/// <see cref="Enum.IsDefined" />, since <see cref="Enum.IsDefined" /> uses reflection.
	/// </summary>
	/// <param name="transitionType">An instance of <see cref="ContentObjectTransitionType" /> to test.</param>
	/// <returns>Returns true if transitionType is one of the defined items in the enumeration; otherwise returns false.</returns>
	public static boolean isValidContentObjectTransitionType(ContentObjectTransitionType transitionType){
		switch (transitionType)	{
			case None:
			case Blind:
			case Bounce:
			case Clip:
			case Drop:
			case Explode:
			case Fade:
			case Fold:
			case Highlight:
			case Puff:
			case Pulsate:
			case Scale:
			case Shake:
			case Size:
			case Slide:
			case Transfer:
				break;

			default:
				return false;
		}
		return true;
	}
	
	public static ContentObjectTransitionType getContentObjectTransitionType(String contentObjectTransitionType) {
		for(ContentObjectTransitionType value : ContentObjectTransitionType.values()) {
			if (value.toString().equalsIgnoreCase(contentObjectTransitionType))
				return value;
		}
		
		return ContentObjectTransitionType.None;
	}
	
    public static ContentObjectTransitionType parse(String contentObjectTransitionType) {
		int val = StringUtils.toInteger(contentObjectTransitionType);
		for(ContentObjectTransitionType value : ContentObjectTransitionType.values()) {
			if (value.value() == val)
				return value;
		}
		
		return ContentObjectTransitionType.valueOf(contentObjectTransitionType);
	}
}