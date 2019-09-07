package com.mds.cm.service;

import com.mds.common.exception.RecordExistsException;
import com.mds.common.service.GenericManager;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.core.exception.WebException;
import com.mds.cm.exception.GallerySecurityException;
import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidContentObjectException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.cm.model.ContentObject;
import com.mds.core.ContentObjectRotation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.ws.rs.core.Response;

@WebService
public interface ContentObjectManager extends GenericManager<ContentObject, Long> {
    /**
     * Saves a contentObject's information
     *
     * @param contentObject the contentObject's information
     * @return updated contentObject
     * @throws RecordExistsException thrown when contentObject already exists
     */
    ContentObject saveContentObject(ContentObject contentObject) throws RecordExistsException;

	void removeContentObject(Long id) ;

	Response removeContentObject(final String contentObjectIds);
	
	void saveCaptions(Map<Long, String> contentCAptions);
	
	void deleteOriginalFile(List<Long> contentObjectIds, List<Long> albumIds) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException, IOException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, WebException;
	
	int saveImageRotates(Map<Long, ContentObjectRotation> contentRotates);
}