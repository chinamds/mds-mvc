/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.jws.WebService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.common.utils.Reflections;
import com.mds.aiotplayer.core.CacheItem;
import com.mds.aiotplayer.sys.dao.AreaDao;
import com.mds.aiotplayer.sys.exception.AreaExistsException;
import com.mds.aiotplayer.sys.model.Area;
import com.mds.aiotplayer.sys.service.AreaManager;
import com.mds.aiotplayer.sys.service.AreaService;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.ConvertUtil;
import com.mds.aiotplayer.util.excel.fieldcell.TreeCell;

@Service("areaManager")
@WebService(serviceName = "AreaService", endpointInterface = "com.mds.aiotplayer.sys.service.AreaService")
public class AreaManagerImpl extends GenericManagerImpl<Area, Long> implements AreaManager, AreaService {
    AreaDao areaDao;

    @Autowired
    public AreaManagerImpl(AreaDao areaDao) {
        super(areaDao);
        this.areaDao = areaDao;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Area getArea(final String areaId) {
        return areaDao.get(new Long(areaId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Area> getAreas() {
    	log.debug("get all areas from db");
        return areaDao.getAllDistinct();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Area saveArea(final Area area) throws AreaExistsException {
   	
        try {
        	Area result =  areaDao.save(area);
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new AreaExistsException("Area '" + area.getName() + "' already exists!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void removeArea(final Area area) {
        log.debug("removing area: " + area);
        areaDao.remove(area);
    }

    /**
     * {@inheritDoc}
     */
    /*@Override
    public void removeArea(final String areaId) {
        log.debug("removing area: " + areaId);
        areaDao.remove(new Long(areaId));
        UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
    }*/
    
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public List<Area> importFrom(List<Area> entities, String[] uniqueKeys){
    	Searchable searchable = null;
    	List<Area> saved = Lists.newArrayList();
    	for (Area entity : entities){
    		if (!StringUtils.isBlank(entity.getParentCodes())) {
	    		Area parent = UserUtils.getAreaRoot();
	    		StringTokenizer toKenizer = new StringTokenizer(entity.getParentCodes(), " > ");        
	            while (toKenizer.hasMoreElements()) {         
	            	parent = TreeCell.getParent(saved, UserUtils.getAreaList(), toKenizer.nextToken(), parent.getCode());
	            }   
	            entity.setParent(parent);
    		}
    		
			searchable = Searchable.newSearchable();
			for(String field : uniqueKeys){
				searchable.addSearchFilter(field, SearchOperator.eq, Reflections.invokeGetter(entity, field));	
			}
            
			saved.add(dao.addOrUpdate(entity, searchable));
		}
    	
    	return saved;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Area> findAll(){
		return UserUtils.getAreaList();
	}
	
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
	public void remove(Long id) {
		areaDao.remove(id);
		//UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
	}

	@Override
	public Area getAreaByAreaname(String areaname) {
		return (Area) areaDao.loadAreaByAreaname(areaname);
	}
	
	/**
     * {@inheritDoc}
     */
	@Transactional
    @Override
    public void removeArea(final String areaIds) {
        log.debug("removing area: " + areaIds);
        areaDao.remove(ConvertUtil.stringtoLongArray(areaIds));
    }
    
    /**
	 * convert area data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param areas
	 * @return
	 */
	private  HashMap<String,Object> toSelect2Data(List<Area> areas){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (Area u : areas) {
			//area list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getName());//area name
			mapData.put("selected", false);//status
			mapData.put("id", u.getId());//area id
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
    public List<Area> searchAreas(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return areaDao.search(pageable, new String[]{"code", "name"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> areasSelect2(String searchTerm, Integer limit, Integer offset) {
    	if (StringUtils.isBlank(searchTerm))
    		return null;
    	
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return toSelect2Data(areaDao.search(pageable, new String[]{"name", "email"}, searchTerm).getContent());
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> areasTable(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Page<Area> list =  null;
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "parent.id", "code");
        searchable.setPage(pageable);
    	if (StringUtils.isBlank(searchTerm)){
    		list = areaDao.find(searchable);
    	}else {
    		list = areaDao.search(searchable, searchTerm);
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", list.getContent());
		
		return resultData;
		
    }

    
    /*@Override
    public Response getLogo(String areaId){
        // uncomment line below to send non-streamed
        return Response.ok(areaDao.get(new Long(areaId)).getLogo()).build();
        // uncomment line below to send streamed
        // return Response.ok(new ByteArrayInputStream(imageData)).build();
    }*/

	/**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> areasTreeTable(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Page<Area> list =  null;
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "parent.id", "code");
        searchable.setPage(pageable);
    	if (StringUtils.isBlank(searchTerm)){
    		list = areaDao.find(searchable);
    	}else {
    		list = areaDao.search(searchable, searchTerm);
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", toTreeTable(list.getContent()));
		
		return resultData;
    }
    
    /**
	 * convert area data to bootstrap table tree grid format(http://issues.wenzhixin.net.cn/bootstrap-table/#extensions/treegrid.html)
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
	 * @param list: areas
	 * @return
	 */
    public List<Map<String, Object>> toTreeTable(List<Area> list) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		for (int i=0; i<list.size(); i++){
			Area e = list.get(i);
			if (e.isRoot())
				continue;
			
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pid", e.isTop() ? 0 : e.getParent().getId());
			map.put("code", e.getCode());
			map.put("name", e.getName());
			map.put("type", e.getType());
							
			mapList.add(map);
		}
		
		return mapList;
	}
    
    @Override
    public List<HashMap<String, Object>> areasTreeSelector(Long excludeId) {
		List<HashMap<String, Object>> mapList = Lists.newArrayList();
		List<Area> list = getAll();
		List<Long> parentIds = list.stream().filter(c->!c.getParentIds().contains(excludeId)).map(Area::getId).collect(Collectors.toList());
		for (int i=0; i<list.size(); i++){
			Area e = list.get(i);
			if (e.isRoot())
				continue;
			
			if (excludeId == null || (excludeId!=null && excludeId != e.getId() && parentIds.contains(e.getId()))){
				HashMap<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.isTop() ? 0:e.getParent().getId());
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
    	return CacheItem.sys_areas.toString();
    }
}