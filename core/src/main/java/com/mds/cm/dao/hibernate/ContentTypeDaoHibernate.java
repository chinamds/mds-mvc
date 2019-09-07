package com.mds.cm.dao.hibernate;

import com.mds.cm.model.ContentType;
import com.mds.cm.dao.ContentTypeDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("contentTypeDao")
public class ContentTypeDaoHibernate extends GenericDaoHibernate<ContentType, Long> implements ContentTypeDao {

    public ContentTypeDaoHibernate() {
        super(ContentType.class);
    }

	/**
     * {@inheritDoc}
     */
    public ContentType saveContentType(ContentType contentType) {
        if (log.isDebugEnabled()) {
            log.debug("contentType's id: " + contentType.getId());
        }
        getSession().saveOrUpdate(contentType);
        // necessary to throw a DataIntegrityViolation and catch it in ContentTypeManager
        getSession().flush();
        return contentType;
    }
}
