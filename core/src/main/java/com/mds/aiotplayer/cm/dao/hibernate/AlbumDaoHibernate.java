package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.Album;
import com.mds.aiotplayer.cm.dao.AlbumDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
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
        Album a = super.save(album);
        // necessary to throw a DataIntegrityViolation and catch it in AlbumManager
        getEntityManager().flush();
        
        return a;
    }
}
