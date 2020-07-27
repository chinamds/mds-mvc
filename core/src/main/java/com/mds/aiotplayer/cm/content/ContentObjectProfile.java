package com.mds.aiotplayer.cm.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mds.aiotplayer.cm.util.CMUtils;

/// <summary>
/// Defines an object that contains user preferences for a content object.
/// </summary>
public class ContentObjectProfile {
	//#region Properties

	/// <summary>
	/// Gets or sets the ID of the content object. Album IDs are not supported.
	/// </summary>
	/// <value>An <see cref="int" />.</value>
    @JsonProperty(value = "Id")
	public long ContentObjectId;

	/// <summary>
	/// Gets or sets the rating for the album or content object having ID <see cref="ContentObjectId" />.
	/// </summary>
	/// <value>A <see cref="String" />.</value>
	public String Rating;

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="ContentObjectProfile" /> class.
	/// </summary>
	public ContentObjectProfile(){
	}

	/// <summary>
	/// Initializes a new instance of the <see cref="ContentObjectProfile" /> class.
	/// </summary>
	/// <param name="mediayObjectId">The mediay object ID.</param>
	/// <param name="rating">The rating.</param>
	public ContentObjectProfile(long mediayObjectId, String rating)	{
		ContentObjectId = mediayObjectId;
		Rating = rating;
	}

	//#endregion

	//#region Methods

	/// <summary>
	/// Perform a deep copy of this item.
	/// </summary>
	/// <returns>An instance of <see cref="ContentObjectProfile" />.</returns>
	public ContentObjectProfile copy(){
		return CMUtils.createContentObjectProfile(ContentObjectId, Rating);
	}

	//#endregion
}
