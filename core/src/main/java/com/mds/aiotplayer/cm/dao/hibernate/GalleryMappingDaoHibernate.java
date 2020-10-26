/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
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
