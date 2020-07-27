package com.mds.aiotplayer.cm.rest;

import javax.xml.bind.annotation.XmlRootElement;

import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.core.MetadataItemName;

/// <summary>
/// A client-optimized object that stores a piece of information describing a gallery object.
/// </summary>
@XmlRootElement(name = "MetaItem")
public class MetaItemRest{
	public MetaItemRest() {}
	
	public MetaItemRest(long id, long contentId, MetadataItemName mTypeId, ContentObjectType gTypeId, String desc, String value, boolean isEditable) {
		this.Id = id;
		this.ContentId = contentId;
		this.MTypeId = mTypeId.value();
		this.GTypeId = gTypeId.getValue();
		this.Desc = desc;
		this.Value = value;
		this.IsEditable = isEditable;
	}
	/// <summary>
	/// Gets the unique ID for this instance. Maps to MetadataId in the Metadata table.
	/// </summary>
	/// <value>An integer</value>
	public long Id;
	public long getId(){
		return this.Id;
	}
	
	public void setId(long id){
		this.Id = id;
	}

	/// <summary>
	/// Gets or sets a value that indentifies the content object or album this instance is associated with.
	/// Refer to <see cref="GTypeId" /> to determine which type of ID it is.
	/// </summary>
	/// <value>The value that uniquely indentifies the content object or album this instance is associated with.</value>
	public long ContentId;

	/// <summary>
	/// Gets a value that uniquely identifies the type of metadata item for this instance
	/// (e.g. Filename, date picture taken, etc). The value maps to the numerical value of the
	/// <see cref="MetadataItemName" /> enumeration, which also maps to MetaName in the Metadata table.
	/// </summary>
	/// <value>An integer</value>
	public int MTypeId;

	/// <summary>
	/// Gets a value that identifies the type of gallery item this instance describes. (e.g. album, image, etc).
	/// The value maps to the numerical value of the <see cref="MDS.Business.GalleryObjectType" /> enumeration.
	/// </summary>
	/// <value>An integer</value>
	public int GTypeId;

	/// <summary>
	/// Gets the description of the metadata item. Examples: "File name", "Date picture taken"
	/// </summary>
	/// <value>A String.</value>
	public String Desc;

	/// <summary>
	/// Gets the value of the metadata item. Examples: "MyImageFilename.jpg", "Jan 30, 2014 9:38:21 AM"
	/// </summary>
	/// <value>A String.</value>
	public String Value;

	/// <summary>
	/// Gets a value indicating whether this instance is editable.
	/// </summary>
	/// <value>
	/// <c>true</c> if this instance is editable; otherwise, <c>false</c>.
	/// </value>
	public boolean IsEditable;
}