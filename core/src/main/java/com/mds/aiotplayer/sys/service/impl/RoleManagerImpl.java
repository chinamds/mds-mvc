package com.mds.aiotplayer.sys.service.impl;

import com.mds.aiotplayer.cm.content.GalleryBoCollection;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.core.CacheItem;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.sys.dao.MenuFunctionDao;
import com.mds.aiotplayer.sys.dao.OrganizationDao;
import com.mds.aiotplayer.sys.dao.RoleDao;
import com.mds.aiotplayer.sys.exception.RoleExistsException;
import com.mds.aiotplayer.sys.model.MenuFunction;
import com.mds.aiotplayer.sys.model.MenuFunctionPermission;
import com.mds.aiotplayer.sys.model.Organization;
import com.mds.aiotplayer.sys.model.Permission;
import com.mds.aiotplayer.sys.model.Role;
import com.mds.aiotplayer.sys.model.RoleType;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.service.RoleManager;
import com.mds.aiotplayer.sys.service.RoleService;
import com.mds.aiotplayer.util.ConvertUtil;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.sys.util.UserUtils;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Implementation of RoleManager interface.
 *
 * @author <a href="mailto:dan@getrolling.com">Dan Kibler</a>
 */
@Service("roleManager")
@WebService(serviceName = "roleService", endpointInterface = "com.mds.aiotplayer.sys.service.RoleService")
public class RoleManagerImpl extends GenericManagerImpl<Role, Long> implements RoleManager, RoleService {
    RoleDao roleDao;
    MenuFunctionDao menuFunctionDao;
    OrganizationDao organizationDao;

    @Autowired
    public RoleManagerImpl(RoleDao roleDao) {
        super(roleDao);
        this.roleDao = roleDao;
    }
    
    @Autowired
    public void setMenuFunctionDao(MenuFunctionDao menuFunctionDao) {
        this.menuFunctionDao = menuFunctionDao;
    }
    
    @Autowired
    public void setOrganizationDao(OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }

    /**
     * {@inheritDoc}
     */
    public List<Role> getRoles(Role role) {
        return dao.getAll();
    }
    
    /**
     * {@inheritDoc}
     */
    public Role getRole(String rolename) {
        return roleDao.getRoleByName(rolename);
    }
    
    /**
     * {@inheritDoc}
     */
    public Role getRole(String rolename, long oId) {
    	Searchable searchable = Searchable.newSearchable();
    	if (oId != Long.MIN_VALUE) {
    		searchable.addSearchFilter("organization.id", SearchOperator.eq, oId);
    	}
       	searchable.addSearchFilter("name", SearchOperator.eq, rolename);
        	
        return roleDao.findOne(searchable);
    }
    
