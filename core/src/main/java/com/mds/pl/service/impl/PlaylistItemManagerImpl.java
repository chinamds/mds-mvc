package com.mds.pl.service.impl;

import com.mds.pl.dao.PlaylistItemDao;
import com.mds.pl.model.PlaylistItem;
import com.mds.pl.service.PlaylistItemManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("playlistItemManager")
@WebService(serviceName = "PlaylistItemService", endpointInterface = "com.mds.pl.service.PlaylistItemManager")
public class PlaylistItemManagerImpl extends GenericManagerImpl<PlaylistItem, Long> implements PlaylistItemManager {
    PlaylistItemDao playlistItemDao;

    @Autowired
    public PlaylistItemManagerImpl(PlaylistItemDao playlistItemDao) {
        super(playlistItemDao);
        this.playlistItemDao = playlistItemDao;
    }
}