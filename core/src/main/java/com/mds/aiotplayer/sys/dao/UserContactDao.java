/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.sys.model.UserContact;

/**
 * An interface that provides a data management interface to the UserContact table.
 */
public interface UserContactDao extends GenericDao<UserContact, Long> {
	/**
     * Saves a userContact's information.
     * @param userContact the object to be saved
     * @return the persisted UserContact object
     */
    UserContact saveUserContact(UserContact userContact);
}