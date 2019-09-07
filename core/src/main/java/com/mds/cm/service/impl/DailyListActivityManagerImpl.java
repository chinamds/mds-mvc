package com.mds.cm.service.impl;

import com.mds.cm.dao.DailyListActivityDao;
import com.mds.cm.model.DailyListActivity;
import com.mds.cm.service.DailyListActivityManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("dailyListActivityManager")
@WebService(serviceName = "DailyListActivityService", endpointInterface = "com.mds.service.DailyListActivityManager")
public class DailyListActivityManagerImpl extends GenericManagerImpl<DailyListActivity, Long> implements DailyListActivityManager {
    DailyListActivityDao dailyListActivityDao;

    @Autowired
    public DailyListActivityManagerImpl(DailyListActivityDao dailyListActivityDao) {
        super(dailyListActivityDao);
        this.dailyListActivityDao = dailyListActivityDao;
    }
}