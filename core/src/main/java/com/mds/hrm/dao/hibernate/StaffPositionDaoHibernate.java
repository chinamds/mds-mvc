package com.mds.hrm.dao.hibernate;

import com.mds.hrm.model.StaffPosition;
import com.mds.hrm.dao.StaffPositionDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("staffPositionDao")
public class StaffPositionDaoHibernate extends GenericDaoHibernate<StaffPosition, Long> implements StaffPositionDao {

    public StaffPositionDaoHibernate() {
        super(StaffPosition.class);
    }
}
