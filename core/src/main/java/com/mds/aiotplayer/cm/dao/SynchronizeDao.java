/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.cm.model.Synchronize;

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