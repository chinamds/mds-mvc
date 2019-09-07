package com.mds.hrm.dao.hibernate;

import com.mds.hrm.model.Department;
import com.mds.hrm.dao.DepartmentDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("departmentDao")
public class DepartmentDaoHibernate extends GenericDaoHibernate<Department, Long> implements DepartmentDao {

    public DepartmentDaoHibernate() {
        super(Department.class);
    }
}
