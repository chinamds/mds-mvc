package com.mds.sys.dao.hibernate;

import com.mds.sys.model.Notification;
import com.mds.sys.dao.NotificationDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import com.mds.common.model.Parameter;

import org.springframework.stereotype.Repository;

@Repository("notificationDao")
public class NotificationDaoHibernate extends GenericDaoHibernate<Notification, Long> implements NotificationDao {

    public NotificationDaoHibernate() {
        super(Notification.class);
    }
    
    public void markReadAll(Long userId){
    	update("update Notification o set o.read=true where user.id=:p1", new Parameter(userId));
    }

	/**
     * {@inheritDoc}
     */
    public Notification saveNotification(Notification notification) {
        if (log.isDebugEnabled()) {
            log.debug("notification's id: " + notification.getId());
        }
        getSession().saveOrUpdate(notification);
        // necessary to throw a DataIntegrityViolation and catch it in NotificationManager
        getSession().flush();
        return notification;
    }
}
