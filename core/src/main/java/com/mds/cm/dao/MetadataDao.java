package com.mds.cm.dao;

import com.mds.common.dao.GenericDao;

import com.mds.cm.model.Metadata;

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