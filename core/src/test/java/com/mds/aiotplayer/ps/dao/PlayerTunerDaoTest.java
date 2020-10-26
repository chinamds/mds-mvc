/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.ps.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.ps.model.PlayerTuner;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PlayerTunerDaoTest extends BaseDaoTestCase {
    @Autowired
    private PlayerTunerDao playerTunerDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemovePlayerTuner() {
        PlayerTuner playerTuner = new PlayerTuner();

        // enter all required fields
        //playerTuner.setChannelName("OrTsSnOcNxDvXlMoQiQnEgUhJqXyLdQoZeGnDfHtUtQfRfHkWv");
        playerTuner.setOutput(new Byte("81"));
        playerTuner.setStartTime(new java.util.Date());

        log.debug("adding playerTuner...");
        playerTuner = playerTunerDao.save(playerTuner);

        playerTuner = playerTunerDao.get(playerTuner.getId());

        assertNotNull(playerTuner.getId());

        log.debug("removing playerTuner...");

        playerTunerDao.remove(playerTuner.getId());

        // should throw DataAccessException 
        playerTunerDao.get(playerTuner.getId());
    }
}