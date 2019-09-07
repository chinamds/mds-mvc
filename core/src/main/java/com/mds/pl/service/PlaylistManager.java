package com.mds.pl.service;

import com.mds.common.service.GenericManager;
import com.mds.pl.model.Playlist;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface PlaylistManager extends GenericManager<Playlist, Long> {
    
}