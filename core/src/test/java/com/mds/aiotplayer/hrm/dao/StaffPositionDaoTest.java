/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.hrm.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.hrm.model.StaffPosition;
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