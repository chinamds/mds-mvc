/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.i18n.service;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.i18n.model.Culture;
import com.mds.aiotplayer.i18n.model.LocalizedResource;

import java.util.List;
import javax.jws.WebService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

//@WebService
public interface CultureManager extends GenericManager<Culture, Long> {
   
	/**
     * Retrieves a list of all cultures.
     *
     * @return List
     */
	//@Cacheable(value="sysCache", key="#root.target.getCacheKey()")
    List<Culture> getCultures();
    
	/**
     * Saves a culture's information
     *
     * @param culture the culture's information
     * @return updated culture
     * @throws CultureExistsException thrown when culture already exists
     */
	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
    Culture saveCulture(Culture culture) throws RecordExistsException;
    
	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	void removeCulture(String cultureIds);
	
	/**
     * Retrieves a cache key.
     * @return String
     *//*
	String getCacheKey();*/
}