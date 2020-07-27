package com.mds.aiotplayer.hrm.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.hrm.dao.StaffIdentityDao;
import com.mds.aiotplayer.hrm.model.StaffIdentity;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class StaffIdentityManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private StaffIdentityManagerImpl manager;

    @Mock
    private StaffIdentityDao dao;

    @Test
    public void testGetStaffIdentity() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final StaffIdentity staffIdentity = new StaffIdentity();
        given(dao.get(id)).willReturn(staffIdentity);

        //when
        StaffIdentity result = manager.get(id);

        //then
        assertSame(staffIdentity, result);
    }

    @Test
    public void testGetStaffIdentities() {
        log.debug("testing getAll...");
        //given
        final List<StaffIdentity> staffIdentities = new ArrayList<>();
        given(dao.getAll()).willReturn(staffIdentities);

        //when
        List result = manager.getAll();

        //then
        assertSame(staffIdentities, result);
    }

    @Test
    public void testSaveStaffIdentity() {
        log.debug("testing save...");

        //given
        final StaffIdentity staffIdentity = new StaffIdentity();
        // enter all required fields

        given(dao.save(staffIdentity)).willReturn(staffIdentity);

        //when
        manager.save(staffIdentity);

        //then
        verify(dao).save(staffIdentity);
    }

    @Test
    public void testRemoveStaffIdentity() {
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
