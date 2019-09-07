package com.mds.sys.dao.hibernate;

import com.mds.sys.model.NotificationTemplate;
import com.mds.sys.dao.NotificationTemplateDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import com.mds.common.model.Parameter;

import org.springframework.stereotype.Repository;

@Repository("notificationTemplateDao")
public class NotificationTemplateDaoHibernate extends GenericDaoHibernate<NotificationTemplate, Long> implements NotificationTemplateDao {

    public NotificationTemplateDaoHibernate() {
        super(NotificationTemplate.class);
    }
    
    //@Query("from NotificationTemplate o where name=?1")
    public NotificationTemplate findByName(String name) {
    	return getByHql("from NotificationTemplate o where name=:p1", new Parameter(name));
    }

	/**
     * {@inheritDoc}
     */
    public NotificationTemplate saveNotificationTemplate(NotificationTemplate notificationTemplate) {
        if (log.isDebugEnabled()) {
            log.debug("notificationTemplate's id: " + notificationTemplate.getId());
        }
        getSession().saveOrUpdate(notificationTemplate);
        // necessary to throw a DataIntegrityViolation and catch it in NotificationTemplateManager
        getSession().flush();
        return notificationTemplate;
    }
}
