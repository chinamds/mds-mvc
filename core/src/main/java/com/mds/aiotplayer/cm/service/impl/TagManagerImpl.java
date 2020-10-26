/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service.impl;

import com.mds.aiotplayer.cm.dao.TagDao;
import com.mds.aiotplayer.cm.model.Tag;
import com.mds.aiotplayer.cm.service.TagManager;
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

@Service("tagManager")
@WebService(serviceName = "TagService", endpointInterface = "com.mds.aiotplayer.cm.service.TagManager")
public class TagManagerImpl extends GenericManagerImpl<Tag, Long> implements TagManager {
    TagDao tagDao;

    @Autowired
    public TagManagerImpl(TagDao tagDao) {
        super(tagDao);
        this.tagDao = tagDao;
    }

		/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
	public void removeTag(Long id) {
		tagDao.remove(id);
		//CacheUtils.remove(CacheItem.Albums.toString());
	}

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Tag saveTag(final Tag tag) throws RecordExistsException {   	
        try {
        	Tag result =  tagDao.save(tag);
        	//CacheUtils.remove(CacheItem.Albums.toString());
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("Tag '" + tag.getTagName() + "' already exists!");
        }
    }

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Response removeTag(final String tagIds) {
        log.debug("removing tag: " + tagIds);
        try {
	        tagDao.remove(ConvertUtil.StringtoLongArray(tagIds));
	      //CacheUtils.remove(CacheItem.Albums.toString());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Tag(id=" + tagIds + ") was successfully deleted.");
        return Response.ok().build();
    }
}