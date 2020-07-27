package com.mds.aiotplayer.i18n.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.i18n.model.NeutralResource;

/**
 * An interface that provides a data management interface to the NeutralResource table.
 */
public interface NeutralResourceDao extends GenericDao<NeutralResource, Long> {
	/**
     * Saves a NeutralResource's information.
     * @param neutralResource the object to be saved
     * @return the persisted NeutralResource object
     */
    NeutralResource saveNeutralResource(NeutralResource neutralResource);
}