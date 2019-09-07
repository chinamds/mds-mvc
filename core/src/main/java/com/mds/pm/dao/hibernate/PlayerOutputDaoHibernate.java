package com.mds.pm.dao.hibernate;

import com.mds.pm.model.PlayerOutput;
import com.mds.pm.dao.PlayerOutputDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("playerOutputDao")
public class PlayerOutputDaoHibernate extends GenericDaoHibernate<PlayerOutput, Long> implements PlayerOutputDao {

    public PlayerOutputDaoHibernate() {
        super(PlayerOutput.class);
    }
}
