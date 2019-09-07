package com.mds.i18n.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.i18n.dao.LocalizedResourceDao;
import com.mds.i18n.model.LocalizedResource;
import com.mds.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class LocalizedResourceManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private LocalizedResourceManagerImpl manager;

    @Mock
    private LocalizedResourceDao dao;

    @Test
    public void testGetLocalizedResource() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final LocalizedResource localizedResource = new LocalizedResource();
        given(dao.get(id)).willReturn(localizedResource);

        //when
        LocalizedResource result = manager.get(id);

        //then
        assertSame(localizedResource, result);
    }

    @Test
    public void testGetLocalizedResources() {
        log.debug("testing getAll...");
        //given
        final List<LocalizedResource> localizedResources = new ArrayList<>();
        given(dao.getAll()).willReturn(localizedResources);

        //when
        List result = manager.getAll();

        //then
        assertSame(localizedResources, result);
    }

    @Test
    public void testSaveLocalizedResource() {
        log.debug("testing save...");

        //given
        final LocalizedResource localizedResource = new LocalizedResource();
        // enter all required fields

        given(dao.save(localizedResource)).willReturn(localizedResource);

        //when
        manager.save(localizedResource);

        //then
        verify(dao).save(localizedResource);
    }

    @Test
    public void testRemoveLocalizedResource() {
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
