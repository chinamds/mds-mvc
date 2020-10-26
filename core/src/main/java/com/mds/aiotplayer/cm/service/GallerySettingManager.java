/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.cm.content.GallerySettings;
import com.mds.aiotplayer.cm.model.GallerySetting;

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