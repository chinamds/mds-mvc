/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.cm.content.UiTemplateBo;
import com.mds.aiotplayer.cm.dao.DailyListDao;
import com.mds.aiotplayer.cm.dao.DailyListItemDao;
import com.mds.aiotplayer.cm.dao.DailyListWorkflowDao;
import com.mds.aiotplayer.cm.dao.DailyListZoneDao;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.model.ContentType;
import com.mds.aiotplayer.cm.model.DailyList;
import com.mds.aiotplayer.cm.model.DailyListActivity;
import com.mds.aiotplayer.cm.model.DailyListItem;
import com.mds.aiotplayer.cm.model.DailyListWorkflow;
import com.mds.aiotplayer.cm.model.DailyListZone;
import com.mds.aiotplayer.cm.rest.CMData;
import com.mds.aiotplayer.cm.rest.CMDataLoadOptions;
import com.mds.aiotplayer.cm.rest.SettingsRest;
import com.mds.aiotplayer.cm.service.DailyListManager;
import com.mds.aiotplayer.cm.service.DailyListService;
import com.mds.aiotplayer.cm.util.AlbumTreePickerBuilder;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.util.ContentObjectUtils;
import com.mds.aiotplayer.cm.util.GalleryUtils;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.model.Parameter;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.core.ApprovalAction;
import com.mds.aiotplayer.core.ApprovalStatus;
import com.mds.aiotplayer.core.ApprovalSwitch;
import com.mds.aiotplayer.core.CacheItem;
import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.core.UiTemplateType;
import com.mds.aiotplayer.core.WorkflowType;
import com.mds.aiotplayer.core.exception.BusinessException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.core.exception.WebException;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.sys.dao.UserDao;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.ConvertUtil;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.util.Utils;
import com.mds.aiotplayer.wf.dao.WorkflowDao;
import com.mds.aiotplayer.wf.dao.WorkflowDetailDao;
import com.mds.aiotplayer.wf.model.Workflow;
import com.mds.aiotplayer.wf.model.WorkflowDetail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Service("dailyListManager")
@WebService(serviceName = "DailyListService", endpointInterface = "com.mds.aiotplayer.service.DailyListService")
public class DailyListManagerImpl extends GenericManagerImpl<DailyList, Long> implements DailyListManager, DailyListService {
    DailyListDao dailyListDao;
    DailyListItemDao dailyListItemDao;
    DailyListZoneDao dailyListZoneDao;
    DailyListWorkflowDao dailyListWorkflowDao;
    
    WorkflowDao workflowDao;
    WorkflowDetailDao workflowDetailDao;
    UserDao userDao;

    @Autowired
    public DailyListManagerImpl(DailyListDao dailyListDao) {
        super(dailyListDao);
        this.dailyListDao = dailyListDao;
    }
    
    @Autowired
    public void setDailyListItemDao(DailyListItemDao dailyListItemDao) {
        this.dailyListItemDao = dailyListItemDao;
    }
     
    @Autowired
    public void setDailyListZoneDao(DailyListZoneDao dailyListZoneDao) {
        this.dailyListZoneDao = dailyListZoneDao;
    }
    
    @Autowired
    public void setWorkflowDao(WorkflowDao workflowDao) {
        this.workflowDao = workflowDao;
    }
    
    @Autowired
    public void setWorkflowDetailDao(WorkflowDetailDao workflowDetailDao) {
        this.workflowDetailDao = workflowDetailDao;
    }
    
    @Autowired
    public void setDailyListWorkflowDao(DailyListWorkflowDao dailyListWorkflowDao) {
        this.dailyListWorkflowDao = dailyListWorkflowDao;
    }
    
    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
     
    
    @Override
    public List<DailyList> getDailyLists(long galleryId){
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "parent.id");
        searchable.addSearchFilter("gallery.id", SearchOperator.eq, galleryId);
        
