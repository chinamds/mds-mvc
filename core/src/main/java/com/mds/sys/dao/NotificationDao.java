package com.mds.sys.dao;

import com.mds.common.dao.GenericDao;

import com.mds.sys.model.Notification;

/**
 * An interface that provides a data management interface to the Notification table.
 */
public interface NotificationDao extends GenericDao<Notification, Long> {
	void markReadAll(Long userId);
	/**
     * Saves a notification's information.
     * @param notification the object to be saved
     * @return the persisted Notification object
     */
    Notification saveNotification(Notification notification);
}