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
