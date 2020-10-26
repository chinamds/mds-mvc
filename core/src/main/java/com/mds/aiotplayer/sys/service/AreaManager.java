/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.sys.exception.AreaExistsException;
import com.mds.aiotplayer.sys.model.Area;

import java.util.List;
import javax.jws.WebService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

//@WebService
public interface AreaManager extends GenericManager<Area, Long> {
	
	/**
     * Retrieves a list of all areas.
     *
     * @return List
     */
	@Cacheable(value="sysCache", key="#root.target.getCacheKey()")
    List<Area> getAreas();

    /**
     * Saves a area's information
     *
     * @param area the area's information
     * @return updated area
     * @throws AreaExistsException thrown when area already exists
     */
	@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
    Area saveArea(Area area) throws AreaExistsException;

    /**
     * Removes a area from the database by their areaId
     *
     * @param areaId the area's id
     */
	@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
    void removeArea(String areaId);
    
	/**
     * Retrieves a list of all areas.
     * @return List
     */
	List<Area> findAll();
	
	/**
     * Removes a area from the database
     *
     * @param id the area to remove
     */
	void remove(Long id);

	@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	void removeArea(Area area);
	
	/**
     * Retrieves a cache key.
     * @return String
     */
	String getCacheKey();
}