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
import com.mds.aiotplayer.cm.model.Gallery;
import com.mds.aiotplayer.cm.model.GalleryMapping;

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