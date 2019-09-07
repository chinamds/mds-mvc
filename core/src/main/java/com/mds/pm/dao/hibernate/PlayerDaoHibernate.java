package com.mds.pm.dao.hibernate;

import com.mds.pm.model.Player;
import com.mds.pm.dao.PlayerDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("playerDao")
public class PlayerDaoHibernate extends GenericDaoHibernate<Player, Long> implements PlayerDao {

    public PlayerDaoHibernate() {
        super(Player.class);
    }

	/**
     * {@inheritDoc}
     */
    public Player savePlayer(Player player) {
        if (log.isDebugEnabled()) {
            log.debug("player's id: " + player.getId());
        }
        getSession().saveOrUpdate(player);
        // necessary to throw a DataIntegrityViolation and catch it in PlayerManager
        getSession().flush();
        return player;
    }
    
    /**
     * {@inheritDoc}
     */
    public Player addPlayer(Player player) {
        getSession().save(player);
        // necessary to throw a DataIntegrityViolation and catch it in PlayerManager
        getSession().flush();
        return player;
    }
    
    /**
     * Overridden simply to call the savePlayer method. This is happening
     * because savePlayer flushes the session and saveObject of BaseDaoHibernate
     * does not.
     *
     * @param player the player to save
     * @return the modified player (with a primary key set if they're new)
     */
    @Override
    public Player save(Player player) {
        return this.savePlayer(player);
    } 
    
    /*public String getMaxRefNo(final String appointmentItem){
    	Pageable pageable = PageRequest.of(0, 1);
    	Page<String> refs = find(pageable, "select refNo from Player where apptDatePeriod.apptItemRange.code=:p1 order by refNo desc", new Parameter(appointmentItem));
    	if (refs.hasContent()) {
    		return refs.getContent().get(0);
    	}
    	
    	return null;
	}
    
    public List<Map<String,Object>> getItemRangeAppointment(final List<Long> apptItemRangeIds){
    	List<Map<String,Object>> result = find("select new map(apptDatePeriod.apptItemRange.id as apptItemRangeId, count(*) as appointed) from Player where apptDatePeriod.apptItemRange.id in :p1 group by apptDatePeriod.apptItemRange.id", new Parameter(apptItemRangeIds));
    	
    	return result;
    }*/
}
