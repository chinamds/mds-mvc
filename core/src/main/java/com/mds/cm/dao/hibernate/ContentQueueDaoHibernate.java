package com.mds.cm.dao.hibernate;

import com.mds.cm.model.ContentQueue;
import com.mds.cm.dao.ContentQueueDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("contentQueueDao")
public class ContentQueueDaoHibernate extends GenericDaoHibernate<ContentQueue, Long> implements ContentQueueDao {

    public ContentQueueDaoHibernate() {
        super(ContentQueue.class);
    }

	/**
     * {@inheritDoc}
     */
    public ContentQueue saveContentQueue(ContentQueue contentQueue) {
        if (log.isDebugEnabled()) {
            log.debug("contentQueue's id: " + contentQueue.getId());
        }
        getSession().saveOrUpdate(contentQueue);
        // necessary to throw a DataIntegrityViolation and catch it in ContentQueueManager
        getSession().flush();
        return contentQueue;
    }
}
