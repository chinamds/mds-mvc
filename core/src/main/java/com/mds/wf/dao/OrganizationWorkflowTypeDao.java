package com.mds.wf.dao;


import com.mds.common.dao.GenericDao;
import com.mds.wf.model.OrganizationWorkflowType;

/**
 * An interface that provides a data management interface to the OrganizationWorkflowType table.
 */
public interface OrganizationWorkflowTypeDao extends GenericDao<OrganizationWorkflowType, Long> {
	/**
     * Saves a organizationWorkflowType's information.
     * @param organizationWorkflowType the object to be saved
     * @return the persisted OrganizationWorkflowType object
     */
    OrganizationWorkflowType saveOrganizationWorkflowType(OrganizationWorkflowType organizationWorkflowType);
    
    /**
     * add a organizationWorkflowType's information.
     * @param organizationWorkflowType the object to be saved
     * @return the persisted OrganizationWorkflowType object
     */
    OrganizationWorkflowType addOrganizationWorkflowType(OrganizationWorkflowType organizationWorkflowType);
    
    /*String getMaxRefNo(final String appointmentItem);
    
    List<Map<String,Object>> getItemRangeAppointment(final List<Long> apptItemRangeIds);*/
}