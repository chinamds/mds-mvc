/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mds.aiotplayer.cm.content.GalleryBo;
import com.mds.aiotplayer.cm.content.UserGalleryProfile;
import com.mds.aiotplayer.cm.content.UserProfile;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.common.mapper.JsonMapper;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.sys.util.UserUtils;

/// <summary>
/// Contains functionality related to managing the user profile.
/// </summary>
public final class ProfileUtils{
	//#region Public Methods

	/// <overloads>
	/// Gets the gallery-specific user profile for a user.
	/// </overloads>
	/// <summary>
	/// Gets the gallery-specific user profile for the currently logged on user and specified <paramref name="galleryId"/>.
	/// Guaranteed to not return null (returns an empty object if no profile is found).
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>Gets the profile for the current user and the specified gallery.</returns>
	public static UserGalleryProfile getProfileForGallery(long galleryId) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		return getProfileForGallery(UserUtils.getLoginName(), galleryId);
	}

	/// <summary>
	/// Gets the gallery-specific user profile for the specified <paramref name="userName"/> and <paramref name="galleryId"/>.
	/// Guaranteed to not return null (returns an empty object if no profile is found).
	/// </summary>
	/// <param name="userName">The account name for the user whose profile settings are to be retrieved. You can specify null or an empty String
	/// for anonymous users.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>Gets the profile for the specified user and gallery.</returns>
	public static UserGalleryProfile getProfileForGallery(String userName, long galleryId) throws UnsupportedContentObjectTypeException, InvalidGalleryException	{
		return getProfile(userName).getGalleryProfile(galleryId);
	}

	/// <overloads>
	/// Gets a user's profile. The UserName property will be an empty String 
	/// for anonymous users and the remaining properties will be set to default values.
	/// </overloads>
	/// <summary>
	/// Gets the profile for the current user.
	/// </summary>
	/// <returns>Gets the profile for the current user.</returns>
	public static UserProfile getProfile()	{
		return getProfile(UserUtils.getLoginName());
	}

	/// <summary>
	/// Gets the user profile for the specified <paramref name="userName" />. Guaranteed to not
	/// return null (returns an empty object if no profile is found).
	/// </summary>
	/// <param name="userName">The account name for the user whose profile settings are to be retrieved. You can specify null or an empty String
	/// for anonymous users.</param>
	/// <returns>Gets the profile for the specified user.</returns>
	public static UserProfile getProfile(String userName)	{
		if (StringUtils.isBlank(userName))	{
			// Anonymous user. Get from session. If not found in session, return an empty object.
			UserProfile userProfile = getProfileFromSession();
			if (userProfile == null)
				userProfile = new UserProfile() ;
			
			return  userProfile;
		}else{
			return CMUtils.loadUserProfile(userName);
		}
	}

	/// <summary>
	/// Saves the specified <paramref name="userProfile" />. Anonymous profiles (those with an 
	/// empty String in <see cref="UserProfile.UserName" />) are saved to session; profiles for 
	/// users with accounts are persisted to the data store. The profile cache is automatically
	/// cleared.
	/// </summary>
	/// <param name="userProfile">The user profile to save.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="userProfile" /> is null.</exception>
	public static void saveProfile(UserProfile userProfile) throws JsonProcessingException	{
		if (userProfile == null)
			throw new ArgumentNullException("userProfile");

		if (StringUtils.isBlank(userProfile.UserName))
			saveProfileToSession(userProfile);
		else
		{
			CMUtils.saveUserProfile(userProfile);
		}
	}

	/// <summary>
	/// Permanently delete the profile records for the specified <paramref name="userName" />.
	/// </summary>
	/// <param name="userName">The user name that uniquely identifies the user.</param>
	public static void deleteProfileForUser(String userName)	{
		CMUtils.deleteUserProfile(userName);
	}

	/// <summary>
	/// Permanently delete the profile records associated with the specified <paramref name="gallery" />.
	/// </summary>
	/// <param name="gallery">The gallery.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="gallery" /> is null.</exception>
	public static void deleteProfileForGallery(GalleryBo gallery)	{
		//CMUtils.getDataProvider().Profile_DeleteProfilesForGallery(gallery.getGalleryId());
		CMUtils.deleteProfileForGallery(gallery);
	}

	//#endregion

	//#region Private Functions

	/// <summary>
	/// Gets the current user's profile from session. Returns null if no object is found.
	/// </summary>
	/// <returns>Returns an instance of <see cref="UserProfile" /> or null if no profile
	/// is found in session.</returns>
	/// <remarks>See the remarks for <see cref="SaveProfileToSession" /> for information about why we use
	/// JSON.NET during the deserialization process.</remarks>
	private static UserProfile getProfileFromSession()	{
		UserProfile pc = null;

		if (UserUtils.getSession() != null)
		{
			String pcString = (String)UserUtils.getCache("_Profile");

			if (!StringUtils.isBlank(pcString))	{
				pc = JsonMapper.getInstance().fromJson(pcString, UserProfile.class);
				/*.<UserProfile>(pcString,
					new UserProfileConverter(),
					new UserGalleryProfileConverter(),
					new AlbumProfileConverter(),
					new ContentObjectProfileConverter());*/
			}
		}

		return pc;
	}

	/// <summary>
	/// Saves the <paramref name="userProfile" /> to session.
	/// </summary>
	/// <param name="userProfile">The user profile.</param>
	/// <remarks>The built-in serializer used by ASP.NET for storing objects in session is unable to save an
	/// instance of <see cref="UserProfile" />, so we first use JSON.NET to serialize it to a String, then
	/// persist *that* to session.</remarks>
	private static void saveProfileToSession(UserProfile userProfile) throws JsonProcessingException	{
		if (UserUtils.getSession() != null){
			UserUtils.putCache("_Profile", new ObjectMapper().writeValueAsString(userProfile));
		}
	}

	//#endregion
}
