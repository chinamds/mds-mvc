package com.mds.cm.service;

import com.mds.common.exception.RecordExistsException;
import com.mds.common.service.GenericManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mds.cm.content.UserProfile;
import com.mds.cm.model.UserGalleryProfile;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

@WebService
public interface UserGalleryProfileManager extends GenericManager<UserGalleryProfile, Long> {
    /**
     * Saves a userGalleryProfile information
     *
     * @param mimeTypeGallery the userGalleryProfile information
     * @return updated userGalleryProfile
     * @throws RecordExistsException thrown when userGalleryProfile already exists
     */
    UserGalleryProfile saveUserGalleryProfile(UserGalleryProfile userGalleryProfile) throws RecordExistsException;
    void saveUserGalleryProfile(UserProfile userProfile, long templateGalleryId)  throws JsonProcessingException;
    
    List<UserGalleryProfile> getUserGalleryProfiles(long galleryId);
    List<UserGalleryProfile> getUserGalleryProfiles(final String userName);

	void removeUserGalleryProfile(Long id) ;

	Response removeUserGalleryProfile(final String mimeTypeGalleryIds);
	
	/// <summary>
	/// Permanently delete the profile records for the specified <paramref name="userName" />.
	/// The profile cache is cleared.
	/// </summary>
	/// <param name="userName">Name of the user.</param>
	void removeUserGalleryProfiles(final String userName);

	/// <summary>
	/// Permanently delete the profile records associated with the specified <paramref name="galleryId" />.
	/// </summary>
	/// <param name="galleryId">The gallery Id.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="galleryId" /> is null.</exception>
	void removeUserGalleryProfiles(final long galleryId);
}