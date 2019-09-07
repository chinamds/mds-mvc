package com.mds.wf.dao;


import java.util.List;

import com.mds.common.dao.GenericDao;
import com.mds.core.WorkflowType;
import com.mds.wf.model.Workflow;

/**
 * An interface that provides a data management interface to the Workflow table.
 */
public interface WorkflowDao extends GenericDao<Workflow, Long> {
	/**
     * Saves a workflow's information.
     * @param workflow the object to be saved
     * @return the persisted Workflow object
     */
    Workflow saveWorkflow(Workflow workflow);
    
    /**
     * add a workflow's information.
     * @param workflow the object to be saved
     * @return the persisted Workflow object
     */
    Workflow addWorkflow(Workflow workflow);
    
    /*String getMaxRefNo(final String appointmentItem);
    
    List<Map<String,Object>> getItemRangeAppointment(final List<Long> apptItemRangeIds);*/
    Workflow findApplyWorkflow(WorkflowType workflowType, long applyUserId, long organizationId);
    
    List<Workflow> findApprovalWorkflows(WorkflowType workflowType, long applyUserId, long organizationId, boolean bFectDetails);
}