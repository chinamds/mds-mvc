/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.sys.dao.MyMessageDao;
import com.mds.aiotplayer.sys.model.MyMessage;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class MyMessageManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private MyMessageManagerImpl manager;

    @Mock
    private MyMessageDao dao;

    @Test
    public void testGetMyMessage() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final MyMessage myMessage = new MyMessage();
        given(dao.get(id)).willReturn(myMessage);

        //when
        MyMessage result = manager.get(id);

        //then
        assertSame(myMessage, result);
    }

    @Test
    public void testGetMyMessages() {
        log.debug("testing getAll...");
        //given
        final List<MyMessage> myMessages = new ArrayList<>();
        given(dao.getAll()).willReturn(myMessages);

        //when
        List result = manager.getAll();

        //then
        assertSame(myMessages, result);
    }

    @Test
    public void testSaveMyMessage() {
        log.debug("testing save...");

        //given
        final MyMessage myMessage = new MyMessage();
        // enter all required fields
        myMessage.setPriority(930581532);

        given(dao.save(myMessage)).willReturn(myMessage);

        //when
        manager.save(myMessage);

        //then
        verify(dao).save(myMessage);
    }

    @Test
    public void testRemoveMyMessage() {
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
