package com.mds.i18n.dao.hibernate;

import com.mds.i18n.model.NeutralResource;
import com.mds.i18n.dao.NeutralResourceDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("neutralResourceDao")
public class NeutralResourceDaoHibernate extends GenericDaoHibernate<NeutralResource, Long> implements NeutralResourceDao {

    public NeutralResourceDaoHibernate() {
        super(NeutralResource.class);
    }

	/**
     * {@inheritDoc}
     */
    public NeutralResource saveNeutralResource(NeutralResource neutralResource) {
        if (log.isDebugEnabled()) {
            log.debug("neutralResource's id: " + neutralResource.getId());
        }
        getSession().saveOrUpdate(preSave(neutralResource));
        // necessary to throw a DataIntegrityViolation and catch it in NeutralResourceManager
        getSession().flush();
        return neutralResource;
    }
}
