package com.mds.sys.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.sys.dao.NotificationTemplateDao;
import com.mds.sys.model.NotificationTemplate;
import com.mds.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class NotificationTemplateManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private NotificationTemplateManagerImpl manager;

    @Mock
    private NotificationTemplateDao dao;

    @Test
    public void testGetNotificationTemplate() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final NotificationTemplate notificationTemplate = new NotificationTemplate();
        given(dao.get(id)).willReturn(notificationTemplate);

        //when
        NotificationTemplate result = manager.get(id);

        //then
        assertSame(notificationTemplate, result);
    }

    @Test
    public void testGetNotificationTemplates() {
        log.debug("testing getAll...");
        //given
        final List<NotificationTemplate> notificationTemplates = new ArrayList<>();
        given(dao.getAll()).willReturn(notificationTemplates);

        //when
        List result = manager.getAll();

        //then
        assertSame(notificationTemplates, result);
    }

    @Test
    public void testSaveNotificationTemplate() {
        log.debug("testing save...");

        //given
        final NotificationTemplate notificationTemplate = new NotificationTemplate();
        // enter all required fields
        notificationTemplate.setName("VlCyYxUuGkJaRhQwGvTuOhToSaEtJbGzUsSeNnRrLmSwYlKkZrYsUeEcNuUgMyWbFsVyCnIlNkJoZoVtDkQsMpLpCqAoPbRgFqAo");

        given(dao.save(notificationTemplate)).willReturn(notificationTemplate);

        //when
        manager.save(notificationTemplate);

        //then
        verify(dao).save(notificationTemplate);
    }

    @Test
    public void testRemoveNotificationTemplate() {
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
