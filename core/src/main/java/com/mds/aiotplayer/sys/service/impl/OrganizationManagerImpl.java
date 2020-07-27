package com.mds.aiotplayer.sys.service.impl;

import com.mds.aiotplayer.sys.dao.OrganizationDao;
import com.mds.aiotplayer.sys.dao.OrganizationLogoDao;
import com.mds.aiotplayer.sys.dao.RoleDao;
import com.mds.aiotplayer.sys.exception.OrganizationExistsException;
import com.mds.aiotplayer.sys.model.Organization;
import com.mds.aiotplayer.sys.model.OrganizationLogo;
import com.mds.aiotplayer.sys.model.Role;
import com.mds.aiotplayer.sys.model.RoleType;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.service.OrganizationManager;
import com.mds.aiotplayer.sys.service.OrganizationService;
import com.mds.aiotplayer.util.CacheUtils;
import com.mds.aiotplayer.util.ConvertUtil;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.aiotplayer.sys.util.MDSRole;
import com.mds.aiotplayer.sys.util.MDSRoleCollection;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.common.model.Parameter;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.core.CacheItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.jws.WebService;
import javax.ws.rs.core.Response;

@Service("organizationManager")
@WebService(serviceName = "OrganizationService", endpointInterface = "com.mds.aiotplayer.sys.service.OrganizationService")
public class OrganizationManagerImpl extends GenericManagerImpl<Organization, Long> implements OrganizationManager, OrganizationService {
    OrganizationDao organizationDao;
    OrganizationLogoDao organizationLogoDao;
    RoleDao roleDao;

    @Autowired
    public OrganizationManagerImpl(OrganizationDao organizationDao) {
        super(organizationDao);
        this.organizationDao = organizationDao;
    }
    
    @Autowired
    public void setOrganizationLogoDao(OrganizationLogoDao organizationLogoDao) {
        this.organizationLogoDao = organizationLogoDao;
    }
    
