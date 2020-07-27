package com.mds.aiotplayer.i18n.service;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.i18n.model.NeutralResource;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

//@WebService
public interface NeutralResourceManager extends GenericManager<NeutralResource, Long> {
	
	/**
     * Retrieves a list of all neutralResource.
     *
     * @return List
     */
	//@Cacheable(value="sysCache", key="#root.target.getCacheKey()")
    List<NeutralResource> getNeutralResources();
    
    /**
     * Saves a neutralResource's information
     *
     * @param neutralResource the neutralResource's information
     * @return updated neutralResource
     * @throws NeutralResourceExistsException thrown when neutralResource already exists
     */
	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
    NeutralResource saveNeutralResource(NeutralResource neutralResource) throws RecordExistsException;
    
	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	Response removeNeutralResource(String neutralResourceIds);
}