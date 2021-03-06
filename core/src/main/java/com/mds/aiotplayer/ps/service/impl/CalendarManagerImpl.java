/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.ps.service.impl;

import com.mds.aiotplayer.ps.dao.CalendarDao;
import com.mds.aiotplayer.ps.model.Calendar;
import com.mds.aiotplayer.ps.service.CalendarManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("calendarManager")
@WebService(serviceName = "CalendarService", endpointInterface = "com.mds.aiotplayer.ps.service.CalendarManager")
public class CalendarManagerImpl extends GenericManagerImpl<Calendar, Long> implements CalendarManager {
    CalendarDao calendarDao;

    @Autowired
    public CalendarManagerImpl(CalendarDao calendarDao) {
        super(calendarDao);
        this.calendarDao = calendarDao;
    }
}