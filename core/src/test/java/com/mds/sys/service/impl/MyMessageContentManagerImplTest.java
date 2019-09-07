package com.mds.sys.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.sys.dao.MyMessageContentDao;
import com.mds.sys.model.MyMessageContent;
import com.mds.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class MyMessageContentManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private MyMessageContentManagerImpl manager;

    @Mock
    private MyMessageContentDao dao;

    @Test
    public void testGetMyMessageContent() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final MyMessageContent myMessageContent = new MyMessageContent();
        given(dao.get(id)).willReturn(myMessageContent);

        //when
        MyMessageContent result = manager.get(id);

        //then
        assertSame(myMessageContent, result);
    }

    @Test
    public void testGetMyMessageContents() {
        log.debug("testing getAll...");
        //given
        final List<MyMessageContent> myMessageContents = new ArrayList<>();
        given(dao.getAll()).willReturn(myMessageContents);

        //when
        List result = manager.getAll();

        //then
        assertSame(myMessageContents, result);
    }

    @Test
    public void testSaveMyMessageContent() {
        log.debug("testing save...");

        //given
        final MyMessageContent myMessageContent = new MyMessageContent();
        // enter all required fields

        given(dao.save(myMessageContent)).willReturn(myMessageContent);

        //when
        manager.save(myMessageContent);

        //then
        verify(dao).save(myMessageContent);
    }

    @Test
    public void testRemoveMyMessageContent() {
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
