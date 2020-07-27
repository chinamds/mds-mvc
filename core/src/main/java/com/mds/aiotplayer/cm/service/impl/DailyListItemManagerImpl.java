package com.mds.aiotplayer.cm.service.impl;

import com.mds.aiotplayer.cm.dao.DailyListItemDao;
import com.mds.aiotplayer.cm.model.DailyListItem;
import com.mds.aiotplayer.cm.service.DailyListItemManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("dailyListItemManager")
@WebService(serviceName = "DailyListItemService", endpointInterface = "com.mds.aiotplayer.service.DailyListItemManager")
public class DailyListItemManagerImpl extends GenericManagerImpl<DailyListItem, Long> implements DailyListItemManager {
    DailyListItemDao dailyListItemDao;

    @Autowired
    public DailyListItemManagerImpl(DailyListItemDao dailyListItemDao) {
        super(dailyListItemDao);
        this.dailyListItemDao = dailyListItemDao;
    }
}