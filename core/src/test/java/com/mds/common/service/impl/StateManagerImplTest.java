package com.mds.common.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.common.dao.StateDao;
import com.mds.common.model.State;
import com.mds.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class StateManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private StateManagerImpl manager;

    @Mock
    private StateDao dao;

    @Test
    public void testGetState() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final State state = new State();
        given(dao.get(id)).willReturn(state);

        //when
        State result = manager.get(id);

        //then
        assertSame(state, result);
    }

    @Test
    public void testGetStates() {
        log.debug("testing getAll...");
        //given
        final List<State> states = new ArrayList<>();
        given(dao.getAll()).willReturn(states);

        //when
        List result = manager.getAll();

        //then
        assertSame(states, result);
    }

    @Test
    public void testSaveState() {
        log.debug("testing save...");

        //given
        final State state = new State();
        // enter all required fields
        state.setStateCode("SkZuAiToOlRnRoUkXlTxJiVmGcIxSlMgJmObBtWaDbJhCdXrSxPeBtFsDnHdJxBoUgTzTgTmYxRdDpTeGuRxRhSuJiGyIwNwSrKw");

        given(dao.save(state)).willReturn(state);

        //when
        manager.save(state);

        //then
        verify(dao).save(state);
    }

    @Test
    public void testRemoveState() {
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
