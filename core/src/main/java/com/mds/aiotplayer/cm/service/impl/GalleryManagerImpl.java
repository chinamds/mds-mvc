package com.mds.aiotplayer.cm.service.impl;

import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.content.GalleryBo;
import com.mds.aiotplayer.cm.dao.GalleryDao;
import com.mds.aiotplayer.cm.dao.GalleryMappingDao;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.model.Gallery;
import com.mds.aiotplayer.cm.model.GalleryMapping;
import com.mds.aiotplayer.cm.service.GalleryManager;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.sys.dao.OrganizationDao;
import com.mds.aiotplayer.sys.dao.UserDao;
import com.mds.aiotplayer.sys.model.MenuFunction;
import com.mds.aiotplayer.sys.model.Role;
import com.mds.aiotplayer.sys.model.RoleType;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.core.CacheItem;
import com.mds.aiotplayer.cm.service.GalleryService;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.util.CacheUtils;
import com.mds.aiotplayer.util.ConvertUtil;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.sys.util.UserUtils;

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
import java.util.stream.Collectors;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("galleryManager")
@WebService(serviceName = "GalleryService", endpointInterface = "com.mds.aiotplayer.cm.service.GalleryService")
public class GalleryManagerImpl extends GenericManagerImpl<Gallery, Long> implements GalleryManager, GalleryService {
    GalleryDao galleryDao;
    GalleryMappingDao galleryMappingDao;
    OrganizationDao organizationDao;
    UserDao userDao;

    @Autowired
    public GalleryManagerImpl(GalleryDao galleryDao) {
        super(galleryDao);
        this.galleryDao = galleryDao;
    }
    
    @Autowired
    public void setGalleryManagerDao(GalleryMappingDao galleryMappingDao) {
        this.galleryMappingDao = galleryMappingDao;
    }
    
    @Autowired
    public void setOrganizationDao(OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }
    
    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

