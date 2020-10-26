/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.sys.model.Tenant;

/**
 * An interface that provides a data management interface to the Tenant table.
 */
public interface TenantDao extends GenericDao<Tenant, String> {
	/**
     * Saves a tenant's information.
     * @param tenant the object to be saved
     * @return the persisted Tenant object
     */
    Tenant saveTenant(Tenant tenant);
}