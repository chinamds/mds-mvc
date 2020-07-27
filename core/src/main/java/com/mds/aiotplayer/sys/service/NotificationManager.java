package com.mds.aiotplayer.sys.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.sys.model.Notification;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface NotificationManager extends GenericManager<Notification, Long> {
	void markReadAll(final Long userId) ;

    void markRead(final Long notificationId);
}