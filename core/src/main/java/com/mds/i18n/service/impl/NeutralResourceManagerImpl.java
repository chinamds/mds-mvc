package com.mds.i18n.service.impl;

import com.mds.i18n.dao.LocalizedResourceDao;
import com.mds.i18n.dao.NeutralResourceDao;
import com.mds.i18n.model.NeutralResource;
import com.mds.i18n.service.NeutralResourceManager;
import com.mds.i18n.service.NeutralResourceService;
import com.mds.sys.dao.DictDao;
import com.mds.sys.model.DictCategory;
import com.mds.util.ConvertUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.model.search.SearchOperator;
import com.mds.common.model.search.Searchable;
import com.mds.common.model.search.filter.SearchFilter;
import com.mds.common.model.search.filter.SearchFilterHelper;
import com.mds.common.service.impl.GenericManagerImpl;
import com.mds.common.utils.Reflections;
import com.mds.common.utils.SpringContextHolder;
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
import javax.ws.rs.core.Response;

@Service("neutralResourceManager")
@WebService(serviceName = "NeutralResourceService", endpointInterface = "com.mds.i18n.service.NeutralResourceService")
public class NeutralResourceManagerImpl extends GenericManagerImpl<NeutralResource, Long> implements NeutralResourceManager, NeutralResourceService {
    NeutralResourceDao neutralResourceDao;
    LocalizedResourceDao localizedResourceDao;

    @Autowired
    public void setLocalizedResourceDao(LocalizedResourceDao localizedResourceDao) {
        this.localizedResourceDao = localizedResourceDao;
    }

    @Autowired
    public NeutralResourceManagerImpl(NeutralResourceDao neutralResourceDao) {
        super(neutralResourceDao);
        this.neutralResourceDao = neutralResourceDao;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public NeutralResource getNeutralResource(final String neutralResourceId) {
        return neutralResourceDao.get(Long.valueOf(neutralResourceId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NeutralResource> getNeutralResources() {
    	log.debug("get all neutral resources from db");
        return neutralResourceDao.getAllDistinct();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<NeutralResource> getShowNeutralResources(Integer limit, Integer offset){
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
			return neutralResourceDao.findAll(searchable);
		}else {
			searchable.setPage(PageRequest.of(offset/limit, limit));
			return neutralResourceDao.find(searchable).getContent();
		}
    }
    
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<NeutralResource> searchNeutralResources(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return neutralResourceDao.search(pageable, new String[]{"resourceClass", "resourceKey", "value"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> neutralResourcesSelect2(String category, String searchTerm, Integer limit, Integer offset) {
    	if (StringUtils.isBlank(searchTerm))
    		return null;
    	
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return toSelect2Data(neutralResourceDao.search(pageable, new String[]{"resourceClass", "resourceKey", "value"}, searchTerm).getContent());
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> notLocalizedNeutralResourcesSelect2(String culture, String searchTerm, Integer limit, Integer offset) {
		/*
		 * if (StringUtils.isBlank(searchTerm)) return null;
		 */
    	
    	var neutralResouceIds = localizedResourceDao.findNeutralIds(Long.valueOf(culture));
    	Searchable searchable = Searchable.newSearchable();
    	searchable.setPage(PageRequest.of(offset/limit, limit));
    	if (neutralResouceIds.size() > 0) {
    		searchable.addSearchFilter("id", SearchOperator.notIn, neutralResouceIds);
    	}
    	
    	if (StringUtils.isBlank(searchTerm)) {
    		return toSelect2Data(neutralResourceDao.find(searchable).getContent());
    	}
       
        return toSelect2Data(neutralResourceDao.search(searchable, new String[]{"resourceKey", "value"}, StringUtils.isBlank(searchTerm) ? "*" : searchTerm).getContent());
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> neutralResourcesTable(String category, String searchTerm, Integer limit, Integer offset) {
    	Searchable searchable = Searchable.newSearchable();
    	searchable.setPage(PageRequest.of(offset/limit, limit));
    	
    	Page<NeutralResource> list =  null;
    	if (StringUtils.isBlank(searchTerm)) {
			searchable.addSearchFilter("resourceClass", SearchOperator.eq, category);	
			
	    	list =  findPaging(searchable);
    	}else {
    		searchable.addSearchFilter("resourceClass", SearchOperator.eq, category);
            
            list =  neutralResourceDao.search(searchable, searchTerm);	
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
    public HashMap<String, Object> neutralResourcesTable(String searchTerm, Integer limit, Integer offset) {
    	Searchable searchable = Searchable.newSearchable();
    	searchable.setPage(PageRequest.of(offset/limit, limit));
    	searchable.addSort(Direction.ASC, "resourceClass", "resourceKey");
    	
    	Page<NeutralResource> list =  null;
    	if (StringUtils.isBlank(searchTerm)) {			
	    	list =  findPaging(searchable);
    	}else {          
            list =  neutralResourceDao.search(searchable, searchTerm);	
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
    public NeutralResource saveNeutralResource(final NeutralResource neutralResource) throws RecordExistsException {

        try {
            return neutralResourceDao.saveNeutralResource(neutralResource);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("Neutral Resource '" + neutralResource.getResourceKey() + "' already exists!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response removeNeutralResource(final String neutralResourceIds) {
        log.debug("removing neutralResource: " + neutralResourceIds);
        neutralResourceDao.remove(ConvertUtil.StringtoLongArray(neutralResourceIds));
        
        log.info("Content Mapping(id=" + neutralResourceIds + ") was successfully deleted.");
        return Response.ok().build();
    }
    
    /**
	 * convert neutralResource data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param neutralResources
	 * @return
	 */
	private  HashMap<String,Object> toSelect2Data(List<NeutralResource> neutralResources){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (NeutralResource u : neutralResources) {
			//neutralResource list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getResourceKey());//neutralResource name
			mapData.put("selected", false);//status
			mapData.put("id", u.getId());//neutralResource id
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
    public List<NeutralResource> importFrom(List<NeutralResource> entities, String[] uniqueKeys){
    	Map<String, Object> mapDict = Maps.newHashMap();
    	Searchable searchable = null;
    	List<NeutralResource> saved = Lists.newArrayList();
    	for (NeutralResource entity : entities){
			searchable = Searchable.newSearchable();
			for(String field : uniqueKeys){
				searchable.addSearchFilter(field, SearchOperator.eq, Reflections.invokeGetter(entity, field));	
			}
            
			saved.add(dao.addOrUpdate(entity, searchable));
			if (!mapDict.containsKey(entity.getResourceClass()))
				mapDict.put(entity.getResourceClass(), entity.getResourceClass());
		}
    	DictDao dictDao = SpringContextHolder.getBean(DictDao.class);
    	dictDao.saveDicts(DictCategory.i18n.toString(), mapDict);
    	
    	return saved;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCacheKey() {
    	return CacheItem.i18n_neutralresources.toString();
    }
}