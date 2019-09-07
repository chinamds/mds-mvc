package com.mds.cm.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mds.core.exception.ArgumentNullException;

/// <summary>
/// Represents a set of gallery-specific settings.
/// </summary>
public class GallerySettingsCollection extends ArrayList<GallerySettings>{
	/// <summary>
	/// Initializes a new instance of the <see cref="GallerySettingsCollection"/> class.
	/// </summary>
	public GallerySettingsCollection()	{
		super(new ArrayList<GallerySettings>());
	}

	/// <summary>
	/// Sort the objects in this collection based on the <see cref="GallerySettings.getGalleryId()"/> property.
	/// </summary>
	public void sort(){
		// We know galleries is actually a List<GallerySettings> because we passed it to the constructor.
		Collections.sort(this);
	}

	/// <summary>
	/// Adds the gallery settings to the current collection.
	/// </summary>
	/// <param name="gallerySettings">The gallery settings to add to the current collection.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="gallerySettings" /> is null.</exception>
	public void addRange(List<GallerySettings> gallerySettings){
		if (gallerySettings == null)
			throw new ArgumentNullException("gallerySettings");

		addAll(gallerySettings);
	}

	/// <summary>
	/// Find the gallery settings in the collection that matches the specified <paramref name="galleryId"/>. If no matching object is found,
	/// null is returned.
	/// </summary>
	/// <param name="galleryId">The ID that uniquely identifies the gallery.</param>
	/// <returns>
	/// Returns an <see cref="GallerySettings"/>object from the collection that matches the specified <paramref name="galleryId"/>,
	/// or null if no matching object is found.
	/// </returns>
	public GallerySettings findByGalleryId(long galleryId){
		return this.stream().filter(gallery->gallery.getGalleryId() == galleryId).findFirst().orElse(null);
	}

	/// <summary>
	/// Adds the specified gallery.
	/// </summary>
	/// <param name="item">The gallery to add.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="item" /> is null.</exception>
	public void addGallerySettings(GallerySettings item){
		if (item == null)
			throw new ArgumentNullException("item", "Cannot add null to an existing GallerySettingsCollection. Items.Count = " + size());

		add(item);
	}
}
