/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import java.util.List;

import com.mds.aiotplayer.cm.model.DailyListWorkflow;

/**
 * An interface that provides a data management interface to the DailyListWorkflow table.
 */
public interface DailyListWorkflowDao extends GenericDao<DailyListWorkflow, Long> {
	Long[] findTodoApprovals(Long[] workflowIds, Long[] workflowDetailIds);
	
	List<DailyListWorkflow> findByDailyLists(Long[] dailyListIds);
	
	DailyListWorkflow findByDailyList(Long dailyListId);
}