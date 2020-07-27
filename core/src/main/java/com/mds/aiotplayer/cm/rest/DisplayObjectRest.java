package com.mds.aiotplayer.cm.rest;

import com.fasterxml.jackson.annotation.JsonInclude;

/// <summary>
/// A client-optimized object that contains information about a particular view of a content object.
/// </summary>
@JsonInclude(value=JsonInclude.Include.NON_DEFAULT)
public class DisplayObjectRest {
	public DisplayObjectRest() {}
	
	public DisplayObjectRest(int viewSize, int viewType, String htmlOutput, String scriptOutput, int width, int height, String url) {
		this.ViewSize = viewSize;
		this.ViewType = viewType;
		this.HtmlOutput = htmlOutput;
		this.ScriptOutput = scriptOutput;
		this.Width = width;
		this.Height = height;
		this.Url = url;
	}
	/// <summary>
	/// The size of this display object. Maps to the <see cref="DisplayObjectType" /> enumeration, so that
	/// 0=Unknown, 1=Thumbnail, 2=Optimized, 3=Original, 4=External, etc.
	/// </summary>
	public int ViewSize;

	/// <summary>
	/// The type of this display object.  Maps to the <see cref="MimeTypeCategory" /> enumeration, so that
	/// 0=NotSet, 1=Other, 2=Image, 3=Video, 4=Audio
	/// </summary>
	public int ViewType;

	/// <summary>
	/// The HTML fragment that renders this content object.
	/// </summary>
	public String HtmlOutput;

	/// <summary>
	/// The ECMA script fragment that renders this content object.
	/// </summary>
	public String ScriptOutput;

	/// <summary>
	/// The width, in pixels, of this content object.
	/// </summary>
	public int Width;

	/// <summary>
	/// The height, in pixels, of this content object.
	/// </summary>
	public int Height;

	/// <summary>
	/// Gets or sets the path to the content object.
	/// </summary>
	public String Url;
}
