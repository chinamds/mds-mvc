/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service.impl;

import com.mds.aiotplayer.cm.dao.GalleryMappingDao;
import com.mds.aiotplayer.cm.model.GalleryMapping;
import com.mds.aiotplayer.cm.service.GalleryMappingManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("galleryMappingManager")
@WebService(serviceName = "GalleryMappingService", endpointInterface = "com.mds.aiotplayer.cm.service.GalleryMappingManager")
public class GalleryMappingManagerImpl extends GenericManagerImpl<GalleryMapping, Long> implements GalleryMappingManager {
    GalleryMappingDao galleryMappingDao;

    @Autowired
    public GalleryMappingManagerImpl(GalleryMappingDao galleryMappingDao) {
        super(galleryMappingDao);
        this.galleryMappingDao = galleryMappingDao;
    }
}