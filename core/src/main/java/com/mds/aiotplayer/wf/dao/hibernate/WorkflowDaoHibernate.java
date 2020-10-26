/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.wf.dao.hibernate;

import com.mds.aiotplayer.wf.model.Workflow;
import com.mds.aiotplayer.wf.dao.WorkflowDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import com.mds.aiotplayer.common.model.Parameter;
import com.mds.aiotplayer.core.WorkflowType;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository("workflowDao")
public class WorkflowDaoHibernate extends GenericDaoHibernate<Workflow, Long> implements WorkflowDao {

    public WorkflowDaoHibernate() {
        super(Workflow.class);
    }

	/**
     * {@inheritDoc}
     */
    public Workflow saveWorkflow(Workflow workflow) {
        if (log.isDebugEnabled()) {
            log.debug("workflow's id: " + workflow.getId());
        }
        var result = super.save(preSave(workflow));
        // necessary to throw a DataIntegrityViolation and catch it in WorkflowManager
        getEntityManager().flush();
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    public Workflow addWorkflow(Workflow workflow) {
    	var result = super.save(workflow);
        // necessary to throw a DataIntegrityViolation and catch it in WorkflowManager
        getEntityManager().flush();
        return result;
    }
    
    /**
     * Overridden simply to call the saveWorkflow method. This is happening
     * because saveWorkflow flushes the session and saveObject of BaseDaoHibernate
     * does not.
     *
     * @param workflow the workflow to save
     * @return the modified workflow (with a primary key set if they're new)
     */
    @Override
    public Workflow save(Workflow workflow) {
        return this.saveWorkflow(workflow);
    } 
    
    public Workflow findApplyWorkflow(WorkflowType workflowType, long applyUserId, long organizationId) {
    	List<Workflow> workflows= find("select distinct wd.workflow from WorkflowDetail wd where wd.apply=true and exists (from wd.activity.activityOrganizationUsers aou where aou.user.id=:p1)" +
				" and wd.workflow.workflowType.workflowType=:p2", new Parameter(applyUserId, workflowType));
    	if (workflows.isEmpty() && organizationId != Long.MIN_VALUE) {
    		workflows= find("select distinct wd.workflow from WorkflowDetail wd where wd.apply=true and exists (from wd.activity.activityOrganizationUsers aou where aou.organization.id=:p1 )" +
    				" and wd.workflow.workflowType.workflowType=:p2", new Parameter(organizationId, workflowType));
    	}
    	
    	return workflows.isEmpty() ? null : workflows.get(0);
    }
    
    public List<Workflow> findApprovalWorkflows(WorkflowType workflowType, long applyUserId, long organizationId, boolean bFectDetails) {
    	List<Workflow> workflows= find("select distinct w from Workflow w join fetch w.workflowDetails join w.workflowDetails wd where wd.approval=true and exists (from wd.activity.activityOrganizationUsers aou where aou.user.id=:p1)" +
				" and w.workflowType.workflowType=:p2", new Parameter(applyUserId, workflowType));
    	if (workflows.isEmpty() && organizationId != Long.MIN_VALUE) {
    		workflows= find("select distinct w from Workflow w join fetch w.workflowDetails join w.workflowDetails wd where wd.approval=true and exists (from wd.activity.activityOrganizationUsers aou where aou.organization.id=:p1 )" +
    				" and w.workflowType.workflowType=:p2", new Parameter(organizationId, workflowType));
    	}
    	
    	return workflows;
    }
    
    /*public String getMaxRefNo(final String appointmentItem){
    	Pageable pageable = PageRequest.of(0, 1);
    	Page<String> refs = find(pageable, "select refNo from Workflow where apptDatePeriod.apptItemRange.code=:p1 order by refNo desc", new Parameter(appointmentItem));
    	if (refs.hasContent()) {
    		return refs.getContent().get(0);
    	}
    	
    	return null;
	}
    
    public List<Map<String,Object>> getItemRangeAppointment(final List<Long> apptItemRangeIds){
    	List<Map<String,Object>> result = find("select new map(apptDatePeriod.apptItemRange.id as apptItemRangeId, count(*) as appointed) from Workflow where apptDatePeriod.apptItemRange.id in :p1 group by apptDatePeriod.apptItemRange.id", new Parameter(apptItemRangeIds));
    	
    	return result;
    }*/
}
