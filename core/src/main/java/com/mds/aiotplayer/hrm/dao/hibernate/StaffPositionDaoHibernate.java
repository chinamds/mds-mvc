package com.mds.aiotplayer.hrm.dao.hibernate;

import com.mds.aiotplayer.hrm.model.StaffPosition;
import com.mds.aiotplayer.hrm.dao.StaffPositionDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("staffPositionDao")
public class StaffPositionDaoHibernate extends GenericDaoHibernate<StaffPosition, Long> implements StaffPositionDao {

    public StaffPositionDaoHibernate() {
        super(StaffPosition.class);
    }
}
