package com.mds.cm.dao;

import com.mds.common.dao.GenericDao;

import com.mds.cm.model.ContentActivity;

/**
 * An interface that provides a data management interface to the ContentActivity table.
 */
public interface ContentActivityDao extends GenericDao<ContentActivity, Long> {
	/**
     * Saves a contentActivity's information.
     * @param contentActivity the object to be saved
     * @return the persisted ContentActivity object
     */
    ContentActivity saveContentActivity(ContentActivity contentActivity);
}