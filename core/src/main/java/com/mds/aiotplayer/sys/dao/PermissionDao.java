/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.sys.model.Permission;

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