package com.mds.common.service.impl;

import com.mds.common.dao.StateDao;
import com.mds.common.model.State;
import com.mds.common.service.StateManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("stateManager")
@WebService(serviceName = "StateService", endpointInterface = "com.mds.common.service.StateManager")
public class StateManagerImpl extends GenericManagerImpl<State, Long> implements StateManager {
    StateDao stateDao;

    @Autowired
    public StateManagerImpl(StateDao stateDao) {
        super(stateDao);
        this.stateDao = stateDao;
    }
}