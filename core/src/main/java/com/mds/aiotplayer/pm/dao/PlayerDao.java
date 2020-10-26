/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pm.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.pm.model.Player;

/**
 * An interface that provides a data management interface to the Player table.
 */
public interface PlayerDao extends GenericDao<Player, Long> {
	/**
     * Saves a player's information.
     * @param player the object to be saved
     * @return the persisted Player object
     */
    Player savePlayer(Player player);
    
    /**
     * add a player's information.
     * @param player the object to be saved
     * @return the persisted Player object
     */
    Player addPlayer(Player player);
    
    /*String getMaxRefNo(final String appointmentItem);
    
    List<Map<String,Object>> getItemRangeAppointment(final List<Long> apptItemRangeIds);*/
}