package com.mds.common.service;

import com.mds.common.service.GenericManager;
import com.mds.common.model.State;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface StateManager extends GenericManager<State, Long> {
    
}