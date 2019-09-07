package com.mds.wf.dao.hibernate;

import com.mds.wf.model.Activity;
import com.mds.wf.dao.ActivityDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("activityDao")
public class ActivityDaoHibernate extends GenericDaoHibernate<Activity, Long> implements ActivityDao {

    public ActivityDaoHibernate() {
        super(Activity.class);
    }

	/**
     * {@inheritDoc}
     */
    public Activity saveActivity(Activity activity) {
        if (log.isDebugEnabled()) {
            log.debug("activity's id: " + activity.getId());
        }
        getSession().saveOrUpdate(preSave(activity));
        // necessary to throw a DataIntegrityViolation and catch it in ActivityManager
        getSession().flush();
        return activity;
    }
    
    /**
     * {@inheritDoc}
     */
    public Activity addActivity(Activity activity) {
        getSession().save(preSave(activity));
        // necessary to throw a DataIntegrityViolation and catch it in ActivityManager
        getSession().flush();
        return activity;
    }
    
    /**
     * Overridden simply to call the saveActivity method. This is happening
     * because saveActivity flushes the session and saveObject of BaseDaoHibernate
     * does not.
     *
     * @param activity the activity to save
     * @return the modified activity (with a primary key set if they're new)
     */
    @Override
    public Activity save(Activity activity) {
        return this.saveActivity(activity);
    } 
    
    /*public String getMaxRefNo(final String appointmentItem){
    	Pageable pageable = PageRequest.of(0, 1);
    	Page<String> refs = find(pageable, "select refNo from Activity where apptDatePeriod.apptItemRange.code=:p1 order by refNo desc", new Parameter(appointmentItem));
    	if (refs.hasContent()) {
    		return refs.getContent().get(0);
    	}
    	
    	return null;
	}
    
    public List<Map<String,Object>> getItemRangeAppointment(final List<Long> apptItemRangeIds){
    	List<Map<String,Object>> result = find("select new map(apptDatePeriod.apptItemRange.id as apptItemRangeId, count(*) as appointed) from Activity where apptDatePeriod.apptItemRange.id in :p1 group by apptDatePeriod.apptItemRange.id", new Parameter(apptItemRangeIds));
    	
    	return result;
    }*/
}
