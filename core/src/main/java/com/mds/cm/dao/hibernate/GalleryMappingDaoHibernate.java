package com.mds.cm.dao.hibernate;

import com.mds.cm.model.GalleryMapping;
import com.mds.cm.dao.GalleryMappingDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("galleryMappingDao")
public class GalleryMappingDaoHibernate extends GenericDaoHibernate<GalleryMapping, Long> implements GalleryMappingDao {

    public GalleryMappingDaoHibernate() {
        super(GalleryMapping.class);
    }
}
