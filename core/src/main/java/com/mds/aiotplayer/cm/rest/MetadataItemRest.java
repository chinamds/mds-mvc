/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.rest;

/// <summary>
/// A simple object that contains content object metadata information. This class is used to pass information between the browser and the web server
/// via AJAX callbacks.
/// </summary>
public class MetadataItemRest{
	/// <summary>
	/// Initializes a new instance of the <see cref="MetadataItemRest"/> class.
	/// </summary>
	private MetadataItemRest() {}

	/// <summary>
	/// Initializes a new instance of the <see cref="MetadataItemRest"/> class.
	/// </summary>
	/// <param name="description">The description.</param>
	/// <param name="value">The value.</param>
	public MetadataItemRest(String description, String value)
	{
		this.Description = description;
		this.Value = value;
	}

	/// <summary>
	/// The description of the metadata item.
	/// </summary>
	public String Description;

	/// <summary>
	/// The value of the metadata item.
	/// </summary>
	public String Value;
}
