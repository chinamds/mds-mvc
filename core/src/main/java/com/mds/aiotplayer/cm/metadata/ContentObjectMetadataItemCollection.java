/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.model.Metadata;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.cm.util.CMUtils;

import java.util.Iterator;

/// <summary>
/// A collection of <see cref="ContentObjectMetadataItem" /> objects.
/// </summary>
public class ContentObjectMetadataItemCollection extends ArrayList<ContentObjectMetadataItem>{
	/// <summary>
	/// Initializes a new instance of the <see cref="ContentObjectMetadataItemCollection"/> class.
	/// </summary>
	public ContentObjectMetadataItemCollection(){
		super(new ArrayList<ContentObjectMetadataItem>());
	}

	/// <summary>
	/// Determines whether the <paramref name="item"/> is a member of the collection. An object is considered a member
	/// of the collection if the value of its <see cref="ContentObjectMetadataItem.MetadataItemName"/> property matches one in the existing collection.
	/// </summary>
	/// <param name="item">The <see cref="ContentObjectMetadataItem"/> to search for.</param>
	/// <returns>
	/// Returns <c>true</c> if <paramref name="item"/> is a member of the current collection;
	/// otherwise returns <c>false</c>.
	/// </returns>
	/// <overloads>
	/// Determines whether the collection contains a particular item.
	/// </overloads>
	public boolean containsMetadataItem(ContentObjectMetadataItem item)	{
		if (item == null)
			return false;

		for(ContentObjectMetadataItem metadataItemIterator : this)	{
			if (item.getMetadataItemName() == metadataItemIterator.getMetadataItemName()){
				return true;
			}
		}
		return false;
	}

	/// <summary>
	/// Determines whether the <paramref name="metadataItemName"/> is a member of the collection.
	/// </summary>
	/// <param name="metadataItemName">The <see cref="MetadataItemName"/> to search for.</param>
	/// <returns>Returns <c>true</c> if <paramref name="metadataItemName"/> is in the current collection;
	/// otherwise returns <c>false</c>.
	/// </returns>
	public boolean contains(MetadataItemName metadataItemName){
		return tryGetMetadataItem(metadataItemName) != null;
	}

	/// <summary>
	/// Adds an object to the end of the <see cref="T:System.Collections.ObjectModel.Collection`1" />.
	/// </summary>
	/// <param name="item">The object to be added to the end of the <see cref="T:System.Collections.ObjectModel.Collection`1" />. The value can be null for reference types.</param>
	public void addMetadataItem(ContentObjectMetadataItem item)	{
		item.getContentObject().setHasChanges(true);

		add(item);
	}

	/// <summary>
	/// Adds the metadata items to the current collection.
	/// </summary>
	/// <param name="contentObjectMetadataItems">The metadata items to add to the collection.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="contentObjectMetadataItems" /> is null.</exception>
	public void addRange(ContentObjectMetadataItemCollection contentObjectMetadataItems){
		if (contentObjectMetadataItems == null)
			throw new ArgumentNullException("contentObjectMetadataItems");

		addAll(contentObjectMetadataItems);
	}

	/// <summary>
	/// Apply the <paramref name="metadataDisplayOptions"/> to the items in the collection. This includes sorting the items and updating
	/// the <see cref="ContentObjectMetadataItem.IsVisible"/> property.
	/// </summary>
	/// <param name="metadataDisplayOptions">A collection of metadata definition items. Specify <see cref="IGallerySettings.MetadataDisplaySettings"/>
	/// for this parameter.</param>
	public void applyDisplayOptions(MetadataDefinitionCollection metadataDisplayOptions){
		// We know contentObjectMetadataItems is actually a List<ContentObjectMetadataItem> because we passed it to the constructor.
		sort(new ContentObjectMetadataItemComparator(metadataDisplayOptions));

		forEach(metaItem ->
			{
				MetadataDefinition metadataDef = metadataDisplayOptions.find(metaItem.getMetadataItemName());

				if (metaItem.getContentObject().getContentObjectType() == ContentObjectType.Album)
					metaItem.setIsVisible(metadataDef.IsVisibleForAlbum);
				else
					metaItem.setIsVisible(metadataDef.IsVisibleForContentObject);
			});
	}

