package com.mds.cm.service.impl;

import com.mds.common.model.search.SearchOperator;
import com.mds.common.model.search.Searchable;
import com.mds.cm.content.ContentEncoderSettingsCollection;
import com.mds.cm.content.GallerySettings;
import com.mds.cm.dao.GallerySettingDao;
import com.mds.cm.metadata.MetadataDefinitionCollection;
import com.mds.cm.model.GallerySetting;
import com.mds.cm.service.GallerySettingManager;
import com.mds.cm.service.GallerySettingService;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.service.impl.GenericManagerImpl;
import com.mds.common.utils.Reflections;
import com.mds.core.CacheItem;
import com.mds.core.exception.ArgumentNullException;
import com.mds.util.CacheUtils;
import com.mds.util.ConvertUtil;
import com.mds.util.DateUtils;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("gallerySettingManager")
@WebService(serviceName = "GallerySettingService", endpointInterface = "com.mds.cm.service.GallerySettingService")
public class GallerySettingManagerImpl extends GenericManagerImpl<GallerySetting, Long> implements GallerySettingManager, GallerySettingService {
    GallerySettingDao gallerySettingDao;

    @Autowired
    public GallerySettingManagerImpl(GallerySettingDao gallerySettingDao) {
        super(gallerySettingDao);
        this.gallerySettingDao = gallerySettingDao;
    }
    
    public Map<String, Object> getGallerySettingsMap(){
    	List<GallerySetting> gallerySettings = gallerySettingDao.getAll();
    	
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	for(GallerySetting gallerySetting : gallerySettings){
    		resultMap.put(gallerySetting.getSettingName(), gallerySetting.getSettingValue());
    	}
    	
    	return resultMap;
    }

		/**
     * {@inheritDoc}
     */
    @Override
	public void removeGallerySetting(Long id) {
		gallerySettingDao.remove(id);
	}

