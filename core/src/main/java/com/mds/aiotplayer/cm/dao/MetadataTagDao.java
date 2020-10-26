/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.cm.model.MetadataTag;

/**
 * An interface that provides a data management interface to the MetadataTag table.
 */
public interface MetadataTagDao extends GenericDao<MetadataTag, Long> {
	/**
     * Saves a metadataTag's information.
     * @param metadataTag the object to be saved
     * @return the persisted MetadataTag object
     */
    MetadataTag saveMetadataTag(MetadataTag metadataTag);
}