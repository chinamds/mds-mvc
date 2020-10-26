/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.hrm.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.hrm.model.StaffIdentity;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class StaffIdentityDaoTest extends BaseDaoTestCase {
    @Autowired
    private StaffIdentityDao staffIdentityDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveStaffIdentity() {
        StaffIdentity staffIdentity = new StaffIdentity();

        // enter all required fields

        log.debug("adding staffIdentity...");
        staffIdentity = staffIdentityDao.save(staffIdentity);

        staffIdentity = staffIdentityDao.get(staffIdentity.getId());

        assertNotNull(staffIdentity.getId());

        log.debug("removing staffIdentity...");

        staffIdentityDao.remove(staffIdentity.getId());

        // should throw DataAccessException 
        staffIdentityDao.get(staffIdentity.getId());
    }
}