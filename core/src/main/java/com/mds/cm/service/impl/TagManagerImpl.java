package com.mds.cm.service.impl;

import com.mds.cm.dao.TagDao;
import com.mds.cm.model.Tag;
import com.mds.cm.service.TagManager;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.service.impl.GenericManagerImpl;
import com.mds.core.CacheItem;
import com.mds.util.CacheUtils;
import com.mds.util.ConvertUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("tagManager")
@WebService(serviceName = "TagService", endpointInterface = "com.mds.cm.service.TagManager")
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
    @Override
	public void removeTag(Long id) {
		tagDao.remove(id);
		//CacheUtils.remove(CacheItem.Albums.toString());
	}

	/**
     * {@inheritDoc}
     */
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