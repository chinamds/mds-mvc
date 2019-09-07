package com.mds.sys.dao;

import com.mds.common.dao.GenericDao;

import com.mds.sys.model.Permission;

/**
 * An interface that provides a data management interface to the Permission table.
 */
public interface PermissionDao extends GenericDao<Permission, Long> {
	/**
     * Saves a permission's information.
     * @param permission the object to be saved
     * @return the persisted Permission object
     */
    Permission savePermission(Permission permission);
}