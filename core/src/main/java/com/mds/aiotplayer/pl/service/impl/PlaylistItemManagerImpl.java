package com.mds.aiotplayer.pl.service.impl;

import com.mds.aiotplayer.pl.dao.PlaylistItemDao;
import com.mds.aiotplayer.pl.model.PlaylistItem;
import com.mds.aiotplayer.pl.service.PlaylistItemManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("playlistItemManager")
@WebService(serviceName = "PlaylistItemService", endpointInterface = "com.mds.aiotplayer.pl.service.PlaylistItemManager")
public class PlaylistItemManagerImpl extends GenericManagerImpl<PlaylistItem, Long> implements PlaylistItemManager {
    PlaylistItemDao playlistItemDao;

    @Autowired
    public PlaylistItemManagerImpl(PlaylistItemDao playlistItemDao) {
        super(playlistItemDao);
        this.playlistItemDao = playlistItemDao;
    }
}