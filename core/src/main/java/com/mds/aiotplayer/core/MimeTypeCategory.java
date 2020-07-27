package com.mds.aiotplayer.core;

import org.apache.commons.lang.StringUtils;

/// <summary>
/// Specifies the category to which this mime type belongs. This usually corresponds to the first portion of 
/// the full mime type description. (e.g. "image" if the full mime type is "image/jpeg") The one exception to 
/// this is the "Other" enumeration, which represents any category not represented by the others. If a value
/// has not yet been assigned, it defaults to the NotSet value.
/// </summary>
public enum MimeTypeCategory{
	/// <summary>
	/// Gets the NotSet mime type name, which indicates that no assignment has been made.
	/// </summary>
	NotSet( 0),
	/// <summary>
	/// Gets the Other mime type name.
	/// </summary>
	Other (1),
	/// <summary>
	/// Gets the Image mime type name.
	/// </summary>
	Image (2),
	/// <summary>
	/// Gets the Video mime type name.
	/// </summary>
	Video (3),
	/// <summary>
	/// Gets the Audio mime type name.
	/// </summary>
	Audio (4);
	
	private final int mimeTypeCategory;
    

    private MimeTypeCategory(int mimeTypeCategory) {
        this.mimeTypeCategory = mimeTypeCategory;
    }
    
    public int value() {
    	return mimeTypeCategory;
    }
	
	// <summary>
	/// Determines if the mimeTypeCategory parameter is one of the defined enumerations. This method is more efficient than using
	/// <see cref="Enum.IsDefined" />, since <see cref="Enum.IsDefined" /> uses reflection.
	/// </summary>
	/// <param name="mimeTypeCategory">An instance of <see cref="MimeTypeCategory" /> to test.</param>
	/// <returns>Returns true if mimeTypeCategory is one of the defined items in the enumeration; otherwise returns false.</returns>
	public static boolean isValidMimeTypeCategory(MimeTypeCategory mimeTypeCategory){
		switch (mimeTypeCategory){
			case NotSet:
			case Audio:
			case Image:
			case Other:
			case Video:
				break;

			default:
				return false;
		}
		return true;
	}

	/// <summary>
	/// Parses the string into an instance of <see cref="MimeTypeCategory" />. If <paramref name="mimeTypeCategory"/> is null or empty, then 
	/// MimeTypeCategory.NotSet is returned.
	/// </summary>
	/// <param name="mimeTypeCategory">The MIME type category to parse into an instance of <see cref="MimeTypeCategory" />.</param>
	/// <returns>Returns an instance of <see cref="MimeTypeCategory" />.</returns>
	public static MimeTypeCategory parseMimeTypeCategory(String mimeTypeCategory){
		if (StringUtils.isBlank(mimeTypeCategory)){
			return MimeTypeCategory.NotSet;
		}

		return MimeTypeCategory.valueOf(mimeTypeCategory);
	}
	
	public static MimeTypeCategory getMimeTypeCategory(String mimeTypeCategory){
		for(MimeTypeCategory value : MimeTypeCategory.values()) {
			if (value.toString().equalsIgnoreCase(mimeTypeCategory))
				return value;
		}
		
		return MimeTypeCategory.Other;
	}
}