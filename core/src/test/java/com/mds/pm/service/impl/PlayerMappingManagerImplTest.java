package com.mds.pm.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.pm.dao.PlayerMappingDao;
import com.mds.pm.model.PlayerMapping;
import com.mds.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class PlayerMappingManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private PlayerMappingManagerImpl manager;

    @Mock
    private PlayerMappingDao dao;

    @Test
    public void testGetPlayerMapping() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final PlayerMapping playerMapping = new PlayerMapping();
        given(dao.get(id)).willReturn(playerMapping);

        //when
        PlayerMapping result = manager.get(id);

        //then
        assertSame(playerMapping, result);
    }

    @Test
    public void testGetPlayerMappings() {
        log.debug("testing getAll...");
        //given
        final List<PlayerMapping> playerMappings = new ArrayList<>();
        given(dao.getAll()).willReturn(playerMappings);

        //when
        List result = manager.getAll();

        //then
        assertSame(playerMappings, result);
    }

    @Test
    public void testSavePlayerMapping() {
        log.debug("testing save...");

        //given
        final PlayerMapping playerMapping = new PlayerMapping();
        // enter all required fields

        given(dao.save(playerMapping)).willReturn(playerMapping);

        //when
        manager.save(playerMapping);

        //then
        verify(dao).save(playerMapping);
    }

    @Test
    public void testRemovePlayerMapping() {
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
