/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.GallerySetting;
import com.mds.aiotplayer.cm.dao.GallerySettingDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
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
        var result =  super.save(gallerySetting);
        // necessary to throw a DataIntegrityViolation and catch it in GallerySettingManager
        getEntityManager().flush();
        
        return result;
    }
}
