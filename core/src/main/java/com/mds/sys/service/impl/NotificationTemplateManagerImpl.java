package com.mds.sys.service.impl;

import com.mds.sys.dao.NotificationTemplateDao;
import com.mds.sys.model.NotificationTemplate;
import com.mds.sys.service.NotificationTemplateManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("notificationTemplateManager")
@WebService(serviceName = "NotificationTemplateService", endpointInterface = "com.mds.sys.service.NotificationTemplateManager")
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