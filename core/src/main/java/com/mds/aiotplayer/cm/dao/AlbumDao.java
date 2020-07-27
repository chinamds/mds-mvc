package com.mds.aiotplayer.cm.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.cm.model.Album;

/**
 * An interface that provides a data management interface to the Album table.
 */
public interface AlbumDao extends GenericDao<Album, Long> {
	/**
     * Saves a album's information.
     * @param album the object to be saved
     * @return the persisted Album object
     */
    Album saveAlbum(Album album);
}