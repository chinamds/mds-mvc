package com.mds.aiotplayer.cm.service;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.cm.content.MimeTypeBo;
import com.mds.aiotplayer.cm.model.ContentActivity;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

@WebService
public interface ContentActivityManager extends GenericManager<ContentActivity, Long> {
    /**
     * Saves a contentActivity's information
     *
     * @param contentActivity the contentActivity's information
     * @return updated contentActivity
     * @throws RecordExistsException thrown when contentActivity already exists
     */
    ContentActivity saveContentActivity(ContentActivity contentActivity) throws RecordExistsException;
    
    List<ContentActivity> getContentActivitys(long galleryId);

	void removeContentActivity(Long id) ;

	Response removeContentActivity(final String contentActivityIds);
}