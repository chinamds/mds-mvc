/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.hrm.service.impl;

import com.mds.aiotplayer.hrm.dao.DepartmentDao;
import com.mds.aiotplayer.hrm.model.Department;
import com.mds.aiotplayer.hrm.service.DepartmentManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("departmentManager")
@WebService(serviceName = "DepartmentService", endpointInterface = "com.mds.aiotplayer.service.DepartmentManager")
public class DepartmentManagerImpl extends GenericManagerImpl<Department, Long> implements DepartmentManager {
    DepartmentDao departmentDao;

    @Autowired
    public DepartmentManagerImpl(DepartmentDao departmentDao) {
        super(departmentDao);
        this.departmentDao = departmentDao;
    }
}