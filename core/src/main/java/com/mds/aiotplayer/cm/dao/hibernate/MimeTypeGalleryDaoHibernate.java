/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.MimeTypeGallery;
import com.mds.aiotplayer.cm.dao.MimeTypeGalleryDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("mimeTypeGalleryDao")
public class MimeTypeGalleryDaoHibernate extends GenericDaoHibernate<MimeTypeGallery, Long> implements MimeTypeGalleryDao {

    public MimeTypeGalleryDaoHibernate() {
        super(MimeTypeGallery.class);
    }

	/**
     * {@inheritDoc}
     */
    public MimeTypeGallery saveMimeTypeGallery(MimeTypeGallery mimeTypeGallery) {
        if (log.isDebugEnabled()) {
            log.debug("mimeTypeGallery's id: " + mimeTypeGallery.getId());
        }
        var result = super.save(mimeTypeGallery);
        // necessary to throw a DataIntegrityViolation and catch it in MimeTypeGalleryManager
        getEntityManager().flush();
        return result;
    }
}
