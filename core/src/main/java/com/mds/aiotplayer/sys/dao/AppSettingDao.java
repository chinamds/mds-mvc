package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.sys.model.AppSetting;

/**
 * An interface that provides a data management interface to the AppSetting table.
 */
public interface AppSettingDao extends GenericDao<AppSetting, Long> {
	/**
     * Saves a appSetting's information.
     * @param appSetting the object to be saved
     * @return the persisted AppSetting object
     */
    AppSetting saveAppSetting(AppSetting appSetting);
}