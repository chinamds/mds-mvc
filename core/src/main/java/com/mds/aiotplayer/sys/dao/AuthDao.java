package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.sys.model.Auth;

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