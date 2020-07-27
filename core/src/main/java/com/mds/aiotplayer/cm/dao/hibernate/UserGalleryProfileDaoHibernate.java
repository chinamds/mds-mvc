package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.UserGalleryProfile;
import com.mds.aiotplayer.cm.dao.UserGalleryProfileDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
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
        var result = super.save(userGalleryProfile);
        // necessary to throw a DataIntegrityViolation and catch it in UserGalleryProfileManager
        getEntityManager().flush();
        
        return result;
    }
}
