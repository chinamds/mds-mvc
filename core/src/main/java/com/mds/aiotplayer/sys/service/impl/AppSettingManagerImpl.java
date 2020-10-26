/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service.impl;

import com.mds.aiotplayer.sys.dao.AppSettingDao;
import com.mds.aiotplayer.sys.model.AppSetting;
import com.mds.aiotplayer.sys.service.AppSettingManager;
import com.mds.aiotplayer.sys.util.AppSettings;
import com.mds.aiotplayer.util.ConvertUtil;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.common.utils.Reflections;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.core.exception.DataException;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("appSettingManager")
@WebService(serviceName = "AppSettingService", endpointInterface = "com.mds.aiotplayer.service.AppSettingManager")
public class AppSettingManagerImpl extends GenericManagerImpl<AppSetting, Long> implements AppSettingManager {
    AppSettingDao appSettingDao;

    @Autowired
    public AppSettingManagerImpl(AppSettingDao appSettingDao) {
        super(appSettingDao);
        this.appSettingDao = appSettingDao;
    }
    
    public Map<String, Object> getGallerySettings(){
    	List<AppSetting> appSettings = appSettingDao.getAll();
    	
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	for(AppSetting appSetting : appSettings){
    		resultMap.put(appSetting.getSettingName(), appSetting.getSettingValue());
    	}
    	
    	return resultMap;
    }

		/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
	public void removeAppSetting(Long id) {
		appSettingDao.removeById(id, "%,"+id+",%");
		//UserUtils.removeCache(UserUtils.CACHE_ORGANIZATION_LIST);
	}

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public AppSetting saveAppSetting(final AppSetting appSetting) throws RecordExistsException {
    	
        try {
        	AppSetting result =  appSettingDao.save(appSetting);
            //UserUtils.removeCache(CacheItem.);
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("AppSetting '" + appSetting.getSettingName() + "' already exists!");
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void saveAppSettings(AppSettings appSettings) {
    	if (appSettings == null)
            throw new ArgumentNullException("appSettings");
    	
    	List<AppSetting> appSettingDtos = appSettingDao.getAll();
        // Specify the list of properties we want to save.
        String[] propertiesToSave = new String[] { "Skin", "ContentObjectDownloadBufferSize", "EncryptContentObjectUrlOnClient", "EncryptionKey", 
                                       "JQueryScriptPath", "JQueryMigrateScriptPath", "JQueryUiScriptPath", "MembershipProviderName", 
  																		 "RoleProviderName", "ProductKey", "EnableCache", "AllowGalleryAdminToManageUsersAndRoles", 
  																		 "AllowGalleryAdminToViewAllUsersAndRoles", "MaxNumberErrorItems", "EmailFromName",
  																		 "EmailFromAddress", "SmtpServer", "SmtpServerPort", "SendEmailUsingSsl"};
    	
    	Field[] fs = appSettings.getClass().getDeclaredFields();
		for (Field prop : fs){
			if (ArrayUtils.indexOf(propertiesToSave, prop.getName()) < 0) {
	          continue; // Skip this one.
	        }

	        // Get a reference to the database record (won't exist for new items).
	        String propName = prop.getName();
	        Object val = Reflections.getFieldValue(appSettings, prop.getName());
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
				} else {
					propValue = val.toString();
				}
            }
        	// Find the app setting in the DB and update it.
        	AppSetting appSettingDto = appSettingDtos.stream().filter(g->g.getSettingName() == propName).findFirst().orElse(null);
        	if (appSettingDto != null) {
        		appSettingDto.setSettingValue(propValue);
        	}else {
        		throw new DataException(MessageFormat.format("Cannot update application setting. No record was found in AppSetting with SettingName='{0}'.", propName));
        	}
        	appSettingDao.save(appSettingDto);
        }
    }

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Response removeAppSetting(final String appSettingIds) {
        log.debug("removing appSetting: " + appSettingIds);
        try {
	        appSettingDao.remove(ConvertUtil.StringtoLongArray(appSettingIds));
	        //UserUtils.removeCache(UserUtils.CACHE_ORGANIZATION_LIST);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Face Define(id=" + appSettingIds + ") was successfully deleted.");
        return Response.ok().build();
    }
}