package com.mds.hrm.service.impl;

import com.mds.hrm.dao.DepartmentDao;
import com.mds.hrm.model.Department;
import com.mds.hrm.service.DepartmentManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("departmentManager")
@WebService(serviceName = "DepartmentService", endpointInterface = "com.mds.service.DepartmentManager")
public class DepartmentManagerImpl extends GenericManagerImpl<Department, Long> implements DepartmentManager {
    DepartmentDao departmentDao;

    @Autowired
    public DepartmentManagerImpl(DepartmentDao departmentDao) {
        super(departmentDao);
        this.departmentDao = departmentDao;
    }
}