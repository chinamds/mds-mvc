package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.sys.model.MenuFunction;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MenuFunctionDaoTest extends BaseDaoTestCase {
    @Autowired
    private MenuFunctionDao menuFunctionDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveMenuFunction() {
        MenuFunction menuFunction = new MenuFunction();

        // enter all required fields

        log.debug("adding menuFunction...");
        menuFunction = menuFunctionDao.save(menuFunction);

        menuFunction = menuFunctionDao.get(menuFunction.getId());

        assertNotNull(menuFunction.getId());

        log.debug("removing menuFunction...");

        menuFunctionDao.remove(menuFunction.getId());

        // should throw DataAccessException 
        menuFunctionDao.get(menuFunction.getId());
    }
}