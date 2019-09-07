package com.mds.cm.service.impl;

import com.mds.cm.content.MimeTypeBo;
import com.mds.cm.dao.ContentWorkflowDao;
import com.mds.cm.model.MimeType;
import com.mds.cm.model.ContentWorkflow;
import com.mds.cm.service.ContentWorkflowManager;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.model.search.SearchOperator;
import com.mds.common.model.search.Searchable;
import com.mds.common.service.impl.GenericManagerImpl;
import com.mds.util.ConvertUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("contentWorkflowManager")
@WebService(serviceName = "ContentWorkflowService", endpointInterface = "com.mds.cm.service.ContentWorkflowManager")
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
    @Override
    public Response removeContentWorkflow(final String contentWorkflowIds) {
        log.debug("removing contentWorkflow: " + contentWorkflowIds);
        try {
	        contentWorkflowDao.remove(ConvertUtil.StringtoLongArray(contentWorkflowIds));
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