package com.mds.aiotplayer.pm.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.pm.model.PlayerOutput;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PlayerOutputDaoTest extends BaseDaoTestCase {
    @Autowired
    private PlayerOutputDao playerOutputDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemovePlayerOutput() {
        PlayerOutput playerOutput = new PlayerOutput();

        // enter all required fields
        playerOutput.setOutput(new Short("20096"));

        log.debug("adding playerOutput...");
        playerOutput = playerOutputDao.save(playerOutput);

        playerOutput = playerOutputDao.get(playerOutput.getId());

        assertNotNull(playerOutput.getId());

        log.debug("removing playerOutput...");

        playerOutputDao.remove(playerOutput.getId());

        // should throw DataAccessException 
        playerOutputDao.get(playerOutput.getId());
    }
}