/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service.impl;

import com.mds.aiotplayer.cm.content.MimeTypeBo;
import com.mds.aiotplayer.cm.dao.SynchronizeDao;
import com.mds.aiotplayer.cm.model.MimeType;
import com.mds.aiotplayer.cm.model.Synchronize;
import com.mds.aiotplayer.cm.service.SynchronizeManager;
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

@Service("synchronizeManager")
@WebService(serviceName = "SynchronizeService", endpointInterface = "com.mds.aiotplayer.cm.service.SynchronizeManager")
public class SynchronizeManagerImpl extends GenericManagerImpl<Synchronize, Long> implements SynchronizeManager {
    SynchronizeDao synchronizeDao;

    @Autowired
    public SynchronizeManagerImpl(SynchronizeDao synchronizeDao) {
        super(synchronizeDao);
        this.synchronizeDao = synchronizeDao;
    }

		/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
	public void removeSynchronize(Long id) {
		synchronizeDao.remove(id);
		//CacheUtils.remove(CacheItem.Albums.toString());
	}
    
    public List<Synchronize> getSynchronizes(long galleryId){
    	Searchable searchable = Searchable.newSearchable();
        //searchable.addSort(Direction.ASC, "name");
        searchable.addSearchFilter("gallery.id", SearchOperator.eq, galleryId);
        
        return synchronizeDao.findAll(searchable);
    }

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Synchronize saveSynchronize(final Synchronize synchronize) throws RecordExistsException {
    	
        try {
        	Synchronize result =  synchronizeDao.save(synchronize);
        	//CacheUtils.remove(CacheItem.Albums.toString());
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("Synchronize '" + synchronize.getId() + "' already exists!");
        }
    }
    
	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Response removeSynchronize(final String synchronizeIds) {
        log.debug("removing synchronize: " + synchronizeIds);
        try {
	        synchronizeDao.remove(ConvertUtil.StringtoLongArray(synchronizeIds));
	        //CacheUtils.remove(CacheItem.Albums.toString());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Face Define(id=" + synchronizeIds + ") was successfully deleted.");
        return Response.ok().build();
    }
}