package com.mds.cm.service.impl;

import com.mds.cm.content.MimeTypeBo;
import com.mds.cm.dao.SynchronizeDao;
import com.mds.cm.model.MimeType;
import com.mds.cm.model.Synchronize;
import com.mds.cm.service.SynchronizeManager;
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

@Service("synchronizeManager")
@WebService(serviceName = "SynchronizeService", endpointInterface = "com.mds.cm.service.SynchronizeManager")
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