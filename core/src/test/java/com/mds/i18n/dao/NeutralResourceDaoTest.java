package com.mds.i18n.dao;

import com.mds.common.dao.BaseDaoTestCase;
import com.mds.i18n.model.NeutralResource;
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