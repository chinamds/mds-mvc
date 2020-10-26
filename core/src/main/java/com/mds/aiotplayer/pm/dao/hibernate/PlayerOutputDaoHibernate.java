/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
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
