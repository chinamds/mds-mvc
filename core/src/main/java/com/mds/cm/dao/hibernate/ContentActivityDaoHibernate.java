package com.mds.cm.dao.hibernate;

import com.mds.cm.model.ContentActivity;
import com.mds.cm.dao.ContentActivityDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("contentActivityDao")
public class ContentActivityDaoHibernate extends GenericDaoHibernate<ContentActivity, Long> implements ContentActivityDao {

    public ContentActivityDaoHibernate() {
        super(ContentActivity.class);
    }

	/**
     * {@inheritDoc}
     */
    public ContentActivity saveContentActivity(ContentActivity contentActivity) {
        if (log.isDebugEnabled()) {
            log.debug("contentActivity's id: " + contentActivity.getId());
        }
        getSession().saveOrUpdate(contentActivity);
        // necessary to throw a DataIntegrityViolation and catch it in ContentActivityManager
        getSession().flush();
        return contentActivity;
    }
}
