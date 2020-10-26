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
import com.mds.aiotplayer.cm.content.MimeTypeBoCollection;
import com.mds.aiotplayer.cm.model.MimeType;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

//@WebService
public interface MimeTypeManager extends GenericManager<MimeType, Long> {
	
	/**
     * Retrieves a list of mimetypes.
     * @return List
     */
	//@Cacheable(value="sysCache", key="#root.target.getCacheKey()")
    List<MimeType> getMimeTypes();
	
	MimeTypeBoCollection loadMimeTypesFromDataStore();

	/**
     * Saves a mimeType's information
     *
     * @param mimeType the mimeType's information
     * @return updated mimeType
     * @throws RecordExistsException thrown when mimeType already exists
     */
	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
    MimeType saveMimeType(MimeType mimeType) throws RecordExistsException;

	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	void removeMimeType(Long id) ;

	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	Response removeMimeType(final String mimeTypeIds);
	
	String getCacheKey();
}