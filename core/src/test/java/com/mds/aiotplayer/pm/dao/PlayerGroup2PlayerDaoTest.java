package com.mds.aiotplayer.pm.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.pm.model.PlayerGroup2Player;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PlayerGroup2PlayerDaoTest extends BaseDaoTestCase {
    @Autowired
    private PlayerGroup2PlayerDao playerGroup2PlayerDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemovePlayerGroup2Player() {
        PlayerGroup2Player playerGroup2Player = new PlayerGroup2Player();

        // enter all required fields

        log.debug("adding playerGroup2Player...");
        playerGroup2Player = playerGroup2PlayerDao.save(playerGroup2Player);

        playerGroup2Player = playerGroup2PlayerDao.get(playerGroup2Player.getId());

        assertNotNull(playerGroup2Player.getId());

        log.debug("removing playerGroup2Player...");

        playerGroup2PlayerDao.remove(playerGroup2Player.getId());

        // should throw DataAccessException 
        playerGroup2PlayerDao.get(playerGroup2Player.getId());
    }
}