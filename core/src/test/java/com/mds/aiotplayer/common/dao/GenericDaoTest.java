/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import com.mds.aiotplayer.sys.model.User;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class GenericDaoTest extends BaseDaoTestCase {
    Logger log = LoggerFactory.getLogger(GenericDaoTest.class);
    GenericDao<User, Long> genericDao;
    @PersistenceContext(unitName=GenericDaoHibernate.PERSISTENCE_UNIT_NAME)
    private EntityManager entityManager;


    @Before
    public void setUp() {
        genericDao = new GenericDaoHibernate<>(User.class, entityManager);
    }

    @Test
    public void getUser() {
        User user = genericDao.get(-1L);
        assertNotNull(user);
        assertEquals("user", user.getUsername());
    }
}
