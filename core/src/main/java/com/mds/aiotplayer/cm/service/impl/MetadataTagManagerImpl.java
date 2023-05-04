/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service.impl;

import com.mds.aiotplayer.cm.dao.MetadataTagDao;
import com.mds.aiotplayer.cm.model.MetadataTag;
import com.mds.aiotplayer.cm.service.MetadataTagManager;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.util.ConvertUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("metadataTagManager")
@WebService(serviceName = "MetadataTagService", endpointInterface = "com.mds.aiotplayer.cm.service.MetadataTagManager")
public class MetadataTagManagerImpl extends GenericManagerImpl<MetadataTag, Long> implements MetadataTagManager {
    MetadataTagDao metadataTagDao;

    @Autowired
    public MetadataTagManagerImpl(MetadataTagDao metadataTagDao) {
        super(metadataTagDao);
        this.metadataTagDao = metadataTagDao;
    }

		/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
	public void removeMetadataTag(Long id) {
		metadataTagDao.remove(id);
		//CacheUtils.remove(CacheItem.Albums.toString());
	}

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public MetadataTag saveMetadataTag(final MetadataTag metadataTag) throws RecordExistsException {
   	
        try {
        	MetadataTag result =  metadataTagDao.save(metadataTag);
        	//CacheUtils.remove(CacheItem.Albums.toString());
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("MetadataTag '" + metadataTag.getTag().getTagName() + "' already exists!");
        }
    }

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Response removeMetadataTag(final String metadataTagIds) {
        log.debug("removing metadataTag: " + metadataTagIds);
        try {
	        metadataTagDao.remove(ConvertUtil.stringtoLongArray(metadataTagIds));
	      //CacheUtils.remove(CacheItem.Albums.toString());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Metadata Tag(id=" + metadataTagIds + ") was successfully deleted.");
        return Response.ok().build();
    }
}