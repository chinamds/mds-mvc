/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao.hibernate;

import com.mds.aiotplayer.sys.model.OrganizationLogo;
import com.mds.aiotplayer.sys.dao.OrganizationLogoDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("organizationLogoDao")
public class OrganizationLogoDaoHibernate extends GenericDaoHibernate<OrganizationLogo, Long> implements OrganizationLogoDao {

    public OrganizationLogoDaoHibernate() {
        super(OrganizationLogo.class);
    }
}
