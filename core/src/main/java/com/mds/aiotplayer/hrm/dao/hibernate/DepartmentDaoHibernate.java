package com.mds.aiotplayer.hrm.dao.hibernate;

import com.mds.aiotplayer.hrm.model.Department;
import com.mds.aiotplayer.hrm.dao.DepartmentDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("departmentDao")
public class DepartmentDaoHibernate extends GenericDaoHibernate<Department, Long> implements DepartmentDao {

    public DepartmentDaoHibernate() {
        super(Department.class);
    }
}
