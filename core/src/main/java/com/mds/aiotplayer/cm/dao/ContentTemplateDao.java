/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.cm.model.ContentTemplate;

/**
 * An interface that provides a data management interface to the ContentTemplate table.
 */
public interface ContentTemplateDao extends GenericDao<ContentTemplate, Long> {
	/**
     * Saves a contentTemplate's information.
     * @param contentTemplate the object to be saved
     * @return the persisted ContentTemplate object
     */
    ContentTemplate saveContentTemplate(ContentTemplate contentTemplate);
}