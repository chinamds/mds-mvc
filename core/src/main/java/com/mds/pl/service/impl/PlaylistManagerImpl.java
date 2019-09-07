package com.mds.pl.service.impl;

import com.mds.pl.dao.PlaylistDao;
import com.mds.pl.model.Playlist;
import com.mds.pl.service.PlaylistManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("playlistManager")
@WebService(serviceName = "PlaylistService", endpointInterface = "com.mds.pl.service.PlaylistManager")
public class PlaylistManagerImpl extends GenericManagerImpl<Playlist, Long> implements PlaylistManager {
    PlaylistDao playlistDao;

    @Autowired
    public PlaylistManagerImpl(PlaylistDao playlistDao) {
        super(playlistDao);
        this.playlistDao = playlistDao;
    }
}