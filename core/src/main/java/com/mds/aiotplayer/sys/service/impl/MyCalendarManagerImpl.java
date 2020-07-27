package com.mds.aiotplayer.sys.service.impl;

import com.mds.aiotplayer.sys.dao.MyCalendarDao;
import com.mds.aiotplayer.sys.model.MyCalendar;
import com.mds.aiotplayer.sys.service.MyCalendarManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import javax.jws.WebService;

@Service("myCalendarManager")
@WebService(serviceName = "MyCalendarService", endpointInterface = "com.mds.aiotplayer.sys.service.MyCalendarManager")
public class MyCalendarManagerImpl extends GenericManagerImpl<MyCalendar, Long> implements MyCalendarManager {
    MyCalendarDao myCalendarDao;

    @Autowired
    public MyCalendarManagerImpl(MyCalendarDao myCalendarDao) {
        super(myCalendarDao);
        this.myCalendarDao = myCalendarDao;
    }
    
    @Transactional
    @Override
    public void copyAndRemove(MyCalendar calendar) {
        removeEntity(calendar);

        MyCalendar copyCalendar = new MyCalendar();
        BeanUtils.copyProperties(calendar, copyCalendar);
        copyCalendar.setId(null);
        save(copyCalendar);
    }

    //2013 10 11   10-20   -3 > now
    //     10 11  10-19
    @Override
    public Long countRecentlyCalendar(Long userId, Integer interval) {
        Date nowDate = new Date();
        Date nowTime = new Time(nowDate.getHours(), nowDate.getMinutes(), nowDate.getSeconds());
        nowDate.setHours(0);
        nowDate.setMinutes(0);
        nowDate.setSeconds(0);

        return myCalendarDao.countRecentlyCalendar(userId, nowDate, nowTime, interval);
    }
}