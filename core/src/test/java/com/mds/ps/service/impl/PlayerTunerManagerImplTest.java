package com.mds.ps.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.ps.dao.PlayerTunerDao;
import com.mds.ps.model.PlayerTuner;
import com.mds.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class PlayerTunerManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private PlayerTunerManagerImpl manager;

    @Mock
    private PlayerTunerDao dao;

    @Test
    public void testGetPlayerTuner() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final PlayerTuner playerTuner = new PlayerTuner();
        given(dao.get(id)).willReturn(playerTuner);

        //when
        PlayerTuner result = manager.get(id);

        //then
        assertSame(playerTuner, result);
    }

    @Test
    public void testGetPlayerTuners() {
        log.debug("testing getAll...");
        //given
        final List<PlayerTuner> playerTuners = new ArrayList<>();
        given(dao.getAll()).willReturn(playerTuners);

        //when
        List result = manager.getAll();

        //then
        assertSame(playerTuners, result);
    }

    @Test
    public void testSavePlayerTuner() {
        log.debug("testing save...");

        //given
        final PlayerTuner playerTuner = new PlayerTuner();
        // enter all required fields
        //playerTuner.setChannelName("OdShZfKkEyLiFgWnFgHoAeOiOyNgFgXcYmMpCeFvSvWvGeWyMc");
        playerTuner.setOutput(new Byte("41"));
        playerTuner.setStartTime(new java.util.Date());

        given(dao.save(playerTuner)).willReturn(playerTuner);

        //when
        manager.save(playerTuner);

        //then
        verify(dao).save(playerTuner);
    }

    @Test
    public void testRemovePlayerTuner() {
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
