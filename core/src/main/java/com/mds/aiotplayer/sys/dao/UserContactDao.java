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