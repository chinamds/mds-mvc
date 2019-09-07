package com.mds.i18n.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.i18n.dao.NeutralResourceDao;
import com.mds.i18n.model.NeutralResource;
import com.mds.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class NeutralResourceManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private NeutralResourceManagerImpl manager;

    @Mock
    private NeutralResourceDao dao;

    @Test
    public void testGetNeutralResource() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final NeutralResource neutralResource = new NeutralResource();
        given(dao.get(id)).willReturn(neutralResource);

        //when
        NeutralResource result = manager.get(id);

        //then
        assertSame(neutralResource, result);
    }

    @Test
    public void testGetNeutralResources() {
        log.debug("testing getAll...");
        //given
        final List<NeutralResource> neutralResources = new ArrayList<>();
        given(dao.getAll()).willReturn(neutralResources);

        //when
        List result = manager.getAll();

        //then
        assertSame(neutralResources, result);
    }

    @Test
    public void testSaveNeutralResource() {
        log.debug("testing save...");

        //given
        final NeutralResource neutralResource = new NeutralResource();
        // enter all required fields

        given(dao.save(neutralResource)).willReturn(neutralResource);

        //when
        manager.save(neutralResource);

        //then
        verify(dao).save(neutralResource);
    }

    @Test
    public void testRemoveNeutralResource() {
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
