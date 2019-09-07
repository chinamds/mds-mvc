package com.mds.i18n.service.impl;

import com.mds.i18n.dao.CultureDao;
import com.mds.i18n.model.Culture;
import com.mds.i18n.service.CultureManager;
import com.mds.i18n.service.CultureService;
import com.mds.util.ConvertUtil;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.model.search.Searchable;
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
import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("cultureManager")
@WebService(serviceName = "CultureService", endpointInterface = "com.mds.i18n.service.CultureService")
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
        return cultureDao.get(new Long(cultureId));
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
		resultData.put("rows", list.getContent());
		
		return resultData;
		
    }

    /**
     * {@inheritDoc}
     */
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
    @Override
    public void removeCulture(final String cultureIds) throws WebApplicationException{
        log.debug("removing culture: " + cultureIds);
        try {
        	cultureDao.remove(ConvertUtil.StringtoLongArray(cultureIds));
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
    @Override
    public String getCacheKey() {
    	return CacheItem.i18n_cultures.toString();
    }
}