package com.mds.pl.dao.hibernate;

import com.mds.pl.model.Playlist;
import com.mds.pl.dao.PlaylistDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("playlistDao")
public class PlaylistDaoHibernate extends GenericDaoHibernate<Playlist, Long> implements PlaylistDao {

    public PlaylistDaoHibernate() {
        super(Playlist.class);
    }
}
