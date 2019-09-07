package com.mds.sys.service;

import com.mds.common.service.GenericManager;
import com.mds.sys.model.User;
import com.mds.sys.model.UserStatus;
import com.mds.sys.model.UserStatusHistory;

import javax.jws.WebService;


@WebService
public interface UserStatusHistoryManager extends GenericManager<UserStatusHistory, Long> {
	 void log(User opUser, User user, int newStatus, String reason);
	 
	 UserStatusHistory findLastHistory(final User user);
	 
	 String getLastReason(User user);
}