	/**
     * {@inheritDoc}
     */
    @Override
    public GallerySetting saveGallerySetting(final GallerySetting gallerySetting) throws RecordExistsException {
    	
        try {
        	GallerySetting result =  gallerySettingDao.save(gallerySetting);
        	//CacheUtils.remove(CacheItem.Albums.toString());
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("GallerySetting '" + gallerySetting.getSettingName() + "' already exists!");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveGallerySettings(GallerySettings gallerySettings) {
    	if (gallerySettings == null)
            throw new ArgumentNullException("gallerySettings");
    	
    	List<GallerySetting> gsDtos = gallerySettingDao.getAll();
    	Field[] fs = gallerySettings.getClass().getDeclaredFields();
		for (Field prop : fs){
			if (prop == null || StringUtils.isBlank(prop.getName()))
				continue;
			
			Class<?> valType = prop.getType();
	        // Get a reference to the database record (won't exist for new items).
	        String propName = StringUtils.capitalize(prop.getName());
			GallerySetting gsDto = gsDtos.stream().filter(g->g.getSettingName() == propName).findFirst().orElse(null);
			if (gsDto == null)
				continue;
						
	        Object val = Reflections.getFieldValue(gallerySettings, prop.getName());
	        String propValue = null;
	        if (val != null)  {
	        	if (val instanceof String) {
	        		propValue = (String) val;
				} else if (val instanceof Integer) {
					propValue = ((Integer) val).toString();
				} else if (val instanceof Long) {
					propValue = ((Long) val).toString();
				} else if (val instanceof Double) {
					propValue = ((Double) val).toString();
				} else if (val instanceof Float) {
					propValue = ((Float) val).toString();
				} else if (val instanceof Boolean) {
					propValue = ((Boolean)val).toString();
				} else if (val instanceof Date) {
					propValue = DateUtils.formatDateTime(((Date)val));
				} else if (val instanceof String[]) {
					propValue = StringUtils.join((String [])val, ",");
				} else if (val instanceof MetadataDefinitionCollection) {
					propValue = ((MetadataDefinitionCollection)val).serialize();
				} else if (val instanceof ContentEncoderSettingsCollection) {
					propValue = ((ContentEncoderSettingsCollection)val).serialize();
				} else {
					propValue = val.toString();
				}      	
	        }

	        if (gsDto != null) {
        		gsDto.setSettingValue(propValue);
        		gallerySettingDao.save(gsDto);
	        }
		}

	    if (gallerySettings.getContentObjectPathIsReadOnly()) {
			// This section resolves bug#599: Error creating gallery when current gallery has read-only content objects path
			// When user saves a read only gallery, we update the template gallery to have the same settings. When a new gallery is subsequently 
			// created, the default values are likely to be ones that work and not generate an error message. Without this step, a new gallery
			// will be created with a path of ds\contentobjects and ContentObjectPathIsReadOnly=false, which will likely fail when the code
			// checks whether the IIS app pool can write to the directory.
	    	Searchable searchable = Searchable.newSearchable();
	        searchable.addSearchFilter("gallery.isTemplate", SearchOperator.eq, true);
	        searchable.addSearchFilter("settingName", SearchOperator.eq, "ContentObjectPathIsReadOnly");
	        GallerySetting gs = gallerySettingDao.findOne(searchable);
	        if (gs != null) {
	        	gs.setSettingValue(Boolean.toString(gallerySettings.getContentObjectPathIsReadOnly()));
	        	gallerySettingDao.save(gs);
	        }
	        searchable = Searchable.newSearchable();
	        searchable.addSearchFilter("gallery.isTemplate", SearchOperator.eq, true);
	        searchable.addSearchFilter("settingName", SearchOperator.eq, "ContentObjectPath");
	        gs = gallerySettingDao.findOne(searchable);
	        if (gs != null) {
	        	gs.setSettingValue(gallerySettings.getContentObjectPath().toString());
	        	gallerySettingDao.save(gs);
	        }
	        searchable = Searchable.newSearchable();
	        searchable.addSearchFilter("gallery.isTemplate", SearchOperator.eq, true);
	        searchable.addSearchFilter("settingName", SearchOperator.eq, "SynchAlbumTitleAndDirectoryName");
	        gs = gallerySettingDao.findOne(searchable);
	        if (gs != null) {
	        	gs.setSettingValue(Boolean.toString(gallerySettings.getSynchAlbumTitleAndDirectoryName()));
	        	gallerySettingDao.save(gs);
	        }
	        searchable = Searchable.newSearchable();
	        searchable.addSearchFilter("gallery.isTemplate", SearchOperator.eq, true);
	        searchable.addSearchFilter("settingName", SearchOperator.eq, "ThumbnailPath");
	        gs = gallerySettingDao.findOne(searchable);
	        if (gs != null) {
	        	gs.setSettingValue(gallerySettings.getThumbnailPath().toString());
	        	gallerySettingDao.save(gs);
	        }
	        
	        searchable = Searchable.newSearchable();
	        searchable.addSearchFilter("gallery.isTemplate", SearchOperator.eq, true);
	        searchable.addSearchFilter("settingName", SearchOperator.eq, "OptimizedPath");
	        gs = gallerySettingDao.findOne(searchable);
	        if (gs != null) {
	        	gs.setSettingValue(gallerySettings.getOptimizedPath().toString());
	        	gallerySettingDao.save(gs);
	        }
	    }
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public Response removeGallerySetting(final String gallerySettingIds) {
        log.debug("removing gallerySetting: " + gallerySettingIds);
        try {
	        gallerySettingDao.remove(ConvertUtil.StringtoLongArray(gallerySettingIds));
	      //CacheUtils.remove(CacheItem.Albums.toString());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Gallery Setting(id=" + gallerySettingIds + ") was successfully deleted.");
        return Response.ok().build();
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public GallerySetting getGallerySetting(final String gallerySettingId) {
        return gallerySettingDao.get(new Long(gallerySettingId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GallerySetting> getGallerySettings() {
    	log.debug("get all gallerySettings from db");
        return gallerySettingDao.getAllDistinct();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<GallerySetting> searchGallerySettings(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return gallerySettingDao.search(pageable, new String[]{"name", "description"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> gallerySettingsSelect2(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	if (StringUtils.isBlank(searchTerm))
    		return toSelect2Data(gallerySettingDao.find(pageable).getContent(), request);
       
        return toSelect2Data(gallerySettingDao.search(pageable, new String[]{"name", "description"}, searchTerm).getContent(), request);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> gallerySettingsTable(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "name");
        searchable.setPage(pageable);
        
    	Page<GallerySetting> list =  null;
    	if (StringUtils.isBlank(searchTerm)){
    		list = gallerySettingDao.find(searchable);
    	}else {
    		list = gallerySettingDao.search(searchable, searchTerm);
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", toBootstrapTableData(list.getContent(), request));
		
		return resultData;
		
    }
    
    /**
	 * convert gallerySetting data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param gallerySettings
	 * @return
	 */
	private  List<HashMap<String,Object>> toBootstrapTableData(List<GallerySetting> gallerySettings, HttpServletRequest request){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (GallerySetting u : gallerySettings) {
			//gallerySetting list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("value", u.getSettingValue());//gallerySetting description
			mapData.put("name", u.getSettingName());//gallerySetting name
			mapData.put("id", u.getId());//gallerySetting id
			list.add(mapData);
		}
				
		return list;
	}
    
    /**
	 * convert gallerySetting data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param gallerySettings
	 * @return
	 */
	private  HashMap<String,Object> toSelect2Data(List<GallerySetting> gallerySettings, HttpServletRequest request){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (GallerySetting u : gallerySettings) {
			//gallerySetting list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getSettingName());//gallerySetting name
			mapData.put("selected", false);//status
			mapData.put("id", u.getId());//gallerySetting id
			list.add(mapData);
		}
		
		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
		resultData.put("results", list);
		
		return resultData;
	}
	
	public String getCacheKey() {
    	return CacheItem.sys_gallerySettings.toString();
    }
}