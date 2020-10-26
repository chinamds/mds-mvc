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

import com.mds.aiotplayer.common.dao.CountryDao;
import com.mds.aiotplayer.common.model.Country;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class CountryManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private CountryManagerImpl manager;

    @Mock
    private CountryDao dao;

    @Test
    public void testGetCountry() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Country country = new Country();
        given(dao.get(id)).willReturn(country);

        //when
        Country result = manager.get(id);

        //then
        assertSame(country, result);
    }

    @Test
    public void testGetCountries() {
        log.debug("testing getAll...");
        //given
        final List<Country> countries = new ArrayList<>();
        given(dao.getAll()).willReturn(countries);

        //when
        List result = manager.getAll();

        //then
        assertSame(countries, result);
    }

    @Test
    public void testSaveCountry() {
        log.debug("testing save...");

        //given
        final Country country = new Country();
        // enter all required fields
        country.setCountryCode("QfBjYiRjSsCsAaJvRlKuBtFyVbRmXaCoWrWeYpKrDlDvKnOpPvYzQqHuMqDfBoZzVxQmViMsHzZqIpXgKpUdPtUaSxBiPaFqLbVs");

        given(dao.save(country)).willReturn(country);

        //when
        manager.save(country);

        //then
        verify(dao).save(country);
    }

    @Test
    public void testRemoveCountry() {
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
