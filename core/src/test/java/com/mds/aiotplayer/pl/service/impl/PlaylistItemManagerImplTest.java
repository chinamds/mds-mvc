package com.mds.aiotplayer.pl.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.pl.dao.PlaylistItemDao;
import com.mds.aiotplayer.pl.model.PlaylistItem;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class PlaylistItemManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private PlaylistItemManagerImpl manager;

    @Mock
    private PlaylistItemDao dao;

    @Test
    public void testGetPlaylistItem() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final PlaylistItem playlistItem = new PlaylistItem();
        given(dao.get(id)).willReturn(playlistItem);

        //when
        PlaylistItem result = manager.get(id);

        //then
        assertSame(playlistItem, result);
    }

    @Test
    public void testGetPlaylistItems() {
        log.debug("testing getAll...");
        //given
        final List<PlaylistItem> playlistItems = new ArrayList<>();
        given(dao.getAll()).willReturn(playlistItems);

        //when
        List result = manager.getAll();

        //then
        assertSame(playlistItems, result);
    }

    @Test
    public void testSavePlaylistItem() {
        log.debug("testing save...");

        //given
        final PlaylistItem playlistItem = new PlaylistItem();
        // enter all required fields
        playlistItem.setGroupIndex(new Short("11423"));
        playlistItem.setIsGroup(Boolean.FALSE);
        playlistItem.setIsTimeSchedule(Boolean.FALSE);
        playlistItem.setItemIndex(new Byte("66"));
        playlistItem.setItemType(new Short("18860"));
        playlistItem.setPlayTimes(new Short("10038"));

        given(dao.save(playlistItem)).willReturn(playlistItem);

        //when
        manager.save(playlistItem);

        //then
        verify(dao).save(playlistItem);
    }

    @Test
    public void testRemovePlaylistItem() {
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
