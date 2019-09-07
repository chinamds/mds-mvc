package com.mds.hrm.dao.hibernate;

import com.mds.hrm.model.StaffDepartment;
import com.mds.hrm.dao.StaffDepartmentDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("staffDepartmentDao")
public class StaffDepartmentDaoHibernate extends GenericDaoHibernate<StaffDepartment, Long> implements StaffDepartmentDao {

    public StaffDepartmentDaoHibernate() {
        super(StaffDepartment.class);
    }
}
