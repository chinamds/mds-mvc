package com.mds.aiotplayer.pm.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.pm.model.PlayerOutput;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface PlayerOutputManager extends GenericManager<PlayerOutput, Long> {
    
}