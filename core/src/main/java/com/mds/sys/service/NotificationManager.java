package com.mds.sys.service;

import com.mds.common.service.GenericManager;
import com.mds.sys.model.Notification;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface NotificationManager extends GenericManager<Notification, Long> {
	void markReadAll(final Long userId) ;

    void markRead(final Long notificationId);
}