package com.mds.sys.service;

import com.mds.common.service.GenericManager;
import com.mds.sys.model.Auth;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface AuthManager extends GenericManager<Auth, Long> {
    
}