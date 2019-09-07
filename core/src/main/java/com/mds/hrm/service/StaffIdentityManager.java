package com.mds.hrm.service;

import com.mds.common.service.GenericManager;
import com.mds.hrm.model.StaffIdentity;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface StaffIdentityManager extends GenericManager<StaffIdentity, Long> {
    
}