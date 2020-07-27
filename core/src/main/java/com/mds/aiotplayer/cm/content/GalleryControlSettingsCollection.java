package com.mds.aiotplayer.cm.content;

import java.util.ArrayList;
import java.util.Collection;

import com.mds.aiotplayer.core.exception.ArgumentNullException;

/// <summary>
/// A collection of <see cref="GalleryControlSettings" /> objects. There is a maximum of one item for each instance of a Gallery
/// control that is used in an application. An item will exist in this collection only if at least one control-specific setting
/// has been saved for a particular control.
/// </summary>
public class GalleryControlSettingsCollection extends ArrayList<GalleryControlSettings>{
	/// <summary>
	/// Initializes a new instance of the <see cref="GallerySettingsCollection"/> class.
	/// </summary>
	public GalleryControlSettingsCollection()	{
		super(new ArrayList<GalleryControlSettings>());
	}

	/// <summary>
	/// Adds the gallery control settings to the current collection.
	/// </summary>
	/// <param name="galleryControlSettings">The gallery control settings to add to the current collection.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="galleryControlSettings" /> is null.</exception>
	public void addRange(Iterable<GalleryControlSettings> galleryControlSettings)	{
		if (galleryControlSettings == null)
			throw new ArgumentNullException("galleryControlSettings");
		
		addAll((Collection<? extends GalleryControlSettings>) galleryControlSettings);
	}

	/// <summary>
	/// Adds the specified gallery control settings.
	/// </summary>
	/// <param name="item">The gallery control settings to add.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="item" /> is null.</exception>
	public void Add(GalleryControlSettings item)
	{
		if (item == null)
			throw new ArgumentNullException("item", "Cannot add null to an existing GalleryControlSettingsCollection. Items.Count = " + size());

		add(item);
	}

	/// <summary>
	/// Find the gallery control settings in the collection that matches the specified <paramref name="controlId" />. If no matching object is found,
	/// null is returned.
	/// </summary>
	/// <param name="controlId">The ID that uniquely identifies the control containing the gallery.</param>
	/// <returns>Returns an <see cref="GalleryControlSettings" />object from the collection that matches the specified <paramref name="controlId" />,
	/// or null if no matching object is found.</returns>
	public GalleryControlSettings findByControlId(String controlId)	{
		return this.stream().filter(gallery->gallery.getControlId().equalsIgnoreCase(controlId)).findFirst().orElse(null);
	}
}
