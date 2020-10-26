/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.i18n.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.i18n.model.LocalizedResource;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class LocalizedResourceDaoTest extends BaseDaoTestCase {
    @Autowired
    private LocalizedResourceDao localizedResourceDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveLocalizedResource() {
        LocalizedResource localizedResource = new LocalizedResource();

        // enter all required fields

        log.debug("adding localizedResource...");
        localizedResource = localizedResourceDao.save(localizedResource);

        localizedResource = localizedResourceDao.get(localizedResource.getId());

        assertNotNull(localizedResource.getId());

        log.debug("removing localizedResource...");

        localizedResourceDao.remove(localizedResource.getId());

        // should throw DataAccessException 
        localizedResourceDao.get(localizedResource.getId());
    }
}