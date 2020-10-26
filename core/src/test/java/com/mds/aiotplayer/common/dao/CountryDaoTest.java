/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.common.model.Country;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CountryDaoTest extends BaseDaoTestCase {
    @Autowired
    private CountryDao countryDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveCountry() {
        Country country = new Country();

        // enter all required fields
        country.setCountryCode("PoOzNsAgJyUwYhEmHmOcPaIfBsPdEkPfGgArVlHqFlGsCuInJjNfXbPgOtQcQuTwEfYsRhSqXnXhPxFfAcZtFhMyYwVpZlBsIeBt");

        log.debug("adding country...");
        country = countryDao.save(country);

        country = countryDao.get(country.getId());

        assertNotNull(country.getId());

        log.debug("removing country...");

        countryDao.remove(country.getId());

        // should throw DataAccessException 
        countryDao.get(country.getId());
    }
}