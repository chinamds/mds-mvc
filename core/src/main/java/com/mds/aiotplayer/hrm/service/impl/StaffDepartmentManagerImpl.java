/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.hrm.service.impl;

import com.mds.aiotplayer.hrm.dao.StaffDepartmentDao;
import com.mds.aiotplayer.hrm.model.StaffDepartment;
import com.mds.aiotplayer.hrm.service.StaffDepartmentManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("staffDepartmentManager")
@WebService(serviceName = "StaffDepartmentService", endpointInterface = "com.mds.aiotplayer.service.StaffDepartmentManager")
public class StaffDepartmentManagerImpl extends GenericManagerImpl<StaffDepartment, Long> implements StaffDepartmentManager {
    StaffDepartmentDao staffDepartmentDao;

    @Autowired
    public StaffDepartmentManagerImpl(StaffDepartmentDao staffDepartmentDao) {
        super(staffDepartmentDao);
        this.staffDepartmentDao = staffDepartmentDao;
    }
}