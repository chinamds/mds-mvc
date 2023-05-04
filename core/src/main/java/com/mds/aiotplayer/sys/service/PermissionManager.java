/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service;

import java.util.List;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.sys.model.Permission;

public interface PermissionManager extends GenericManager<Permission, Long> {
	
	/**
     * Retrieves a list of all permissions.
     *
     * @return List
     */
    List<Permission> getPermissions();
    
   	/**
     * Saves a permission's information
     *
     * @param permission the permission's information
     * @return updated permission
     * @throws PermissionExistsException thrown when permission already exists
     */
    Permission savePermission(Permission permission) throws RecordExistsException;
    
	void removePermission(String permissionIds);
	
	String getCacheKey();
}