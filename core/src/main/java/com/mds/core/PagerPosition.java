package com.mds.core;

import com.mds.core.exception.ArgumentException;
import com.mds.util.StringUtils;

/// <summary>
/// Specifies the position for a pager rendered to a UI. A pager is a control that allows a user to navigate
/// large collections of objects. It typically has next and previous buttons, and my contain buttons for quickly
/// accessing intermediate pages.
/// </summary>
public enum PagerPosition{
	/// <summary>
	/// A pager positioned at the top of the control.
	/// </summary>
	Top (0),
	/// <summary>
	/// A pager positioned at the bottom of the control.
	/// </summary>
	Bottom(1),
	/// <summary>
	/// Pagers positioned at both the top and the bottom of the control.
	/// </summary>
	TopAndBottom(2);
	
	private final int pagerPosition;
    

    private PagerPosition(int pagerPosition) {
        this.pagerPosition = pagerPosition;
    }
    
    public int value() {
    	return pagerPosition;
    }

	/// <summary>
	/// Determines if the <paramref name="pagerPosition"/> is one of the defined enumerations. This method is more efficient than using
	/// <see cref="Enum.IsDefined" />, since <see cref="Enum.IsDefined" /> uses reflection.
	/// </summary>
	/// <param name="pagerPosition">An instance of <see cref="PagerPosition" /> to test.</param>
	/// <returns>Returns true if <paramref name="pagerPosition"/> is one of the defined items in the enumeration; otherwise returns false.</returns>
	public static boolean isValidPagerPosition(PagerPosition pagerPosition)	{
		switch (pagerPosition)		{
			case Top:
			case Bottom:
			case TopAndBottom:
				break;

			default:
				return false;
		}
		return true;
	}

	/// <summary>
	/// Parses the string into an instance of <see cref="PagerPosition" />. If <paramref name="pagerPosition"/> is null or empty, an 
	/// <see cref="ArgumentException"/> is thrown.
	/// </summary>
	/// <param name="pagerPosition">The pager position to parse into an instance of <see cref="PagerPosition" />.</param>
	/// <returns>Returns an instance of <see cref="PagerPosition" />.</returns>
	public static PagerPosition ParsePagerPosition(String pagerPosition){
		if (StringUtils.isBlank(pagerPosition))
			throw new ArgumentException("pagerPosition", pagerPosition);

		return PagerPosition.valueOf(pagerPosition);
	}
	
    public static PagerPosition parse(String pagerPosition) {
		int val = StringUtils.toInteger(pagerPosition);
		for(PagerPosition value : PagerPosition.values()) {
			if (value.value() == val)
				return value;
		}
		
		return PagerPosition.valueOf(pagerPosition);
	}
}
