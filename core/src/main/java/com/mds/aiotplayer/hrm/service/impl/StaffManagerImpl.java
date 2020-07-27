package com.mds.aiotplayer.hrm.service.impl;

import com.mds.aiotplayer.hrm.dao.StaffDao;
import com.mds.aiotplayer.hrm.model.Staff;
import com.mds.aiotplayer.hrm.service.StaffManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("staffManager")
@WebService(serviceName = "StaffService", endpointInterface = "com.mds.aiotplayer.hrm.service.StaffManager")
public class StaffManagerImpl extends GenericManagerImpl<Staff, Long> implements StaffManager {
    StaffDao staffDao;

    @Autowired
    public StaffManagerImpl(StaffDao staffDao) {
        super(staffDao);
        this.staffDao = staffDao;
    }
}