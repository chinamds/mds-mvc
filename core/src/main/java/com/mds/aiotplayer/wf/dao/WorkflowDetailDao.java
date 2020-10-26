/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.wf.dao;

import java.util.List;

import com.mds.aiotplayer.common.dao.GenericDao;
import com.mds.aiotplayer.core.WorkflowType;
import com.mds.aiotplayer.wf.model.WorkflowDetail;

/**
 * An interface that provides a data management interface to the WorkflowDetail table.
 */
public interface WorkflowDetailDao extends GenericDao<WorkflowDetail, Long> {
	List<WorkflowDetail> findApprovalWorkflowDetail(WorkflowType workflowType, long applyUserId, long organizationId);
}