/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.ContentQueue;
import com.mds.aiotplayer.cm.dao.ContentQueueDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
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
        var result = super.save(contentQueue);
        // necessary to throw a DataIntegrityViolation and catch it in ContentQueueManager
        getEntityManager().flush();
        
        return result;
    }
}
