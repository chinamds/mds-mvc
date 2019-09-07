package com.mds.sys.dao;

import com.mds.common.dao.GenericDao;

import com.mds.sys.model.AppSetting;

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