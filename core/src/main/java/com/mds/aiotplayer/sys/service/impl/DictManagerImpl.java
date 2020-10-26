/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service.impl;

import com.mds.aiotplayer.sys.dao.DictDao;
import com.mds.aiotplayer.sys.model.Dict;
import com.mds.aiotplayer.sys.model.DictCategory;
import com.mds.aiotplayer.sys.service.DictManager;
import com.mds.aiotplayer.sys.service.DictService;
import com.mds.aiotplayer.util.CacheUtils;
import com.mds.aiotplayer.util.ConvertUtil;
import com.mds.aiotplayer.util.DictUtils;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.core.CacheItem;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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
import javax.jws.WebService;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("dictManager")
@WebService(serviceName = "DictService", endpointInterface = "com.mds.aiotplayer.sys.service.DictService")
public class DictManagerImpl extends GenericManagerImpl<Dict, Long> implements DictManager, DictService {
    DictDao dictDao;

    @Autowired
    public DictManagerImpl(DictDao dictDao) {
        super(dictDao);
        this.dictDao = dictDao;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Dict> find(Pageable page, Dict dict) {
		return null;
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
	public List<String> findTypeList(){
		return dictDao.findTypeList();
	}
		
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
	public void removeDict(Long id) {
		dictDao.removeById(id);
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
	}

	
	
	/**
     * {@inheritDoc}
     */
    @Override
    public Dict getDict(final String dictId) {
        return dictDao.get(new Long(dictId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Dict> getDicts() {
    	log.debug("get all dicts from db");
        return dictDao.getAllDistinct();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<Dict> searchDicts(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return dictDao.search(pageable, new String[]{"word", "value"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> dictsSelect2(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	if (StringUtils.isBlank(searchTerm))
    		return toSelect2Data(dictDao.find(pageable).getContent(), request);
       
        return toSelect2Data(dictDao.search(pageable, new String[]{"word", "value"}, searchTerm).getContent(), request);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> dictsTable(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "category", "word");
        searchable.setPage(pageable);
        
    	Page<Dict> list =  null;
    	if (StringUtils.isBlank(searchTerm)){
    		list = dictDao.find(searchable);
    	}else {
    		list = dictDao.search(searchable, searchTerm);
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", toBootstrapTableData(list.getContent(), request));
		
		return resultData;
		
    }
    
    /**
	 * convert dict data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param dicts
	 * @return
	 */
	private  List<HashMap<String,Object>> toBootstrapTableData(List<Dict> dicts, HttpServletRequest request){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (Dict u : dicts) {
			//dict list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("description", u.getDescription());//dict description
			mapData.put("word", u.getWord());//dict name
			mapData.put("category", u.getCategory());//dict
			mapData.put("id", u.getId());//dict id
			mapData.put("sort", u.getSort());//is show
			mapData.put("value", u.getValue());//dict message key
			list.add(mapData);
		}
				
		return list;
	}

    /**
     * {@inheritDoc}
     */
	@Transactional
    @Override
    public Dict saveDict(final Dict dict) throws RecordExistsException {

        try {
            return dictDao.saveDict(dict);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("Dict '" + dict.getWord() + "' already exists!");
        }
    }

    /**
     * {@inheritDoc}
     */
	@Transactional
    @Override
    public void removeDict(final String dictIds) throws WebApplicationException{
        log.debug("removing dict: " + dictIds);
        try {
        	dictDao.remove(ConvertUtil.StringtoLongArray(dictIds));
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Dict(id=" + dictIds + ") was successfully deleted.");
        //return Response.ok().build();
    }
    
    /**
	 * convert dict data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param dicts
	 * @return
	 */
	private  HashMap<String,Object> toSelect2Data(List<Dict> dicts, HttpServletRequest request){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (Dict u : dicts) {
			//dict list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getWord());//dict name
			mapData.put("selected", false);//status
			mapData.put("id", u.getId());//dict id
			list.add(mapData);
		}
		
		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
		resultData.put("results", list);
		
		return resultData;
	}
	
	public String getCacheKey() {
    	return CacheItem.sys_dicts.toString();
    }
}