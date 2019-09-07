package com.mds.pl.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.pl.dao.PlaylistDao;
import com.mds.pl.model.Playlist;
import com.mds.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class PlaylistManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private PlaylistManagerImpl manager;

    @Mock
    private PlaylistDao dao;

    @Test
    public void testGetPlaylist() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Playlist playlist = new Playlist();
        given(dao.get(id)).willReturn(playlist);

        //when
        Playlist result = manager.get(id);

        //then
        assertSame(playlist, result);
    }

    @Test
    public void testGetPlaylists() {
        log.debug("testing getAll...");
        //given
        final List<Playlist> playlists = new ArrayList<>();
        given(dao.getAll()).willReturn(playlists);

        //when
        List result = manager.getAll();

        //then
        assertSame(playlists, result);
    }

    @Test
    public void testSavePlaylist() {
        log.debug("testing save...");

        //given
        final Playlist playlist = new Playlist();
        // enter all required fields
        playlist.setApprovalLevel(new Short("19560"));
        playlist.setApprovalStatus(new Short("22672"));
        playlist.setAutoPlay(Boolean.FALSE);
        playlist.setEnd(new java.util.Date());
        playlist.setGroupLoop(new Short("5485"));
        playlist.setGroupNumber(new Short("23429"));
        playlist.setIsTimeSchedule(Boolean.FALSE);
        playlist.setScheduleDesc("XbZyLbVbVzTsCpIvYtTtLjGdWsCfDwJkVhEdWeLsMwWlJtLiFwNpAmDjJpQnStXyQmEsSwQeFdMnFuZxPzFzSbFwKkUaCpFyZcRiQrEfLoItRqUtRwVyEfFoXyPzBgQnWfGdExNkFhFnNnEtIqCdRyIqMdJwPaZsKvUeWoWkRjUyAhIaYbDkHsGnSjLuZpHcXhXbOhIvPbUyUcQqGdWoMzTfAiUpMiWtCfYvXvTbIvLxYcMpDcEmSiJtPwFqYtOg");
        playlist.setScheduleName("TmUgKjKnUnXdOvUeAlYiJiLiTgHmYfJdLjMzGmDcWlJgDhTkVk");
        playlist.setStart(new java.util.Date());
        playlist.setStopAndQuit(Boolean.FALSE);

        given(dao.save(playlist)).willReturn(playlist);

        //when
        manager.save(playlist);

        //then
        verify(dao).save(playlist);
    }

    @Test
    public void testRemovePlaylist() {
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
