package com.mds.sys.dao;

import com.mds.common.dao.GenericDao;

import com.mds.sys.model.Tenant;

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