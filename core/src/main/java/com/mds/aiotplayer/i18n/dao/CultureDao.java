/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.i18n.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.i18n.model.Culture;

/**
 * An interface that provides a data management interface to the Cultures table.
 */
public interface CultureDao extends GenericDao<Culture, Long> {
	/**
     * Saves a culture's information.
     * @param culture the object to be saved
     * @return the persisted Culture object
     */
    Culture saveCulture(Culture culture);
}