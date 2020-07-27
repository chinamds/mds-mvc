package com.mds.aiotplayer.pm.dao.hibernate;

import com.mds.aiotplayer.pm.model.PlayerMapping;
import com.mds.aiotplayer.pm.dao.PlayerMappingDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("playerMappingDao")
public class PlayerMappingDaoHibernate extends GenericDaoHibernate<PlayerMapping, Long> implements PlayerMappingDao {

    public PlayerMappingDaoHibernate() {
        super(PlayerMapping.class);
    }
}
