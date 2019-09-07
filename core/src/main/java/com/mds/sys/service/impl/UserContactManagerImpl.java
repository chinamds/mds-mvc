package com.mds.sys.service.impl;

import com.mds.sys.dao.UserContactDao;
import com.mds.sys.model.UserContact;
import com.mds.sys.service.UserContactManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("userContactManager")
@WebService(serviceName = "UserContactService1", endpointInterface = "com.mds.sys.service.UserContactManager")
public class UserContactManagerImpl extends GenericManagerImpl<UserContact, Long> implements UserContactManager {
    UserContactDao userContactDao;

    @Autowired
    public UserContactManagerImpl(UserContactDao userContactDao) {
        super(userContactDao);
        this.userContactDao = userContactDao;
    }
}