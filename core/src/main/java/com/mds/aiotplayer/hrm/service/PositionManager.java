package com.mds.aiotplayer.hrm.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.hrm.model.Position;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface PositionManager extends GenericManager<Position, Long> {
    
}