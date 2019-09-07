package com.mds.pl.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.pl.dao.LayoutDtlDao;
import com.mds.pl.model.LayoutDtl;
import com.mds.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class LayoutDtlManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private LayoutDtlManagerImpl manager;

    @Mock
    private LayoutDtlDao dao;

    @Test
    public void testGetLayoutDtl() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final LayoutDtl layoutDtl = new LayoutDtl();
        given(dao.get(id)).willReturn(layoutDtl);

        //when
        LayoutDtl result = manager.get(id);

        //then
        assertSame(layoutDtl, result);
    }

    @Test
    public void testGetLayoutDtls() {
        log.debug("testing getAll...");
        //given
        final List<LayoutDtl> layoutDtls = new ArrayList<>();
        given(dao.getAll()).willReturn(layoutDtls);

        //when
        List result = manager.getAll();

        //then
        assertSame(layoutDtls, result);
    }

    @Test
    public void testSaveLayoutDtl() {
        log.debug("testing save...");

        //given
        final LayoutDtl layoutDtl = new LayoutDtl();
        // enter all required fields

        given(dao.save(layoutDtl)).willReturn(layoutDtl);

        //when
        manager.save(layoutDtl);

        //then
        verify(dao).save(layoutDtl);
    }

    @Test
    public void testRemoveLayoutDtl() {
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
