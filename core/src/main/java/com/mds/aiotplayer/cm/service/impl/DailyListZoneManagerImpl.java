/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service.impl;

import com.mds.aiotplayer.cm.dao.DailyListZoneDao;
import com.mds.aiotplayer.cm.model.DailyListZone;
import com.mds.aiotplayer.cm.service.DailyListZoneManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("dailyListZoneManager")
@WebService(serviceName = "DailyListZoneService", endpointInterface = "com.mds.aiotplayer.service.DailyListZoneManager")
public class DailyListZoneManagerImpl extends GenericManagerImpl<DailyListZone, Long> implements DailyListZoneManager {
    DailyListZoneDao dailyListZoneDao;

    @Autowired
    public DailyListZoneManagerImpl(DailyListZoneDao dailyListZoneDao) {
        super(dailyListZoneDao);
        this.dailyListZoneDao = dailyListZoneDao;
    }
}