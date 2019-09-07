package com.mds.pm.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.pm.dao.PlayerOutputDao;
import com.mds.pm.model.PlayerOutput;
import com.mds.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class PlayerOutputManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private PlayerOutputManagerImpl manager;

    @Mock
    private PlayerOutputDao dao;

    @Test
    public void testGetPlayerOutput() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final PlayerOutput playerOutput = new PlayerOutput();
        given(dao.get(id)).willReturn(playerOutput);

        //when
        PlayerOutput result = manager.get(id);

        //then
        assertSame(playerOutput, result);
    }

    @Test
    public void testGetPlayerOutputs() {
        log.debug("testing getAll...");
        //given
        final List<PlayerOutput> playerOutputs = new ArrayList<>();
        given(dao.getAll()).willReturn(playerOutputs);

        //when
        List result = manager.getAll();

        //then
        assertSame(playerOutputs, result);
    }

    @Test
    public void testSavePlayerOutput() {
        log.debug("testing save...");

        //given
        final PlayerOutput playerOutput = new PlayerOutput();
        // enter all required fields
        playerOutput.setOutput(new Short("25434"));

        given(dao.save(playerOutput)).willReturn(playerOutput);

        //when
        manager.save(playerOutput);

        //then
        verify(dao).save(playerOutput);
    }

    @Test
    public void testRemovePlayerOutput() {
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
