package com.mds.aiotplayer.pl.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.pl.model.LayoutMst;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class LayoutMstDaoTest extends BaseDaoTestCase {
    @Autowired
    private LayoutMstDao layoutMstDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveLayoutMst() {
        LayoutMst layoutMst = new LayoutMst();

        // enter all required fields

        log.debug("adding layoutMst...");
        layoutMst = layoutMstDao.save(layoutMst);

        layoutMst = layoutMstDao.get(layoutMst.getId());

        assertNotNull(layoutMst.getId());

        log.debug("removing layoutMst...");

        layoutMstDao.remove(layoutMst.getId());

        // should throw DataAccessException 
        layoutMstDao.get(layoutMst.getId());
    }
}