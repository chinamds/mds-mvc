/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.i18n.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.i18n.model.NeutralResource;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class NeutralResourceDaoTest extends BaseDaoTestCase {
    @Autowired
    private NeutralResourceDao neutralResourceDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveNeutralResource() {
        NeutralResource neutralResource = new NeutralResource();

        // enter all required fields

        log.debug("adding neutralResource...");
        neutralResource = neutralResourceDao.save(neutralResource);

        neutralResource = neutralResourceDao.get(neutralResource.getId());

        assertNotNull(neutralResource.getId());

        log.debug("removing neutralResource...");

        neutralResourceDao.remove(neutralResource.getId());

        // should throw DataAccessException 
        neutralResourceDao.get(neutralResource.getId());
    }
}