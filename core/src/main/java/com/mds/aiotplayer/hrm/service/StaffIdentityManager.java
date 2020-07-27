package com.mds.aiotplayer.hrm.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.hrm.model.StaffIdentity;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface StaffIdentityManager extends GenericManager<StaffIdentity, Long> {
    
}