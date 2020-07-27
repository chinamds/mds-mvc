package com.mds.aiotplayer.wf.service;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.wf.model.Activity;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

//@WebService
public interface ActivityManager extends GenericManager<Activity, Long> {
    Response removeActivity(String activityIds);
	
	Activity saveActivity(Activity activity) throws RecordExistsException;
	Activity addActivity(Activity activity) throws RecordExistsException;
	
	Activity userAppointment(String mobile, String idNumber, Activity activity) throws RecordExistsException;
	
	/*Response changeActivityStatus(String activityId, String activityStatus);*/  
}