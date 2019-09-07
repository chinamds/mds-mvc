package com.mds.sys.dao.hibernate;

import com.mds.sys.model.UserStatusHistory;
import com.mds.sys.dao.UserStatusHistoryDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("userStatusHistoryDao")
public class UserStatusHistoryDaoHibernate extends GenericDaoHibernate<UserStatusHistory, Long> implements UserStatusHistoryDao {

    public UserStatusHistoryDaoHibernate() {
        super(UserStatusHistory.class);
    }

	/**
     * {@inheritDoc}
     */
    public UserStatusHistory saveUserStatusHistory(UserStatusHistory userStatusHistory) {
        if (log.isDebugEnabled()) {
            log.debug("userStatusHistory's id: " + userStatusHistory.getId());
        }
        getSession().saveOrUpdate(userStatusHistory);
        // necessary to throw a DataIntegrityViolation and catch it in UserStatusHistoryManager
        getSession().flush();
        return userStatusHistory;
    }
}
