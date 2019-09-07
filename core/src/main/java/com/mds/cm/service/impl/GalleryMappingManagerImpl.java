package com.mds.cm.service.impl;

import com.mds.cm.dao.GalleryMappingDao;
import com.mds.cm.model.GalleryMapping;
import com.mds.cm.service.GalleryMappingManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("galleryMappingManager")
@WebService(serviceName = "GalleryMappingService", endpointInterface = "com.mds.cm.service.GalleryMappingManager")
public class GalleryMappingManagerImpl extends GenericManagerImpl<GalleryMapping, Long> implements GalleryMappingManager {
    GalleryMappingDao galleryMappingDao;

    @Autowired
    public GalleryMappingManagerImpl(GalleryMappingDao galleryMappingDao) {
        super(galleryMappingDao);
        this.galleryMappingDao = galleryMappingDao;
    }
}