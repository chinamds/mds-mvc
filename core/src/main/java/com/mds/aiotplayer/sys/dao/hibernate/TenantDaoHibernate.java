package com.mds.aiotplayer.sys.dao.hibernate;

import com.mds.aiotplayer.sys.model.Tenant;
import com.mds.aiotplayer.sys.dao.TenantDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("tenantDao")
public class TenantDaoHibernate extends GenericDaoHibernate<Tenant, String> implements TenantDao {

    public TenantDaoHibernate() {
        super(Tenant.class);
    }

	/**
     * {@inheritDoc}
     */
    public Tenant saveTenant(Tenant log) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("log's id: " + log.getId());
        }
        var result = super.save(log);
        // necessary to throw a DataIntegrityViolation and catch it in TenantManager
        getEntityManager().flush();
        return result;
    }
}
