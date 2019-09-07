package com.mds.cm.service;

import com.mds.common.exception.RecordExistsException;
import com.mds.common.service.GenericManager;
import com.mds.cm.model.Tag;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

@WebService
public interface TagManager extends GenericManager<Tag, Long> {
    /**
     * Saves a tag's information
     *
     * @param tag the tag's information
     * @return updated tag
     * @throws RecordExistsException thrown when tag already exists
     */
    Tag saveTag(Tag tag) throws RecordExistsException;

	void removeTag(Long id) ;

	Response removeTag(final String tagIds);
}