/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.sys.dao.NotificationDao;
import com.mds.aiotplayer.sys.model.Notification;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class NotificationManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private NotificationManagerImpl manager;

    @Mock
    private NotificationDao dao;

    @Test
    public void testGetNotification() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Notification notification = new Notification();
        given(dao.get(id)).willReturn(notification);

        //when
        Notification result = manager.get(id);

        //then
        assertSame(notification, result);
    }

    @Test
    public void testGetNotifications() {
        log.debug("testing getAll...");
        //given
        final List<Notification> notifications = new ArrayList<>();
        given(dao.getAll()).willReturn(notifications);

        //when
        List result = manager.getAll();

        //then
        assertSame(notifications, result);
    }

    @Test
    public void testSaveNotification() {
        log.debug("testing save...");

        //given
        final Notification notification = new Notification();
        // enter all required fields

        given(dao.save(notification)).willReturn(notification);

        //when
        manager.save(notification);

        //then
        verify(dao).save(notification);
    }

    @Test
    public void testRemoveNotification() {
        log.debug("testing remove...");

        //given
        final Long id = -11L;
        willDoNothing().given(dao).remove(id);

        //when
        manager.remove(id);

        //then
        verify(dao).remove(id);
    }
}
