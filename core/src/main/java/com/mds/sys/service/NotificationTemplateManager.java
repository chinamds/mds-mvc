package com.mds.sys.service;

import com.mds.common.service.GenericManager;
import com.mds.sys.model.NotificationTemplate;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface NotificationTemplateManager extends GenericManager<NotificationTemplate, Long> {
	NotificationTemplate findByName(final String name);
}