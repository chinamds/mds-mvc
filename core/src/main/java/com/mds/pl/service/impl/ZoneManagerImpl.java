package com.mds.pl.service.impl;

import com.mds.pl.dao.ZoneDao;
import com.mds.pl.model.Zone;
import com.mds.pl.service.ZoneManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("zoneManager")
@WebService(serviceName = "ZoneService", endpointInterface = "com.mds.service.ZoneManager")
public class ZoneManagerImpl extends GenericManagerImpl<Zone, Long> implements ZoneManager {
    ZoneDao zoneDao;

    @Autowired
    public ZoneManagerImpl(ZoneDao zoneDao) {
        super(zoneDao);
        this.zoneDao = zoneDao;
    }
}