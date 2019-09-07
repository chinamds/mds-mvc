package com.mds.cm.dao.hibernate;

import com.mds.cm.model.GallerySetting;
import com.mds.cm.dao.GallerySettingDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("gallerySettingDao")
public class GallerySettingDaoHibernate extends GenericDaoHibernate<GallerySetting, Long> implements GallerySettingDao {

    public GallerySettingDaoHibernate() {
        super(GallerySetting.class);
    }

	/**
     * {@inheritDoc}
     */
    public GallerySetting saveGallerySetting(GallerySetting gallerySetting) {
        if (log.isDebugEnabled()) {
            log.debug("gallerySetting's id: " + gallerySetting.getId());
        }
        getSession().saveOrUpdate(gallerySetting);
        // necessary to throw a DataIntegrityViolation and catch it in GallerySettingManager
        getSession().flush();
        return gallerySetting;
    }
}
