package com.mds.aiotplayer.cm.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.cm.model.Metadata;

/**
 * An interface that provides a data management interface to the Metadata table.
 */
public interface MetadataDao extends GenericDao<Metadata, Long> {
	/**
     * Saves a metadata's information.
     * @param metadata the object to be saved
     * @return the persisted Metadata object
     */
    Metadata saveMetadata(Metadata metadata);
}