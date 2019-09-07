package com.mds.sys.service.impl;

import com.mds.sys.dao.AuthDao;
import com.mds.sys.model.Auth;
import com.mds.sys.service.AuthManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("authManager")
@WebService(serviceName = "AuthService1", endpointInterface = "com.mds.sys.service.AuthManager")
public class AuthManagerImpl extends GenericManagerImpl<Auth, Long> implements AuthManager {
    AuthDao authDao;

    @Autowired
    public AuthManagerImpl(AuthDao authDao) {
        super(authDao);
        this.authDao = authDao;
    }
}