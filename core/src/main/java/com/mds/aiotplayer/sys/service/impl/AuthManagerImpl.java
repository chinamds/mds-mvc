package com.mds.aiotplayer.sys.service.impl;

import com.mds.aiotplayer.sys.dao.AuthDao;
import com.mds.aiotplayer.sys.model.Auth;
import com.mds.aiotplayer.sys.service.AuthManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("authManager")
@WebService(serviceName = "AuthService1", endpointInterface = "com.mds.aiotplayer.sys.service.AuthManager")
public class AuthManagerImpl extends GenericManagerImpl<Auth, Long> implements AuthManager {
    AuthDao authDao;

    @Autowired
    public AuthManagerImpl(AuthDao authDao) {
        super(authDao);
        this.authDao = authDao;
    }
}