/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.common.dao.ZipCodeTypeDao;
import com.mds.aiotplayer.common.model.ZipCodeType;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class ZipCodeTypeManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private ZipCodeTypeManagerImpl manager;

    @Mock
    private ZipCodeTypeDao dao;

    @Test
    public void testGetZipCodeType() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final ZipCodeType zipCodeType = new ZipCodeType();
        given(dao.get(id)).willReturn(zipCodeType);

        //when
        ZipCodeType result = manager.get(id);

        //then
        assertSame(zipCodeType, result);
    }

    @Test
    public void testGetZipCodeTypes() {
        log.debug("testing getAll...");
        //given
        final List<ZipCodeType> zipCodeTypes = new ArrayList<>();
        given(dao.getAll()).willReturn(zipCodeTypes);

        //when
        List result = manager.getAll();

        //then
        assertSame(zipCodeTypes, result);
    }

    @Test
    public void testSaveZipCodeType() {
        log.debug("testing save...");

        //given
        final ZipCodeType zipCodeType = new ZipCodeType();
        // enter all required fields
        zipCodeType.setType("RbYyJqRwQmBzPjJvSrDrXmGgXiDoBdXdXiOoLfNxRdRiCqMmUiKmIlFfKiHxQaPaXwYfFyQhEjPbIlMiCzQeDxJnMiSvUmZjAjLz");

        given(dao.save(zipCodeType)).willReturn(zipCodeType);

        //when
        manager.save(zipCodeType);

        //then
        verify(dao).save(zipCodeType);
    }

    @Test
    public void testRemoveZipCodeType() {
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
