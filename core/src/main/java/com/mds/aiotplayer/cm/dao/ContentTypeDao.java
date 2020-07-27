package com.mds.aiotplayer.cm.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.cm.model.ContentType;

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