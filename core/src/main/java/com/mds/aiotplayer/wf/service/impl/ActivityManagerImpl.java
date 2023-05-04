/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.wf.service.impl;

import com.mds.aiotplayer.wf.dao.ActivityDao;
import com.mds.aiotplayer.wf.dao.ActivityOrganizationUserDao;
import com.mds.aiotplayer.wf.model.Activity;
import com.mds.aiotplayer.wf.model.ActivityOrganizationUser;
import com.mds.aiotplayer.wf.service.ActivityManager;
import com.mds.aiotplayer.wf.service.ActivityService;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

import com.beust.jcommander.internal.Lists;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.model.search.exception.SearchException;
import com.mds.aiotplayer.common.model.search.filter.SearchFilter;
import com.mds.aiotplayer.common.model.search.filter.SearchFilterHelper;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.sys.dao.OrganizationDao;
import com.mds.aiotplayer.sys.dao.UserDao;
import com.mds.aiotplayer.sys.model.Organization;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.ConvertUtil;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.util.DateUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("activityManager")
@WebService(serviceName = "ActivityService", endpointInterface = "com.mds.aiotplayer.wf.service.ActivityService")
public class ActivityManagerImpl extends GenericManagerImpl<Activity, Long> implements ActivityManager, ActivityService {
    ActivityDao activityDao;
    OrganizationDao organizationDao;
    UserDao userDao;
    ActivityOrganizationUserDao activityOrganizationUserDao;

    @Autowired
    public ActivityManagerImpl(ActivityDao activityDao) {
        super(activityDao);
        this.activityDao = activityDao;
    }
    
