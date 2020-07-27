package com.mds.aiotplayer.common.dao.hibernate;

import com.mds.aiotplayer.common.model.State;
import com.mds.aiotplayer.common.dao.StateDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("stateDao")
public class StateDaoHibernate extends GenericDaoHibernate<State, Long> implements StateDao {

    public StateDaoHibernate() {
        super(State.class);
    }
}
