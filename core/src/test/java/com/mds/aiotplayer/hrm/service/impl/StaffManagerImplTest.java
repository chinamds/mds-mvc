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

import com.mds.aiotplayer.hrm.dao.StaffDao;
import com.mds.aiotplayer.hrm.model.Staff;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class StaffManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private StaffManagerImpl manager;

    @Mock
    private StaffDao dao;

    @Test
    public void testGetStaff() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Staff staff = new Staff();
        given(dao.get(id)).willReturn(staff);

        //when
        Staff result = manager.get(id);

        //then
        assertSame(staff, result);
    }

    @Test
    public void testGetStaffs() {
        log.debug("testing getAll...");
        //given
        final List<Staff> staffs = new ArrayList<>();
        given(dao.getAll()).willReturn(staffs);

        //when
        List result = manager.getAll();

        //then
        assertSame(staffs, result);
    }

    @Test
    public void testSaveStaff() {
        log.debug("testing save...");

        //given
        final Staff staff = new Staff();
        // enter all required fields
        staff.setChineseName("NpQcKiXbAxBbIiLjLsGxFmDbIqZpJiWpSlDrRnRdMqDkBoGwMj");
        staff.setGender("E");
        staff.setGivenName("LaRfIqYwSvUsIiNyRzCgQuGwRuArKqZeXvYvFmYeYvIbAbApHfGyGfKySgAhRiCiIdSmSyAyWyAeJcPyNgEbLeNwJhAsUgEyBlGf");
        staff.setJoinDate(new java.util.Date());
        staff.setMarital("LrWiNnAxUcEqSzLgCgOrRnWaRaHuSqPeYoRsWrZoIgKqMrQnEtZvOsDeLvYhTgVpVzAkQpKgRuZbOmGvRjDlUtBxIoEwFjRyOaQlBzWfTpKlJjXxWfUsYxGyQdAxLgFhCeToNyQqIfBaKdPjUcVmAbBdBxAsNaVyGcHpRvOnWwKjErLsYyNkVkZhCrCoQpQkSxVzNqYbVaKfErRfImPzAiKuDmSkSvWlZhFoClDyUzIrFiOjMkAyEoMtEcHqJbS");
        staff.setStaffNo("SzIcSdGzDnWuNbNxCgInBeOlMfMqVeTjAfEzOcVrUoQwWeAuDhNxAwDyYxOyJaLeRfJeRkGvQmSsBvFiFaNfQvIwPeIyNxZhFzYq");
        staff.setStatus("S");
        staff.setSurname("XcUfImDrLtVeAcHgRpGpIbEpTyJgZfBpTmIuHiTcAuHvKoRlJdMxLbPpQiQrEpMtIyIcPrEwDgYiNeMwSoUgQbOsKoBwGrVtLpZw");

        given(dao.save(staff)).willReturn(staff);

        //when
        manager.save(staff);

        //then
        verify(dao).save(staff);
    }

    @Test
    public void testRemoveStaff() {
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
