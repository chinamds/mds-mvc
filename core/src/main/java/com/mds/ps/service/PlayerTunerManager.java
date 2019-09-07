package com.mds.ps.service;

import com.mds.common.service.GenericManager;
import com.mds.ps.model.PlayerTuner;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface PlayerTunerManager extends GenericManager<PlayerTuner, Long> {
    
}