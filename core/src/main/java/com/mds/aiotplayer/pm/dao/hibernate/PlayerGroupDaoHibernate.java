/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pm.dao.hibernate;

import com.mds.aiotplayer.pm.model.PlayerGroup;
import com.mds.aiotplayer.pm.dao.PlayerGroupDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("playerGroupDao")
public class PlayerGroupDaoHibernate extends GenericDaoHibernate<PlayerGroup, Long> implements PlayerGroupDao {

    public PlayerGroupDaoHibernate() {
        super(PlayerGroup.class);
    }

	/**
     * {@inheritDoc}
     */
    public PlayerGroup savePlayerGroup(PlayerGroup playerGroup) {
        if (log.isDebugEnabled()) {
            log.debug("playerGroup's id: " + playerGroup.getId());
        }
        var result = super.save(playerGroup);
        // necessary to throw a DataIntegrityViolation and catch it in PlayerGroupManager
        getEntityManager().flush();
        return playerGroup;
    }
    
    /**
     * {@inheritDoc}
     */
    public PlayerGroup addPlayerGroup(PlayerGroup playerGroup) {
        var result = super.save(playerGroup);
        // necessary to throw a DataIntegrityViolation and catch it in PlayerGroupManager
        getEntityManager().flush();
        
        return result;
    }
    
    /**
     * Overridden simply to call the savePlayerGroup method. This is happening
     * because savePlayerGroup flushes the session and saveObject of BaseDaoHibernate
     * does not.
     *
     * @param playerGroup the playerGroup to save
     * @return the modified playerGroup (with a primary key set if they're new)
     */
    @Override
    public PlayerGroup save(PlayerGroup playerGroup) {
        return this.savePlayerGroup(playerGroup);
    } 
    
    /*public String getMaxRefNo(final String appointmentItem){
    	Pageable pageable = PageRequest.of(0, 1);
    	Page<String> refs = find(pageable, "select refNo from PlayerGroup where apptDatePeriod.apptItemRange.code=:p1 order by refNo desc", new Parameter(appointmentItem));
    	if (refs.hasContent()) {
    		return refs.getContent().get(0);
    	}
    	
    	return null;
	}
    
    public List<Map<String,Object>> getItemRangeAppointment(final List<Long> apptItemRangeIds){
    	List<Map<String,Object>> result = find("select new map(apptDatePeriod.apptItemRange.id as apptItemRangeId, count(*) as appointed) from PlayerGroup where apptDatePeriod.apptItemRange.id in :p1 group by apptDatePeriod.apptItemRange.id", new Parameter(apptItemRangeIds));
    	
    	return result;
    }*/
}
