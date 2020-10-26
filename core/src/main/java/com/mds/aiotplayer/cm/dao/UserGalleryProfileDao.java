/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.cm.model.UserGalleryProfile;

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