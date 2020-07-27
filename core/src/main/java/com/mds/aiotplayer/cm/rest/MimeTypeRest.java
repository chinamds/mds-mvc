package com.mds.aiotplayer.cm.rest;

/// <summary>
/// A client-optimized object representing a MIME type associated with a file's extension.
/// </summary>
public class MimeTypeRest{
	/// <summary>
	/// Gets or sets a value indicating whether the MIME type is enabled.
	/// </summary>
	public boolean Enabled;

	/// <summary>
	/// Gets the file extension this mime type is associated with, including the period (e.g. ".jpg", ".avi").
	/// </summary>
	/// <value>The file extension this mime type is associated with.</value>
	public String Extension;

	/// <summary>
	/// Gets the full mime type. This is the <see cref="MajorType"/> concatenated with the <see cref="Subtype"/>, with a '/' between them
	/// (e.g. image/jpeg, video/quicktime).
	/// </summary>
	/// <value>The full mime type.</value>
	public String FullType;
}
