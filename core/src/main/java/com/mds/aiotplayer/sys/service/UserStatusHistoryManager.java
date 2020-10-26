/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.model.UserStatus;
import com.mds.aiotplayer.sys.model.UserStatusHistory;

import javax.jws.WebService;


@WebService
public interface UserStatusHistoryManager extends GenericManager<UserStatusHistory, Long> {
	 void log(User opUser, User user, int newStatus, String reason);
	 
	 UserStatusHistory findLastHistory(final User user);
	 
	 String getLastReason(User user);
}