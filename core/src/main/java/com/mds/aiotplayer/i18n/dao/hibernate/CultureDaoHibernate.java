/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.i18n.dao.hibernate;

import com.mds.aiotplayer.i18n.model.Culture;
import com.mds.aiotplayer.i18n.dao.CultureDao;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository("culturesDao")
public class CultureDaoHibernate extends GenericDaoHibernate<Culture, Long> implements CultureDao {

    public CultureDaoHibernate() {
        super(Culture.class);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Culture> getAll() {
		try {
			CriteriaQuery criteriaQuery = getCriteriaQuery(getCriteriaBuilder(), Culture.class);
			Root<Culture> root = criteriaQuery.from(Culture.class);
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
    public Culture saveCulture(Culture culture) {
        if (log.isDebugEnabled()) {
            log.debug("culture's id: " + culture.getId());
        }
        var result = super.save(culture);
        // necessary to throw a DataIntegrityViolation and catch it in CultureManager
        getEntityManager().flush();
        return result;
    }
}
