/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service.impl;

import com.mds.aiotplayer.sys.dao.UserContactDao;
import com.mds.aiotplayer.sys.model.UserContact;
import com.mds.aiotplayer.sys.service.UserContactManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("userContactManager")
@WebService(serviceName = "UserContactService1", endpointInterface = "com.mds.aiotplayer.sys.service.UserContactManager")
public class UserContactManagerImpl extends GenericManagerImpl<UserContact, Long> implements UserContactManager {
    UserContactDao userContactDao;

    @Autowired
    public UserContactManagerImpl(UserContactDao userContactDao) {
        super(userContactDao);
        this.userContactDao = userContactDao;
    }
}