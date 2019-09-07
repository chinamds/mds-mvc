package com.mds.pl.dao;

import com.mds.common.dao.BaseDaoTestCase;
import com.mds.pl.model.Playlist;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PlaylistDaoTest extends BaseDaoTestCase {
    @Autowired
    private PlaylistDao playlistDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemovePlaylist() {
        Playlist playlist = new Playlist();

        // enter all required fields
        playlist.setApprovalLevel(new Short("27146"));
        playlist.setApprovalStatus(new Short("27864"));
        playlist.setAutoPlay(Boolean.FALSE);
        playlist.setEnd(new java.util.Date());
        playlist.setGroupLoop(new Short("21943"));
        playlist.setGroupNumber(new Short("24444"));
        playlist.setIsTimeSchedule(Boolean.FALSE);
        playlist.setScheduleDesc("LiCcIxGqWyVcRrYbVgZtBxZdFbErWhWeGcIgZmHuWcKyJeAqJsOiFvRgJeEdRhIcCuMzMbIyNlNaUmCeDeFhLsSaEdLcIxKoHgOsSyGoUfPrGtAhDtExPiOjTtXoIeHlKtRnBrXfXpGjWlCvIhBvVvApRwXiDdWhFxHjVhLeGuHcKrAuDtGhZvWlVlEqDzWjSyQtIhEyKlMgHiHnEhJbDuPiWbPwOlCyQeDtTmIiSaVhRsOfHaAkIqNxVnVrXqFz");
        playlist.setScheduleName("YtNuAkIhAnAdDlKnSxUgNrYyUsUhPxLzWqXkWqDtMwCkEkIsRq");
        playlist.setStart(new java.util.Date());
        playlist.setStopAndQuit(Boolean.FALSE);

        log.debug("adding playlist...");
        playlist = playlistDao.save(playlist);

        playlist = playlistDao.get(playlist.getId());

        assertNotNull(playlist.getId());

        log.debug("removing playlist...");

        playlistDao.remove(playlist.getId());

        // should throw DataAccessException 
        playlistDao.get(playlist.getId());
    }
}