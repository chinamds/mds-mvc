/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.mds.aiotplayer.cm.dao.UiTemplateDao;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.cm.model.UiTemplate;
import com.mds.aiotplayer.cm.service.UiTemplateManager;
import com.mds.aiotplayer.cm.service.UiTemplateService;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.core.UiTemplateType;
import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.ConvertUtil;
import com.mds.aiotplayer.util.StringUtils;

@Service("uiTemplateManager")
@WebService(serviceName = "UiTemplateService", endpointInterface = "com.mds.aiotplayer.cm.service.UiTemplateService")
public class UiTemplateManagerImpl extends GenericManagerImpl<UiTemplate, Long> implements UiTemplateManager, UiTemplateService {
    UiTemplateDao uiTemplateDao;

    @Autowired
    public UiTemplateManagerImpl(UiTemplateDao uiTemplateDao) {
        super(uiTemplateDao);
        this.uiTemplateDao = uiTemplateDao;
    }
    
    /**
	 * {@inheritDoc}
	 */
	@Override
	public UiTemplate getUiTemplate(final String uiTemplateId) {
	    return uiTemplateDao.get(Long.valueOf(uiTemplateId));
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<UiTemplate> getUiTemplate(){
    	log.debug("get all UI template from db");
        return uiTemplateDao.getAllDistinct();
    }

	/**
     * {@inheritDoc}
     */
    @Override
	public void removeUiTemplate(Long id) {
		uiTemplateDao.remove(id);
		//CacheUtils.remove(CacheItem.UiTemplates.toString());
	}
    
    /**
     * {@inheritDoc}
     * @throws InvalidDCMSystemRoleException 
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> uiTemplatesSelect2(String oId, String templateType, String searchTerm, Integer limit, Integer offset) throws InvalidMDSRoleException {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "templateType", "name");
        searchable.setPage(pageable);
        
        var organizationId = Long.MIN_VALUE;
        if (StringUtils.isNotBlank(oId)) {
        	organizationId = StringUtils.toLong(oId);
        }
        
        if (organizationId == Long.MIN_VALUE) {
	        UserAccount user = UserUtils.getUser();
	        if (!user.isSystem()) {
	        	List<Long> userOrganizationIds = UserUtils.getUserOrganizationIds(user.getUsername());
	        	searchable.addSearchFilter("organization.id", SearchOperator.in, userOrganizationIds);
	        }
        }else {
        	searchable.addSearchFilter("organization.id", SearchOperator.eq, organizationId);
        }
        
        List<UiTemplateType> gots = new ArrayList<>();
        if (!StringUtils.isBlank(templateType)) {
        	UiTemplateType got = UiTemplateType.NotSpecified;
        	String[] templateTypes = StringUtils.split(templateType, ',');
        	for(var ft : templateTypes) {
				try {
					got = UiTemplateType.valueOf(templateType);
					if (got != UiTemplateType.NotSpecified) {
						gots.add(got);
					}
				}catch(Exception ex) {
				}
        	}
        }
		
		if (gots.size() > 0) {
			searchable.addSearchFilter("templateType", SearchOperator.in, gots);
		}
        
    	if (StringUtils.isBlank(searchTerm))
    		return toSelect2Data(uiTemplateDao.find(searchable).getContent());
       
        return toSelect2Data(uiTemplateDao.search(searchable, new String[]{"name", "description"}, searchTerm).getContent());
    }
    
    /**
   	 * convert uiTemplate data to select2 format(https://select2.org/data-sources/formats)
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
   	 * @param uiTemplates
   	 * @return
   	 */
   	private  HashMap<String,Object> toSelect2Data(List<UiTemplate> uiTemplates){
   		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
   		for (UiTemplate u : uiTemplates) {
   			//uiTemplate list
   			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
   			mapData.put("text", u.getName());//uiTemplate name
   			mapData.put("selected", false);//status
   			mapData.put("id", u.getId());//uiTemplate id
   			list.add(mapData);
   		}
   		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
   		resultData.put("results", list);
   		
   		return resultData;
   	}
    
    /**
     * {@inheritDoc}
     * @throws InvalidDCMSystemRoleException 
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> uiTemplatesTable(String searchTerm, Integer limit, Integer offset) throws InvalidMDSRoleException {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "templateType", "name");
        searchable.setPage(pageable);
        UserAccount user = UserUtils.getUser();
        if (!user.isSystem()) {
        	List<Long> galleryIds = UserUtils.getGalleriesCurrentUserCanAdminister().stream().map(g->g.getGalleryId()).collect(Collectors.toList());;
        	searchable.addSearchFilter("gallery.id", SearchOperator.in, galleryIds);
        }
        
    	Page<UiTemplate> list =  null;
    	if (StringUtils.isBlank(searchTerm)){
    		list = uiTemplateDao.find(searchable);
    	}else {
    		list = uiTemplateDao.search(searchable, searchTerm);
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", list.getContent());
		
		return resultData;
		
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public UiTemplate saveUiTemplate(final UiTemplate uiTemplate) throws RecordExistsException {   	
        try {
        	UiTemplate result =  uiTemplateDao.save(uiTemplate);
        	//CacheUtils.remove(CacheItem.UiTemplates.toString());
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("UiTemplate '" + uiTemplate.getName() + "' already exists!");
        }
    }
    
	/**
     * {@inheritDoc}
     */
    @Override
    public Response removeUiTemplate(final String uiTemplateIds) {
        log.debug("removing uiTemplate: " + uiTemplateIds);
        try {
	        uiTemplateDao.remove(ConvertUtil.stringtoLongArray(uiTemplateIds));
	        //CacheUtils.remove(CacheItem.UiTemplates.toString());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("UI Template(id=" + uiTemplateIds + ") was successfully deleted.");
        return Response.ok().build();
    }
}