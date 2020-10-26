/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.i18n.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.i18n.model.Culture;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CultureDaoTest extends BaseDaoTestCase {
    @Autowired
    private CultureDao cultureDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveCulture() {
        Culture culture = new Culture();

        // enter all required fields

        log.debug("adding culture...");
        culture = cultureDao.save(culture);

        culture = cultureDao.get(culture.getId());

        assertNotNull(culture.getId());

        log.debug("removing culture...");

        cultureDao.remove(culture.getId());

        // should throw DataAccessException 
        cultureDao.get(culture.getId());
    }
}