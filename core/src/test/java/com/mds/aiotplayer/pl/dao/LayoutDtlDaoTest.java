package com.mds.aiotplayer.pl.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.pl.model.LayoutDtl;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class LayoutDtlDaoTest extends BaseDaoTestCase {
    @Autowired
    private LayoutDtlDao layoutDtlDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveLayoutDtl() {
        LayoutDtl layoutDtl = new LayoutDtl();

        // enter all required fields

        log.debug("adding layoutDtl...");
        layoutDtl = layoutDtlDao.save(layoutDtl);

        layoutDtl = layoutDtlDao.get(layoutDtl.getId());

        assertNotNull(layoutDtl.getId());

        log.debug("removing layoutDtl...");

        layoutDtlDao.remove(layoutDtl.getId());

        // should throw DataAccessException 
        layoutDtlDao.get(layoutDtl.getId());
    }
}