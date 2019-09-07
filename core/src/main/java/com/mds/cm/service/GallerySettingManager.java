package com.mds.cm.service;

import com.mds.common.exception.RecordExistsException;
import com.mds.common.service.GenericManager;
import com.mds.cm.content.GallerySettings;
import com.mds.cm.model.GallerySetting;

import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.ws.rs.core.Response;

//@WebService
public interface GallerySettingManager extends GenericManager<GallerySetting, Long> {
 
	/**
     * Retrieves all gallery setting.
     *
     * @return Map
     */
	Map<String, Object> getGallerySettingsMap();

	/**
     * Saves a gallerySetting's information
     *
     * @param gallerySetting the gallerySetting's information
     * @return updated gallerySetting
     * @throws RecordExistsException thrown when gallerySetting already exists
     */
    GallerySetting saveGallerySetting(GallerySetting gallerySetting) throws RecordExistsException;
    void saveGallerySettings(GallerySettings gallerySettings);

	void removeGallerySetting(Long id) ;

	Response removeGallerySetting(final String gallerySettingIds);
}