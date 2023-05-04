/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.i18n.service.impl;

import com.mds.aiotplayer.i18n.dao.CultureDao;
import com.mds.aiotplayer.i18n.model.Culture;
import com.mds.aiotplayer.i18n.service.CultureManager;
import com.mds.aiotplayer.i18n.service.CultureService;
import com.mds.aiotplayer.util.ConvertUtil;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.core.CacheItem;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("cultureManager")
@WebService(serviceName = "CultureService", endpointInterface = "com.mds.aiotplayer.i18n.service.CultureService")
public class CultureManagerImpl extends GenericManagerImpl<Culture, Long> implements CultureManager, CultureService {
    CultureDao cultureDao;

    @Autowired
    public CultureManagerImpl(CultureDao cultureDao) {
        super(cultureDao);
        this.cultureDao = cultureDao;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public Culture getCulture(final String cultureId) {
        return cultureDao.get(Long.valueOf(cultureId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Culture> getCultures() {
    	log.debug("get all cultures from db");
        return cultureDao.getAllDistinct();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<Culture> searchCultures(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return cultureDao.search(pageable, new String[]{"cultureCode", "cultureName"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> culturesSelect2(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	if (StringUtils.isBlank(searchTerm))
    		return toSelect2Data(cultureDao.find(pageable).getContent());
       
        return toSelect2Data(cultureDao.search(pageable, new String[]{"cultureCode", "cultureName"}, searchTerm).getContent());
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> availableCulturesSelect2(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
        final Locale[] available = Locale.getAvailableLocales();
        List<Culture> cultures = Lists.newArrayList();
        for (int i = 0; i < available.length; i++) {
        	String languageTag = available[i].getLanguage();
        	if (StringUtils.isNotBlank(available[i].getCountry())){
        		languageTag =  languageTag + "_" + available[i].getCountry();
        	}
        	
        	if (StringUtils.isBlank(searchTerm) || languageTag.startsWith(searchTerm) 
        			|| available[i].getDisplayName().startsWith(searchTerm)) {
        		cultures.add(new Culture(languageTag, available[i].getDisplayName()));
        	}
			/*
			 * final String iso = available[i].getCountry(); final String name =
			 * available[i].getDisplayCountry(locale);
			 */
		}
        if (cultures.size() == 0) {
        	for (int i = 0; i < available.length; i++) {
            	String languageTag = available[i].getLanguage();
            	if (StringUtils.isNotBlank(available[i].getCountry())){
            		languageTag =  languageTag + "_" + available[i].getCountry();
            	}
            	
            	if (StringUtils.isBlank(searchTerm) || languageTag.contains(searchTerm) 
            			|| available[i].getDisplayName().contains(searchTerm)) {
            		cultures.add(new Culture(languageTag, available[i].getDisplayName()));
            	}
    		}
        }

        cultures = cultures.stream().sorted(Comparator.comparing(Culture::getCultureCode)).collect(Collectors.toList());
        List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
        for (Culture u : cultures) {           
			//culture list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getCultureName());//culture name
			mapData.put("selected", false);//status
			mapData.put("id", u.getCultureCode());//culture id
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
    public HashMap<String, Object> culturesTable(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "cultureCode");
        searchable.setPage(pageable);
        
    	Page<Culture> list =  null;
    	if (StringUtils.isBlank(searchTerm)){
    		list = cultureDao.find(searchable);
    	}else {
    		list = cultureDao.search(searchable, searchTerm);
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", toBootstrapTableData(list.getContent()));
		
		return resultData;
		
    }
    
    /**
	 * convert permission data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param permissions
	 * @return
	 */
	private  List<HashMap<String,Object>> toBootstrapTableData(List<Culture> cultures){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (Culture u : cultures) {
			//permission list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("cultureName", u.getCultureName());//culture description
			mapData.put("cultureCode", u.getCultureCode());//culture name
			mapData.put("id", u.getId());//permission id
			list.add(mapData);
		}
				
		return list;
	}

    /**
     * {@inheritDoc}
     */
	@Transactional
    @Override
    public Culture saveCulture(final Culture culture) throws RecordExistsException {

        try {
            return cultureDao.saveCulture(culture);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("Culture '" + culture.getCultureCode() + "' already exists!");
        }
    }

    /**
     * {@inheritDoc}
     */
	@Transactional
    @Override
    public void removeCulture(final String cultureIds) throws WebApplicationException{
        log.debug("removing culture: " + cultureIds);
        try {
        	cultureDao.remove(ConvertUtil.stringtoLongArray(cultureIds));
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Culture(id=" + cultureIds + ") was successfully deleted.");
        //return Response.ok().build();
    }
    
    /**
	 * convert culture data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param cultures
	 * @return
	 */
	private  HashMap<String,Object> toSelect2Data(List<Culture> cultures){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (Culture u : cultures) {
			//culture list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getCultureName());//culture name
			mapData.put("selected", false);//status
			mapData.put("id", u.getId());//culture id
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
    	return CacheItem.i18n_cultures.toString();
    }
}