package com.mds.wf.service;

import com.mds.common.exception.RecordExistsException;
import com.mds.common.service.GenericManager;
import com.mds.core.WorkflowType;
import com.mds.wf.model.Workflow;
import com.mds.wf.model.WorkflowDetail;

import java.util.HashMap;
import java.util.List;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

//@WebService
public interface WorkflowManager extends GenericManager<Workflow, Long> {
    Response removeWorkflow(String workflowIds);
	
	Workflow saveWorkflow(Workflow workflow) throws RecordExistsException;
	Workflow addWorkflow(Workflow workflow) throws RecordExistsException;
	
	Workflow userAppointment(String mobile, String idNumber, Workflow workflow) throws RecordExistsException;
	
	/*Response changeWorkflowStatus(String workflowId, String workflowStatus);*/
	HashMap<String,Object> toAppendGridData(List<WorkflowDetail> workflowDetails, HttpServletRequest request);
	
	Workflow getApplyWorkflow(WorkflowType workflowType, String userName);
}