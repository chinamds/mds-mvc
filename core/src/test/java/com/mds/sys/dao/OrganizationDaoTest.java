package com.mds.sys.dao;

import com.mds.common.dao.BaseDaoTestCase;
import com.mds.sys.model.Organization;
import com.mds.sys.util.UserUtils;

import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class OrganizationDaoTest extends BaseDaoTestCase {
    @Autowired
    private OrganizationDao organizationDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveOrganization() {
        Organization organization = new Organization();

        // enter all required fields
        //organization.setTenant(UserUtils.getTenant("212aab68-7fb3-11e9-bc42-526af7764f64"));

        log.debug("adding organization...");
        organization = organizationDao.save(organization);

        organization = organizationDao.get(organization.getId());

        assertNotNull(organization.getId());

        log.debug("removing organization...");

        organizationDao.remove(organization.getId());

        // should throw DataAccessException 
        organizationDao.get(organization.getId());
    }
}