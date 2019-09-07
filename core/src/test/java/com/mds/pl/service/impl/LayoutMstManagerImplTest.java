package com.mds.pl.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.pl.dao.LayoutMstDao;
import com.mds.pl.model.LayoutMst;
import com.mds.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class LayoutMstManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private LayoutMstManagerImpl manager;

    @Mock
    private LayoutMstDao dao;

    @Test
    public void testGetLayoutMst() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final LayoutMst layoutMst = new LayoutMst();
        given(dao.get(id)).willReturn(layoutMst);

        //when
        LayoutMst result = manager.get(id);

        //then
        assertSame(layoutMst, result);
    }

    @Test
    public void testGetLayoutMsts() {
        log.debug("testing getAll...");
        //given
        final List<LayoutMst> layoutMsts = new ArrayList<>();
        given(dao.getAll()).willReturn(layoutMsts);

        //when
        List result = manager.getAll();

        //then
        assertSame(layoutMsts, result);
    }

    @Test
    public void testSaveLayoutMst() {
        log.debug("testing save...");

        //given
        final LayoutMst layoutMst = new LayoutMst();
        // enter all required fields

        given(dao.save(layoutMst)).willReturn(layoutMst);

        //when
        manager.save(layoutMst);

        //then
        verify(dao).save(layoutMst);
    }

    @Test
    public void testRemoveLayoutMst() {
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
