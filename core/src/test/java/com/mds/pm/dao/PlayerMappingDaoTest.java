package com.mds.pm.dao;

import com.mds.common.dao.BaseDaoTestCase;
import com.mds.pm.model.PlayerMapping;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PlayerMappingDaoTest extends BaseDaoTestCase {
    @Autowired
    private PlayerMappingDao playerMappingDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemovePlayerMapping() {
        PlayerMapping playerMapping = new PlayerMapping();

        // enter all required fields

        log.debug("adding playerMapping...");
        playerMapping = playerMappingDao.save(playerMapping);

        playerMapping = playerMappingDao.get(playerMapping.getId());

        assertNotNull(playerMapping.getId());

        log.debug("removing playerMapping...");

        playerMappingDao.remove(playerMapping.getId());

        // should throw DataAccessException 
        playerMappingDao.get(playerMapping.getId());
    }
}