package com.mds.pm.service.impl;

import com.mds.pm.dao.PlayerOutputDao;
import com.mds.pm.model.PlayerOutput;
import com.mds.pm.service.PlayerOutputManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("playerOutputManager")
@WebService(serviceName = "PlayerOutputService", endpointInterface = "com.mds.pm.service.PlayerOutputManager")
public class PlayerOutputManagerImpl extends GenericManagerImpl<PlayerOutput, Long> implements PlayerOutputManager {
    PlayerOutputDao playerOutputDao;

    @Autowired
    public PlayerOutputManagerImpl(PlayerOutputDao playerOutputDao) {
        super(playerOutputDao);
        this.playerOutputDao = playerOutputDao;
    }
}