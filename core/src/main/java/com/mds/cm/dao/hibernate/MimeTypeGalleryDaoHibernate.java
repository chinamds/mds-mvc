package com.mds.cm.dao.hibernate;

import com.mds.cm.model.MimeTypeGallery;
import com.mds.cm.dao.MimeTypeGalleryDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
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
        getSession().saveOrUpdate(mimeTypeGallery);
        // necessary to throw a DataIntegrityViolation and catch it in MimeTypeGalleryManager
        getSession().flush();
        return mimeTypeGallery;
    }
}
