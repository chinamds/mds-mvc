package com.mds.aiotplayer.sys.dao.hibernate;

import com.mds.aiotplayer.sys.model.AppSetting;
import com.mds.aiotplayer.sys.dao.AppSettingDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
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
        var result = super.save(appSetting);
        // necessary to throw a DataIntegrityViolation and catch it in AppSettingManager
        getEntityManager().flush();
        return result;
    }
}
