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
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.core.exception.WebException;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.model.ContentObject;
import com.mds.aiotplayer.core.ContentObjectRotation;

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