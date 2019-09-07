package com.mds.hrm.service.impl;

import com.mds.hrm.dao.StaffDao;
import com.mds.hrm.model.Staff;
import com.mds.hrm.service.StaffManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("staffManager")
@WebService(serviceName = "StaffService", endpointInterface = "com.mds.hrm.service.StaffManager")
public class StaffManagerImpl extends GenericManagerImpl<Staff, Long> implements StaffManager {
    StaffDao staffDao;

    @Autowired
    public StaffManagerImpl(StaffDao staffDao) {
        super(staffDao);
        this.staffDao = staffDao;
    }
}