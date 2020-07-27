package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.sys.model.OrganizationLogo;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class OrganizationLogoDaoTest extends BaseDaoTestCase {
    @Autowired
    private OrganizationLogoDao organizationLogoDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveOrganizationLogo() {
        OrganizationLogo organizationLogo = new OrganizationLogo();

        // enter all required fields

        log.debug("adding organizationLogo...");
        organizationLogo = organizationLogoDao.save(organizationLogo);

        organizationLogo = organizationLogoDao.get(organizationLogo.getId());

        assertNotNull(organizationLogo.getId());

        log.debug("removing organizationLogo...");

        organizationLogoDao.remove(organizationLogo.getId());

        // should throw DataAccessException 
        organizationLogoDao.get(organizationLogo.getId());
    }
}