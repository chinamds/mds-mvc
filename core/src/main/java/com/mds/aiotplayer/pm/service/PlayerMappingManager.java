package com.mds.aiotplayer.pm.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.pm.model.PlayerMapping;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface PlayerMappingManager extends GenericManager<PlayerMapping, Long> {
    
}