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