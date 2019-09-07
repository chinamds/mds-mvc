package com.mds.cm.dao.hibernate;

import com.mds.cm.model.DailyList;
import com.mds.cm.dao.DailyListDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("dailyListDao")
public class DailyListDaoHibernate extends GenericDaoHibernate<DailyList, Long> implements DailyListDao {

    public DailyListDaoHibernate() {
        super(DailyList.class);
    }
}
