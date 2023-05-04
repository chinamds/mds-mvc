/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service.impl;

import com.mds.aiotplayer.cm.content.MimeTypeBo;
import com.mds.aiotplayer.cm.dao.ContentActivityDao;
import com.mds.aiotplayer.cm.model.MimeType;
import com.mds.aiotplayer.cm.model.ContentActivity;
import com.mds.aiotplayer.cm.service.ContentActivityManager;
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

@Service("contentActivityManager")
@WebService(serviceName = "ContentActivityService", endpointInterface = "com.mds.aiotplayer.cm.service.ContentActivityManager")
public class ContentActivityManagerImpl extends GenericManagerImpl<ContentActivity, Long> implements ContentActivityManager {
    ContentActivityDao contentActivityDao;

    @Autowired
    public ContentActivityManagerImpl(ContentActivityDao contentActivityDao) {
        super(contentActivityDao);
        this.contentActivityDao = contentActivityDao;
    }

		/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
	public void removeContentActivity(Long id) {
		contentActivityDao.remove(id);
		//CacheUtils.remove(CacheItem.Albums.toString());
	}
    
    public List<ContentActivity> getContentActivitys(long galleryId){
    	Searchable searchable = Searchable.newSearchable();
        //searchable.addSort(Direction.ASC, "name");
        searchable.addSearchFilter("gallery.id", SearchOperator.eq, galleryId);
        
        return contentActivityDao.findAll(searchable);
    }

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public ContentActivity saveContentActivity(final ContentActivity contentActivity) throws RecordExistsException {
    	
        try {
        	ContentActivity result =  contentActivityDao.save(contentActivity);
        	//CacheUtils.remove(CacheItem.Albums.toString());
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("ContentActivity '" + contentActivity.getId() + "' already exists!");
        }
    }
    
	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Response removeContentActivity(final String contentActivityIds) {
        log.debug("removing contentActivity: " + contentActivityIds);
        try {
	        contentActivityDao.remove(ConvertUtil.stringtoLongArray(contentActivityIds));
	        //CacheUtils.remove(CacheItem.Albums.toString());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Face Define(id=" + contentActivityIds + ") was successfully deleted.");
        return Response.ok().build();
    }
}