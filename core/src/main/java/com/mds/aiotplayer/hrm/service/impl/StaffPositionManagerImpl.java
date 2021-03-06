/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.hrm.service.impl;

import com.mds.aiotplayer.hrm.dao.StaffPositionDao;
import com.mds.aiotplayer.hrm.model.StaffPosition;
import com.mds.aiotplayer.hrm.service.StaffPositionManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("staffPositionManager")
@WebService(serviceName = "StaffPositionService", endpointInterface = "com.mds.aiotplayer.service.StaffPositionManager")
public class StaffPositionManagerImpl extends GenericManagerImpl<StaffPosition, Long> implements StaffPositionManager {
    StaffPositionDao staffPositionDao;

    @Autowired
    public StaffPositionManagerImpl(StaffPositionDao staffPositionDao) {
        super(staffPositionDao);
        this.staffPositionDao = staffPositionDao;
    }
}