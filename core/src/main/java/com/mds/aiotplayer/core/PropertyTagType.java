package com.mds.aiotplayer.core;

import com.mds.aiotplayer.util.StringUtils;

///<summary>
/// Specifies the data type of the values stored in the value data member of that same <see cref="System.Drawing.Imaging.PropertyItem" /> object.
///</summary>
public enum PropertyTagType{
	///<summary>Specifies that the format is 4 bits per pixel, indexed.</summary>
	PixelFormat4bppIndexed(0),
	///<summary>Specifies that the value data member is an array of bytes.</summary>
	Byte(1),
	///<summary>Specifies that the value data member is a null-terminated ASCII string. If you set the type data member of a <see cref="System.Drawing.Imaging.PropertyItem" /> object to PropertyTagType.ASCII, you should set the length data member to the length of the string including the NULL terminator. For example, the string HELLO would have a length of 6.</summary>
	ASCII(2),
	///<summary>Specifies that the value data member is an array of unsigned short (16-bit) integers.</summary>
	UnsignedShort(3),
	///<summary>Specifies that the value data member is an array of unsigned long (32-bit) integers.</summary>
	UnsignedInt(4),
	///<summary>Specifies that the value data member is an array of pairs of unsigned long (32-bit) integers. Each pair represents a fraction; the first integer is the numerator and the second integer is the denominator.</summary>
	UnsignedFraction(5),
	///<summary>Specifies that the value data member is an array of bytes that can hold values of any data type.</summary>
	Undefined(6),
	///<summary>Specifies that the value data member is an array of signed long (32-bit) integers.</summary>
	Int(7),
	///<summary>Specifies that the value data member is an array of pairs of signed long (32-bit) integers. Each pair represents a fraction; the first integer is the numerator and the second integer is the denominator.</summary>
	Fraction(10);
	
	private final int propertyTagType;
    private PropertyTagType(int propertyTagType) {
        this.propertyTagType = propertyTagType;
    }
}
