package com.mds.sys.dao;

import com.mds.common.dao.BaseDaoTestCase;
import com.mds.sys.model.Permission;
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