	/// <summary>
	/// Gets the <see cref="ContentObjectMetadataItem"/> object that matches the specified
	/// <see cref="MetadataItemName"/>. The <paramref name="metadataItem"/>
	/// parameter remains null if no matching object is in the collection.
	/// </summary>
	/// <param name="metadataName">The <see cref="MetadataItemName"/> of the
	/// <see cref="ContentObjectMetadataItem"/> to get.</param>
	/// <param name="metadataItem">When this method returns, contains the <see cref="ContentObjectMetadataItem"/> associated with the
	/// specified <see cref="MetadataItemName"/>, if the key is found; otherwise, the
	/// parameter remains null. This parameter is passed uninitialized.</param>
	/// <returns>
	/// Returns true if the <see cref="ContentObjectMetadataItemCollection"/> contains an element with the specified
	/// <see cref="MetadataItemName"/>; otherwise, false.
	/// </returns>
	public ContentObjectMetadataItem tryGetMetadataItem(MetadataItemName metadataName){
		// We know contentObjectMetadataItems is actually a List<ContentObjectMetadataItem> because we passed it to the constructor.
		for(ContentObjectMetadataItem metaItem : this) {
			if (metaItem.getMetadataItemName() == metadataName) {
				return metaItem;
			}
		};

		return null;
	}

	/// <summary>
	/// Get a list of items whose metadata must be persisted to the data store, either because it has been added or because
	/// it has been modified. All ContentObjectMetadataItem whose HasChanges property are true are returned. This is called during a
	/// save operation to indicate which metadata items must be saved. Guaranteed to not return null. If no items
	/// are found, an empty collection is returned.
	/// </summary>
	/// <returns>
	/// Returns a list of items whose metadata must be updated with the metadata currently in the content object's file.
	/// </returns>
	public ContentObjectMetadataItemCollection getItemsToSave()	{
		// We know contentObjectMetadataItems is actually a List<ContentObjectMetadataItem> because we passed it to the constructor.
		ContentObjectMetadataItemCollection metadataItemsCollection = new ContentObjectMetadataItemCollection();
		forEach(metaItem->{
				if (metaItem.getHasChanges()){
					metadataItemsCollection.add(metaItem);
				}
			});

		return metadataItemsCollection;
	}

	/// <summary>
	/// Perform a deep copy of this metadata collection.
	/// </summary>
	/// <returns>
	/// Returns a deep copy of this metadata collection.
	/// </returns>
	public ContentObjectMetadataItemCollection copy(){
		ContentObjectMetadataItemCollection metaDataItemCollectionCopy = new ContentObjectMetadataItemCollection();

		for (ContentObjectMetadataItem metaDataItem : this)	{
			metaDataItemCollectionCopy.add(metaDataItem.Copy());
		}

		return metaDataItemCollectionCopy;
	}

	/// <summary>
	/// Gets the items in the collection that are visible to the UI. That is, get the items where <see cref="ContentObjectMetadataItem.IsVisible" />
	/// = <c>true</c>.
	/// </summary>
	/// <returns>Returns a list of items that are visible to the UI.</returns>
	public ContentObjectMetadataItemCollection getVisibleItems(){
		// We know contentObjectMetadataItems is actually a List<ContentObjectMetadataItem> because we passed it to the constructor.
		ContentObjectMetadataItemCollection metadataItemsCollection = new ContentObjectMetadataItemCollection();

		forEach(metaItem->
		{
			if (metaItem.getIsVisible()){
				metadataItemsCollection.add(metaItem);
			}
		});

		return metadataItemsCollection;
	}

	/// <summary>
	/// Converts the <paramref name="metaDtos" /> to an instance of <see cref="ContentObjectMetadataItemCollection" /> and
	/// returns it. An empty collection is returned if <paramref name="metaDtos" /> is null or empty. Guaranteed to not return null.
	/// </summary>
	/// <param name="contentObject">The gallery object the <paramref name="metaDtos" /> belong to.</param>
	/// <param name="metaDtos">An enumerable collection of <see cref="Data.MetadataDto" /> instances.</param>
	/// <returns>An instance of <see cref="ContentObjectMetadataItemCollection" />.</returns>
	public static ContentObjectMetadataItemCollection fromMetaDtos(ContentObjectBo contentObject, Iterable<Metadata> metaDtos) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		MetadataDefinitionCollection metaDefs = CMUtils.loadGallerySetting(contentObject.getGalleryId()).getMetadataDisplaySettings();

		ContentObjectMetadataItemCollection metadata = CMUtils.createMetadataCollection();

		if (!metaDefs.isEmpty() && metaDtos != null){
			for (Metadata mDto : metaDtos){
				metadata.add(CMUtils.createMetadataItem(
					mDto.getId(), 
					contentObject,
					mDto.getRawValue(),
					mDto.getValue().trim(),
					false,
					metaDefs.find(mDto.getMetaName())));
			}
		}

		return metadata;
	}
}
