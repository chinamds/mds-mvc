package com.mds.cm.dao.hibernate;

import com.mds.cm.model.ContentObject;
import com.mds.cm.dao.ContentObjectDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("contentObjectDao")
public class ContentObjectDaoHibernate extends GenericDaoHibernate<ContentObject, Long> implements ContentObjectDao {

    public ContentObjectDaoHibernate() {
        super(ContentObject.class);
    }

	/**
     * {@inheritDoc}
     */
    public ContentObject saveContentObject(ContentObject contentObject) {
        if (log.isDebugEnabled()) {
            log.debug("contentObject's id: " + contentObject.getId());
        }
        getSession().saveOrUpdate(contentObject);
        // necessary to throw a DataIntegrityViolation and catch it in ContentObjectManager
        getSession().flush();
        return contentObject;
    }
}
