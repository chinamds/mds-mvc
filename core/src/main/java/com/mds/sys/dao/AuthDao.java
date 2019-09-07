package com.mds.sys.dao;

import com.mds.common.dao.GenericDao;

import com.mds.sys.model.Auth;

/**
 * An interface that provides a data management interface to the Auth table.
 */
public interface AuthDao extends GenericDao<Auth, Long> {
	/**
     * Saves a auth's information.
     * @param auth the object to be saved
     * @return the persisted Auth object
     */
    Auth saveAuth(Auth auth);
}