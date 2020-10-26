/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.DailyListItem;
import com.mds.aiotplayer.cm.dao.DailyListItemDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("dailyListItemDao")
public class DailyListItemDaoHibernate extends GenericDaoHibernate<DailyListItem, Long> implements DailyListItemDao {

    public DailyListItemDaoHibernate() {
        super(DailyListItem.class);
    }
}
