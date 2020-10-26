/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.ps.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.ps.dao.ChannelDao;
import com.mds.aiotplayer.ps.model.Channel;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class ChannelManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private ChannelManagerImpl manager;

    @Mock
    private ChannelDao dao;

    @Test
    public void testGetChannel() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Channel channel = new Channel();
        given(dao.get(id)).willReturn(channel);

        //when
        Channel result = manager.get(id);

        //then
        assertSame(channel, result);
    }

    @Test
    public void testGetChannels() {
        log.debug("testing getAll...");
        //given
        final List<Channel> channels = new ArrayList<>();
        given(dao.getAll()).willReturn(channels);

        //when
        List result = manager.getAll();

        //then
        assertSame(channels, result);
    }

    @Test
    public void testSaveChannel() {
        log.debug("testing save...");

        //given
        final Channel channel = new Channel();
        // enter all required fields
        channel.setBAllContent(new Byte("21"));
        channel.setBImm(new Byte("50"));
        channel.setBIncludeToday(new Byte("34"));
        channel.setChannelDesc("AbKjBoWuXzAtExMlEiXpAnUiWmCwMnBnHbUmYtNsIbZbAmLwZqBqNaKrKnXuWjMdFuFbDqWaHyHgAaCuRhGrAtXgAkNeAfJdCdDy");
        channel.setChannelName("PwEdShBzLhQjQyTjAeGgWkNbCgMaCrHyMePrYgHdDuRtYzGnYt");
        channel.setPeriod(new Short("1725"));

        given(dao.save(channel)).willReturn(channel);

        //when
        manager.save(channel);

        //then
        verify(dao).save(channel);
    }

    @Test
    public void testRemoveChannel() {
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
