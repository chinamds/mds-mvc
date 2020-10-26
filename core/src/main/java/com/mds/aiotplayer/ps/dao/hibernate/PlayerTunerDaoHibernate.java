/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
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
