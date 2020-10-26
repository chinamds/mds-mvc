/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.Synchronize;
import com.mds.aiotplayer.cm.dao.SynchronizeDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
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
        var result = super.save(synchronize);
        // necessary to throw a DataIntegrityViolation and catch it in SynchronizeManager
        getEntityManager().flush();
        return result;
    }
}
