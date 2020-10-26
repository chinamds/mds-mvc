/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.DailyListActivity;
import com.mds.aiotplayer.cm.dao.DailyListActivityDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("dailyListActivityDao")
public class DailyListActivityDaoHibernate extends GenericDaoHibernate<DailyListActivity, Long> implements DailyListActivityDao {

    public DailyListActivityDaoHibernate() {
        super(DailyListActivity.class);
    }
}
