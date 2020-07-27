package com.mds.aiotplayer.cm.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.cm.model.GalleryMapping;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface GalleryMappingManager extends GenericManager<GalleryMapping, Long> {
    
}