package com.mds.hrm.dao.hibernate;

import com.mds.hrm.model.IdentityType;
import com.mds.hrm.dao.IdentityTypeDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("identityTypeDao")
public class IdentityTypeDaoHibernate extends GenericDaoHibernate<IdentityType, Long> implements IdentityTypeDao {

    public IdentityTypeDaoHibernate() {
        super(IdentityType.class);
    }
}