        return dailyListDao.findAll(searchable);
    }
    
    public List<Map<Long, Long>> findDailyListMap(long galleryId){
    	return dailyListDao.find("select new map(parent.id as parentId, id) from DailyList where gallery.id = :p1", new Parameter(galleryId));
    }
        
	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
	public void removeDailyList(Long id) {
		dailyListDao.remove(id);
	}

	/**
     * {@inheritDoc}
	 * @throws InvalidGalleryException 
	 * @throws UnsupportedContentObjectTypeException 
     */
    @Transactional
    @Override
    public DailyList saveDailyList(final DailyList dailyList) throws RecordExistsException, UnsupportedContentObjectTypeException, InvalidGalleryException {
    	boolean isNew = (dailyList.getId() == null);
    	boolean needApproval = false;
    	if (dailyList.getGallery() != null) {
    		needApproval = (CMUtils.loadGallerySetting(dailyList.getGallery().getId()).getApprovalSwitch() & ApprovalSwitch.dailylist.value()) > 0;
    	}
    	Searchable searchable = Searchable.newSearchable();
        if (!isNew) {
        	searchable.addSearchFilter("id", SearchOperator.ne, dailyList.getId());
        }
        if (Utils.isIndependentSpaceForDailyList()) {
        	searchable.addSearchFilter("gallery.id", SearchOperator.eq, dailyList.getGallery().getId());
        }else {
        	searchable.addSearchFilter("contentName", SearchOperator.eq, dailyList.getContentName());
        }
        searchable.addSearchFilter("date", SearchOperator.eq, dailyList.getDate());
        try {
        	if (dailyListDao.findAny(searchable)) {
        		throw new RecordExistsException("DailyList '" + DateUtils.formatDate(dailyList.getDate()) + "' already exists!");
        	}
        	
        	if (needApproval) {
        		dailyList.setApprovalStatus(ApprovalStatus.Waiting);
        	}
        	DailyList result = dailyListDao.save(dailyList);
        	if (needApproval && isNew) {
        		UserAccount user = UserUtils.getUser();
        		Workflow workflow = workflowDao.findApplyWorkflow(WorkflowType.DailyList, user.getId(), user.getOrganizationId());
        		if (workflow == null) {
        			throw new BusinessException("Workflow not define for '" + DateUtils.formatDate(dailyList.getDate()) + "'!");
        		}
        		
    			WorkflowDetail workflowDetail = workflow.getApplyStep();
    			if (workflowDetail == null) {
    				throw new BusinessException("Workflow details not define for '" + DateUtils.formatDate(dailyList.getDate()) + "'!");
    			}
    			
    			DailyListWorkflow dailyListWorkflow = new DailyListWorkflow(result, workflow, (short)0, (short)0);
    			dailyListWorkflow.setCurrentUser(UserUtils.getLoginName());
    			dailyListWorkflow.getDailyListActivities().add(new DailyListActivity(dailyListWorkflow, workflowDetail, userDao.get(user.getId()), ApprovalAction.Apply, UserUtils.getLoginName()));
    			WorkflowDetail nextWorkflowDetail = workflow.getNextStep(workflowDetail);
    			if (nextWorkflowDetail != null) {
    				dailyListWorkflow.getDailyListActivities().add(new DailyListActivity(dailyListWorkflow, nextWorkflowDetail, null, ApprovalAction.NotSpecified, UserUtils.getLoginName()));
    			}
			
    			dailyListWorkflowDao.save(dailyListWorkflow);
        	}
        	/*if (isNew) {
        		result =  dailyListDao.save(dailyList);
        	}else {
        		DailyList exists = dailyListDao.get(dailyList.getId());
        		for(DailyListItem dailyListItem : dailyList.getDailyListItems()) {
        			
        		}
        	}*/
        	
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("DailyList '" + DateUtils.formatDate(dailyList.getDate()) + "' already exists!");
        }
    }
    
    /**
     * {@inheritDoc}
	 * @throws InvalidGalleryException 
	 * @throws UnsupportedContentObjectTypeException 
     */
    @Transactional
    @Override
    public DailyList dailyListApprove(final DailyList dailyList, ApprovalAction approvalAction, String approvalOpinion) throws RecordExistsException, UnsupportedContentObjectTypeException, InvalidGalleryException {
        try {
        	DailyList dailyListSave = dailyListDao.get(dailyList.getId());
        	DailyListWorkflow dailyListWorkflow = dailyListWorkflowDao.findByDailyList(dailyList.getId());
        	DailyList result = dailyListApprove(dailyListSave, dailyListWorkflow.getWorkflow(), dailyListWorkflow, approvalAction, approvalOpinion);
        	
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException("DailyList '" + DateUtils.formatDate(dailyList.getDate()) + "' approval failure!");
        }
    }
    
    private DailyList dailyListApprove(final DailyList dailyList, Workflow workflow, DailyListWorkflow dailyListWorkflow, ApprovalAction approvalAction, String approvalOpinion) {
    	DailyListActivity currentActivity = dailyListWorkflow.getDailyListActivities().stream().filter(d->d.getApprovalAction() == ApprovalAction.NotSpecified).findFirst().orElse(null);
    	if (currentActivity == null) {
    		throw new BusinessException("Workflow not define for '" + DateUtils.formatDate(dailyList.getDate()) + "'!");
    	}
    	
    	UserAccount user = UserUtils.getUser();
		dailyList.setCurrentUser(user.getUsername());
		currentActivity.setRemark(approvalOpinion);
		currentActivity.setApprovalAction(approvalAction);
		currentActivity.setUser(userDao.get(user.getId()));
		if (approvalAction == ApprovalAction.Approve) {
    		WorkflowDetail currWorkflowDetail = workflow.getWorkflowDetails().stream().filter(w->w.getId() ==currentActivity.getWorkflowDetail().getId() ).findFirst().orElse(null);
    		WorkflowDetail nextWorkflowDetail = workflow.getNextStep(currWorkflowDetail);
    		if (nextWorkflowDetail == null) {
    			dailyList.setApprovalStatus(ApprovalStatus.Approved);
    		}else {
    			dailyList.setApprovalStatus(ApprovalStatus.Approving);
    			dailyListWorkflow.getDailyListActivities().add(new DailyListActivity(dailyListWorkflow, nextWorkflowDetail, null, ApprovalAction.NotSpecified, user.getUsername()));
    		}
		}else {
			dailyList.setApprovalStatus(ApprovalStatus.Rejected);
		}
		
		DailyList result = dailyListDao.save(dailyList);
		dailyListWorkflowDao.save(dailyListWorkflow);
		
		return result;
    }
    
    /**
     * {@inheritDoc}
	 * @throws InvalidGalleryException 
	 * @throws UnsupportedContentObjectTypeException 
     */
    @Transactional
    @Override
    public Response dailyListApprove(List<HashMap<String,Object>> dailyListIds) throws UnsupportedContentObjectTypeException, InvalidGalleryException {
        try {
        	List<Long> ids = Lists.newArrayList();
        	for(HashMap<String,Object> dailyListId : dailyListIds) {
        		ids.add(StringUtils.toLong(dailyListId.get("id").toString()));
        	}
        	List<DailyList> dailyLists = dailyListDao.find(ids.toArray(new Long[0]));
        	List<DailyListWorkflow> dailyListWorkflows = dailyListWorkflowDao.findByDailyLists(ids.toArray(new Long[0]));
        	for(HashMap<String,Object> dailyListId : dailyListIds) {
        		long id = StringUtils.toLong(dailyListId.get("id").toString());
        		DailyList dailyList = dailyLists.stream().filter(d->d.getId() == id).findFirst().orElse(null);
        		DailyListWorkflow dailyListWorkflow = dailyListWorkflows.stream().filter(d->d.getDailyList().getId() == id).findFirst().orElse(null);
        		
            	dailyListApprove(dailyList, dailyListWorkflow.getWorkflow(), dailyListWorkflow, ApprovalAction.valueOf(dailyListId.get("approvalAction").toString()), dailyListId.get("approvalOpinion").toString());
        	}        	
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException("DailyLists approval failure!");
        }
        
        log.info("dailyList(id=" + dailyListIds + ") was successfully approved.");
        return Response.ok().build();
    }

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Response removeDailyList(final String dailyListIds) {
        log.debug("removing dailyList: " + dailyListIds);
        try {
	        dailyListDao.remove(ConvertUtil.stringtoLongArray(dailyListIds));
	        //CacheUtils.remove(CacheItem.DailyLists.toString());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("dailyList(id=" + dailyListIds + ") was successfully deleted.");
        return Response.ok().build();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<HashMap<String, Object>> genDailyListItemsFromContent(String contentObjectIds, HttpServletRequest request) {
        log.debug("gen DailyListItems from content object ids: " + contentObjectIds);
        List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
        try {
        	String[] cIds  = StringUtils.split(contentObjectIds, ",");
        	List<Long> ids = Lists.newArrayList();
        	for(String cId : cIds) {
        		if (cId.startsWith("m")) {
        			long id = StringUtils.toLong(cId.substring(1));
        			if (id > 0) {
        				ids.add(id);
        			}
        		}
        	}
        	//Long[] ids = ConvertUtil.stringtoLongArray(contentObjectIds);
        	for(long id : ids) {
        		HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();  			
        		ContentObjectBo mo = CMUtils.loadContentObjectInstance(id);
        		mapData.put("content", mo.getTitle());//dailyList parent
				mapData.put("fileName", mo.getOriginal().getFileName());//dailyList name
				mapData.put("thumbnailHtml", AlbumTreePickerBuilder.getFullThumbnailHtml(mo, null, null, "javascript: renderPreview(" + mo.getId() + ");", request));//dailyList name
				mapData.put("timeFrom", DateUtils.formatDate(DateUtils.getDateStart(DateUtils.Now()), "HH:mm"));//dailyList name
				mapData.put("timeTo", DateUtils.formatDate(DateUtils.getDateEnd(DateUtils.Now()), "HH:mm"));//dailyList name
				mapData.put("duration", mo.getDuration());//gallery
				mapData.put("id", Long.MIN_VALUE);//dailyList id
				mapData.put("dailyListItemId", Long.MIN_VALUE);//dailyList id
				mapData.put("contentObjectId", id);//content object id
				mapData.put("mute", 0);//is private
				mapData.put("aspectRatio", 0);//is private
				ContentType contentType = CMUtils.getContentType(mo.getOriginal().getFileName());
				mapData.put("contentType", contentType.getType());//content type
				mapData.put("contentTypeDisplay", I18nUtils.getString(contentType.getLanguageKey(), request.getLocale()));//content type
				list.add(mapData);
        	}
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return list;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public DailyList getDailyList(final String dailyListId) {
        return dailyListDao.get(new Long(dailyListId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DailyList> getDailyLists() {
    	log.debug("get all dailyLists from db");
        return dailyListDao.getAllDistinct();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<DailyList> searchDailyLists(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return dailyListDao.search(pageable, new String[]{"name", "description"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> dailyListsSelect2(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	if (StringUtils.isBlank(searchTerm))
    		return toSelect2Data(dailyListDao.find(pageable).getContent(), request);
       
        return toSelect2Data(dailyListDao.search(pageable, new String[]{"name", "description"}, searchTerm).getContent(), request);
    }
    
    /**
     * {@inheritDoc}
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> dailyListsTable(String galleryId, String searchTerm, Integer limit, Integer offset, HttpServletRequest request) throws Exception {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
    	searchable.setPage(pageable);
    	if (Utils.isIndependentSpaceForDailyList()) {
    		searchable.addSort(Direction.ASC, "gallery.id", "date");
    		if (StringUtils.isBlank(galleryId)) {
    			UserAccount user = UserUtils.getUser();
                if (!user.isSystem()) {
        	        Long[] ids = CMUtils.loadLoginUserGalleries(user).stream().map(g->g.getGalleryId()).toArray(Long[]::new);
        	    	searchable.addSearchFilter("gallery.id", SearchOperator.in, ids);
                }
            }else {
            	searchable.addSearchFilter("gallery.id", SearchOperator.in, ConvertUtil.stringtoLongArray(galleryId));
            }
    	}else {
    		searchable.addSort(Direction.ASC, "contentName", "date");
    		UserAccount user = UserUtils.getUser();
            if (!user.isSystem()) {
            	List<Long> userOrganizationIds = UserUtils.getUserOrganizationIds(user.getUsername());
    	    	searchable.addSearchFilter("organization.id", SearchOperator.in, userOrganizationIds);
            }
    	}
                
    	Page<DailyList> list =  null;
    	if (StringUtils.isBlank(searchTerm)){
    		list = dailyListDao.find(searchable);
    	}else {
    		list = dailyListDao.search(searchable, searchTerm);
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", toBootstrapTableData(list.getContent(), request));
		
		return resultData;
    }
    
    /**
     * {@inheritDoc}
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> dailyListsApprovalTable(String galleryId, String searchTerm, Integer limit, Integer offset, HttpServletRequest request) throws Exception {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.setPage(pageable);
        UserAccount user = UserUtils.getUser();
        if (Utils.isIndependentSpaceForDailyList()) {
        	searchable.addSort(Direction.ASC, "gallery.id", "date");
	        if (StringUtils.isBlank(galleryId)) {
	            if (!user.isSystem()) {
	    	        Long[] ids = CMUtils.loadLoginUserGalleries(user).stream().map(g->g.getGalleryId()).toArray(Long[]::new);
	    	    	searchable.addSearchFilter("gallery.id", SearchOperator.in, ids);
	            }
	        }else {
	        	searchable.addSearchFilter("gallery.id", SearchOperator.in, ConvertUtil.stringtoLongArray(galleryId));
	        }
        }else {
    		searchable.addSort(Direction.ASC, "contentName", "date");
            if (!user.isSystem()) {
            	List<Long> userOrganizationIds = UserUtils.getUserOrganizationIds(user.getUsername());
    	    	searchable.addSearchFilter("organization.id", SearchOperator.in, userOrganizationIds);
            }
    	}
        
        HashMap<String,Object> resultData = null;
        List<Workflow> workflows = workflowDao.findApprovalWorkflows(WorkflowType.DailyList, user.getId(), user.getOrganizationId(), false);
        if (!workflows.isEmpty()) {
	        List<WorkflowDetail> workflowDetails = workflowDetailDao.findApprovalWorkflowDetail(WorkflowType.DailyList, user.getId(), user.getOrganizationId());
	        if (!workflowDetails.isEmpty()) {
	        	Long[] dailyListIds = dailyListWorkflowDao.findTodoApprovals(workflowDetails.stream().map(w->w.getWorkflow().getId()).distinct().toArray(Long[]::new), workflowDetails.stream().map(w->w.getId()).distinct().toArray(Long[]::new));
	        	searchable.addSearchFilter("id", SearchOperator.in, dailyListIds);
	        	
	        	Page<DailyList> list =  null;
	        	if (StringUtils.isBlank(searchTerm)){
	        		list = dailyListDao.find(searchable);
	        	}else {
	        		list = dailyListDao.search(searchable, searchTerm);
	        	}

	        	resultData = new LinkedHashMap<String, Object>();
	        	resultData.put("total", list.getTotalElements());
	    		resultData.put("rows", toBootstrapApprovalTable(list.getContent(), workflows, request));
	        }
        }
        
        if (resultData == null) {
        	resultData = new LinkedHashMap<String, Object>();
        	resultData.put("total", 0);
    		resultData.put("rows", Lists.newArrayList());
        }          	
		
		return resultData;
		
    }
    
    /**
	 * convert dailyList data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param dailyLists
	 * @return
     * @throws Exception 
	 */
	private  List<HashMap<String,Object>> toBootstrapTableData(List<DailyList> dailyLists, HttpServletRequest request) throws Exception{
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (DailyList u : dailyLists) {
			//dailyList list		
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("content", u.getContentName());//dailyList parent
			ContentObjectBo mo = CMUtils.loadContentObjectInstance(u.getThumbnailContentObjectId());
			String url = request.getContextPath() + "/cm/dailyListform?method=Edit&id=" + u.getId();
			mapData.put("thumbnailHtml", AlbumTreePickerBuilder.getFullThumbnailHtml(mo, u.getContentName(), "thmb album", url, request));//dailyList name
			mapData.put("date", DateUtils.formatDate(u.getDate()));//dailyList name
			if (u.getGallery() != null) {
				mapData.put("gallery", u.getGallery().getName());//gallery
			}
			if (u.getOrganization() != null) {
				mapData.put("organization", u.getOrganization().getFullName());//organization
			}
			mapData.put("id", u.getId());//dailyList id
			mapData.put("createdBy", u.getCreatedBy());//create user
			mapData.put("dateAdded", DateUtils.jsonSerializer(u.getDateAdded()));//create date
			list.add(mapData);
		}
				
		return list;
	}
	
	private  List<HashMap<String,Object>> toBootstrapApprovalTable(List<DailyList> dailyLists, List<Workflow> workflows, HttpServletRequest request) throws Exception{
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		if (!dailyLists.isEmpty()) {
			Long[] dailyListIds = dailyLists.stream().map(d->d.getId()).distinct().toArray(Long[]::new);
			List<DailyListWorkflow> dailyListWorkflows= dailyListWorkflowDao.findByDailyLists(dailyListIds);
			for (DailyList u : dailyLists) {
				//dailyList list		
				DailyListWorkflow dailyListWorkflow = dailyListWorkflows.stream().filter(d->d.getDailyList().getId() == u.getId()).findFirst().orElse(null);
				HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
				mapData.put("content", u.getContentName());//dailyList parent
				ContentObjectBo mo = CMUtils.loadContentObjectInstance(u.getThumbnailContentObjectId());
				String url = request.getContextPath() + "/cm/dailyListform?method=Approve&id=" + u.getId();
				mapData.put("thumbnailHtml", AlbumTreePickerBuilder.getFullThumbnailHtml(mo, u.getContentName(), "thmb album", url, request));//dailyList name
				if (dailyListWorkflow != null) {
					Workflow workflow = workflows.stream().filter(w->w.getId() == dailyListWorkflow.getWorkflow().getId()).findFirst().orElse(null);
					mapData.put("flowchart", getFlowchartScript(workflow, dailyListWorkflow));//workflow name
				}
				mapData.put("approvalOpinion", "");//dailyList approval opinion
				mapData.put("date", DateUtils.formatDate(u.getDate()));//dailyList name
				mapData.put("gallery", u.getGallery().getName());//gallery
				mapData.put("id", u.getId());//dailyList id
				mapData.put("createdBy", u.getCreatedBy());//create user
				mapData.put("dateAdded", DateUtils.jsonSerializer(u.getDateAdded()));//create date
				list.add(mapData);
			}
		}
				
		return list;
	}
	
	private String getFlowchartScript(Workflow workflow, DailyListWorkflow dailyListWorkflow) {
		String flowchartScript = "";
		String flowchartSeq = "";
		if (!workflow.getWorkflowDetails().isEmpty()) {
			workflow.getWorkflowDetails().sort(Comparator.comparing(WorkflowDetail::getSeq));
			int seq = 0;
			for(WorkflowDetail workflowDetail : workflow.getWorkflowDetails()) {
				DailyListActivity dailyListActivity = dailyListWorkflow.getDailyListActivities().stream().filter(d->d.getWorkflowDetail().getId() == workflowDetail.getId()).findFirst().orElse(null);
				String user= "";
				if (dailyListActivity != null) {
					if (dailyListActivity.getApprovalAction()!= ApprovalAction.NotSpecified) {
						user = "(" + dailyListActivity.getUser().getUsername() + ")" + (seq == 0 ? "" : (dailyListActivity.getApprovalAction() == ApprovalAction.Approve ? "|approved" : "|rejected"));
					}else {
						user = "|current";
					}
				}
				if (seq == 0) {
					flowchartScript += StringUtils.format("st=>start: {0}\n{1}",  workflowDetail.getActivity().getCode(), user);
					flowchartSeq = "st(right)";
				}else {
					flowchartScript += "\n";
					flowchartScript += StringUtils.format("op{0}=>operation: {1}\n{2}", seq, workflowDetail.getActivity().getCode(), user);
					flowchartSeq += StringUtils.format("->op{0}(right)", seq);
				}
				seq++;
			}
			flowchartScript += "\n";
			flowchartScript += "e=>end: approved";
			flowchartSeq += "->e";
			flowchartScript += "\n";
			flowchartScript += flowchartSeq;
		}
		
		return flowchartScript;
	}
    
    /**
	 * convert dailyList data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param dailyLists
	 * @return
	 */
	private  HashMap<String,Object> toSelect2Data(List<DailyList> dailyLists, HttpServletRequest request){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (DailyList u : dailyLists) {
			//dailyList list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getContentName());//dailyList name
			mapData.put("selected", false);//status
			mapData.put("id", u.getId());//dailyList id
			list.add(mapData);
		}
		
		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
		resultData.put("results", list);
		
		return resultData;
	}
	
	/// <summary>
	/// Gets the gallery data for the current content object, if one exists, or the current album.
	/// <see cref="Entity.MDSData.Settings" /> is assigned, unlike when this object is retrieved
	/// through the web service (since the control-specific settings can't be determined in that case).
	/// </summary>
	/// <returns>Returns an instance of <see cref="Entity.MDSData" />.</returns>
	public CMData getContentPreviewData(String contentObjectId, String dailyListId, HttpServletRequest request) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException, InvalidAlbumException, Exception{
		ContentObjectBo mo = null;
		if(!StringUtils.isBlank(contentObjectId)) {
			mo = CMUtils.loadContentObjectInstance(new Long(contentObjectId));
		}
		
		List<ContentObjectBo> contentObjects = Lists.newArrayList();
		if (!StringUtils.isBlank(dailyListId)) {
			Searchable searchable = Searchable.newSearchable();
	        searchable.addSort(Direction.ASC, "index");
	        searchable.addSearchFilter("dailyList.id", SearchOperator.eq, new Long(dailyListId));
	        List<DailyListItem> dailyListItems = dailyListItemDao.findAll(searchable);
	        for (DailyListItem u : dailyListItems) {
	        	DailyListZone zone = u.getDailyListZones().get(0);
	        	contentObjects.add(CMUtils.loadContentObjectInstance(zone.getContentObject().getId()));
	        }
	        if (mo == null && !contentObjects.isEmpty()) {
	        	mo = contentObjects.get(0);
	        }
		}
		
		if (contentObjects.isEmpty() && mo != null) {
			contentObjects.add(mo);
		}
		
				
		AlbumBo album = ContentObjectUtils.getContentObjectsForPreview(mo.getGalleryId(), contentObjects);
		CMData mdsData = GalleryUtils.getCMDataForContentPreview(mo, album
				, new CMDataLoadOptions(false, true, ContentObjectType.ContentObject, ApprovalStatus.All), request);
		
		UiTemplateBo uiTemplate = CMUtils.loadUiTemplates().get(UiTemplateType.ContentObject, album);
		mdsData.setSettings(getSettingsEntity(mo.getGalleryId(), uiTemplate, contentObjects.size() > 1, contentObjects.size() > 1, false));

		return mdsData;
	}
	
	/// <summary>
	/// Gets a data entity containing information about the current gallery. The instance can be JSON-parsed and sent to the 
	/// browser.
	/// </summary>
	/// <returns>Returns <see cref="Entity.Settings" /> object containing information about the current gallery.</returns>
	private SettingsRest getSettingsEntity(long galleryId, UiTemplateBo uiTemplate, boolean isShowContentObjectNavigation, boolean isShowContentObjectIndexPosition, boolean isShowContentObjectToolbar) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, GallerySecurityException, IOException, InvalidGalleryException, WebException, InvalidAlbumException, RecordExistsException{
		SettingsRest settings = new SettingsRest();
		settings.setGalleryId(galleryId);
		settings.setContentTmplName(uiTemplate.HtmlTemplate);
		settings.setHeaderTmplName(uiTemplate.ScriptTemplate);
		/*settings.setClientId(getMdsClientId());
		settings.setContentClientId(getContentClientId());
		settings.setContentTmplName(getContentTmplName());*/
		/*settings.setHeaderClientId(getHeaderClientId());
		settings.setHeaderTmplName(getHeaderTmplName());
		settings.setThumbnailClientId(getThumbnailClientId());
		settings.setThumbnailTmplName(getThumbnailTmplName());
		settings.setLeftPaneClientId(getLeftPaneClientId());
		settings.setLeftPaneTmplName(getLeftPaneTmplName());
		settings.setRightPaneClientId(getRightPaneClientId());
		settings.setRightPaneTmplName("");*/
		settings.setShowHeader(false);
		settings.setShowLogin(false);
		settings.setShowSearch(false);
		settings.setShowContentObjectNavigation(isShowContentObjectNavigation);
		settings.setShowContentObjectIndexPosition(isShowContentObjectIndexPosition);
		/*settings.setEnableSelfRegistration(getGallerySettings().getEnableSelfRegistration());
		settings.setEnableUserAlbum(getGallerySettings().getEnableUserAlbum());
		settings.setAllowManageOwnAccount(getGallerySettings().getAllowManageOwnAccount());
		settings.setTitle(getGalleryTitle());
		settings.setTitleUrl(getTitleUrl());
		settings.setTitleUrlTooltip(getTitleUrlTooltip());*/
		settings.setShowContentObjectTitle(false);
		/*settings.setPageSize(getGallerySettings().getPageSize());
		settings.setPagerLocation(getGallerySettings().getPagerLocation().toString());
		settings.setTransitionType(getGallerySettings().getContentObjectTransitionType().toString().toLowerCase());
		settings.setTransitionDurationMs((int)(getGallerySettings().getContentObjectTransitionDuration() * 1000));*/
		settings.setShowContentObjectToolbar(isShowContentObjectToolbar);
		settings.setAllowDownload(false);
		settings.setAllowZipDownload(false);
		settings.setShowUrlsButton(false);
		settings.setShowSlideShowButton(false);
		/*settings.setSlideShowIsRunning(isAutoPlaySlideShow() && !getAlbum().getChildContentObjects(ContentObjectType.Image).values().isEmpty());
		settings.setSlideShowType(getSlideShowType().toString());
		settings.setSlideShowIntervalMs(getGallerySettings().getSlideshowInterval());*/
		settings.setShowTransferContentObjectButton(false);
		settings.setShowCopyContentObjectButton(false);
		settings.setShowRotateContentObjectButton(false);
		settings.setShowDeleteContentObjectButton(false);
		/*settings.setMaxThmbTitleDisplayLength(getGallerySettings().getMaxThumbnailTitleDisplayLength());
		settings.setAllowAnonymousRating(getGallerySettings().getAllowAnonymousRating());
		settings.setAllowAnonBrowsing(getGallerySettings().getAllowAnonymousBrowsing());*/
		settings.setReadOnlyGallery(true);
		
		return settings;
	}	
	
	@Override
	public HashMap<String, Object> getDailyListItem(String dailyListId, HttpServletRequest request) throws Exception{
		Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "index");
        searchable.addSearchFilter("dailyList.id", SearchOperator.eq, new Long(dailyListId));
        List<DailyListItem> dailyListItems = dailyListItemDao.findAll(searchable);
        List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (DailyListItem u : dailyListItems) {
			//dailyList list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("seq", u.getIndex());//dailyList parent
			if (!u.getDailyListZones().isEmpty()) {
				DailyListZone zone = u.getDailyListZones().get(0);
				ContentObjectBo mo = CMUtils.loadContentObjectInstance(zone.getContentObject().getId());
				mapData.put("content", zone.getContentName());//dailyList parent
				mapData.put("fileName", zone.getZoneFile());//dailyList name
				mapData.put("thumbnailHtml", AlbumTreePickerBuilder.getFullThumbnailHtml(mo, null, null, "javascript: renderPreview(" + mo.getId() + ");", request));//dailyList name
				mapData.put("timeFrom", DateUtils.formatDate(zone.getStartTime(), "HH:mm"));//dailyList name
				mapData.put("timeTo", DateUtils.formatDate(zone.getEndTime(), "HH:mm"));//dailyList name
				mapData.put("duration", zone.getZoneDuration());//gallery
				mapData.put("id", zone.getId());//dailyList id
				mapData.put("mute", zone.isZoneMute() ? 1 : 0);//is mutee
				mapData.put("aspectRatio", zone.isZoneRatio() ? 1 : 0);//is aspect ratio
				ContentType contentType = CMUtils.getContentType(zone.getZoneType());
				mapData.put("contentType", zone.getZoneType());//is private
				mapData.put("contentTypeDisplay", I18nUtils.getString(contentType.getLanguageKey(), request.getLocale()));//is private
				mapData.put("contentObjectId", zone.getContentObject().getId());//content object id
				mapData.put("dailyListItemId", u.getId());//dailyList id
			}
			list.add(mapData);
		}
        
        HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
        
    	resultData.put("total", dailyListItems.size());
		resultData.put("rows", list);
		
		return resultData;
	}
	
	@Override
	public  HashMap<String,Object> toAppendGridData(List<DailyListItem> dailyListItems, HttpServletRequest request) throws Exception{
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (DailyListItem u : dailyListItems) {
			//dailyList list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("seq", u.getIndex());//dailyList parent
			if (!u.getDailyListZones().isEmpty()) {
				DailyListZone zone = u.getDailyListZones().get(0);
				ContentObjectBo mo = CMUtils.loadContentObjectInstance(zone.getContentObject().getId());
				mapData.put("content", zone.getContentName());//dailyList parent
				mapData.put("fileName", zone.getZoneFile());//dailyList name
				mapData.put("thumbnailHtml", AlbumTreePickerBuilder.getFullThumbnailHtml(mo, null, null, "javascript: renderPreview(" + mo.getId() + ");", request));//dailyList name
				mapData.put("timeFrom", DateUtils.formatDate(zone.getStartTime(), "HH:mm"));//dailyList name
				mapData.put("timeTo", DateUtils.formatDate(zone.getEndTime(), "HH:mm"));//dailyList name
				mapData.put("duration", zone.getZoneDuration());//gallery
				mapData.put("id", zone.getId());//dailyList id
				mapData.put("mute", zone.isZoneMute() ? 1 : 0);//is mutee
				mapData.put("aspectRatio", zone.isZoneRatio() ? 1 : 0);//is aspect ratio
				ContentType contentType = CMUtils.getContentType(zone.getZoneType());
				mapData.put("contentType", zone.getZoneType());//is private
				mapData.put("contentTypeDisplay", I18nUtils.getString(contentType.getLanguageKey(), request.getLocale()));//is private
				mapData.put("contentObjectId", zone.getContentObject().getId());//content object id
				mapData.put("dailyListItemId", u.getId());//dailyList id
			}
			list.add(mapData);
		}
		
		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
        
    	resultData.put("total", dailyListItems.size());
		resultData.put("rows", list);
		
		return resultData;
	}
	
	@Override
	public  List<DailyList> getFileList(String organizations, String galleries, String startDate, Integer days
			, String user_ip, String user_agent, String xforwardedfor, HttpHeaders headers,  HttpServletRequest request) throws InvalidGalleryException, InvalidMDSRoleException{
		if (StringUtils.isBlank(organizations) && StringUtils.isBlank(galleries)) {
			return Lists.newArrayList();
		}
		
		Searchable searchable = Searchable.newSearchable();
		if (Utils.isIndependentSpaceForDailyList()) {
			if (!StringUtils.isBlank(organizations)) {
				log.debug("get FileList from organizations: " + organizations);
				String[] oCodes  = StringUtils.split(organizations, ",");
				
				searchable.addSearchFilter("gallery.id", SearchOperator.in, CMUtils.loadOrganizationGalleries(Lists.newArrayList(oCodes), true));
			}else {
				log.debug("get FileList from galleries: " + galleries);
				String[] gCodes  = StringUtils.split(galleries, ",");
				searchable.addSearchFilter("gallery.name", SearchOperator.in, gCodes);
			}
		}else {
			log.debug("get FileList from galleries: " + galleries);
			String[] gCodes  = StringUtils.split(galleries, ",");
			searchable.addSearchFilter("contentName", SearchOperator.in, gCodes);
		}
		
		Date dateFrom = DateUtils.getDateOnly();
		if (!StringUtils.isBlank(startDate)) {
			try {
				dateFrom = DateUtils.convertStringToDate("yyyyMMdd", startDate);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		searchable.addSearchFilter("date", SearchOperator.gte, dateFrom);
		searchable.addSearchFilter("date", SearchOperator.lte, DateUtils.addDays(dateFrom, days));
		
		List<DailyList> list = dailyListDao.findAll(searchable);
		        
        return list;
	}
	
	@Override
	public String getCacheKey() {
    	return CacheItem.cm_dailylists.toString();
    }
}