package com.mds.aiotplayer.sys.dao.hibernate;

import com.mds.aiotplayer.sys.model.Auth;
import com.mds.aiotplayer.sys.dao.AuthDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("authDao")
public class AuthDaoHibernate extends GenericDaoHibernate<Auth, Long> implements AuthDao {

    public AuthDaoHibernate() {
        super(Auth.class);
    }

	/**
     * {@inheritDoc}
     */
    public Auth saveAuth(Auth auth) {
        if (log.isDebugEnabled()) {
            log.debug("auth's id: " + auth.getId());
        }
        var result = super.save(auth);
        // necessary to throw a DataIntegrityViolation and catch it in AuthManager
        getEntityManager().flush();
        return result;
    }
}
