package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.sys.model.MyMessageContent;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MyMessageContentDaoTest extends BaseDaoTestCase {
    @Autowired
    private MyMessageContentDao myMessageContentDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveMyMessageContent() {
        MyMessageContent myMessageContent = new MyMessageContent();

        // enter all required fields

        log.debug("adding myMessageContent...");
        myMessageContent = myMessageContentDao.save(myMessageContent);

        myMessageContent = myMessageContentDao.get(myMessageContent.getId());

        assertNotNull(myMessageContent.getId());

        log.debug("removing myMessageContent...");

        myMessageContentDao.remove(myMessageContent.getId());

        // should throw DataAccessException 
        myMessageContentDao.get(myMessageContent.getId());
    }
}