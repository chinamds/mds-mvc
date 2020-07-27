package com.mds.aiotplayer.cm.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import com.mds.aiotplayer.core.exception.ArgumentNullException;

/// <summary>
/// A collection of <see cref="GalleryBo" /> objects.
/// </summary>
public class GalleryBoCollection extends ArrayList<GalleryBo>
{
	/// <summary>
	/// Initializes a new instance of the <see cref="GalleryBoCollection"/> class.
	/// </summary>
	public GalleryBoCollection(){
		super(new ArrayList<GalleryBo>());
	}

	/// <summary>
	/// Sort the objects in this collection based on the <see cref="GalleryBo.getGalleryId()" /> property.
	/// </summary>
	@SuppressWarnings("unchecked")
	public void sort()	{
		// We know galleries is actually a List<GalleryBo> because we passed it to the constructor.
		sort(new Comparator(){
			@Override
			public int compare(Object o1, Object o2) {
				// TODO Auto-generated method stub
				if (((GalleryBo)o2).getGalleryId()>(((GalleryBo)o1).getGalleryId()))
					return -1;
				else if (((GalleryBo)o2).getGalleryId() < (((GalleryBo)o1).getGalleryId()))
					return 1;
				else
					return 0;
				
			}
		});
	}

	/// <summary>
	/// Adds the galleries to the current collection.
	/// </summary>
	/// <param name="galleries">The galleries to add to the current collection.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="galleries" /> is null.</exception>
	public void AddRange(Iterable<GalleryBo> galleries)	{
		if (galleries == null)
			throw new ArgumentNullException("galleries");

		addAll((Collection<? extends GalleryBo>) galleries);
	}

	/// <summary>
	/// Find the gallery in the collection that matches the specified <paramref name="galleryId" />. If no matching object is found,
	/// null is returned.
	/// </summary>
	/// <param name="galleryId">The ID that uniquely identifies the gallery.</param>
	/// <returns>Returns an <see cref="GalleryBo" />object from the collection that matches the specified <paramref name="galleryId" />,
	/// or null if no matching object is found.</returns>
	public GalleryBo findById(long galleryId){
		return this.stream().filter(gallery->gallery.getGalleryId() == galleryId).findFirst().orElse(null);
	}

	/// <summary>
	/// Creates a new, empty instance of an <see cref="GalleryBo" /> object. This method can be used by code that only has a 
	/// reference to the interface layer and therefore cannot create a new instance of an object on its own.
	/// </summary>
	/// <returns>Returns a new, empty instance of an <see cref="GalleryBo" /> object.</returns>
	public GalleryBo createEmptyGalleryBoInstance(){
		return new GalleryBo();
	}

	/// <summary>
	/// Creates a new, empty instance of an <see cref="GalleryBoCollection" /> object. This method can be used by code that only has a 
	/// reference to the interface layer and therefore cannot create a new instance of an object on its own.
	/// </summary>
	/// <returns>Returns a new, empty instance of an <see cref="GalleryBoCollection" /> object.</returns>
	public GalleryBoCollection createEmptyGalleryBoCollection(){
		return new GalleryBoCollection();
	}

	/// <summary>
	/// Creates a new collection containing deep copies of the items it contains.
	/// </summary>
	/// <returns>Returns a new collection containing deep copies of the items it contains.</returns>
	public GalleryBoCollection copy(){
		GalleryBoCollection copy = new GalleryBoCollection();
		for (GalleryBo gallery : this){
			copy.add(gallery.copy());
		}

		return copy;
	}
	
	/// <summary>
	/// Determines whether the <paramref name="item"/> is already a member of the collection. An object is considered a member
	/// of the collection if they both have the same <see cref="IGallery.GalleryId" />.
	/// </summary>
	/// <param name="item">An <see cref="IGallery"/> to determine whether it is a member of the current collection.</param>
	/// <returns>Returns <c>true</c> if <paramref name="item"/> is a member of the current collection;
	/// otherwise returns <c>false</c>.</returns>
	public boolean contains(GalleryBo item){
		if (item == null)
			return false;

		for (GalleryBo galleryInCollection : this){
			if (galleryInCollection.getGalleryId() == item.getGalleryId()){
				return true;
			}
		}
		
		return false;
	}


	/// <summary>
	/// Adds the specified gallery.
	/// </summary>
	/// <param name="item">The gallery to add.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="item" /> is null.</exception>
	public void addGalleryBo(GalleryBo item)
	{
		if (item == null)
			throw new ArgumentNullException("item", "Cannot add null to an existing GalleryBoCollection. Items.Count = " + size());

		add(item);
	}
}
