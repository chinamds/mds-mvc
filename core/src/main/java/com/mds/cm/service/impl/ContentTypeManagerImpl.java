package com.mds.cm.service.impl;

import com.mds.common.model.search.SearchOperator;
import com.mds.common.model.search.Searchable;
import com.google.common.collect.Lists;
import com.mds.cm.dao.ContentTypeDao;
import com.mds.cm.model.ContentType;
import com.mds.cm.service.ContentTypeManager;
import com.mds.common.exception.RecordExistsException;
import com.mds.sys.model.MenuFunction;
import com.mds.sys.model.Role;
import com.mds.sys.model.RoleType;
import com.mds.sys.model.User;
import com.mds.common.service.impl.GenericManagerImpl;
import com.mds.core.CacheItem;
import com.mds.cm.service.ContentTypeService;
import com.mds.util.CacheUtils;
import com.mds.util.ConvertUtil;
import com.mds.i18n.util.I18nUtils;
import com.mds.sys.util.UserUtils;

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
import java.util.stream.Collectors;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("contentTypeManager")
@WebService(serviceName = "ContentTypeService", endpointInterface = "com.mds.cm.service.ContentTypeService")
public class ContentTypeManagerImpl extends GenericManagerImpl<ContentType, Long> implements ContentTypeManager, ContentTypeService {
    ContentTypeDao contentTypeDao;

    @Autowired
    public ContentTypeManagerImpl(ContentTypeDao contentTypeDao) {
        super(contentTypeDao);
        this.contentTypeDao = contentTypeDao;
    }

		/**
     * {@inheritDoc}
     */
    @Override
	public void removeContentType(Long id) {
		contentTypeDao.remove(id);
	}

	/**
     * {@inheritDoc}
     */
    @Override
    public ContentType saveContentType(final ContentType contentType) throws RecordExistsException {
   	
        try {
        	ContentType result =  contentTypeDao.save(contentType);
        	//CacheUtils.remove(CacheItem.Albums.toString());
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("ContentType '" + contentType.getContentName() + "' already exists!");
        }
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public Response removeContentType(final String contentTypeIds) {
        log.debug("removing contentType: " + contentTypeIds);
        try {
	        contentTypeDao.remove(ConvertUtil.StringtoLongArray(contentTypeIds));
	        //CacheUtils.remove(CacheItem.Albums.toString());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("ContentType(id=" + contentTypeIds + ") was successfully deleted.");
        return Response.ok().build();
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public ContentType getContentType(final String contentTypeId) {
        return contentTypeDao.get(new Long(contentTypeId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ContentType> getContentTypes() {
    	log.debug("get all content types from db");
        return contentTypeDao.findAll();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<ContentType> searchContentTypes(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return contentTypeDao.search(pageable, new String[]{"name", "description"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> contentTypesSelect2(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	if (StringUtils.isBlank(searchTerm))
    		return toSelect2Data(contentTypeDao.find(pageable).getContent(), request);
       
        return toSelect2Data(contentTypeDao.search(pageable, new String[]{"name", "description"}, searchTerm).getContent(), request);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> contentTypesTable(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "name");
        searchable.setPage(pageable);
        
    	Page<ContentType> list =  null;
    	if (StringUtils.isBlank(searchTerm)){
    		list = contentTypeDao.find(searchable);
    	}else {
    		list = contentTypeDao.search(searchable, searchTerm);
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", toBootstrapTableData(list.getContent(), request));
		
		return resultData;
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> contentTypesTable(String userId, String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "name");
        searchable.setPage(pageable);
        
    	Page<ContentType> list =  null;
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	    	
		if (StringUtils.isBlank(searchTerm)){
    		list = contentTypeDao.find(searchable);
    	}else {
    		list = contentTypeDao.search(searchable, searchTerm);
    	}
		
		resultData.put("total", list.getTotalElements());
		resultData.put("rows", toBootstrapTableData(list.getContent(), request));
  	   			
		return resultData;
    }
    
    /**
	 * convert contentType data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param contentTypes
	 * @return
	 */
	private  List<HashMap<String,Object>> toBootstrapTableData(List<ContentType> contentTypes, HttpServletRequest request){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (ContentType u : contentTypes) {
			//contentType list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("code", u.getCode());//contentType description
			mapData.put("type", u.getType());//contentType name
			mapData.put("contentName", u.getContentName());//contentType name
			mapData.put("id", u.getId());//contentType id
			mapData.put("fileFilter", u.getFileFilter());//is template
			list.add(mapData);
		}
				
		return list;
	}   
    
    /**
	 * convert contentType data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param contentTypes
	 * @return
	 */
	private  HashMap<String,Object> toSelect2Data(List<ContentType> contentTypes, HttpServletRequest request){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (ContentType u : contentTypes) {
			//contentType list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getContentName());//contentType name
			mapData.put("selected", false);//status
			mapData.put("id", u.getId());//contentType id
			list.add(mapData);
		}
		
		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
		resultData.put("results", list);
		
		return resultData;
	}
	
	public String getCacheKey() {
    	return CacheItem.cm_contenttypes.toString();
    }
}