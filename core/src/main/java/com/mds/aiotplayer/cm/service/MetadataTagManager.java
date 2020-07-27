package com.mds.aiotplayer.cm.service;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.cm.model.MetadataTag;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

@WebService
public interface MetadataTagManager extends GenericManager<MetadataTag, Long> {
    /**
     * Saves a metadataTag's information
     *
     * @param metadataTag the metadataTag's information
     * @return updated metadataTag
     * @throws RecordExistsException thrown when metadataTag already exists
     */
    MetadataTag saveMetadataTag(MetadataTag metadataTag) throws RecordExistsException;

	void removeMetadataTag(Long id) ;

	Response removeMetadataTag(final String metadataTagIds);
}