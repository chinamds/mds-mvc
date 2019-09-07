package com.mds.cm.dao.hibernate;

import com.mds.cm.model.UserGalleryProfile;
import com.mds.cm.dao.UserGalleryProfileDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("userGalleryProfileDao")
public class UserGalleryProfileDaoHibernate extends GenericDaoHibernate<UserGalleryProfile, Long> implements UserGalleryProfileDao {

    public UserGalleryProfileDaoHibernate() {
        super(UserGalleryProfile.class);
    }

	/**
     * {@inheritDoc}
     */
    public UserGalleryProfile saveUserGalleryProfile(UserGalleryProfile userGalleryProfile) {
        if (log.isDebugEnabled()) {
            log.debug("userGalleryProfile's id: " + userGalleryProfile.getId());
        }
        getSession().saveOrUpdate(userGalleryProfile);
        // necessary to throw a DataIntegrityViolation and catch it in UserGalleryProfileManager
        getSession().flush();
        return userGalleryProfile;
    }
}
