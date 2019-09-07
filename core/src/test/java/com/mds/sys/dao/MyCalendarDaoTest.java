package com.mds.sys.dao;

import com.mds.common.dao.BaseDaoTestCase;
import com.mds.sys.model.MyCalendar;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MyCalendarDaoTest extends BaseDaoTestCase {
    @Autowired
    private MyCalendarDao myCalendarDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveMyCalendar() {
        MyCalendar myCalendar = new MyCalendar();

        // enter all required fields

        log.debug("adding myCalendar...");
        myCalendar = myCalendarDao.save(myCalendar);

        myCalendar = myCalendarDao.get(myCalendar.getId());

        assertNotNull(myCalendar.getId());

        log.debug("removing myCalendar...");

        myCalendarDao.remove(myCalendar.getId());

        // should throw DataAccessException 
        myCalendarDao.get(myCalendar.getId());
    }
}