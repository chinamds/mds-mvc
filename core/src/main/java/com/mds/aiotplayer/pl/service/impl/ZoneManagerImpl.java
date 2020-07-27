package com.mds.aiotplayer.pl.service.impl;

import com.mds.aiotplayer.pl.dao.ZoneDao;
import com.mds.aiotplayer.pl.model.Zone;
import com.mds.aiotplayer.pl.service.ZoneManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("zoneManager")
@WebService(serviceName = "ZoneService", endpointInterface = "com.mds.aiotplayer.service.ZoneManager")
public class ZoneManagerImpl extends GenericManagerImpl<Zone, Long> implements ZoneManager {
    ZoneDao zoneDao;

    @Autowired
    public ZoneManagerImpl(ZoneDao zoneDao) {
        super(zoneDao);
        this.zoneDao = zoneDao;
    }
}