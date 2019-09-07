package com.mds.hrm.dao;

import com.mds.common.dao.BaseDaoTestCase;
import com.mds.hrm.model.IdentityType;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class IdentityTypeDaoTest extends BaseDaoTestCase {
    @Autowired
    private IdentityTypeDao identityTypeDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveIdentityType() {
        IdentityType identityType = new IdentityType();

        // enter all required fields
        identityType.setIdentityTypeCode("AkEvUpEmXpRjVzMfMxGbRmAtBaJtKmAtBfZiOwGwFbYdPyMfSrCtWjYfTsSnCzJqUzBoWbAqMnNpOqWgHsEhKsSzBdUvImFvYjTq");

        log.debug("adding identityType...");
        identityType = identityTypeDao.save(identityType);

        identityType = identityTypeDao.get(identityType.getId());

        assertNotNull(identityType.getId());

        log.debug("removing identityType...");

        identityTypeDao.remove(identityType.getId());

        // should throw DataAccessException 
        identityTypeDao.get(identityType.getId());
    }
}