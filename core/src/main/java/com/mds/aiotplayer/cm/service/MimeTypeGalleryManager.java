package com.mds.aiotplayer.cm.service;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.cm.content.MimeTypeBo;
import com.mds.aiotplayer.cm.model.MimeTypeGallery;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

@WebService
public interface MimeTypeGalleryManager extends GenericManager<MimeTypeGallery, Long> {
    /**
     * Saves a mimeTypeGallery's information
     *
     * @param mimeTypeGallery the mimeTypeGallery's information
     * @return updated mimeTypeGallery
     * @throws RecordExistsException thrown when mimeTypeGallery already exists
     */
    MimeTypeGallery saveMimeTypeGallery(MimeTypeGallery mimeTypeGallery) throws RecordExistsException;
    MimeTypeGallery saveMimeTypeGallery(MimeTypeBo mimeType );
    
    List<MimeTypeGallery> getMimeTypeGalleries(long galleryId);

	void removeMimeTypeGallery(Long id) ;

	Response removeMimeTypeGallery(final String mimeTypeGalleryIds);
}