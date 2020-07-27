package com.mds.aiotplayer.hrm.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.hrm.model.StaffDepartment;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class StaffDepartmentDaoTest extends BaseDaoTestCase {
    @Autowired
    private StaffDepartmentDao staffDepartmentDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveStaffDepartment() {
        StaffDepartment staffDepartment = new StaffDepartment();

        // enter all required fields

        log.debug("adding staffDepartment...");
        staffDepartment = staffDepartmentDao.save(staffDepartment);

        staffDepartment = staffDepartmentDao.get(staffDepartment.getId());

        assertNotNull(staffDepartment.getId());

        log.debug("removing staffDepartment...");

        staffDepartmentDao.remove(staffDepartment.getId());

        // should throw DataAccessException 
        staffDepartmentDao.get(staffDepartment.getId());
    }
}