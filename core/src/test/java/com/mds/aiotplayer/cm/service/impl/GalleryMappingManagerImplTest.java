package com.mds.aiotplayer.cm.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.cm.dao.GalleryMappingDao;
import com.mds.aiotplayer.cm.model.GalleryMapping;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class GalleryMappingManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private GalleryMappingManagerImpl manager;

    @Mock
    private GalleryMappingDao dao;

    @Test
    public void testGetGalleryMapping() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final GalleryMapping galleryMapping = new GalleryMapping();
        given(dao.get(id)).willReturn(galleryMapping);

        //when
        GalleryMapping result = manager.get(id);

        //then
        assertSame(galleryMapping, result);
    }

    @Test
    public void testGetGalleryMappings() {
        log.debug("testing getAll...");
        //given
        final List<GalleryMapping> galleryMappings = new ArrayList<>();
        given(dao.getAll()).willReturn(galleryMappings);

        //when
        List result = manager.getAll();

        //then
        assertSame(galleryMappings, result);
    }

    @Test
    public void testSaveGalleryMapping() {
        log.debug("testing save...");

        //given
        final GalleryMapping galleryMapping = new GalleryMapping();
        // enter all required fields

        given(dao.save(galleryMapping)).willReturn(galleryMapping);

        //when
        manager.save(galleryMapping);

        //then
        verify(dao).save(galleryMapping);
    }

    @Test
    public void testRemoveGalleryMapping() {
        log.debug("testing remove...");

        //given
        final Long id = -11L;
        willDoNothing().given(dao).remove(id);

        //when
        manager.remove(id);

        //then
        verify(dao).remove(id);
    }
}
