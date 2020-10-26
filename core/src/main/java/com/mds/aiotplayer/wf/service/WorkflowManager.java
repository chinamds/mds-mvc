/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.wf.service;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.core.WorkflowType;
import com.mds.aiotplayer.wf.model.Workflow;
import com.mds.aiotplayer.wf.model.WorkflowDetail;

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