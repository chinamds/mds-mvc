package com.mds.aiotplayer.hrm.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.hrm.model.Department;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface DepartmentManager extends GenericManager<Department, Long> {
    
}