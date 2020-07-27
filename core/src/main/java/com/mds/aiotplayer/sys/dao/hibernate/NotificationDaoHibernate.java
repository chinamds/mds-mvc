package com.mds.aiotplayer.sys.dao.hibernate;

import com.mds.aiotplayer.sys.model.Notification;
import com.mds.aiotplayer.sys.dao.NotificationDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import com.mds.aiotplayer.common.model.Parameter;

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
        var result = super.save(notification);
        // necessary to throw a DataIntegrityViolation and catch it in NotificationManager
        getEntityManager().flush();
        return result;
    }
}
