package com.mds.hrm.dao.hibernate;

import com.mds.hrm.model.Position;
import com.mds.hrm.dao.PositionDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("positionDao")
public class PositionDaoHibernate extends GenericDaoHibernate<Position, Long> implements PositionDao {

    public PositionDaoHibernate() {
        super(Position.class);
    }
}
