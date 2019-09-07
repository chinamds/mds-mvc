package com.mds.hrm.service.impl;

import com.mds.hrm.dao.PositionDao;
import com.mds.hrm.model.Position;
import com.mds.hrm.service.PositionManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("positionManager")
@WebService(serviceName = "PositionService", endpointInterface = "com.mds.service.PositionManager")
public class PositionManagerImpl extends GenericManagerImpl<Position, Long> implements PositionManager {
    PositionDao positionDao;

    @Autowired
    public PositionManagerImpl(PositionDao positionDao) {
        super(positionDao);
        this.positionDao = positionDao;
    }
}