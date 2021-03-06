/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.cm.model.UiTemplate;

/**
 * An interface that provides a data management interface to the UiTemplate table.
 */
public interface UiTemplateDao extends GenericDao<UiTemplate, Long> {
	/**
     * Saves a uiTemplate's information.
     * @param uiTemplate the object to be saved
     * @return the persisted UiTemplate object
     */
    UiTemplate saveUiTemplate(UiTemplate uiTemplate);
}