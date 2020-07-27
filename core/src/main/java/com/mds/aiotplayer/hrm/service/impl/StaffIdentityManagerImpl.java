package com.mds.aiotplayer.hrm.service.impl;

import com.mds.aiotplayer.hrm.dao.StaffIdentityDao;
import com.mds.aiotplayer.hrm.model.StaffIdentity;
import com.mds.aiotplayer.hrm.service.StaffIdentityManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("staffIdentityManager")
@WebService(serviceName = "StaffIdentityService", endpointInterface = "com.mds.aiotplayer.service.StaffIdentityManager")
public class StaffIdentityManagerImpl extends GenericManagerImpl<StaffIdentity, Long> implements StaffIdentityManager {
    StaffIdentityDao staffIdentityDao;

    @Autowired
    public StaffIdentityManagerImpl(StaffIdentityDao staffIdentityDao) {
        super(staffIdentityDao);
        this.staffIdentityDao = staffIdentityDao;
    }
}