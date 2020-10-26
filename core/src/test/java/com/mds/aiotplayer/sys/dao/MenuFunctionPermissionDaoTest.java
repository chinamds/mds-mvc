/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.sys.model.MenuFunctionPermission;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MenuFunctionPermissionDaoTest extends BaseDaoTestCase {
    @Autowired
    private MenuFunctionPermissionDao menuFunctionPermissionDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveMenuFunctionPermission() {
        MenuFunctionPermission menuFunctionPermission = new MenuFunctionPermission();

        // enter all required fields

        log.debug("adding menuFunctionPermission...");
        menuFunctionPermission = menuFunctionPermissionDao.save(menuFunctionPermission);

        menuFunctionPermission = menuFunctionPermissionDao.get(menuFunctionPermission.getId());

        assertNotNull(menuFunctionPermission.getId());

        log.debug("removing menuFunctionPermission...");

        menuFunctionPermissionDao.remove(menuFunctionPermission.getId());

        // should throw DataAccessException 
        menuFunctionPermissionDao.get(menuFunctionPermission.getId());
    }
}