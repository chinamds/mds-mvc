package com.mds.hrm.service;

import com.mds.common.service.GenericManager;
import com.mds.hrm.model.Staff;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface StaffManager extends GenericManager<Staff, Long> {
    
}