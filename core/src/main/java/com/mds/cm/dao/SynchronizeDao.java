package com.mds.cm.dao;

import com.mds.common.dao.GenericDao;

import com.mds.cm.model.Synchronize;

/**
 * An interface that provides a data management interface to the Synchronize table.
 */
public interface SynchronizeDao extends GenericDao<Synchronize, Long> {
	/**
     * Saves a synchronize's information.
     * @param synchronize the object to be saved
     * @return the persisted Synchronize object
     */
    Synchronize saveSynchronize(Synchronize synchronize);
}