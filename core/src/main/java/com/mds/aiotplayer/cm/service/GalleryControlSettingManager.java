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
import com.mds.aiotplayer.cm.content.GalleryControlSettings;
import com.mds.aiotplayer.cm.model.GalleryControlSetting;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

@WebService
public interface GalleryControlSettingManager extends GenericManager<GalleryControlSetting, Long> {
    /**
     * Saves a galleryControlSetting's information
     *
     * @param galleryControlSetting the galleryControlSetting's information
     * @return updated galleryControlSetting
     * @throws RecordExistsException thrown when galleryControlSetting already exists
     */
    GalleryControlSetting saveGalleryControlSetting(GalleryControlSetting galleryControlSetting) throws RecordExistsException;
    
    void saveGalleryControlSetting(GalleryControlSettings galleryControlSettings);

	void removeGalleryControlSetting(Long id) ;

	Response removeGalleryControlSetting(final String galleryControlSettingIds);
}