package com.mds.hrm.service;

import com.mds.common.service.GenericManager;
import com.mds.hrm.model.IdentityType;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface IdentityTypeManager extends GenericManager<IdentityType, Long> {
    
}