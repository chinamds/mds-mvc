/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.content;

import java.util.EventObject;
import java.util.Objects;

/// <summary>
/// Provides data for the <see cref="Gallery.GalleryCreated" /> event.
/// </summary>
public class GalleryCreatedEventArgs extends EventObject{
	private final long galleryId;

	/// <summary>
	/// Initializes a new instance of the <see cref="GalleryCreatedEventArgs"/> class.
	/// </summary>
	/// <param name="galleryId">The ID of the newly created gallery.</param>
	public GalleryCreatedEventArgs(Object source, long galleryId){
		super(source);
		this.galleryId = galleryId;
	}

	/// <summary>
	/// Gets the ID of the newly created gallery.
	/// </summary>
	/// <value>The gallery ID.</value>
	public long getGalleryId(){
		return this.galleryId;
	}
}
