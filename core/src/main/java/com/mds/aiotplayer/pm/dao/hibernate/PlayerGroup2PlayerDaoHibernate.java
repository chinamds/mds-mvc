/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pm.dao.hibernate;

import com.mds.aiotplayer.pm.model.PlayerGroup2Player;
import com.mds.aiotplayer.pm.dao.PlayerGroup2PlayerDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("playerGroup2PlayerDao")
public class PlayerGroup2PlayerDaoHibernate extends GenericDaoHibernate<PlayerGroup2Player, Long> implements PlayerGroup2PlayerDao {

    public PlayerGroup2PlayerDaoHibernate() {
        super(PlayerGroup2Player.class);
    }
}
