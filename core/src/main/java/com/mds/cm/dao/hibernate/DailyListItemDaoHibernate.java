package com.mds.cm.dao.hibernate;

import com.mds.cm.model.DailyListItem;
import com.mds.cm.dao.DailyListItemDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("dailyListItemDao")
public class DailyListItemDaoHibernate extends GenericDaoHibernate<DailyListItem, Long> implements DailyListItemDao {

    public DailyListItemDaoHibernate() {
        super(DailyListItem.class);
    }
}
