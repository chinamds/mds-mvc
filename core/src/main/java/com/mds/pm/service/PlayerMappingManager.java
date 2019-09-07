package com.mds.pm.service;

import com.mds.common.service.GenericManager;
import com.mds.pm.model.PlayerMapping;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface PlayerMappingManager extends GenericManager<PlayerMapping, Long> {
    
}