package com.mds.cm.dao;

import com.mds.common.dao.GenericDao;

import com.mds.cm.model.ContentQueue;

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