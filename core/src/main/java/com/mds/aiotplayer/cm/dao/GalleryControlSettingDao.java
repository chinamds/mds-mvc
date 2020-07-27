package com.mds.aiotplayer.cm.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.cm.model.GalleryControlSetting;

/**
 * An interface that provides a data management interface to the GalleryControllSetting table.
 */
public interface GalleryControlSettingDao extends GenericDao<GalleryControlSetting, Long> {
	/**
     * Saves a galleryControlSetting's information.
     * @param galleryControlSetting the object to be saved
     * @return the persisted GalleryControlSetting object
     */
    GalleryControlSetting saveGalleryControlSetting(GalleryControlSetting galleryControlSetting);
}