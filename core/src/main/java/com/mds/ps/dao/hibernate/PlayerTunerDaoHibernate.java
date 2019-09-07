package com.mds.ps.dao.hibernate;

import com.mds.ps.model.PlayerTuner;
import com.mds.ps.dao.PlayerTunerDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("playerTunerDao")
public class PlayerTunerDaoHibernate extends GenericDaoHibernate<PlayerTuner, Long> implements PlayerTunerDao {

    public PlayerTunerDaoHibernate() {
        super(PlayerTuner.class);
    }
}
