package com.mds.cm.service;

import com.mds.common.service.GenericManager;
import com.mds.cm.model.GalleryMapping;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface GalleryMappingManager extends GenericManager<GalleryMapping, Long> {
    
}