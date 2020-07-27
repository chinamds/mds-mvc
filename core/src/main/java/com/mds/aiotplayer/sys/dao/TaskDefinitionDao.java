package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.sys.model.TaskDefinition;

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