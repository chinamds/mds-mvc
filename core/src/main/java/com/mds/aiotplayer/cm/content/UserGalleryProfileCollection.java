/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.content;

import java.util.ArrayList;

import com.mds.aiotplayer.core.exception.ArgumentNullException;

/// <summary>
/// A collection of <see cref="UserGalleryProfile" /> objects.
/// </summary>
public class UserGalleryProfileCollection extends ArrayList<UserGalleryProfile>{
	/// <summary>
	/// Initializes a new instance of the <see cref="UserGalleryProfileCollection"/> class.
	/// </summary>
	public UserGalleryProfileCollection(){
	}

	/// <summary>
	/// Determines whether the <paramref name="item"/> is already a member of the collection. An object is considered a member
	/// of the collection if they both have the same <see cref="UserGalleryProfile.GalleryId"/>.
	/// </summary>
	/// <param name="item">An <see cref="UserGalleryProfile"/> to determine whether it is a member of the current collection.</param>
	/// <returns>
	/// Returns <c>true</c> if <paramref name="item"/> is a member of the current collection;
	/// otherwise returns <c>false</c>.
	/// </returns>
	public boolean contains(UserGalleryProfile item){
		if (item == null)
			return false;
		
		for (UserGalleryProfile userAccountInCollection : this)	{
			if (userAccountInCollection.getGalleryId() == item.getGalleryId()){
				return true;
			}
		}
		return false;
	}

	/// <summary>
	/// Adds the specified user profile.
	/// </summary>
	/// <param name="item">The user profile to add.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="item" /> is null.</exception>
	public void addUserGalleryProfile(UserGalleryProfile item){
		if (item == null)
			throw new ArgumentNullException("item", "Cannot add null to an existing UserGalleryProfileCollection. Items.Count = " + size());

		add(item);
	}

	/// <summary>
	/// Adds the gallery profiles to the current collection.
	/// </summary>
	/// <param name="galleryProfiles">The gallery profiles to add to the current collection.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="galleryProfiles" /> is null.</exception>
	public void addRange(Iterable<UserGalleryProfile> galleryProfiles){
		if (galleryProfiles == null)
			throw new ArgumentNullException("galleryProfiles");

		for(UserGalleryProfile galleryProfile : galleryProfiles){
			add(galleryProfile);
		}
	}

	/// <summary>
	/// Find the user account in the collection that matches the specified <paramref name="galleryId" />. If no matching object is found,
	/// null is returned.
	/// </summary>
	/// <param name="galleryId">The ID of the gallery.</param>
	/// <returns>Returns an <see cref="UserGalleryProfile" />object from the collection that matches the specified <paramref name="galleryId" />,
	/// or null if no matching object is found.</returns>
	public UserGalleryProfile findByGalleryId(long galleryId){
		return this.stream().filter(p->p.getGalleryId() == galleryId).findFirst().orElse(null);
	}

	/// <summary>
	/// Creates a new instance of an <see cref="UserGalleryProfile"/> object. This method can be used by code that only has a
	/// reference to the interface layer and therefore cannot create a new instance of an object on its own.
	/// </summary>
	/// <param name="galleryId">The ID of the gallery.</param>
	/// <returns>
	/// Returns a new instance of an <see cref="UserGalleryProfile"/> object.
	/// </returns>
	public UserGalleryProfile createNewUserGalleryProfile(long galleryId){
		return new UserGalleryProfile(galleryId);
	}

	/// <summary>
	/// Creates a new collection containing deep copies of the items it contains.
	/// </summary>
	/// <returns>Returns a new collection containing deep copies of the items it contains.</returns>
	public UserGalleryProfileCollection copy(){
		UserGalleryProfileCollection copy = new UserGalleryProfileCollection();

		for (UserGalleryProfile galleryProfile : this){
			copy.add(galleryProfile.copy());
		}

		return copy;
	}
}
