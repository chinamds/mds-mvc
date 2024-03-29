/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.i18n.dao.hibernate;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import com.mds.aiotplayer.common.model.Parameter;
import com.mds.aiotplayer.i18n.dao.LocalizedResourceDao;
import com.mds.aiotplayer.i18n.model.LocalizedResource;

@Repository("localizedResourceDao")
public class LocalizedResourceDaoHibernate extends GenericDaoHibernate<LocalizedResource, Long> implements LocalizedResourceDao {

    public LocalizedResourceDaoHibernate() {
        super(LocalizedResource.class);
    }
    
    public List<LocalizedResource> findByCultureId(Long cultureId){
    	return find("from LocalizedResource l where l.culture.id = :p1", new Parameter(cultureId));
    }
    
    public List<LocalizedResource> findByCultureCode(String cultureCode){
    	return find("from LocalizedResource l where l.culture.cultureCode = :p1", new Parameter(cultureCode));
    }
    
    public List<Map<Long, Long>> findNeutralMap(Long cultureId){
    	return find("select new map(l.neutralResource.id as neutralResourceId, l.id) from LocalizedResource l where l.culture.id = :p1", new Parameter(cultureId));
    }
    
    public List<Long> findNeutralIds(Long cultureId){
    	return find("select l.neutralResource.id from LocalizedResource l where l.culture.id = :p1", new Parameter(cultureId));
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
