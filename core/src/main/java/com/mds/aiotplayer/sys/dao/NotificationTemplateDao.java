/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
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