/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.common.model.ZipCodeType;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ZipCodeTypeDaoTest extends BaseDaoTestCase {
    @Autowired
    private ZipCodeTypeDao zipCodeTypeDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveZipCodeType() {
        ZipCodeType zipCodeType = new ZipCodeType();

        // enter all required fields
        zipCodeType.setType("RrBjSyVfZaOwIsBxCuRpFbCgApWiQiJjBpLzTwPeOgYkKuHrCsTpBjYqYdRnRgCqNdQxIhSeOxEgJuTaQlBmOrRoKmWrFuZnHtNm");

        log.debug("adding zipCodeType...");
        zipCodeType = zipCodeTypeDao.save(zipCodeType);

        zipCodeType = zipCodeTypeDao.get(zipCodeType.getId());

        assertNotNull(zipCodeType.getId());

        log.debug("removing zipCodeType...");

        zipCodeTypeDao.remove(zipCodeType.getId());

        // should throw DataAccessException 
        zipCodeTypeDao.get(zipCodeType.getId());
    }
}