package com.mds.cm.service;

import com.mds.common.exception.RecordExistsException;
import com.mds.common.service.GenericManager;
import com.mds.cm.metadata.ContentObjectMetadataItem;
import com.mds.cm.model.Metadata;

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