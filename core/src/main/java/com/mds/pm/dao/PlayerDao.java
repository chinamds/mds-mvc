package com.mds.pm.dao;

import com.mds.common.dao.GenericDao;

import com.mds.pm.model.Player;

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