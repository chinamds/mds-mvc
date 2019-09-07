package com.mds.sys.dao;

import com.mds.common.dao.BaseDaoTestCase;
import com.mds.sys.model.MyMessage;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MyMessageDaoTest extends BaseDaoTestCase {
    @Autowired
    private MyMessageDao myMessageDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveMyMessage() {
        MyMessage myMessage = new MyMessage();

        // enter all required fields
        myMessage.setPriority(1884762188);

        log.debug("adding myMessage...");
        myMessage = myMessageDao.save(myMessage);

        myMessage = myMessageDao.get(myMessage.getId());

        assertNotNull(myMessage.getId());

        log.debug("removing myMessage...");

        myMessageDao.remove(myMessage.getId());

        // should throw DataAccessException 
        myMessageDao.get(myMessage.getId());
    }
}