package com.mds.hrm.service;

import com.mds.common.service.GenericManager;
import com.mds.hrm.model.Department;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface DepartmentManager extends GenericManager<Department, Long> {
    
}