package com.mds.aiotplayer.pm.service.impl;

import com.mds.aiotplayer.pm.dao.PlayerMappingDao;
import com.mds.aiotplayer.pm.model.PlayerMapping;
import com.mds.aiotplayer.pm.service.PlayerMappingManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("playerMappingManager")
@WebService(serviceName = "PlayerMappingService", endpointInterface = "com.mds.aiotplayer.pm.service.PlayerMappingManager")
public class PlayerMappingManagerImpl extends GenericManagerImpl<PlayerMapping, Long> implements PlayerMappingManager {
    PlayerMappingDao playerMappingDao;

    @Autowired
    public PlayerMappingManagerImpl(PlayerMappingDao playerMappingDao) {
        super(playerMappingDao);
        this.playerMappingDao = playerMappingDao;
    }
}