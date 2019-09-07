package com.mds.sys.dao;

import java.util.Date;

import com.mds.common.dao.GenericDao;
import com.mds.sys.model.MyCalendar;

/**
 * An interface that provides a data management interface to the MyCalendar table.
 */
public interface MyCalendarDao extends GenericDao<MyCalendar, Long> {
	Long countRecentlyCalendar(Long userId, Date nowDate, Date nowTime, Integer interval);
	/**
     * Saves a myCalendar's information.
     * @param myCalendar the object to be saved
     * @return the persisted MyCalendar object
     */
    MyCalendar saveMyCalendar(MyCalendar myCalendar);
}