package com.mds.aiotplayer.cm.service.impl;

import com.mds.aiotplayer.cm.dao.DailyListActivityDao;
import com.mds.aiotplayer.cm.model.DailyListActivity;
import com.mds.aiotplayer.cm.service.DailyListActivityManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("dailyListActivityManager")
@WebService(serviceName = "DailyListActivityService", endpointInterface = "com.mds.aiotplayer.service.DailyListActivityManager")
public class DailyListActivityManagerImpl extends GenericManagerImpl<DailyListActivity, Long> implements DailyListActivityManager {
    DailyListActivityDao dailyListActivityDao;

    @Autowired
    public DailyListActivityManagerImpl(DailyListActivityDao dailyListActivityDao) {
        super(dailyListActivityDao);
        this.dailyListActivityDao = dailyListActivityDao;
    }
}