package com.mds.sys.dao.hibernate;

import com.mds.sys.model.Auth;
import com.mds.sys.dao.AuthDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
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
        getSession().saveOrUpdate(auth);
        // necessary to throw a DataIntegrityViolation and catch it in AuthManager
        getSession().flush();
        return auth;
    }
}
