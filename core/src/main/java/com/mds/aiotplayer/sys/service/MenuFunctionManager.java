package com.mds.aiotplayer.sys.service;

import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.common.exception.RecordExistsException;
//import com.mds.aiotplayer.common.model.Page;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.sys.exception.MenuFunctionExistsException;
import com.mds.aiotplayer.sys.model.MenuFunction;

import java.util.List;
import javax.jws.WebService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

//@WebService
public interface MenuFunctionManager extends GenericManager<MenuFunction, Long> {
	
	Page<MenuFunction> findMenuFunction(Pageable page, MenuFunction menuFunction);

	/**
     * Retrieves a list of all menuFunctions.
     * @return List
     */
	List<MenuFunction> findAll();
	
	/**
     * Retrieves a list of all menuFunctions.
     *
     * @return List
     */
	//@Cacheable(value="sysCache", key="#root.target.getCacheKey()")
    List<MenuFunction> getMenuFunctions();
	
	/**
     * Removes a menuFunction from the database
     *
     * @param id the menuFunction to remove
     */
	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	void remove(Long id);

	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	void removeMenuFunction(MenuFunction menuFunction);
	
	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	void removeMenuFunction(final String menuFunctionIds);
	
	/**
     * Saves a menuFunction's information
     *
     * @param menuFunction the menuFunction's information
     * @return updated menuFunction
     * @throws MenuFunctionExistsException thrown when menuFunction already exists
     */
	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
    MenuFunction saveMenuFunction(MenuFunction menuFunction) throws MenuFunctionExistsException;
}