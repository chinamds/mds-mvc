/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pl.dao.hibernate;

import com.mds.aiotplayer.pl.model.Zone;
import com.mds.aiotplayer.pl.dao.ZoneDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("zoneDao")
public class ZoneDaoHibernate extends GenericDaoHibernate<Zone, Long> implements ZoneDao {

    public ZoneDaoHibernate() {
        super(Zone.class);
    }
}
