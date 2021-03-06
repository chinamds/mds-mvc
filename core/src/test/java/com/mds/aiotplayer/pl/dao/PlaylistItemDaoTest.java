/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pl.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.pl.model.PlaylistItem;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PlaylistItemDaoTest extends BaseDaoTestCase {
    @Autowired
    private PlaylistItemDao playlistItemDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemovePlaylistItem() {
        PlaylistItem playlistItem = new PlaylistItem();

        // enter all required fields
        playlistItem.setGroupIndex(new Short("6677"));
        playlistItem.setIsGroup(Boolean.FALSE);
        playlistItem.setIsTimeSchedule(Boolean.FALSE);
        playlistItem.setItemIndex(new Byte("86"));
        playlistItem.setItemType(new Short("1831"));
        playlistItem.setPlayTimes(new Short("8860"));

        log.debug("adding playlistItem...");
        playlistItem = playlistItemDao.save(playlistItem);

        playlistItem = playlistItemDao.get(playlistItem.getId());

        assertNotNull(playlistItem.getId());

        log.debug("removing playlistItem...");

        playlistItemDao.remove(playlistItem.getId());

        // should throw DataAccessException 
        playlistItemDao.get(playlistItem.getId());
    }
}