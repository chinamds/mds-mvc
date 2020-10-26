/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pm.service.impl;

import com.mds.aiotplayer.pm.dao.PlayerGroup2PlayerDao;
import com.mds.aiotplayer.pm.model.PlayerGroup2Player;
import com.mds.aiotplayer.pm.service.PlayerGroup2PlayerManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("playerGroup2PlayerManager")
@WebService(serviceName = "PlayerGroup2PlayerService", endpointInterface = "com.mds.aiotplayer.pm.service.PlayerGroup2PlayerManager")
public class PlayerGroup2PlayerManagerImpl extends GenericManagerImpl<PlayerGroup2Player, Long> implements PlayerGroup2PlayerManager {
    PlayerGroup2PlayerDao playerGroup2PlayerDao;

    @Autowired
    public PlayerGroup2PlayerManagerImpl(PlayerGroup2PlayerDao playerGroup2PlayerDao) {
        super(playerGroup2PlayerDao);
        this.playerGroup2PlayerDao = playerGroup2PlayerDao;
    }
}