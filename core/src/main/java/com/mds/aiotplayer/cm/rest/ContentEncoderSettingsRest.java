/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.rest;

/// <summary>
/// A client-optimized object that represents media encoder settings.
/// </summary>
public class ContentEncoderSettingsRest{
	/// <summary>
	/// Gets or sets the file extension of the media file used as the source for an encoding. 
	/// Example: .avi, .dv
	/// </summary>
	/// <value>A String.</value>
	public String SourceFileExtension;

	/// <summary>
	/// Gets or sets the file extension of the media file created as a result of the encoding. 
	/// Example: .mp4, .flv
	/// </summary>
	/// <value>A String.</value>
	public String DestinationFileExtension;

	/// <summary>
	/// Gets or sets the arguments to pass to the encoder utility. May contain the following 
	/// replacement tokens: {SourceFilePath}, {DestinationFilePath}, {GalleryResourcesPath},
	/// {BinPath}, {AspectRatio}, {Width}, {Height}
	/// </summary>
	/// <value>A String.</value>
	public String EncoderArguments;
}