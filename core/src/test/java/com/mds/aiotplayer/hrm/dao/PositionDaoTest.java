package com.mds.aiotplayer.hrm.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.hrm.model.Position;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PositionDaoTest extends BaseDaoTestCase {
    @Autowired
    private PositionDao positionDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemovePosition() {
        Position position = new Position();

        // enter all required fields
        position.setCategory(1475386469);
        position.setRank(518064105);

        log.debug("adding position...");
        position = positionDao.save(position);

        position = positionDao.get(position.getId());

        assertNotNull(position.getId());

        log.debug("removing position...");

        positionDao.remove(position.getId());

        // should throw DataAccessException 
        positionDao.get(position.getId());
    }
}