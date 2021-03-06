/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pm.service.impl;

import com.mds.aiotplayer.pm.dao.PlayerOutputDao;
import com.mds.aiotplayer.pm.model.PlayerOutput;
import com.mds.aiotplayer.pm.service.PlayerOutputManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("playerOutputManager")
@WebService(serviceName = "PlayerOutputService", endpointInterface = "com.mds.aiotplayer.pm.service.PlayerOutputManager")
public class PlayerOutputManagerImpl extends GenericManagerImpl<PlayerOutput, Long> implements PlayerOutputManager {
    PlayerOutputDao playerOutputDao;

    @Autowired
    public PlayerOutputManagerImpl(PlayerOutputDao playerOutputDao) {
        super(playerOutputDao);
        this.playerOutputDao = playerOutputDao;
    }
}