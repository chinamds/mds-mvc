package com.mds.aiotplayer.cm.content;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;

/// <summary>
/// Represents a profile for a user in the current application.
/// </summary>
public class UserProfile implements Comparable<UserProfile>{
	public static final String ProfileNameEnableUserAlbum = "EnableUserAlbum";
	public static final String ProfileNameUserAlbumId = "UserAlbumId";
	public static final String ProfileNameAlbumProfiles = "AlbumProfiles";
	public static final String ProfileNameContentObjectProfiles = "ContentObjectProfiles";
	
	//#region Private Fields

	private UserGalleryProfileCollection galleryProfiles;
	private AlbumProfileCollection albumProfiles;
	private ContentObjectProfileCollection contentObjectProfiles;

	//#endregion

	//#region Properties

	/// <summary>
	/// Gets or sets the account name of the user these profile settings belong to.
	/// </summary>
	/// <value>The account name of the user.</value>
	public String UserName;

	/// <summary>
	/// Gets a collection of album preferences for this user. Guaranteed to not return null.
	/// </summary>
	/// <value>An instance of <see cref="AlbumProfileCollection" />.</value>
	public AlbumProfileCollection getAlbumProfiles(){
		return this.albumProfiles;
	}

	/// <summary>
	/// Gets a collection of content object preferences for this user. Guaranteed to not return null.
	/// </summary>
	/// <value>An instance of <see cref="ContentObjectProfileCollection" />.</value>
	public ContentObjectProfileCollection getContentObjectProfiles(){
		return this.contentObjectProfiles;
	}

	/// <summary>
	/// Gets the collection of gallery profiles for the user. A gallery profile is a set of properties for a user that 
	/// are specific to a particular gallery. Guaranteed to not return null.
	/// </summary>
	/// <value>The gallery profiles.</value>
	//[Newtonsoft.Json.JsonConverter(typeof(GalleryProfileConverter))]
	public UserGalleryProfileCollection getGalleryProfiles(){
		return this.galleryProfiles;
	}

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="UserProfile" /> class.
	/// </summary>
	public UserProfile(){
		this.galleryProfiles = new UserGalleryProfileCollection();
		this.albumProfiles = new AlbumProfileCollection();
		this.contentObjectProfiles = new ContentObjectProfileCollection();
	}

	//#endregion

	//#region Methods

	/// <summary>
	/// Retrieves the profile for the specified <paramref name="userName" />. Guaranteed to not return null.
	/// </summary>
	/// <param name="userName">Name of the user.</param>
	/// <returns>An instance of <see cref="UserProfile" />.</returns>
	public static UserProfile retrieveFromDataStore(String userName){
		return CMUtils.retrieveFromDataStore(userName);
	}


	/// <summary>
	/// Persist the specified <paramref name="profile"/> to the data store.
	/// </summary>
	/// <param name="profile">The profile to persist to the data store.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="profile" /> is null.</exception>
	public static void save(UserProfile profile) throws JsonProcessingException{
		CMUtils.saveUserProfile(profile);
	}
	
	/// <summary>
	/// Gets the gallery profile for the specified <paramref name="galleryId" />. Guaranteed to not return null.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>A UserGalleryProfile containing profile information.</returns>
	public UserGalleryProfile getGalleryProfile(long galleryId) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		UserGalleryProfile profile = this.galleryProfiles.findByGalleryId(galleryId);

		if (profile == null){
			profile = createDefaultProfile(galleryId);

			this.galleryProfiles.add(profile);
		}

		return profile;
	}

	/// <summary>
	/// Creates a new instance containing a deep copy of the items it contains.
	/// </summary>
	/// <returns>Returns a new instance containing a deep copy of the items it contains.</returns>
	public UserProfile copy(){
		UserProfile copy = new UserProfile();

		copy.UserName = this.UserName;
		copy.galleryProfiles.addRange(this.galleryProfiles.copy());
		copy.albumProfiles.addRange(this.albumProfiles.copy().values());
		copy.contentObjectProfiles.addRange(this.contentObjectProfiles.copy().values());

		return copy;
	}

	//#endregion

	//#region Private Functions

	private UserGalleryProfile createDefaultProfile(long galleryId) throws UnsupportedContentObjectTypeException, InvalidGalleryException	{
		UserGalleryProfile profile = new UserGalleryProfile(galleryId);
		profile.setUserAlbumId(0); // Redundant since this is the default value, but this is for clarity to programmer
		profile.setEnableUserAlbum(CMUtils.loadGallerySetting(galleryId).getEnableUserAlbumDefaultForUser());

		return profile;
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
	public int compareTo(UserProfile obj)
	{
		if (obj == null)
			return 1;
		else{
			return this.UserName.compareTo(obj.UserName);
		}
	}

	//#endregion	
}



