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

import com.mds.aiotplayer.sys.dao.MenuFunctionPermissionDao;
import com.mds.aiotplayer.sys.model.MenuFunctionPermission;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class MenuFunctionPermissionManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private MenuFunctionPermissionManagerImpl manager;

    @Mock
    private MenuFunctionPermissionDao dao;

    @Test
    public void testGetMenuFunctionPermission() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final MenuFunctionPermission menuFunctionPermission = new MenuFunctionPermission();
        given(dao.get(id)).willReturn(menuFunctionPermission);

        //when
        MenuFunctionPermission result = manager.get(id);

        //then
        assertSame(menuFunctionPermission, result);
    }

    @Test
    public void testGetMenuFunctionPermissions() {
        log.debug("testing getAll...");
        //given
        final List<MenuFunctionPermission> menuFunctionPermissions = new ArrayList<>();
        given(dao.getAll()).willReturn(menuFunctionPermissions);

        //when
        List result = manager.getAll();

        //then
        assertSame(menuFunctionPermissions, result);
    }

    @Test
    public void testSaveMenuFunctionPermission() {
        log.debug("testing save...");

        //given
        final MenuFunctionPermission menuFunctionPermission = new MenuFunctionPermission();
        // enter all required fields

        given(dao.save(menuFunctionPermission)).willReturn(menuFunctionPermission);

        //when
        manager.save(menuFunctionPermission);

        //then
        verify(dao).save(menuFunctionPermission);
    }

    @Test
    public void testRemoveMenuFunctionPermission() {
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
