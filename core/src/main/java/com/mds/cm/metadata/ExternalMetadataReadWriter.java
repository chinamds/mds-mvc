package com.mds.cm.metadata;

import com.mds.cm.content.ContentObjectBo;
import com.mds.core.MetadataItemName;

/// <summary>
/// Provides functionality for reading and writing metadata to or from an external content object.
/// </summary>
public class ExternalMetadataReadWriter extends MediaObjectMetadataReadWriter{
	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="ExternalMetadataReadWriter" /> class.
	/// </summary>
	/// <param name="contentObject">The gallery object.</param>
	public ExternalMetadataReadWriter(ContentObjectBo contentObject){
		super(contentObject);
	}

	//#endregion

	//#region Methods

	/// <summary>
	/// Gets the metadata value for the specified <paramref name="metaName" />.
	/// </summary>
	/// <param name="metaName">Name of the metadata item to retrieve.</param>
	/// <returns>An instance that implements <see cref="MetaValue" />.</returns>
	public MetaValue getMetaValue(MetadataItemName metaName){
		switch (metaName){
			case HtmlSource: return getHtmlContent();
			default:
				return super.getMetaValue(metaName);
		}
	}

	//#endregion

	//#region Functions

	private MetaValue getHtmlContent(){
		return new MetaValue(getContentObject().getOriginal().getExternalHtmlSource());
	}

	//#endregion
}