/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.sys.model.UserAddress;

/**
 * An interface that provides a data management interface to the UserAddress table.
 */
public interface UserAddressDao extends GenericDao<UserAddress, Long> {
	/**
     * Saves a userAddress's information.
     * @param userAddress the object to be saved
     * @return the persisted UserAddress object
     */
    UserAddress saveUserAddress(UserAddress userAddress);
}