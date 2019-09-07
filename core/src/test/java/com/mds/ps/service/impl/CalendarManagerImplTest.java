package com.mds.ps.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.ps.dao.CalendarDao;
import com.mds.ps.model.Calendar;
import com.mds.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class CalendarManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private CalendarManagerImpl manager;

    @Mock
    private CalendarDao dao;

    @Test
    public void testGetCalendar() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Calendar calendar = new Calendar();
        given(dao.get(id)).willReturn(calendar);

        //when
        Calendar result = manager.get(id);

        //then
        assertSame(calendar, result);
    }

    @Test
    public void testGetCalendars() {
        log.debug("testing getAll...");
        //given
        final List<Calendar> calendars = new ArrayList<>();
        given(dao.getAll()).willReturn(calendars);

        //when
        List result = manager.getAll();

        //then
        assertSame(calendars, result);
    }

    @Test
    public void testSaveCalendar() {
        log.debug("testing save...");

        //given
        final Calendar calendar = new Calendar();
        // enter all required fields
        calendar.setApprovalLevel(new Byte("105"));
        calendar.setApprovalStatus(new Byte("109"));
        calendar.setDay(new java.util.Date());
        calendar.setPlayMeth(new Byte("102"));

        given(dao.save(calendar)).willReturn(calendar);

        //when
        manager.save(calendar);

        //then
        verify(dao).save(calendar);
    }

    @Test
    public void testRemoveCalendar() {
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
