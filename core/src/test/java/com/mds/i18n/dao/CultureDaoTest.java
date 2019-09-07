package com.mds.i18n.dao;

import com.mds.common.dao.BaseDaoTestCase;
import com.mds.i18n.model.Culture;
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