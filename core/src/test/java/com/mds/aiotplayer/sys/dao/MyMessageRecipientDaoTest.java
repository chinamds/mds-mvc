/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.sys.model.MyMessageRecipient;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MyMessageRecipientDaoTest extends BaseDaoTestCase {
    @Autowired
    private MyMessageRecipientDao myMessageRecipientDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveMyMessageRecipient() {
        MyMessageRecipient myMessageRecipient = new MyMessageRecipient();

        // enter all required fields

        log.debug("adding myMessageRecipient...");
        myMessageRecipient = myMessageRecipientDao.save(myMessageRecipient);

        myMessageRecipient = myMessageRecipientDao.get(myMessageRecipient.getId());

        assertNotNull(myMessageRecipient.getId());

        log.debug("removing myMessageRecipient...");

        myMessageRecipientDao.remove(myMessageRecipient.getId());

        // should throw DataAccessException 
        myMessageRecipientDao.get(myMessageRecipient.getId());
    }
}