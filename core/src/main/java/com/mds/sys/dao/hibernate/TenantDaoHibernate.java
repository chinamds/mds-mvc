package com.mds.sys.dao.hibernate;

import com.mds.sys.model.Tenant;
import com.mds.sys.dao.TenantDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
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
        getSession().saveOrUpdate(log);
        // necessary to throw a DataIntegrityViolation and catch it in TenantManager
        getSession().flush();
        return log;
    }
}
