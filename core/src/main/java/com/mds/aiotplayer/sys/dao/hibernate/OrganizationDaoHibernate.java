/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao.hibernate;

import com.mds.aiotplayer.sys.model.Organization;
import com.mds.aiotplayer.sys.dao.OrganizationDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
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
        var result = super.save(preSave(organization));
        // necessary to throw a DataIntegrityViolation and catch it in OrganizationManager
        getEntityManager().flush();
        return result;
    }
}
