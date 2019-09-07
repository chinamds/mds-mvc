package com.mds.cm.service.impl;

import com.mds.cm.dao.DailyListItemDao;
import com.mds.cm.model.DailyListItem;
import com.mds.cm.service.DailyListItemManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("dailyListItemManager")
@WebService(serviceName = "DailyListItemService", endpointInterface = "com.mds.service.DailyListItemManager")
public class DailyListItemManagerImpl extends GenericManagerImpl<DailyListItem, Long> implements DailyListItemManager {
    DailyListItemDao dailyListItemDao;

    @Autowired
    public DailyListItemManagerImpl(DailyListItemDao dailyListItemDao) {
        super(dailyListItemDao);
        this.dailyListItemDao = dailyListItemDao;
    }
}