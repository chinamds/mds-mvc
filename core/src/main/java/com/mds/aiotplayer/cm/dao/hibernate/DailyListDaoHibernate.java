package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.DailyList;
import com.mds.aiotplayer.cm.dao.DailyListDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("dailyListDao")
public class DailyListDaoHibernate extends GenericDaoHibernate<DailyList, Long> implements DailyListDao {

    public DailyListDaoHibernate() {
        super(DailyList.class);
    }
}
