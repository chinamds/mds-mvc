package com.mds.cm.service.impl;

import com.mds.cm.dao.DailyListZoneDao;
import com.mds.cm.model.DailyListZone;
import com.mds.cm.service.DailyListZoneManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("dailyListZoneManager")
@WebService(serviceName = "DailyListZoneService", endpointInterface = "com.mds.service.DailyListZoneManager")
public class DailyListZoneManagerImpl extends GenericManagerImpl<DailyListZone, Long> implements DailyListZoneManager {
    DailyListZoneDao dailyListZoneDao;

    @Autowired
    public DailyListZoneManagerImpl(DailyListZoneDao dailyListZoneDao) {
        super(dailyListZoneDao);
        this.dailyListZoneDao = dailyListZoneDao;
    }
}