package com.mds.aiotplayer.i18n.dao.hibernate;

import com.mds.aiotplayer.common.model.Parameter;
import com.mds.aiotplayer.i18n.model.LocalizedResource;
import com.mds.aiotplayer.i18n.dao.LocalizedResourceDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository("localizedResourceDao")
public class LocalizedResourceDaoHibernate extends GenericDaoHibernate<LocalizedResource, Long> implements LocalizedResourceDao {

    public LocalizedResourceDaoHibernate() {
        super(LocalizedResource.class);
    }
    
    public List<LocalizedResource> findByCultureId(Long cultureId){
    	return find("from LocalizedResource where culture.id = :p1", new Parameter(cultureId));
    }
    
    public List<Map<Long, Long>> findNeutralMap(Long cultureId){
    	return find("select new map(neutralResource.id as neutralResourceId, id) from LocalizedResource where culture.id = :p1", new Parameter(cultureId));
    }
    
    public List<Long> findNeutralIds(Long cultureId){
    	return find("select neutralResource.id from LocalizedResource where culture.id = :p1", new Parameter(cultureId));
    }

	/**
     * {@inheritDoc}
     */
    public LocalizedResource saveLocalizedResource(LocalizedResource localizedResource) {
        if (log.isDebugEnabled()) {
            log.debug("localizedResource's id: " + localizedResource.getId());
        }
        var result = super.save(preSave(localizedResource));
        // necessary to throw a DataIntegrityViolation and catch it in LocalizedResourceManager
        getEntityManager().flush();
        return result;
    }
}
