package com.mds.cm.dao.hibernate;

import com.mds.cm.model.Album;
import com.mds.cm.dao.AlbumDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("albumDao")
public class AlbumDaoHibernate extends GenericDaoHibernate<Album, Long> implements AlbumDao {

    public AlbumDaoHibernate() {
        super(Album.class);
    }

	/**
     * {@inheritDoc}
     */
    public Album saveAlbum(Album album) {
        if (log.isDebugEnabled()) {
            log.debug("album's id: " + album.getId());
        }
        getSession().saveOrUpdate(album);
        // necessary to throw a DataIntegrityViolation and catch it in AlbumManager
        getSession().flush();
        return album;
    }
}
