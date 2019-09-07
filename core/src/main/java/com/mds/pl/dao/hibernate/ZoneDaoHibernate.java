package com.mds.pl.dao.hibernate;

import com.mds.pl.model.Zone;
import com.mds.pl.dao.ZoneDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("zoneDao")
public class ZoneDaoHibernate extends GenericDaoHibernate<Zone, Long> implements ZoneDao {

    public ZoneDaoHibernate() {
        super(Zone.class);
    }
}