		/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
	public void removeGallery(Long id) {
		galleryDao.remove(id);
	}

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Gallery saveGallery(final Gallery gallery) throws RecordExistsException {
   	
        try {
        	Gallery result =  galleryDao.save(gallery);
        	//CacheUtils.remove(CacheItem.Albums.toString());
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("Gallery '" + gallery.getName() + "' already exists!");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Gallery saveGalleryWithMapping(Gallery gallery, List<GalleryMapping> galleryMappings) throws RecordExistsException{
    	try {
        	Gallery result =  galleryDao.save(gallery);
        	//CacheUtils.remove(CacheItem.Albums.toString());
        	for (GalleryMapping galleryMapping : galleryMappings)
        	{
        		galleryMapping.setGallery(result);
        	}
        	galleryMappingDao.save(galleryMappings);
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("Gallery '" + gallery.getName() + "' already exists!");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Gallery saveGalleryWithMapping(Gallery gallery, List<Long> organizations, List<Long> users) throws RecordExistsException{
    	List<GalleryMapping> galleryMappings = Lists.newArrayList();
    	if (!gallery.isNew()) {
	    	Searchable searchable = Searchable.newSearchable();
			searchable.addSearchFilter("gallery.id", SearchOperator.eq, gallery.getId());
			galleryMappings = galleryMappingDao.findAll(searchable);
    	}
		
    	try {
        	Gallery result =  galleryDao.save(gallery);
        	if (users != null && !users.isEmpty()) {
        		galleryMappingDao.remove(galleryMappings.stream().filter(g->g.getUser() != null && !users.contains(g.getUser().getId())).collect(Collectors.toList()));
				for (long user : users) {
					if (!galleryMappings.stream().anyMatch(g->g.getUser() != null && g.getUser().getId() == user )) {
						galleryMappingDao.save(new GalleryMapping(result, null, userDao.get(user)));
					}
				}
        	}else{
        		galleryMappingDao.remove(galleryMappings.stream().filter(g->g.getUser() != null).collect(Collectors.toList()));
        	}
        	if (organizations != null && !organizations.isEmpty()) {
        		galleryMappingDao.remove(galleryMappings.stream().filter(g->g.getOrganization() != null && !organizations.contains(g.getOrganization().getId())).collect(Collectors.toList()));
				for (long organization : organizations) {
					if (!galleryMappings.stream().anyMatch(g->g.getOrganization() != null && g.getOrganization().getId() == organization )) {
						galleryMappingDao.save(new GalleryMapping(result, organizationDao.get(organization), null));
					}
				}
        	}else{
        		galleryMappingDao.remove(galleryMappings.stream().filter(g->g.getOrganization() != null).collect(Collectors.toList()));
        	}
    	            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("Gallery '" + gallery.getName() + "' already exists!");
        }
    }

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Response removeGallery(final String galleryIds) {
        log.debug("removing gallery: " + galleryIds);
        try {
	        galleryDao.remove(ConvertUtil.StringtoLongArray(galleryIds));
	        //CacheUtils.remove(CacheItem.Albums.toString());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Gallery(id=" + galleryIds + ") was successfully deleted.");
        return Response.ok().build();
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public Gallery getGallery(final String galleryId) {
        return galleryDao.get(new Long(galleryId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Gallery> getGalleries() {
    	log.debug("get all galleries from db");
        return galleryDao.getAllDistinct();
    }
    
    public List<Gallery> findGalleries(long organizationId){
    	return galleryDao.findGalleries(organizationId);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<Gallery> searchGalleries(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return galleryDao.search(pageable, new String[]{"name", "description"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     * @throws InvalidGalleryException 
     * @throws InvalidMDSRoleException 
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> galleriesSelect2(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) throws InvalidGalleryException, InvalidMDSRoleException {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "name");
        searchable.setPage(pageable);
        
        UserAccount user = UserUtils.getUser();
        if (!user.isSystem()) {
	        Long[] ids = CMUtils.loadLoginUserGalleries(user).stream().map(g->g.getGalleryId()).toArray(Long[]::new);
	    	searchable.addSearchFilter("id", SearchOperator.in, ids);
        }else {
        	searchable.addSearchFilter("isTemplate", SearchOperator.eq, false);
        }
        
    	if (StringUtils.isBlank(searchTerm))
    		return toSelect2Data(galleryDao.find(searchable).getContent(), request);
       
        return toSelect2Data(galleryDao.search(pageable, new String[]{"name", "description"}, searchTerm).getContent(), request);
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> galleriesOrganizationSelect2(String organizationId, String searchTerm, Integer limit, Integer offset, HttpServletRequest request) throws InvalidGalleryException, InvalidMDSRoleException {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "name");
        searchable.setPage(pageable);
        
        long oId = StringUtils.toLong(organizationId);
       	Long[] ids = CMUtils.loadGalleries().stream().filter(g->g.getOrganizations().contains(oId)).map(g->g.getGalleryId()).toArray(Long[]::new);
    	searchable.addSearchFilter("id", SearchOperator.in, ids);
        
    	if (StringUtils.isBlank(searchTerm))
    		return toSelect2Data(galleryDao.find(searchable).getContent(), request);
       
        return toSelect2Data(galleryDao.search(searchable, new String[]{"name", "description"}, searchTerm).getContent(), request);
    }
    
    /**
     * {@inheritDoc}
     * @throws InvalidGalleryException 
     * @throws InvalidMDSRoleException 
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> galleriesTable(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) throws InvalidGalleryException, InvalidMDSRoleException {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "name");
        searchable.setPage(pageable);
        
        UserAccount user = UserUtils.getUser();
        if (!user.isSystem()) {
	        Long[] ids = CMUtils.loadLoginUserGalleries(user).stream().map(g->g.getGalleryId()).toArray(Long[]::new);
	    	searchable.addSearchFilter("id", SearchOperator.in, ids);
        }
        
    	Page<Gallery> list =  null;
    	if (StringUtils.isBlank(searchTerm)){
    		list = galleryDao.find(searchable);
    	}else {
    		list = galleryDao.search(searchable, searchTerm);
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", toBootstrapTableData(list.getContent(), request));
		
		return resultData;
    }
    
    /**
     * {@inheritDoc}
     * @throws InvalidMDSRoleException 
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> galleriesTable(String userId, String searchTerm, Integer limit, Integer offset, HttpServletRequest request) throws InvalidMDSRoleException {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "name");
        searchable.setPage(pageable);
        
    	Page<Gallery> list =  null;
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	
    	User user = UserUtils.getUserById(new Long(userId));
    	if (user != null && !user.isSystem()){
    		List<Long> galleries = Lists.newArrayList();
			galleries.addAll(user.getGalleryMappings().stream().map(g->g.getGallery().getId()).collect(Collectors.toList()));
			if (user.getOrganization() != null && UserUtils.hasRoleType(RoleType.oa))
				galleries.addAll(user.getOrganization().getGalleryMappings().stream().map(g->g.getGallery().getId()).collect(Collectors.toList()));
			for(Role role:user.getRoles()) {
				if (role.getType() == RoleType.oa) {
					galleries.addAll(role.getGalleries().stream().map(g->g.getId()).collect(Collectors.toList()));
				}
			}
			searchable.addSearchFilter("id", SearchOperator.in, galleries);
    	}
    	
    	if (user != null) {
			if (StringUtils.isBlank(searchTerm)){
	    		list = galleryDao.find(searchable);
	    	}else {
	    		list = galleryDao.search(searchable, searchTerm);
	    	}
			
			resultData.put("total", list.getTotalElements());
			resultData.put("rows", toBootstrapTableData(list.getContent(), request));
    	}else {
    		resultData.put("total", 0);
			resultData.put("rows", null);
    	}
  	   			
		return resultData;
    }
    
    /**
	 * convert gallery data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param galleries
	 * @return
	 */
	private  List<HashMap<String,Object>> toBootstrapTableData(List<Gallery> galleries, HttpServletRequest request){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (Gallery u : galleries) {
			//gallery list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("description", u.getDescription());//gallery description
			mapData.put("organizations", u.getOrganizations());//gallery name
			mapData.put("name", u.getName());//gallery name
			mapData.put("id", u.getId());//gallery id
			mapData.put("isTemplate", u.isIsTemplate());//is template
			list.add(mapData);
		}
				
		return list;
	}   
    
    /**
	 * convert gallery data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param galleries
	 * @return
     * @throws InvalidGalleryException 
	 */
	private  HashMap<String,Object> toSelect2Data(List<Gallery> galleries, HttpServletRequest request) throws InvalidGalleryException{
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (Gallery u : galleries) {
			//gallery list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getTitle());//gallery name
			mapData.put("selected", false);//status
			mapData.put("id", u.getId());//gallery id
			list.add(mapData);
		}
		
		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
		resultData.put("results", list);
		
		return resultData;
	}
	
	public String getCacheKey() {
    	return CacheItem.cm_galleries.toString();
    }
}