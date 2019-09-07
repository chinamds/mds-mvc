package com.mds.cm.dao;

import com.mds.common.dao.GenericDao;

import java.util.List;

import com.mds.cm.model.DailyListWorkflow;

/**
 * An interface that provides a data management interface to the DailyListWorkflow table.
 */
public interface DailyListWorkflowDao extends GenericDao<DailyListWorkflow, Long> {
	Long[] findTodoApprovals(Long[] workflowIds, Long[] workflowDetailIds);
	
	List<DailyListWorkflow> findByDailyLists(Long[] dailyListIds);
	
	DailyListWorkflow findByDailyList(Long dailyListId);
}