package com.mds.hrm.service.impl;

import com.mds.hrm.dao.StaffIdentityDao;
import com.mds.hrm.model.StaffIdentity;
import com.mds.hrm.service.StaffIdentityManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("staffIdentityManager")
@WebService(serviceName = "StaffIdentityService", endpointInterface = "com.mds.service.StaffIdentityManager")
public class StaffIdentityManagerImpl extends GenericManagerImpl<StaffIdentity, Long> implements StaffIdentityManager {
    StaffIdentityDao staffIdentityDao;

    @Autowired
    public StaffIdentityManagerImpl(StaffIdentityDao staffIdentityDao) {
        super(staffIdentityDao);
        this.staffIdentityDao = staffIdentityDao;
    }
}