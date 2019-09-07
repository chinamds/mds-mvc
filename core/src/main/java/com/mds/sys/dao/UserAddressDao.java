package com.mds.sys.dao;

import com.mds.common.dao.GenericDao;

import com.mds.sys.model.UserAddress;

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