    @Autowired
    public void setOrganizationDao(OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }
    
    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    
    @Autowired
    public void setActivityOrganizationUserDao(ActivityOrganizationUserDao activityOrganizationUserDao) {
        this.activityOrganizationUserDao = activityOrganizationUserDao;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public Activity getActivity(final String activityId) {
        return activityDao.get(new Long(activityId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Activity> getActivities() {
        return activityDao.getAllDistinct();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<Activity> searchActivities(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return activityDao.search(pageable, new String[]{"code", "description"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     * @throws InvalidMDSRoleException 
     * @throws SearchException 
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> activitiesSelect2(String searchTerm, Integer limit, Integer offset) throws SearchException, InvalidMDSRoleException {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.setPage(pageable);
        UserAccount user = UserUtils.getUser();
    	if (!user.isSystem()) {
    		searchable.addSearchFilter("organization.id", SearchOperator.in, UserUtils.getUserOrganizationIds(user.getUsername()));
    	}
    	if (!StringUtils.isBlank(searchTerm)) {
    		SearchFilter codeFilter = SearchFilterHelper.newCondition("code", SearchOperator.contain, searchTerm);
            SearchFilter descriptionFilter = SearchFilterHelper.newCondition("description", SearchOperator.contain, searchTerm);
            SearchFilter and1 = SearchFilterHelper.or(codeFilter, descriptionFilter);
            searchable.addSearchFilter(and1);
    	}
    		
    	return toSelect2Data(activityDao.find(searchable).getContent());
        //return toSelect2Data(activityDao.search(pageable, new String[]{"code", "description"}, searchTerm).getContent());
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> activitiesTable(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.setPage(pageable);
        
        Page<Activity> list =  null;
    	if (StringUtils.isBlank(searchTerm)){
    		searchable.addSort(Direction.DESC, "apptDate", "apptPeriod.timeFrom", "apptPeriod.apptItem.code");
    		list = activityDao.find(searchable);
    	}else {
    		searchable.addSort(Direction.DESC, "apptDate", "timeRange", "apptCode");
    		list = activityDao.search(searchable, searchTerm);
    	}
    	    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", toBootstrapTableData(list.getContent()));
		
		return resultData;
		
    }
    
    /**
     * {@inheritDoc}
     * @throws InvalidMDSRoleException 
     * @throws SearchException 
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> activitiesTable(String organizationId, String searchTerm, Integer limit, Integer offset) throws SearchException, InvalidMDSRoleException {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "organization.id", "code");
        searchable.setPage(pageable);
        
    	long oId = StringUtils.toLong(organizationId);
    	if (oId != Long.MIN_VALUE && oId != 0) {
			searchable.addSearchFilter("organization.id", SearchOperator.eq, oId);
		}else {
			UserAccount user = UserUtils.getUser();
	    	if (!user.isSystem()) {
	    		searchable.addSearchFilter("organization.id", SearchOperator.in, UserUtils.getUserOrganizationIds(user.getUsername()));
	    	}
		}
        
    	List<Activity> list =  null;
    	long totalElements = 0;
    	if (StringUtils.isBlank(searchTerm)){
    		Page<Activity> page = activityDao.find(searchable);
    		list = page.getContent();
    		totalElements = page.getTotalElements();
    	}else {
    		//searchable.addSearchFilter("code", SearchOperator.contain, searchTerm);
    		//searchable.addSearchFilter("description", SearchOperator.contain, searchTerm);
    		SearchFilter codeFilter = SearchFilterHelper.newCondition("code", SearchOperator.contain, searchTerm);
            SearchFilter descriptionFilter = SearchFilterHelper.newCondition("description", SearchOperator.contain, searchTerm);
            SearchFilter and1 = SearchFilterHelper.or(codeFilter, descriptionFilter);
            searchable.addSearchFilter(and1);
            
            Page<Activity> page = activityDao.find(searchable);
    		list = page.getContent();
    		totalElements = page.getTotalElements();
    		//totalElements = list.size();
    		//list = list.stream().skip(offset).limit(limit).collect(Collectors.toList());
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", totalElements);
		resultData.put("rows", toBootstrapTableData(list));
		
		return resultData;
		
    }
    
    /**
   	 * convert Activity data to Bootstrap Table Data format
   	 * @param activities
   	 * @return
        * @throws Exception 
   	 */
   	private  List<HashMap<String,Object>> toBootstrapTableData(List<Activity> activities){
   		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
   		for (Activity u : activities) {
   			//Organization's Workflow Type list		
   			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
   			mapData.put("code", u.getCode());//workflow type title
   			mapData.put("organizationCode", u.getOrganizationCode());//organization Code
   			mapData.put("id", u.getId());//role id
   			mapData.put("createdBy", u.getCreatedBy());
   			mapData.put("dateAdded", DateUtils.jsonSerializer(u.getDateAdded()));//create date
   			mapData.put("description", u.getDescription());//
   			list.add(mapData);
   		}
   				
   		return list;
   	}

    /**
     * {@inheritDoc}
     */
    @Override
    public Activity saveActivity(final Activity activity) throws RecordExistsException {
    	List<ActivityOrganizationUser> activityOrganizationUsers =activity.getActivityOrganizationUsers();
    	activity.setActivityOrganizationUsers(null);
    	boolean isNew = (activity.getId() == null);

        try {
        	Activity result = activityDao.saveActivity(activity);
        	if (!isNew) {
        		Searchable searchable = Searchable.newSearchable();
            	searchable.addSearchFilter("activity.id", SearchOperator.eq, activity.getId());	
        		List<ActivityOrganizationUser> existActivityOrganizationUsers = activityOrganizationUserDao.findAll(searchable);
        		if (!existActivityOrganizationUsers.isEmpty()) {
	        		for(ActivityOrganizationUser activityOrganizationUser : existActivityOrganizationUsers) {
	        			if (activityOrganizationUsers.isEmpty()) {
	        				activityOrganizationUserDao.remove(activityOrganizationUser.getId());
	        			}else {
		        			activityOrganizationUser.setUser(activityOrganizationUsers.get(0).getUser()); 
		        			activityOrganizationUser.setOrganization(activityOrganizationUsers.get(0).getOrganization());
		        			activityOrganizationUser.setActivity(result);
		        			activityOrganizationUserDao.save(activityOrganizationUser);
		        			activityOrganizationUsers.remove(0);
	        			}
	        		}
        		}
        	}
        	for(ActivityOrganizationUser activityOrganizationUser : activityOrganizationUsers) {
				activityOrganizationUser.setActivity(result);
				activityOrganizationUserDao.save(activityOrganizationUser);
			}
        	        	
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("Activity type: '" + activity.getCode() + "' already exists!");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Activity addActivity(final Activity activity) throws RecordExistsException {

        try {
            return activityDao.addActivity(activity);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("Activity type: '" + activity.getCode() + "' already exists!");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Activity userAppointment(String mobile, String idNumber, Activity activity) 
    		throws RecordExistsException {
		
		return saveActivity(activity);
	}
    
    /**
     * {@inheritDoc}
     * @throws InvalidMDSRoleException 

     */
    @SuppressWarnings("unchecked")
	@Override
    public List<Map<String,Object>> organizationsTreeView(String activityId, String organizationId, HttpServletRequest request) throws InvalidMDSRoleException {
    	Searchable searchable = Searchable.newSearchable();
    	//searchable.addSearchFilter("id", SearchOperator.ne, 1L);
    	searchable.addSearchFilter("available", SearchOperator.eq, true);	
        searchable.addSort(Direction.ASC, "parent.id", "code");
        long oId = StringUtils.toLong(organizationId);
        UserAccount user = UserUtils.getUser();
        if (!user.isSystem()) {
        	List<Long> userOrganizationIds = UserUtils.getUserOrganizationIds(user.getUsername());
        	if (oId != Long.MIN_VALUE && oId != 0) {
            	//List<Long> childOrganizationIds = UserUtils.getOrganizationChildren(oId);
            	userOrganizationIds = (List<Long>) CollectionUtils.intersection(userOrganizationIds, UserUtils.getOrganizationChildren(oId));
            	//searchable.addSearchFilter("id", SearchOperator.in, childOrganizationIds);
            }
        	searchable.addSearchFilter("id", SearchOperator.in, userOrganizationIds);
        }else {
        	if (oId != Long.MIN_VALUE && oId != 0) {
            	List<Long> childOrganizationIds = UserUtils.getOrganizationChildren(oId);
            	if (!childOrganizationIds.contains(oId)) {
            		childOrganizationIds.add(oId);
            	}
            	searchable.addSearchFilter("id", SearchOperator.in, childOrganizationIds);
            }
        }
        
        List<Organization> list = organizationDao.findAll(searchable);
        List<Organization> selectOrganizations = Lists.newArrayList();
        long aId = StringUtils.toLong(activityId);
        if (aId > 0) {
        	Activity activity = get(new Long(activityId));
        	selectOrganizations = activity.getActivityOrganizationUsers().stream().filter(a->a.getOrganization() != null).map(a->a.getOrganization()).collect(Collectors.toList());
        }
        List<Long> organizationIds = list.stream().map(c->c.getId()).collect(Collectors.toList());
        List<Organization> organizations = list.stream().filter(c->c.getParent() == null || c.getParent().getId() == null || !organizationIds.contains(c.getParent().getId())).collect(Collectors.toList());
        
        List<Map<String,Object>> resultData = new LinkedList<Map<String,Object>>();
		for (Organization organization : organizations) {
			resultData = toBSTreeData(list, organization, selectOrganizations, resultData);
		}

    	return resultData;
    }
    
    /**
     * {@inheritDoc}
     * @throws InvalidMDSRoleException 

     */
    @SuppressWarnings("unchecked")
	@Override
    public List<HashMap<String,Object>> usersDualListbox(String activityId, String organizationId, String organizationIds, String userIds, HttpServletRequest request) throws InvalidMDSRoleException {
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "username");
        long oId = StringUtils.toLong(organizationId);
        List<Long> oIds = Lists.newArrayList(ConvertUtil.stringtoLongArray(organizationIds));
        UserAccount user = UserUtils.getUser();
        if (!user.isSystem()) {
        	List<Long> userOrganizationIds = UserUtils.getUserOrganizationIds(user.getUsername());
        	if (oId != Long.MIN_VALUE && oId != 0) {
            	//List<Long> childOrganizationIds = UserUtils.getOrganizationChildren(oId);
            	userOrganizationIds = (List<Long>) CollectionUtils.intersection(userOrganizationIds, UserUtils.getOrganizationChildren(oId));
            	//searchable.addSearchFilter("id", SearchOperator.in, childOrganizationIds);
            }
        	userOrganizationIds.removeAll(oIds);
        	searchable.addSearchFilter("organization.id", SearchOperator.in, userOrganizationIds);
        }else {
        	if (oId != Long.MIN_VALUE && oId != 0) {
            	List<Long> childOrganizationIds = UserUtils.getOrganizationChildren(oId);
            	if (!childOrganizationIds.contains(oId)) {
            		childOrganizationIds.add(oId);
            	}
            	childOrganizationIds.removeAll(oIds);
            	searchable.addSearchFilter("organization.id", SearchOperator.in, childOrganizationIds);
            }
        }
                
        List<User> list = userDao.findAll(searchable);
        /*List<Long> selectOrganizations = Lists.newArrayList();
        long aId = StringUtils.toLong(activityId);
        if (aId > 0) {
        	Activity activity = get(new Long(activityId));
        	selectOrganizations = activity.getActivityOrganizationUsers().stream().filter(a->a.getUser() != null).map(a->a.getUser().getId()).collect(Collectors.toList());
        }*/
        List<Long> selectedUserIds = Lists.newArrayList(ConvertUtil.stringtoLongArray(userIds));
        
        List<HashMap<String,Object>> resultData = new LinkedList<HashMap<String,Object>>();
		for (User u : list) {
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
   			mapData.put("username", u.getUsername());//user name
   			mapData.put("organizationCode", u.getOrganizationCode());//organization Code
   			mapData.put("id", u.getId());//role id
			mapData.put("selected", selectedUserIds.contains(u.getId()));

   			resultData.add(mapData);
		}

    	return resultData;
    }
    
    /**
	 * convert organization permission data to bootstarp tree format
	 * @param albums
	 * @return
	 */
	private  List<Map<String,Object>> toBSTreeData(List<Organization> organizations, Organization organization, List<Organization> activityOrganizations, List<Map<String,Object>> resultData){
		//menu function list
		Map<String,Object> map = new LinkedHashMap<String, Object>();
		map.put("text", organization.getCode());//album name
		map.put("href", "javascript:void(0)");//link
		map.put("id", organization.getId());//album id
		Map<String,Object> mapState = new LinkedHashMap<String, Object>();
		mapState.put("checked", activityOrganizations.contains(organization));
		map.put("state", mapState);
		
		List<Organization> ps = organizations.stream().filter(c->c.getParentId() == organization.getId()).collect(Collectors.toList());
		List<Map<String,Object>> list = new LinkedList<Map<String,Object>>();
		if(null != ps && ps.size() > 0){
			//child menufunctions
			for (Organization up : ps) {
				list = toBSTreeData(organizations, up, activityOrganizations, list);
			}
		}
		
		map.put("tags",  new Integer[]{ps.size()});//show child data size
		if (!list.isEmpty())
			map.put("nodes", list);

		resultData.add(map);
		
		return resultData;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public Response removeActivity(final String activityIds) throws WebApplicationException{
        log.debug("removing activity: " + activityIds);
        try {
        	activityDao.remove(ConvertUtil.stringtoLongArray(activityIds));
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Face Define(id=" + activityIds + ") was successfully deleted.");
        return Response.ok().build();
    }
    
    /**
     * {@inheritDoc}
     */
/*    @Override
    public Response changeActivityStatus(String activityId, String activityStatus) {
    	try {
        	Activity activity = activityDao.get(new Long(activityId));
        	if (activity != null){
        		activity.setActivityType(ActivityType.valueOf(activityStatus));
        		activityDao.save(activity);
        	}
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    	
    	return Response.ok().build();
    }*/
        
    /**
	 * convert activity data to select2 format(https://select2.org/data-sources/formats)
	 * {
	 *	  "results": [
	 *	    {
	 *	      "id": 1,
	 *	      "text": "Option 1"
	 *    	},
	 *	    {
	 *	      "id": 2,
	 *	      "text": "Option 2",
	 *	      "selected": true
	 *	    },
	 *	    {
	 *	      "id": 3,
	 *	      "text": "Option 3",
	 *	      "disabled": true
	 *	    }
	 *	  ]
	 *	}
	 * @param activities
	 * @return
	 */
	private  HashMap<String,Object> toSelect2Data(List<Activity> activities){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (Activity u : activities) {
			//activity list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getCode());//activity name
			mapData.put("selected", false);//status
			mapData.put("id", u.getId());//activity id
			list.add(mapData);
		}
		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
		resultData.put("results", list);
		
		return resultData;
	}
	
	@Override
    public Response getQRCode(String refNo){
		Searchable searchable = Searchable.newSearchable();
	    searchable.addSearchFilter("refNo", SearchOperator.eq, refNo);
	    Activity activity = activityDao.findOne(searchable);
        // uncomment line below to send non-streamed
		 //QRCode.from("Hello World").to(ImageType.PNG).writeTo(outputStream);

        //return Response.ok(QRCode.from(activity.getRefNo()).withSize(250, 250).stream()).build();
        // uncomment line below to send streamed
        // return Response.ok(new ByteArrayInputStream(imageData)).build();
	    ByteArrayInputStream bas = null;
    	try {
	    	File file =QRCode.from(activity.getCode()).to(ImageType.PNG).withSize(250, 250).file();
	    	bas =new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
    	} catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        
        return Response.ok(bas).build();
    }
}