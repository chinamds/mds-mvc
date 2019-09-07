package com.mds.cm.dao;

import com.mds.common.dao.BaseDaoTestCase;
import com.mds.cm.model.GalleryMapping;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class GalleryMappingDaoTest extends BaseDaoTestCase {
    @Autowired
    private GalleryMappingDao galleryMappingDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveGalleryMapping() {
        GalleryMapping galleryMapping = new GalleryMapping();

        // enter all required fields

        log.debug("adding galleryMapping...");
        galleryMapping = galleryMappingDao.save(galleryMapping);

        galleryMapping = galleryMappingDao.get(galleryMapping.getId());

        assertNotNull(galleryMapping.getId());

        log.debug("removing galleryMapping...");

        galleryMappingDao.remove(galleryMapping.getId());

        // should throw DataAccessException 
        galleryMappingDao.get(galleryMapping.getId());
    }
}