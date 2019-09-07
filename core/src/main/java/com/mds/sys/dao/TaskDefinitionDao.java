package com.mds.sys.dao;

import com.mds.common.dao.GenericDao;

import com.mds.sys.model.TaskDefinition;

/**
 * An interface that provides a data management interface to the TaskDefinition table.
 */
public interface TaskDefinitionDao extends GenericDao<TaskDefinition, Long> {
	/**
     * Saves a taskDefinition's information.
     * @param taskDefinition the object to be saved
     * @return the persisted TaskDefinition object
     */
    TaskDefinition saveTaskDefinition(TaskDefinition taskDefinition);
}