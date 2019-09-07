package com.mds.i18n.service;

import com.mds.common.exception.RecordExistsException;
import com.mds.common.service.GenericManager;
import com.mds.i18n.model.LocalizedResource;

import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.ws.rs.core.Response;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

//@WebService
public interface LocalizedResourceManager extends GenericManager<LocalizedResource, Long> {
	
	List<LocalizedResource> findByCultureId(Long cultureId);
	List<Map<Long, Long>> findNeutralMap(Long cultureId);
    /**
     * Retrieves a list of all localizedResource.
     *
     * @return List
     */
	@Cacheable(value="sysCache", key="#root.target.getCacheKey()")
    List<LocalizedResource> getLocalizedResources();
    
    /**
     * Saves a localizedResource's information
     *
     * @param localizedResource the localizedResource's information
     * @return updated localizedResource
     * @throws LocalizedResourceExistsException thrown when localizedResource already exists
     */
	@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
    LocalizedResource saveLocalizedResource(LocalizedResource localizedResource) throws RecordExistsException;
    
	@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	Response removeLocalizedResource(String localizedResourceIds);
}