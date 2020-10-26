/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.GalleryControlSetting;
import com.mds.aiotplayer.cm.dao.GalleryControlSettingDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
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
        var result = super.save(galleryControlSetting);
        // necessary to throw a DataIntegrityViolation and catch it in GalleryControlSettingManager
        getEntityManager().flush();
        
        return result;
    }
}
