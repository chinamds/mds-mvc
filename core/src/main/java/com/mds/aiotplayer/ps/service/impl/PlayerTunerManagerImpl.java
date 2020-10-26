/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.ps.service.impl;

import com.mds.aiotplayer.ps.dao.PlayerTunerDao;
import com.mds.aiotplayer.ps.model.PlayerTuner;
import com.mds.aiotplayer.ps.service.PlayerTunerManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("playerTunerManager")
@WebService(serviceName = "PlayerTunerService", endpointInterface = "com.mds.aiotplayer.ps.service.PlayerTunerManager")
public class PlayerTunerManagerImpl extends GenericManagerImpl<PlayerTuner, Long> implements PlayerTunerManager {
    PlayerTunerDao playerTunerDao;

    @Autowired
    public PlayerTunerManagerImpl(PlayerTunerDao playerTunerDao) {
        super(playerTunerDao);
        this.playerTunerDao = playerTunerDao;
    }
}