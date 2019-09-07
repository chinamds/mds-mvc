package com.mds.wf.dao;

import java.util.List;

import com.mds.common.dao.GenericDao;
import com.mds.core.WorkflowType;
import com.mds.wf.model.WorkflowDetail;

/**
 * An interface that provides a data management interface to the WorkflowDetail table.
 */
public interface WorkflowDetailDao extends GenericDao<WorkflowDetail, Long> {
	List<WorkflowDetail> findApprovalWorkflowDetail(WorkflowType workflowType, long applyUserId, long organizationId);
}