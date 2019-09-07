package com.mds.sys.service;

import com.mds.common.exception.RecordExistsException;
import com.mds.common.service.GenericManager;
import com.mds.sys.model.Permission;

import java.util.List;
import javax.jws.WebService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

//@WebService
public interface PermissionManager extends GenericManager<Permission, Long> {
	
	/**
     * Retrieves a list of all permissions.
     *
     * @return List
     */
	//@Cacheable(value="sysCache", key="#root.target.getCacheKey()")
    List<Permission> getPermissions();
    
   	/**
     * Saves a permission's information
     *
     * @param permission the permission's information
     * @return updated permission
     * @throws PermissionExistsException thrown when permission already exists
     */
	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
    Permission savePermission(Permission permission) throws RecordExistsException;
    
	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	void removePermission(String permissionIds);
	
	String getCacheKey();
}