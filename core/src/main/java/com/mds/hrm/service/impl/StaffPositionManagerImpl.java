package com.mds.hrm.service.impl;

import com.mds.hrm.dao.StaffPositionDao;
import com.mds.hrm.model.StaffPosition;
import com.mds.hrm.service.StaffPositionManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("staffPositionManager")
@WebService(serviceName = "StaffPositionService", endpointInterface = "com.mds.service.StaffPositionManager")
public class StaffPositionManagerImpl extends GenericManagerImpl<StaffPosition, Long> implements StaffPositionManager {
    StaffPositionDao staffPositionDao;

    @Autowired
    public StaffPositionManagerImpl(StaffPositionDao staffPositionDao) {
        super(staffPositionDao);
        this.staffPositionDao = staffPositionDao;
    }
}