package com.mds.aiotplayer.cm.service.impl;

import com.mds.aiotplayer.cm.dao.AlbumDao;
import com.mds.aiotplayer.cm.dao.ContentObjectDao;
import com.mds.aiotplayer.cm.dao.MetadataDao;
import com.mds.aiotplayer.cm.metadata.ContentObjectMetadataItem;
import com.mds.aiotplayer.cm.model.Metadata;
import com.mds.aiotplayer.cm.service.MetadataManager;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.util.ConvertUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("metadataManager")
@WebService(serviceName = "MetadataService", endpointInterface = "com.mds.aiotplayer.cm.service.MetadataManager")
public class MetadataManagerImpl extends GenericManagerImpl<Metadata, Long> implements MetadataManager {
    MetadataDao metadataDao;
    AlbumDao albumDao;
    ContentObjectDao contentObjectDao;

    @Autowired
    public MetadataManagerImpl(MetadataDao metadataDao) {
        super(metadataDao);
        this.metadataDao = metadataDao;
    }
    
    @Autowired
    public void setAlbumDao(AlbumDao albumDao) {
        this.albumDao = albumDao;
    }
    
    @Autowired
    public void setContentObjectDao(ContentObjectDao contentObjectDao) {
        this.contentObjectDao = contentObjectDao;
    }

		/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
	public void removeMetadata(Long id) {
		metadataDao.remove(id);
		//CacheUtils.remove(CacheItem.Albums.toString());
	}
    
    @Override
    public List<Metadata> getMetadatas(Long albumId){
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSearchFilter("album.id", SearchOperator.eq, albumId);
        
        return metadataDao.findAll(searchable);
    }

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Metadata saveMetadata(final Metadata metadata) throws RecordExistsException {
    	
        try {
        	Metadata result =  metadataDao.save(metadata);
        	//CacheUtils.remove(CacheItem.Albums.toString());
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("Metadata '" + metadata.getMetaName() + "' already exists!");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void saveMetadata(ContentObjectMetadataItem metaDataItem) {
    	if (metaDataItem.getIsDeleted())	{
    		metadataDao.remove(metaDataItem.getContentObjectMetadataId());
			// Remove it from the collection.
			metaDataItem.getContentObject().getMetadataItems().remove(metaDataItem);
		}else if (metaDataItem.getContentObjectMetadataId() == Long.MIN_VALUE){
			// Insert the item.
			boolean isAlbum = metaDataItem.getContentObject().getContentObjectType() == ContentObjectType.Album;

			Metadata mDto = new Metadata();
			if (isAlbum) {
				mDto.setAlbum(albumDao.get(metaDataItem.getContentObject().getId()));
			}else {
				mDto.setContentObject(contentObjectDao.get(metaDataItem.getContentObject().getId()));
			}
			mDto.setMetaName(metaDataItem.getMetadataItemName());
			mDto.setValue(metaDataItem.getValue());
			mDto.setRawValue(metaDataItem.getRawValue());
			metadataDao.save(mDto);
		}else{
			// Update the item.
			Metadata mDto = metadataDao.get(metaDataItem.getContentObjectMetadataId());
			if (mDto != null){
				mDto.setMetaName(metaDataItem.getMetadataItemName());
				mDto.setValue(metaDataItem.getValue());
				mDto.setRawValue(metaDataItem.getRawValue());
			}
			metadataDao.save(mDto);
		}
    }

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Response removeMetadata(final String metadataIds) {
        log.debug("removing metadata: " + metadataIds);
        try {
	        metadataDao.remove(ConvertUtil.StringtoLongArray(metadataIds));
	        //CacheUtils.remove(CacheItem.Albums.toString());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Metadata(id=" + metadataIds + ") was successfully deleted.");
        return Response.ok().build();
    }
}