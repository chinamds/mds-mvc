/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.core;

/// <summary>
/// Specifies the type of processing to be executed on a content object in the content object conversion queue.
/// </summary>
public enum ContentQueueItemConversionType
{
	/// <summary>
	/// Specifies the unknown media queue conversion type.
	/// </summary>
	Unknown(0),
	/// <summary>
	/// Specifies that an optimized media file is to be created.
	/// </summary>
	CreateOptimized(1),
	/// <summary>
	/// Specifies that a video is to be rotated.
	/// </summary>
	RotateVideo(2);
	
	private final int contentQueueItemConversionType;
    
    private ContentQueueItemConversionType(int contentQueueItemConversionType) {
        this.contentQueueItemConversionType = contentQueueItemConversionType;
    }	
}