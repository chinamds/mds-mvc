package com.mds.hrm.service.impl;

import com.mds.hrm.dao.StaffDepartmentDao;
import com.mds.hrm.model.StaffDepartment;
import com.mds.hrm.service.StaffDepartmentManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("staffDepartmentManager")
@WebService(serviceName = "StaffDepartmentService", endpointInterface = "com.mds.service.StaffDepartmentManager")
public class StaffDepartmentManagerImpl extends GenericManagerImpl<StaffDepartment, Long> implements StaffDepartmentManager {
    StaffDepartmentDao staffDepartmentDao;

    @Autowired
    public StaffDepartmentManagerImpl(StaffDepartmentDao staffDepartmentDao) {
        super(staffDepartmentDao);
        this.staffDepartmentDao = staffDepartmentDao;
    }
}