package com.mds.aiotplayer.pm.dao.hibernate;

import com.mds.aiotplayer.pm.model.PlayerOutput;
import com.mds.aiotplayer.pm.dao.PlayerOutputDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("playerOutputDao")
public class PlayerOutputDaoHibernate extends GenericDaoHibernate<PlayerOutput, Long> implements PlayerOutputDao {

    public PlayerOutputDaoHibernate() {
        super(PlayerOutput.class);
    }
}
