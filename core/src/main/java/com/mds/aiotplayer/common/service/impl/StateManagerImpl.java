package com.mds.aiotplayer.common.service.impl;

import com.mds.aiotplayer.common.dao.StateDao;
import com.mds.aiotplayer.common.model.State;
import com.mds.aiotplayer.common.service.StateManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("stateManager")
@WebService(serviceName = "StateService", endpointInterface = "com.mds.aiotplayer.common.service.StateManager")
public class StateManagerImpl extends GenericManagerImpl<State, Long> implements StateManager {
    StateDao stateDao;

    @Autowired
    public StateManagerImpl(StateDao stateDao) {
        super(stateDao);
        this.stateDao = stateDao;
    }
}