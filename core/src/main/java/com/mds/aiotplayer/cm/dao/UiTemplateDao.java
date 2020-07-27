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