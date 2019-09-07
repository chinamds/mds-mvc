package com.mds.sys.service;

import com.mds.common.exception.RecordExistsException;
import com.mds.common.service.GenericManager;
import com.mds.sys.model.AppSetting;
import com.mds.sys.util.AppSettings;

import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

@WebService
public interface AppSettingManager extends GenericManager<AppSetting, Long> {
    /**
     * Saves a appSetting's information
     *
     * @param appSetting the appSetting's information
     * @return updated appSetting
     * @throws RecordExistsException thrown when appSetting already exists
     */
    AppSetting saveAppSetting(AppSetting appSetting) throws RecordExistsException;
    void saveAppSettings(AppSettings appSettings);

	void removeAppSetting(Long id) ;

	Response removeAppSetting(final String appSettingIds);
}