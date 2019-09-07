package com.mds.sys.dao;

import com.mds.common.dao.BaseDaoTestCase;
import com.mds.sys.model.MenuFunctionPermission;
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