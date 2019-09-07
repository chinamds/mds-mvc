package com.mds.cm.content;

import com.mds.common.utils.Reflections;

/// <summary>
/// Represents a set of properties for a user that are specific to a particular gallery.
/// </summary>
public class UserGalleryProfile implements Comparable<UserGalleryProfile>{
	//#region Private Fields

	private long galleryId;
	private long userAlbumId;
	private boolean enableUserAlbum;

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="UserGalleryProfile" /> class.
	/// </summary>
	public UserGalleryProfile()	{
	}

	/// <summary>
	/// Initializes a new instance of the <see cref="UserGalleryProfile"/> class.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	public UserGalleryProfile(long galleryId)	{
		this.galleryId = galleryId;
	}

	//#endregion

	//#region Properties

	/// <summary>
	/// Gets or sets the ID of the gallery the profile properties are associated with.
	/// </summary>
	/// <value>The gallery ID.</value>
	public long getGalleryId(){
		return this.galleryId;
	}
	
	public void setGalleryId(long galleryId){
		this.galleryId = galleryId;
	}

	/// <summary>
	/// Gets or sets the ID for the user's personal album (aka user album).
	/// </summary>
	/// <value>The ID for the user's personal album (aka user album).</value>
	public long getUserAlbumId(){
		return this.userAlbumId;
	}
	
	public void setUserAlbumId(long userAlbumId){
		this.userAlbumId = userAlbumId;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the user has enabled or disabled her personal album (aka user album).
	/// </summary>
	/// <value>
	/// A value indicating whether the user has enabled or disabled her personal album (aka user album).
	/// </value>
	public boolean getEnableUserAlbum()	{
		return this.enableUserAlbum;
	}
	
	public void setEnableUserAlbum(boolean enableUserAlbum)	{	
		this.enableUserAlbum = enableUserAlbum;
	}

	//#endregion

	//#region Methods

	/// <summary>
	/// Creates a new instance containing a deep copy of the items it contains.
	/// </summary>
	/// <returns>Returns a new instance containing a deep copy of the items it contains.</returns>
	public UserGalleryProfile copy(){
		UserGalleryProfile copy = new UserGalleryProfile(this.galleryId);

		copy.enableUserAlbum = this.enableUserAlbum;
		copy.userAlbumId = this.userAlbumId;

		return copy;
	}

	//#endregion

	//#region IComparable

	/// <summary>
	/// Compares the current instance with another object of the same type.
	/// </summary>
	/// <param name="obj">An object to compare with this instance.</param>
	/// <returns>
	/// A 32-bit signed integer that indicates the relative order of the objects being compared. The return value has these meanings: Value Meaning Less than zero This instance is less than <paramref name="obj"/>. Zero This instance is equal to <paramref name="obj"/>. Greater than zero This instance is greater than <paramref name="obj"/>.
	/// </returns>
	/// <exception cref="T:System.ArgumentException">
	/// 	<paramref name="obj"/> is not the same type as this instance. </exception>
	public int compareTo(UserGalleryProfile obj){
		if (obj == null)
			return 1;
		else{
			return Long.compare(this.galleryId, obj.getGalleryId());
		}
	}

	//#endregion
}