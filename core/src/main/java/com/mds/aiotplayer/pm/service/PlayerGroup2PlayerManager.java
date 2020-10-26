/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pm.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.pm.model.PlayerGroup2Player;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface PlayerGroup2PlayerManager extends GenericManager<PlayerGroup2Player, Long> {
    
}