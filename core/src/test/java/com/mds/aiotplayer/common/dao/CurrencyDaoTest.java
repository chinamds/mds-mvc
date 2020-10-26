/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.common.model.Currency;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CurrencyDaoTest extends BaseDaoTestCase {
    @Autowired
    private CurrencyDao currencyDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveCurrency() {
        Currency currency = new Currency();

        // enter all required fields
        currency.setCurrencyCode("PdCfAbGqKfGaGaOeIwGzWiQkHhWsPtDfQuBaDxGoWqRjGvXuNyNvImRyEvGyFfSwUuMbVpNoEuAbSpQnNnEzMjXfBzLmLpCxTiBg");

        log.debug("adding currency...");
        currency = currencyDao.save(currency);

        currency = currencyDao.get(currency.getId());

        assertNotNull(currency.getId());

        log.debug("removing currency...");

        currencyDao.remove(currency.getId());

        // should throw DataAccessException 
        currencyDao.get(currency.getId());
    }
}