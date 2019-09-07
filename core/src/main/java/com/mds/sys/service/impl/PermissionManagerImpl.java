package com.mds.sys.service.impl;

import com.mds.sys.dao.PermissionDao;
import com.mds.sys.model.Permission;
import com.mds.sys.service.PermissionManager;
import com.mds.sys.service.PermissionService;
import com.mds.util.CacheUtils;
import com.mds.util.ConvertUtil;
import com.mds.i18n.util.I18nUtils;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.model.search.Searchable;
import com.mds.common.service.impl.GenericManagerImpl;
import com.mds.core.CacheItem;
import com.mds.core.UserAction;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("permissionManager")
@WebService(serviceName = "PermissionService", endpointInterface = "com.mds.sys.service.PermissionService")
public class PermissionManagerImpl extends GenericManagerImpl<Permission, Long> implements PermissionManager, PermissionService {
    PermissionDao permissionDao;

    @Autowired
    public PermissionManagerImpl(PermissionDao permissionDao) {
        super(permissionDao);
        this.permissionDao = permissionDao;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public Permission getPermission(final String permissionId) {
        return permissionDao.get(new Long(permissionId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Permission> getPermissions() {
    	log.debug("get all permissions from db");
        return permissionDao.getAllDistinct();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<Permission> searchPermissions(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return permissionDao.search(pageable, new String[]{"name", "description"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> permissionsSelect2(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	if (StringUtils.isBlank(searchTerm))
    		return toSelect2Data(permissionDao.find(pageable).getContent(), request);
       
        return toSelect2Data(permissionDao.search(pageable, new String[]{"name", "description"}, searchTerm).getContent(), request);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> permissionsTable(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "name");
        searchable.setPage(pageable);
        
    	Page<Permission> list =  null;
    	if (StringUtils.isBlank(searchTerm)){
    		list = permissionDao.find(searchable);
    	}else {
    		list = permissionDao.search(searchable, searchTerm);
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", toBootstrapTableData(list.getContent(), request));
		
		return resultData;
		
    }
    
    /**
	 * convert permission data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param permissions
	 * @return
	 */
	private  List<HashMap<String,Object>> toBootstrapTableData(List<Permission> permissions, HttpServletRequest request){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (Permission u : permissions) {
			//permission list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("description", u.getDescription());//permission description
			mapData.put("name", u.getName());//permission name
			mapData.put("permission", I18nUtils.UserActionConverter(u.getPermissions(), request.getLocale()));//permission
			mapData.put("id", u.getId());//permission id
			mapData.put("show", u.getShow());//is show
			mapData.put("permissionMsgKey", u.getPermissionMsgKey());//permission message key
			mapData.put("permissionKey", u.getPermissionKey());//permission key
			list.add(mapData);
		}
				
		return list;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public Permission savePermission(final Permission permission) throws RecordExistsException {

        try {
        	Permission result = permissionDao.savePermission(permission);
            CacheUtils.remove(CacheItem.sys_permissions);
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("Permission '" + permission.getName() + "' already exists!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePermission(final String permissionIds) throws WebApplicationException{
        log.debug("removing permission: " + permissionIds);
        try {
        	permissionDao.remove(ConvertUtil.StringtoLongArray(permissionIds));
        	CacheUtils.remove(CacheItem.sys_permissions);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Permission(id=" + permissionIds + ") was successfully deleted.");
        //return Response.ok().build();
    }
    
    /**
	 * convert permission data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param permissions
	 * @return
	 */
	private  HashMap<String,Object> toSelect2Data(List<Permission> permissions, HttpServletRequest request){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (Permission u : permissions) {
			//permission list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getName());//permission name
			mapData.put("selected", false);//status
			mapData.put("id", u.getId());//permission id
			list.add(mapData);
		}
		
		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
		resultData.put("results", list);
		
		return resultData;
	}
	
	public String getCacheKey() {
    	return CacheItem.sys_permissions.toString();
    }
}