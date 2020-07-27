package com.mds.aiotplayer.hrm.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.hrm.model.StaffPosition;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface StaffPositionManager extends GenericManager<StaffPosition, Long> {
    
}