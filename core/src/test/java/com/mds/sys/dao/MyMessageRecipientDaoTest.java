package com.mds.sys.dao;

import com.mds.common.dao.BaseDaoTestCase;
import com.mds.sys.model.MyMessageRecipient;
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