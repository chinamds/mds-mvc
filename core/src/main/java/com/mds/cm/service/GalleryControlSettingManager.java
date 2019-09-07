package com.mds.cm.service;

import com.mds.common.exception.RecordExistsException;
import com.mds.common.service.GenericManager;
import com.mds.cm.content.GalleryControlSettings;
import com.mds.cm.model.GalleryControlSetting;

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