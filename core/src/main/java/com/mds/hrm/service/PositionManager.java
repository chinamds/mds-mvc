package com.mds.hrm.service;

import com.mds.common.service.GenericManager;
import com.mds.hrm.model.Position;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface PositionManager extends GenericManager<Position, Long> {
    
}