package com.mds.cm.dao;

import com.mds.common.dao.GenericDao;

import com.mds.cm.model.ContentWorkflow;

/**
 * An interface that provides a data management interface to the ContentWorkflow table.
 */
public interface ContentWorkflowDao extends GenericDao<ContentWorkflow, Long> {
	/**
     * Saves a contentWorkflow's information.
     * @param contentWorkflow the object to be saved
     * @return the persisted ContentWorkflow object
     */
    ContentWorkflow saveContentWorkflow(ContentWorkflow contentWorkflow);
}