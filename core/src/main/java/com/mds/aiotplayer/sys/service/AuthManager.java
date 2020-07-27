package com.mds.aiotplayer.sys.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.sys.model.Auth;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface AuthManager extends GenericManager<Auth, Long> {
    
}