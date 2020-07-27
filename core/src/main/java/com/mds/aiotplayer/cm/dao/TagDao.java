package com.mds.aiotplayer.cm.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.cm.model.Tag;

/**
 * An interface that provides a data management interface to the Tag table.
 */
public interface TagDao extends GenericDao<Tag, Long> {
	/**
     * Saves a tag's information.
     * @param tag the object to be saved
     * @return the persisted Tag object
     */
    Tag saveTag(Tag tag);
}