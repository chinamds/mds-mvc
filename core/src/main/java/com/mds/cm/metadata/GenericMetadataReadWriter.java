package com.mds.cm.metadata;

import com.mds.cm.content.ContentObjectBo;

/// <summary>
/// Provides functionality for reading and writing metadata to or from a generic gallery object.
/// </summary>
public class GenericMetadataReadWriter extends MediaObjectMetadataReadWriter{
	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="GenericMetadataReadWriter" /> class.
	/// </summary>
	/// <param name="contentObject">The gallery object.</param>
	public GenericMetadataReadWriter(ContentObjectBo contentObject){
		super(contentObject);
	}

	//#endregion

	//#region Methods

	// NOTE: To perform metadata extraction that applies only to generic content objects,
	// uncomment the following and write the desired code.

	///// <summary>
	///// Gets the metadata value for the specified <paramref name="metaName" />.
	///// </summary>
	///// <param name="metaName">Name of the metadata item to retrieve.</param>
	///// <returns>Returns a String.</returns>
	//public override String GetMetaValue(MetadataItemName metaName)
	//{
	//	return base.GetMetaValue(metaName);
	//}

	//#endregion
}