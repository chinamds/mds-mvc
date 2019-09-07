package com.mds.sys.dao.hibernate;

import com.mds.sys.model.Organization;
import com.mds.sys.dao.OrganizationDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("organizationDao")
public class OrganizationDaoHibernate extends GenericDaoHibernate<Organization, Long> implements OrganizationDao {

    public OrganizationDaoHibernate() {
        super(Organization.class);
    }

	/**
     * {@inheritDoc}
     */
    public Organization saveOrganization(Organization organization) {
        if (log.isDebugEnabled()) {
            log.debug("organization's id: " + organization.getId());
        }
        getSession().saveOrUpdate(preSave(organization));
        // necessary to throw a DataIntegrityViolation and catch it in OrganizationManager
        getSession().flush();
        return organization;
    }
}
