package com.mds.aiotplayer.sys.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.sys.dao.LogDao;
import com.mds.aiotplayer.sys.model.Log;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class LogManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private LogManagerImpl manager;

    @Mock
    private LogDao dao;

    @Test
    public void testGetLog() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Log log = new Log();
        given(dao.get(id)).willReturn(log);

        //when
        Log result = manager.get(id);

        //then
        assertSame(log, result);
    }

    @Test
    public void testGetLogs() {
        log.debug("testing getAll...");
        //given
        final List<Log> logs = new ArrayList<>();
        given(dao.getAll()).willReturn(logs);

        //when
        List result = manager.getAll();

        //then
        assertSame(logs, result);
    }

    @Test
    public void testSaveLog() {
        log.debug("testing save...");

        //given
        final Log log = new Log();
        // enter all required fields

        given(dao.save(log)).willReturn(log);

        //when
        manager.save(log);

        //then
        verify(dao).save(log);
    }

    @Test
    public void testRemoveLog() {
        log.debug("testing remove...");

        //given
        final Long id = -11L;
        willDoNothing().given(dao).remove(id);

        //when
        manager.remove(id);

        //then
        verify(dao).remove(id);
    }
}