    @Autowired
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
	public void removeOrganization(Long id) {
		organizationDao.remove(id);
		//UserUtils.removeCache(UserUtils.CACHE_ORGANIZATION_LIST);
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Organization getOrganization(final String organizationId) {
        return organizationDao.get(new Long(organizationId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Organization> getOrganizations() {
    	log.debug("get all organizations from db");
        return organizationDao.getAllDistinct();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Organization saveOrganization(final Organization organization) throws OrganizationExistsException {   	
    	boolean isNew = (organization.getId() == null);
        try {
        	if (!isNew) {
	        	Searchable searchable = Searchable.newSearchable();
	    		searchable.addSearchFilter("organization.id", SearchOperator.eq, organization.getId());	
	        	List<OrganizationLogo> organizationLogos = organizationLogoDao.findAll(searchable);
	        	if (!organizationLogos.isEmpty()) {
	        		//Set<OrganizationLogo> saves = new HashSet<OrganizationLogo>();
		        	List<OrganizationLogo> newLogos = Lists.newArrayList();
	    			for(OrganizationLogo organizationLogo : organization.getOrganizationLogos()) {
	    				if (organizationLogo.getId() != null) {
		    				Optional<OrganizationLogo> optional = organizationLogos.stream()
		    						.filter(m->m.getId() == organizationLogo.getId()).findFirst();
		    				if (optional.isPresent()) {
		    					OrganizationLogo exists = optional.get();
		    					organizationLogo.setId(exists.getId());
		    					/*exists.setOrganization(result);
		    					exists.setOrganization(organizationLogo.getOrganization());
		
		    					saves.add(organizationLogoDao.save(exists));*/
		        				organizationLogos.remove(exists);
		    				}else {
		    					newLogos.add(organizationLogo);
		    				}
	    				}else {
	    					newLogos.add(organizationLogo);
	    				}
	    			}
	    			
	    			for(OrganizationLogo organizationLogo : newLogos) {
	    				if (!organizationLogos.isEmpty()){
	    					OrganizationLogo exists = organizationLogos.get(0);
	    					organizationLogo.setId(exists.getId());
	    					/*exists.setOrganization(result);
	    					exists.setOrganization(organizationLogo.getOrganization());
	
	    					saves.add(organizationLogoDao.save(exists));*/
	        				organizationLogos.remove(exists);
	    				}/*else {
	    					organizationLogo.setOrganization(result);
	
	        				saves.add(organizationLogoDao.save(organizationLogo));
	    				}*/
	    			}
	        	}
	        	organizationLogoDao.clear();
	        	organizationDao.clear();
	        	
	        	if (!organizationLogos.isEmpty()){
    				organizationLogoDao.remove(organizationLogos);
    			}
    			//result.setOrganizationLogos(saves);
        	}
        	Organization result =  organizationDao.saveOrganization(organization);
        	if (isNew && organization.getParent() != null && !UserUtils.getUser().isSystem()) {
	        	MDSRoleCollection roles = RoleUtils.getMDSRolesForUser(UserUtils.getLoginName());
	        	for(MDSRole role : roles) {
	        		if ((role.getOrganizationId() == organization.getParent().getId() && role.getRoleType() == RoleType.oa) 
	        			|| role.getOrganizationIds().contains(organization.getParent().getId())) {
	        			Role roleDto = roleDao.getRoleByName(role.getRoleName());
	        			roleDto.getOrganizations().add(result);
	        			roleDao.save(roleDto);
	        		}
	        	}
	        	CacheUtils.remove(CacheItem.MDSRoles);
        	}
        	
            //UserUtils.removeCache(UserUtils.CACHE_ORGANIZATION_LIST);
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new OrganizationExistsException("Organization '" + organization.getName() + "' already exists!");
        }
    }

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void removeOrganization(final String organizationIds) {
        log.debug("removing organization: " + organizationIds);
        organizationDao.remove(ConvertUtil.StringtoLongArray(organizationIds));
        //UserUtils.removeCache(UserUtils.CACHE_ORGANIZATION_LIST);
    }
    
    /**
	 * convert organization data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param organizations
	 * @return
	 */
	private  HashMap<String,Object> toSelect2Data(List<Organization> organizations){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (Organization u : organizations) {
			//organization list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getName());//organization name
			mapData.put("selected", false);//status
			mapData.put("id", u.getId());//organization id
			list.add(mapData);
		}
		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
		resultData.put("results", list);
		
		return resultData;
	}

	@Override
	public Organization getOrganizationByOrganizationname(String organizationname) {
		return organizationDao.getByHql("from Organization where name = :p1", new Parameter(organizationname));
	}

	/**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<Organization> searchOrganizations(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return organizationDao.search(pageable, new String[]{"code", "name"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> organizationsSelect2(String searchTerm, Integer limit, Integer offset) {
    	if (StringUtils.isBlank(searchTerm))
    		return null;
    	
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return toSelect2Data(organizationDao.search(pageable, new String[]{"name", "email"}, searchTerm).getContent());
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> organizationsTable(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Page<Organization> list =  null;
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "parent.id", "code");
        searchable.setPage(pageable);
    	if (StringUtils.isBlank(searchTerm)){
    		list = organizationDao.find(searchable);
    	}else {
    		list = organizationDao.search(searchable, searchTerm);
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", list.getContent());
		
		return resultData;
		
    }

    
    @Override
    public Response getLogo(String organizationId){
        // uncomment line below to send non-streamed
        return Response.ok(organizationDao.get(new Long(organizationId)).getLogo()).build();
        // uncomment line below to send streamed
        // return Response.ok(new ByteArrayInputStream(imageData)).build();
    }

	/**
     * {@inheritDoc}
	 * @throws InvalidMDSRoleException 
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> organizationsTreeTable(String searchTerm, Integer limit, Integer offset) throws InvalidMDSRoleException {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Page<Organization> list =  null;
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "parent.id", "code");
        searchable.setPage(pageable);
        UserAccount user = UserUtils.getUser();
    	if (user != null && !user.isSystem()){
    		List<Long> userOrganizationIds = UserUtils.getUserOrganizationIds(user.getUsername());
			searchable.addSearchFilter("id", SearchOperator.in, userOrganizationIds);
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	if (user != null) {
	    	if (StringUtils.isBlank(searchTerm)){
	    		list = organizationDao.find(searchable);
	    	}else {
	    		list = organizationDao.search(searchable, searchTerm);
	    	}
	    	resultData.put("total", list.getTotalElements());
			resultData.put("rows", toTreeTable(list.getContent()));
    	}else {
    		resultData.put("total", 0);
			resultData.put("rows", null);
    	}

		return resultData;
    }
    
    @Override
    public List<Map<String,Object>> organizationChildrenTreeTable(String organizationId, Integer limit, Integer offset){
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Page<Organization> list =  null;
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "parent.id", "code");
        searchable.setPage(pageable);
    	list = organizationDao.find(searchable); 	
    	
    	List<Map<String,Object>> resultData = new LinkedList<Map<String,Object>>();
    	Organization organization = list.getContent().stream().filter(o->o.getId() == new Long(organizationId)).findFirst().get();
    	
		return toBSTreeData(list.getContent(), organization, resultData);
    }
    
    @Override
    public List<Map<String,Object>> organizationTreeView(String organizationId, Integer limit, Integer offset) throws InvalidMDSRoleException{
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	List<Organization> list =  null;
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "parent.id", "code");
        searchable.setPage(pageable);
        long oId = StringUtils.toLong(organizationId);
        List<Organization> organizations = null;
        if (oId != Long.MIN_VALUE && oId > 0) {
        	//List<Organization> organizations = list.getContent().stream().filter(o->o.getId() == oId).collect(Collectors.toList());
        	list = organizationDao.find(searchable).getContent();
        	organizations = list.stream().filter(o->o.getId() == oId).collect(Collectors.toList());
        }else {
        	UserAccount user = UserUtils.getUser();
	        if (!user.isSystem()) {
	        	List<Long> userOrganizationIds = UserUtils.getUserOrganizationIds(user.getUsername());
	        	searchable.addSearchFilter("id", SearchOperator.in, userOrganizationIds);
	        }
	        list = organizationDao.find(searchable).getContent();
	        List<Long> organizationIds = list.stream().map(c->c.getId()).collect(Collectors.toList());
	        organizations = list.stream().filter(c->c.getParent() == null || c.getParent().getId() == null || !organizationIds.contains(c.getParent().getId())).collect(Collectors.toList());
        }
    	
    	List<Map<String,Object>> resultData = new LinkedList<Map<String,Object>>();
    	for(Organization organization : organizations) {
    		resultData = toBSTreeData(list, organization, resultData);
    	}
    	
    	    	
		return resultData;
    }
    
    /**
	 * convert organization data to bootstrap table tree grid format(http://issues.wenzhixin.net.cn/bootstrap-table/#extensions/treegrid.html)
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
	 * @param list: organizations
	 * @return
	 */
    public List<Map<String, Object>> toTreeTable(List<Organization> list) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		for (int i=0; i<list.size(); i++){
			Organization e = list.get(i);
			if (e.isRoot())
				continue;
			
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pid", e.isTop() ? 0 : e.getParent().getId());
			map.put("code", e.getCode());
			map.put("name", e.getName());
			map.put("header", e.getHeader());
			map.put("area", e.getArea().getCode());
			map.put("available", e.isAvailable());
			map.put("preferredlanguage", e.getPreferredlanguage().getCultureCode());
							
			mapList.add(map);
		}
		
		return mapList;
	}
    
    /**
	 * convert album data to bootstarp tree format
	 * @param albums
	 * @return
	 */
	private  List<Map<String,Object>> toBSTreeData(List<Organization> organizations, Organization organization, List<Map<String,Object>> resultData){
		//organization list
		Map<String,Object> map = new LinkedHashMap<String, Object>();
		map.put("text", organization.getCode());//album name
		map.put("href", "javascript:void(0)");//link
		map.put("id", organization.getId());//album id
		List<Organization> ps = organizations.stream().filter(c->c.getParentId() == organization.getId()).collect(Collectors.toList());
		List<Map<String,Object>> list = new LinkedList<Map<String,Object>>();
		if(null != ps && ps.size() > 0){
			//child organizations
			for (Organization up : ps) {
				resultData = toBSTreeData(organizations, up, resultData);
			}
		}
		
		map.put("tags",  new Integer[]{ps.size() });//show child data size
		if (!list.isEmpty())
			map.put("nodes", list);

		resultData.add(map);
		
		return resultData;
	}
    
    @Override
    public List<HashMap<String, Object>> organizationsTreeSelector(Long excludeId) throws InvalidMDSRoleException {
		List<HashMap<String, Object>> mapList = Lists.newArrayList();
		Searchable searchable = Searchable.newSearchable();
    	//searchable.addSearchFilter("id", SearchOperator.ne, 1L);
    	searchable.addSearchFilter("available", SearchOperator.eq, true);	
        searchable.addSort(Direction.ASC, "parent.id", "code");
        UserAccount user = UserUtils.getUser();
        if (!user.isSystem()) {
        	List<Long> userOrganizationIds = UserUtils.getUserOrganizationIds(user.getUsername());
        	userOrganizationIds.add(Organization.getRootId());
        	searchable.addSearchFilter("id", SearchOperator.in, userOrganizationIds);
        }
		List<Organization> list = organizationDao.findAll(searchable);
		List<Long> organizationIds = list.stream().map(c->c.getId()).collect(Collectors.toList());
        List<Organization> organizations = list.stream().filter(c->c.getParent() == null || c.getParent().getId() == null || !organizationIds.contains(c.getParent().getId())).collect(Collectors.toList());
        
		List<Long> parentIds = list.stream().filter(c->!c.getParentIds().contains(excludeId)).map(Organization::getId).collect(Collectors.toList());
		for (int i=0; i<list.size(); i++){
			Organization e = list.get(i);
			if (e.isRoot())
				continue;
			
			if (excludeId == null || (excludeId!=null && excludeId != e.getId() && parentIds.contains(e.getId()))){
				HashMap<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.isTop() ? 0 : (organizations.contains(e) ? 1 : e.getParent().getId()));
				map.put("name", e.getName());
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
    	return CacheItem.sys_organizations.toString();
    }
}