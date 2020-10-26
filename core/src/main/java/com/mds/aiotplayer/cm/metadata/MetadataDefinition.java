/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.metadata;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Date;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mds.aiotplayer.core.MetadataItemName;

/// <summary>
/// Represents the definition of a type of metadata that is associated with content objects. Note that this is not an actual
/// piece of metadata, but rather defines the behavior of metadata stored in <see cref="ContentObjectMetadataItem" />.
/// </summary>
public class MetadataDefinition implements Comparable<MetadataDefinition>, Serializable{
	public MetadataDefinition() {}
	/// <summary>
	/// Initializes a new instance of the <see cref="MetadataDefinition" /> class.
	/// </summary>
	/// <param name="metadataItem">The metadata item.</param>
	/// <param name="displayName">The user-friendly name that describes this metadata item (e.g. "Date picture taken")</param>
	/// <param name="isVisibleForAlbum">If set to <c>true</c> metadata items belonging to albums are visible
	/// in the user interface.</param>
	/// <param name="isVisibleForContentObject">If set to <c>true</c> metadata items belonging to media
	/// objects are visible in the user interface.</param>
	/// <param name="isEditable">If set to <c>true</c> metadata items of this type are editable by the user.</param>
	/// <param name="sequence">Indicates the display order of the metadata item.</param>
	/// <param name="defaultValue">The template to use when adding a metadata item for a new album or content object.</param>
	public MetadataDefinition(MetadataItemName metadataItem, String displayName, boolean isVisibleForAlbum, boolean isVisibleForContentObject, boolean isEditable, int sequence, String defaultValue){
		MetadataItem = metadataItem.value();
		DisplayName = displayName;
		IsVisibleForAlbum= isVisibleForAlbum;
		IsVisibleForContentObject = isVisibleForContentObject;
		IsEditable = isEditable;
		Sequence = sequence;
		DefaultValue = defaultValue;
	}

	/// <summary>
	/// Gets or sets the name of the metadata item.
	/// </summary>
	/// <value>The metadata item.</value>
	public int MetadataItem;
	
	@JsonIgnore
	public MetadataItemName getMetadataItemName() {
		return MetadataItemName.getMetadataItemName(MetadataItem);
	}
	
	public void setMetadataItemName(MetadataItemName metadataItem) {
		this.MetadataItem = metadataItem.value();
	}

	/// <summary>
	/// Gets the String representation of the <see cref="MetadataItem" /> property.
	/// </summary>
	/// <value>A String.</value>
	@JsonProperty(value = "Name")
	public String getName() {
		return getMetadataItemName().toString(); 
	}
	
	public void setName(String name) {
	}

	/// <summary>
	/// Gets or sets the user-friendly name to apply to this metadata item.
	/// </summary>
	/// <value>A String.</value>
	public String DisplayName;

	/// <summary>
	/// Gets or sets a value indicating whether metadata items of this type are visible for albums.
	/// </summary>
	/// <value><c>true</c> if metadata items of this type are visible for albums; otherwise, <c>false</c>.</value>
	public boolean IsVisibleForAlbum;

	/// <summary>
	/// Gets or sets a value indicating whether metadata items of this type are visible in the gallery.
	/// </summary>
	/// <value><c>true</c> if metadata items of this type are visible in the gallery; otherwise, <c>false</c>.</value>
	public boolean IsVisibleForContentObject;

	/// <summary>
	/// Gets or sets a value indicating whether this metadata item can be edited by the user. The user
	/// must also have permission to edit the album or content object.
	/// </summary>
	/// <value><c>true</c> if this metadata item can be edited by the user; otherwise, <c>false</c>.</value>
	public boolean IsEditable;

	/// <summary>
	/// Gets or sets the template to use when adding a metadata item for a new album or content object.
	/// Values of the <see cref="MetadataItemName" /> can be used as replacement parameters.
	/// Example: "{IsoSpeed} - {LensAperture}"
	/// </summary>
	/// <value>A String.</value>
	public String DefaultValue;

	/// <summary>
	/// Gets or sets the order this metadata item is to be displayed in relation to other metadata items.
	/// </summary>
	/// <value>The order this metadata item is to be displayed in relation to other metadata items.</value>
	public int Sequence;

	/// <summary>
	/// Gets or sets the gallery ID this metadata definition is associated with.
	/// </summary>
	/// <value>The gallery ID this metadata definition is associated with.</value>
	//[Newtonsoft.Json.JsonIgnore]
	//public int GalleryId;

	/// <summary>
	/// Gets the data type of the metadata item. Returns either <see cref="DateTime" /> or <see cref="System.String" />.
	/// </summary>
	/// <value>The type of the metadata item.</value>
	@JsonIgnore
	public Class<?> getDataType(){
		switch (getMetadataItemName()){
			case DateAdded:
			case DateFileCreated:
			case DateFileCreatedUtc:
			case DateFileLastModified:
			case DateFileLastModifiedUtc:
			case DatePictureTaken:
			case ApprovalDate:
				return Date.class;
			default:
				return String.class;
		}
	}

	//#region IComparable
	
	/// <summary>
	/// Compares the current object with another object of the same type.
	/// </summary>
	/// <param name="other">An object to compare with this object.</param>
	/// <returns>
	/// A 32-bit signed integer that indicates the relative order of the objects being compared. The return value has the following meanings: Value Meaning Less than zero This object is less than the <paramref name="other"/> parameter.Zero This object is equal to <paramref name="other"/>. Greater than zero This object is greater than <paramref name="other"/>.
	/// </returns>
	public int compareTo(MetadataDefinition other){
		if (other == null)
			return 1;
		else{
			return Integer.compare(Sequence, other.Sequence);
		}
	}

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

        MetadataDefinition pojo = (MetadataDefinition)o;
        return (new EqualsBuilder()
             .append(Sequence, pojo.Sequence)
             ).isEquals();
	}

	//#endregion

	/// <summary>
	/// Serves as a hash function for a particular type.
	/// </summary>
	/// <returns>
	/// A hash code for the current <see cref="MetadataDefinition"/>.
	/// </returns>
	public int hashCode()
	{
		return   new  HashCodeBuilder( 17 ,  37 )
	             .append(MetadataItem)
	             .toHashCode();
	}
	
	public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("MetadataItem").append("='").append(MetadataItem).append("', ");
        sb.append("]");
        
        return sb.toString();
    }
}
