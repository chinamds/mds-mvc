/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import java.util.Map;

import com.mds.aiotplayer.cm.model.GallerySetting;

/**
 * An interface that provides a data management interface to the GallerySetting table.
 */
public interface GallerySettingDao extends GenericDao<GallerySetting, Long> {
	/**
     * Saves a gallerySetting's information.
     * @param gallerySetting the object to be saved
     * @return the persisted GallerySetting object
     */
    GallerySetting saveGallerySetting(GallerySetting gallerySetting);
}