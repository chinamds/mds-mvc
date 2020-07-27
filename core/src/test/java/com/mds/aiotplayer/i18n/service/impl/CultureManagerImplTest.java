package com.mds.aiotplayer.i18n.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.i18n.dao.CultureDao;
import com.mds.aiotplayer.i18n.model.Culture;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class CultureManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private CultureManagerImpl manager;

    @Mock
    private CultureDao dao;

    @Test
    public void testGetCulture() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Culture culture = new Culture();
        given(dao.get(id)).willReturn(culture);

        //when
        Culture result = manager.get(id);

        //then
        assertSame(culture, result);
    }

    @Test
    public void testGetCultures() {
        log.debug("testing getAll...");
        //given
        final List<Culture> culture = new ArrayList<>();
        given(dao.getAll()).willReturn(culture);

        //when
        List result = manager.getAll();

        //then
        assertSame(culture, result);
    }

    @Test
    public void testSaveCulture() {
        log.debug("testing save...");

        //given
        final Culture culture = new Culture();
        // enter all required fields

        given(dao.save(culture)).willReturn(culture);

        //when
        manager.save(culture);

        //then
        verify(dao).save(culture);
    }

    @Test
    public void testRemoveCulture() {
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
