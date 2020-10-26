/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.metadata;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.model.ContentObject;

/// <summary>
/// Represents an item of metadata for a gallery object.
/// </summary>

public class ContentObjectMetadataItem implements Serializable{
	//#region Private Fields

	private long contentObjectMetadataId;
	private MetadataItemName metadataItemName;
	private ContentObjectBo contentObject;
	private MetadataDefinition metaDefinition;
	private String description;
	private String rawValue;
	private String value;
	private boolean hasChanges;
	private boolean isVisible;
	private boolean isDeleted;

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="ContentObjectMetadataItem" /> class.
	/// </summary>
	/// <param name="contentObjectMetadataId">The value that uniquely indentifies this metadata item.</param>
	/// <param name="contentObject">The gallery object this metadata item applies to.</param>
	/// <param name="rawValue">The raw value of the metadata item. Typically this is the value extracted from 
	/// the metadata of the media file.</param>
	/// <param name="value">The value of the metadata item (e.g. "F5.7", "1/500 sec.").</param>
	/// <param name="hasChanges">if set to <c>true</c> this object has changes that have not been persisted to the database.</param>
	/// <param name="metaDef">The meta definition.</param>
	public ContentObjectMetadataItem(long contentObjectMetadataId, ContentObjectBo contentObject, String rawValue, String value, boolean hasChanges, MetadataDefinition metaDef){
		this.contentObjectMetadataId = contentObjectMetadataId;
		this.contentObject = contentObject;
		this.metadataItemName = metaDef.getMetadataItemName();
		this.description = metaDef.DisplayName;
		this.rawValue = rawValue;
		this.value = value;
		this.hasChanges = hasChanges;
		this.metaDefinition = metaDef;
		this.isVisible = false;
		this.isDeleted = false;
	}

	//#endregion

	//#region Public Properties

	/// <summary>
	/// Gets or sets a value that uniquely indentifies this metadata item.
	/// </summary>
	/// <value>The value that uniquely indentifies this metadata item.</value>
	public long getContentObjectMetadataId()	{
		return contentObjectMetadataId; 
	}
	
	public void setContentObjectMetadataId(long contentObjectMetadataId){
		this.contentObjectMetadataId = contentObjectMetadataId; 
	}

	/// <summary>
	/// Gets or sets the object this instance applies to.
	/// </summary>
	/// <value>The object this instance applies to.</value>
	public ContentObjectBo getContentObject() { 
		return this.contentObject;
	}
	
	public void setContentObject(ContentObjectBo contentObject) {
		this.contentObject = contentObject;
	}

	/// <summary>
	/// Gets or sets the description of the metadata item (e.g. "Exposure time", "Camera model"). Setting this to a new
	/// value causes <see cref="HasChanges" /> to be <c>true</c>.
	/// </summary>
	/// <value>The description of the metadata item.</value>
	public String getDescription(){
		return description;
	}
	
	public void setDescription(String description){
		if (this.description != description){
			this.description = description;
			hasChanges = true;
			//contentObject.HasChanges = true;
		}
	}

	/// <summary>
	/// Gets or sets the raw value of the metadata item. Typically this is the value extracted from the metadata of the
	/// media file. Setting this to a new value causes <see cref="HasChanges" /> to be <c>true</c>.
	/// </summary>
	/// <value>The value of the metadata item.</value>
	public String getRawValue()	{
		return rawValue;
	}
	
	public void setRawValue(String rawValue){
		if (this.rawValue != rawValue){
			this.rawValue = rawValue;
			hasChanges = true;
			//ContentObject.HasChanges = true;
		}
	}

	/// <summary>
	/// Gets or sets the value of the metadata item (e.g. "F5.7", "1/500 sec."). Setting this to a new
	/// value causes <see cref="HasChanges" /> to be <c>true</c>.
	/// </summary>
	/// <value>The value of the metadata item.</value>
	public String getValue(){
		return value;
	}
	
	public void setValue(String value){
		if (this.value != value){
			this.value = value;
			hasChanges = true;
			//ContentObject.HasChanges = true;
		}
	}

	/// <summary>
	/// Gets or sets a value indicating whether this metadata item is visible in the UI. Setting this to a new
	/// value does not affect <see cref="HasChanges" />.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if this metadata item is visible in the UI; otherwise, <c>false</c>.
	/// </value>
	public boolean getIsVisible(){
		return isVisible;
	}
	
