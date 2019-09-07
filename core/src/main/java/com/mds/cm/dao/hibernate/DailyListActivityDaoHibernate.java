package com.mds.cm.dao.hibernate;

import com.mds.cm.model.DailyListActivity;
import com.mds.cm.dao.DailyListActivityDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("dailyListActivityDao")
public class DailyListActivityDaoHibernate extends GenericDaoHibernate<DailyListActivity, Long> implements DailyListActivityDao {

    public DailyListActivityDaoHibernate() {
        super(DailyListActivity.class);
    }
}
