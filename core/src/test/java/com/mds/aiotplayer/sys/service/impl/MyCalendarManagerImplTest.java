package com.mds.aiotplayer.sys.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.sys.dao.MyCalendarDao;
import com.mds.aiotplayer.sys.model.MyCalendar;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class MyCalendarManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private MyCalendarManagerImpl manager;

    @Mock
    private MyCalendarDao dao;

    @Test
    public void testGetMyCalendar() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final MyCalendar myCalendar = new MyCalendar();
        given(dao.get(id)).willReturn(myCalendar);

        //when
        MyCalendar result = manager.get(id);

        //then
        assertSame(myCalendar, result);
    }

    @Test
    public void testGetMyCalendars() {
        log.debug("testing getAll...");
        //given
        final List<MyCalendar> myCalendars = new ArrayList<>();
        given(dao.getAll()).willReturn(myCalendars);

        //when
        List result = manager.getAll();

        //then
        assertSame(myCalendars, result);
    }

    @Test
    public void testSaveMyCalendar() {
        log.debug("testing save...");

        //given
        final MyCalendar myCalendar = new MyCalendar();
        // enter all required fields

        given(dao.save(myCalendar)).willReturn(myCalendar);

        //when
        manager.save(myCalendar);

        //then
        verify(dao).save(myCalendar);
    }

    @Test
    public void testRemoveMyCalendar() {
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
