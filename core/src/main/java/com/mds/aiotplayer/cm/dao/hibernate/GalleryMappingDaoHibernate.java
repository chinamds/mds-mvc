package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.GalleryMapping;
import com.mds.aiotplayer.cm.dao.GalleryMappingDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("galleryMappingDao")
public class GalleryMappingDaoHibernate extends GenericDaoHibernate<GalleryMapping, Long> implements GalleryMappingDao {

    public GalleryMappingDaoHibernate() {
        super(GalleryMapping.class);
    }
}
