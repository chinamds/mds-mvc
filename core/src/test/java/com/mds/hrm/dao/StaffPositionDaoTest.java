package com.mds.hrm.dao;

import com.mds.common.dao.BaseDaoTestCase;
import com.mds.hrm.model.StaffPosition;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class StaffPositionDaoTest extends BaseDaoTestCase {
    @Autowired
    private StaffPositionDao staffPositionDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveStaffPosition() {
        StaffPosition staffPosition = new StaffPosition();

        // enter all required fields

        log.debug("adding staffPosition...");
        staffPosition = staffPositionDao.save(staffPosition);

        staffPosition = staffPositionDao.get(staffPosition.getId());

        assertNotNull(staffPosition.getId());

        log.debug("removing staffPosition...");

        staffPositionDao.remove(staffPosition.getId());

        // should throw DataAccessException 
        staffPositionDao.get(staffPosition.getId());
    }
}