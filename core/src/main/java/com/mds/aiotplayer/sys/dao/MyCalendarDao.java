/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao;

import java.util.Date;

import com.mds.aiotplayer.common.dao.GenericDao;
import com.mds.aiotplayer.sys.model.MyCalendar;

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