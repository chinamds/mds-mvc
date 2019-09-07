package com.mds.sys.dao.hibernate;

import com.mds.sys.model.AppSetting;
import com.mds.sys.dao.AppSettingDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("appSettingDao")
public class AppSettingDaoHibernate extends GenericDaoHibernate<AppSetting, Long> implements AppSettingDao {

    public AppSettingDaoHibernate() {
        super(AppSetting.class);
    }

	/**
     * {@inheritDoc}
     */
    public AppSetting saveAppSetting(AppSetting appSetting) {
        if (log.isDebugEnabled()) {
            log.debug("appSetting's id: " + appSetting.getId());
        }
        getSession().saveOrUpdate(appSetting);
        // necessary to throw a DataIntegrityViolation and catch it in AppSettingManager
        getSession().flush();
        return appSetting;
    }
}
