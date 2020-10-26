/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.content;

import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mds.aiotplayer.core.exception.ArgumentNullException;

/// <summary>
/// A collection of <see cref="ContentObjectProfile" /> objects.
/// </summary>
public class ContentObjectProfileCollection extends HashMap<Long, ContentObjectProfile> {
	/// <summary>
	/// Adds the specified item.
	/// </summary>
	/// <param name="item">The item.</param>
	/// <exception cref="System.ArgumentNullException">Thrown when <paramref name="item" /> is null.</exception>
	public void add(ContentObjectProfile item){
		if (item == null)
			throw new ArgumentNullException("item", "Cannot add null to an existing ContentObjectProfileCollection. Items.Count = " + size());

		put(item.ContentObjectId, item);
	}

	/// <summary>
	/// Adds the <paramref name="items" /> to the current collection.
	/// </summary>
	/// <param name="items">The items to add to the current collection.</param>
	/// <exception cref="System.ArgumentNullException">Thrown when <paramref name="items" /> is null.</exception>
	public void addRange(Iterable<ContentObjectProfile> items)	{
		if (items == null)
			throw new ArgumentNullException("items");

		for (ContentObjectProfile item : items)	{
			put(item.ContentObjectId, item);
		}
	}

	/// <summary>
	/// Find the content object profile in the collection that matches the specified <paramref name="contentObjectId" />. If no matching object is found,
	/// null is returned.
	/// </summary>
	/// <param name="contentObjectId">The ID for the content object to find.</param>
	/// <returns>Returns an <see cref="ContentObjectProfile" />object from the collection that matches the specified <paramref name="contentObjectId" />,
	/// or null if no matching object is found.</returns>
	public ContentObjectProfile find(long contentObjectId){
		return get(contentObjectId);
	}

	/// <summary>
	/// Generates as String representation of the items in the collection.
	/// </summary>
	/// <returns>Returns a String representation of the items in the collection.</returns>
	public String serialize() throws JsonProcessingException{
		//return Newtonsoft.Json.JsonConvert.SerializeObject(Items);
		return new ObjectMapper().writeValueAsString(this);
	}

	/// <summary>
	/// Perform a deep copy of this collection.
	/// </summary>
	/// <returns>Returns a deep copy of this collection.</returns>
	public ContentObjectProfileCollection copy(){
		ContentObjectProfileCollection itemCollectionCopy = new ContentObjectProfileCollection();

		for (ContentObjectProfile item : this.values())	{
			itemCollectionCopy.add(item.copy());
		}

		return itemCollectionCopy;
	}

	/// <summary>
	/// Gets the key for item.
	/// </summary>
	/// <param name="item">The item.</param>
	/// <returns>System.Int32.</returns>
	/// <exception cref="System.ArgumentNullException">Thrown when <paramref name="item" /> is null.</exception>
	protected long GetKeyForItem(ContentObjectProfile item){
		if (item == null)
			throw new ArgumentNullException("item");

		return item.ContentObjectId;
	}
}
