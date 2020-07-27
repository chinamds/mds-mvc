package com.mds.aiotplayer.sys.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.sys.model.NotificationTemplate;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface NotificationTemplateManager extends GenericManager<NotificationTemplate, Long> {
	NotificationTemplate findByName(final String name);
}