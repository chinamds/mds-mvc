package com.mds.sys.service.impl;

import com.mds.sys.dao.UserStatusHistoryDao;
import com.mds.sys.model.User;
import com.mds.sys.model.UserStatus;
import com.mds.sys.model.UserStatusHistory;
import com.mds.sys.service.UserStatusHistoryManager;
import com.mds.common.model.search.Searchable;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import javax.jws.WebService;

@Service("userStatusHistoryManager")
@WebService(serviceName = "UserStatusHistoryService", endpointInterface = "com.mds.sys.service.UserStatusHistoryManager")
public class UserStatusHistoryManagerImpl extends GenericManagerImpl<UserStatusHistory, Long> implements UserStatusHistoryManager {
    UserStatusHistoryDao userStatusHistoryDao;

    @Autowired
    public UserStatusHistoryManagerImpl(UserStatusHistoryDao userStatusHistoryDao) {
        super(userStatusHistoryDao);
        this.userStatusHistoryDao = userStatusHistoryDao;
    }
    
    public void log(User opUser, User user, int newStatus, String reason) {
        UserStatusHistory history = new UserStatusHistory();
        history.setUser(user);
        history.setOpUser(opUser);
        history.setOpDate(new Date());
        history.setOriginalStatus(user.getStatus());
        history.setPresentStatus(newStatus);
        history.setReason(reason);
        save(history);
    }

    public UserStatusHistory findLastHistory(final User user) {
        Searchable searchable = Searchable.newSearchable()
                .addSearchParam("user_eq", user)
                .addSort(Sort.Direction.DESC, "opDate")
                .setPage(0, 1);

        Page<UserStatusHistory> page = userStatusHistoryDao.find(searchable);

        if (page.hasContent()) {
            return page.getContent().get(0);
        }
        return null;
    }

    public String getLastReason(User user) {
        UserStatusHistory history = findLastHistory(user);
        if (history == null) {
            return "";
        }
        return history.getReason();
    }
}