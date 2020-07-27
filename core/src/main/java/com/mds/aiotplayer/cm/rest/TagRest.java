package com.mds.aiotplayer.cm.rest;

/// <summary>
/// A client-optimized object representing a tag or person.
/// </summary>
public class TagRest{
	/// <summary>
	/// Gets or sets the value of the tag or person.
	/// </summary>
	/// <value>The value.</value>
	//[JsonProperty(PropertyName = "value")]
	public String Value;

	/// <summary>
	/// Gets or sets the number of times this tag is used in the gallery.
	/// </summary>
	/// <value>The count.</value>
	//[JsonProperty(PropertyName = "count")]
	public int Count;
}
