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
import com.mds.aiotplayer.wf.model.OrganizationWorkflowType;

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