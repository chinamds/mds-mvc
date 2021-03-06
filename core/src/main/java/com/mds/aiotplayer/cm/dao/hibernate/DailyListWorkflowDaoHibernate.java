/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.DailyListWorkflow;
import com.beust.jcommander.internal.Lists;
import com.mds.aiotplayer.cm.dao.DailyListWorkflowDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import com.mds.aiotplayer.common.model.Parameter;
import com.mds.aiotplayer.core.ApprovalAction;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository("dailyListWorkflowDao")
public class DailyListWorkflowDaoHibernate extends GenericDaoHibernate<DailyListWorkflow, Long> implements DailyListWorkflowDao {

    public DailyListWorkflowDaoHibernate() {
        super(DailyListWorkflow.class);
    }
    
    public Long[] findTodoApprovals(Long[] workflowIds, Long[] workflowDetailIds) {
    	List<Long> dailyListIds = find("select dw.dailyList.id from DailyListWorkflow dw where dw.workflow.id in (:p1)" +
				" and exists (from dw.dailyListActivities da where da.approvalAction=:p3 and da.workflowDetail.id in (:p2) )", new Parameter(workflowIds, workflowDetailIds, ApprovalAction.NotSpecified));
    	
    	return dailyListIds.toArray(new Long[0]);
    }
    
    public List<DailyListWorkflow> findByDailyLists(Long[] dailyListIds) {
    	return find("select dw from DailyListWorkflow dw join fetch dw.dailyListActivities where dw.dailyList.id in (:p1)", new Parameter(Lists.newArrayList(dailyListIds)));
    }
    
    public DailyListWorkflow findByDailyList(Long dailyListId) {
    	List<DailyListWorkflow>  dailyListWorkflows =  find("select dw from DailyListWorkflow dw join fetch dw.dailyListActivities where dw.dailyList.id = :p1", new Parameter(dailyListId));
    	
    	return dailyListWorkflows.isEmpty() ? null : dailyListWorkflows.get(0);
    }
}