	public void setIsVisible(boolean isVisible){
		this.isVisible = isVisible;
	}

	/// <summary>
	/// Gets a value indicating whether this metadata item is defined as being editable. The value is 
	/// retrieved from the <see cref="IMetadataDefinition" /> object for this item. The calling code
	/// must also verify the user has permission to edit the album or content object.
	/// </summary>
	/// <value><c>true</c> if this metadata item is defined as being editable; otherwise, <c>false</c>.</value>
	public boolean getIsEditable() throws UnsupportedContentObjectTypeException, InvalidGalleryException	{
		return CMUtils.loadGallerySetting(getContentObject().getGalleryId()).getMetadataDisplaySettings().find(metadataItemName).IsEditable;
	}

	/// <summary>
	/// Gets or sets a value indicating whether this instance is to be permanently removed from the data 
	/// store.
	/// </summary>
	/// <value>
	/// <c>true</c> if this instance is to be deleted the next time the gallery object is saved; 
	/// otherwise, <c>false</c>.
	/// </value>
	public boolean getIsDeleted(){
		return isDeleted;
	}
	
	public void setIsDeleted(boolean isDeleted){
		if (this.isDeleted != isDeleted){
			this.isDeleted = isDeleted;
			hasChanges = true;
			//ContentObject.HasChanges = true;
		}
	}

	/// <summary>
	/// Gets or sets the name of this metadata item. Setting this to a new
	/// value causes <see cref="HasChanges" /> to be <c>true</c>.
	/// </summary>
	/// <value>The name of the metadata item.</value>
	public MetadataItemName getMetadataItemName(){
		return metadataItemName;
	}
	
	public void setMetadataItemName(MetadataItemName metadataItemName)	{
		if (this.metadataItemName != metadataItemName){
			this.metadataItemName = metadataItemName;
			hasChanges = true;
			//ContentObject.HasChanges = true;
		}
	}

	/// <summary>
	/// Gets or sets the meta definition for this instance.
	/// </summary>
	/// <value>An instance of <see cref="IMetadataDefinition" />.</value>
	public MetadataDefinition getMetaDefinition() { 
		return metaDefinition;
	}
	
	public void setMetaDefinition(MetadataDefinition metaDefinition) {
		this.metaDefinition = metaDefinition;
	}

	/// <summary>
	/// Gets or sets a value indicating whether this object has changes that have not been persisted to the database.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if this instance has changes; otherwise, <c>false</c>.
	/// </value>
	public boolean getHasChanges() {
		return hasChanges;
	}
	
	public void setHasChanges(boolean hasChanges){
		this.hasChanges = hasChanges;
	}

	//#endregion

	//#region Public Methods

	/// <summary>
	/// Perform a deep copy of this metadata item.
	/// </summary>
	/// <returns>
	/// Returns a deep copy of this metadata item.
	/// </returns>
	public ContentObjectMetadataItem Copy(){
		return CMUtils.createMetadataItem(Long.MIN_VALUE, contentObject, rawValue, value, true, metaDefinition);
	}

	//#endregion

	//#region IComparable

	/// <summary>
	/// Compares the current object with another object of the same type.
	/// </summary>
	/// <param name="other">An object to compare with this object.</param>
	/// <returns>
	/// A 32-bit signed integer that indicates the relative order of the objects being compared. The return value has the following meanings: Value Meaning Less than zero This object is less than the <paramref name="other"/> parameter.Zero This object is equal to <paramref name="other"/>. Greater than zero This object is greater than <paramref name="other"/>.
	/// </returns>
	public boolean equals(Object o) {
		if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContentObjectMetadataItem pojo = (ContentObjectMetadataItem)o;
        return (new EqualsBuilder()
             .append(metadataItemName, pojo.metadataItemName)
             ).isEquals();
	}

	//#endregion

	/// <summary>
	/// Serves as a hash function for a particular type.
	/// </summary>
	/// <returns>
	/// A hash code for the current <see cref="ContentObjectMetadataItem"/>.
	/// </returns>
	public int hashCode(){
		return   new  HashCodeBuilder( 17 ,  37 )
	             .append(metadataItemName)
	             .toHashCode();
	}
	
	public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("metadataItemName").append("='").append(getMetadataItemName()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }
}
