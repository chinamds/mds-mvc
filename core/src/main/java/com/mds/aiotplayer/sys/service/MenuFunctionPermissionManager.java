package com.mds.aiotplayer.sys.service;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.sys.model.MenuFunctionPermission;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

//@WebService
public interface MenuFunctionPermissionManager extends GenericManager<MenuFunctionPermission, Long> {
	/**
     * Retrieves a list of all menuFunctionPermissions.
     *
     * @return List
     */
	@Cacheable(value="sysCache", key="#root.target.getCacheKey()")
    List<MenuFunctionPermission> getMenuFunctionPermissions();
	
	/**
     * Removes a menuFunctionPermission from the database
     *
     * @param id the menuFunctionPermission to remove
     */
	@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	void remove(Long id);

	@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	void removeMenuFunctionPermission(MenuFunctionPermission menuFunctionPermission);
	
	@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	void removeMenuFunctionPermission(final String menuFunctionPermissionIds);
	
	/**
     * Saves a menuFunctionPermission's information
     *
     * @param menuFunctionPermission the menuFunctionPermission's information
     * @return updated menuFunctionPermission
     * @throws MenuFunctionPermissionExistsException thrown when menuFunctionPermission already exists
     */
	@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
    MenuFunctionPermission saveMenuFunctionPermission(MenuFunctionPermission menuFunctionPermission) throws RecordExistsException;
	
	List<MenuFunctionPermission> findByMenuFunctionIds(List menuFunctionIds);
	
	List<MenuFunctionPermission> findByRoleIds(List roleIds);
}