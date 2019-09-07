package com.mds.cm.dao.hibernate;

import com.mds.cm.model.Synchronize;
import com.mds.cm.dao.SynchronizeDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("synchronizeDao")
public class SynchronizeDaoHibernate extends GenericDaoHibernate<Synchronize, Long> implements SynchronizeDao {

    public SynchronizeDaoHibernate() {
        super(Synchronize.class);
    }

	/**
     * {@inheritDoc}
     */
    public Synchronize saveSynchronize(Synchronize synchronize) {
        if (log.isDebugEnabled()) {
            log.debug("synchronize's id: " + synchronize.getId());
        }
        getSession().saveOrUpdate(synchronize);
        // necessary to throw a DataIntegrityViolation and catch it in SynchronizeManager
        getSession().flush();
        return synchronize;
    }
}
