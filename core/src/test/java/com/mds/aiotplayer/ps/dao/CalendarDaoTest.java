package com.mds.aiotplayer.ps.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.ps.model.Calendar;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CalendarDaoTest extends BaseDaoTestCase {
    @Autowired
    private CalendarDao calendarDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveCalendar() {
        Calendar calendar = new Calendar();

        // enter all required fields
        calendar.setApprovalLevel(new Byte("50"));
        calendar.setApprovalStatus(new Byte("45"));
        calendar.setDay(new java.util.Date());
        calendar.setPlayMeth(new Byte("35"));

        log.debug("adding calendar...");
        calendar = calendarDao.save(calendar);

        calendar = calendarDao.get(calendar.getId());

        assertNotNull(calendar.getId());

        log.debug("removing calendar...");

        calendarDao.remove(calendar.getId());

        // should throw DataAccessException 
        calendarDao.get(calendar.getId());
    }
}