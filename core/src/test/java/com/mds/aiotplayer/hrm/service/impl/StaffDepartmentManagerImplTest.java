/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.hrm.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.hrm.dao.StaffDepartmentDao;
import com.mds.aiotplayer.hrm.model.StaffDepartment;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class StaffDepartmentManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private StaffDepartmentManagerImpl manager;

    @Mock
    private StaffDepartmentDao dao;

    @Test
    public void testGetStaffDepartment() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final StaffDepartment staffDepartment = new StaffDepartment();
        given(dao.get(id)).willReturn(staffDepartment);

        //when
        StaffDepartment result = manager.get(id);

        //then
        assertSame(staffDepartment, result);
    }

    @Test
    public void testGetStaffDepartments() {
        log.debug("testing getAll...");
        //given
        final List<StaffDepartment> staffDepartments = new ArrayList<>();
        given(dao.getAll()).willReturn(staffDepartments);

        //when
        List result = manager.getAll();

        //then
        assertSame(staffDepartments, result);
    }

    @Test
    public void testSaveStaffDepartment() {
        log.debug("testing save...");

        //given
        final StaffDepartment staffDepartment = new StaffDepartment();
        // enter all required fields

        given(dao.save(staffDepartment)).willReturn(staffDepartment);

        //when
        manager.save(staffDepartment);

        //then
        verify(dao).save(staffDepartment);
    }

    @Test
    public void testRemoveStaffDepartment() {
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
