/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.i18n.dao.hibernate;

import com.mds.aiotplayer.i18n.model.NeutralResource;
import com.mds.aiotplayer.i18n.dao.NeutralResourceDao;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

@Repository("neutralResourceDao")
public class NeutralResourceDaoHibernate extends GenericDaoHibernate<NeutralResource, Long> implements NeutralResourceDao {

    public NeutralResourceDaoHibernate() {
        super(NeutralResource.class);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<NeutralResource> getAll() {
		try {
			CriteriaQuery criteriaQuery = getCriteriaQuery(getCriteriaBuilder(), NeutralResource.class);
			Root<NeutralResource> root = criteriaQuery.from(NeutralResource.class);
	        criteriaQuery.select(root);
	        
	        return executeCriteriaQuery(criteriaQuery, true, -1, -1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Lists.newArrayList();
    }

	/**
     * {@inheritDoc}
     */
    public NeutralResource saveNeutralResource(NeutralResource neutralResource) {
        if (log.isDebugEnabled()) {
            log.debug("neutralResource's id: " + neutralResource.getId());
        }
        var result = super.save(preSave(neutralResource));
        // necessary to throw a DataIntegrityViolation and catch it in NeutralResourceManager
        getEntityManager().flush();
        return result;
    }
}
