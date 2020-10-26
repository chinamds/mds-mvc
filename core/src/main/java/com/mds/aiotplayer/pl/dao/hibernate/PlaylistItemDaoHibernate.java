/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pl.dao.hibernate;

import com.mds.aiotplayer.pl.model.PlaylistItem;
import com.mds.aiotplayer.pl.dao.PlaylistItemDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("playlistItemDao")
public class PlaylistItemDaoHibernate extends GenericDaoHibernate<PlaylistItem, Long> implements PlaylistItemDao {

    public PlaylistItemDaoHibernate() {
        super(PlaylistItem.class);
    }
}
