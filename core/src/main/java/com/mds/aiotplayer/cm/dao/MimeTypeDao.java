/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.cm.model.MimeType;

/**
 * An interface that provides a data management interface to the MimeType table.
 */
public interface MimeTypeDao extends GenericDao<MimeType, Long> {
	/**
     * Saves a mimeType's information.
     * @param mimeType the object to be saved
     * @return the persisted MimeType object
     */
    MimeType saveMimeType(MimeType mimeType);
}