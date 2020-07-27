package com.mds.aiotplayer.common.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.common.model.ZipCode;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ZipCodeDaoTest extends BaseDaoTestCase {
    @Autowired
    private ZipCodeDao zipCodeDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveZipCode() {
        ZipCode zipCode = new ZipCode();

        // enter all required fields
        zipCode.setCode("HzIqOlTqUbTgUbJvYpHkLbZjTsVzDsEnFoRjVpLhEpNhSqKrBsFmMcUsWoOjTnWpStNqBbGnExYmWxQfWvSmVeNxFnEyXlXsJyIs");

        log.debug("adding zipCode...");
        zipCode = zipCodeDao.save(zipCode);

        zipCode = zipCodeDao.get(zipCode.getId());

        assertNotNull(zipCode.getId());

        log.debug("removing zipCode...");

        zipCodeDao.remove(zipCode.getId());

        // should throw DataAccessException 
        zipCodeDao.get(zipCode.getId());
    }
}