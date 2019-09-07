package com.mds.sys.dao;

import com.mds.common.dao.GenericDao;

import com.mds.sys.model.UserStatusHistory;

/**
 * An interface that provides a data management interface to the UserStatusHistory table.
 */
public interface UserStatusHistoryDao extends GenericDao<UserStatusHistory, Long> {
	/**
     * Saves a userStatusHistory's information.
     * @param userStatusHistory the object to be saved
     * @return the persisted UserStatusHistory object
     */
    UserStatusHistory saveUserStatusHistory(UserStatusHistory userStatusHistory);
}