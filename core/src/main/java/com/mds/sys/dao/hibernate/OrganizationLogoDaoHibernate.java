package com.mds.sys.dao.hibernate;

import com.mds.sys.model.OrganizationLogo;
import com.mds.sys.dao.OrganizationLogoDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("organizationLogoDao")
public class OrganizationLogoDaoHibernate extends GenericDaoHibernate<OrganizationLogo, Long> implements OrganizationLogoDao {

    public OrganizationLogoDaoHibernate() {
        super(OrganizationLogo.class);
    }
}
