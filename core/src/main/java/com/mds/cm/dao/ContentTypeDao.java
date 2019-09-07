package com.mds.cm.dao;

import com.mds.common.dao.GenericDao;

import com.mds.cm.model.ContentType;

/**
 * An interface that provides a data management interface to the ContentType table.
 */
public interface ContentTypeDao extends GenericDao<ContentType, Long> {
	/**
     * Saves a contentType's information.
     * @param contentType the object to be saved
     * @return the persisted ContentType object
     */
    ContentType saveContentType(ContentType contentType);
}