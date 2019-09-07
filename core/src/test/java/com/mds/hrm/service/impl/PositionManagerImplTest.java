package com.mds.hrm.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.hrm.dao.PositionDao;
import com.mds.hrm.model.Position;
import com.mds.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class PositionManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private PositionManagerImpl manager;

    @Mock
    private PositionDao dao;

    @Test
    public void testGetPosition() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Position position = new Position();
        given(dao.get(id)).willReturn(position);

        //when
        Position result = manager.get(id);

        //then
        assertSame(position, result);
    }

    @Test
    public void testGetPositions() {
        log.debug("testing getAll...");
        //given
        final List<Position> positions = new ArrayList<>();
        given(dao.getAll()).willReturn(positions);

        //when
        List result = manager.getAll();

        //then
        assertSame(positions, result);
    }

    @Test
    public void testSavePosition() {
        log.debug("testing save...");

        //given
        final Position position = new Position();
        // enter all required fields
        position.setCategory(1615351886);
        position.setRank(987952920);

        given(dao.save(position)).willReturn(position);

        //when
        manager.save(position);

        //then
        verify(dao).save(position);
    }

    @Test
    public void testRemovePosition() {
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
