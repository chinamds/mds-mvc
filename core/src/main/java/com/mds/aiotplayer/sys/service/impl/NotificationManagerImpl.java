package com.mds.aiotplayer.sys.service.impl;

import com.mds.aiotplayer.sys.dao.NotificationDao;
import com.mds.aiotplayer.sys.model.MessageAction;
import com.mds.aiotplayer.sys.model.MessageFolder;
import com.mds.aiotplayer.sys.model.Notification;
import com.mds.aiotplayer.sys.model.RecipientType;
import com.mds.aiotplayer.sys.service.NotificationManager;
import com.mds.aiotplayer.sys.service.NotificationService;
import com.mds.aiotplayer.util.ConvertUtil;
import com.mds.aiotplayer.util.DateUtils;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.model.search.filter.SearchFilter;
import com.mds.aiotplayer.common.model.search.filter.SearchFilterHelper;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.jws.WebService;
import javax.ws.rs.core.Response;

@Service("notificationManager")
@WebService(serviceName = "NotificationService", endpointInterface = "com.mds.aiotplayer.sys.service.NotificationService")
public class NotificationManagerImpl extends GenericManagerImpl<Notification, Long> implements NotificationManager, NotificationService {
    NotificationDao notificationDao;

    @Autowired
    public NotificationManagerImpl(NotificationDao notificationDao) {
        super(notificationDao);
        this.notificationDao = notificationDao;
    }
    
    @Transactional
    @Override
    public void markReadAll(final Long userId) {
    	notificationDao.markReadAll(userId);
    }


    @Transactional
    @Override
    public void markRead(final Long notificationId) {
        Notification data = get(notificationId);
        if(data == null || data.getRead().equals(Boolean.TRUE)) {
            return;
        }
        data.setRead(Boolean.TRUE);
        save(data);
    }    
    
     
    /**
     * {@inheritDoc}
     */
    @Override
    public Notification getNotification(final String notificationId) {
        return notificationDao.get(new Long(notificationId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Notification> getNotifications() {
        return notificationDao.getAllDistinct();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Notification> getShowNotifications(Integer limit, Integer offset){
    	Searchable searchable = Searchable.newSearchable();
		searchable.addSearchFilter("show", SearchOperator.eq, true);	
		searchable.addSearchFilter("dateFrom", SearchOperator.lte, Calendar.getInstance().getTime());
		//searchable.addSearchFilter("dateTo", SearchOperator.gte, Calendar.getInstance().getTime());
		
		 //sender
        SearchFilter dateToFilter = SearchFilterHelper.newCondition("dateTo", SearchOperator.gte, Calendar.getInstance().getTime());
        SearchFilter dateToFilter2 = SearchFilterHelper.newCondition("dateTo", SearchOperator.isNull, null);
        SearchFilter and1 = SearchFilterHelper.or(dateToFilter, dateToFilter2);
        searchable.addSearchFilter(and1);

		if (limit<0){
			return notificationDao.findAll(searchable);
		}else {
			searchable.setPage(PageRequest.of(offset/limit, limit));
			return notificationDao.find(searchable).getContent();
		}
    }
    
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<Notification> searchNotifications(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return notificationDao.search(pageable, new String[]{"content", "faceDefine.code"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> notificationsSelect2(String searchTerm, Integer limit, Integer offset) {
    	if (StringUtils.isBlank(searchTerm))
    		return null;
    	
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return toSelect2Data(notificationDao.search(pageable, new String[]{"content", "faceDefine.code"}, searchTerm).getContent());
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> notificationsTable(String userId, String searchTerm, Integer limit, Integer offset) {
    	Searchable searchable = Searchable.newSearchable();
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	searchable.setPage(pageable);
    	
    	Page<Notification> list =  null;
    	if (StringUtils.isBlank(searchTerm)) {
        	searchable.addSearchFilter("user.id", SearchOperator.eq, new Long(userId));
            
        	list = notificationDao.find(searchable);
        }else {
        	searchable.addSearchFilter("userId", SearchOperator.ftqFilter, userId, "notificationUser");
            
        	list = notificationDao.search(searchable, searchTerm);	
        }
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", list.getContent());
		
		return resultData;
		
    }   

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Notification saveNotification(final Notification notification) throws RecordExistsException {       
        try {
            return notificationDao.saveNotification(notification);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("Notification '" + notification.getContent() + "' already exists!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Response removeNotification(final String notificationIds) {
        log.debug("removing notification: " + notificationIds);
        notificationDao.remove(ConvertUtil.StringtoLongArray(notificationIds));
        
        log.info("Content Mapping(id=" + notificationIds + ") was successfully deleted.");
        return Response.ok().build();
    }
    
    /**
	 * convert notification data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param notifications
	 * @return
	 */
	private  HashMap<String,Object> toSelect2Data(List<Notification> notifications){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (Notification u : notifications) {
			//notification list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getContent());//notification name
			mapData.put("selected", false);//status
			mapData.put("id", u.getId());//notification id
			list.add(mapData);
		}
		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
		resultData.put("results", list);
		
		return resultData;
	}
}