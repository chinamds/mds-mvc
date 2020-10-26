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

import com.mds.aiotplayer.common.dao.CurrencyDao;
import com.mds.aiotplayer.common.model.Currency;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class CurrencyManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private CurrencyManagerImpl manager;

    @Mock
    private CurrencyDao dao;

    @Test
    public void testGetCurrency() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Currency currency = new Currency();
        given(dao.get(id)).willReturn(currency);

        //when
        Currency result = manager.get(id);

        //then
        assertSame(currency, result);
    }

    @Test
    public void testGetCurrencies() {
        log.debug("testing getAll...");
        //given
        final List<Currency> currencies = new ArrayList<>();
        given(dao.getAll()).willReturn(currencies);

        //when
        List result = manager.getAll();

        //then
        assertSame(currencies, result);
    }

    @Test
    public void testSaveCurrency() {
        log.debug("testing save...");

        //given
        final Currency currency = new Currency();
        // enter all required fields
        currency.setCurrencyCode("CkUrGbIdIkPtMcUhLwSsRmYlEtBjSuJoOzDcNxFfMmWcThZjLmFzHoBsDqLlOzMgSpJmTeOkLxLtRtPgDvCvRhByAlShVwFnWmKh");

        given(dao.save(currency)).willReturn(currency);

        //when
        manager.save(currency);

        //then
        verify(dao).save(currency);
    }

    @Test
    public void testRemoveCurrency() {
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
