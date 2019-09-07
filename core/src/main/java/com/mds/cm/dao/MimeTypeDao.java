package com.mds.cm.dao;

import com.mds.common.dao.GenericDao;

import com.mds.cm.model.MimeType;

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