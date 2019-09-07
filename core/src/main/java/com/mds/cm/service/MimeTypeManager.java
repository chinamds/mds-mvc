package com.mds.cm.service;

import com.mds.common.exception.RecordExistsException;
import com.mds.common.service.GenericManager;
import com.mds.cm.content.MimeTypeBoCollection;
import com.mds.cm.model.MimeType;

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