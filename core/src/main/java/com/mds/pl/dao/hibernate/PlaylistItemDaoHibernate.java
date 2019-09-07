package com.mds.pl.dao.hibernate;

import com.mds.pl.model.PlaylistItem;
import com.mds.pl.dao.PlaylistItemDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("playlistItemDao")
public class PlaylistItemDaoHibernate extends GenericDaoHibernate<PlaylistItem, Long> implements PlaylistItemDao {

    public PlaylistItemDaoHibernate() {
        super(PlaylistItem.class);
    }
}
