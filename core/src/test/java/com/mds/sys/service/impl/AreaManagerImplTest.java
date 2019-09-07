package com.mds.sys.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.sys.dao.AreaDao;
import com.mds.sys.model.Area;
import com.mds.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class AreaManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private AreaManagerImpl manager;

    @Mock
    private AreaDao dao;

    @Test
    public void testGetArea() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Area area = new Area();
        given(dao.get(id)).willReturn(area);

        //when
        Area result = manager.get(id);

        //then
        assertSame(area, result);
    }

    @Test
    public void testGetAreas() {
        log.debug("testing getAll...");
        //given
        final List<Area> areas = new ArrayList<>();
        given(dao.getAll()).willReturn(areas);

        //when
        List result = manager.getAll();

        //then
        assertSame(areas, result);
    }

    @Test
    public void testSaveArea() {
        log.debug("testing save...");

        //given
        final Area area = new Area();
        // enter all required fields

        given(dao.save(area)).willReturn(area);

        //when
        manager.save(area);

        //then
        verify(dao).save(area);
    }

    @Test
    public void testRemoveArea() {
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
