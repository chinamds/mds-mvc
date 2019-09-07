package com.mds.sys.dao;

import com.mds.common.dao.GenericDao;

import com.mds.sys.model.Log;

/**
 * An interface that provides a data management interface to the Log table.
 */
public interface LogDao extends GenericDao<Log, Long> {
	/**
     * Saves a log's information.
     * @param log the object to be saved
     * @return the persisted Log object
     */
    Log saveLog(Log log);
}