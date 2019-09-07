package com.mds.ps.service.impl;

import com.mds.ps.dao.PlayerTunerDao;
import com.mds.ps.model.PlayerTuner;
import com.mds.ps.service.PlayerTunerManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("playerTunerManager")
@WebService(serviceName = "PlayerTunerService", endpointInterface = "com.mds.ps.service.PlayerTunerManager")
public class PlayerTunerManagerImpl extends GenericManagerImpl<PlayerTuner, Long> implements PlayerTunerManager {
    PlayerTunerDao playerTunerDao;

    @Autowired
    public PlayerTunerManagerImpl(PlayerTunerDao playerTunerDao) {
        super(playerTunerDao);
        this.playerTunerDao = playerTunerDao;
    }
}