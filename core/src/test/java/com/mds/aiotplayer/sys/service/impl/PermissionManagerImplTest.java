package com.mds.aiotplayer.sys.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.sys.dao.PermissionDao;
import com.mds.aiotplayer.sys.model.Permission;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class PermissionManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private PermissionManagerImpl manager;

    @Mock
    private PermissionDao dao;

    @Test
    public void testGetPermission() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Permission permission = new Permission();
        given(dao.get(id)).willReturn(permission);

        //when
        Permission result = manager.get(id);

        //then
        assertSame(permission, result);
    }

    @Test
    public void testGetPermissions() {
        log.debug("testing getAll...");
        //given
        final List<Permission> permissions = new ArrayList<>();
        given(dao.getAll()).willReturn(permissions);

        //when
        List result = manager.getAll();

        //then
        assertSame(permissions, result);
    }

    @Test
    public void testSavePermission() {
        log.debug("testing save...");

        //given
        final Permission permission = new Permission();
        // enter all required fields

        given(dao.save(permission)).willReturn(permission);

        //when
        manager.save(permission);

        //then
        verify(dao).save(permission);
    }

    @Test
    public void testRemovePermission() {
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
