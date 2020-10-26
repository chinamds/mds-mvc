/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
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