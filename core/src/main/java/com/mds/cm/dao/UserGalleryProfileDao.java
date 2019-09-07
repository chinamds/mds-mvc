package com.mds.cm.dao;

import com.mds.common.dao.GenericDao;

import com.mds.cm.model.UserGalleryProfile;

/**
 * An interface that provides a data management interface to the UserGalleryProfile table.
 */
public interface UserGalleryProfileDao extends GenericDao<UserGalleryProfile, Long> {
	/**
     * Saves a userGalleryProfile's information.
     * @param userGalleryProfile the object to be saved
     * @return the persisted UserGalleryProfile object
     */
    UserGalleryProfile saveUserGalleryProfile(UserGalleryProfile userGalleryProfile);
}