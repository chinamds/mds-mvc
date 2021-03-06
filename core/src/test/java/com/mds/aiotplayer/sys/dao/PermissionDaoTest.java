/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.sys.model.Permission;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PermissionDaoTest extends BaseDaoTestCase {
    @Autowired
    private PermissionDao permissionDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemovePermission() {
        Permission permission = new Permission();

        // enter all required fields

        log.debug("adding permission...");
        permission = permissionDao.save(permission);

        permission = permissionDao.get(permission.getId());

        assertNotNull(permission.getId());

        log.debug("removing permission...");

        permissionDao.remove(permission.getId());

        // should throw DataAccessException 
        permissionDao.get(permission.getId());
    }
}