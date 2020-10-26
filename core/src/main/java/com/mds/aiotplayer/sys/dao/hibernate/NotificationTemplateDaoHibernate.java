/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao.hibernate;

import com.mds.aiotplayer.sys.model.NotificationTemplate;
import com.mds.aiotplayer.sys.dao.NotificationTemplateDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import com.mds.aiotplayer.common.model.Parameter;

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
        var result = super.save(notificationTemplate);
        // necessary to throw a DataIntegrityViolation and catch it in NotificationTemplateManager
        getEntityManager().flush();
        return result;
    }
}
