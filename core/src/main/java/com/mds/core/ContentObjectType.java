package com.mds.core;

import org.apache.commons.lang.StringUtils;

/// <summary>
/// Specifies the type of the content object.
/// </summary>
public enum ContentObjectType
{
	/// <summary>
	/// Specifies that no content object type has been assigned.
	/// </summary>
	NotSpecified(0),
	/// <summary>
	/// Gets all possible content object types.
	/// </summary>
	All(1),
	/// <summary>
	/// Gets all content object types except the Album type.
	/// </summary>
	ContentObject(2),
	/// <summary>
	/// Gets the Album content object type.
	/// </summary>
	Album(3),
	/// <summary>
	/// Gets the Image content object type.
	/// </summary>
	Image(4),
	/// <summary>
	/// Gets the Audio content object type.
	/// </summary>
	Audio(5),
	/// <summary>
	/// Gets the Video content object type.
	/// </summary>
	Video(6),
	/// <summary>
	/// Gets the Generic content object type.
	/// </summary>
	Generic(7),
	/// <summary>
	/// Gets the External content object type.
	/// </summary>
	External(8),
	/// <summary>
	/// Gets the Unknown content object type.
	/// </summary>
	Unknown(9),
	/// <summary>
	/// Specifies no content object type.
	/// </summary>
	None(10);
	
	private final int contentObjectType;
    
    private ContentObjectType(int contentObjectType) {
        this.contentObjectType = contentObjectType;
    }
    
    public int getValue() {
		return contentObjectType;
	}

    /// <summary>
	/// Determines if the contentObjectType parameter is one of the defined enumerations. This method is more efficient than using
	/// <see cref="Enum.IsDefined" />, since <see cref="Enum.IsDefined" /> uses reflection.
	/// </summary>
	/// <param name="contentObjectType">An instance of <see cref="ContentObjectType" /> to test.</param>
	/// <returns>Returns true if contentObjectType is one of the defined items in the enumeration; otherwise returns false.</returns>
	public static boolean IsValidContentObjectType(ContentObjectType contentObjectType){
		switch (contentObjectType){
			case NotSpecified:
			case All:
			case ContentObject:
			case Album:
			case Image:
			case Audio:
			case Video:
			case Generic:
			case External:
			case Unknown:
			case None:
				break;

			default:
				return false;
		}
		return true;
	}

	/// <summary>
	/// Parses the string into an instance of <see cref="ContentObjectType" />. If <paramref name="contentObjectType"/>
	/// is null, empty, or an invalid value, then <paramref name="defaultFilter" /> is returned.
	/// </summary>
	/// <param name="contentObjectType">The content object type to parse into an instance of <see cref="ContentObjectType" />.</param>
	/// <param name="defaultFilter">The value to return if <paramref name="contentObjectType" /> is invalid.</param>
	/// <returns>Returns an instance of <see cref="ContentObjectType" />.</returns>
	public static ContentObjectType parse(String contentObjectType, ContentObjectType defaultFilter){
		return ContentObjectType.valueOf(contentObjectType);
	}
	
	public static ContentObjectType getContentObjectType(int contentObjectType){
		for(ContentObjectType value : ContentObjectType.values()) {
			if (value.getValue() == contentObjectType)
				return value;
		}
		
		return ContentObjectType.NotSpecified;
	}
}

