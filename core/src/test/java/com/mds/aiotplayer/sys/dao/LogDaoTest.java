package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.sys.model.Log;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class LogDaoTest extends BaseDaoTestCase {
    @Autowired
    private LogDao logDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveLog() {
        Log syslog = new Log();

        // enter all required fields

        log.debug("adding log...");
        syslog = logDao.save(syslog);

        syslog = logDao.get(syslog.getId());

        assertNotNull(syslog.getId());

        log.debug("removing log...");

        logDao.remove(syslog.getId());

        // should throw DataAccessException 
        logDao.get(syslog.getId());
    }
}