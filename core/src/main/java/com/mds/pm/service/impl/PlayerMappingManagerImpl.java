package com.mds.pm.service.impl;

import com.mds.pm.dao.PlayerMappingDao;
import com.mds.pm.model.PlayerMapping;
import com.mds.pm.service.PlayerMappingManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("playerMappingManager")
@WebService(serviceName = "PlayerMappingService", endpointInterface = "com.mds.pm.service.PlayerMappingManager")
public class PlayerMappingManagerImpl extends GenericManagerImpl<PlayerMapping, Long> implements PlayerMappingManager {
    PlayerMappingDao playerMappingDao;

    @Autowired
    public PlayerMappingManagerImpl(PlayerMappingDao playerMappingDao) {
        super(playerMappingDao);
        this.playerMappingDao = playerMappingDao;
    }
}