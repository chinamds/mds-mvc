package com.mds.pm.service;

import com.mds.common.service.GenericManager;
import com.mds.pm.model.PlayerOutput;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface PlayerOutputManager extends GenericManager<PlayerOutput, Long> {
    
}