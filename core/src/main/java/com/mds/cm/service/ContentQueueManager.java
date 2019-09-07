package com.mds.cm.service;

import com.mds.common.exception.RecordExistsException;
import com.mds.common.service.GenericManager;
import com.mds.cm.content.ContentQueueItem;
import com.mds.cm.model.ContentObject;
import com.mds.cm.model.ContentQueue;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

//@WebService
public interface ContentQueueManager extends GenericManager<ContentQueue, Long> {
	
	/**
     * Retrieves a list of contentQueues.
     * @return List
     */
	//@Cacheable(value="sysCache", key="#root.target.getCacheKey()")
    List<ContentQueue> getContentQueues();

	/**
     * Saves a contentQueue's information
     *
     * @param contentQueue the contentQueue's information
     * @return updated contentQueue
     * @throws RecordExistsException thrown when contentQueue already exists
     */
	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
    ContentQueue saveContentQueue(ContentQueue contentQueue) throws RecordExistsException;
	ContentQueue saveContentQueue(ContentQueueItem contentQueue, ContentObject conentObject) throws RecordExistsException;

	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	void removeContentQueue(Long id) ;

	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	Response removeContentQueue(final String contentQueueIds);
	
	//String getCacheKey();
}