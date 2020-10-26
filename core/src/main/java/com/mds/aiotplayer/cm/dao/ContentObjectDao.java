/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.cm.model.ContentObject;

/**
 * An interface that provides a data management interface to the ContentObject table.
 */
public interface ContentObjectDao extends GenericDao<ContentObject, Long> {
	/**
     * Saves a contentObject's information.
     * @param contentObject the object to be saved
     * @return the persisted ContentObject object
     */
    ContentObject saveContentObject(ContentObject contentObject);
}