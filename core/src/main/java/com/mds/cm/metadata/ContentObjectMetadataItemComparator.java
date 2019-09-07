package com.mds.cm.metadata;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.mds.core.exception.BusinessException;

import java.util.Iterator;

/// <summary>
/// Defines a method for comparing two instances of <see cref="ContentObjectMetadataItem" /> objects. The items are compared using
/// the <see cref="IMetadataDefinition.Sequence" /> property of the <see cref="IMetadataDefinitionCollection" /> passed to the
/// constructor.
/// </summary>
/// <remarks>Instances of <see cref="IMetadataDefinitionCollection" /> are sorted according to the sequence defined in the 
/// gallery setting <see cref="IGallerySettings.MetadataDisplaySettings" />. That is, this class looks up the corresponding
/// metadata item in this property and uses its <see cref="IMetadataDefinition.Sequence" /> property for the comparison.</remarks>
public class ContentObjectMetadataItemComparator implements Comparator<ContentObjectMetadataItem>, Serializable {
	
	/*@Override
    public int compare(ContentObjectMetadataItem contentObjectMetadataItem1, ContentObjectMetadataItem contentObjectMetadataItem2) {
        return contentObjectMetadataItem1.getName().compareTo(contentObjectMetadataItem2.getName());
    }*/
	
	private MetadataDefinitionCollection _metadataDisplayOptions;

	/// <summary>
	/// Initializes a new instance of the <see cref="ContentObjectMetadataItemComparer"/> class. The items are compared using
	/// the <see cref="IMetadataDefinition.Sequence" /> property of the <paramref name="metadataDisplayOptions" /> parameter.
	/// </summary>
	/// <param name="metadataDisplayOptions">The metadata display options.</param>
	public ContentObjectMetadataItemComparator(MetadataDefinitionCollection metadataDisplayOptions){
		_metadataDisplayOptions = metadataDisplayOptions;
	}

	/// <summary>
	/// Compares the two instances and returns a value indicating their sort relation to each other.
	/// -1: obj1 is less than obj2
	/// 0: obj1 is equal to obj2
	/// 1: obj1 is greater than obj2
	/// </summary>
	/// <param name="x">One of the instances to compare.</param>
	/// <param name="y">One of the instances to compare.</param>
	/// <returns>Returns in integer indicating the objects' sort relation to each other.</returns>
	@Override
	public int compare(ContentObjectMetadataItem x, ContentObjectMetadataItem y){
		if (x == null){
			// If obj1 is null and obj2 is null, they're equal.
			// If obj1 is null and obj2 is not null, obj2 is greater.
			return (y == null ? 0 : -1);
		}else{
			if (y == null){
				return 1; // obj1 is not null and obj2 is null, so obj1 is greater.
			}

			// Neither is null. Look up the display settings for each item and sort by its associated sequence property.
			MetadataDefinition obj1MetadataDefinition = _metadataDisplayOptions.find(x.getMetadataItemName());
			MetadataDefinition obj2MetadataDefinition = _metadataDisplayOptions.find(y.getMetadataItemName());

			if ((obj1MetadataDefinition != null) && (obj2MetadataDefinition != null)){
				return Integer.compare(obj1MetadataDefinition.Sequence, obj2MetadataDefinition.Sequence);
			}else{
				// Can't find one of the display settings. This should never occur because the IMetadataDefinitionCollection should 
				// have an entry for every value of the MetadataItemName enumeration.
				throw new BusinessException(MessageFormat.format("The IMetadataDefinitionCollection instance passed to the ContentObjectMetadataItemComparer constructor did not have an item corresponding to one of these MetadataItemName enum values: {0}, {1}. This collection should contain an item for every enum value.", x.getMetadataItemName(), y.getMetadataItemName()));
			}
		}
	}
}
