package com.mds.hrm.dao.hibernate;

import com.mds.hrm.model.StaffIdentity;
import com.mds.hrm.dao.StaffIdentityDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("staffIdentityDao")
public class StaffIdentityDaoHibernate extends GenericDaoHibernate<StaffIdentity, Long> implements StaffIdentityDao {

    public StaffIdentityDaoHibernate() {
        super(StaffIdentity.class);
    }
}
