/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service.impl;

import com.mds.aiotplayer.cm.content.ContentQueueItem;
import com.mds.aiotplayer.cm.dao.ContentQueueDao;
import com.mds.aiotplayer.cm.model.ContentObject;
import com.mds.aiotplayer.cm.model.ContentQueue;
import com.mds.aiotplayer.cm.service.ContentQueueManager;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.core.CacheItem;
import com.mds.aiotplayer.util.CacheUtils;
import com.mds.aiotplayer.util.ConvertUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("contentQueueManager")
@WebService(serviceName = "ContentQueueService", endpointInterface = "com.mds.aiotplayer.cm.service.ContentQueueManager")
public class ContentQueueManagerImpl extends GenericManagerImpl<ContentQueue, Long> implements ContentQueueManager {
	
    ContentQueueDao contentQueueDao;

    @Autowired
    public ContentQueueManagerImpl(ContentQueueDao contentQueueDao) {
        super(contentQueueDao);
        this.contentQueueDao = contentQueueDao;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ContentQueue> getContentQueues(){
    	/*@SuppressWarnings("unchecked")
		List<ContentQueue> contentQueues = null;// = (List<ContentQueue>)CacheUtils.get(CacheItem.ContentQueues.toString());
		if (contentQueues == null){
			contentQueues = getAll();
			//CacheUtils.put(CacheItem.ContentQueues.toString(), contentQueues);
		}
		
		return contentQueues;*/
    	log.debug("get all contentQueues from db");
        return contentQueueDao.getAllDistinct();
    }

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
	public void removeContentQueue(Long id) {
		contentQueueDao.remove(id);
		//CacheUtils.remove(CacheItem.ContentQueues.toString());
	}

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public ContentQueue saveContentQueue(final ContentQueue contentQueue) throws RecordExistsException {
    	
        try {
        	ContentQueue result =  contentQueueDao.save(contentQueue);
        	//CacheUtils.remove(CacheItem.ContentQueues.toString());
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("ContentQueue '" + contentQueue.getId() + "' already exists!");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public ContentQueue saveContentQueue(final ContentQueueItem contentQueue, ContentObject conentObject) throws RecordExistsException {
    	
        try {
        	ContentQueue result =  contentQueueDao.save(toContentQueueDto(contentQueue, conentObject));
        	contentQueue.ContentQueueId = result.getId();
        	//CacheUtils.remove(CacheItem.ContentQueues.toString());
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("ContentQueue '" + contentQueue.ContentQueueId + "' already exists!");
        }
    }
    
    /// <summary>
  	/// Converts the <paramref name="item" /> to an instance of <see cref="ContentQueueDto" />.
  	/// </summary>
  	/// <param name="item">The item.</param>
  	/// <returns>An instance of <see cref="ContentQueueDto" />.</returns>
  	private ContentQueue toContentQueueDto(ContentQueueItem item, ContentObject contentObject){
  	   ContentQueue mediaQueueDto =  new ContentQueue();
  	   if (item.ContentQueueId != Long.MIN_VALUE)
  		   mediaQueueDto.setId(item.ContentQueueId);
  	   mediaQueueDto.setContentObject(contentObject);
  	   mediaQueueDto.setStatus(item.Status);
  	   mediaQueueDto.setStatusDetail(item.StatusDetail);
  	   mediaQueueDto.setConversionType(item.ConversionType);
  	   mediaQueueDto.setRotationAmount(item.RotationAmount);
  	   mediaQueueDto.setDateAdded(item.DateAdded);
  	   mediaQueueDto.setDateConversionStarted(item.DateConversionStarted);
  	   mediaQueueDto.setDateConversionCompleted(item.DateConversionCompleted);
  	   
  	   return mediaQueueDto;
  	}
    
	/**
     * {@inheritDoc}
     */
  	@Transactional
    @Override
    public Response removeContentQueue(final String contentQueueIds) {
        log.debug("removing contentQueue: " + contentQueueIds);
        try {
	        contentQueueDao.remove(ConvertUtil.StringtoLongArray(contentQueueIds));
	        //CacheUtils.remove(CacheItem.ContentQueues.toString());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("contentQueue(id=" + contentQueueIds + ") was successfully deleted.");
        return Response.ok().build();
    }
    
    /**
     * {@inheritDoc}
     *//*
    @Override
    public String getCacheKey() {
    	return CacheItem.cm_contentQueues.toString();
    }*/
}