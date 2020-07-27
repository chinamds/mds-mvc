package com.mds.aiotplayer.hrm.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.hrm.model.IdentityType;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface IdentityTypeManager extends GenericManager<IdentityType, Long> {
    
}