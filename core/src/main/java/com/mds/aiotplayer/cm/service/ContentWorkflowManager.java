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
import com.mds.aiotplayer.cm.content.MimeTypeBo;
import com.mds.aiotplayer.cm.model.ContentWorkflow;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

@WebService
public interface ContentWorkflowManager extends GenericManager<ContentWorkflow, Long> {
    /**
     * Saves a contentWorkflow's information
     *
     * @param contentWorkflow the contentWorkflow's information
     * @return updated contentWorkflow
     * @throws RecordExistsException thrown when contentWorkflow already exists
     */
    ContentWorkflow saveContentWorkflow(ContentWorkflow contentWorkflow) throws RecordExistsException;
    
    List<ContentWorkflow> getContentWorkflows(long galleryId);

	void removeContentWorkflow(Long id) ;

	Response removeContentWorkflow(final String contentWorkflowIds);
}