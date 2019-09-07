package com.mds.cm.dao.hibernate;

import com.mds.cm.model.DailyListZone;
import com.mds.cm.dao.DailyListZoneDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository("dailyListZoneDao")
public class DailyListZoneDaoHibernate extends GenericDaoHibernate<DailyListZone, Long> implements DailyListZoneDao {

    public DailyListZoneDaoHibernate() {
        super(DailyListZone.class);
    }
    
    public List<DailyListZone> getDailyListZones(long dailyListId){
    	return getSession().createCriteria(DailyListZone.class).add(Restrictions.eq("dailyListItem.dailyList.id", dailyListId)).list();
    }
}
