/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service;

import java.util.List;

import javax.ws.rs.core.Response;

import com.mds.aiotplayer.cm.model.UiTemplate;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.GenericManager;

public interface UiTemplateManager extends GenericManager<UiTemplate, Long> {
	
	/**
     * Retrieves a list of uiTemplates.
     * @return List
     */
    List<UiTemplate> getUiTemplate();
	
	/**
     * Saves a uiTemplate's information
     *
     * @param uiTemplate the uiTemplate's information
     * @return updated uiTemplate
     * @throws RecordExistsException thrown when uiTemplate already exists
     */
    UiTemplate saveUiTemplate(UiTemplate uiTemplate) throws RecordExistsException;

	void removeUiTemplate(Long id) ;

	Response removeUiTemplate(final String uiTemplateIds);
}