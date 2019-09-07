package com.mds.sys.dao.hibernate;

import com.mds.sys.model.UserContact;
import com.mds.sys.dao.UserContactDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("userContactDao")
public class UserContactDaoHibernate extends GenericDaoHibernate<UserContact, Long> implements UserContactDao {

    public UserContactDaoHibernate() {
        super(UserContact.class);
    }

	/**
     * {@inheritDoc}
     */
    public UserContact saveUserContact(UserContact userContact) {
        if (log.isDebugEnabled()) {
            log.debug("userContact's id: " + userContact.getId());
        }
        getSession().saveOrUpdate(userContact);
        // necessary to throw a DataIntegrityViolation and catch it in UserContactManager
        getSession().flush();
        return userContact;
    }
}
