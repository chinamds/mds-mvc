package com.mds.aiotplayer.wf.dao.hibernate;

import com.mds.aiotplayer.wf.model.WorkflowDetail;
import com.mds.aiotplayer.wf.dao.WorkflowDetailDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import com.mds.aiotplayer.common.model.Parameter;
import com.mds.aiotplayer.core.WorkflowType;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository("workflowDetailDao")
public class WorkflowDetailDaoHibernate extends GenericDaoHibernate<WorkflowDetail, Long> implements WorkflowDetailDao {

    public WorkflowDetailDaoHibernate() {
        super(WorkflowDetail.class);
    }
        
    public List<WorkflowDetail> findApprovalWorkflowDetail(WorkflowType workflowType, long applyUserId, long organizationId) {
    	List<WorkflowDetail> workflowDetails= find("select wd from WorkflowDetail wd where wd.approval=true and exists (from wd.activity.activityOrganizationUsers aou where aou.user.id=:p1)" +
				" and wd.workflow.workflowType.workflowType=:p2", new Parameter(applyUserId, workflowType));
    	if (workflowDetails.isEmpty() && organizationId != Long.MIN_VALUE) {
    		workflowDetails= find("select wd from WorkflowDetail wd where wd.approval=true and exists (from wd.activity.activityOrganizationUsers aou where aou.organization.id=:p1 )" +
    				" and wd.workflow.workflowType.workflowType=:p2", new Parameter(organizationId, workflowType));
    	}
    	
    	return workflowDetails;
    }
}
