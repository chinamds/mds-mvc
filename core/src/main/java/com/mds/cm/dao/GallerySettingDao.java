package com.mds.cm.dao;

import com.mds.common.dao.GenericDao;

import java.util.Map;

import com.mds.cm.model.GallerySetting;

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