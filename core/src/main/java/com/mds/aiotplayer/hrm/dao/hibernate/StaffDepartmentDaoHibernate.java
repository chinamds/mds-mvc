package com.mds.aiotplayer.hrm.dao.hibernate;

import com.mds.aiotplayer.hrm.model.StaffDepartment;
import com.mds.aiotplayer.hrm.dao.StaffDepartmentDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("staffDepartmentDao")
public class StaffDepartmentDaoHibernate extends GenericDaoHibernate<StaffDepartment, Long> implements StaffDepartmentDao {

    public StaffDepartmentDaoHibernate() {
        super(StaffDepartment.class);
    }
}
