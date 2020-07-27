package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.sys.model.Area;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AreaDaoTest extends BaseDaoTestCase {
    @Autowired
    private AreaDao areaDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveArea() {
        Area area = new Area();

        // enter all required fields
        area.setCode("test");
        area.setParent(null);

        log.debug("adding area...");
        area = areaDao.save(area);

        area = areaDao.get(area.getId());

        assertNotNull(area.getId());

        log.debug("removing area...");

        areaDao.remove(area.getId());

        // should throw DataAccessException 
        areaDao.get(area.getId());
    }
}