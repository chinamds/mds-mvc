/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service.impl;

import com.mds.aiotplayer.cm.content.MimeTypeBo;
import com.mds.aiotplayer.cm.dao.MimeTypeGalleryDao;
import com.mds.aiotplayer.cm.model.MimeType;
import com.mds.aiotplayer.cm.model.MimeTypeGallery;
import com.mds.aiotplayer.cm.service.MimeTypeGalleryManager;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.util.ConvertUtil;
import com.mds.aiotplayer.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("mimeTypeGalleryManager")
@WebService(serviceName = "MimeTypeGalleryService", endpointInterface = "com.mds.aiotplayer.cm.service.MimeTypeGalleryManager")
public class MimeTypeGalleryManagerImpl extends GenericManagerImpl<MimeTypeGallery, Long> implements MimeTypeGalleryManager {
    MimeTypeGalleryDao mimeTypeGalleryDao;

    @Autowired
    public MimeTypeGalleryManagerImpl(MimeTypeGalleryDao mimeTypeGalleryDao) {
        super(mimeTypeGalleryDao);
        this.mimeTypeGalleryDao = mimeTypeGalleryDao;
    }

		/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
	public void removeMimeTypeGallery(Long id) {
		mimeTypeGalleryDao.remove(id);
		//CacheUtils.remove(CacheItem.Albums.toString());
	}
    
    public List<MimeTypeGallery> getMimeTypeGalleries(long galleryId){
    	Searchable searchable = Searchable.newSearchable();
        //searchable.addSort(Direction.ASC, "name");
        searchable.addSearchFilter("gallery.id", SearchOperator.eq, galleryId);
        
        return mimeTypeGalleryDao.findAll(searchable);
    }

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public MimeTypeGallery saveMimeTypeGallery(final MimeTypeGallery mimeTypeGallery) throws RecordExistsException {
    	
        try {
        	MimeTypeGallery result =  mimeTypeGalleryDao.save(mimeTypeGallery);
        	//CacheUtils.remove(CacheItem.Albums.toString());
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("MimeTypeGallery '" + mimeTypeGallery.getId() + "' already exists!");
        }
    }
    
    /// <summary>
  	/// Persist the gallery-specific properties of this instance to the data store. Currently, only the <see cref="MimeTypeBo.AllowAddToGallery" /> 
  	/// property is unique to the gallery identified in <see cref="MimeTypeBo.GalleryId" />; the other properties are application-wide and at
  	/// present there is no API to modify them. In other words, this method saves whether a particular MIME type is enabled or disabled for a
  	/// particular gallery.
  	/// </summary>
  	/// <exception cref="InvalidOperationException">Thrown when the current instance is an application-level MIME type. Only gallery-specific
  	/// MIME types can be persisted to the data store. Specifically, the exception is thrown when <see cref="MimeTypeBo.GalleryId" /> or
  	/// <see cref="MimeTypeBo.MimeTypeGalleryId" /> is <see cref="Int32.MinValue" />.</exception>
    @Transactional
    @Override
  	public MimeTypeGallery saveMimeTypeGallery(MimeTypeBo mimeType ){
  		if ((mimeType.getGalleryId() == Long.MIN_VALUE) || (mimeType.getMimeTypeGalleryId() == Long.MIN_VALUE))
  		{
  			throw new UnsupportedOperationException(StringUtils.format("Cannot save. This MIME type instance is an application-level MIME type and cannot be persisted to the data store. Only gallery-specific MIME types can be saved. (GalleryId={0}, MimeTypeId={1}, MimeTypeGalleryId={2}, FileExtension={3}"
  					, mimeType.getGalleryId(), mimeType.getMimeTypeId(), mimeType.getMimeTypeGalleryId(), mimeType.getExtension()));
  		}

  		MimeTypeGallery mtDto = get(mimeType.getMimeTypeGalleryId());
		if (mtDto != null){
			mtDto.setIsEnabled(mimeType.getAllowAddToGallery());
			return save(mtDto);
		}
		
		return null;
  	}

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Response removeMimeTypeGallery(final String mimeTypeGalleryIds) {
        log.debug("removing mimeTypeGallery: " + mimeTypeGalleryIds);
        try {
	        mimeTypeGalleryDao.remove(ConvertUtil.stringtoLongArray(mimeTypeGalleryIds));
	        //CacheUtils.remove(CacheItem.Albums.toString());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Face Define(id=" + mimeTypeGalleryIds + ") was successfully deleted.");
        return Response.ok().build();
    }
}