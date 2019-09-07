package com.mds.cm.content;

import java.util.EventObject;
import java.util.Objects;

/// <summary>
/// Provides data for the events relating to <see cref="IGallerySettings" />.
/// </summary>
public class GallerySettingsEventArgs extends EventObject{
	private final long galleryId;

	/// <summary>
	/// Initializes a new instance of the <see cref="GallerySettingsEventArgs"/> class.
	/// </summary>
	/// <param name="galleryId">The gallery ID for the gallery related to the gallery settings.</param>
	public GallerySettingsEventArgs(Object source, long galleryId){
		super(source);
		this.galleryId = galleryId;
	}

	/// <summary>
	/// Gets the gallery ID for the gallery related to the gallery settings.
	/// </summary>
	/// <value>The gallery ID.</value>
	public long getGalleryId(){
		return this.galleryId;
	}
}
