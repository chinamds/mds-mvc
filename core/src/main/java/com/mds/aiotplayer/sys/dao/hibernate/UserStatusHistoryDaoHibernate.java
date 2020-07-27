package com.mds.aiotplayer.sys.dao.hibernate;

import com.mds.aiotplayer.sys.model.UserStatusHistory;
import com.mds.aiotplayer.sys.dao.UserStatusHistoryDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
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
        var result = super.save(userStatusHistory);
        // necessary to throw a DataIntegrityViolation and catch it in UserStatusHistoryManager
        getEntityManager().flush();
        return result;
    }
}
