package com.mds.wf.dao;

import com.mds.common.dao.GenericDao;

import com.mds.wf.model.Activity;

/**
 * An interface that provides a data management interface to the Activity table.
 */
public interface ActivityDao extends GenericDao<Activity, Long> {
	/**
     * Saves a activity's information.
     * @param activity the object to be saved
     * @return the persisted Activity object
     */
    Activity saveActivity(Activity activity);
    
    /**
     * add a activity's information.
     * @param activity the object to be saved
     * @return the persisted Activity object
     */
    Activity addActivity(Activity activity);
    
    /*String getMaxRefNo(final String appointmentItem);
    
    List<Map<String,Object>> getItemRangeAppointment(final List<Long> apptItemRangeIds);*/
}