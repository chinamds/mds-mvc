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
import com.mds.aiotplayer.cm.model.ContentType;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

//@WebService
public interface ContentTypeManager extends GenericManager<ContentType, Long> {
    /**
     * Saves a contentType's information
     *
     * @param contentType the contentType's information
     * @return updated contentType
     * @throws RecordExistsException thrown when contentType already exists
     */
	@CacheEvict(value="cmCache", key="#root.target.getCacheKey()")
    ContentType saveContentType(ContentType contentType) throws RecordExistsException;

	@CacheEvict(value="cmCache", key="#root.target.getCacheKey()")
	void removeContentType(Long id) ;

	@CacheEvict(value="cmCache", key="#root.target.getCacheKey()")
	Response removeContentType(final String contentTypeIds);
	
	/**
     * Retrieves a list of all content types.
     *
     * @return List
     */
	@Cacheable(value="cmCache", key="#root.target.getCacheKey()")
	List<ContentType> getContentTypes();
}