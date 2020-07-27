package com.mds.aiotplayer.ps.dao.hibernate;

import com.mds.aiotplayer.ps.model.PlayerTuner;
import com.mds.aiotplayer.ps.dao.PlayerTunerDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("playerTunerDao")
public class PlayerTunerDaoHibernate extends GenericDaoHibernate<PlayerTuner, Long> implements PlayerTunerDao {

    public PlayerTunerDaoHibernate() {
        super(PlayerTuner.class);
    }
}
