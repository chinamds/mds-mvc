package com.mds.aiotplayer.cm.service;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.cm.content.MimeTypeBo;
import com.mds.aiotplayer.cm.model.Synchronize;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

@WebService
public interface SynchronizeManager extends GenericManager<Synchronize, Long> {
    /**
     * Saves a synchronize's information
     *
     * @param synchronize the synchronize's information
     * @return updated synchronize
     * @throws RecordExistsException thrown when synchronize already exists
     */
    Synchronize saveSynchronize(Synchronize synchronize) throws RecordExistsException;
    
    List<Synchronize> getSynchronizes(long galleryId);

	void removeSynchronize(Long id) ;

	Response removeSynchronize(final String synchronizeIds);
}