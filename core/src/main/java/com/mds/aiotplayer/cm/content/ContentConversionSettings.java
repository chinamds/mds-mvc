package com.mds.aiotplayer.cm.content;

import com.mds.aiotplayer.core.CancelToken;

/// <summary>
/// Contains settings for controlling the conversion of media instances from one format
/// to another.
/// </summary>
public class ContentConversionSettings
{
	/// <summary>
	/// Gets or sets the full path to the source file. Example: "D:\Content\Vacation\party.avi"
	/// Specify <see cref="String.Empty" /> if not applicable.
	/// </summary>
	/// <value>A string.</value>
	public String FilePathSource;

	/// <summary>
	/// Gets or sets the full path to the destination file. This file will be created during
	/// the conversion process. Example: "D:\Content\Vacation\party.avi"
	/// Specify <see cref="String.Empty" /> if not applicable.
	/// </summary>
	/// <value>A string.</value>
	public String FilePathDestination;

	/// <summary>
	/// Gets or sets the encoder setting. May be null.
	/// </summary>
	/// <value>An instance of <see cref="IContentEncoderSettings" />.</value>
	public ContentEncoderSettings EncoderSetting;

	/// <summary>
	/// Gets or sets the gallery ID.
	/// </summary>
	/// <value>An integer.</value>
	public long GalleryId;

	/// <summary>
	/// Gets or sets the media queue ID. Specify <see cref="Int32.MinValue" /> if not applicable.
	/// </summary>
	/// <value>An integer.</value>
	public long ContentQueueId;

	/// <summary>
	/// Gets or sets the timeout to apply to FFmpeg, in milliseconds.
	/// </summary>
	/// <value>An integer.</value>
	public int TimeoutMs;

	/// <summary>
	/// Gets or sets the ID of the content object that is being converted.
	/// Specify <see cref="Int32.MinValue" /> if not applicable.
	/// </summary>
	/// <value>An integer.</value>
	public long ContentObjectId;

	/// <summary>
	/// Gets or sets the width, in pixels, the file generated by this conversion process should have.
	/// </summary>
	/// <value>The width of the target.</value>
	public int TargetWidth;

	/// <summary>
	/// Gets or sets the height, in pixels, the file generated by this conversion process should have.
	/// </summary>
	/// <value>The height of the target.</value>
	public int TargetHeight;

	/// <summary>
	/// Gets or sets the output FFmpeg generates during the conversion.
	/// </summary>
	/// <value>The F fmpeg output.</value>
	public String FFmpegOutput;

	/// <summary>
	/// Gets or sets the arguments to provide to FFmpeg. Any replacement tokens 
	/// (e.g. {SourceFilePath}, {DestinationFilePath}, {GalleryResourcesPath}) should be replaced with their 
	/// actual values prior to assigning this property.
	/// /// </summary>
	/// <value>A string.</value>
	public String FFmpegArgs;

	/// <summary>
	/// Gets or sets a value indicating whether the destination file was created.
	/// </summary>
	/// <value><c>true</c> if the file was created; otherwise, <c>false</c>.</value>
	public boolean FileCreated;

	/// <summary>
	/// Gets or sets the cancellation token. Can be used to cancel the conversion process
	/// when it is running asynchronously.
	/// </summary>
	/// <value>An instance of <see cref="CancellationToken" />.</value>
	//public CancellationToken CancellationToken;
	public CancelToken CancellationToken;
}