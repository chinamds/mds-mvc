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

import com.mds.aiotplayer.pm.dao.PlayerGroupDao;
import com.mds.aiotplayer.pm.model.PlayerGroup;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class PlayerGroupManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private PlayerGroupManagerImpl manager;

    @Mock
    private PlayerGroupDao dao;

    @Test
    public void testGetPlayerGroup() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final PlayerGroup playerGroup = new PlayerGroup();
        given(dao.get(id)).willReturn(playerGroup);

        //when
        PlayerGroup result = manager.get(id);

        //then
        assertSame(playerGroup, result);
    }

    @Test
    public void testGetPlayerGroups() {
        log.debug("testing getAll...");
        //given
        final List<PlayerGroup> playerGroups = new ArrayList<>();
        given(dao.getAll()).willReturn(playerGroups);

        //when
        List result = manager.getAll();

        //then
        assertSame(playerGroups, result);
    }

    @Test
    public void testSavePlayerGroup() {
        log.debug("testing save...");

        //given
        final PlayerGroup playerGroup = new PlayerGroup();
        // enter all required fields
        playerGroup.setCode("XbCpMwBnZqBjUzIkQrKzHmYiLbUfDnIoRbCoHs");
        playerGroup.setDescription("KhOxLlBsEbBzMsByTuBoOiXlMvQaCnLgPlTxKwEcHfSrRqNfZcHwCwEiCyUmTqDrEvBqCaRzPwKjCuZxMqQbJfNbPrMcQpIlVlXpWnPqKnChNnDoJdTzNyEgVmTcPnZyOcTrUuMuOdWrSaCxTcBrSbQaSlZeUgMmUkLaIkUuHjBwTjEoNaSfPyOrWrNlTuIlZxWuBxCgNaOiWmMsQoZoRxWzNuWsSfBqKmThFaFsPvTgLsKkLfPuIuBoWbWfFjNg");

        given(dao.save(playerGroup)).willReturn(playerGroup);

        //when
        manager.save(playerGroup);

        //then
        verify(dao).save(playerGroup);
    }

    @Test
    public void testRemovePlayerGroup() {
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
