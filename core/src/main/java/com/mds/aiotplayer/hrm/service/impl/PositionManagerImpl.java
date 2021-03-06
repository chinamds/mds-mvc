/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.hrm.service.impl;

import com.mds.aiotplayer.hrm.dao.PositionDao;
import com.mds.aiotplayer.hrm.model.Position;
import com.mds.aiotplayer.hrm.service.PositionManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("positionManager")
@WebService(serviceName = "PositionService", endpointInterface = "com.mds.aiotplayer.service.PositionManager")
public class PositionManagerImpl extends GenericManagerImpl<Position, Long> implements PositionManager {
    PositionDao positionDao;

    @Autowired
    public PositionManagerImpl(PositionDao positionDao) {
        super(positionDao);
        this.positionDao = positionDao;
    }
}