package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.DailyListZone;
import com.mds.aiotplayer.cm.dao.DailyListZoneDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository("dailyListZoneDao")
public class DailyListZoneDaoHibernate extends GenericDaoHibernate<DailyListZone, Long> implements DailyListZoneDao {

    public DailyListZoneDaoHibernate() {
        super(DailyListZone.class);
    }
    
    public List<DailyListZone> getDailyListZones(long dailyListId){
    	CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(DailyListZone.class);
        Root from = criteriaQuery.from(DailyListZone.class);
        criteriaQuery.select(from);
		criteriaQuery.where(criteriaBuilder.equal(from.get("dailyListItem.dailyList.id"), dailyListId));
		Query query = this.getEntityManager().createQuery(criteriaQuery);
		
    	return query.getResultList();
    }
}
