/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.cm.model.ContentWorkflow;

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