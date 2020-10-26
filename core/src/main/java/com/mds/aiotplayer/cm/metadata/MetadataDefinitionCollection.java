/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.metadata;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mds.aiotplayer.core.MetadataItemName;

/// <summary>
/// A collection of <see cref="IMetadataDefinition" /> objects.
/// </summary>
public class MetadataDefinitionCollection extends HashMap<String, MetadataDefinition>{
	/// <summary>
	/// Adds the specified metadata definition.
	/// </summary>
	/// <param name="item">The metadata definition to add.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="item" /> is null.</exception>
	public void add(MetadataDefinition item){
		if (item == null)
			throw new ArgumentNullException("item", "Cannot add null to an existing MetadataDefinitionCollection. Items.Count = " + size());

		put(item.getMetadataItemName().toString(), item);
	}

	/// <summary>
	/// Find the metadata definition in the collection that matches the specified <paramref name="metadataItemName" />. If no matching object is found,
	/// null is returned.
	/// </summary>
	/// <param name="metadataItemName">The metadata item to find.</param>
	/// <returns>Returns an <see cref="IMetadataDefinition" />object from the collection that matches the specified <paramref name="metadataItemName" />,
	/// or null if no matching object is found.</returns>
	public MetadataDefinition find(MetadataItemName metadataItemName){
		return get(metadataItemName.toString());
	}

	/// <summary>
	/// Verify that an item exists in this collection for every enumeration value of 
	/// <see cref="MetadataItemName" />. If an item is missing, one is added with default values.
	/// This should be called after the collection is populated from the gallery settings. Doing this 
	/// validation guarantees that later calls to <see cref="IMetadataDefinitionCollection.Find" /> will 
	/// never fail and helps to automatically add items for newly added 
	/// <see cref="MetadataItemName" /> values. 
	/// </summary>
	public void validate()	{
		for (String item : keySet())	{
			if (!containsKey(item) && MetadataItemName.getMetadataItemName(item) != MetadataItemName.NotSpecified){
				put(item, new MetadataDefinition(MetadataItemName.getMetadataItemName(item), item, false, false, false, Integer.MAX_VALUE, StringUtils.EMPTY));
			}
		}

		////Remove after metadata defs are all created as desired
		//foreach (IMetadataDefinition metadataDef in base.Items)
		//{
		//	if (StringUtils.isBlank(metadataDef.DisplayName))
		//	{
		//		metadataDef.DisplayName = metadataDef.MetadataItem.ToString();
		//		metadataDef.DefaultValue = String.Concat("{", metadataDef.MetadataItem.ToString(), "}");
		//	}
		//}
	}

	/// <summary>
	/// Generates as String representation of the items in the collection.
	/// </summary>
	/// <returns>Returns a String representation of the items in the collection.</returns>
	public String serialize(){
		//return Newtonsoft.Json.JsonConvert.SerializeObject(base.Items);
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}

	/// <summary>
	/// When implemented in a derived class, extracts the key from the specified element.
	/// </summary>
	/// <returns>
	/// The key for the specified element.
	/// </returns>
	/// <param name="item">The element from which to extract the key.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="" /> is null.</exception>
	protected MetadataItemName getKeyForItem(MetadataDefinition item){
		if (item == null)
			throw new ArgumentNullException("item"); 
		
		return item.getMetadataItemName();
	}
}

