package com.mds.aiotplayer.core;

import com.mds.aiotplayer.util.StringUtils;

/// <summary>
/// Specifies the data type of the meta data value after it has been extracted from the <see cref="System.Drawing.Imaging.PropertyItem" /> object and formatted
/// into a user-friendly value.
/// </summary>
public enum ExtractedValueType{
	///<summary>Specifies that the value is not defined.</summary>
	NotDefined(0),
	///<summary>Specifies that the value is a System.Byte[].</summary>
	ByteArray(1),
	///<summary>Specifies that the value is a System.Int64.</summary>
	Int64(2),
	///<summary>Specifies that the value is a System.Int64[].</summary>
	Int64Array(3),
	///<summary>Specifies that the value is a System.String.</summary>
	String(4),
	///<summary>Specifies that the value is an instance of MDS.Business.Fraction.</summary>
	Fraction(5),
	///<summary>Specifies that the value is an instance of MDS.Business.Fraction[].</summary>
	FractionArray(6);
	
	private final int extractedValueType;
    private ExtractedValueType(int extractedValueType) {
        this.extractedValueType = extractedValueType;
    }
}