    public List<Role> getRolesByRoleType(RoleType roleType, long oId) {
    	Searchable searchable = Searchable.newSearchable();
    	if (oId != Long.MIN_VALUE) {
    		searchable.addSearchFilter("organization.id", SearchOperator.eq, oId);
    	}
       	searchable.addSearchFilter("type", SearchOperator.eq, roleType);
        	
        return roleDao.findAll(searchable);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Role> getRoles(long oId, boolean includeChildOrginaztion){
    	Searchable searchable = Searchable.newSearchable();
    	searchable.addSort(Direction.ASC, "name");
    	if (includeChildOrginaztion) {
    		List<Long> oIds = UserUtils.getOrganizationChildren(oId);
    		searchable.addSearchFilter("organization.id", SearchOperator.in, oIds);
    	}else {
    		searchable.addSearchFilter("organization.id", SearchOperator.eq, oId);
    	}
        	
        return roleDao.findAll(searchable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Role> getRoles() {
        return roleDao.getAllDistinct();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Role saveRole(final Role role) throws RoleExistsException {  	
        try {
        	Role result =  roleDao.save(role);
            //UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RoleExistsException("Role '" + role.getName() + "' already exists!");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Role save(final Role role) {
    	Role result =  roleDao.save(role);
    	//CacheUtils.remove(CacheItem.MDSRoles);
    	
    	return result;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void removeRole(final Role role) {
        log.debug("removing role: " + role);
        roleDao.remove(role);
    }

	@Override
	public Role getRoleByRolename(String rolename) {
		return (Role) roleDao.getRoleByName(rolename);
	}
	
	@Override
	public boolean roleExists(String rolename) {
		return roleDao.roleExists(rolename);
	}
	
	@Override
	public boolean roleExists(String rolename, long oId) {
		Searchable searchable = Searchable.newSearchable();
		if (oId != Long.MIN_VALUE) {
			searchable.addSearchFilter("organization.id", SearchOperator.eq, oId);
		}
       	searchable.addSearchFilter("name", SearchOperator.eq, rolename);
        	
        return roleDao.findAny(searchable);
	}
	
	@Override
	public boolean roleTypeExists(RoleType roleType, long oId) {
		Searchable searchable = Searchable.newSearchable();
		if (oId != Long.MIN_VALUE) {
			searchable.addSearchFilter("organization.id", SearchOperator.eq, oId);
		}
       	searchable.addSearchFilter("type", SearchOperator.eq, roleType);
        	
        return roleDao.findAny(searchable);
	}
	
	public List<Role> findSARoleNotOwnerAlbum(Long albumId){
		return roleDao.findSARoleNotOwnerAlbum(albumId);
	}
	
	/**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<Role> searchRoles(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return roleDao.search(pageable, new String[]{"name", "description"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> rolesSelect2(String searchTerm, Integer limit, Integer offset) throws InvalidMDSRoleException {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
    	searchable.addSort(Direction.ASC, "name");
        searchable.setPage(pageable);
        UserAccount user = UserUtils.getUser();
        if (!user.isSystem()) {
        	if (UserUtils.hasRoleType(RoleType.oa)) {
        		List<Long> userOrganizationIds = UserUtils.getUserOrganizationIds(UserUtils.getLoginName());
        		searchable.addSearchFilter("organization.id", SearchOperator.in, userOrganizationIds);
        	//}else if (UserUtils.hasRoleType(new RoleType[] {RoleType.ga, RoleType.gu, RoleType.gg})) {
        	}else if (UserUtils.hasRoleType(RoleType.ga)) {
        		GalleryBoCollection galleries = UserUtils.getGalleriesCurrentUserCanAdminister();
        		List<RoleType> roleTypes = RoleType.getRoleTypes("g");
        		List<Long> roles = RoleUtils.getMDSRoles().stream().filter(r->roleTypes.contains(r.getRoleType()) 
        				&& galleries.stream().anyMatch(g->r.getGalleries().contains(g))).map(r->r.getRoleId()).collect(Collectors.toList());
        		searchable.addSearchFilter("id", SearchOperator.in, roles);
        	}
        }
        
    	if (StringUtils.isBlank(searchTerm))
    		return toSelect2Data(roleDao.find(searchable).getContent());
    	else {
    		searchable.setPage(null);
    		List<Role> roles = roleDao.findAll(searchable);
    		
    		return toSelect2Data(roles.stream().filter(r->r.getName().contains(searchTerm) || r.getDescription().contains(searchTerm))
    				.skip(offset).limit(limit).collect(Collectors.toList()));
    	}
       
        //return toSelect2Data(roleDao.search(searchable, new String[]{"name", "description"}, searchTerm).getContent());
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> rolesOrganizationSelect2(String organizationId, String rtype, String searchTerm, Integer limit, Integer offset) throws InvalidMDSRoleException {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
    	searchable.addSort(Direction.ASC, "name");
        searchable.setPage(pageable);
        UserAccount user = UserUtils.getUser();
    	long oId = StringUtils.toLong(organizationId);
    	if (!user.isSystem()) {
    		searchable.addSearchFilter("organization.id", SearchOperator.eq, oId);
    	}else {
    		if (!Role.isIllegalId(oId)) {
    			searchable.addSearchFilter("organization.id", SearchOperator.eq, oId);
    		}
    	}
    	
    	if (!UserUtils.hasRoleType(RoleType.sa)) {
        	//if (UserUtils.hasRoleType(new RoleType[] {RoleType.oa, RoleType.ou, RoleType.og})) {
        	if (UserUtils.hasRoleType(RoleType.oa)) {
        		List<Long> userOrganizationIds = UserUtils.getUserOrganizationIds(UserUtils.getLoginName());
        		searchable.addSearchFilter("organization.id", SearchOperator.in, userOrganizationIds);
        	//}else if (UserUtils.hasRoleType(new RoleType[] {RoleType.ga, RoleType.gu, RoleType.gg})) {
        	}else if (UserUtils.hasRoleType(RoleType.ga)) {
        		GalleryBoCollection galleries = UserUtils.getGalleriesCurrentUserCanAdminister();
        		List<RoleType> roleTypes = RoleType.getRoleTypes("g");
        		List<Long> roles = RoleUtils.getMDSRoles().stream().filter(r->roleTypes.contains(r.getRoleType()) 
        				&& galleries.stream().anyMatch(g->r.getGalleries().contains(g))).map(r->r.getRoleId()).collect(Collectors.toList());
        		searchable.addSearchFilter("id", SearchOperator.in, roles);
        	}
        }
    	if (StringUtils.isNotBlank(rtype)) {
    		searchable.addSearchFilter("type", SearchOperator.in, RoleType.getRoleTypesCanManager(rtype));
    	}
        
    	if (StringUtils.isBlank(searchTerm))
    		return toSelect2Data(roleDao.find(searchable).getContent());
    	else {
    		searchable.setPage(null);
    		List<Role> roles = roleDao.findAll(searchable);
    		
    		return toSelect2Data(roles.stream().filter(r->r.getName().contains(searchTerm) || r.getDescription().contains(searchTerm))
    				.skip(offset).limit(limit).collect(Collectors.toList()));
    	}
       
        //return toSelect2Data(roleDao.search(searchable, new String[]{"name", "description"}, searchTerm).getContent());
    }
    
    /**
     * {@inheritDoc}
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> rolesTable(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) throws Exception {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "name");
        searchable.setPage(pageable);
        if (!UserUtils.hasRoleType(RoleType.sa)) {
        	//if (UserUtils.hasRoleType(new RoleType[] {RoleType.oa, RoleType.ou, RoleType.og})) {
        	if (UserUtils.hasRoleType(RoleType.oa)) {
        		List<Long> userOrganizationIds = UserUtils.getUserOrganizationIds(UserUtils.getLoginName());
        		searchable.addSearchFilter("organization.id", SearchOperator.in, userOrganizationIds);
        	//}else if (UserUtils.hasRoleType(new RoleType[] {RoleType.ga, RoleType.gu, RoleType.gg})) {
        	}else if (UserUtils.hasRoleType(RoleType.ga)) {
        		GalleryBoCollection galleries = UserUtils.getGalleriesCurrentUserCanAdminister();
        		List<RoleType> roleTypes = RoleType.getRoleTypes("g");
        		List<Long> roles = RoleUtils.getMDSRoles().stream().filter(r->roleTypes.contains(r.getRoleType()) 
        				&& galleries.stream().anyMatch(g->r.getGalleries().contains(g))).map(r->r.getRoleId()).collect(Collectors.toList());
        		searchable.addSearchFilter("id", SearchOperator.in, roles);
        	}
        }
        
    	Page<Role> list =  null;
    	if (StringUtils.isBlank(searchTerm)){
    		list = roleDao.find(searchable);
    	}else {
    		list = roleDao.search(searchable, searchTerm);
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", toBootstrapTableData(list.getContent(), request));
		
		return resultData;
    }
    
    /**
	 * convert role data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param roles
	 * @return
     * @throws Exception 
	 */
	private  List<HashMap<String,Object>> toBootstrapTableData(List<Role> roles, HttpServletRequest request) throws Exception{
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (Role u : roles) {
			//role list		
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("name", u.getName());//role name
			mapData.put("type", u.getType());//role type
			mapData.put("roleType", I18nUtils.getRoleType(u.getType(), request));//role type title
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
	@Transactional
    @Override
    public void removeRole(final String roleIds) throws WebApplicationException{
        log.debug("removing role: " + roleIds);
        try {
        	roleDao.remove(ConvertUtil.StringtoLongArray(roleIds));
        	//UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Role(id=" + roleIds + ") was successfully deleted.");
        //return Response.ok().build();
    }
    
    /**
	 * convert role data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param roles
	 * @return
	 */
	private  HashMap<String,Object> toSelect2Data(List<Role> roles){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (Role u : roles) {
			//role list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getName());//role name
			mapData.put("selected", false);//status
			mapData.put("id", u.getName());//role id
			list.add(mapData);
		}
		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
		resultData.put("results", list);
		
		return resultData;
	}
	
	/**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<Map<String,Object>> menuFunctionPermissionTreeView(String roleId, HttpServletRequest request) throws InvalidMDSRoleException {
    	Searchable searchable = Searchable.newSearchable();
    	//searchable.addSearchFilter("id", SearchOperator.ne, 1L);
    	searchable.addSearchFilter("isShow", SearchOperator.eq, true);	
        searchable.addSort(Direction.ASC, "sort");
        List<MenuFunction> userlist = UserUtils.getMenuFunctionList(false);
        if (userlist != null) {
        	searchable.addSearchFilter("id", SearchOperator.in, userlist.stream().map(m->m.getId()).collect(Collectors.toList())); //.add(MenuFunction.getRootId())
        }
        List<MenuFunction> list = menuFunctionDao.findAll(searchable);
        Role role = get(new Long(roleId));
        //List<MenuFunction> menuFunctions = list.stream().filter(c->c.isRoot()).collect(Collectors.toList());
        List<Long> menuFunctionIds = list.stream().map(c->c.getId()).collect(Collectors.toList());
        List<MenuFunction> menuFunctions = list.stream().filter(c->c.getParent() == null || c.getParent().getId() == null || !menuFunctionIds.contains(c.getParent().getId())).collect(Collectors.toList());
        /*for(MenuFunction menuFunction : topMenuFunctions) {
        	menuFunction.setParent(menuFunctions.get(0));
        }*/
        
        List<Map<String,Object>> resultData = new LinkedList<Map<String,Object>>();
		for (MenuFunction menuFunction : menuFunctions) {
			resultData = toBSTreeData(list, menuFunction, role.getMenuFunctionPermissions(), resultData, request).getLeft();
		}

    	return resultData;
    }
    
    /**
     * {@inheritDoc}
     * @throws InvalidMDSRoleException 
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<Map<String,Object>> organizationsTreeView(String roleId, HttpServletRequest request) throws InvalidMDSRoleException {
    	Searchable searchable = Searchable.newSearchable();
    	//searchable.addSearchFilter("id", SearchOperator.ne, 1L);
    	searchable.addSearchFilter("available", SearchOperator.eq, true);	
        searchable.addSort(Direction.ASC, "parent.id", "code");
        UserAccount user = UserUtils.getUser();
        if (!user.isSystem()) {
        	List<Long> userOrganizationIds = UserUtils.getUserOrganizationIds(user.getUsername());
        	//userOrganizationIds.add(Organization.getRootId());
        	searchable.addSearchFilter("id", SearchOperator.in, userOrganizationIds);
        }
        
        List<Organization> list = organizationDao.findAll(searchable);
        Role role = get(new Long(roleId));
        List<Long> organizationIds = list.stream().map(c->c.getId()).collect(Collectors.toList());
        List<Organization> organizations = list.stream().filter(c->c.getParent() == null || c.getParent().getId() == null || !organizationIds.contains(c.getParent().getId())).collect(Collectors.toList());
        if (role.getOrganization() != null) {
        	organizations = list.stream().filter(c->c.getId() == role.getOrganization().getId()).collect(Collectors.toList());
        }
        
        List<Map<String,Object>> resultData = new LinkedList<Map<String,Object>>();
		for (Organization organization : organizations) {
			resultData = toBSTreeData(list, organization, role.getOrganizations(), resultData);
		}

    	return resultData;
    }
    
    /**
	 * convert organization permission data to bootstarp tree format
	 * @param albums
	 * @return
	 */
	private  List<Map<String,Object>> toBSTreeData(List<Organization> organizations, Organization organization, List<Organization> roleOrganizations, List<Map<String,Object>> resultData){
		//menu function list
		Map<String,Object> map = new LinkedHashMap<String, Object>();
		map.put("text", organization.getCode());//album name
		map.put("href", "javascript:void(0)");//link
		map.put("id", organization.getId());//album id
		Map<String,Object> mapState = new LinkedHashMap<String, Object>();
		mapState.put("checked", roleOrganizations.contains(organization));
		map.put("state", mapState);
		
		List<Organization> ps = organizations.stream().filter(c->c.getParentId() == organization.getId()).collect(Collectors.toList());
		List<Map<String,Object>> list = new LinkedList<Map<String,Object>>();
		if(null != ps && ps.size() > 0){
			//child menufunctions
			for (Organization up : ps) {
				list = toBSTreeData(organizations, up, roleOrganizations, list);
			}
		}
		
		map.put("tags",  new Integer[]{ps.size()});//show child data size
		if (!list.isEmpty())
			map.put("nodes", list);

		resultData.add(map);
		
		return resultData;
	}
    
    
    /**
	 * convert Menu Function permission data to bootstarp tree format
	 * @param albums
	 * @return
     * @throws InvalidMDSRoleException 
	 */
	private  Pair<List<Map<String,Object>>, Boolean> toBSTreeData(List<MenuFunction> menuFunctions, MenuFunction menuFunction
			, List<MenuFunctionPermission> menuFunctionPermissions, List<Map<String,Object>> resultData, HttpServletRequest request) throws InvalidMDSRoleException{
		//menu function list
		Map<String,Object> map = new LinkedHashMap<String, Object>();
		map.put("text", I18nUtils.getString(menuFunction.getTitle(), request.getLocale()));//album name
		map.put("href", "javascript:void(0)");//link
		map.put("id", menuFunction.getId());//album id
		List<MenuFunction> ps = menuFunctions.stream().filter(c->c.getParentId() == menuFunction.getId()).collect(Collectors.toList());
		List<Map<String,Object>> list = new LinkedList<Map<String,Object>>();
		boolean checkAll = true;
		if(null != ps && ps.size() > 0){
			//child menufunctions
			for (MenuFunction up : ps) {
				/*Map<String,Object> mapx = new LinkedHashMap<String, Object>();
				mapx.put("text", up.getCode());//menu or function name
				//mapx.put("href", up.getUrl());//menu function url
				//mapx.put("tags", "0");//
				mapx.put("id", up.getId());//menu function id
				list.add(mapx);*/
				Pair<List<Map<String,Object>>, Boolean> result = toBSTreeData(menuFunctions, up, menuFunctionPermissions, list, request);
				list = result.getLeft();
				if (!result.getRight()) {
					checkAll = false;
				}
			}
		}
		
		//permission list
		List<MenuFunctionPermission> currMenuFunctionPermissions = UserUtils.getUserMenuFunctionPermissions().stream()
				.filter(mp->mp.getMenuFunction().getId() == menuFunction.getId()).collect(Collectors.toList());
		if (currMenuFunctionPermissions.isEmpty()) {
			checkAll = false;
		}else {
			for (MenuFunctionPermission menuPermission : currMenuFunctionPermissions) { //menuFunction.getMenuFunctionPermissions()
				Map<String,Object> mapx = new LinkedHashMap<String, Object>();
				mapx.put("text", menuPermission.getPermission().getName());//menu or function name
				//mapx.put("href", up.getUrl());//menu function url
				//mapx.put("tags", "0");//
				mapx.put("id", menuPermission.getId());//menu function id .getPermission()
				Map<String,Object> mapState = new LinkedHashMap<String, Object>();
				if (menuFunctionPermissions.contains(menuPermission)) {
					mapState.put("checked", true);
				}else {
					mapState.put("checked", false);
					checkAll = false;
				}
				mapx.put("state", mapState);
				list.add(mapx);
			}
		}
		map.put("tags",  new Integer[]{ps.size() + currMenuFunctionPermissions.size()});//show child data size
		if (!list.isEmpty())
			map.put("nodes", list);
		Map<String,Object> mapStateUp = new LinkedHashMap<String, Object>();
		mapStateUp.put("checked", checkAll);
		map.put("state", mapStateUp);

		resultData.add(map);
		
		return new ImmutablePair<List<Map<String,Object>>, Boolean>(resultData, checkAll);
	}
	
	/**
     * {@inheritDoc}
     */
    @Override
    public String getCacheKey() {
    	return CacheItem.sys_roles.toString();
    }
}