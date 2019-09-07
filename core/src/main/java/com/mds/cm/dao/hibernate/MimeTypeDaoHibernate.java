package com.mds.cm.dao.hibernate;

import com.mds.cm.model.MimeType;
import com.mds.cm.dao.MimeTypeDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("mimeTypeDao")
public class MimeTypeDaoHibernate extends GenericDaoHibernate<MimeType, Long> implements MimeTypeDao {

    public MimeTypeDaoHibernate() {
        super(MimeType.class);
    }

	/**
     * {@inheritDoc}
     */
    public MimeType saveMimeType(MimeType mimeType) {
        if (log.isDebugEnabled()) {
            log.debug("mimeType's id: " + mimeType.getId());
        }
        getSession().saveOrUpdate(mimeType);
        // necessary to throw a DataIntegrityViolation and catch it in MimeTypeManager
        getSession().flush();
        return mimeType;
    }
}
