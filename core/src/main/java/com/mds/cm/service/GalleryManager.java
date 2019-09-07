package com.mds.cm.service;

import com.mds.common.exception.RecordExistsException;
import com.mds.common.service.GenericManager;
import com.mds.cm.model.Gallery;
import com.mds.cm.model.GalleryMapping;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

//@WebService
public interface GalleryManager extends GenericManager<Gallery, Long> {
    /**
     * Saves a gallery's information
     *
     * @param gallery the gallery's information
     * @return updated gallery
     * @throws RecordExistsException thrown when gallery already exists
     */
    Gallery saveGallery(Gallery gallery) throws RecordExistsException;
    Gallery saveGalleryWithMapping(Gallery gallery, List<GalleryMapping> galleryMappings) throws RecordExistsException;
    Gallery saveGalleryWithMapping(Gallery gallery, List<Long> organizations, List<Long> users) throws RecordExistsException;

	void removeGallery(Long id) ;

	Response removeGallery(final String galleryIds);
	List<Gallery> findGalleries(long organizationId);
}