/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pm.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.pm.dao.PlayerGroup2PlayerDao;
import com.mds.aiotplayer.pm.model.PlayerGroup2Player;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class PlayerGroup2PlayerManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private PlayerGroup2PlayerManagerImpl manager;

    @Mock
    private PlayerGroup2PlayerDao dao;

    @Test
    public void testGetPlayerGroup2Player() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final PlayerGroup2Player playerGroup2Player = new PlayerGroup2Player();
        given(dao.get(id)).willReturn(playerGroup2Player);

        //when
        PlayerGroup2Player result = manager.get(id);

        //then
        assertSame(playerGroup2Player, result);
    }

    @Test
    public void testGetPlayerGroup2Players() {
        log.debug("testing getAll...");
        //given
        final List<PlayerGroup2Player> playerGroup2Players = new ArrayList<>();
        given(dao.getAll()).willReturn(playerGroup2Players);

        //when
        List result = manager.getAll();

        //then
        assertSame(playerGroup2Players, result);
    }

    @Test
    public void testSavePlayerGroup2Player() {
        log.debug("testing save...");

        //given
        final PlayerGroup2Player playerGroup2Player = new PlayerGroup2Player();
        // enter all required fields

        given(dao.save(playerGroup2Player)).willReturn(playerGroup2Player);

        //when
        manager.save(playerGroup2Player);

        //then
        verify(dao).save(playerGroup2Player);
    }

    @Test
    public void testRemovePlayerGroup2Player() {
        log.debug("testing remove...");

        //given
        final Long id = -11L;
        willDoNothing().given(dao).remove(id);

        //when
        manager.remove(id);

        //then
        verify(dao).remove(id);
    }
}
