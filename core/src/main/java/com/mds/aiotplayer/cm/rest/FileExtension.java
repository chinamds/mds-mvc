/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.rest;

/// <summary>
/// A client-optimized object that represents a file extension.
/// </summary>
public class FileExtension{
	/// <summary>
	/// Gets or sets the text representation of a file extension (e.g. ".jpg", "All video").
	/// </summary>
	/// <value>The text.</value>
	public String Text;

	/// <summary>
	/// Gets or sets the file extension (e.g. ".jpg", "*video").
	/// </summary>
	/// <value>The value.</value>
	public String Value;
}
