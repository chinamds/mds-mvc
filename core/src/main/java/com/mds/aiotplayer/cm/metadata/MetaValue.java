package com.mds.aiotplayer.cm.metadata;

/// <summary>
/// Represents a metadata value. It is composed of two main properties - the raw value extracted
/// from the media file and the formatted, user-friendly version.
/// </summary>

public class MetaValue
{
	public String RawValue;
	public String FormattedValue;
	
	/// <summary>
	/// Initializes a new instance of the <see cref="MetaValue"/> class.
	/// </summary>
	/// <param name="formattedValue">The formatted, user-friendly value of the meta item.</param>
	/// <param name="rawValue">The raw value as it extracted from the media file. Omit or set to
	/// null when not applicable.</param>
	public MetaValue(String formattedValue, String rawValue){
		RawValue = rawValue;
		FormattedValue = formattedValue;
	}
	
	public MetaValue(String formattedValue)	{
		RawValue = null;
		FormattedValue = formattedValue;
	}

	/// <summary>
	/// Gets the raw value as it extracted from the media file. This value will be null in these cases:
	/// (1) The meta item does not exist in the media file. (2) The meta item is a composite of
	/// multiple meta values (e.g. <see cref="MDS.Business.Metadata.MetadataItemName.GpsLocationWithMapLink" />)
	/// </summary>
	public String getRawValue() { 
		return RawValue; 
	}

	/// <summary>
	/// Gets or sets the formatted, user-friendly value of the meta item.
	/// </summary>
	public String getFormattedValue(){ 
		return FormattedValue; 
	}
	
	public void setFormattedValue(String formattedValue){ 
		this.FormattedValue = formattedValue; 
	}
}
