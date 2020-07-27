package com.mds.aiotplayer.cm.service.impl;

import com.mds.aiotplayer.cm.content.GalleryControlSettings;
import com.mds.aiotplayer.cm.dao.GalleryControlSettingDao;
import com.mds.aiotplayer.cm.model.GalleryControlSetting;
import com.mds.aiotplayer.cm.model.GallerySetting;
import com.mds.aiotplayer.cm.service.GalleryControlSettingManager;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.common.utils.Reflections;
import com.mds.aiotplayer.core.CacheItem;
import com.mds.aiotplayer.core.SlideShowType;
import com.mds.aiotplayer.core.ViewMode;
import com.mds.aiotplayer.util.CacheUtils;
import com.mds.aiotplayer.util.ConvertUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("galleryControlSettingManager")
@WebService(serviceName = "GalleryControlSettingService", endpointInterface = "com.mds.aiotplayer.cm.service.GalleryControlSettingManager")
public class GalleryControlSettingManagerImpl extends GenericManagerImpl<GalleryControlSetting, Long> implements GalleryControlSettingManager {
    GalleryControlSettingDao galleryControlSettingDao;

    @Autowired
    public GalleryControlSettingManagerImpl(GalleryControlSettingDao galleryControlSettingDao) {
        super(galleryControlSettingDao);
        this.galleryControlSettingDao = galleryControlSettingDao;
    }

		/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
	public void removeGalleryControlSetting(Long id) {
		galleryControlSettingDao.remove(id);
		//CacheUtils.remove(CacheItem.ContentTemplates.toString());
	}

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public GalleryControlSetting saveGalleryControlSetting(final GalleryControlSetting galleryControlSetting) throws RecordExistsException {
    	
        try {
        	GalleryControlSetting result =  galleryControlSettingDao.save(galleryControlSetting);
        	//CacheUtils.remove(CacheItem.ContentTemplates.toString());
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("GalleryControlSetting '" + galleryControlSetting.getSettingName() + "' already exists!");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void saveGalleryControlSetting(GalleryControlSettings galleryControlSettings) {
    	List<GalleryControlSetting> gcsDtos = galleryControlSettingDao.getAll();
    	
		String[] propertiesToExclude = { "GalleryControlSettingId", "ControlId" };
		Field[] fs = galleryControlSettings.getClass().getDeclaredFields();
		for (Field prop : fs){
			String propName = StringUtils.capitalize(prop.getName());
			if (ArrayUtils.indexOf(propertiesToExclude, propName) >= 0) {
	          continue; // Skip this one.
	        }

	        // Get a reference to the database record (won't exist for new items).
	        GalleryControlSetting gcsDto = gcsDtos.stream().filter(g->g.getSettingName() == propName).findFirst().orElse(null);
	        Object val = Reflections.getFieldValue(galleryControlSettings, prop.getName());
	        if (val != null)  {
	        	String propValue = null;
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
				} else if (val instanceof ViewMode) {
					//ViewMode = ViewMode.valueOf(arg0);
					if (val == ViewMode.NotSet){
						// Property not assigned. Delete the record.
						if (gcsDto != null) {
							galleryControlSettingDao.remove(gcsDto);
						}

						continue; // We're done with this property, so let's move on to the next one.
		            }
					propValue = val.toString();
				} else if (val instanceof SlideShowType) {
					//ViewMode = ViewMode.valueOf(arg0);
					if (val == SlideShowType.NotSet){
						// Property not assigned. Delete the record.
						if (gcsDto != null) {
							galleryControlSettingDao.remove(gcsDto);
						}

						continue; // We're done with this property, so let's move on to the next one.
		            }
					propValue = val.toString();
				} else if (val instanceof Date) {
				} else {
					propValue = Reflections.getFieldValue(galleryControlSettings, prop.getName()).toString();
				}
	        	if (gcsDto != null) {
	        		gcsDto.setSettingValue(propValue);
	        	}else {
	        		gcsDto = new GalleryControlSetting(galleryControlSettings.getControlId(), propName, propValue);
	        	}
	        	galleryControlSettingDao.save(gcsDto);
	        	
	        }else {
	        	if (gcsDto != null) {
	        		galleryControlSettingDao.remove(gcsDto);
	        	}
	        }
		}
    }

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Response removeGalleryControlSetting(final String galleryControlSettingIds) {
        log.debug("removing galleryControlSetting: " + galleryControlSettingIds);
        try {
	        galleryControlSettingDao.remove(ConvertUtil.StringtoLongArray(galleryControlSettingIds));
	      //CacheUtils.remove(CacheItem.ContentTemplates.toString());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Gallery control setting(id=" + galleryControlSettingIds + ") was successfully deleted.");
        return Response.ok().build();
    }
}