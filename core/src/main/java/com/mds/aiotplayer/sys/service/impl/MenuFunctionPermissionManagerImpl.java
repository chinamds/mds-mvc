package com.mds.aiotplayer.sys.service.impl;

import com.mds.aiotplayer.sys.dao.MenuFunctionPermissionDao;
import com.mds.aiotplayer.sys.model.MenuFunction;
import com.mds.aiotplayer.sys.model.MenuFunctionPermission;
import com.mds.aiotplayer.sys.model.Permission;
import com.mds.aiotplayer.sys.service.MenuFunctionPermissionManager;
import com.mds.aiotplayer.sys.service.MenuFunctionPermissionService;
import com.mds.aiotplayer.util.ConvertUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.core.CacheItem;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;

@Service("menuFunctionPermissionManager")
@WebService(serviceName = "MenuFunctionPermissionService", endpointInterface = "com.mds.aiotplayer.sys.service.MenuFunctionPermissionService")
public class MenuFunctionPermissionManagerImpl extends GenericManagerImpl<MenuFunctionPermission, Long> implements MenuFunctionPermissionManager, MenuFunctionPermissionService {
    MenuFunctionPermissionDao menuFunctionPermissionDao;

    @Autowired
    public MenuFunctionPermissionManagerImpl(MenuFunctionPermissionDao menuFunctionPermissionDao) {
        super(menuFunctionPermissionDao);
        this.menuFunctionPermissionDao = menuFunctionPermissionDao;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public MenuFunctionPermission getMenuFunctionPermission(final String menuFunctionPermissionId) {
        return menuFunctionPermissionDao.get(new Long(menuFunctionPermissionId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MenuFunctionPermission> getMenuFunctionPermissions() {
        //return menuFunctionPermissionDao.getAllDistinct();
    	log.debug("get all menu and functions permissions from db");
    	Searchable searchable = Searchable.newSearchable();
    	searchable.addSort(Direction.ASC, "menuFunction.id");
        List<MenuFunctionPermission> menuFunctionPermissions = findAll(searchable);
        return menuFunctionPermissions;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public MenuFunctionPermission saveMenuFunctionPermission(final MenuFunctionPermission menuFunctionPermission) throws RecordExistsException {
        try {
        	MenuFunctionPermission result =  menuFunctionPermissionDao.save(menuFunctionPermission);
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("this menu or function '" + menuFunctionPermission.getMenuFunction().getCode() 
            		+ "' with permission '" + menuFunctionPermission.getPermission().getName() + "' already exists!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void removeMenuFunctionPermission(final MenuFunctionPermission menuFunctionPermission) {
        log.debug("removing menuFunctionPermission: " + menuFunctionPermission);
        menuFunctionPermissionDao.remove(menuFunctionPermission);
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void removeMenuFunctionPermission(final String menuFunctionPermissionIds) {
    	//Separates the comma-separated string into a collection
        log.debug("removing menuFunctionPermission: " + menuFunctionPermissionIds);
        menuFunctionPermissionDao.remove(ConvertUtil.StringtoLongArray(menuFunctionPermissionIds));
    }
    
	
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
	public void remove(Long id) {
		menuFunctionPermissionDao.remove(id);
	}
	
	/**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> menuFunctionPermissionsTreeTable(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Page<MenuFunctionPermission> list =  null;
    	Searchable searchable = Searchable.newSearchable();
    	searchable.addSort(Direction.ASC, "menuFunction.id");
        searchable.setPage(pageable);
    	if (StringUtils.isBlank(searchTerm)){
    		list = menuFunctionPermissionDao.find(searchable);
    	}else {
    		list = menuFunctionPermissionDao.search(searchable, searchTerm);
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", toTreeTable(list.getContent(), request));
		
		return resultData;
    }
    
    /**
	 * convert menuFunctionPermission data to bootstrap table tree grid format(http://issues.wenzhixin.net.cn/bootstrap-table/#extensions/treegrid.html)
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
	 * @param list: menuFunctionPermissions
	 * @return
	 */
    public List<Map<String, Object>> toTreeTable(List<MenuFunctionPermission> list, HttpServletRequest request) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		for (int i=0; i<list.size(); i++){
			MenuFunctionPermission e = list.get(i);
			
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("permission", e.getPermission());
							
			mapList.add(map);
		}
		
		return mapList;
	}
    
    @Override
    public List<HashMap<String, Object>> menuFunctionPermissionsTreeSelector(Long excludeId) {
		List<HashMap<String, Object>> mapList = Lists.newArrayList();
		/*List<MenuFunctionPermission> list = getAll();
		List<Long> parentIds = list.stream().filter(c->!c.getParentIds().contains(excludeId)).map(MenuFunctionPermission::getId).collect(Collectors.toList());
		for (int i=0; i<list.size(); i++){
			MenuFunctionPermission e = list.get(i);
			
			if (excludeId == null || (excludeId!=null && excludeId != e.getId() && parentIds.contains(e.getId()))){
				HashMap<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				mapList.add(map);
			}
		}*/
		
		return mapList;
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCacheKey() {
    	return CacheItem.sys_menufunctionpermissions.toString();
    }
    
    /**
	 * convert album data to bootstarp tree format
	 * @param albums
	 * @return
	 */
	private  List<Map<String,Object>> toBSTreeData(List<MenuFunction> menuFunctions, List<MenuFunctionPermission> menuFunctionPermissions, List<Permission> permissions){
		List<Map<String,Object>> resultData = new LinkedList<Map<String,Object>>();
		for (MenuFunction u : menuFunctions) {
			//menu function list
			Map<String,Object> map = new LinkedHashMap<String, Object>();
			map.put("text", u.getName());//album name
			map.put("href", "javascript:void(0)");//link
			map.put("id", u.getId());//album id
			List<MenuFunction> ps = u.getChildren();
			map.put("tags",  new Integer[]{ps.size()});//show child data size
			if(null != ps && ps.size() > 0){
				List<Map<String,Object>> list = new LinkedList<Map<String,Object>>();
				//child menufunctions
				for (MenuFunction up : ps) {
					Map<String,Object> mapx = new LinkedHashMap<String, Object>();
					mapx.put("text", up.getName());//menu or function name
					//mapx.put("href", up.getUrl());//menu function url
					//mapx.put("tags", "0");//
					map.put("id", u.getId());//menu function id
					list.add(mapx);
				}
				map.put("nodes", list);
			}else {
				List<Permission> thisPermissions = menuFunctionPermissions.stream().filter(c->c.getMenuFunction() == u).map(MenuFunctionPermission::getPermission).collect(Collectors.toList());
				List<Map<String,Object>> list = new LinkedList<Map<String,Object>>();
				//permission list
				for (Permission permission : permissions) {
					Map<String,Object> mapx = new LinkedHashMap<String, Object>();
					mapx.put("text", permission.getName());//menu or function name
					//mapx.put("href", up.getUrl());//menu function url
					//mapx.put("tags", "0");//
					map.put("id", u.getId());//menu function id
					Map<String,Object> mapState = new LinkedHashMap<String, Object>();
					mapState.put("checked", thisPermissions.contains(permission));
					map.put("state", mapState);
					list.add(mapx);
				}
			}
			resultData.add(map);
		}
		return resultData;
	}
	
	public List<MenuFunctionPermission> findByMenuFunctionIds(List menuFunctionIds){
		return menuFunctionPermissionDao.findByMenuFunctionIds(menuFunctionIds);
	}
	
	public List<MenuFunctionPermission> findByRoleIds(List roleIds){
		return menuFunctionPermissionDao.findByRoleIds(roleIds);
	}
}