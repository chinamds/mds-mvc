/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.ContentActivity;
import com.mds.aiotplayer.cm.dao.ContentActivityDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
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
        ContentActivity c = super.save(contentActivity);
        // necessary to throw a DataIntegrityViolation and catch it in ContentActivityManager
        getEntityManager().flush();
        
        return c;
    }
}
