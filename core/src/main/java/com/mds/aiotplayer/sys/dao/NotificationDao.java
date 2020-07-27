package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.sys.model.Notification;

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