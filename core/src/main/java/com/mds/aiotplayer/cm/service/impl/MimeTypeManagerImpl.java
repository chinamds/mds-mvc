/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service.impl;

import com.mds.aiotplayer.cm.content.MimeTypeBo;
import com.mds.aiotplayer.cm.content.MimeTypeBoCollection;
import com.mds.aiotplayer.cm.dao.MimeTypeDao;
import com.mds.aiotplayer.cm.model.MimeType;
import com.mds.aiotplayer.cm.service.MimeTypeManager;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.core.CacheItem;
import com.mds.aiotplayer.core.exception.BusinessException;
import com.mds.aiotplayer.util.CacheUtils;
import com.mds.aiotplayer.util.ConvertUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("mimeTypeManager")
@WebService(serviceName = "MimeTypeService", endpointInterface = "com.mds.aiotplayer.cm.service.MimeTypeManager")
public class MimeTypeManagerImpl extends GenericManagerImpl<MimeType, Long> implements MimeTypeManager {
	
    MimeTypeDao mimeTypeDao;

    @Autowired
    public MimeTypeManagerImpl(MimeTypeDao mimeTypeDao) {
        super(mimeTypeDao);
        this.mimeTypeDao = mimeTypeDao;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<MimeType> getMimeTypes(){
    	/*@SuppressWarnings("unchecked")
		List<MimeType> mimeTypes = null;// = (List<MimeType>)CacheUtils.get(CacheItem.MimeTypes.toString());
		if (mimeTypes == null){
			mimeTypes = getAll();
			//CacheUtils.put(CacheItem.MimeTypes.toString(), mimeTypes);
		}
		
		return mimeTypes;*/
    	log.debug("get all mimetypes from db");
        return mimeTypeDao.getAllDistinct();
    }

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
	public void removeMimeType(Long id) {
		mimeTypeDao.remove(id);
		//CacheUtils.remove(CacheItem.MimeTypes.toString());
	}

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public MimeType saveMimeType(final MimeType mimeType) throws RecordExistsException {
    	
        try {
        	MimeType result =  mimeTypeDao.save(mimeType);
        	//CacheUtils.remove(CacheItem.MimeTypes.toString());
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("MimeType '" + mimeType.getFileExtension() + "' already exists!");
        }
    }
      	
  	/// <summary>
	/// Loads the set of MIME types from the data store. These MIME types are the master list of MIME types and are not
	/// specific to a particular gallery. That is, the <see cref="MimeTypeBo.GalleryId" /> property is set to <see cref="Int32.MinValue" />
	/// and the <see cref="MimeTypeBo.AllowAddToGallery" /> property is <c>false</c> for all items.
	/// </summary>
	/// <returns>Returns a <see cref="MimeTypeBoCollection" /> containing MIME types..</returns>
	/// <exception cref="BusinessException">Thrown when no records were found in the master list of MIME types in the data store.</exception>
	public MimeTypeBoCollection loadMimeTypesFromDataStore(){
		MimeTypeBoCollection baseMimeTypes = new MimeTypeBoCollection();
	
		Searchable searchable = Searchable.newSearchable();
	    searchable.addSort(Direction.ASC, "fileExtension");
	    List<MimeType> mineTypes = mimeTypeDao.findAll(searchable);
		for(MimeType mimeTypeDto : mineTypes){
			baseMimeTypes.add(new MimeTypeBo(mimeTypeDto.getId(), Long.MIN_VALUE, Long.MIN_VALUE, mimeTypeDto.getFileExtension().trim()
					, mimeTypeDto.getMimeTypeValue().trim(), mimeTypeDto.getBrowserMimeTypeValue().trim(), false));
		}
	
		if (baseMimeTypes.isEmpty()){
			throw new BusinessException("No records were found in the master list of MIME types in the data store.");
		}
	
		return baseMimeTypes;
  	}

	/**
     * {@inheritDoc}
     */
	@Transactional
    @Override
    public Response removeMimeType(final String mimeTypeIds) {
        log.debug("removing mimeType: " + mimeTypeIds);
        try {
	        mimeTypeDao.remove(ConvertUtil.stringtoLongArray(mimeTypeIds));
	        //CacheUtils.remove(CacheItem.MimeTypes.toString());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("mimeType(id=" + mimeTypeIds + ") was successfully deleted.");
        return Response.ok().build();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCacheKey() {
    	return CacheItem.cm_mimetypes.toString();
    }
}