/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.cm.model.Tag;

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