package com.mds.pm.dao.hibernate;

import com.mds.pm.model.PlayerMapping;
import com.mds.pm.dao.PlayerMappingDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("playerMappingDao")
public class PlayerMappingDaoHibernate extends GenericDaoHibernate<PlayerMapping, Long> implements PlayerMappingDao {

    public PlayerMappingDaoHibernate() {
        super(PlayerMapping.class);
    }
}
