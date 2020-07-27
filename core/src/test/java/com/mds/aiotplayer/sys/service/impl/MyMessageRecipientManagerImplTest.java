package com.mds.aiotplayer.sys.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.sys.dao.MyMessageRecipientDao;
import com.mds.aiotplayer.sys.model.MyMessageRecipient;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class MyMessageRecipientManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private MyMessageRecipientManagerImpl manager;

    @Mock
    private MyMessageRecipientDao dao;

    @Test
    public void testGetMyMessageRecipient() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final MyMessageRecipient myMessageRecipient = new MyMessageRecipient();
        given(dao.get(id)).willReturn(myMessageRecipient);

        //when
        MyMessageRecipient result = manager.get(id);

        //then
        assertSame(myMessageRecipient, result);
    }

    @Test
    public void testGetMyMessageRecipients() {
        log.debug("testing getAll...");
        //given
        final List<MyMessageRecipient> myMessageRecipients = new ArrayList<>();
        given(dao.getAll()).willReturn(myMessageRecipients);

        //when
        List result = manager.getAll();

        //then
        assertSame(myMessageRecipients, result);
    }

    @Test
    public void testSaveMyMessageRecipient() {
        log.debug("testing save...");

        //given
        final MyMessageRecipient myMessageRecipient = new MyMessageRecipient();
        // enter all required fields

        given(dao.save(myMessageRecipient)).willReturn(myMessageRecipient);

        //when
        manager.save(myMessageRecipient);

        //then
        verify(dao).save(myMessageRecipient);
    }

    @Test
    public void testRemoveMyMessageRecipient() {
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
