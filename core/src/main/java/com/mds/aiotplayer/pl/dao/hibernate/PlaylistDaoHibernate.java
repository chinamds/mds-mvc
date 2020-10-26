/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pl.dao.hibernate;

import com.mds.aiotplayer.pl.model.Playlist;
import com.mds.aiotplayer.pl.dao.PlaylistDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("playlistDao")
public class PlaylistDaoHibernate extends GenericDaoHibernate<Playlist, Long> implements PlaylistDao {

    public PlaylistDaoHibernate() {
        super(Playlist.class);
    }
}
