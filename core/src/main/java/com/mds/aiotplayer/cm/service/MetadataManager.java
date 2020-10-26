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
import com.mds.aiotplayer.cm.metadata.ContentObjectMetadataItem;
import com.mds.aiotplayer.cm.model.Metadata;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

@WebService
public interface MetadataManager extends GenericManager<Metadata, Long> {
    /**
     * Saves a metadata's information
     *
     * @param metadata the metadata's information
     * @return updated metadata
     * @throws RecordExistsException thrown when metadata already exists
     */
    Metadata saveMetadata(Metadata metadata) throws RecordExistsException;
    void saveMetadata(ContentObjectMetadataItem metaDataItem);
    
    List<Metadata> getMetadatas(Long albumId);

	void removeMetadata(Long id) ;
	
	Response removeMetadata(final String metadataIds);
}