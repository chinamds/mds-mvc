package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.sys.model.Notification;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class NotificationDaoTest extends BaseDaoTestCase {
    @Autowired
    private NotificationDao notificationDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveNotification() {
        Notification notification = new Notification();

        // enter all required fields

        log.debug("adding notification...");
        notification = notificationDao.save(notification);

        notification = notificationDao.get(notification.getId());

        assertNotNull(notification.getId());

        log.debug("removing notification...");

        notificationDao.remove(notification.getId());

        // should throw DataAccessException 
        notificationDao.get(notification.getId());
    }
}