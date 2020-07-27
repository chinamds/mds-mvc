package com.mds.aiotplayer.common.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.common.model.State;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface StateManager extends GenericManager<State, Long> {
    
}