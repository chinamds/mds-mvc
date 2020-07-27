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
