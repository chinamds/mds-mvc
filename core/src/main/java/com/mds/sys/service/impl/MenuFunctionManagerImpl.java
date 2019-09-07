package com.mds.sys.service.impl;

import com.mds.sys.dao.MenuFunctionDao;
import com.mds.sys.dao.MenuFunctionPermissionDao;
import com.mds.sys.exception.MenuFunctionExistsException;
import com.mds.sys.exception.MenuFunctionNotExistsException;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.exception.RecordNotExistsException;
import com.mds.sys.model.MenuFunction;
import com.mds.sys.model.MenuFunctionPermission;
import com.mds.sys.model.Permission;
import com.mds.sys.service.MenuFunctionManager;
import com.mds.sys.service.MenuFunctionService;
import com.mds.util.ConvertUtil;
import com.mds.i18n.util.I18nUtils;
import com.mds.sys.util.UserUtils;
import com.mds.util.excel.fieldcell.TreeCell;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.common.model.search.SearchOperator;
import com.mds.common.model.search.Searchable;
import com.mds.common.utils.Reflections;
import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.common.exception.ImportFromException;
//import com.mds.common.model.Page;
import com.mds.common.service.impl.GenericManagerImpl;
import com.mds.core.CacheItem;

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
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service("menuFunctionManager")
@WebService(serviceName = "MenuFunctionService", endpointInterface = "com.mds.sys.service.MenuFunctionService")
public class MenuFunctionManagerImpl extends GenericManagerImpl<MenuFunction, Long> implements MenuFunctionManager, MenuFunctionService {
    MenuFunctionDao menuFunctionDao;
    MenuFunctionPermissionDao menuFunctionPermissionDao;

    @Autowired
    public MenuFunctionManagerImpl(MenuFunctionDao menuFunctionDao) {
        super(menuFunctionDao);
        this.menuFunctionDao = menuFunctionDao;
    }
    
    @Autowired
    public void setMenuFunctionPermissionDao(MenuFunctionPermissionDao menuFunctionPermissionDao) {
        this.menuFunctionPermissionDao = menuFunctionPermissionDao;
    }
    
    /**
     * {@inheritDoc}
     */
	public Page<MenuFunction> findMenuFunction(Pageable page, MenuFunction menuFunction) {	
		return menuFunctionDao.find(page);
	}
	
