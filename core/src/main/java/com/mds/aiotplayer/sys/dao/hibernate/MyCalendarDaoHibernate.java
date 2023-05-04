/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao.hibernate;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import com.mds.aiotplayer.common.model.Parameter;
import com.mds.aiotplayer.sys.dao.MyCalendarDao;
import com.mds.aiotplayer.sys.model.MyCalendar;

@Repository("myCalendarDao")
public class MyCalendarDaoHibernate extends GenericDaoHibernate<MyCalendar, Long> implements MyCalendarDao {

    public MyCalendarDaoHibernate() {
        super(MyCalendar.class);
    }
    
    public Long countRecentlyCalendar(Long userId, Date nowDate, Date nowTime, Integer interval){
    	List count = find("select count(id) from MyCalendar where user.id=:p1 and ((startDate=:p2 and (startTime is null or startTime<:p3)) "
    			+ "or (startDate > :p2 and startDate<=(:p2+:p4)) or (startDate<:p2 and (startDate + duration)>:p2) or ((startDate + duration)=:p2 and "
    			+ "(endTime is null or endTime>:p3)))", new Parameter(userId, nowDate, nowTime, interval));
    	if (count.size() > 0)
    		return Long.valueOf(count.get(0).toString());
    	else
    		return 0L;
    }

	/**
     * {@inheritDoc}
     */
    public MyCalendar saveMyCalendar(MyCalendar myCalendar) {
        if (log.isDebugEnabled()) {
            log.debug("myCalendar's id: " + myCalendar.getId());
        }
        var result = super.save(myCalendar);
        // necessary to throw a DataIntegrityViolation and catch it in MyCalendarManager
        getEntityManager().flush();
        return result;
    }
}
