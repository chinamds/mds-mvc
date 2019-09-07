package com.mds.wf.service;

import com.mds.common.exception.RecordExistsException;
import com.mds.common.service.GenericManager;
import com.mds.wf.model.OrganizationWorkflowType;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

//@WebService
public interface OrganizationWorkflowTypeManager extends GenericManager<OrganizationWorkflowType, Long> {
    Response removeOrganizationWorkflowType(String organizationWorkflowTypeIds);
	
	OrganizationWorkflowType saveOrganizationWorkflowType(OrganizationWorkflowType organizationWorkflowType) throws RecordExistsException;
	OrganizationWorkflowType addOrganizationWorkflowType(OrganizationWorkflowType organizationWorkflowType) throws RecordExistsException;
	
	OrganizationWorkflowType userAppointment(String mobile, String idNumber, OrganizationWorkflowType organizationWorkflowType) throws RecordExistsException;
	
	/*Response changeOrganizationWorkflowTypeStatus(String organizationOrganizationWorkflowTypeTypeId, String organizationWorkflowTypeStatus);*/  
}