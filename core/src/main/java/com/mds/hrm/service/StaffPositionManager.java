package com.mds.hrm.service;

import com.mds.common.service.GenericManager;
import com.mds.hrm.model.StaffPosition;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface StaffPositionManager extends GenericManager<StaffPosition, Long> {
    
}