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