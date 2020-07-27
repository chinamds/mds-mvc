package com.mds.aiotplayer.pm.service.impl;

import com.mds.aiotplayer.pm.dao.PlayerGroupDao;
import com.mds.aiotplayer.pm.model.PlayerGroup;
import com.mds.aiotplayer.pm.service.PlayerGroupManager;
import com.mds.aiotplayer.pm.service.PlayerGroupService;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.ConvertUtil;
import com.mds.aiotplayer.util.StringUtils;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

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

@Service("playerGroupManager")
@WebService(serviceName = "PlayerGroupService", endpointInterface = "com.mds.aiotplayer.pm.service.PlayerGroupService")
public class PlayerGroupManagerImpl extends GenericManagerImpl<PlayerGroup, Long> implements PlayerGroupManager, PlayerGroupService {
    PlayerGroupDao playerGroupDao;

    @Autowired
    public PlayerGroupManagerImpl(PlayerGroupDao playerGroupDao) {
        super(playerGroupDao);
        this.playerGroupDao = playerGroupDao;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public PlayerGroup getPlayerGroup(final String playerGroupId) {
        return playerGroupDao.get(new Long(playerGroupId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlayerGroup> getPlayerGroups() {
        return playerGroupDao.getAllDistinct();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<PlayerGroup> searchPlayerGroups(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return playerGroupDao.search(pageable, new String[]{"code", "description"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> playerGroupsSelect2(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	if (StringUtils.isBlank(searchTerm))
    		return toSelect2Data(playerGroupDao.find(pageable).getContent());
       
        return toSelect2Data(playerGroupDao.search(pageable, new String[]{"code", "description"}, searchTerm).getContent());
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> playerGroupsTable(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.setPage(pageable);
        
        Page<PlayerGroup> list =  null;
    	if (StringUtils.isBlank(searchTerm)){
    		searchable.addSort(Direction.DESC, "apptDate", "apptPeriod.timeFrom", "apptPeriod.apptItem.code");
    		list = playerGroupDao.find(searchable);
    	}else {
    		searchable.addSort(Direction.DESC, "apptDate", "timeRange", "apptCode");
    		list = playerGroupDao.search(searchable, searchTerm);
    	}
    	    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", list.getContent());
		
		return resultData;
		
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> playerGroupsTable(String organizationId, String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "playerGroupType", "playerGroupName");
        searchable.setPage(pageable);
        
        UserAccount user = UserUtils.getUser();
    	long oId = StringUtils.toLong(organizationId);
    	if (!user.isSystem()) {
    		searchable.addSearchFilter("organization.id", SearchOperator.eq, oId);
    	}else {
    		if (oId != Long.MIN_VALUE && oId > 0) {
    			searchable.addSearchFilter("organization.id", SearchOperator.eq, oId);
    		}
    	}
        
    	List<PlayerGroup> list =  null;
    	long totalElements = 0;
    	if (StringUtils.isBlank(searchTerm)){
    		Page<PlayerGroup> page = playerGroupDao.find(searchable);
    		list = page.getContent();
    		totalElements = page.getTotalElements();
    	}else {
    		list = playerGroupDao.findAll(searchable);
    		totalElements = list.size();
    		list = list.stream().skip(offset).limit(limit).collect(Collectors.toList());
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", totalElements);
		resultData.put("rows", toTreeTable(list));
		
		return resultData;
    }
    
    /**
     * {@inheritDoc}
	 * @throws InvalidMDSRoleException 
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> playerGroupsTreeTable(String searchTerm, Integer limit, Integer offset) throws InvalidMDSRoleException {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Page<PlayerGroup> list =  null;
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "parent.id", "code");
        searchable.setPage(pageable);
        UserAccount user = UserUtils.getUser();
    	if (user != null && !user.isSystem()){
    		List<Long> organizationIds = UserUtils.getUserOrganizationIds(user.getUsername());
			searchable.addSearchFilter("organization.id", SearchOperator.in, organizationIds);
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	if (user != null) {
	    	if (StringUtils.isBlank(searchTerm)){
	    		list = playerGroupDao.find(searchable);
	    	}else {
	    		list = playerGroupDao.search(searchable, searchTerm);
	    	}
	    	resultData.put("total", list.getTotalElements());
			resultData.put("rows", toTreeTable(list.getContent()));
    	}else {
    		resultData.put("total", 0);
			resultData.put("rows", null);
    	}

		return resultData;
    }
    
    /**
   	 * convert playerGroup data to bootstrap table tree grid format(http://issues.wenzhixin.net.cn/bootstrap-table/#extensions/treegrid.html)
   	 * {
   	 *	  [
   		    {
   		      "id": 1,
   		      "pid": 0,
   		      "status": 1,
   		      "name": "system management",
   		      "permissionValue": "open:system:get"
   		    },
   		    {
   		      "id": 2,
   		      "pid": 0,
   		      "status": 1,
   		      "name": "dictory management",
   		      "permissionValue": "open:dict:get"
   		    },
   	 *	  ]
   	 *	}
   	 * @param list: playerGroups
   	 * @return
   	 */
   public List<Map<String, Object>> toTreeTable(List<PlayerGroup> list) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		for (int i=0; i<list.size(); i++){
			PlayerGroup e = list.get(i);
			if (e.isRoot())
				continue;
			
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pid", e.isTop() ? 0 : e.getParent().getId());
			map.put("code", e.getCode());
			map.put("name", e.getName());
			map.put("description", e.getDescription());
							
			mapList.add(map);
		}
		
		return mapList;
   	}

    /**
     * {@inheritDoc}
     */
    @Override
    public PlayerGroup savePlayerGroup(final PlayerGroup playerGroup) throws RecordExistsException {

        try {
            return playerGroupDao.savePlayerGroup(playerGroup);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("PlayerGroup type: '" + playerGroup.getCode() + "' already exists!");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PlayerGroup addPlayerGroup(final PlayerGroup playerGroup) throws RecordExistsException {

        try {
            return playerGroupDao.addPlayerGroup(playerGroup);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("PlayerGroup type: '" + playerGroup.getCode() + "' already exists!");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PlayerGroup userAppointment(String mobile, String idNumber, PlayerGroup playerGroup) 
    		throws RecordExistsException {
		
		return savePlayerGroup(playerGroup);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public Response removePlayerGroup(final String playerGroupIds) throws WebApplicationException{
        log.debug("removing playerGroup: " + playerGroupIds);
        try {
        	playerGroupDao.remove(ConvertUtil.StringtoLongArray(playerGroupIds));
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Face Define(id=" + playerGroupIds + ") was successfully deleted.");
        return Response.ok().build();
    }
    
    /**
     * {@inheritDoc}
     */
/*    @Override
    public Response changePlayerGroupStatus(String playerGroupId, String playerGroupStatus) {
    	try {
        	PlayerGroup playerGroup = playerGroupDao.get(new Long(playerGroupId));
        	if (playerGroup != null){
        		playerGroup.setPlayerGroupType(PlayerGroupType.valueOf(playerGroupStatus));
        		playerGroupDao.save(playerGroup);
        	}
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    	
    	return Response.ok().build();
    }*/
        
    /**
	 * convert playerGroup data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param playerGroups
	 * @return
	 */
	private  HashMap<String,Object> toSelect2Data(List<PlayerGroup> playerGroups){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (PlayerGroup u : playerGroups) {
			//playerGroup list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getCode());//playerGroup name
			mapData.put("selected", false);//status
			mapData.put("id", u.getId());//playerGroup id
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
	    PlayerGroup playerGroup = playerGroupDao.findOne(searchable);
        // uncomment line below to send non-streamed
		 //QRCode.from("Hello World").to(ImageType.PNG).writeTo(outputStream);

        //return Response.ok(QRCode.from(playerGroup.getRefNo()).withSize(250, 250).stream()).build();
        // uncomment line below to send streamed
        // return Response.ok(new ByteArrayInputStream(imageData)).build();
	    ByteArrayInputStream bas = null;
    	try {
	    	File file =QRCode.from(playerGroup.getCode()).to(ImageType.PNG).withSize(250, 250).file();
	    	bas =new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
    	} catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        
        return Response.ok(bas).build();
    }
	
	 @Override
    public List<HashMap<String, Object>> playerGroupsTreeSelector(Long excludeId) throws InvalidMDSRoleException {
		List<HashMap<String, Object>> mapList = Lists.newArrayList();
		Searchable searchable = Searchable.newSearchable();
    	//searchable.addSearchFilter("id", SearchOperator.ne, 1L);
        searchable.addSort(Direction.ASC, "parent.id", "code");
        UserAccount user = UserUtils.getUser();
        if (!user.isSystem()) {
        	List<Long> userOrganizationIds = UserUtils.getUserOrganizationIds(user.getUsername());
        	//userOrganizationIds.add(Organization.getRootId());
        	searchable.addSearchFilter("organization.id", SearchOperator.in, userOrganizationIds);
        }
		List<PlayerGroup> list = playerGroupDao.findAll(searchable);
		List<Long> playerGroupIds = list.stream().map(c->c.getId()).collect(Collectors.toList());
        List<PlayerGroup> playerGroups = list.stream().filter(c->c.getParent() == null || c.getParent().getId() == null || !playerGroupIds.contains(c.getParent().getId())).collect(Collectors.toList());
        
		List<Long> parentIds = list.stream().filter(c->!c.getParentIds().contains(excludeId)).map(PlayerGroup::getId).collect(Collectors.toList());
		for (int i=0; i<list.size(); i++){
			PlayerGroup e = list.get(i);
			if (e.isRoot())
				continue;
			
			if (excludeId == null || (excludeId!=null && excludeId != e.getId() && parentIds.contains(e.getId()))){
				HashMap<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.isTop() ? 0 : (playerGroups.contains(e) ? 1 : e.getParent().getId()));
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		
		return mapList;
	}
}