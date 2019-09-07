package com.mds.ps.service.impl;

import com.mds.ps.dao.CalendarDao;
import com.mds.ps.model.Calendar;
import com.mds.ps.service.CalendarManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("calendarManager")
@WebService(serviceName = "CalendarService", endpointInterface = "com.mds.ps.service.CalendarManager")
public class CalendarManagerImpl extends GenericManagerImpl<Calendar, Long> implements CalendarManager {
    CalendarDao calendarDao;

    @Autowired
    public CalendarManagerImpl(CalendarDao calendarDao) {
        super(calendarDao);
        this.calendarDao = calendarDao;
    }
}