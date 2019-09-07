package com.mds.pl.service;

import com.mds.common.service.GenericManager;
import com.mds.pl.model.PlaylistItem;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface PlaylistItemManager extends GenericManager<PlaylistItem, Long> {
    
}