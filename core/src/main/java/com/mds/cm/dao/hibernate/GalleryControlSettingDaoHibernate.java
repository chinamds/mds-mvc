package com.mds.cm.dao.hibernate;

import com.mds.cm.model.GalleryControlSetting;
import com.mds.cm.dao.GalleryControlSettingDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("galleryControlSettingDao")
public class GalleryControlSettingDaoHibernate extends GenericDaoHibernate<GalleryControlSetting, Long> implements GalleryControlSettingDao {

    public GalleryControlSettingDaoHibernate() {
        super(GalleryControlSetting.class);
    }

	/**
     * {@inheritDoc}
     */
    public GalleryControlSetting saveGalleryControlSetting(GalleryControlSetting galleryControlSetting) {
        if (log.isDebugEnabled()) {
            log.debug("galleryControlSetting's id: " + galleryControlSetting.getId());
        }
        getSession().saveOrUpdate(galleryControlSetting);
        // necessary to throw a DataIntegrityViolation and catch it in GalleryControlSettingManager
        getSession().flush();
        return galleryControlSetting;
    }
}
