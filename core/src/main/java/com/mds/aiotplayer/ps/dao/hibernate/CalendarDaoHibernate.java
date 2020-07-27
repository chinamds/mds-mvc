package com.mds.aiotplayer.ps.dao.hibernate;

import com.mds.aiotplayer.ps.model.Calendar;
import com.mds.aiotplayer.ps.dao.CalendarDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("calendarDao")
public class CalendarDaoHibernate extends GenericDaoHibernate<Calendar, Long> implements CalendarDao {

    public CalendarDaoHibernate() {
        super(Calendar.class);
    }
}
