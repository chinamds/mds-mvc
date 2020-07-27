package com.mds.aiotplayer.sys.dao.hibernate;

import com.mds.aiotplayer.sys.model.UserContact;
import com.mds.aiotplayer.sys.dao.UserContactDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
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
        var result = super.save(userContact);
        // necessary to throw a DataIntegrityViolation and catch it in UserContactManager
        getEntityManager().flush();
        return result;
    }
}
