package com.mds.common.dao.hibernate;

import com.mds.common.model.State;
import com.mds.common.dao.StateDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("stateDao")
public class StateDaoHibernate extends GenericDaoHibernate<State, Long> implements StateDao {

    public StateDaoHibernate() {
        super(State.class);
    }
}
