/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service.impl;

import com.mds.aiotplayer.cm.content.MimeTypeBo;
import com.mds.aiotplayer.cm.dao.ContentWorkflowDao;
import com.mds.aiotplayer.cm.model.MimeType;
import com.mds.aiotplayer.cm.model.ContentWorkflow;
import com.mds.aiotplayer.cm.service.ContentWorkflowManager;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.util.ConvertUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("contentWorkflowManager")
@WebService(serviceName = "ContentWorkflowService", endpointInterface = "com.mds.aiotplayer.cm.service.ContentWorkflowManager")
public class ContentWorkflowManagerImpl extends GenericManagerImpl<ContentWorkflow, Long> implements ContentWorkflowManager {
    ContentWorkflowDao contentWorkflowDao;

    @Autowired
    public ContentWorkflowManagerImpl(ContentWorkflowDao contentWorkflowDao) {
        super(contentWorkflowDao);
        this.contentWorkflowDao = contentWorkflowDao;
    }

		/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
	public void removeContentWorkflow(Long id) {
		contentWorkflowDao.remove(id);
		//CacheUtils.remove(CacheItem.Albums.toString());
	}
    
    public List<ContentWorkflow> getContentWorkflows(long galleryId){
    	Searchable searchable = Searchable.newSearchable();
        //searchable.addSort(Direction.ASC, "name");
        searchable.addSearchFilter("gallery.id", SearchOperator.eq, galleryId);
        
        return contentWorkflowDao.findAll(searchable);
    }

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public ContentWorkflow saveContentWorkflow(final ContentWorkflow contentWorkflow) throws RecordExistsException {
    	
        try {
        	ContentWorkflow result =  contentWorkflowDao.save(contentWorkflow);
        	//CacheUtils.remove(CacheItem.Albums.toString());
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("ContentWorkflow '" + contentWorkflow.getId() + "' already exists!");
        }
    }
    
	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Response removeContentWorkflow(final String contentWorkflowIds) {
        log.debug("removing contentWorkflow: " + contentWorkflowIds);
        try {
	        contentWorkflowDao.remove(ConvertUtil.stringtoLongArray(contentWorkflowIds));
	        //CacheUtils.remove(CacheItem.Albums.toString());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Face Define(id=" + contentWorkflowIds + ") was successfully deleted.");
        return Response.ok().build();
    }
}