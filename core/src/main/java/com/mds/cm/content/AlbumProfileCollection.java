package com.mds.cm.content;

import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;

import com.mds.core.exception.ArgumentNullException;


/// <summary>
/// A collection of <see cref="AlbumProfile" /> objects.
/// </summary>
public class AlbumProfileCollection extends HashMap<Long, AlbumProfile>{
	/// <summary>
	/// Adds the specified item.
	/// </summary>
	/// <param name="item">The item.</param>
	/// <exception cref="System.ArgumentNullException">Thrown when <paramref name="item" /> is null.</exception>
	public void addAlbumProfile(AlbumProfile item){
		if (item == null)
			throw new ArgumentNullException("item", "Cannot add null to an existing AlbumSortDefinitionCollection. Items.Count = " + size());

		put(item.AlbumId, item);
	}

	/// <summary>
	/// Adds the <paramref name="items" /> to the current collection.
	/// </summary>
	/// <param name="items">The items to add to the current collection.</param>
	/// <exception cref="System.ArgumentNullException">Thrown when <paramref name="items" /> is null.</exception>
	public void addRange(Iterable<AlbumProfile> items){
		if (items == null)
			throw new ArgumentNullException("items");

		for (AlbumProfile item : items)	{
			put(item.AlbumId, item);
		}
	}

	/// <summary>
	/// Find the album profile in the collection that matches the specified <paramref name="albumId" />. If no matching object is found,
	/// null is returned.
	/// </summary>
	/// <param name="albumId">The ID for the album to find.</param>
	/// <returns>Returns an <see cref="AlbumProfile" />object from the collection that matches the specified <paramref name="albumId" />,
	/// or null if no matching object is found.</returns>
	public AlbumProfile find(long albumId){
		return get(albumId);
	}

	/// <summary>
	/// Generates as String representation of the items in the collection.
	/// </summary>
	/// <returns>Returns a String representation of the items in the collection.</returns>
	public String serialize() throws JsonProcessingException{
		//return Newtonsoft.Json.JsonConvert.SerializeObject(base.Items);
		return new ObjectMapper().writeValueAsString(this); //todo
	}

	/// <summary>
	/// Perform a deep copy of this collection.
	/// </summary>
	/// <returns>Returns a deep copy of this collection.</returns>
	public AlbumProfileCollection copy(){
		AlbumProfileCollection itemCollectionCopy = new AlbumProfileCollection();

		for(AlbumProfile item : this.values()){
			itemCollectionCopy.put(item.AlbumId, item.Copy());
		}

		return itemCollectionCopy;
	}

	/// <summary>
	/// Gets the key for item.
	/// </summary>
	/// <param name="item">The item.</param>
	/// <returns>System.Int32.</returns>
	/// <exception cref="System.ArgumentNullException">Thrown when <paramref name="item" /> is null.</exception>
	protected long getKeyForItem(AlbumProfile item)	{
		if (item == null)
			throw new ArgumentNullException("item");

		return item.AlbumId;
	}
}
