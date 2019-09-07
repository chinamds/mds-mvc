package com.mds.hrm.service;

import com.mds.common.service.GenericManager;
import com.mds.hrm.model.StaffDepartment;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface StaffDepartmentManager extends GenericManager<StaffDepartment, Long> {
    
}