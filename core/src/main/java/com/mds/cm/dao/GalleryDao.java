package com.mds.cm.dao;

import com.mds.common.dao.GenericDao;

import java.util.List;

import com.mds.cm.model.Gallery;

/**
 * An interface that provides a data management interface to the Gallery table.
 */
public interface GalleryDao extends GenericDao<Gallery, Long> {
	/**
     * Saves a gallery's information.
     * @param gallery the object to be saved
     * @return the persisted Gallery object
     */
    Gallery saveGallery(Gallery gallery);
    
    List<Gallery> findGalleries(long organizationId);
}