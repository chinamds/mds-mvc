package com.mds.i18n.service.impl;

import com.mds.i18n.dao.LocalizedResourceDao;
import com.mds.i18n.model.LocalizedResource;
import com.mds.i18n.service.LocalizedResourceManager;
import com.mds.i18n.service.LocalizedResourceService;
import com.mds.util.CacheUtils;
import com.mds.util.ConvertUtil;
import com.mds.i18n.util.I18nUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.model.Parameter;
import com.mds.common.model.search.SearchOperator;
import com.mds.common.model.search.Searchable;
import com.mds.common.model.search.filter.SearchFilter;
import com.mds.common.model.search.filter.SearchFilterHelper;
import com.mds.common.service.impl.GenericManagerImpl;
import com.mds.common.utils.Reflections;
import com.mds.core.CacheItem;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

@Service("localizedResourceManager")
@WebService(serviceName = "LocalizedResourceService", endpointInterface = "com.mds.i18n.service.LocalizedResourceService")
public class LocalizedResourceManagerImpl extends GenericManagerImpl<LocalizedResource, Long> implements LocalizedResourceManager, LocalizedResourceService {
    LocalizedResourceDao localizedResourceDao;

    @Autowired
    public LocalizedResourceManagerImpl(LocalizedResourceDao localizedResourceDao) {
        super(localizedResourceDao);
        this.localizedResourceDao = localizedResourceDao;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public LocalizedResource getLocalizedResource(final String localizedResourceId) {
        return localizedResourceDao.get(new Long(localizedResourceId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LocalizedResource> getLocalizedResources() {
    	log.debug("get all neutral resources from db");
        return localizedResourceDao.getAllDistinct();
    }
    
    public List<LocalizedResource> findByCultureId(Long cultureId){
    	return localizedResourceDao.findByCultureId(cultureId);
    }
    
    public List<Map<Long, Long>> findNeutralMap(Long cultureId){
    	return localizedResourceDao.findNeutralMap(cultureId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<LocalizedResource> getShowLocalizedResources(Integer limit, Integer offset){
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
			return localizedResourceDao.findAll(searchable);
		}else {
			searchable.setPage(PageRequest.of(offset/limit, limit));
			return localizedResourceDao.find(searchable).getContent();
		}
    }
    
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<LocalizedResource> searchLocalizedResources(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return localizedResourceDao.search(pageable, new String[]{"resourceClass", "resourceKey", "value"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> getLocalizedResource(String category, final HttpServletRequest request){
        return I18nUtils.getStrings(null, request.getLocale());
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> localizedResourcesSelect2(String category, String searchTerm, Integer limit, Integer offset) {
    	if (StringUtils.isBlank(searchTerm))
    		return null;
    	
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return toSelect2Data(localizedResourceDao.search(pageable, new String[]{"resourceClass", "resourceKey", "value"}, searchTerm).getContent());
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> localizedResourcesTable(String culture, String searchTerm, Integer limit, Integer offset) {
    	Searchable searchable = Searchable.newSearchable();
    	searchable.setPage(PageRequest.of(offset/limit, limit));
    	
    	Page<LocalizedResource> list =  null;
    	if (StringUtils.isBlank(searchTerm)) {
			searchable.addSearchFilter("culture.cultureCode", SearchOperator.eq, culture);	
			
	    	list =  findPaging(searchable);
    	}else {
    		searchable.addSearchFilter("culture.cultureCode", SearchOperator.ftqFilter, culture, "culture.cultureCode");
            
            list =  localizedResourceDao.search(searchable, searchTerm);	
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
    public HashMap<String, Object> localizedResourcesTable(String searchTerm, Integer limit, Integer offset) {
    	Searchable searchable = Searchable.newSearchable();
    	searchable.setPage(PageRequest.of(offset/limit, limit));
    	searchable.addSort(Direction.ASC, "culture.cultureCode", "neutralResource.resourceClass", "neutralResource.resourceKey");
    	
    	Page<LocalizedResource> list =  null;
    	if (StringUtils.isBlank(searchTerm)) {			
	    	list =  findPaging(searchable);
    	}else {           
            list =  localizedResourceDao.search(searchable, searchTerm);	
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", toBootstrapTableData(list.getContent()));
		
		return resultData;
		
    }
    
    private  List<HashMap<String,Object>> toBootstrapTableData(List<LocalizedResource> localizedResources){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (LocalizedResource u : localizedResources) {
			//localizedResource list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("resourceClass", u.getNeutralResource().getResourceClass());//localizedResource description
			mapData.put("resourceKey", u.getNeutralResource().getResourceKey());//localizedResource description
			mapData.put("cultureCode", u.getCulture().getCultureCode());//localizedResource name
			mapData.put("value", u.getValue());//localizedResource
			mapData.put("id", u.getId());//localizedResource id
			list.add(mapData);
		}
				
		return list;
	} 

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalizedResource saveLocalizedResource(final LocalizedResource localizedResource) throws RecordExistsException {

        try {
        	LocalizedResource result = localizedResourceDao.saveLocalizedResource(localizedResource);
            CacheUtils.remove(I18nUtils.CACHE_I18N_MAP);
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("Localized Resource '" + localizedResource.getNeutralResource().getResourceKey() + "' already exists!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response removeLocalizedResource(final String localizedResourceIds) {
        log.debug("removing localizedResource: " + localizedResourceIds);
        localizedResourceDao.remove(ConvertUtil.StringtoLongArray(localizedResourceIds));
        CacheUtils.remove(I18nUtils.CACHE_I18N_MAP);
        
        log.info("Content Mapping(id=" + localizedResourceIds + ") was successfully deleted.");
        return Response.ok().build();
    }
    
    /**
	 * convert localizedResource data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param localizedResources
	 * @return
	 */
	private  HashMap<String,Object> toSelect2Data(List<LocalizedResource> localizedResources){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (LocalizedResource u : localizedResources) {
			//localizedResource list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getValue());//localizedResource name
			mapData.put("selected", false);//status
			mapData.put("id", u.getNeutralResource().getResourceKey());//localizedResource id
			list.add(mapData);
		}
		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
		resultData.put("results", list);
		
		return resultData;
	}
		    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCacheKey() {
    	return CacheItem.i18n_localizedresources.toString();
    }
}