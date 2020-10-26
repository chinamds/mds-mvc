/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.cm.model.ContentQueue;

/**
 * An interface that provides a data management interface to the ContentQueue table.
 */
public interface ContentQueueDao extends GenericDao<ContentQueue, Long> {
	/**
     * Saves a contentQueue's information.
     * @param contentQueue the object to be saved
     * @return the persisted ContentQueue object
     */
    ContentQueue saveContentQueue(ContentQueue contentQueue);
}