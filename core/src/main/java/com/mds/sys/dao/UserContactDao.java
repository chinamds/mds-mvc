package com.mds.sys.dao;

import com.mds.common.dao.GenericDao;

import com.mds.sys.model.UserContact;

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