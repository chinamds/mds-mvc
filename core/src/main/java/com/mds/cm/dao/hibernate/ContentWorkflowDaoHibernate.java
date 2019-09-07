package com.mds.cm.dao.hibernate;

import com.mds.cm.model.ContentWorkflow;
import com.mds.cm.dao.ContentWorkflowDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
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
        getSession().saveOrUpdate(contentWorkflow);
        // necessary to throw a DataIntegrityViolation and catch it in ContentWorkflowManager
        getSession().flush();
        return contentWorkflow;
    }
}
