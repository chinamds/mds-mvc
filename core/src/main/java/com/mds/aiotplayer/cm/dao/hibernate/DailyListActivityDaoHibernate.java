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
