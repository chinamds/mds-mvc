/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.cm.model.UiTemplate;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

@WebService
public interface UiTemplateManager extends GenericManager<UiTemplate, Long> {
	
	/**
     * Retrieves a list of uiTemplates.
     * @return List
     */
	//@Cacheable(value="sysCache", key="#root.target.getCacheKey()")
    List<UiTemplate> getUiTemplate();
	
	/**
     * Saves a uiTemplate's information
     *
     * @param uiTemplate the uiTemplate's information
     * @return updated uiTemplate
     * @throws RecordExistsException thrown when uiTemplate already exists
     */
	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
    UiTemplate saveUiTemplate(UiTemplate uiTemplate) throws RecordExistsException;

    //@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	void removeUiTemplate(Long id) ;

	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	Response removeUiTemplate(final String uiTemplateIds);
	
	/**
     * Retrieves a cache key.
     * @return String
     */
	String getCacheKey();
}