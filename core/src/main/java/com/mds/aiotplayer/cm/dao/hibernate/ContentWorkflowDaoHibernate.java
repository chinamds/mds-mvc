/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.ContentWorkflow;
import com.mds.aiotplayer.cm.dao.ContentWorkflowDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("contentWorkflowDao")
public class ContentWorkflowDaoHibernate extends GenericDaoHibernate<ContentWorkflow, Long> implements ContentWorkflowDao {

    public ContentWorkflowDaoHibernate() {
        super(ContentWorkflow.class);
    }

	/**
     * {@inheritDoc}
     */
    public ContentWorkflow saveContentWorkflow(ContentWorkflow contentWorkflow) {
        if (log.isDebugEnabled()) {
            log.debug("contentWorkflow's id: " + contentWorkflow.getId());
        }
        var result = super.save(contentWorkflow);
        // necessary to throw a DataIntegrityViolation and catch it in ContentWorkflowManager
        getEntityManager().flush();
        
        return result;
    }
}