	/**
     * {@inheritDoc}
     */
    @Override
    public List<MenuFunction> importFrom(List<MenuFunction> entities, String[] uniqueKeys) throws ImportFromException{
    	Searchable searchable = null;
    	List<MenuFunction> saved = Lists.newArrayList();
    	String importMenu = "";
    	try {
	    	for (MenuFunction entity : entities){
	    		importMenu = entity.getCode();
	    		if (!StringUtils.isBlank(entity.getParentCodes())) {
		    		MenuFunction parent = UserUtils.getMenuFunctionRoot();
		    		StringTokenizer toKenizer = new StringTokenizer(entity.getParentCodes(), " > ");        
		            while (toKenizer.hasMoreElements()) {         
		            	parent = TreeCell.getParent(saved, UserUtils.getMenuFunctions(), toKenizer.nextToken(), parent.getCode());
		            }   
		            entity.setParent(parent);
	    		}
	    	
				searchable = Searchable.newSearchable();
				for(String field : uniqueKeys){
					searchable.addSearchFilter(field, SearchOperator.eq, Reflections.invokeGetter(entity, field));	
				}
	            
				saved.add(dao.addOrUpdate(entity, searchable));
			}
	    } catch (final Exception e) {
	        e.printStackTrace();
	        log.warn(e.getMessage());
	        throw new ImportFromException("import Menu or Function '" + importMenu + "' failure!");
	    }
    	
    	return saved;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public MenuFunction getMenuFunction(final String menuFunctionId) {
        return menuFunctionDao.get(new Long(menuFunctionId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MenuFunction> getMenuFunctions() {
        //return menuFunctionDao.getAllDistinct();
    	log.debug("get all menu and functions from db");
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "sort");
        List<MenuFunction> menuFunctions = findAll(searchable);
        return menuFunctions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MenuFunction saveMenuFunction(final MenuFunction menuFunction) throws MenuFunctionExistsException {   	
    	boolean isNew = (menuFunction.getId() == null);
        try {
        	MenuFunction result =  menuFunctionDao.save(menuFunction);
            //UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
        	if (menuFunction.getMenuFunctionPermissions() != null && !menuFunction.getMenuFunctionPermissions().isEmpty()) {
        		List<MenuFunctionPermission> saves = Lists.newArrayList();
        		if (isNew) {
        			for(MenuFunctionPermission menuFunctionPermission : menuFunction.getMenuFunctionPermissions()) {
        				menuFunctionPermission.setMenuFunction(result);
        				menuFunctionPermission.setCurrentUser(result.getLastModifiedBy());
        				saves.add(menuFunctionPermissionDao.save(menuFunctionPermission));
        			}
        		}else {
        			Searchable searchable = Searchable.newSearchable();
        			searchable.addSearchFilter("menuFunction.id", SearchOperator.eq, menuFunction.getId());	
        			List<MenuFunctionPermission> menuFunctionPermissions = menuFunctionPermissionDao.findAll(searchable);
        			List<MenuFunctionPermission> newPermissions = Lists.newArrayList();
        			for(MenuFunctionPermission menuFunctionPermission : menuFunction.getMenuFunctionPermissions()) {
        				Optional<MenuFunctionPermission> optional = menuFunctionPermissions.stream()
        						.filter(m->m.getPermission().getId() == menuFunctionPermission.getPermission().getId()).findFirst();
        				if (optional.isPresent()) {
        					MenuFunctionPermission exists = optional.get();
        					exists.setMenuFunction(result);
        					exists.setPermission(menuFunctionPermission.getPermission());
        					exists.setCurrentUser(result.getLastModifiedBy());
        					saves.add(menuFunctionPermissionDao.save(exists));
	        				menuFunctionPermissions.remove(exists);
        				}else {
        					newPermissions.add(menuFunctionPermission);
        				}
        			}
        			
        			for(MenuFunctionPermission menuFunctionPermission : newPermissions) {
        				if (!menuFunctionPermissions.isEmpty()){
        					MenuFunctionPermission exists = menuFunctionPermissions.get(0);
        					exists.setMenuFunction(result);
        					exists.setPermission(menuFunctionPermission.getPermission());
        					exists.setCurrentUser(result.getLastModifiedBy());
        					saves.add(menuFunctionPermissionDao.save(exists));
	        				menuFunctionPermissions.remove(exists);
        				}else {
        					menuFunctionPermission.setMenuFunction(result);
            				menuFunctionPermission.setCurrentUser(result.getLastModifiedBy());
            				saves.add(menuFunctionPermissionDao.save(menuFunctionPermission));
        				}
        			}
        			if (!menuFunctionPermissions.isEmpty()){
        				menuFunctionPermissionDao.remove(menuFunctionPermissions);
        			}
        		}
        		result.setMenuFunctionPermissions(saves);
        	}
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new MenuFunctionExistsException("Menu or Function '" + menuFunction.getCode() + "' already exists!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeMenuFunction(final MenuFunction menuFunction) {
        log.debug("removing menuFunction: " + menuFunction);
        menuFunctionDao.remove(menuFunction);
        UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeMenuFunction(final String menuFunctionIds) {
    	//Separates the comma-separated string into a collection
    	String[] ids = StringUtils.split(menuFunctionIds, ',');
        log.debug("removing menuFunction: " + menuFunctionIds);
        menuFunctionDao.remove(ConvertUtil.StringtoLongArray(menuFunctionIds));
        UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<MenuFunction> findAll(){
		return getMenuFunctions();
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
	public void remove(Long id) {
		menuFunctionDao.remove(id);
		//UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
	}

	@Override
	public MenuFunction getMenuFunctionByMenuFunctioncode(String menuFunctioncode) throws MenuFunctionNotExistsException {
		return (MenuFunction) menuFunctionDao.loadMenuFunctionByMenuFunctioncode(menuFunctioncode);
	}
	
	/**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> menuFunctionsTreeTable(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Page<MenuFunction> list =  null;
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "sort");
        searchable.setPage(pageable);
    	if (StringUtils.isBlank(searchTerm)){
    		list = menuFunctionDao.find(searchable);
    	}else {
    		list = menuFunctionDao.search(searchable, searchTerm);
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", toTreeTable(list.getContent(), request));
		
		return resultData;
    }
    
    /**
	 * convert menuFunction data to bootstrap table tree grid format(http://issues.wenzhixin.net.cn/bootstrap-table/#extensions/treegrid.html)
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
	 * @param list: menuFunctions
	 * @return
	 */
    public List<Map<String, Object>> toTreeTable(List<MenuFunction> list, HttpServletRequest request) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		for (int i=0; i<list.size(); i++){
			MenuFunction e = list.get(i);
			if (e.isRoot())
				continue;
			
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pid", e.isTop() ? 0 : e.getParent().getId());
			map.put("code", e.getCode());
			map.put("title", I18nUtils.getString(e.getTitle(), request.getLocale()));
			map.put("href", e.getHref());
			map.put("sort", e.getSort());
			map.put("isShow", e.getIsShow());
			map.put("permission", e.getPermission());
							
			mapList.add(map);
		}
		
		return mapList;
	}   
    
    @Override
    public List<HashMap<String, Object>> menuFunctionsTreeSelector(Long excludeId) {
		List<HashMap<String, Object>> mapList = Lists.newArrayList();
		List<MenuFunction> list = getAll();
		List<Long> parentIds = list.stream().filter(c->!c.getParentIds().contains(excludeId)).map(MenuFunction::getId).collect(Collectors.toList());
		for (int i=0; i<list.size(); i++){
			MenuFunction e = list.get(i);
			if (e.isRoot())
				continue;
			
			if (excludeId == null || (excludeId!=null && excludeId != e.getId() && parentIds.contains(e.getId()))){
				HashMap<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.isTop() ? 0:e.getParent().getId());
				map.put("name", e.getCode());
				mapList.add(map);
			}
		}
		
		return mapList;
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCacheKey() {
    	return CacheItem.sys_menufunctions.toString();
    }
}