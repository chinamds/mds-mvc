package com.mds.ps.dao.hibernate;

import com.mds.ps.model.Calendar;
import com.mds.ps.dao.CalendarDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("calendarDao")
public class CalendarDaoHibernate extends GenericDaoHibernate<Calendar, Long> implements CalendarDao {

    public CalendarDaoHibernate() {
        super(Calendar.class);
    }
}
