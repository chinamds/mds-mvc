/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.hrm.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.hrm.dao.StaffPositionDao;
import com.mds.aiotplayer.hrm.model.StaffPosition;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class StaffPositionManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private StaffPositionManagerImpl manager;

    @Mock
    private StaffPositionDao dao;

    @Test
    public void testGetStaffPosition() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final StaffPosition staffPosition = new StaffPosition();
        given(dao.get(id)).willReturn(staffPosition);

        //when
        StaffPosition result = manager.get(id);

        //then
        assertSame(staffPosition, result);
    }

    @Test
    public void testGetStaffPositions() {
        log.debug("testing getAll...");
        //given
        final List<StaffPosition> staffPositions = new ArrayList<>();
        given(dao.getAll()).willReturn(staffPositions);

        //when
        List result = manager.getAll();

        //then
        assertSame(staffPositions, result);
    }

    @Test
    public void testSaveStaffPosition() {
        log.debug("testing save...");

        //given
        final StaffPosition staffPosition = new StaffPosition();
        // enter all required fields

        given(dao.save(staffPosition)).willReturn(staffPosition);

        //when
        manager.save(staffPosition);

        //then
        verify(dao).save(staffPosition);
    }

    @Test
    public void testRemoveStaffPosition() {
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
