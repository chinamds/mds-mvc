package com.mds.aiotplayer.hrm.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.hrm.dao.DepartmentDao;
import com.mds.aiotplayer.hrm.model.Department;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class DepartmentManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private DepartmentManagerImpl manager;

    @Mock
    private DepartmentDao dao;

    @Test
    public void testGetDepartment() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Department department = new Department();
        given(dao.get(id)).willReturn(department);

        //when
        Department result = manager.get(id);

        //then
        assertSame(department, result);
    }

    @Test
    public void testGetDepartments() {
        log.debug("testing getAll...");
        //given
        final List<Department> departments = new ArrayList<>();
        given(dao.getAll()).willReturn(departments);

        //when
        List result = manager.getAll();

        //then
        assertSame(departments, result);
    }

    @Test
    public void testSaveDepartment() {
        log.debug("testing save...");

        //given
        final Department department = new Department();
        // enter all required fields

        given(dao.save(department)).willReturn(department);

        //when
        manager.save(department);

        //then
        verify(dao).save(department);
    }

    @Test
    public void testRemoveDepartment() {
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
