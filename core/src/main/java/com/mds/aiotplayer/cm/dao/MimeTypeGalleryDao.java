/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.cm.model.MimeTypeGallery;

/**
 * An interface that provides a data management interface to the MimeTypeGallery table.
 */
public interface MimeTypeGalleryDao extends GenericDao<MimeTypeGallery, Long> {
	/**
     * Saves a mimeTypeGallery's information.
     * @param mimeTypeGallery the object to be saved
     * @return the persisted MimeTypeGallery object
     */
    MimeTypeGallery saveMimeTypeGallery(MimeTypeGallery mimeTypeGallery);
}