package com.mds.aiotplayer.ps.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.ps.model.PlayerTuner;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface PlayerTunerManager extends GenericManager<PlayerTuner, Long> {
    
}