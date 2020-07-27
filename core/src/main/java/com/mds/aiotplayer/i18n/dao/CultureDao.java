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