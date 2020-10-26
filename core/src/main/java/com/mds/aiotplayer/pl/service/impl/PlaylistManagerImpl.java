/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pl.service.impl;

import com.mds.aiotplayer.pl.dao.PlaylistDao;
import com.mds.aiotplayer.pl.model.Playlist;
import com.mds.aiotplayer.pl.service.PlaylistManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("playlistManager")
@WebService(serviceName = "PlaylistService", endpointInterface = "com.mds.aiotplayer.pl.service.PlaylistManager")
public class PlaylistManagerImpl extends GenericManagerImpl<Playlist, Long> implements PlaylistManager {
    PlaylistDao playlistDao;

    @Autowired
    public PlaylistManagerImpl(PlaylistDao playlistDao) {
        super(playlistDao);
        this.playlistDao = playlistDao;
    }
}