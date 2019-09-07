package com.mds.hrm.dao.hibernate;

import com.mds.hrm.model.Staff;
import com.mds.hrm.dao.StaffDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("staffDao")
public class StaffDaoHibernate extends GenericDaoHibernate<Staff, Long> implements StaffDao {

    public StaffDaoHibernate() {
        super(Staff.class);
    }
}
