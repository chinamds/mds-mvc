/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pm.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.pm.model.PlayerGroup;

/**
 * An interface that provides a data management interface to the PlayerGroup table.
 */
public interface PlayerGroupDao extends GenericDao<PlayerGroup, Long> {
	/**
     * Saves a playerGroup's information.
     * @param playerGroup the object to be saved
     * @return the persisted PlayerGroup object
     */
    PlayerGroup savePlayerGroup(PlayerGroup playerGroup);
    
    /**
     * add a playerGroup's information.
     * @param playerGroup the object to be saved
     * @return the persisted PlayerGroup object
     */
    PlayerGroup addPlayerGroup(PlayerGroup playerGroup);
    
    /*String getMaxRefNo(final String appointmentItem);
    
    List<Map<String,Object>> getItemRangeAppointment(final List<Long> apptItemRangeIds);*/
}