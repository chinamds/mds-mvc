/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.test;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-1-14 下午4:25
 * <p>Version: 1.0
 */
@ContextConfiguration({"classpath:spring-common.xml", "classpath:spring-test.xml"})
//@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public abstract class BaseIT extends AbstractTransactionalJUnit4SpringContextTests {

    @PersistenceContext
    protected EntityManager entityManager;


    public void clear() {
        flush();
        entityManager.clear();
    }

    public void flush() {
        entityManager.flush();
    }


    protected String nextRandom() {
        return System.currentTimeMillis() + RandomStringUtils.randomNumeric(5);
    }

}
