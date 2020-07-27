package com.mds.aiotplayer.hrm.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.hrm.model.Department;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DepartmentDaoTest extends BaseDaoTestCase {
    @Autowired
    private DepartmentDao departmentDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveDepartment() {
        Department department = new Department();

        // enter all required fields

        log.debug("adding department...");
        department = departmentDao.save(department);

        department = departmentDao.get(department.getId());

        assertNotNull(department.getId());

        log.debug("removing department...");

        departmentDao.remove(department.getId());

        // should throw DataAccessException 
        departmentDao.get(department.getId());
    }
}