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

import com.mds.aiotplayer.sys.dao.MenuFunctionDao;
import com.mds.aiotplayer.sys.model.MenuFunction;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class MenuFunctionManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private MenuFunctionManagerImpl manager;

    @Mock
    private MenuFunctionDao dao;

    @Test
    public void testGetMenuFunction() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final MenuFunction menuFunction = new MenuFunction();
        given(dao.get(id)).willReturn(menuFunction);

        //when
        MenuFunction result = manager.get(id);

        //then
        assertSame(menuFunction, result);
    }

    @Test
    public void testGetMenuFunctions() {
        log.debug("testing getAll...");
        //given
        final List<MenuFunction> menuFunctions = new ArrayList<>();
        given(dao.getAll()).willReturn(menuFunctions);

        //when
        List result = manager.getAll();

        //then
        assertSame(menuFunctions, result);
    }

    @Test
    public void testSaveMenuFunction() {
        log.debug("testing save...");

        //given
        final MenuFunction menuFunction = new MenuFunction();
        // enter all required fields

        given(dao.save(menuFunction)).willReturn(menuFunction);

        //when
        manager.save(menuFunction);

        //then
        verify(dao).save(menuFunction);
    }

    @Test
    public void testRemoveMenuFunction() {
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
