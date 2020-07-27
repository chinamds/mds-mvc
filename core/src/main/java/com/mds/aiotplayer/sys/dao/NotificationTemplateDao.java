package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.sys.model.NotificationTemplate;

/**
 * An interface that provides a data management interface to the NotificationTemplate table.
 */
public interface NotificationTemplateDao extends GenericDao<NotificationTemplate, Long> {
    NotificationTemplate findByName(String name);
	/**
     * Saves a notificationTemplate's information.
     * @param notificationTemplate the object to be saved
     * @return the persisted NotificationTemplate object
     */
    NotificationTemplate saveNotificationTemplate(NotificationTemplate notificationTemplate);
}