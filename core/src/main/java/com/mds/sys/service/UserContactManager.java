package com.mds.sys.service;

import com.mds.common.service.GenericManager;
import com.mds.sys.model.UserContact;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface UserContactManager extends GenericManager<UserContact, Long> {
    
}