/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service.impl;

import com.mds.aiotplayer.sys.dao.NotificationTemplateDao;
import com.mds.aiotplayer.sys.model.NotificationTemplate;
import com.mds.aiotplayer.sys.service.NotificationTemplateManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("notificationTemplateManager")
@WebService(serviceName = "NotificationTemplateService", endpointInterface = "com.mds.aiotplayer.sys.service.NotificationTemplateManager")
public class NotificationTemplateManagerImpl extends GenericManagerImpl<NotificationTemplate, Long> implements NotificationTemplateManager {
    NotificationTemplateDao notificationTemplateDao;

    @Autowired
    public NotificationTemplateManagerImpl(NotificationTemplateDao notificationTemplateDao) {
        super(notificationTemplateDao);
        this.notificationTemplateDao = notificationTemplateDao;
    }
    
    public NotificationTemplate findByName(final String name) {
        return notificationTemplateDao.findByName(name);
    }
}